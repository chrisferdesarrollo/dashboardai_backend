package com.dashboardai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.dashboardai.entity.N8nConfiguration;
import com.dashboardai.entity.Workflow;
import com.dashboardai.repository.N8nConfigurationRepository;
import com.dashboardai.repository.WorkflowRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class N8nIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(N8nIntegrationService.class);
    
    @Autowired
    private N8nConfigurationService n8nConfigurationService;
    
    @Autowired
    private N8nConfigurationRepository n8nConfigurationRepository;
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Obtiene el estado de los workflows en n8n para un usuario específico
     */
    public Map<String, Object> getWorkflowsStatus(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Getting workflows status for user: {}", userId);
            
            // Obtener configuración de n8n del usuario
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                response.put("success", false);
                response.put("error", "No N8n configuration found for user");
                return response;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Llamar a la API de n8n para obtener workflows
            String url = n8nConfig.getApiBase() + "/workflows";
            ResponseEntity<String> n8nResponse = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );
            
            if (n8nResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode workflows = objectMapper.readTree(n8nResponse.getBody());
                List<Map<String, Object>> workflowStatuses = new ArrayList<>();
                
                if (workflows.has("data") && workflows.get("data").isArray()) {
                    for (JsonNode workflow : workflows.get("data")) {
                        Map<String, Object> workflowStatus = new HashMap<>();
                        workflowStatus.put("id", workflow.get("id").asText());
                        workflowStatus.put("name", workflow.get("name").asText());
                        workflowStatus.put("active", workflow.get("active").asBoolean());
                        workflowStatus.put("lastRun", workflow.has("lastRun") ? workflow.get("lastRun").asText() : null);
                        workflowStatuses.add(workflowStatus);
                    }
                }
                
                response.put("success", true);
                response.put("data", workflowStatuses);
                response.put("count", workflowStatuses.size());
            } else {
                response.put("success", false);
                response.put("error", "Failed to fetch workflows from n8n");
            }
            
        } catch (RestClientException e) {
            logger.error("Error connecting to n8n for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error getting workflows status for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Error retrieving workflows status: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Verifica la salud de la conexión n8n para un usuario específico
     */
    public Map<String, Object> checkN8nHealth(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Checking n8n health for user: {}", userId);
            
            // Obtener configuración de n8n del usuario
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                response.put("success", false);
                response.put("status", "unhealthy");
                response.put("error", "No N8n configuration found for user");
                return response;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Hacer ping a la API de n8n
            String url = n8nConfig.getApiBase() + "/workflows";
            ResponseEntity<String> n8nResponse = restTemplate.exchange(
                url, HttpMethod.HEAD, entity, String.class
            );
            
            if (n8nResponse.getStatusCode() == HttpStatus.OK) {
                // Actualizar estado de la configuración
                n8nConfig.setLastTestDate(LocalDateTime.now());
                n8nConfig.setLastTestStatus("healthy");
                n8nConfigurationRepository.save(n8nConfig);
                
                response.put("success", true);
                response.put("status", "healthy");
                response.put("message", "N8n connection is healthy");
                response.put("lastChecked", LocalDateTime.now().toString());
            } else {
                response.put("success", false);
                response.put("status", "unhealthy");
                response.put("error", "N8n returned status: " + n8nResponse.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Error connecting to n8n for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("status", "unhealthy");
            response.put("error", "Connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error checking n8n health for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("status", "unhealthy");
            response.put("error", "Error checking n8n health: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Obtiene los workflows del usuario desde n8n y sincroniza con la base de datos local
     */
    public Map<String, Object> getUserWorkflowsFromN8n(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Getting user workflows from n8n for user: {}", userId);
            
            // Obtener configuración de n8n del usuario
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                response.put("success", false);
                response.put("error", "No N8n configuration found for user");
                return response;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Obtener workflows desde n8n
            String url = n8nConfig.getApiBase() + "/workflows";
            ResponseEntity<String> n8nResponse = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );
            
            if (n8nResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode workflowsData = objectMapper.readTree(n8nResponse.getBody());
                List<Map<String, Object>> workflows = new ArrayList<>();
                
                if (workflowsData.has("data") && workflowsData.get("data").isArray()) {
                    for (JsonNode workflowNode : workflowsData.get("data")) {
                        String n8nWorkflowId = workflowNode.get("id").asText();
                        
                        // Buscar o crear workflow en base de datos local
                        Optional<Workflow> existingWorkflow = workflowRepository.findByN8nWorkflowId(n8nWorkflowId);
                        Workflow workflow;
                        
                        if (existingWorkflow.isPresent()) {
                            workflow = existingWorkflow.get();
                            // Actualizar datos
                            workflow.setName(workflowNode.get("name").asText());
                            workflow.setActive(workflowNode.get("active").asBoolean());
                            workflow.setUpdatedAt(LocalDateTime.now());
                        } else {
                            // Crear nuevo workflow
                            workflow = new Workflow();
                            workflow.setN8nWorkflowId(n8nWorkflowId);
                            workflow.setName(workflowNode.get("name").asText());
                            workflow.setActive(workflowNode.get("active").asBoolean());
                            workflow.setWorkflowData(workflowNode.toString());
                            workflow.setCreatedAt(LocalDateTime.now());
                            workflow.setUpdatedAt(LocalDateTime.now());
                        }
                        
                        // Guardar workflow
                        workflow = workflowRepository.save(workflow);
                        
                        // Agregar a la respuesta
                        Map<String, Object> workflowData = new HashMap<>();
                        workflowData.put("id", workflow.getId().toString());
                        workflowData.put("n8nWorkflowId", workflow.getN8nWorkflowId());
                        workflowData.put("name", workflow.getName());
                        workflowData.put("description", workflow.getDescription());
                        workflowData.put("active", workflow.getActive());
                        workflowData.put("nodeCount", workflow.getNodeCount());
                        workflowData.put("tags", workflow.getTags());
                        workflowData.put("createdAt", workflow.getCreatedAt());
                        workflowData.put("updatedAt", workflow.getUpdatedAt());
                        
                        workflows.add(workflowData);
                    }
                }
                
                response.put("success", true);
                response.put("workflows", workflows);
                response.put("count", workflows.size());
                
            } else {
                response.put("success", false);
                response.put("error", "Failed to fetch workflows from n8n: " + n8nResponse.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Error connecting to n8n for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error getting user workflows from n8n for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Error retrieving user workflows from n8n: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Despliega workflows de un agente en n8n
     */
    public boolean deployAgentWorkflows(String agentId, Long userId, List<String> workflowIds) {
        try {
            logger.info("Deploying workflows for agent: {} with workflows: {}", agentId, workflowIds);
            
            // Obtener configuración de n8n del usuario del agente
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                logger.error("No N8n configuration found for user: {}", userId);
                return false;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            boolean allSuccessful = true;
            
            for (String workflowId : workflowIds) {
                try {
                    // Obtener workflow de la base de datos local
                    Optional<Workflow> workflowOpt = workflowRepository.findById(UUID.fromString(workflowId));
                    if (workflowOpt.isEmpty()) {
                        logger.error("Workflow not found in local database: {}", workflowId);
                        allSuccessful = false;
                        continue;
                    }
                    
                    Workflow workflow = workflowOpt.get();
                    
                    // Activar workflow en n8n
                    String activateUrl = n8nConfig.getApiBase() + "/workflows/" + workflow.getN8nWorkflowId() + "/activate";
                    
                    Map<String, Object> activateData = new HashMap<>();
                    activateData.put("active", true);
                    
                    HttpEntity<Map<String, Object>> activateEntity = new HttpEntity<>(activateData, headers);
                    
                    ResponseEntity<String> activateResponse = restTemplate.exchange(
                        activateUrl, HttpMethod.PATCH, activateEntity, String.class
                    );
                    
                    if (activateResponse.getStatusCode() == HttpStatus.OK) {
                        logger.info("Successfully activated workflow {} for agent {}", workflowId, agentId);
                        
                        // Actualizar estado en base de datos local
                        workflow.setActive(true);
                        workflow.setUpdatedAt(LocalDateTime.now());
                        workflowRepository.save(workflow);
                        
                    } else {
                        logger.error("Failed to activate workflow {} for agent {}: {}", 
                                   workflowId, agentId, activateResponse.getStatusCode());
                        allSuccessful = false;
                    }
                    
                } catch (Exception e) {
                    logger.error("Error deploying workflow {} for agent {}: {}", 
                               workflowId, agentId, e.getMessage());
                    allSuccessful = false;
                }
            }
            
            if (allSuccessful) {
                logger.info("All workflows deployed successfully for agent: {}", agentId);
            } else {
                logger.warn("Some workflows failed to deploy for agent: {}", agentId);
            }
            
            return allSuccessful;
            
        } catch (Exception e) {
            logger.error("Error deploying workflows for agent {}: {}", agentId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Ejecuta un workflow específico en n8n
     */
    public Map<String, Object> executeWorkflow(Long userId, String workflowId, Map<String, Object> inputData) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Executing workflow {} for user: {}", workflowId, userId);
            
            // Obtener configuración de n8n del usuario
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                response.put("success", false);
                response.put("error", "No N8n configuration found for user");
                return response;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Obtener workflow de la base de datos local
            Optional<Workflow> workflowOpt = workflowRepository.findById(UUID.fromString(workflowId));
            if (workflowOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "Workflow not found");
                return response;
            }
            
            Workflow workflow = workflowOpt.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            // Ejecutar workflow via webhook o API
            String executeUrl = n8nConfig.getApiBase() + "/workflows/" + workflow.getN8nWorkflowId() + "/execute";
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(inputData, headers);
            
            ResponseEntity<String> executeResponse = restTemplate.exchange(
                executeUrl, HttpMethod.POST, entity, String.class
            );
            
            if (executeResponse.getStatusCode() == HttpStatus.OK || 
                executeResponse.getStatusCode() == HttpStatus.CREATED) {
                
                JsonNode executionResult = objectMapper.readTree(executeResponse.getBody());
                
                response.put("success", true);
                response.put("executionId", executionResult.has("id") ? executionResult.get("id").asText() : null);
                response.put("status", executionResult.has("status") ? executionResult.get("status").asText() : "started");
                response.put("data", executionResult);
                
                logger.info("Workflow {} executed successfully for user {}", workflowId, userId);
                
            } else {
                response.put("success", false);
                response.put("error", "Failed to execute workflow: " + executeResponse.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Error connecting to n8n for workflow execution: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "Connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error executing workflow {} for user {}: {}", workflowId, userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Error executing workflow: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Sincroniza workflows entre n8n y la base de datos local
     */
    public Map<String, Object> syncWorkflows(Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Syncing workflows for user: {}", userId);
            
            // Obtener workflows desde n8n
            Map<String, Object> n8nResult = getUserWorkflowsFromN8n(userId);
            
            if ((Boolean) n8nResult.get("success")) {
                response.put("success", true);
                response.put("message", "Workflows synchronized successfully");
                response.put("syncedWorkflows", n8nResult.get("count"));
            } else {
                response.put("success", false);
                response.put("error", n8nResult.get("error"));
            }
            
        } catch (Exception e) {
            logger.error("Error syncing workflows for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Error syncing workflows: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Desactiva un workflow específico en n8n
     */
    public Map<String, Object> deactivateWorkflow(Long userId, String workflowId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Deactivating workflow {} for user: {}", workflowId, userId);
            
            // Obtener configuración de n8n del usuario
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                response.put("success", false);
                response.put("error", "No N8n configuration found for user");
                return response;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Obtener workflow de la base de datos local
            Optional<Workflow> workflowOpt = workflowRepository.findById(UUID.fromString(workflowId));
            if (workflowOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "Workflow not found");
                return response;
            }
            
            Workflow workflow = workflowOpt.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            // Desactivar workflow en n8n
            String deactivateUrl = n8nConfig.getApiBase() + "/workflows/" + workflow.getN8nWorkflowId() + "/activate";
            
            Map<String, Object> deactivateData = new HashMap<>();
            deactivateData.put("active", false);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(deactivateData, headers);
            
            ResponseEntity<String> deactivateResponse = restTemplate.exchange(
                deactivateUrl, HttpMethod.PATCH, entity, String.class
            );
            
            if (deactivateResponse.getStatusCode() == HttpStatus.OK) {
                // Actualizar estado en base de datos local
                workflow.setActive(false);
                workflow.setUpdatedAt(LocalDateTime.now());
                workflowRepository.save(workflow);
                
                response.put("success", true);
                response.put("message", "Workflow deactivated successfully");
                
                logger.info("Successfully deactivated workflow {} for user {}", workflowId, userId);
                
            } else {
                response.put("success", false);
                response.put("error", "Failed to deactivate workflow: " + deactivateResponse.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Error connecting to n8n for workflow deactivation: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "Connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deactivating workflow {} for user {}: {}", workflowId, userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Error deactivating workflow: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Obtiene el historial de ejecuciones de un workflow
     */
    public Map<String, Object> getWorkflowExecutions(Long userId, String workflowId, int limit) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Getting executions for workflow {} for user: {}", workflowId, userId);
            
            // Obtener configuración de n8n del usuario
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isEmpty()) {
                response.put("success", false);
                response.put("error", "No N8n configuration found for user");
                return response;
            }
            
            N8nConfiguration n8nConfig = config.get();
            
            // Obtener workflow de la base de datos local
            Optional<Workflow> workflowOpt = workflowRepository.findById(UUID.fromString(workflowId));
            if (workflowOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "Workflow not found");
                return response;
            }
            
            Workflow workflow = workflowOpt.get();
            
            // Crear headers con autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (n8nConfig.getApiKey() != null && !n8nConfig.getApiKey().isEmpty()) {
                headers.set("X-N8N-API-KEY", n8nConfig.getApiKey());
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Obtener ejecuciones desde n8n
            String executionsUrl = n8nConfig.getApiBase() + "/executions" + 
                                  "?workflowId=" + workflow.getN8nWorkflowId() + 
                                  "&limit=" + limit;
            
            ResponseEntity<String> executionsResponse = restTemplate.exchange(
                executionsUrl, HttpMethod.GET, entity, String.class
            );
            
            if (executionsResponse.getStatusCode() == HttpStatus.OK) {
                JsonNode executionsData = objectMapper.readTree(executionsResponse.getBody());
                
                response.put("success", true);
                response.put("executions", executionsData);
                
                logger.info("Successfully retrieved executions for workflow {} for user {}", workflowId, userId);
                
            } else {
                response.put("success", false);
                response.put("error", "Failed to retrieve executions: " + executionsResponse.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Error connecting to n8n for executions: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "Connection error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error getting executions for workflow {} for user {}: {}", workflowId, userId, e.getMessage());
            response.put("success", false);
            response.put("error", "Error retrieving executions: " + e.getMessage());
        }
        
        return response;
    }
}