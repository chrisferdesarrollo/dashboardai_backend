package com.dashboardai.repository;

import com.dashboardai.entity.VectorEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * Repositorio para gestionar los embeddings vectorizados
 */
@Repository
public interface VectorEmbeddingRepository extends JpaRepository<VectorEmbedding, BigInteger> {
    
    /**
     * Elimina todos los vectores donde el metadata contiene un agentId específico
     * Usa una query nativa para buscar dentro del campo JSONB
     * 
     * @param agentId El ID del agente a buscar en el metadata
     * @return El número de registros eliminados
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM documents_vectors WHERE metadata->>'agentId' = :agentId", nativeQuery = true)
    int deleteByAgentIdInMetadata(@Param("agentId") String agentId);
    
    /**
     * Cuenta cuántos vectores existen para un agentId específico
     * 
     * @param agentId El ID del agente a buscar en el metadata
     * @return El número de registros encontrados
     */
    @Query(value = "SELECT COUNT(*) FROM documents_vectors WHERE metadata->>'agentId' = :agentId", nativeQuery = true)
    long countByAgentIdInMetadata(@Param("agentId") String agentId);
}
