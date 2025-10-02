package com.dashboardai.repository;

import com.dashboardai.entity.ConversationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationLogRepository extends JpaRepository<ConversationLog, UUID> {
    
    // Buscar logs por session name
    List<ConversationLog> findBySessionName(String sessionName);
    
    // Buscar logs por session name ordenados por fecha de creación
    List<ConversationLog> findBySessionNameOrderByCreatedAtAsc(String sessionName);
    
    // Buscar logs por nombre de usuario
    List<ConversationLog> findByUserName(String userName);
    
    // Buscar logs por nombre de usuario en un rango de fechas
    List<ConversationLog> findByUserNameAndCreatedAtBetween(String userName, ZonedDateTime startDate, ZonedDateTime endDate);
    
    // Buscar logs por session name en un rango de fechas
    List<ConversationLog> findBySessionNameAndCreatedAtBetween(String sessionName, ZonedDateTime startDate, ZonedDateTime endDate);
    
    // Obtener los últimos logs de una sesión
    @Query("SELECT cl FROM ConversationLog cl WHERE cl.sessionName = :sessionName ORDER BY cl.createdAt DESC")
    List<ConversationLog> findLatestBySessionName(@Param("sessionName") String sessionName);
    
    // Obtener logs recientes (últimas 24 horas)
    @Query("SELECT cl FROM ConversationLog cl WHERE cl.createdAt >= :since ORDER BY cl.createdAt DESC")
    List<ConversationLog> findRecentLogs(@Param("since") ZonedDateTime since);
    
    // Contar mensajes por sesión
    @Query("SELECT COUNT(cl) FROM ConversationLog cl WHERE cl.sessionName = :sessionName")
    Long countBySessionName(@Param("sessionName") String sessionName);
    
    // Contar mensajes por usuario
    @Query("SELECT COUNT(cl) FROM ConversationLog cl WHERE cl.userName = :userName")
    Long countByUserName(@Param("userName") String userName);
    
    // Obtener sesiones únicas de un usuario
    @Query("SELECT DISTINCT cl.sessionName FROM ConversationLog cl WHERE cl.userName = :userName ORDER BY cl.sessionName")
    List<String> findDistinctSessionNamesByUserName(@Param("userName") String userName);
    
    // Buscar logs que contengan texto específico en el mensaje del usuario
    @Query("SELECT cl FROM ConversationLog cl WHERE LOWER(cl.userMessage) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY cl.createdAt DESC")
    List<ConversationLog> findByUserMessageContainingIgnoreCase(@Param("searchText") String searchText);
    
    // Buscar logs que contengan texto específico en la respuesta de la IA
    @Query("SELECT cl FROM ConversationLog cl WHERE LOWER(cl.aiResponse) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY cl.createdAt DESC")
    List<ConversationLog> findByAiResponseContainingIgnoreCase(@Param("searchText") String searchText);
    
    // Obtener estadísticas de conversación por sesión con nombre del agente
    @Query("SELECT cl.sessionName, COUNT(cl), MIN(cl.createdAt), MAX(cl.createdAt), " +
           "COALESCE(aw.name, at.name, 'Agente sin nombre') as agentName, " +
           "CASE WHEN aw.sessionName IS NOT NULL THEN 'whatsapp' " +
           "     WHEN at.sessionName IS NOT NULL THEN 'telegram' " +
           "     ELSE LOWER(cl.platform) END as platform " +
           "FROM ConversationLog cl " +
           "LEFT JOIN AgentWhatsApp aw ON cl.sessionName = aw.sessionName " +
           "LEFT JOIN AgentTelegram at ON cl.sessionName = at.sessionName " +
           "GROUP BY cl.sessionName, aw.name, at.name, aw.sessionName, at.sessionName, cl.platform " +
           "ORDER BY MAX(cl.createdAt) DESC")
    List<Object[]> getConversationStatsBySession();
    
    // Query de debug para verificar los session names
    @Query("SELECT DISTINCT cl.sessionName, cl.platform FROM ConversationLog cl ORDER BY cl.sessionName")
    List<Object[]> getDistinctSessionNames();
    
    // Query de debug para verificar joins con agentes de telegram
    @Query("SELECT cl.sessionName, cl.platform, at.sessionName as agentSessionName, at.name as agentName " +
           "FROM ConversationLog cl " +
           "LEFT JOIN AgentTelegram at ON cl.sessionName = at.sessionName " +
           "WHERE cl.platform = 'telegram' OR LOWER(cl.sessionName) LIKE '%telegram%'")
    List<Object[]> debugTelegramJoins();
    
    // Eliminar logs antiguos (más de X días)
    @Query("DELETE FROM ConversationLog cl WHERE cl.createdAt < :cutoffDate")
    void deleteOldLogs(@Param("cutoffDate") ZonedDateTime cutoffDate);
    
    // Obtener el último log de una sesión
    @Query("SELECT cl FROM ConversationLog cl WHERE cl.sessionName = :sessionName ORDER BY cl.createdAt DESC LIMIT 1")
    ConversationLog findLastBySessionName(@Param("sessionName") String sessionName);
    
    // Verificar si existe algún log para una sesión
    boolean existsBySessionName(String sessionName);
    
    // Buscar logs por plataforma
    List<ConversationLog> findByPlatform(String platform);
    
    // Buscar logs por plataforma ordenados por fecha de creación descendente
    List<ConversationLog> findByPlatformOrderByCreatedAtDesc(String platform);
    
    // Obtener logs paginados por sesión
    @Query(value = "SELECT * FROM conversation_logs WHERE session_name = :sessionName ORDER BY created_at ASC LIMIT :limit OFFSET :offset", 
           nativeQuery = true)
    List<ConversationLog> findBySessionNamePaginated(@Param("sessionName") String sessionName, 
                                                     @Param("limit") int limit, 
                                                     @Param("offset") int offset);
}
