package com.dashboardai.repository;

import com.dashboardai.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    
    // Buscar workflows por usuario
    List<Workflow> findByUserId(Long userId);
    
    // Buscar workflows por usuario y no eliminados
    @Query("SELECT w FROM Workflow w WHERE w.userId = :userId AND w.isDeleted = false ORDER BY w.createdAt DESC")
    List<Workflow> findByUserIdAndNotDeleted(@Param("userId") Long userId);
    
    // Buscar por ID de n8n
    Optional<Workflow> findByN8nWorkflowId(String n8nWorkflowId);
    
    // Buscar por ID de n8n y usuario
    Optional<Workflow> findByN8nWorkflowIdAndUserId(String n8nWorkflowId, Long userId);
    
    // Buscar workflows activos por usuario
    List<Workflow> findByUserIdAndActiveTrueAndIsDeletedFalse(Long userId);
    
    // Buscar todos los workflows no eliminados (solo para admin)
    @Query("SELECT w FROM Workflow w WHERE w.isDeleted = false ORDER BY w.createdAt DESC")
    List<Workflow> findAllNotDeleted();
    
    // Buscar por nombre y usuario (case insensitive)
    @Query("SELECT w FROM Workflow w WHERE w.userId = :userId AND LOWER(w.name) LIKE LOWER(CONCAT('%', :name, '%')) AND w.isDeleted = false")
    List<Workflow> findByUserIdAndNameContainingIgnoreCase(@Param("userId") Long userId, @Param("name") String name);
    
    // Verificar si existe por ID de n8n y usuario
    boolean existsByN8nWorkflowIdAndUserId(String n8nWorkflowId, Long userId);
    
    // Verificar si existe por nombre y usuario
    boolean existsByNameAndUserId(String name, Long userId);
    
    // Buscar workflows por estado activo y usuario
    @Query("SELECT w FROM Workflow w WHERE w.userId = :userId AND w.active = :active AND w.isDeleted = false ORDER BY w.createdAt DESC")
    List<Workflow> findByUserIdAndActiveAndNotDeleted(@Param("userId") Long userId, @Param("active") Boolean active);
    
    // Contar workflows por usuario
    @Query("SELECT COUNT(w) FROM Workflow w WHERE w.userId = :userId AND w.isDeleted = false")
    Long countByUserIdAndNotDeleted(@Param("userId") Long userId);
    
    // Contar workflows activos por usuario
    @Query("SELECT COUNT(w) FROM Workflow w WHERE w.userId = :userId AND w.active = true AND w.isDeleted = false")
    Long countActiveByUserId(@Param("userId") Long userId);
    
    // Obtener workflows más recientes por usuario
    @Query("SELECT w FROM Workflow w WHERE w.userId = :userId AND w.isDeleted = false ORDER BY w.createdAt DESC")
    List<Workflow> findRecentByUserId(@Param("userId") Long userId);
}
