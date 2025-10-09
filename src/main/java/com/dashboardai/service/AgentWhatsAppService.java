package com.dashboardai.service;

import com.dashboardai.dto.request.CreateWhatsAppAgentRequest;
import com.dashboardai.dto.request.UpdateAgentRequest;
import com.dashboardai.dto.response.WhatsAppAgentResponse;
import com.dashboardai.entity.AgentWhatsApp;
import com.dashboardai.repository.AgentWhatsAppRepository;
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
public class AgentWhatsAppService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentWhatsAppService.class);
    
    @Autowired
    private AgentWhatsAppRepository agentWhatsAppRepository;
    
    /**
     * Crear un nuevo agente WhatsApp
     */
    public WhatsAppAgentResponse createAgent(CreateWhatsAppAgentRequest request) {
        try {
            logger.info("Creating new WhatsApp agent: {}", request.getName());
            
            // Verificar si ya existe un agente con ese nombre para el usuario
            if (request.getUserId() != null && 
                agentWhatsAppRepository.existsByNameAndUser_Id(request.getName(), request.getUserId())) {
                throw new RuntimeException("Ya existe un agente WhatsApp con ese nombre");
            }
            
            // Verificar si ya existe un agente con ese session name
            if (request.getSessionName() != null && 
                agentWhatsAppRepository.existsBySessionName(request.getSessionName())) {
                throw new RuntimeException("Ya existe un agente con ese session name");
            }
            
            // Crear la entidad AgentWhatsApp
            AgentWhatsApp agent = new AgentWhatsApp();
            agent.setName(request.getName());
            agent.setDescription(request.getDescription());
            agent.setPrompt(request.getPrompt());
            agent.setSessionName(request.getSessionName());
            
            // Procesar platformConfig - asegurar que es un JSON válido
            if (request.getPlatformConfig() != null && !request.getPlatformConfig().isEmpty()) {
                agent.setPlatformConfig(request.getPlatformConfig());
            } else {
                agent.setPlatformConfig("{}"); // JSON vacío por defecto
            }
            
            agent.setUserId(request.getUserId());
            agent.setStatus(AgentWhatsApp.AgentStatus.active); // Por defecto activo
            
            // Guardar en la base de datos
            AgentWhatsApp savedAgent = agentWhatsAppRepository.save(agent);
            
            logger.info("WhatsApp agent created successfully with ID: {}", savedAgent.getId());
            
            return new WhatsAppAgentResponse(savedAgent);
            
        } catch (Exception e) {
            logger.error("Error creating WhatsApp agent: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el agente WhatsApp: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todos los agentes WhatsApp
     */
    @Transactional(readOnly = true)
    public List<WhatsAppAgentResponse> getAllAgents() {
        try {
            List<AgentWhatsApp> agents = agentWhatsAppRepository.findAll();
            return agents.stream()
                    .map(WhatsAppAgentResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agents: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los agentes WhatsApp");
        }
    }
    
    /**
     * Obtener agentes WhatsApp por usuario
     */
    @Transactional(readOnly = true)
    public List<WhatsAppAgentResponse> getAgentsByUser(Long userId) {
        try {
            List<AgentWhatsApp> agents = agentWhatsAppRepository.findByUser_Id(userId);
            return agents.stream()
                    .map(WhatsAppAgentResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agents for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los agentes WhatsApp del usuario");
        }
    }
    
    /**
     * Obtener un agente WhatsApp por ID
     */
    @Transactional(readOnly = true)
    public WhatsAppAgentResponse getAgentById(UUID agentId) {
        try {
            Optional<AgentWhatsApp> agent = agentWhatsAppRepository.findById(agentId);
            if (agent.isPresent()) {
                return new WhatsAppAgentResponse(agent.get());
            } else {
                throw new RuntimeException("Agente WhatsApp no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el agente WhatsApp");
        }
    }
    
    /**
     * Obtener un agente WhatsApp por sessionName
     */
    @Transactional(readOnly = true)
    public WhatsAppAgentResponse getAgentBySessionName(String sessionName) {
        try {
            logger.info("Searching for WhatsApp agent with sessionName: {}", sessionName);
            AgentWhatsApp agent = agentWhatsAppRepository.findBySessionName(sessionName);
            if (agent != null) {
                logger.info("Found WhatsApp agent: {} (ID: {})", agent.getName(), agent.getId());
                return new WhatsAppAgentResponse(agent);
            } else {
                logger.warn("No WhatsApp agent found with sessionName: {}", sessionName);
                throw new RuntimeException("Agente WhatsApp no encontrado con sessionName: " + sessionName);
            }
        } catch (Exception e) {
            logger.error("Error fetching WhatsApp agent by sessionName {}: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el agente WhatsApp por sessionName");
        }
    }
    
    /**
     * Actualizar el estado de un agente WhatsApp
     */
    public WhatsAppAgentResponse updateAgentStatus(UUID agentId, AgentWhatsApp.AgentStatus status) {
        try {
            Optional<AgentWhatsApp> agentOpt = agentWhatsAppRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                AgentWhatsApp agent = agentOpt.get();
                agent.setStatus(status);
                AgentWhatsApp updatedAgent = agentWhatsAppRepository.save(agent);
                
                logger.info("WhatsApp agent {} status updated to {}", agentId, status);
                return new WhatsAppAgentResponse(updatedAgent);
            } else {
                throw new RuntimeException("Agente WhatsApp no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error updating WhatsApp agent status: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el estado del agente WhatsApp");
        }
    }
    
    /**
     * Actualizar un agente WhatsApp
     */
    public WhatsAppAgentResponse updateAgent(UUID agentId, UpdateAgentRequest request) {
        try {
            Optional<AgentWhatsApp> agentOpt = agentWhatsAppRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                AgentWhatsApp agent = agentOpt.get();
                
                // Actualizar solo los campos que no son nulos en la request
                if (request.getName() != null && !request.getName().trim().isEmpty()) {
                    agent.setName(request.getName().trim());
                }
                
                if (request.getDescription() != null) {
                    agent.setDescription(request.getDescription().trim());
                }
                
                if (request.getPrompt() != null) {
                    agent.setPrompt(request.getPrompt());
                }
                
                if (request.getSessionName() != null && !request.getSessionName().trim().isEmpty()) {
                    agent.setSessionName(request.getSessionName().trim());
                }
                
                if (request.getPlatformConfig() != null) {
                    agent.setPlatformConfig(request.getPlatformConfig());
                }
                
                AgentWhatsApp updatedAgent = agentWhatsAppRepository.save(agent);
                
                logger.info("WhatsApp agent {} updated successfully", agentId);
                return new WhatsAppAgentResponse(updatedAgent);
            } else {
                throw new RuntimeException("Agente WhatsApp no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error updating WhatsApp agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el agente WhatsApp: " + e.getMessage());
        }
    }

    /**
     * Eliminar un agente WhatsApp
     */
    public void deleteAgent(UUID agentId) {
        try {
            if (agentWhatsAppRepository.existsById(agentId)) {
                agentWhatsAppRepository.deleteById(agentId);
                logger.info("WhatsApp agent {} deleted successfully", agentId);
            } else {
                throw new RuntimeException("Agente WhatsApp no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error deleting WhatsApp agent {}: {}", agentId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el agente WhatsApp");
        }
    }
    
    /**
     * Incrementar contador de ejecuciones
     */
    public void incrementExecutions(UUID agentId) {
        try {
            Optional<AgentWhatsApp> agentOpt = agentWhatsAppRepository.findById(agentId);
            if (agentOpt.isPresent()) {
                AgentWhatsApp agent = agentOpt.get();
                agent.setTotalExecutions(agent.getTotalExecutions() + 1);
                agent.setLastExecutionAt(LocalDateTime.now());
                agentWhatsAppRepository.save(agent);
                
                logger.info("Execution count incremented for WhatsApp agent {}", agentId);
            }
        } catch (Exception e) {
            logger.error("Error incrementing executions for WhatsApp agent {}: {}", agentId, e.getMessage(), e);
        }
    }
    
    /**
     * Contar agentes WhatsApp activos
     */
    @Transactional(readOnly = true)
    public Long countActiveAgents() {
        try {
            return agentWhatsAppRepository.countActiveAgents();
        } catch (Exception e) {
            logger.error("Error counting active WhatsApp agents: {}", e.getMessage(), e);
            return 0L;
        }
    }
}