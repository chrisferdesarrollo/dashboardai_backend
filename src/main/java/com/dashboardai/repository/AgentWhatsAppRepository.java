package com.dashboardai.repository;

import com.dashboardai.entity.AgentWhatsApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentWhatsAppRepository extends JpaRepository<AgentWhatsApp, UUID> {
    
    // Buscar agentes por usuario
    List<AgentWhatsApp> findByUser_Id(Long userId);
    
    // Buscar agentes por estado
    List<AgentWhatsApp> findByStatus(AgentWhatsApp.AgentStatus status);
    
    // Buscar agentes activos de un usuario
    List<AgentWhatsApp> findByUser_IdAndStatus(Long userId, AgentWhatsApp.AgentStatus status);
    
    // Buscar agentes por nombre (case insensitive)
    List<AgentWhatsApp> findByNameContainingIgnoreCase(String name);
    
    // Buscar por session name
    AgentWhatsApp findBySessionName(String sessionName);
    
    // Contar agentes activos
    @Query("SELECT COUNT(a) FROM AgentWhatsApp a WHERE a.status = 'active'")
    Long countActiveAgents();
    
    // Obtener agentes con más ejecuciones
    @Query("SELECT a FROM AgentWhatsApp a ORDER BY a.totalExecutions DESC")
    List<AgentWhatsApp> findTopByExecutions();
    
    // Verificar si existe un agente con ese nombre para un usuario
    boolean existsByNameAndUser_Id(String name, Long userId);
    
    // Verificar si existe un agente con ese session name
    boolean existsBySessionName(String sessionName);
}