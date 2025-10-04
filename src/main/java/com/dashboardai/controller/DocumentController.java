package com.dashboardai.controller;

import com.dashboardai.dto.request.UpdateDocumentRequest;
import com.dashboardai.dto.response.DocumentResponse;
import com.dashboardai.dto.response.DocumentUploadResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.Document;
import com.dashboardai.service.DocumentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class DocumentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    @Autowired
    private DocumentService documentService;
    
    /**
     * Subir un nuevo documento
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "agentId", required = false) UUID agentId) {
        
        try {
            logger.info("POST /api/documents/upload - Subiendo documento: {}", name);
            
            Document document = documentService.uploadDocument(file, name, description, tags, agentId);
            DocumentResponse documentResponse = DocumentResponse.fromEntity(document);
            
            return ResponseEntity.ok(DocumentUploadResponse.success(documentResponse));
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al subir documento: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(DocumentUploadResponse.error(e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Error interno al subir documento: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DocumentUploadResponse.error("Error interno del servidor"));
        }
    }
    
    /**
     * Obtener estadísticas de documentos
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDocumentStats() {
        try {
            logger.info("GET /api/documents/stats");
            
            Map<String, Object> stats = documentService.getDocumentStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas de documentos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener todos los documentos con paginación
     */
    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            logger.info("GET /api/documents - Página: {}, Tamaño: {}", page, size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Document> documents = documentService.getAllDocuments(pageable);
            Page<DocumentResponse> response = documents.map(DocumentResponse::fromEntity);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo documentos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener documento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentById(@PathVariable UUID id) {
        try {
            logger.info("GET /api/documents/{}", id);
            
            Optional<Document> document = documentService.getDocumentById(id);
            if (document.isPresent()) {
                return ResponseEntity.ok(DocumentResponse.fromEntity(document.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error obteniendo documento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error interno del servidor"));
        }
    }
    
    /**
     * Obtener documentos por agente
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByAgent(@PathVariable UUID agentId) {
        try {
            logger.info("GET /api/documents/agent/{}", agentId);
            
            List<Document> documents = documentService.getDocumentsByAgent(agentId);
            List<DocumentResponse> response = documents.stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo documentos del agente {}: {}", agentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Buscar documentos por término
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(@RequestParam String query) {
        try {
            logger.info("GET /api/documents/search?query={}", query);
            
            List<Document> documents = documentService.searchDocuments(query);
            List<DocumentResponse> response = documents.stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error buscando documentos con query '{}': {}", query, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener documentos por tag
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByTag(@PathVariable String tag) {
        try {
            logger.info("GET /api/documents/tag/{}", tag);
            
            List<Document> documents = documentService.getDocumentsByTag(tag);
            List<DocumentResponse> response = documents.stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo documentos con tag '{}': {}", tag, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Actualizar metadatos del documento
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable UUID id, 
            @Valid @RequestBody UpdateDocumentRequest request) {
        
        try {
            logger.info("PUT /api/documents/{} - Actualizando documento", id);
            
            Document document = documentService.updateDocument(
                id, 
                request.getName(), 
                request.getDescription(), 
                request.getTags(), 
                request.getAgentId()
            );
            
            return ResponseEntity.ok(DocumentResponse.fromEntity(document));
            
        } catch (IllegalArgumentException e) {
            logger.error("Documento no encontrado: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error actualizando documento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error interno del servidor"));
        }
    }
    
    /**
     * Eliminar documento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID id) {
        try {
            logger.info("DELETE /api/documents/{}", id);
            
            documentService.deleteDocument(id);
            return ResponseEntity.ok(new MessageResponse("Documento eliminado exitosamente"));
            
        } catch (IllegalArgumentException e) {
            logger.error("Documento no encontrado: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error eliminando documento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error interno del servidor"));
        }
    }
    
    /**
     * Actualizar estado de procesamiento (endpoint para N8N)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateProcessingStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        
        try {
            logger.info("PUT /api/documents/{}/status - Nuevo estado: {}", id, status);
            
            Document.ProcessingStatus processingStatus = Document.ProcessingStatus.valueOf(status.toUpperCase());
            documentService.updateProcessingStatus(id, processingStatus);
            
            return ResponseEntity.ok(new MessageResponse("Estado actualizado exitosamente"));
            
        } catch (IllegalArgumentException e) {
            logger.error("Estado o documento inválido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Estado o documento inválido"));
                
        } catch (Exception e) {
            logger.error("Error actualizando estado del documento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error interno del servidor"));
        }
    }
    
    /**
     * Obtener estadísticas de documentos
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDocumentStats() {
        try {
            logger.info("GET /api/documents/stats");
            
            Map<String, Object> stats = documentService.getDocumentStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}