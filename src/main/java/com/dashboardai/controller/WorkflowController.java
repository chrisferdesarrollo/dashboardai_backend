package com.dashboardai.controller;

import com.dashboardai.dto.request.UploadWorkflowRequest;
import com.dashboardai.dto.response.WorkflowResponse;
import com.dashboardai.security.services.UserDetailsImpl;
import com.dashboardai.service.WorkflowService;
//import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class WorkflowController {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowController.class);
    
    @Autowired
    private WorkflowService workflowService;
    
    /**
     * Obtener el ID del usuario autenticado
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            // Temporal: usar usuario testuser (ID 2) cuando no hay autenticación
            return 2L; // Usuario testuser que existe en la base de datos
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    /**
     * Subir workflow desde archivo JSON
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadWorkflow(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {
        try {
            logger.info("POST /api/workflows/upload - Uploading workflow file: {}", file.getOriginalFilename());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new WorkflowResponseWrapper(false, null, "El archivo está vacío"));
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.endsWith(".json")) {
                return ResponseEntity.badRequest()
                        .body(new WorkflowResponseWrapper(false, null, "Solo se permiten archivos JSON"));
            }
            
            WorkflowResponse workflow = workflowService.uploadWorkflow(file, name, description, getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowResponseWrapper(true, workflow, null));
            
        } catch (Exception e) {
            logger.error("Error uploading workflow: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Crear workflow desde JSON string
     */
    @PostMapping("/create")
    public ResponseEntity<?> createWorkflow(@Valid @RequestBody UploadWorkflowRequest request, Authentication authentication) {
        try {
            logger.info("POST /api/workflows/create - Creating workflow: {}", request.getName());
            
            WorkflowResponse workflow = workflowService.createWorkflow(request, getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowResponseWrapper(true, workflow, null));
            
        } catch (Exception e) {
            logger.error("Error creating workflow: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los workflows
     */
    @GetMapping
    public ResponseEntity<?> getWorkflows(Authentication authentication) {
        try {
            logger.info("GET /api/workflows - Fetching all workflows");
            
            List<WorkflowResponse> workflows = workflowService.getAllWorkflows(getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowListResponseWrapper(true, workflows, null));
            
        } catch (Exception e) {
            logger.error("Error fetching workflows: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowListResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener workflow por ID
     */
    @GetMapping("/{workflowId}")
    public ResponseEntity<?> getWorkflow(@PathVariable String workflowId, Authentication authentication) {
        try {
            logger.info("GET /api/workflows/{} - Fetching workflow", workflowId);
            
            WorkflowResponse workflow = workflowService.getWorkflowById(workflowId, getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowResponseWrapper(true, workflow, null));
            
        } catch (Exception e) {
            logger.error("Error fetching workflow {}: {}", workflowId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Activar workflow en n8n
     */
    @PostMapping("/{workflowId}/activate")
    public ResponseEntity<?> activateWorkflow(@PathVariable String workflowId, Authentication authentication) {
        try {
            logger.info("POST /api/workflows/{}/activate - Activating workflow", workflowId);
            
            WorkflowResponse workflow = workflowService.activateWorkflow(workflowId, getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowResponseWrapper(true, workflow, null));
            
        } catch (Exception e) {
            logger.error("Error activating workflow {}: {}", workflowId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Desactivar workflow en n8n
     */
    @PostMapping("/{workflowId}/deactivate")
    public ResponseEntity<?> deactivateWorkflow(@PathVariable String workflowId, Authentication authentication) {
        try {
            logger.info("POST /api/workflows/{}/deactivate - Deactivating workflow", workflowId);
            
            WorkflowResponse workflow = workflowService.deactivateWorkflow(workflowId, getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowResponseWrapper(true, workflow, null));
            
        } catch (Exception e) {
            logger.error("Error deactivating workflow {}: {}", workflowId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Eliminar workflow
     */
    @DeleteMapping("/{workflowId}")
    public ResponseEntity<?> deleteWorkflow(@PathVariable String workflowId, Authentication authentication) {
        try {
            logger.info("DELETE /api/workflows/{} - Deleting workflow", workflowId);
            
            workflowService.deleteWorkflow(workflowId, getCurrentUserId(authentication));
            
            return ResponseEntity.ok(new WorkflowResponseWrapper(true, null, null));
            
        } catch (Exception e) {
            logger.error("Error deleting workflow {}: {}", workflowId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new WorkflowResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    // Clases auxiliares para las respuestas
    public static class WorkflowResponseWrapper {
        private boolean success;
        private WorkflowResponse data;
        private String error;
        
        public WorkflowResponseWrapper(boolean success, WorkflowResponse data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public WorkflowResponse getData() { return data; }
        public String getError() { return error; }
    }
    
    public static class WorkflowListResponseWrapper {
        private boolean success;
        private List<WorkflowResponse> data;
        private String error;
        
        public WorkflowListResponseWrapper(boolean success, List<WorkflowResponse> data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public List<WorkflowResponse> getData() { return data; }
        public String getError() { return error; }
    }
}
