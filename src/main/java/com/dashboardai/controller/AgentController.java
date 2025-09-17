package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateAgentRequest;
import com.dashboardai.dto.response.AgentResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.Agent;
import com.dashboardai.service.AgentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://topias.app"}, maxAge = 3600)
public class AgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    @Autowired
    private AgentService agentService;
    
    /**
     * Crear un nuevo agente
     */
    @PostMapping
    public ResponseEntity<?> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        try {
            logger.info("POST /api/agents - Creating agent: {}", request.getName());
            
            AgentResponse agentResponse = agentService.createAgent(request);
            
            return ResponseEntity.ok(new CreateAgentResponseWrapper(true, agentResponse, null));
            
        } catch (Exception e) {
            logger.error("Error creating agent: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los agentes
     */
    @GetMapping
    public ResponseEntity<?> getAllAgents() {
        try {
            logger.info("GET /api/agents - Fetching all agents");
            
            List<AgentResponse> agents = agentService.getAllAgents();
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, agents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching agents: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener agentes por usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAgentsByUser(@PathVariable Long userId) {
        try {
            logger.info("GET /api/agents/user/{} - Fetching agents for user", userId);
            
            List<AgentResponse> agents = agentService.getAgentsByUser(userId);
            
            return ResponseEntity.ok(new GetAgentsResponseWrapper(true, agents, null));
            
        } catch (Exception e) {
            logger.error("Error fetching agents for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetAgentsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener un agente por ID
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<?> getAgentById(@PathVariable UUID agentId) {
        try {
            logger.info("GET /api/agents/{} - Fetching agent", agentId);
            
            AgentResponse agent = agentService.getAgentById(agentId);
            
            return ResponseEntity.ok(new CreateAgentResponseWrapper(true, agent, null));
            
        } catch (Exception e) {
            logger.error("Error fetching agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Actualizar el estado de un agente
     */
    @PutMapping("/{agentId}/status")
    public ResponseEntity<?> updateAgentStatus(
            @PathVariable UUID agentId, 
            @RequestBody UpdateStatusRequest request) {
        try {
            logger.info("PUT /api/agents/{}/status - Updating status to {}", agentId, request.getStatus());
            
            AgentResponse agent = agentService.updateAgentStatus(agentId, request.getStatus());
            
            return ResponseEntity.ok(new CreateAgentResponseWrapper(true, agent, null));
            
        } catch (Exception e) {
            logger.error("Error updating agent status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateAgentResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Eliminar un agente
     */
    @DeleteMapping("/{agentId}")
    public ResponseEntity<?> deleteAgent(@PathVariable UUID agentId) {
        try {
            logger.info("DELETE /api/agents/{} - Deleting agent", agentId);
            
            agentService.deleteAgent(agentId);
            
            return ResponseEntity.ok(new MessageResponse("Agente eliminado exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error deleting agent {}: {}", agentId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar el agente: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener estadísticas de agentes
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAgentStats() {
        try {
            logger.info("GET /api/agents/stats - Fetching agent statistics");
            
            AgentService.AgentStatsResponse stats = agentService.getAgentStats();
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error fetching agent stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al obtener estadísticas: " + e.getMessage()));
        }
    }
    
    /**
     * Incrementar contador de ejecuciones
     */
    @PostMapping("/{agentId}/execution")
    public ResponseEntity<?> incrementExecution(@PathVariable UUID agentId) {
        try {
            logger.info("POST /api/agents/{}/execution - Incrementing execution count", agentId);
            
            agentService.incrementExecutions(agentId);
            
            return ResponseEntity.ok(new MessageResponse("Ejecución registrada exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error incrementing execution: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al registrar ejecución: " + e.getMessage()));
        }
    }
    
    // Clases auxiliares para las respuestas
    public static class CreateAgentResponseWrapper {
        private boolean success;
        private AgentResponse data; // Cambiar 'agent' por 'data' para consistencia con el frontend
        private String error;
        
        public CreateAgentResponseWrapper(boolean success, AgentResponse data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public AgentResponse getData() { return data; } // Cambiar getter
        public String getError() { return error; }
    }
    
    public static class GetAgentsResponseWrapper {
        private boolean success;
        private List<AgentResponse> agents;
        private String error;
        
        public GetAgentsResponseWrapper(boolean success, List<AgentResponse> agents, String error) {
            this.success = success;
            this.agents = agents;
            this.error = error;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public List<AgentResponse> getAgents() { return agents; }
        public String getError() { return error; }
    }
    
    public static class UpdateStatusRequest {
        private Agent.AgentStatus status;
        
        public Agent.AgentStatus getStatus() { return status; }
        public void setStatus(Agent.AgentStatus status) { this.status = status; }
    }
}
