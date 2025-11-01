package com.dashboardai.repository;

import com.dashboardai.entity.AgentTemplate;
import com.dashboardai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentTemplateRepository extends JpaRepository<AgentTemplate, UUID> {
    
    // Métodos por usuario
    List<AgentTemplate> findByUser(User user);
    
    List<AgentTemplate> findByUserAndIsActive(User user, Boolean isActive);
    
    List<AgentTemplate> findByUserOrderByCreatedAtDesc(User user);
    
    // Métodos para ADMIN (todos los usuarios)
    List<AgentTemplate> findAllByOrderByCreatedAtDesc();
    
    List<AgentTemplate> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
}
