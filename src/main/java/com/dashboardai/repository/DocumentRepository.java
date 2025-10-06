package com.dashboardai.repository;

import com.dashboardai.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    // Buscar documentos por agente específico
    List<Document> findByAgentId(UUID agentId);
    
    // Buscar documentos por agente específico con paginación
    Page<Document> findByAgentId(UUID agentId, Pageable pageable);
    
    // Buscar documentos por estado de procesamiento
    List<Document> findByProcessed(Boolean processed);
    
    // Buscar documentos por estado de procesamiento específico
    List<Document> findByProcessingStatus(Document.ProcessingStatus status);
    
    // Buscar documentos por tipo de archivo
    List<Document> findByFileType(String fileType);
    
    // Buscar documentos que contengan tags específicos
    @Query(value = "SELECT * FROM documents d WHERE :tag = ANY(d.tags)", nativeQuery = true)
    List<Document> findByTagsContaining(@Param("tag") String tag);
    
    // Buscar documentos por nombre (case insensitive)
    @Query("SELECT d FROM Document d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Document> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Buscar documentos por nombre o descripción
    @Query("SELECT d FROM Document d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Document> findByNameOrDescriptionContainingIgnoreCase(@Param("query") String query);
    
    // Buscar documentos por agente y procesados
    List<Document> findByAgentIdAndProcessed(UUID agentId, Boolean processed);
    
    // Contar documentos por agente
    long countByAgentId(UUID agentId);
    
    // Contar documentos procesados por agente
    long countByAgentIdAndProcessed(UUID agentId, Boolean processed);
    
    // Obtener documentos ordenados por fecha de subida descendente
    List<Document> findAllByOrderByUploadDateDesc();
    
    // Obtener documentos ordenados por fecha de subida descendente con paginación
    Page<Document> findAllByOrderByUploadDateDesc(Pageable pageable);
    
    // Buscar documentos por usuario
    List<Document> findByUser_Id(Long userId);
    
    // Buscar documentos por usuario y agente
    List<Document> findByUser_IdAndAgentId(Long userId, UUID agentId);
    
    // Buscar documentos por usuario ordenados por fecha
    List<Document> findByUser_IdOrderByUploadDateDesc(Long userId);
    
    // Buscar documentos por usuario con paginación
    Page<Document> findByUser_Id(Long userId, Pageable pageable);
    
    // Buscar documentos por usuario y texto (nombre o descripción)
    @Query("SELECT d FROM Document d WHERE d.user.id = :userId AND " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Document> findByUser_IdAndNameOrDescriptionContainingIgnoreCase(@Param("userId") Long userId, @Param("query") String query);
    
    // Contar documentos por usuario
    long countByUser_Id(Long userId);
}