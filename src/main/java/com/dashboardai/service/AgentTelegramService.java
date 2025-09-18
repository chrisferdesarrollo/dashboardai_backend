package com.dashboardai.service;

import com.dashboardai.dto.request.CreateTelegramAgentRequest;
import com.dashboardai.dto.response.TelegramAgentResponse;
import com.dashboardai.entity.AgentTelegram;
import com.dashboardai.repository.AgentTelegramRepository;
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
public class AgentTelegramService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentTelegramService.class);
    
    @Autowired
    private AgentTelegramRepository agentTelegramRepository;
    
    /**
     * Crear un nuevo agente Telegram
     */
    public TelegramAgentResponse createAgent(CreateTelegramAgentRequest request) {
        try {
            logger.info("Creating new Telegram agent: {}", request.getName());
            
            // Verificar si ya existe un agente con ese nombre para el usuario
            if (request.getUserId() != null && 
                agentTelegramRepository.existsByNameAndUserId(request.getName(), request.getUserId())) {
                throw new RuntimeException("Ya existe un agente Telegram con ese nombre");
            }
            
            // Verificar si ya existe un agente con ese bot name
            if (request.getBotName() != null && 
                agentTelegramRepository.existsByBotName(request.getBotName())) {
                throw new RuntimeException("Ya existe un agente con ese bot name");
            }
            
            // Crear la entidad AgentTelegram
            AgentTelegram agent = new AgentTelegram();
            agent.setName(request.getName());
            agent.setDescription(request.getDescription());
            agent.setPrompt(request.getPrompt());
            agent.setBotName(request.getBotName());
            
            // Procesar platformConfig - asegurar que es un JSON válido
            if (request.getPlatformConfig() != null && !request.getPlatformConfig().isEmpty()) {
                agent.setPlatformConfig(request.getPlatformConfig());
            } else {
                agent.setPlatformConfig("{}"); // JSON vacío por defecto
            }
            
            agent.setUserId(request.getUserId());
            agent.setStatus(AgentTelegram.AgentStatus.active); // Por defecto activo
            
            // Guardar en la base de datos
            AgentTelegram savedAgent = agentTelegramRepository.save(agent);
            
            logger.info("Telegram agent created successfully with ID: {}", savedAgent.getId());
            
            return new TelegramAgentResponse(savedAgent);
            
        } catch (Exception e) {
            logger.error("Error creating Telegram agent: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el agente Telegram: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todos los agentes Telegram
     */
    @Transactional(readOnly = true)
    public List<TelegramAgentResponse> getAllAgents() {
        try {
            List<AgentTelegram> agents = agentTelegramRepository.findAll();
            return agents.stream()
                    .map(TelegramAgentResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching Telegram agents: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los agentes Telegram");
        }
    }
    
    /**
     * Obtener agentes Telegram por usuario
     */
    @Transactional(readOnly = true)
    public List<TelegramAgentResponse> getAgentsByUser(Long userId) {
        try {
            List<AgentTelegram> agents = agentTelegramRepository.findByUserId(userId);
            return agents.stream()
                    .map(TelegramAgentResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching Telegram agents for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los agentes Telegram del usuario");
        }
    }
    
    /**
     * Obtener un agente Telegram por ID
     */
    @Transactional(readOnly = true)
    public TelegramAgentResponse getAgentById(UUID agentId) {
        try {
            Optional<AgentTelegram> agent = agentTelegramRepository.findById(agentId);
            if (agent.isPresent()) {
                return new TelegramAgentResponse(agent.get());
            } else {
                throw new RuntimeException("Agente Telegram no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error fetching Telegram agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el agente Telegram");
        }
    }
    
    /**
     * Obtener un agente Telegram por botName
     */
    @Transactional(readOnly = true)
    public TelegramAgentResponse getAgentByBotName(String botName) {
        try {
            logger.info("Searching for Telegram agent with botName: {}", botName);
            AgentTelegram agent = agentTelegramRepository.findByBotName(botName);
            if (agent != null) {
                logger.info("Found Telegram agent: {} (ID: {})", agent.getName(), agent.getId());
                return new TelegramAgentResponse(agent);
            } else {
                logger.warn("No Telegram agent found with botName: {}", botName);
                throw new RuntimeException("Agente Telegram no encontrado con botName: " + botName);
            }
        } catch (Exception e) {
            logger.error("Error fetching Telegram agent by botName {}: {}", botName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el agente Telegram por botName");
        }
    }
    
    /**
     * Actualizar el estado de un agente Telegram
     */
    public TelegramAgentResponse updateAgentStatus(UUID agentId, AgentTelegram.AgentStatus status) {
        try {
            Optional<AgentTelegram> agentOpt = agentTelegramRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                AgentTelegram agent = agentOpt.get();
                agent.setStatus(status);
                AgentTelegram updatedAgent = agentTelegramRepository.save(agent);
                
                logger.info("Telegram agent {} status updated to {}", agentId, status);
                return new TelegramAgentResponse(updatedAgent);
            } else {
                throw new RuntimeException("Agente Telegram no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error updating Telegram agent status: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el estado del agente Telegram");
        }
    }
    
    /**
     * Eliminar un agente Telegram
     */
    public void deleteAgent(UUID agentId) {
        try {
            if (agentTelegramRepository.existsById(agentId)) {
                agentTelegramRepository.deleteById(agentId);
                logger.info("Telegram agent {} deleted successfully", agentId);
            } else {
                throw new RuntimeException("Agente Telegram no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error deleting Telegram agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el agente Telegram");
        }
    }
    
    /**
     * Incrementar contador de ejecuciones
     */
    public void incrementExecutions(UUID agentId) {
        try {
            Optional<AgentTelegram> agentOpt = agentTelegramRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                AgentTelegram agent = agentOpt.get();
                agent.setTotalExecutions(agent.getTotalExecutions() + 1);
                agent.setLastExecutionAt(LocalDateTime.now());
                agentTelegramRepository.save(agent);
                
                logger.info("Execution count incremented for Telegram agent {}", agentId);
            }
        } catch (Exception e) {
            logger.error("Error incrementing executions for Telegram agent {}: {}", agentId, e.getMessage(), e);
        }
    }
    
    /**
     * Contar agentes Telegram activos
     */
    @Transactional(readOnly = true)
    public Long countActiveAgents() {
        try {
            return agentTelegramRepository.countActiveAgents();
        } catch (Exception e) {
            logger.error("Error counting active Telegram agents: {}", e.getMessage(), e);
            return 0L;
        }
    }
}