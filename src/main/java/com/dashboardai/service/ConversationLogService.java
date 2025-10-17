package com.dashboardai.service;

import com.dashboardai.dto.response.ConversationSessionStatsResponse;
import com.dashboardai.dto.response.ConversationLogResponse;
import com.dashboardai.entity.ConversationLog;
import com.dashboardai.repository.ConversationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConversationLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationLogService.class);
    
    @Autowired
    private ConversationLogRepository conversationLogRepository;
    
    /**
     * Guardar un nuevo log de conversación
     */
    public ConversationLog saveConversationLog(String sessionName, String userMessage, String aiResponse, String userName) {
        try {
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setTimestamp(ZonedDateTime.now());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Guardar un log de conversación con timestamp personalizado
     */
    public ConversationLog saveConversationLog(String sessionName, String userMessage, String aiResponse, String userName, ZonedDateTime timestamp) {
        try {
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setTimestamp(timestamp);
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log with custom timestamp: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Guardar un nuevo log de conversación con teléfono
     */
    public ConversationLog saveConversationLog(String sessionName, String userMessage, String aiResponse, String userName, String userPhone) {
        try {
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setTimestamp(ZonedDateTime.now());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Guardar un nuevo log de conversación con teléfono y timestamp personalizado
     */
    public ConversationLog saveConversationLog(String sessionName, String userMessage, String aiResponse, String userName, String userPhone, ZonedDateTime timestamp) {
        try {
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setTimestamp(timestamp);
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log with custom timestamp: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Guardar un nuevo log de conversación con plataforma
     */
    public ConversationLog saveConversationLog(String sessionName, String userMessage, String aiResponse, String userName, String userPhone, String platform) {
        try {
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setPlatform(platform);
            conversationLog.setTimestamp(ZonedDateTime.now());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Guardar un nuevo log de conversación con plataforma y timestamp personalizado
     */
    public ConversationLog saveConversationLog(String sessionName, String userMessage, String aiResponse, String userName, String userPhone, String platform, ZonedDateTime timestamp) {
        try {
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setPlatform(platform);
            conversationLog.setTimestamp(timestamp);
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log with custom timestamp: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Guardar un log completo desde un objeto ConversationLog
     */
    public ConversationLog saveConversationLog(ConversationLog conversationLog) {
        try {
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            return savedLog;
            
        } catch (Exception e) {
            logger.error("Error saving conversation log entity: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el log de conversación: " + e.getMessage());
        }
    }
    
    /**
     * Obtener todos los logs de conversación
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getAllConversationLogs() {
        try {
            return conversationLogRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all conversation logs: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs de conversación");
        }
    }
    
    /**
     * Obtener logs de conversación por plataforma
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getConversationLogsByPlatform(String platform) {
        try {
            List<ConversationLog> logs = conversationLogRepository.findByPlatformOrderByCreatedAtDesc(platform);
            return logs;
        } catch (Exception e) {
            logger.error("Error fetching conversation logs for platform {}: {}", platform, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs de conversación para la plataforma: " + platform);
        }
    }
    
    /**
     * Obtener logs por session name
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getConversationLogsBySession(String sessionName) {
        try {
            return conversationLogRepository.findBySessionNameOrderByCreatedAtAsc(sessionName);
        } catch (Exception e) {
            logger.error("Error fetching conversation logs for session {}: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs de la sesión");
        }
    }
    
    /**
     * Obtener logs por nombre de usuario
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getConversationLogsByUser(String userName) {
        try {
            return conversationLogRepository.findByUserName(userName);
        } catch (Exception e) {
            logger.error("Error fetching conversation logs for user {}: {}", userName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs del usuario");
        }
    }
    
    /**
     * Obtener un log por ID
     */
    @Transactional(readOnly = true)
    public ConversationLog getConversationLogById(UUID logId) {
        try {
            Optional<ConversationLog> log = conversationLogRepository.findById(logId);
            if (log.isPresent()) {
                return log.get();
            } else {
                throw new RuntimeException("Log de conversación no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error fetching conversation log {}: {}", logId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el log de conversación");
        }
    }
    
    /**
     * Obtener logs recientes (últimas 24 horas)
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getRecentConversationLogs() {
        try {
            ZonedDateTime since = ZonedDateTime.now().minusHours(24);
            return conversationLogRepository.findRecentLogs(since);
        } catch (Exception e) {
            logger.error("Error fetching recent conversation logs: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs recientes");
        }
    }
    
    /**
     * Obtener nuevos mensajes para notificaciones del usuario
     * Solo incluye mensajes de agentes que pertenecen al usuario autenticado
     * Ahora retorna ConversationLogResponse que incluye el nombre del agente
     */
    @Transactional(readOnly = true)
    public List<ConversationLogResponse> getNewMessagesForUser(Long userId, ZonedDateTime since) {
        try {
            logger.info("Fetching new messages for user {} since {}", userId, since);
            List<Object[]> results = conversationLogRepository.findNewMessagesForUserWithAgentName(userId, since);
            logger.info("Query returned {} results for user {}", results.size(), userId);
            
            // Convertir Object[] a ConversationLogResponse
            return results.stream()
                    .map(result -> {
                        ConversationLog log = (ConversationLog) result[0];
                        String agentName = (String) result[1];
                        
                        ConversationLogResponse response = new ConversationLogResponse(log);
                        response.setAgentName(agentName);
                        
                        logger.info("Found message: id={}, session={}, agentId={}, agentName={}, userMessage={}", 
                                   log.getId(), log.getSessionName(), log.getAgentId(), agentName,
                                   log.getUserMessage() != null ? log.getUserMessage().substring(0, Math.min(50, log.getUserMessage().length())) : "null");
                        
                        return response;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching new messages for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener nuevos mensajes para notificaciones");
        }
    }
    
    /**
     * Obtener logs en un rango de fechas para una sesión
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getConversationLogsBySessionAndDateRange(String sessionName, ZonedDateTime startDate, ZonedDateTime endDate) {
        try {
            return conversationLogRepository.findBySessionNameAndCreatedAtBetween(sessionName, startDate, endDate);
        } catch (Exception e) {
            logger.error("Error fetching conversation logs for session {} in date range: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs de la sesión en el rango de fechas");
        }
    }
    
    /**
     * Buscar logs que contengan texto específico en el mensaje del usuario
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> searchInUserMessages(String searchText) {
        try {
            return conversationLogRepository.findByUserMessageContainingIgnoreCase(searchText);
        } catch (Exception e) {
            logger.error("Error searching in user messages: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar en los mensajes de usuario");
        }
    }
    
    /**
     * Buscar logs que contengan texto específico en las respuestas de la IA
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> searchInAiResponses(String searchText) {
        try {
            return conversationLogRepository.findByAiResponseContainingIgnoreCase(searchText);
        } catch (Exception e) {
            logger.error("Error searching in AI responses: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar en las respuestas de la IA");
        }
    }
    
    /**
     * Contar mensajes por sesión
     */
    @Transactional(readOnly = true)
    public Long countMessagesBySession(String sessionName) {
        try {
            return conversationLogRepository.countBySessionName(sessionName);
        } catch (Exception e) {
            logger.error("Error counting messages for session {}: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al contar mensajes de la sesión");
        }
    }
    
    /**
     * Obtener sesiones únicas de un usuario
     */
    @Transactional(readOnly = true)
    public List<String> getSessionsByUser(String userName) {
        try {
            return conversationLogRepository.findDistinctSessionNamesByUserName(userName);
        } catch (Exception e) {
            logger.error("Error fetching sessions for user {}: {}", userName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener las sesiones del usuario");
        }
    }
    
    /**
     * Obtener el último log de una sesión
     */
    @Transactional(readOnly = true)
    public ConversationLog getLastLogBySession(String sessionName) {
        try {
            return conversationLogRepository.findLastBySessionName(sessionName);
        } catch (Exception e) {
            logger.error("Error fetching last log for session {}: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el último log de la sesión");
        }
    }
    
    /**
     * Verificar si existe algún log para una sesión
     */
    @Transactional(readOnly = true)
    public boolean existsLogForSession(String sessionName) {
        try {
            return conversationLogRepository.existsBySessionName(sessionName);
        } catch (Exception e) {
            logger.error("Error checking if logs exist for session {}: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al verificar la existencia de logs para la sesión");
        }
    }
    
    /**
     * Eliminar logs antiguos (más de X días)
     */
    public void deleteOldLogs(int daysToKeep) {
        try {
            ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(daysToKeep);
            conversationLogRepository.deleteOldLogs(cutoffDate);
        } catch (Exception e) {
            logger.error("Error deleting old logs: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar logs antiguos");
        }
    }
    
    /**
     * Eliminar un log específico
     */
    public void deleteConversationLog(UUID logId) {
        try {
            if (!conversationLogRepository.existsById(logId)) {
                throw new RuntimeException("Log de conversación no encontrado");
            }
            
            conversationLogRepository.deleteById(logId);
            
        } catch (Exception e) {
            logger.error("Error deleting conversation log {}: {}", logId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el log de conversación");
        }
    }
    
    /**
     * Eliminar todos los logs de una sesión específica (conversación completa)
     */
    public void deleteConversationBySessionName(String sessionName) {
        try {
            logger.info("Attempting to delete all logs for session: {}", sessionName);
            
            // Verificar que existe la sesión
            if (!conversationLogRepository.existsBySessionName(sessionName)) {
                logger.warn("Session {} not found", sessionName);
                throw new RuntimeException("Sesión de conversación no encontrada");
            }
            
            // Contar mensajes antes de eliminar
            Long messageCount = conversationLogRepository.countBySessionName(sessionName);
            logger.info("Found {} messages for session {}", messageCount, sessionName);
            
            // Eliminar todos los logs de la sesión
            conversationLogRepository.deleteBySessionName(sessionName);
            
            logger.info("Successfully deleted {} messages from session {}", messageCount, sessionName);
            
        } catch (Exception e) {
            logger.error("Error deleting conversation session {}: {}", sessionName, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar la conversación: " + e.getMessage());
        }
    }
    
    /**
     * Obtener conversaciones agrupadas por sesión con información resumida
     */
    @Transactional(readOnly = true)
    public List<ConversationSessionStatsResponse> getConversationSessionsSummary() {
        try {
            List<Object[]> rawResults = conversationLogRepository.getConversationStatsBySession();
            
            return rawResults.stream()
                    .map(result -> new ConversationSessionStatsResponse(
                            (String) result[0],           // sessionName
                            (Long) result[1],             // messageCount
                            (ZonedDateTime) result[2],    // startTime
                            (ZonedDateTime) result[3],    // lastActivity
                            (String) result[4],           // agentName
                            (String) result[5]            // platform
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching conversation sessions summary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener el resumen de sesiones de conversación");
        }
    }
    
    /**
     * Obtener conversaciones agrupadas por sesión con información resumida filtradas por userId
     */
    @Transactional(readOnly = true)
    public List<ConversationSessionStatsResponse> getConversationSessionsSummaryByUserId(Long userId) {
        try {
            List<Object[]> rawResults = conversationLogRepository.getConversationStatsBySessionAndUserId(userId);
            
            return rawResults.stream()
                    .map(result -> new ConversationSessionStatsResponse(
                            (String) result[0],           // sessionName
                            (Long) result[1],             // messageCount
                            (ZonedDateTime) result[2],    // startTime
                            (ZonedDateTime) result[3],    // lastActivity
                            (String) result[4],           // agentName
                            (String) result[5]            // platform
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching conversation sessions summary for userId {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener el resumen de sesiones de conversación para el usuario");
        }
    }
    
    /**
     * Obtener todas las sesiones únicas con información básica
     */
    @Transactional(readOnly = true)
    public List<String> getAllUniqueSessions() {
        try {
            return conversationLogRepository.findAll()
                    .stream()
                    .map(ConversationLog::getSessionName)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching unique sessions: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener las sesiones únicas");
        }
    }
}
