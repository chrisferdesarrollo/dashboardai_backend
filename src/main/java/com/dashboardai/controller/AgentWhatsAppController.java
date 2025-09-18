package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateWhatsAppAgentRequest;
import com.dashboardai.dto.response.WhatsAppAgentResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.AgentWhatsApp;
import com.dashboardai.service.AgentWhatsAppService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agents/whatsapp")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class AgentWhatsAppController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentWhatsAppController.class);
    
    @Autowired
    private AgentWhatsAppService agentWhatsAppService;
    
    /**
     * Crear un nuevo agente WhatsApp
     */
    @PostMapping
    public ResponseEntity<?> createAgent(@Valid @RequestBody CreateWhatsAppAgentRequest request) {
        try {
            logger.info("POST /api/agents/whatsapp - Creating WhatsApp agent: {}", request.getName());
            
            WhatsAppAgentResponse agentResponse = agentWhatsAppService.createAgent(request);
            
            return ResponseEntity.ok(new CreateAgentResponseWrapper(true, agentResponse, null));
            
        } catch (Exception e) {
            logger.error("Error creating WhatsApp agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los agentes WhatsApp
     */
    @GetMapping
    public ResponseEntity<?> getAllAgents() {
        try {
            logger.info("GET /api/agents/whatsapp - Fetching all WhatsApp agents");
            
            List<WhatsAppAgentResponse> agents = agentWhatsAppService.getAllAgents();
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, agents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener agentes WhatsApp por usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAgentsByUser(@PathVariable Long userId) {
        try {
            logger.info("GET /api/agents/whatsapp/user/{} - Fetching WhatsApp agents for user", userId);
            
            List<WhatsAppAgentResponse> agents = agentWhatsAppService.getAgentsByUser(userId);
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, agents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agents for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener un agente WhatsApp por ID
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<?> getAgentById(@PathVariable UUID agentId) {
        try {
            logger.info("GET /api/agents/whatsapp/{} - Fetching WhatsApp agent by ID", agentId);
            
            WhatsAppAgentResponse agent = agentWhatsAppService.getAgentById(agentId);
            
            return ResponseEntity.ok(agent);
            
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Obtener un agente WhatsApp por sessionName
     */
    @GetMapping("/session/{sessionName}")
    public ResponseEntity<?> getAgentBySessionName(@PathVariable String sessionName) {
        try {
            logger.info("GET /api/agents/whatsapp/session/{} - Fetching WhatsApp agent by session name", sessionName);
            
            WhatsAppAgentResponse agent = agentWhatsAppService.getAgentBySessionName(sessionName);
            
            return ResponseEntity.ok(agent);
            
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agent by session name {}: {}", sessionName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Actualizar el estado de un agente WhatsApp
     */
    @PutMapping("/{agentId}/status")
    public ResponseEntity<?> updateAgentStatus(@PathVariable UUID agentId, @RequestBody UpdateStatusRequest request) {
        try {
            logger.info("PUT /api/agents/whatsapp/{}/status - Updating status to: {}", agentId, request.getStatus());
            
            WhatsAppAgentResponse updatedAgent = agentWhatsAppService.updateAgentStatus(agentId, request.getStatus());
            
            return ResponseEntity.ok(updatedAgent);
            
        } catch (Exception e) {
            logger.error("Error updating WhatsApp agent status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Eliminar un agente WhatsApp
     */
    @DeleteMapping("/{agentId}")
    public ResponseEntity<?> deleteAgent(@PathVariable UUID agentId) {
        try {
            logger.info("DELETE /api/agents/whatsapp/{} - Deleting WhatsApp agent", agentId);
            
            agentWhatsAppService.deleteAgent(agentId);
            
            return ResponseEntity.ok(new MessageResponse("Agente WhatsApp eliminado exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error deleting WhatsApp agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Incrementar contador de ejecuciones
     */
    @PostMapping("/{agentId}/increment-executions")
    public ResponseEntity<?> incrementExecutions(@PathVariable UUID agentId) {
        try {
            logger.info("POST /api/agents/whatsapp/{}/increment-executions - Incrementing execution count", agentId);
            
            agentWhatsAppService.incrementExecutions(agentId);
            
            return ResponseEntity.ok(new MessageResponse("Contador de ejecuciones incrementado"));
            
        } catch (Exception e) {
            logger.error("Error incrementing executions for WhatsApp agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Clases internas para wrappers de respuesta
    public static class CreateAgentResponseWrapper {
        private boolean success;
        private WhatsAppAgentResponse data;
        private String message;
        
        public CreateAgentResponseWrapper(boolean success, WhatsAppAgentResponse data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public WhatsAppAgentResponse getData() { return data; }
        public String getMessage() { return message; }
    }
    
    public static class GetAgentsResponseWrapper {
        private boolean success;
        private List<WhatsAppAgentResponse> data;
        private String message;
        
        public GetAgentsResponseWrapper(boolean success, List<WhatsAppAgentResponse> data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public List<WhatsAppAgentResponse> getData() { return data; }
        public String getMessage() { return message; }
    }
    
    public static class UpdateStatusRequest {
        private AgentWhatsApp.AgentStatus status;
        
        public AgentWhatsApp.AgentStatus getStatus() { return status; }
        public void setStatus(AgentWhatsApp.AgentStatus status) { this.status = status; }
    }
}