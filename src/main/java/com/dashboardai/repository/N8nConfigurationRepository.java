package com.dashboardai.repository;

import com.dashboardai.entity.N8nConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface N8nConfigurationRepository extends JpaRepository<N8nConfiguration, Long> {
    
    /**
     * Find the active N8n configuration for a user
     */
    @Query("SELECT n FROM N8nConfiguration n WHERE n.userId = :userId AND n.isActive = true")
    Optional<N8nConfiguration> findActiveByUserId(@Param("userId") Long userId);
    
    /**
     * Find all configurations for a user (active and inactive)
     */
    @Query("SELECT n FROM N8nConfiguration n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    java.util.List<N8nConfiguration> findAllByUserId(@Param("userId") Long userId);
    
    /**
     * Check if user has any N8n configuration
     */
    @Query("SELECT COUNT(n) > 0 FROM N8nConfiguration n WHERE n.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
    
    /**
     * Deactivate all configurations for a user
     */
    @Query("UPDATE N8nConfiguration n SET n.isActive = false WHERE n.userId = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);
}
