package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateAgentRequest;
import com.dashboardai.dto.request.CreateWhatsAppAgentRequest;
import com.dashboardai.dto.request.CreateTelegramAgentRequest;
import com.dashboardai.dto.response.UnifiedAgentResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.Agent;
import com.dashboardai.service.AgentWhatsAppService;
import com.dashboardai.service.AgentTelegramService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agents/unified")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class UnifiedAgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(UnifiedAgentController.class);
    
    @Autowired
    private AgentWhatsAppService agentWhatsAppService;
    
    @Autowired
    private AgentTelegramService agentTelegramService;
    
    /**
     * Crear un nuevo agente (WhatsApp o Telegram basado en la plataforma)
     */
    @PostMapping
    public ResponseEntity<?> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        try {
            logger.info("POST /api/agents/unified - Creating agent: {} for platform: {}", request.getName(), request.getPlatform());
            
            if (request.getPlatform() == Agent.Platform.whatsapp) {
                // Convertir a CreateWhatsAppAgentRequest
                CreateWhatsAppAgentRequest whatsappRequest = new CreateWhatsAppAgentRequest();
                whatsappRequest.setName(request.getName());
                whatsappRequest.setDescription(request.getDescription());
                whatsappRequest.setPrompt(request.getPrompt());
                whatsappRequest.setSessionName(request.getSessionName());
                whatsappRequest.setPlatformConfig(request.getPlatformConfig());
                whatsappRequest.setUserId(request.getUserId());
                whatsappRequest.setWorkflowIds(request.getWorkflowIds());
                whatsappRequest.setPrimaryWorkflowId(request.getPrimaryWorkflowId());
                
                var response = agentWhatsAppService.createAgent(whatsappRequest);
                return ResponseEntity.ok(new CreateAgentResponseWrapper(true, new UnifiedAgentResponse(response, "whatsapp"), null));
                
            } else if (request.getPlatform() == Agent.Platform.telegram) {
                // Convertir a CreateTelegramAgentRequest
                CreateTelegramAgentRequest telegramRequest = new CreateTelegramAgentRequest();
                telegramRequest.setName(request.getName());
                telegramRequest.setDescription(request.getDescription());
                telegramRequest.setPrompt(request.getPrompt());
                telegramRequest.setBotName(request.getSessionName()); // sessionName se mapea a botName para Telegram
                telegramRequest.setPlatformConfig(request.getPlatformConfig());
                telegramRequest.setUserId(request.getUserId());
                telegramRequest.setWorkflowIds(request.getWorkflowIds());
                telegramRequest.setPrimaryWorkflowId(request.getPrimaryWorkflowId());
                
                var response = agentTelegramService.createAgent(telegramRequest);
                return ResponseEntity.ok(new CreateAgentResponseWrapper(true, new UnifiedAgentResponse(response, "telegram"), null));
                
            } else {
                throw new RuntimeException("Plataforma no soportada: " + request.getPlatform());
            }
            
        } catch (Exception e) {
            logger.error("Error creating unified agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los agentes (WhatsApp y Telegram juntos)
     */
    @GetMapping
    public ResponseEntity<?> getAllAgents() {
        try {
            logger.info("GET /api/agents/unified - Fetching all agents");
            
            List<UnifiedAgentResponse> allAgents = new ArrayList<>();
            
            // Obtener agentes de WhatsApp
            var whatsappAgents = agentWhatsAppService.getAllAgents();
            whatsappAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "whatsapp")));
            
            // Obtener agentes de Telegram
            var telegramAgents = agentTelegramService.getAllAgents();
            telegramAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "telegram")));
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, allAgents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching all unified agents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener agentes por usuario (WhatsApp y Telegram)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAgentsByUser(@PathVariable Long userId) {
        try {
            logger.info("GET /api/agents/unified/user/{} - Fetching agents for user", userId);
            
            List<UnifiedAgentResponse> allAgents = new ArrayList<>();
            
            // Obtener agentes WhatsApp del usuario
            var whatsappAgents = agentWhatsAppService.getAgentsByUser(userId);
            whatsappAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "whatsapp")));
            
            // Obtener agentes Telegram del usuario
            var telegramAgents = agentTelegramService.getAgentsByUser(userId);
            telegramAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "telegram")));
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, allAgents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching unified agents for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    // Clases internas para wrappers de respuesta
    public static class CreateAgentResponseWrapper {
        private boolean success;
        private UnifiedAgentResponse data;
        private String message;
        
        public CreateAgentResponseWrapper(boolean success, UnifiedAgentResponse data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public UnifiedAgentResponse getData() { return data; }
        public String getMessage() { return message; }
    }
    
    public static class GetAgentsResponseWrapper {
        private boolean success;
        private List<UnifiedAgentResponse> data;
        private String message;
        
        public GetAgentsResponseWrapper(boolean success, List<UnifiedAgentResponse> data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public List<UnifiedAgentResponse> getData() { return data; }
        public String getMessage() { return message; }
    }
}