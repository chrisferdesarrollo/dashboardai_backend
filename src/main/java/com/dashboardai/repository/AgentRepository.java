package com.dashboardai.repository;

import com.dashboardai.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {
    
    // Buscar agentes por usuario
    List<Agent> findByUserId(Long userId);
    
    // Buscar agentes por plataforma
    List<Agent> findByPlatform(Agent.Platform platform);
    
    // Buscar agentes por estado
    List<Agent> findByStatus(Agent.AgentStatus status);
    
    // Buscar agentes activos de un usuario
    List<Agent> findByUserIdAndStatus(Long userId, Agent.AgentStatus status);
    
    // Buscar agentes por nombre (case insensitive)
    List<Agent> findByNameContainingIgnoreCase(String name);
    
    // Buscar por workflow ID
    Agent findByWorkflowId(String workflowId);
    
    // Contar agentes por plataforma
    @Query("SELECT COUNT(a) FROM Agent a WHERE a.platform = :platform")
    Long countByPlatform(@Param("platform") Agent.Platform platform);
    
    // Contar agentes activos
    @Query("SELECT COUNT(a) FROM Agent a WHERE a.status = 'ACTIVE'")
    Long countActiveAgents();
    
    // Obtener agentes con más ejecuciones
    @Query("SELECT a FROM Agent a ORDER BY a.totalExecutions DESC")
    List<Agent> findTopByExecutions();
    
    // Verificar si existe un agente con ese nombre para un usuario
    boolean existsByNameAndUserId(String name, Long userId);
}
