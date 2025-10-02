package com.dashboardai.service;

import com.dashboardai.dto.response.ConversationSessionStatsResponse;
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
            logger.info("Saving conversation log for session: {}, user: {}", sessionName, userName);
            
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setTimestamp(ZonedDateTime.now());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Saving conversation log with custom timestamp for session: {}, user: {}", sessionName, userName);
            
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setTimestamp(timestamp);
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log with custom timestamp saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Saving conversation log for session: {}, user: {}, phone: {}", sessionName, userName, userPhone);
            
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setTimestamp(ZonedDateTime.now());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Saving conversation log with custom timestamp for session: {}, user: {}, phone: {}", sessionName, userName, userPhone);
            
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setTimestamp(timestamp);
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log with custom timestamp saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Saving conversation log for session: {}, user: {}, phone: {}, platform: {}", sessionName, userName, userPhone, platform);
            
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setPlatform(platform);
            conversationLog.setTimestamp(ZonedDateTime.now());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Saving conversation log with custom timestamp for session: {}, user: {}, phone: {}, platform: {}", sessionName, userName, userPhone, platform);
            
            ConversationLog conversationLog = new ConversationLog();
            conversationLog.setSessionName(sessionName);
            conversationLog.setUserMessage(userMessage);
            conversationLog.setAiResponse(aiResponse);
            conversationLog.setUserName(userName);
            conversationLog.setUserPhone(userPhone);
            conversationLog.setPlatform(platform);
            conversationLog.setTimestamp(timestamp);
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log with custom timestamp saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Saving conversation log entity for session: {}", conversationLog.getSessionName());
            
            ConversationLog savedLog = conversationLogRepository.save(conversationLog);
            
            logger.info("Conversation log entity saved successfully with ID: {}", savedLog.getId());
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
            logger.info("Fetching all conversation logs");
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
            logger.info("Fetching conversation logs for platform: {}", platform);
            List<ConversationLog> logs = conversationLogRepository.findByPlatformOrderByCreatedAtDesc(platform);
            logger.info("Found {} logs for platform: {}", logs.size(), platform);
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
            logger.info("Fetching conversation logs for session: {}", sessionName);
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
            logger.info("Fetching conversation logs for user: {}", userName);
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
            logger.info("Fetching conversation log with ID: {}", logId);
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
            logger.info("Fetching recent conversation logs (last 24 hours)");
            ZonedDateTime since = ZonedDateTime.now().minusHours(24);
            return conversationLogRepository.findRecentLogs(since);
        } catch (Exception e) {
            logger.error("Error fetching recent conversation logs: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los logs recientes");
        }
    }
    
    /**
     * Obtener logs en un rango de fechas para una sesión
     */
    @Transactional(readOnly = true)
    public List<ConversationLog> getConversationLogsBySessionAndDateRange(String sessionName, ZonedDateTime startDate, ZonedDateTime endDate) {
        try {
            logger.info("Fetching conversation logs for session {} between {} and {}", sessionName, startDate, endDate);
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
            logger.info("Searching for text '{}' in user messages", searchText);
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
            logger.info("Searching for text '{}' in AI responses", searchText);
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
            logger.info("Counting messages for session: {}", sessionName);
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
            logger.info("Fetching unique sessions for user: {}", userName);
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
            logger.info("Fetching last log for session: {}", sessionName);
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
            logger.info("Checking if logs exist for session: {}", sessionName);
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
            logger.info("Deleting logs older than {} days", daysToKeep);
            ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(daysToKeep);
            conversationLogRepository.deleteOldLogs(cutoffDate);
            logger.info("Old logs deleted successfully");
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
            logger.info("Deleting conversation log with ID: {}", logId);
            
            if (!conversationLogRepository.existsById(logId)) {
                throw new RuntimeException("Log de conversación no encontrado");
            }
            
            conversationLogRepository.deleteById(logId);
            logger.info("Conversation log deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting conversation log {}: {}", logId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el log de conversación");
        }
    }
    
    /**
     * Obtener conversaciones agrupadas por sesión con información resumida
     */
    @Transactional(readOnly = true)
    public List<ConversationSessionStatsResponse> getConversationSessionsSummary() {
        try {
            logger.info("Fetching conversation sessions summary with agent names");
            
            // Debug: verificar session names disponibles
            List<Object[]> sessionNames = conversationLogRepository.getDistinctSessionNames();
            logger.info("Available session names in conversation_logs:");
            for (Object[] session : sessionNames) {
                logger.info("SessionName: {}, Platform: {}", session[0], session[1]);
            }
            
            // Debug: verificar joins con telegram
            List<Object[]> telegramJoins = conversationLogRepository.debugTelegramJoins();
            logger.info("Telegram joins debug:");
            for (Object[] join : telegramJoins) {
                logger.info("ConversationSessionName: {}, Platform: {}, AgentSessionName: {}, AgentName: {}", 
                           join[0], join[1], join[2], join[3]);
            }
            
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
            logger.info("Fetching conversation sessions summary with agent names for userId: {}", userId);
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
            logger.info("Fetching all unique session names");
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
