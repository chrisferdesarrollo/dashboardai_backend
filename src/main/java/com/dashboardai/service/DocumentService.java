package com.dashboardai.service;

import com.dashboardai.dto.request.CreateDocumentRequest;
import com.dashboardai.dto.response.DocumentResponse;
import com.dashboardai.entity.Document;
import com.dashboardai.repository.DocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${app.n8n.webhook.documents:https://n8n.topias.app/webhook/documents-api}")
    private String n8nWebhookUrl;
    
    // Tipos de archivo permitidos
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain",
        "text/markdown",
        "text/csv",
        "application/csv"
    );
    
    // Tamaño máximo: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * Procesar documento enviándolo directamente a N8N para vectorización
     */
    public DocumentResponse processDocumentForVectorization(MultipartFile file, CreateDocumentRequest request, String authToken) {
        
        // Validar archivo
        validateFile(file);
        
        // Crear registro en BD con metadatos
        Document document = new Document();
        document.setName(request.getName());
        document.setDescription(request.getDescription());
        document.setFileType(file.getContentType());
        document.setTags(request.getTags() != null ? request.getTags().toArray(new String[0]) : null);
        document.setAgentId(request.getAgentId());
        document.setProcessed(false);
        document.setProcessingStatus(Document.ProcessingStatus.PENDING);
        
        // Guardar en BD
        document = documentRepository.save(document);
        logger.info("Document metadata saved with ID: {}", document.getId());
        
        // Enviar a N8N de forma asíncrona
        try {
            sendToN8nWebhook(file, document, authToken);
            
            // Actualizar estado a "procesando"
            document.setProcessingStatus(Document.ProcessingStatus.PROCESSING);
            documentRepository.save(document);
            
        } catch (Exception e) {
            logger.error("Error sending document to N8N: {}", e.getMessage(), e);
            
            // Marcar como fallido
            document.setProcessingStatus(Document.ProcessingStatus.FAILED);
            documentRepository.save(document);
            
            throw new RuntimeException("Error enviando documento a N8N: " + e.getMessage());
        }
        
        return DocumentResponse.fromEntity(document);
    }
    
    /**
     * Enviar documento al webhook de N8N
     */
    private void sendToN8nWebhook(MultipartFile file, Document document, String authToken) throws Exception {
        
        // Crear objeto con metadatos para el body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("documentId", document.getId().toString());
        requestBody.put("name", document.getName());
        requestBody.put("description", document.getDescription() != null ? document.getDescription() : "");
        requestBody.put("fileType", document.getFileType());
        requestBody.put("agentId", document.getAgentId() != null ? document.getAgentId().toString() : "");
        if (document.getTags() != null) {
            requestBody.put("tags", String.join(",", document.getTags()));
        }
        
        // Agregar archivo como base64 al body
        requestBody.put("fileContent", Base64.getEncoder().encodeToString(file.getBytes()));
        requestBody.put("fileName", file.getOriginalFilename());
        
        // Limpiar token
        String cleanToken = "";
        if (authToken != null && !authToken.isEmpty()) {
            if (authToken.startsWith("Bearer ")) {
                cleanToken = authToken.substring(7);
            } else {
                cleanToken = authToken;
            }
        }
        
        // Configurar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (!cleanToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + cleanToken);
        }
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        // Enviar request
        logger.info("Sending document with metadata in body to N8N webhook: {}", n8nWebhookUrl);
        logger.info("Metadata in body: documentId={}, name={}", document.getId(), document.getName());
        ResponseEntity<String> response = restTemplate.postForEntity(n8nWebhookUrl, requestEntity, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Document sent successfully to N8N for document ID: {}", document.getId());
        } else {
            throw new RuntimeException("N8N webhook returned status: " + response.getStatusCode());
        }
    }
    
    /**
     * Validar archivo
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo es requerido");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo de 10MB");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Formatos soportados: PDF, Word, TXT, MD, CSV");
        }
    }
    
    
    /**
     * Actualizar estado de procesamiento desde N8N
     */
    public void updateProcessingStatus(UUID documentId, String status, String error) {
        Optional<Document> optDocument = documentRepository.findById(documentId);
        if (optDocument.isPresent()) {
            Document document = optDocument.get();
            
            switch (status.toLowerCase()) {
                case "success":
                case "completed":
                    document.setProcessingStatus(Document.ProcessingStatus.COMPLETED);
                    document.setProcessed(true);
                    break;
                case "failed":
                case "error":
                    document.setProcessingStatus(Document.ProcessingStatus.FAILED);
                    document.setProcessed(false);
                    if (error != null) {
                        logger.error("Document processing failed for ID {}: {}", documentId, error);
                    }
                    break;
                default:
                    logger.warn("Unknown processing status received: {}", status);
                    return;
            }
            
            documentRepository.save(document);
            logger.info("Updated document {} processing status to: {}", documentId, status);
        } else {
            logger.error("Document not found for ID: {}", documentId);
            throw new RuntimeException("Document not found: " + documentId);
        }
    }
    
    /**
     * Obtener todos los documentos
     */
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAllByOrderByUploadDateDesc()
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener documentos por agente
     */
    public List<DocumentResponse> getDocumentsByAgent(UUID agentId) {
        return documentRepository.findByAgentId(agentId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener documento por ID
     */
    public DocumentResponse getDocumentById(UUID id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isEmpty()) {
            throw new RuntimeException("Documento no encontrado");
        }
        return DocumentResponse.fromEntity(document.get());
    }
    
    /**
     * Eliminar documento
     */
    public void deleteDocument(UUID id) {
        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Documento no encontrado");
        }
        documentRepository.deleteById(id);
        logger.info("Document deleted: {}", id);
    }
    
    /**
     * Buscar documentos
     */
    public List<DocumentResponse> searchDocuments(String query) {
        return documentRepository.findByNameOrDescriptionContainingIgnoreCase(query)
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener estadísticas de documentos
     */
    public com.dashboardai.dto.response.DocumentStats getDocumentStats() {
        List<Document> documents = documentRepository.findAll();
        
        long total = documents.size();
        long processed = documents.stream()
                .mapToLong(doc -> doc.getProcessed() ? 1 : 0)
                .sum();
        long pending = documents.stream()
                .mapToLong(doc -> (doc.getProcessingStatus() == Document.ProcessingStatus.PENDING || 
                                 doc.getProcessingStatus() == Document.ProcessingStatus.PROCESSING) ? 1 : 0)
                .sum();
        long failed = documents.stream()
                .mapToLong(doc -> doc.getProcessingStatus() == Document.ProcessingStatus.FAILED ? 1 : 0)
                .sum();
        
        double successRate = total > 0 ? (double) processed / total * 100 : 0;
        successRate = Math.round(successRate * 100.0) / 100.0; // Redondear a 2 decimales
        
        return new com.dashboardai.dto.response.DocumentStats(total, processed, pending, failed, successRate);
    }
    
    /**
     * Marcar documento como procesado (llamado por N8N)
     */
    public void markAsProcessed(UUID id) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            document.setProcessed(true);
            document.setProcessingStatus(Document.ProcessingStatus.COMPLETED);
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);
            logger.info("Document marked as processed: {}", id);
        } else {
            throw new RuntimeException("Documento no encontrado: " + id);
        }
    }
}