package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateAgentRequest;
import com.dashboardai.dto.request.CreateWhatsAppAgentRequest;
import com.dashboardai.dto.request.CreateTelegramAgentRequest;
import com.dashboardai.dto.request.UpdateAgentRequest;
import com.dashboardai.dto.response.UnifiedAgentResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.model.AgentPlatform;
import com.dashboardai.entity.AgentWhatsApp;
import com.dashboardai.entity.AgentTelegram;
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
@RequestMapping("/api/agents")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class AgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    @Autowired
    private AgentWhatsAppService agentWhatsAppService;
    
    @Autowired
    private AgentTelegramService agentTelegramService;
    
    /**
     * Crear un nuevo agente (WhatsApp o Telegram basado en la plataforma)
     * Mantiene compatibilidad con frontend existente
     */
    @PostMapping
    public ResponseEntity<?> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        try {
            logger.info("POST /api/agents - Creating agent: {} for platform: {}", request.getName(), request.getPlatform());
            
            if (request.getPlatform() == AgentPlatform.whatsapp) {
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
                
            } else if (request.getPlatform() == AgentPlatform.telegram) {
                // Convertir a CreateTelegramAgentRequest
                CreateTelegramAgentRequest telegramRequest = new CreateTelegramAgentRequest();
                telegramRequest.setName(request.getName());
                telegramRequest.setDescription(request.getDescription());
                telegramRequest.setPrompt(request.getPrompt());
                telegramRequest.setSessionName(request.getSessionName()); // ✅ CORRECTO: mapear sessionName correctamente
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
            logger.error("Error creating agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los agentes (WhatsApp y Telegram combinados)
     */
    @GetMapping
    public ResponseEntity<?> getAllAgents() {
        try {
            logger.info("GET /api/agents - Fetching all agents");
            
            List<UnifiedAgentResponse> allAgents = new ArrayList<>();
            
            // Obtener agentes de WhatsApp
            var whatsappAgents = agentWhatsAppService.getAllAgents();
            whatsappAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "whatsapp")));
            
            // Obtener agentes de Telegram
            var telegramAgents = agentTelegramService.getAllAgents();
            telegramAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "telegram")));
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, allAgents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching agents: {}", e.getMessage(), e);
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
            logger.info("GET /api/agents/user/{} - Fetching agents for user", userId);
            
            List<UnifiedAgentResponse> allAgents = new ArrayList<>();
            
            // Obtener agentes WhatsApp del usuario
            var whatsappAgents = agentWhatsAppService.getAgentsByUser(userId);
            whatsappAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "whatsapp")));
            
            // Obtener agentes Telegram del usuario
            var telegramAgents = agentTelegramService.getAgentsByUser(userId);
            telegramAgents.forEach(agent -> allAgents.add(new UnifiedAgentResponse(agent, "telegram")));
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, allAgents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching agents for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener un agente por sessionName (busca en ambas tablas)
     */
    @GetMapping("/session/{sessionName}")
    public ResponseEntity<?> getAgentBySessionName(@PathVariable String sessionName) {
        try {
            logger.info("GET /api/agents/session/{} - Fetching agent by sessionName", sessionName);
            
            // Primero buscar en WhatsApp agents
            try {
                var whatsappAgent = agentWhatsAppService.getAgentBySessionName(sessionName);
                return ResponseEntity.ok(new CreateAgentResponseWrapper(true, new UnifiedAgentResponse(whatsappAgent, "whatsapp"), null));
            } catch (Exception e) {
                logger.debug("Agent not found in WhatsApp table, trying Telegram");
            }
            
            // Si no se encuentra en WhatsApp, buscar en Telegram usando botName
            try {
                var telegramAgent = agentTelegramService.getAgentByBotName(sessionName);
                return ResponseEntity.ok(new CreateAgentResponseWrapper(true, new UnifiedAgentResponse(telegramAgent, "telegram"), null));
            } catch (Exception e) {
                logger.debug("Agent not found in Telegram table either");
            }
            
            throw new RuntimeException("Agente no encontrado con sessionName: " + sessionName);
            
        } catch (Exception e) {
            logger.error("Error fetching agent by sessionName {}: {}", sessionName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener estadísticas combinadas de agentes
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAgentStats() {
        try {
            logger.info("GET /api/agents/stats - Fetching agent statistics");
            
            Long whatsappActive = agentWhatsAppService.countActiveAgents();
            Long telegramActive = agentTelegramService.countActiveAgents();
            
            var stats = new CombinedAgentStats(whatsappActive, telegramActive);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error fetching agent stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al obtener estadísticas: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar un agente por ID
     * Busca en ambas tablas (WhatsApp y Telegram) y elimina del lugar correcto
     */
    @DeleteMapping("/{agentId}")
    public ResponseEntity<?> deleteAgent(@PathVariable UUID agentId) {
        try {
            logger.info("DELETE /api/agents/{} - Deleting agent", agentId);
            
            // Primero intentar encontrar en WhatsApp
            try {
                var whatsappAgent = agentWhatsAppService.getAgentById(agentId);
                if (whatsappAgent != null) {
                    agentWhatsAppService.deleteAgent(agentId);
                    logger.info("WhatsApp agent {} deleted successfully", agentId);
                    return ResponseEntity.ok(new MessageResponse("Agente WhatsApp eliminado correctamente"));
                }
            } catch (RuntimeException e) {
                // El agente no existe en WhatsApp, continuar buscando en Telegram
                logger.debug("Agent not found in WhatsApp table: {}", e.getMessage());
            }
            
            // Luego intentar encontrar en Telegram
            try {
                var telegramAgent = agentTelegramService.getAgentById(agentId);
                if (telegramAgent != null) {
                    agentTelegramService.deleteAgent(agentId);
                    logger.info("Telegram agent {} deleted successfully", agentId);
                    return ResponseEntity.ok(new MessageResponse("Agente Telegram eliminado correctamente"));
                }
            } catch (RuntimeException e) {
                // El agente no existe en Telegram tampoco
                logger.debug("Agent not found in Telegram table: {}", e.getMessage());
            }
            
            // Si llegamos aquí, el agente no existe en ninguna tabla
            logger.warn("Agent {} not found in any table", agentId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error deleting agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar agente: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar un agente (WhatsApp o Telegram)
     */
    @PutMapping("/{agentId}")
    public ResponseEntity<?> updateAgent(@PathVariable UUID agentId, @Valid @RequestBody UpdateAgentRequest request) {
        try {
            logger.info("PUT /api/agents/{} - Updating agent", agentId);
            
            // Primero intentar encontrar el agente en WhatsApp
            try {
                var whatsappAgent = agentWhatsAppService.getAgentById(agentId);
                if (whatsappAgent != null) {
                    var updatedAgent = agentWhatsAppService.updateAgent(agentId, request);
                    logger.info("WhatsApp agent {} updated successfully", agentId);
                    return ResponseEntity.ok(new UnifiedAgentResponse(updatedAgent, "whatsapp"));
                }
            } catch (RuntimeException e) {
                // El agente no existe en WhatsApp, intentar Telegram
                logger.debug("Agent not found in WhatsApp table: {}", e.getMessage());
            }
            
            // Si no es WhatsApp o no se encontró, intentar con Telegram
            try {
                var telegramAgent = agentTelegramService.getAgentById(agentId);
                if (telegramAgent != null) {
                    var updatedAgent = agentTelegramService.updateAgent(agentId, request);
                    logger.info("Telegram agent {} updated successfully", agentId);
                    return ResponseEntity.ok(new UnifiedAgentResponse(updatedAgent, "telegram"));
                }
            } catch (RuntimeException e) {
                // El agente no existe en Telegram tampoco
                logger.debug("Agent not found in Telegram table: {}", e.getMessage());
            }
            
            // Si llegamos aquí, el agente no existe en ninguna tabla
            logger.warn("Agent {} not found in any table", agentId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error updating agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar agente: " + e.getMessage()));
        }
    }

    /**
     * Actualizar el estado de un agente (WhatsApp o Telegram)
     * Mantiene compatibilidad con frontend existente
     */
    @PutMapping("/{agentId}/status")
    public ResponseEntity<?> updateAgentStatus(@PathVariable UUID agentId, @Valid @RequestBody UpdateStatusRequest request) {
        try {
            logger.info("PUT /api/agents/{}/status - Updating agent status", agentId);
            
            // Primero intentar encontrar el agente en WhatsApp
            try {
                var whatsappAgent = agentWhatsAppService.getAgentById(agentId);
                if (whatsappAgent != null && request.getWhatsappStatus() != null) {
                    var updatedAgent = agentWhatsAppService.updateAgentStatus(agentId, request.getWhatsappStatus());
                    logger.info("WhatsApp agent {} status updated to {}", agentId, request.getWhatsappStatus());
                    return ResponseEntity.ok(new UnifiedAgentResponse(updatedAgent, "whatsapp"));
                }
            } catch (RuntimeException e) {
                // El agente no existe en WhatsApp, intentar Telegram
                logger.debug("Agent not found in WhatsApp table: {}", e.getMessage());
            }
            
            // Si no es WhatsApp o no se encontró, intentar con Telegram
            try {
                var telegramAgent = agentTelegramService.getAgentById(agentId);
                if (telegramAgent != null && request.getTelegramStatus() != null) {
                    // Para Telegram, por ahora solo retornamos el agente sin cambiar estado
                    // TODO: Implementar updateAgentStatus en AgentTelegramService
                    logger.info("Telegram agent {} found, but status update not implemented yet", agentId);
                    return ResponseEntity.ok(new UnifiedAgentResponse(telegramAgent, "telegram"));
                }
            } catch (RuntimeException e) {
                // El agente no existe en Telegram tampoco
                logger.debug("Agent not found in Telegram table: {}", e.getMessage());
            }
            
            // Si llegamos aquí, el agente no existe en ninguna tabla
            logger.warn("Agent {} not found in any table", agentId);
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error updating agent status {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al actualizar estado del agente: " + e.getMessage()));
        }
    }

    // Clases auxiliares para las respuestas
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
        private List<UnifiedAgentResponse> agents;
        private String message;
        
        public GetAgentsResponseWrapper(boolean success, List<UnifiedAgentResponse> agents, String message) {
            this.success = success;
            this.agents = agents;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public List<UnifiedAgentResponse> getAgents() { return agents; }
        public String getMessage() { return message; }
    }
    
    public static class UpdateStatusRequest {
        private AgentWhatsApp.AgentStatus whatsappStatus;
        private AgentTelegram.AgentStatus telegramStatus;
        
        public AgentWhatsApp.AgentStatus getWhatsappStatus() { return whatsappStatus; }
        public void setWhatsappStatus(AgentWhatsApp.AgentStatus status) { this.whatsappStatus = status; }
        
        public AgentTelegram.AgentStatus getTelegramStatus() { return telegramStatus; }
        public void setTelegramStatus(AgentTelegram.AgentStatus status) { this.telegramStatus = status; }
    }
    
    public static class CombinedAgentStats {
        private long totalAgents;
        private long activeAgents;
        private long whatsappAgents;
        private long telegramAgents;
        
        public CombinedAgentStats(Long whatsappActive, Long telegramActive) {
            this.whatsappAgents = whatsappActive != null ? whatsappActive : 0;
            this.telegramAgents = telegramActive != null ? telegramActive : 0;
            this.activeAgents = this.whatsappAgents + this.telegramAgents;
            this.totalAgents = this.activeAgents; // Por simplicidad, asumimos que activos = total
        }
        
        // Getters
        public long getTotalAgents() { return totalAgents; }
        public long getActiveAgents() { return activeAgents; }
        public long getWhatsappAgents() { return whatsappAgents; }
        public long getTelegramAgents() { return telegramAgents; }
    }
}
