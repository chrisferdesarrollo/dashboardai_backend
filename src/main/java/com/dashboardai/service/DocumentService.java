package com.dashboardai.service;

import com.dashboardai.dto.request.CreateDocumentRequest;
import com.dashboardai.dto.response.DocumentResponse;
import com.dashboardai.entity.Document;
import com.dashboardai.entity.AgentTelegram;
import com.dashboardai.entity.AgentWhatsApp;
import com.dashboardai.repository.DocumentRepository;
import com.dashboardai.repository.AgentTelegramRepository;
import com.dashboardai.repository.AgentWhatsAppRepository;
import com.dashboardai.repository.VectorEmbeddingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private AgentTelegramRepository agentTelegramRepository;
    
    @Autowired
    private AgentWhatsAppRepository agentWhatsAppRepository;
    
    @Autowired
    private VectorEmbeddingRepository vectorEmbeddingRepository;
    
    @Autowired
    private ExcelProcessingService excelProcessingService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${app.n8n.webhook.documents:https://n8n.topias.app/webhook/documents-api}")
    private String n8nWebhookUrl;
    
    // Tipos de archivo permitidos
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/msword-docx", // Versión corta para DOCX
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain",
        "text/markdown",
        "text/csv",
        "application/csv",
        // Agregar soporte para Excel
        "application/vnd.ms-excel", // .xls
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
        "application/excel",
        "application/x-excel",
        "application/x-msexcel"
    );
    
    // Tamaño máximo: 30MB
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;
    
    /**
     * Procesar documento enviándolo directamente a N8N para vectorización
     */
    public DocumentResponse processDocumentForVectorization(MultipartFile file, CreateDocumentRequest request, String authToken, Long userId) {
        
        // Validar archivo
        validateFile(file);
        
        // Validar datos requeridos
        validateRequestData(request, userId);
        
        // Crear registro en BD con metadatos
        Document document = new Document();
        document.setName(request.getName().trim());
        document.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        
        // Normalizar tipo de archivo para Word
        String fileType = normalizeFileType(file.getContentType(), file.getOriginalFilename());
        document.setFileType(fileType);
        
        document.setTags(request.getTags() != null ? request.getTags().toArray(new String[0]) : new String[0]);
        document.setAgentId(request.getAgentId());
        document.setUserId(userId);
        document.setProcessed(false);
        document.setProcessingStatus(Document.ProcessingStatus.PENDING);
        
        // Guardar en BD
        try {
            logger.info("Attempting to save document: name={}, fileType={}, userId={}, agentId={}", 
                       document.getName(), document.getFileType(), userId, document.getAgentId());
            
            document = documentRepository.save(document);
            logger.info("Document metadata saved successfully with ID: {}", document.getId());
            
        } catch (Exception e) {
            logger.error("Error saving document to database: name={}, fileType={}, userId={}, error={}", 
                        document.getName(), document.getFileType(), userId, e.getMessage(), e);
            throw new RuntimeException("Error guardando el documento en la base de datos: " + e.getMessage(), e);
        }
        
        // Enviar a N8N de forma asíncrona
        try {
            sendToN8nWebhook(file, document, authToken);
            
            // Actualizar estado a "completado" ya que el envío fue exitoso
            document.setProcessingStatus(Document.ProcessingStatus.COMPLETED);
            documentRepository.save(document);
            logger.info("Document processing completed for ID: {}", document.getId());
            
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
        
        // Crear el body con metadatos (sin fileContent) y archivo binario separado
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Crear objeto de metadatos sin el contenido del archivo
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("documentId", document.getId().toString());
        metadata.put("name", document.getName());
        metadata.put("description", document.getDescription() != null ? document.getDescription() : "");
        metadata.put("fileType", document.getFileType());
        metadata.put("agentId", document.getAgentId() != null ? document.getAgentId().toString() : "");
        metadata.put("userId", document.getUserId().toString());
        if (document.getTags() != null) {
            metadata.put("tags", String.join(",", document.getTags()));
        }
        metadata.put("fileName", file.getOriginalFilename());
        
        // Procesar archivos Excel para extraer contenido de texto
        if (isExcelFile(file)) {
            try {
                logger.info("Processing Excel file for text extraction: {}", file.getOriginalFilename());
                String extractedText = excelProcessingService.processExcelFile(file);
                metadata.put("extractedText", extractedText);
                metadata.put("isExcelProcessed", true);
                
                // Obtener estadísticas del Excel
                Map<String, Object> excelStats = excelProcessingService.getExcelStats(file);
                metadata.put("excelStats", excelStats);
                
                logger.info("Excel text extraction completed. Text length: {} characters", extractedText.length());
            } catch (Exception e) {
                logger.error("Error processing Excel file: {}", e.getMessage(), e);
                metadata.put("excelProcessingError", e.getMessage());
                metadata.put("isExcelProcessed", false);
            }
        }
        
        // Obtener session_name del agente si está disponible
        if (document.getAgentId() != null) {
            try {
                // Aquí consultaremos el session_name del agente
                String agentSessionName = getAgentSessionName(document.getAgentId());
                if (agentSessionName != null && !agentSessionName.isEmpty()) {
                    metadata.put("sessionName", agentSessionName);
                    metadata.put("platform", extractPlatformFromSession(agentSessionName)); // telegram, whatsapp, etc.
                }
            } catch (Exception e) {
                logger.warn("Could not retrieve agent session name for agentId: {}", document.getAgentId());
            }
        }
        
        // Agregar metadatos como JSON string
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String metadataJson = objectMapper.writeValueAsString(metadata);
            body.add("metadata", metadataJson);
        } catch (Exception e) {
            logger.error("Error serializing metadata: {}", e.getMessage());
            throw e;
        }
        
        // Agregar archivo como binary
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", fileResource);
        
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
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        if (!cleanToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + cleanToken);
        }
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        // Enviar request
        logger.info("Sending document with binary file to N8N webhook: {}", n8nWebhookUrl);
        logger.info("Metadata includes: documentId={}, name={}, file as binary", 
                   document.getId(), document.getName());
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
            throw new IllegalArgumentException("El archivo excede el tamaño máximo de 30MB");
        }
        
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        logger.info("Validating file: name={}, contentType={}, size={}", filename, contentType, file.getSize());
        
        // Verificar por tipo MIME o extensión
        boolean isValidType = false;
        
        if (contentType != null) {
            String normalizedType = contentType.toLowerCase().trim();
            isValidType = ALLOWED_TYPES.contains(normalizedType) ||
                         normalizedType.contains("wordprocessingml") ||
                         normalizedType.contains("openxmlformats") ||
                         normalizedType.contains("msword");
        }
        
        // Si no es válido por tipo MIME, verificar por extensión
        if (!isValidType && filename != null) {
            String lowerFilename = filename.toLowerCase();
            isValidType = lowerFilename.endsWith(".pdf") ||
                         lowerFilename.endsWith(".docx") ||
                         lowerFilename.endsWith(".doc") ||
                         lowerFilename.endsWith(".txt") ||
                         lowerFilename.endsWith(".md") ||
                         lowerFilename.endsWith(".csv");
        }
        
        if (!isValidType) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Formatos soportados: PDF, Word (.doc, .docx), TXT, MD, CSV");
        }
    }
    
    /**
     * Validar datos de la request
     */
    private void validateRequestData(CreateDocumentRequest request, Long userId) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del documento son requeridos");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del documento es requerido");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("El ID de usuario es requerido");
        }
    }
    
    /**
     * Normalizar tipo de archivo, especialmente para documentos Word
     */
    private String normalizeFileType(String contentType, String filename) {
        if (contentType == null || contentType.isEmpty()) {
            // Intentar determinar por extensión
            if (filename != null) {
                String lowerFilename = filename.toLowerCase();
                if (lowerFilename.endsWith(".docx")) {
                    return "application/msword-docx"; // Versión corta para DOCX
                } else if (lowerFilename.endsWith(".doc")) {
                    return "application/msword"; // 19 caracteres
                } else if (lowerFilename.endsWith(".pdf")) {
                    return "application/pdf"; // 15 caracteres
                } else if (lowerFilename.endsWith(".txt")) {
                    return "text/plain"; // 10 caracteres
                }
            }
            return "application/octet-stream"; // 24 caracteres
        }
        
        // Normalizar tipos MIME conocidos y acortarlos para que quepan en 50 caracteres
        String normalizedType = contentType.toLowerCase().trim();
        
        // Para documentos Word, usar versiones cortas
        if (normalizedType.contains("wordprocessingml") || 
            normalizedType.contains("openxmlformats") ||
            (filename != null && filename.toLowerCase().endsWith(".docx"))) {
            return "application/msword-docx"; // 22 caracteres - versión corta para DOCX
        }
        
        if (normalizedType.contains("msword") || 
            (filename != null && filename.toLowerCase().endsWith(".doc"))) {
            return "application/msword"; // 19 caracteres
        }
        
        // Asegurar que no exceda 50 caracteres
        if (normalizedType.length() > 50) {
            // Acortar tipos conocidos
            if (normalizedType.startsWith("application/")) {
                if (normalizedType.contains("pdf")) return "application/pdf";
                if (normalizedType.contains("text")) return "text/plain";
                if (normalizedType.contains("csv")) return "text/csv";
            }
            // Si sigue siendo muy largo, truncar
            return normalizedType.substring(0, 50);
        }
        
        return normalizedType;
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
     * Eliminar documento y sus vectores asociados
     */
    @Transactional
    public void deleteDocument(UUID id) {
        // Verificar que el documento existe
        Optional<Document> documentOpt = documentRepository.findById(id);
        if (documentOpt.isEmpty()) {
            throw new RuntimeException("Documento no encontrado");
        }
        
        Document document = documentOpt.get();
        
        // Si el documento tiene un agente asociado, eliminar los vectores
        if (document.getAgentId() != null) {
            try {
                String agentIdStr = document.getAgentId().toString();
                
                // Contar cuántos vectores se van a eliminar
                long vectorCount = vectorEmbeddingRepository.countByAgentIdInMetadata(agentIdStr);
                logger.info("Found {} vectors for agentId: {}", vectorCount, agentIdStr);
                
                // Eliminar los vectores asociados
                int deletedVectors = vectorEmbeddingRepository.deleteByAgentIdInMetadata(agentIdStr);
                logger.info("Deleted {} vectors for agentId: {}", deletedVectors, agentIdStr);
                
            } catch (Exception e) {
                logger.error("Error deleting vectors for document {}: {}", id, e.getMessage(), e);
                // Continuamos con la eliminación del documento aunque falle la eliminación de vectores
            }
        } else {
            logger.warn("Document {} has no agentId, skipping vector deletion", id);
        }
        
        // Eliminar el documento
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
     * Obtener todos los documentos de un usuario específico
     */
    public List<DocumentResponse> getAllDocumentsByUser(Long userId) {
        return documentRepository.findByUser_IdOrderByUploadDateDesc(userId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener documentos por agente y usuario
     */
    public List<DocumentResponse> getDocumentsByAgentAndUser(UUID agentId, Long userId) {
        return documentRepository.findByUser_IdAndAgentId(userId, agentId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar documentos por usuario
     */
    public List<DocumentResponse> searchDocumentsByUser(String query, Long userId) {
        return documentRepository.findByUser_IdAndNameOrDescriptionContainingIgnoreCase(userId, query)
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener estadísticas de documentos por usuario
     */
    public com.dashboardai.dto.response.DocumentStats getDocumentStatsByUser(Long userId) {
        List<Document> documents = documentRepository.findByUser_IdOrderByUploadDateDesc(userId);
        
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
     * Obtener estadísticas de documentos globales (mantenido para compatibilidad)
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
    
    /**
     * Obtener session_name del agente basado en su ID
     */
    private String getAgentSessionName(UUID agentId) {
        // Buscar primero en AgentTelegram
        Optional<AgentTelegram> telegramAgent = agentTelegramRepository.findById(agentId);
        if (telegramAgent.isPresent() && telegramAgent.get().getSessionName() != null) {
            return telegramAgent.get().getSessionName();
        }
        
        // Buscar en AgentWhatsApp
        Optional<AgentWhatsApp> whatsappAgent = agentWhatsAppRepository.findById(agentId);
        if (whatsappAgent.isPresent() && whatsappAgent.get().getSessionName() != null) {
            return whatsappAgent.get().getSessionName();
        }
        
        logger.warn("No session name found for agent ID: {}", agentId);
        return null;
    }
    
    /**
     * Extraer plataforma del session_name
     */
    private String extractPlatformFromSession(String sessionName) {
        if (sessionName == null || sessionName.isEmpty()) {
            return "unknown";
        }
        
        if (sessionName.toLowerCase().contains("telegram")) {
            return "telegram";
        } else if (sessionName.toLowerCase().contains("whatsapp")) {
            return "whatsapp";
        } else {
            // Intentar extraer de formato: platform_type_id
            String[] parts = sessionName.split("_");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        
        return "unknown";
    }
    
    /**
     * Determina si un archivo es de tipo Excel
     */
    private boolean isExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase();
            if (lowerContentType.equals("application/vnd.ms-excel") ||
                lowerContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                lowerContentType.equals("application/excel") ||
                lowerContentType.equals("application/x-excel") ||
                lowerContentType.equals("application/x-msexcel")) {
                return true;
            }
        }
        
        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            return lowerFilename.endsWith(".xls") || lowerFilename.endsWith(".xlsx");
        }
        
        return false;
    }
}