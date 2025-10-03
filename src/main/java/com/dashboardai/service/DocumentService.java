package com.dashboardai.service;

import com.dashboardai.entity.Document;
import com.dashboardai.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.n8n.webhook.documents:https://n8n.topias.app/webhook/documents-api}")
    private String n8nWebhookUrl;
    
    // Tipos de archivo permitidos
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain",
        "text/markdown",
        "text/csv",
        "application/csv"
    );
    
    // Extensiones permitidas
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "txt", "md", "csv"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * Subir un nuevo documento
     */
    public Document uploadDocument(MultipartFile file, String name, String description, 
                                 List<String> tags, UUID agentId) throws IOException {
        
        logger.info("Iniciando subida de documento: {}", name);
        
        // Validaciones
        validateFile(file);
        
        // Crear directorio de uploads si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generar nombre único para el archivo
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        // Guardar archivo
        Files.copy(file.getInputStream(), filePath);
        logger.info("Archivo guardado en: {}", filePath.toString());
        
        // Crear entidad Document
        Document document = new Document();
        document.setName(name);
        document.setDescription(description);
        document.setOriginalFilename(originalFilename);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setTags(tags != null ? tags.toArray(new String[0]) : new String[0]);
        document.setAgentId(agentId);
        document.setFilePath(filePath.toString());
        document.setProcessingStatus(Document.ProcessingStatus.PENDING);
        
        // Guardar en base de datos
        Document savedDocument = documentRepository.save(document);
        logger.info("Documento guardado en BD con ID: {}", savedDocument.getId());
        
        // Enviar a N8N para procesamiento asíncrono
        try {
            sendToN8nWebhook(savedDocument);
        } catch (Exception e) {
            logger.error("Error enviando documento a N8N: {}", e.getMessage());
            // No fallar la subida si hay error en N8N, solo marcar como fallido
            savedDocument.setProcessingStatus(Document.ProcessingStatus.FAILED);
            documentRepository.save(savedDocument);
        }
        
        return savedDocument;
    }
    
    /**
     * Validar archivo subido
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Tipo de archivo no permitido. Formatos soportados: PDF, Word, TXT, MD, CSV");
            }
        }
    }
    
    /**
     * Obtener extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
    
    /**
     * Enviar documento a webhook de N8N para procesamiento
     */
    private void sendToN8nWebhook(Document document) {
        try {
            logger.info("Enviando documento {} a N8N webhook", document.getId());
            
            // Actualizar estado a processing
            document.setProcessingStatus(Document.ProcessingStatus.PROCESSING);
            documentRepository.save(document);
            
            // Preparar payload para N8N
            Map<String, Object> payload = new HashMap<>();
            payload.put("documentId", document.getId().toString());
            payload.put("name", document.getName());
            payload.put("description", document.getDescription());
            payload.put("originalFilename", document.getOriginalFilename());
            payload.put("fileType", document.getFileType());
            payload.put("fileSize", document.getFileSize());
            payload.put("tags", document.getTags());
            payload.put("agentId", document.getAgentId() != null ? document.getAgentId().toString() : null);
            payload.put("filePath", document.getFilePath());
            payload.put("uploadDate", document.getUploadDate().toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(n8nWebhookUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Documento {} enviado exitosamente a N8N", document.getId());
            } else {
                logger.error("Error enviando documento a N8N. Status: {}", response.getStatusCode());
                throw new RestClientException("N8N webhook respondió con status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("Error enviando documento a N8N webhook: {}", e.getMessage());
            document.setProcessingStatus(Document.ProcessingStatus.FAILED);
            documentRepository.save(document);
            throw e;
        }
    }
    
    /**
     * Obtener todos los documentos con paginación
     */
    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAllByOrderByUploadDateDesc(pageable);
    }
    
    /**
     * Obtener documento por ID
     */
    public Optional<Document> getDocumentById(UUID id) {
        return documentRepository.findById(id);
    }
    
    /**
     * Obtener documentos por agente
     */
    public List<Document> getDocumentsByAgent(UUID agentId) {
        return documentRepository.findByAgentId(agentId);
    }
    
    /**
     * Obtener documentos por agente con paginación
     */
    public Page<Document> getDocumentsByAgent(UUID agentId, Pageable pageable) {
        return documentRepository.findByAgentId(agentId, pageable);
    }
    
    /**
     * Buscar documentos por término
     */
    public List<Document> searchDocuments(String query) {
        return documentRepository.findByNameOrDescriptionContainingIgnoreCase(query);
    }
    
    /**
     * Obtener documentos por tag
     */
    public List<Document> getDocumentsByTag(String tag) {
        return documentRepository.findByTagsContaining(tag);
    }
    
    /**
     * Actualizar metadatos del documento
     */
    public Document updateDocument(UUID id, String name, String description, 
                                 List<String> tags, UUID agentId) {
        Optional<Document> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isEmpty()) {
            throw new IllegalArgumentException("Documento no encontrado");
        }
        
        Document document = optionalDocument.get();
        if (name != null) document.setName(name);
        if (description != null) document.setDescription(description);
        if (tags != null) document.setTags(tags.toArray(new String[0]));
        if (agentId != null) document.setAgentId(agentId);
        
        return documentRepository.save(document);
    }
    
    /**
     * Eliminar documento
     */
    public void deleteDocument(UUID id) {
        Optional<Document> optionalDocument = documentRepository.findById(id);
        if (optionalDocument.isEmpty()) {
            throw new IllegalArgumentException("Documento no encontrado");
        }
        
        Document document = optionalDocument.get();
        
        // Eliminar archivo físico
        try {
            Path filePath = Paths.get(document.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Archivo físico eliminado: {}", document.getFilePath());
            }
        } catch (IOException e) {
            logger.error("Error eliminando archivo físico: {}", e.getMessage());
        }
        
        // Eliminar de base de datos
        documentRepository.delete(document);
        logger.info("Documento {} eliminado de BD", id);
    }
    
    /**
     * Actualizar estado de procesamiento del documento (usado por N8N)
     */
    public void updateProcessingStatus(UUID documentId, Document.ProcessingStatus status) {
        Optional<Document> optionalDocument = documentRepository.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            document.setProcessingStatus(status);
            if (status == Document.ProcessingStatus.COMPLETED) {
                document.setProcessed(true);
            }
            documentRepository.save(document);
            logger.info("Estado de procesamiento actualizado para documento {}: {}", documentId, status);
        }
    }
    
    /**
     * Obtener estadísticas de documentos
     */
    public Map<String, Object> getDocumentStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalDocuments = documentRepository.count();
        long processedDocuments = documentRepository.findByProcessed(true).size();
        long pendingDocuments = documentRepository.findByProcessingStatus(Document.ProcessingStatus.PENDING).size();
        long failedDocuments = documentRepository.findByProcessingStatus(Document.ProcessingStatus.FAILED).size();
        
        stats.put("total", totalDocuments);
        stats.put("processed", processedDocuments);
        stats.put("pending", pendingDocuments);
        stats.put("failed", failedDocuments);
        stats.put("successRate", totalDocuments > 0 ? (double) processedDocuments / totalDocuments * 100 : 0);
        
        return stats;
    }
}