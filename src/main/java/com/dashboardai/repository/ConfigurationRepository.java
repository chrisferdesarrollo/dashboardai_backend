package com.dashboardai.repository;

import com.dashboardai.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    
    // Buscar configuración específica de un usuario
    Optional<Configuration> findByUserIdAndKey(Long userId, String key);
    
    // Buscar todas las configuraciones de un usuario
    List<Configuration> findByUserId(Long userId);
    
    // Buscar solo el valor de una configuración específica de un usuario
    @Query("SELECT c.value FROM Configuration c WHERE c.userId = :userId AND c.key = :key")
    Optional<String> findValueByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);
    
    // Verificar si un usuario tiene una configuración específica
    boolean existsByUserIdAndKey(Long userId, String key);
    
    // Buscar configuraciones por clave (útil para obtener configuraciones de todos los usuarios)
    List<Configuration> findByKey(String key);
    
    // Eliminar configuración específica de un usuario
    void deleteByUserIdAndKey(Long userId, String key);
    
    // Eliminar todas las configuraciones de un usuario (útil al eliminar usuario)
    void deleteByUserId(Long userId);
}
