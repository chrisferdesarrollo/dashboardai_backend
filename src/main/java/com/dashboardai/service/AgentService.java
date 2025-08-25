package com.dashboardai.service;

import com.dashboardai.dto.request.CreateAgentRequest;
import com.dashboardai.dto.response.AgentResponse;
import com.dashboardai.entity.Agent;
import com.dashboardai.repository.AgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
    
    @Autowired
    private AgentRepository agentRepository;
    
    /**
     * Crear un nuevo agente
     */
    public AgentResponse createAgent(CreateAgentRequest request) {
        try {
            logger.info("Creating new agent: {}", request.getName());
            
            // Verificar si ya existe un agente con ese nombre para el usuario
            if (request.getUserId() != null && 
                agentRepository.existsByNameAndUserId(request.getName(), request.getUserId())) {
                throw new RuntimeException("Ya existe un agente con ese nombre");
            }
            
            // Crear la entidad Agent
            Agent agent = new Agent();
            agent.setName(request.getName());
            agent.setDescription(request.getDescription());
            agent.setPlatform(request.getPlatform());
            agent.setPrompt(request.getPrompt());
            agent.setWorkflowId(request.getWorkflowId());
            agent.setPlatformConfig(request.getPlatformConfig());
            agent.setUserId(request.getUserId());
            agent.setStatus(Agent.AgentStatus.active); // Por defecto activo
            
            // Guardar en la base de datos
            Agent savedAgent = agentRepository.save(agent);
            
            logger.info("Agent created successfully with ID: {}", savedAgent.getId());
            
            return new AgentResponse(savedAgent);
            
        } catch (Exception e) {
            logger.error("Error creating agent: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el agente: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todos los agentes
     */
    @Transactional(readOnly = true)
    public List<AgentResponse> getAllAgents() {
        try {
            List<Agent> agents = agentRepository.findAll();
            return agents.stream()
                    .map(AgentResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching agents: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los agentes");
        }
    }
    
    /**
     * Obtener agentes por usuario
     */
    @Transactional(readOnly = true)
    public List<AgentResponse> getAgentsByUser(Long userId) {
        try {
            List<Agent> agents = agentRepository.findByUserId(userId);
            return agents.stream()
                    .map(AgentResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching agents for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los agentes del usuario");
        }
    }
    
    /**
     * Obtener un agente por ID
     */
    @Transactional(readOnly = true)
    public AgentResponse getAgentById(UUID agentId) {
        try {
            Optional<Agent> agent = agentRepository.findById(agentId);
            if (agent.isPresent()) {
                return new AgentResponse(agent.get());
            } else {
                throw new RuntimeException("Agente no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error fetching agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el agente");
        }
    }
    
    /**
     * Actualizar el estado de un agente
     */
    public AgentResponse updateAgentStatus(UUID agentId, Agent.AgentStatus status) {
        try {
            Optional<Agent> agentOpt = agentRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                Agent agent = agentOpt.get();
                agent.setStatus(status);
                Agent updatedAgent = agentRepository.save(agent);
                
                logger.info("Agent {} status updated to {}", agentId, status);
                return new AgentResponse(updatedAgent);
            } else {
                throw new RuntimeException("Agente no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error updating agent status: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el estado del agente");
        }
    }
    
    /**
     * Eliminar un agente
     */
    public void deleteAgent(UUID agentId) {
        try {
            if (agentRepository.existsById(agentId)) {
                agentRepository.deleteById(agentId);
                logger.info("Agent {} deleted successfully", agentId);
            } else {
                throw new RuntimeException("Agente no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error deleting agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el agente");
        }
    }
    
    /**
     * Obtener estadísticas de agentes
     */
    @Transactional(readOnly = true)
    public AgentStatsResponse getAgentStats() {
        try {
            long totalAgents = agentRepository.count();
            long activeAgents = agentRepository.countActiveAgents();
            long whatsappAgents = agentRepository.countByPlatform(Agent.Platform.WHATSAPP);
            long telegramAgents = agentRepository.countByPlatform(Agent.Platform.TELEGRAM);
            
            return new AgentStatsResponse(totalAgents, activeAgents, whatsappAgents, telegramAgents);
        } catch (Exception e) {
            logger.error("Error fetching agent stats: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener estadísticas");
        }
    }
    
    /**
     * Incrementar contador de ejecuciones
     */
    public void incrementExecutions(UUID agentId) {
        try {
            Optional<Agent> agentOpt = agentRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                Agent agent = agentOpt.get();
                agent.setTotalExecutions(agent.getTotalExecutions() + 1);
                agent.setLastExecutionAt(LocalDateTime.now());
                agentRepository.save(agent);
                
                logger.info("Execution count incremented for agent {}", agentId);
            }
        } catch (Exception e) {
            logger.error("Error incrementing executions for agent {}: {}", agentId, e.getMessage(), e);
        }
    }
    
    // Clase interna para estadísticas
    public static class AgentStatsResponse {
        private long totalAgents;
        private long activeAgents;
        private long whatsappAgents;
        private long telegramAgents;
        
        public AgentStatsResponse(long totalAgents, long activeAgents, long whatsappAgents, long telegramAgents) {
            this.totalAgents = totalAgents;
            this.activeAgents = activeAgents;
            this.whatsappAgents = whatsappAgents;
            this.telegramAgents = telegramAgents;
        }
        
        // Getters
        public long getTotalAgents() { return totalAgents; }
        public long getActiveAgents() { return activeAgents; }
        public long getWhatsappAgents() { return whatsappAgents; }
        public long getTelegramAgents() { return telegramAgents; }
    }
}
