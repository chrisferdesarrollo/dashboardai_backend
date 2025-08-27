package com.dashboardai.service;

import com.dashboardai.dto.request.UploadWorkflowRequest;
import com.dashboardai.dto.response.WorkflowResponse;
import com.dashboardai.entity.Workflow;
import com.dashboardai.repository.WorkflowRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkflowService {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);
    
    @Autowired
    private WorkflowRepository workflowRepository;

    private final ObjectMapper objectMapper;
    
    public WorkflowService() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Subir workflow desde archivo
     */
    public WorkflowResponse uploadWorkflow(MultipartFile file, String name, String description, Long userId) throws IOException {
        logger.info("Uploading workflow from file: {} for user: {}", file.getOriginalFilename(), userId);
        
        // Leer contenido del archivo
        String workflowJson = new String(file.getBytes());
        
        // Usar el nombre del archivo si no se proporciona uno
        if (name == null || name.trim().isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            name = originalFilename != null ? originalFilename.replace(".json", "") : "Workflow_" + System.currentTimeMillis();
        }
        
        // Verificar si ya existe un workflow con ese nombre para el usuario
        if (workflowRepository.existsByNameAndUserId(name, userId)) {
            throw new RuntimeException("Ya existe un workflow con ese nombre");
        }
        
        // Crear request
        UploadWorkflowRequest request = new UploadWorkflowRequest(name, description, workflowJson, false);
        
        return createWorkflow(request, userId);
    }
    
    /**
     * Crear workflow (solo en base de datos)
     */
    public WorkflowResponse createWorkflow(UploadWorkflowRequest request, Long userId) {
        try {
            logger.info("Creating workflow in database: {} for user: {}", request.getName(), userId);
            
            // Parsear el JSON para validarlo y extraer información
            JsonNode workflowNode = objectMapper.readTree(request.getWorkflowJson());
            
            // Contar nodos
            int nodeCount = countNodes(workflowNode);
            
            // Extraer tags si existen
            List<String> tags = extractTags(workflowNode);
            
            // Generar un ID único para el workflow (simulando el ID que tendría en n8n)
            String workflowId = "workflow_" + System.currentTimeMillis() + "_" + userId;
            
            // Crear entidad Workflow
            Workflow workflow = new Workflow();
            workflow.setN8nWorkflowId(workflowId);
            workflow.setName(request.getName());
            workflow.setDescription(request.getDescription());
            workflow.setWorkflowData(request.getWorkflowJson());
            workflow.setUserId(userId);
            workflow.setNodeCount(nodeCount);
            workflow.setTags(tags);
            workflow.setActive(request.isActivate()); // Usar el valor solicitado
            
            // Guardar en la base de datos
            Workflow savedWorkflow = workflowRepository.save(workflow);
            
            logger.info("Workflow created successfully with ID: {}", savedWorkflow.getId());
            
            return new WorkflowResponse(savedWorkflow);
            
        } catch (Exception e) {
            logger.error("Error creating workflow: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el workflow: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todos los workflows por usuario
     */
    @Transactional(readOnly = true)
    public List<WorkflowResponse> getAllWorkflows(Long userId) {
        try {
            List<Workflow> workflows = workflowRepository.findByUserIdAndNotDeleted(userId);
            return workflows.stream()
                    .map(WorkflowResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching workflows for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los workflows");
        }
    }
    
    /**
     * Obtener workflow por ID
     */
    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflowById(String workflowId, Long userId) {
        try {
            logger.info("Fetching workflow by ID: {} for user: {}", workflowId, userId);
            
            Optional<Workflow> workflowOpt = workflowRepository.findByN8nWorkflowIdAndUserId(workflowId, userId);
            
            if (workflowOpt.isEmpty() || workflowOpt.get().getIsDeleted()) {
                throw new RuntimeException("Workflow no encontrado o no tienes permisos para acceder a él");
            }
            
            return new WorkflowResponse(workflowOpt.get());
            
        } catch (Exception e) {
            logger.error("Error fetching workflow {}: {}", workflowId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el workflow: " + e.getMessage());
        }
    }
    
    /**
     * Activar workflow (solo en base de datos)
     */
    public WorkflowResponse activateWorkflow(String workflowId, Long userId) {
        try {
            logger.info("Activating workflow: {} for user: {}", workflowId, userId);
            
            Optional<Workflow> workflowOpt = workflowRepository.findByN8nWorkflowIdAndUserId(workflowId, userId);
            
            if (workflowOpt.isEmpty() || workflowOpt.get().getIsDeleted()) {
                throw new RuntimeException("Workflow no encontrado");
            }
            
            Workflow workflow = workflowOpt.get();
            
            // Actualizar estado en base de datos
            workflow.setActive(true);
            Workflow updatedWorkflow = workflowRepository.save(workflow);
            
            logger.info("Workflow activated successfully: {}", workflowId);
            return new WorkflowResponse(updatedWorkflow);
            
        } catch (Exception e) {
            logger.error("Error activating workflow {}: {}", workflowId, e.getMessage(), e);
            throw new RuntimeException("Error al activar el workflow: " + e.getMessage());
        }
    }
    
    /**
     * Desactivar workflow (solo en base de datos)
     */
    public WorkflowResponse deactivateWorkflow(String workflowId, Long userId) {
        try {
            logger.info("Deactivating workflow: {} for user: {}", workflowId, userId);
            
            Optional<Workflow> workflowOpt = workflowRepository.findByN8nWorkflowIdAndUserId(workflowId, userId);
            
            if (workflowOpt.isEmpty() || workflowOpt.get().getIsDeleted()) {
                throw new RuntimeException("Workflow no encontrado");
            }
            
            Workflow workflow = workflowOpt.get();
            
            // Actualizar estado en base de datos
            workflow.setActive(false);
            Workflow updatedWorkflow = workflowRepository.save(workflow);
            
            logger.info("Workflow deactivated successfully: {}", workflowId);
            return new WorkflowResponse(updatedWorkflow);
            
        } catch (Exception e) {
            logger.error("Error deactivating workflow {}: {}", workflowId, e.getMessage(), e);
            throw new RuntimeException("Error al desactivar el workflow: " + e.getMessage());
        }
    }
    
    /**
     * Eliminar workflow (borrado lógico)
     */
    public void deleteWorkflow(String workflowId, Long userId) {
        try {
            logger.info("Deleting workflow: {} for user: {}", workflowId, userId);
            
            Optional<Workflow> workflowOpt = workflowRepository.findByN8nWorkflowIdAndUserId(workflowId, userId);
            
            if (workflowOpt.isEmpty() || workflowOpt.get().getIsDeleted()) {
                throw new RuntimeException("Workflow no encontrado");
            }
            
            Workflow workflow = workflowOpt.get();
            
            // Desactivar primero si está activo (solo en base de datos)
            if (workflow.getActive()) {
                workflow.setActive(false);
            }
            
            // Borrado lógico
            workflow.setIsDeleted(true);
            workflowRepository.save(workflow);
            
            logger.info("Workflow deleted successfully: {}", workflowId);
            
        } catch (Exception e) {
            logger.error("Error deleting workflow {}: {}", workflowId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el workflow: " + e.getMessage());
        }
    }
    
    // Métodos auxiliares
    
    /**
     * Contar nodos en el workflow
     */
    private int countNodes(JsonNode workflowNode) {
        try {
            if (workflowNode.has("nodes") && workflowNode.get("nodes").isArray()) {
                return workflowNode.get("nodes").size();
            }
            return 0;
        } catch (Exception e) {
            logger.warn("Error counting nodes: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Extraer tags del workflow
     */
    private List<String> extractTags(JsonNode workflowNode) {
        List<String> tags = new ArrayList<>();
        try {
            if (workflowNode.has("tags") && workflowNode.get("tags").isArray()) {
                for (JsonNode tagNode : workflowNode.get("tags")) {
                    if (tagNode.has("name")) {
                        tags.add(tagNode.get("name").asText());
                    } else if (tagNode.isTextual()) {
                        tags.add(tagNode.asText());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error extracting tags: {}", e.getMessage());
        }
        return tags;
    }
}
