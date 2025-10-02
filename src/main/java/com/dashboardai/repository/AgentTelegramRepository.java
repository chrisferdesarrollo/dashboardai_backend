package com.dashboardai.repository;

import com.dashboardai.entity.AgentTelegram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentTelegramRepository extends JpaRepository<AgentTelegram, UUID> {
    
    // Buscar agentes por usuario
    List<AgentTelegram> findByUserId(Long userId);
    
    // Buscar agentes por estado
    List<AgentTelegram> findByStatus(AgentTelegram.AgentStatus status);
    
    // Buscar agentes activos de un usuario
    List<AgentTelegram> findByUserIdAndStatus(Long userId, AgentTelegram.AgentStatus status);
    
    // Buscar agentes por nombre (case insensitive)
    List<AgentTelegram> findByNameContainingIgnoreCase(String name);
    
    // Buscar por bot name
    AgentTelegram findByBotName(String botName);
    
    // Buscar por session name
    AgentTelegram findBySessionName(String sessionName);
    
    // Contar agentes activos
    @Query("SELECT COUNT(a) FROM AgentTelegram a WHERE a.status = 'active'")
    Long countActiveAgents();
    
    // Obtener agentes con más ejecuciones
    @Query("SELECT a FROM AgentTelegram a ORDER BY a.totalExecutions DESC")
    List<AgentTelegram> findTopByExecutions();
    
    // Verificar si existe un agente con ese nombre para un usuario
    boolean existsByNameAndUserId(String name, Long userId);
    
    // Verificar si existe un agente con ese bot name
    boolean existsByBotName(String botName);
    
    // Verificar si existe un agente con ese session name
    boolean existsBySessionName(String sessionName);
}