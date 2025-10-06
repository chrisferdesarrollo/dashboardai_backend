package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateDocumentRequest;
import com.dashboardai.dto.response.DocumentResponse;
import com.dashboardai.dto.response.DocumentUploadResponse;
import com.dashboardai.dto.response.DocumentStats;
import com.dashboardai.security.services.UserDetailsImpl;
import com.dashboardai.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class DocumentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    @Autowired
    private DocumentService documentService;
    
    /**
     * Obtener el ID del usuario autenticado
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId(); // Devolver ID como Long
    }
    
    /**
     * Subir documento y enviarlo automáticamente a N8N para vectorización
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "agentId", required = false) UUID agentId,
            @RequestParam(value = "authToken", required = false) String authToken,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication authentication) {
        
        try {
            logger.info("POST /api/documents/upload - Uploading document: {} ({})", 
                       name, file.getOriginalFilename());
            
            // Usar token del FormData si está disponible, sino usar del header
            String finalToken = authToken;
            if (finalToken == null || finalToken.isEmpty()) {
                finalToken = authHeader;
            }
            
            logger.info("Token source - FormData: {}, Header: {}", 
                       authToken != null ? "Present" : "Missing",
                       authHeader != null ? "Present" : "Missing");
            logger.info("Using token from: {}", 
                       authToken != null ? "FormData" : "Header");
            logger.info("Authorization header received: {}", authHeader != null ? "Present" : "Missing");
            
            // Crear request con metadatos
            CreateDocumentRequest request = new CreateDocumentRequest();
            request.setName(name);
            request.setDescription(description);
            request.setTags(tags != null ? List.of(tags) : null);
            request.setAgentId(agentId);
            
            // Obtener userId del usuario autenticado
            Long userId = getCurrentUserId(authentication);
            
            // Procesar documento y enviarlo a N8N
            DocumentResponse response = documentService.processDocumentForVectorization(file, request, finalToken, userId);
            
            return ResponseEntity.ok(DocumentUploadResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Error uploading document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(DocumentUploadResponse.error("Error al procesar el documento: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los documentos del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            logger.info("GET /api/documents - Getting all documents for user: {}", userId);
            List<DocumentResponse> documents = documentService.getAllDocumentsByUser(userId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            logger.error("Error getting documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtener documentos por agente del usuario autenticado
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByAgent(@PathVariable UUID agentId, Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            logger.info("GET /api/documents/agent/{} - Getting documents by agent for user: {}", agentId, userId);
            List<DocumentResponse> documents = documentService.getDocumentsByAgentAndUser(agentId, userId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            logger.error("Error getting documents by agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtener documento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID id) {
        try {
            logger.info("GET /api/documents/{} - Getting document by ID", id);
            DocumentResponse document = documentService.getDocumentById(id);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            logger.error("Error getting document by ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Eliminar documento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        try {
            logger.info("DELETE /api/documents/{} - Deleting document", id);
            documentService.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Buscar documentos por nombre del usuario autenticado
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(@RequestParam String query, Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            logger.info("GET /api/documents/search?query={} - Searching documents for user: {}", query, userId);
            List<DocumentResponse> documents = documentService.searchDocumentsByUser(query, userId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            logger.error("Error searching documents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Webhook para que N8N notifique el resultado del procesamiento
     */
    @PostMapping("/webhook/processing-complete")
    public ResponseEntity<String> processingComplete(
            @RequestParam("documentId") UUID documentId,
            @RequestParam("status") String status,
            @RequestParam(value = "error", required = false) String error) {
        
        try {
            logger.info("Processing complete webhook for document: {} with status: {}", documentId, status);
            documentService.updateProcessingStatus(documentId, status, error);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<DocumentStats> getDocumentStats() {
        try {
            logger.info("GET /api/documents/stats - Getting document statistics");
            DocumentStats stats = documentService.getDocumentStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting document stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Endpoint para que N8N notifique que el procesamiento está completo
     */
    @PostMapping("/{id}/processing-complete")
    public ResponseEntity<Void> markProcessingComplete(@PathVariable UUID id) {
        try {
            logger.info("POST /api/documents/{}/processing-complete - Marking document as processed", id);
            documentService.markAsProcessed(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error marking document as processed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}