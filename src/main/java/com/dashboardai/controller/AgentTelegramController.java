package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateTelegramAgentRequest;
import com.dashboardai.dto.response.TelegramAgentResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.AgentTelegram;
import com.dashboardai.service.AgentTelegramService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/agents/telegram")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class AgentTelegramController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentTelegramController.class);
    
    @Autowired
    private AgentTelegramService agentTelegramService;
    
    /**
     * Crear un nuevo agente Telegram
     */
    @PostMapping
    public ResponseEntity<?> createAgent(@Valid @RequestBody CreateTelegramAgentRequest request) {
        try {
            logger.info("POST /api/agents/telegram - Creating Telegram agent: {}", request.getName());
            
            TelegramAgentResponse agentResponse = agentTelegramService.createAgent(request);
            
            return ResponseEntity.ok(new CreateAgentResponseWrapper(true, agentResponse, null));
            
        } catch (Exception e) {
            logger.error("Error creating Telegram agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los agentes Telegram
     */
    @GetMapping
    public ResponseEntity<?> getAllAgents() {
        try {
            logger.info("GET /api/agents/telegram - Fetching all Telegram agents");
            
            List<TelegramAgentResponse> agents = agentTelegramService.getAllAgents();
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, agents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching Telegram agents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener agentes Telegram por usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAgentsByUser(@PathVariable Long userId) {
        try {
            logger.info("GET /api/agents/telegram/user/{} - Fetching Telegram agents for user", userId);
            
            List<TelegramAgentResponse> agents = agentTelegramService.getAgentsByUser(userId);
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, agents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching Telegram agents for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener un agente Telegram por ID
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<?> getAgentById(@PathVariable UUID agentId) {
        try {
            logger.info("GET /api/agents/telegram/{} - Fetching Telegram agent by ID", agentId);
            
            TelegramAgentResponse agent = agentTelegramService.getAgentById(agentId);
            
            return ResponseEntity.ok(agent);
            
        } catch (Exception e) {
            logger.error("Error fetching Telegram agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Obtener un agente Telegram por botName
     */
    @GetMapping("/bot/{botName}")
    public ResponseEntity<?> getAgentByBotName(@PathVariable String botName) {
        try {
            logger.info("GET /api/agents/telegram/bot/{} - Fetching Telegram agent by bot name", botName);
            
            TelegramAgentResponse agent = agentTelegramService.getAgentByBotName(botName);
            
            return ResponseEntity.ok(agent);
            
        } catch (Exception e) {
            logger.error("Error fetching Telegram agent by bot name {}: {}", botName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Obtener el token del bot para uso en workflows
     * Este endpoint puede ser llamado por n8n para obtener el token necesario
     */
    @GetMapping("/{agentId}/bot-token")
    public ResponseEntity<?> getBotToken(@PathVariable UUID agentId) {
        try {
            logger.info("GET /api/agents/telegram/{}/bot-token - Fetching bot token for workflow", agentId);
            
            String botToken = agentTelegramService.getBotToken(agentId);
            
            // Respuesta con el token (solo para workflows autorizados)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("botToken", botToken);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching bot token for agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Actualizar el estado de un agente Telegram
     */
    @PutMapping("/{agentId}/status")
    public ResponseEntity<?> updateAgentStatus(@PathVariable UUID agentId, @RequestBody UpdateStatusRequest request) {
        try {
            logger.info("PUT /api/agents/telegram/{}/status - Updating status to: {}", agentId, request.getStatus());
            
            TelegramAgentResponse updatedAgent = agentTelegramService.updateAgentStatus(agentId, request.getStatus());
            
            return ResponseEntity.ok(updatedAgent);
            
        } catch (Exception e) {
            logger.error("Error updating Telegram agent status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Eliminar un agente Telegram
     */
    @DeleteMapping("/{agentId}")
    public ResponseEntity<?> deleteAgent(@PathVariable UUID agentId) {
        try {
            logger.info("DELETE /api/agents/telegram/{} - Deleting Telegram agent", agentId);
            
            agentTelegramService.deleteAgent(agentId);
            
            return ResponseEntity.ok(new MessageResponse("Agente Telegram eliminado exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error deleting Telegram agent {}: {}", agentId, e.getMessage(), e);
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
            logger.info("POST /api/agents/telegram/{}/increment-executions - Incrementing execution count", agentId);
            
            agentTelegramService.incrementExecutions(agentId);
            
            return ResponseEntity.ok(new MessageResponse("Contador de ejecuciones incrementado"));
            
        } catch (Exception e) {
            logger.error("Error incrementing executions for Telegram agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Clases internas para wrappers de respuesta
    public static class CreateAgentResponseWrapper {
        private boolean success;
        private TelegramAgentResponse data;
        private String message;
        
        public CreateAgentResponseWrapper(boolean success, TelegramAgentResponse data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public TelegramAgentResponse getData() { return data; }
        public String getMessage() { return message; }
    }
    
    public static class GetAgentsResponseWrapper {
        private boolean success;
        private List<TelegramAgentResponse> data;
        private String message;
        
        public GetAgentsResponseWrapper(boolean success, List<TelegramAgentResponse> data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public List<TelegramAgentResponse> getData() { return data; }
        public String getMessage() { return message; }
    }
    
    public static class UpdateStatusRequest {
        private AgentTelegram.AgentStatus status;
        
        public AgentTelegram.AgentStatus getStatus() { return status; }
        public void setStatus(AgentTelegram.AgentStatus status) { this.status = status; }
    }
}