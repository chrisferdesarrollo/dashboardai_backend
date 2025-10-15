package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateConversationLogRequest;
import com.dashboardai.dto.response.ConversationLogResponse;
import com.dashboardai.dto.response.ConversationSessionStatsResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.ConversationLog;
import com.dashboardai.security.services.UserDetailsImpl;
import com.dashboardai.service.ConversationLogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/conversation-logs")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173", "http://localhost:8443", "http://host.docker.internal:8443", "http://192.168.128.6:8443", "https://ba15be3b1392.ngrok-free.app", "https://*.ngrok-free.app"}, maxAge = 3600)
public class ConversationLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationLogController.class);
    
    @Autowired
    private ConversationLogService conversationLogService;
    
    /**
     * Obtener el ID del usuario autenticado
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            // Temporal: usar usuario testuser (ID 2) cuando no hay autenticación
            return 2L; // Usuario testuser que existe en la base de datos
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    /**
     * Crear un nuevo log de conversación
     */
    @PostMapping
    public ResponseEntity<?> createConversationLog(@Valid @RequestBody CreateConversationLogRequest request) {
        try {
            logger.info("POST /api/conversation-logs - Request received:");
            logger.info("SessionName: {}", request.getSessionName());
            logger.info("UserMessage: {}", request.getUserMessage());
            logger.info("AiResponse: {}", request.getAiResponse());
            logger.info("UserName: {}", request.getUserName());
            logger.info("UserPhone: {}", request.getUserPhone());
            logger.info("Platform: {}", request.getPlatform());
            logger.info("Timestamp: {}", request.getTimestamp());
            
            // Procesar mensajes para convertir caracteres de escape
            String processedUserMessage = request.getUserMessage();
            String processedAiResponse = request.getAiResponse();
            
            if (processedUserMessage != null) {
                processedUserMessage = processedUserMessage.replace("\\n", "\n")
                                                         .replace("\\r", "\r")
                                                         .replace("\\t", "\t");
                logger.info("Processed UserMessage: {}", processedUserMessage);
            }
            
            if (processedAiResponse != null) {
                processedAiResponse = processedAiResponse.replace("\\n", "\n")
                                                       .replace("\\r", "\r")
                                                       .replace("\\t", "\t");
                logger.info("Processed AiResponse: {}", processedAiResponse);
            }
            
            ConversationLog savedLog;
            
            // Auto-detectar platform si no viene especificado
            String detectedPlatform = request.getPlatform();
            if (detectedPlatform == null || detectedPlatform.trim().isEmpty()) {
                detectedPlatform = detectPlatform(request.getUserPhone(), request.getSessionName());
                logger.info("Platform auto-detected as: {}", detectedPlatform);
            }
            
            // Determinar qué método usar basado en los campos disponibles
            if (request.getTimestamp() != null) {
                // Siempre usar la versión que incluye platform
                savedLog = conversationLogService.saveConversationLog(
                    request.getSessionName(),
                    processedUserMessage,
                    processedAiResponse,
                    request.getUserName(),
                    request.getUserPhone(),
                    detectedPlatform,
                    request.getTimestamp()
                );
            } else {
                // Siempre usar la versión que incluye platform
                savedLog = conversationLogService.saveConversationLog(
                    request.getSessionName(),
                    processedUserMessage,
                    processedAiResponse,
                    request.getUserName(),
                    request.getUserPhone(),
                    detectedPlatform
                );
            }
            
            ConversationLogResponse response = new ConversationLogResponse(savedLog);
            
            return ResponseEntity.ok(new CreateConversationLogResponseWrapper(true, response, null));
            
        } catch (Exception e) {
            logger.error("Error creating conversation log: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateConversationLogResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Detectar plataforma basada en el teléfono o nombre de sesión
     */
    private String detectPlatform(String userPhone, String sessionName) {
        // 1. Detectar por nombre de sesión (patrón más confiable)
        if (sessionName != null) {
            String sessionLower = sessionName.toLowerCase();
            
            // WhatsApp usa patrones como: agent_*, whatsapp_*, wa_*
            if (sessionLower.startsWith("agent_") || 
                sessionLower.contains("whatsapp") || 
                sessionLower.contains("wa_")) {
                return "whatsapp";
            }
            
            // Telegram usa patrones como: bot_*, token_*, telegram_*, tg_*
            if (sessionLower.startsWith("bot_") || 
                sessionLower.startsWith("token_") ||
                sessionLower.contains("telegram") || 
                sessionLower.contains("tg_")) {
                return "telegram";
            }
        }
        
        // 2. Detectar por número de teléfono (formato WhatsApp)
        if (userPhone != null && userPhone.contains("@c.us")) {
            return "whatsapp";
        }
        
        // 3. Valor por defecto (ajustar según tu caso más común)
        return "whatsapp";  // Cambié de "unknown" a "whatsapp" como default
    }
    
    /**
     * Obtener todos los logs de conversación (con filtro opcional por plataforma)
     */
    @GetMapping
    public ResponseEntity<?> getAllConversationLogs(@RequestParam(required = false) String platform) {
        try {
            logger.info("GET /api/conversation-logs - Fetching conversation logs with platform filter: {}", platform);
            
            List<ConversationLog> logs;
            if (platform != null && !platform.isEmpty() && !platform.equalsIgnoreCase("all")) {
                // Filtrar por plataforma específica
                logs = conversationLogService.getConversationLogsByPlatform(platform);
                logger.info("Retrieved {} logs for platform: {}", logs.size(), platform);
            } else {
                // Obtener todos los logs
                logs = conversationLogService.getAllConversationLogs();
                logger.info("Retrieved {} logs for all platforms", logs.size());
            }
            
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error fetching conversation logs: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener logs por session name
     */
    @GetMapping("/session/{sessionName}")
    public ResponseEntity<?> getConversationLogsBySession(@PathVariable String sessionName) {
        try {
            logger.info("GET /api/conversation-logs/session/{} - Fetching logs for session", sessionName);
            
            List<ConversationLog> logs = conversationLogService.getConversationLogsBySession(sessionName);
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error fetching logs for session {}: {}", sessionName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener logs por nombre de usuario
     */
    @GetMapping("/user/{userName}")
    public ResponseEntity<?> getConversationLogsByUser(@PathVariable String userName) {
        try {
            logger.info("GET /api/conversation-logs/user/{} - Fetching logs for user", userName);
            
            List<ConversationLog> logs = conversationLogService.getConversationLogsByUser(userName);
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error fetching logs for user {}: {}", userName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener un log específico por ID
     */
    @GetMapping("/{logId}")
    public ResponseEntity<?> getConversationLogById(@PathVariable UUID logId) {
        try {
            logger.info("GET /api/conversation-logs/{} - Fetching conversation log", logId);
            
            ConversationLog log = conversationLogService.getConversationLogById(logId);
            ConversationLogResponse response = new ConversationLogResponse(log);
            
            return ResponseEntity.ok(new CreateConversationLogResponseWrapper(true, response, null));
            
        } catch (Exception e) {
            logger.error("Error fetching conversation log {}: {}", logId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CreateConversationLogResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener logs recientes (últimas 24 horas)
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentConversationLogs() {
        try {
            logger.info("GET /api/conversation-logs/recent - Fetching recent conversation logs");
            
            List<ConversationLog> logs = conversationLogService.getRecentConversationLogs();
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error fetching recent conversation logs: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener logs recientes para notificaciones (con filtro de timestamp opcional)
     */
    @GetMapping("/recent-for-notifications")
    public ResponseEntity<?> getRecentConversationLogsForNotifications(
            @RequestParam(required = false) String since) {
        try {
            logger.info("GET /api/conversation-logs/recent-for-notifications - Since: {}", since);
            
            List<ConversationLog> logs;
            if (since != null && !since.isEmpty()) {
                // Parsear el timestamp desde el parámetro
                ZonedDateTime sinceTimestamp = ZonedDateTime.parse(since);
                logs = conversationLogService.getConversationLogsSince(sinceTimestamp);
            } else {
                // Si no hay timestamp, obtener logs de las últimas 2 horas
                logs = conversationLogService.getRecentConversationLogsForNotifications();
            }
            
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error fetching recent conversation logs for notifications: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Buscar en mensajes de usuario
     */
    @GetMapping("/search/user-messages")
    public ResponseEntity<?> searchInUserMessages(@RequestParam String searchText) {
        try {
            logger.info("GET /api/conversation-logs/search/user-messages - Searching for: {}", searchText);
            
            List<ConversationLog> logs = conversationLogService.searchInUserMessages(searchText);
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error searching in user messages: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Buscar en respuestas de IA
     */
    @GetMapping("/search/ai-responses")
    public ResponseEntity<?> searchInAiResponses(@RequestParam String searchText) {
        try {
            logger.info("GET /api/conversation-logs/search/ai-responses - Searching for: {}", searchText);
            
            List<ConversationLog> logs = conversationLogService.searchInAiResponses(searchText);
            List<ConversationLogResponse> responses = logs.stream()
                    .map(log -> new ConversationLogResponse(log))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error searching in AI responses: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationLogsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener sesiones únicas de un usuario
     */
    @GetMapping("/sessions/user/{userName}")
    public ResponseEntity<?> getSessionsByUser(@PathVariable String userName) {
        try {
            logger.info("GET /api/conversation-logs/sessions/user/{} - Fetching sessions for user", userName);
            
            List<String> sessions = conversationLogService.getSessionsByUser(userName);
            
            return ResponseEntity.ok(new GetSessionsResponseWrapper(true, sessions, null));
            
        } catch (Exception e) {
            logger.error("Error fetching sessions for user {}: {}", userName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetSessionsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Contar mensajes por sesión
     */
    @GetMapping("/count/session/{sessionName}")
    public ResponseEntity<?> countMessagesBySession(@PathVariable String sessionName) {
        try {
            logger.info("GET /api/conversation-logs/count/session/{} - Counting messages for session", sessionName);
            
            Long count = conversationLogService.countMessagesBySession(sessionName);
            
            return ResponseEntity.ok(new CountResponseWrapper(true, count, null));
            
        } catch (Exception e) {
            logger.error("Error counting messages for session {}: {}", sessionName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new CountResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Eliminar un log específico
     */
    @DeleteMapping("/{logId}")
    public ResponseEntity<?> deleteConversationLog(@PathVariable UUID logId) {
        try {
            logger.info("DELETE /api/conversation-logs/{} - Deleting conversation log", logId);
            
            conversationLogService.deleteConversationLog(logId);
            
            return ResponseEntity.ok(new MessageResponse("Log de conversación eliminado exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error deleting conversation log {}: {}", logId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar el log: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar toda una conversación (todos los logs de una sesión)
     */
    @DeleteMapping("/session/{sessionName}")
    public ResponseEntity<?> deleteConversationBySessionName(@PathVariable String sessionName) {
        try {
            logger.info("DELETE /api/conversation-logs/session/{} - Deleting entire conversation", sessionName);
            
            conversationLogService.deleteConversationBySessionName(sessionName);
            
            return ResponseEntity.ok(new MessageResponse("Conversación eliminada exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error deleting conversation {}: {}", sessionName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar la conversación: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar logs antiguos
     */
    @DeleteMapping("/cleanup/{daysToKeep}")
    public ResponseEntity<?> deleteOldLogs(@PathVariable int daysToKeep) {
        try {
            logger.info("DELETE /api/conversation-logs/cleanup/{} - Deleting logs older than {} days", daysToKeep, daysToKeep);
            
            conversationLogService.deleteOldLogs(daysToKeep);
            
            return ResponseEntity.ok(new MessageResponse("Logs antiguos eliminados exitosamente"));
            
        } catch (Exception e) {
            logger.error("Error deleting old logs: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error al eliminar logs antiguos: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener todas las sesiones únicas
     */
    @GetMapping("/sessions")
    public ResponseEntity<?> getAllUniqueSessions() {
        try {
            logger.info("GET /api/conversation-logs/sessions - Fetching all unique sessions");
            
            List<String> sessions = conversationLogService.getAllUniqueSessions();
            
            return ResponseEntity.ok(new GetSessionsResponseWrapper(true, sessions, null));
            
        } catch (Exception e) {
            logger.error("Error fetching unique sessions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetSessionsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    /**
     * Obtener estadísticas de conversaciones por sesión filtradas por usuario
     */
    @GetMapping("/sessions/stats")
    public ResponseEntity<?> getConversationSessionsStats(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            logger.info("GET /api/conversation-logs/sessions/stats - Fetching conversation sessions statistics for userId: {}", userId);
            
            List<ConversationSessionStatsResponse> stats = conversationLogService.getConversationSessionsSummaryByUserId(userId);
            
            return ResponseEntity.ok(new GetConversationStatsResponseWrapper(true, stats, null));
            
        } catch (Exception e) {
            logger.error("Error fetching conversation sessions stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationStatsResponseWrapper(false, null, e.getMessage()));
        }
    }

    /**
     * Obtener estadísticas de conversaciones por sesión SIN FILTRO (para testing)
     */
    @GetMapping("/sessions/stats/all")
    public ResponseEntity<?> getAllConversationSessionsStats() {
        try {
            logger.info("GET /api/conversation-logs/sessions/stats/all - Fetching ALL conversation sessions statistics");
            
            List<ConversationSessionStatsResponse> stats = conversationLogService.getConversationSessionsSummary();
            
            return ResponseEntity.ok(new GetConversationStatsResponseWrapper(true, stats, null));
            
        } catch (Exception e) {
            logger.error("Error fetching ALL conversation sessions stats: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GetConversationStatsResponseWrapper(false, null, e.getMessage()));
        }
    }
    
    // Response Wrapper Classes
    public static class CreateConversationLogResponseWrapper {
        private boolean success;
        private ConversationLogResponse data;
        private String error;
        
        public CreateConversationLogResponseWrapper(boolean success, ConversationLogResponse data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public ConversationLogResponse getData() { return data; }
        public void setData(ConversationLogResponse data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public static class GetConversationLogsResponseWrapper {
        private boolean success;
        private List<ConversationLogResponse> data;
        private String error;
        
        public GetConversationLogsResponseWrapper(boolean success, List<ConversationLogResponse> data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<ConversationLogResponse> getData() { return data; }
        public void setData(List<ConversationLogResponse> data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public static class GetSessionsResponseWrapper {
        private boolean success;
        private List<String> data;
        private String error;
        
        public GetSessionsResponseWrapper(boolean success, List<String> data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<String> getData() { return data; }
        public void setData(List<String> data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public static class CountResponseWrapper {
        private boolean success;
        private Long data;
        private String error;
        
        public CountResponseWrapper(boolean success, Long data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Long getData() { return data; }
        public void setData(Long data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    public static class GetConversationStatsResponseWrapper {
        private boolean success;
        private List<ConversationSessionStatsResponse> data;
        private String error;
        
        public GetConversationStatsResponseWrapper(boolean success, List<ConversationSessionStatsResponse> data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<ConversationSessionStatsResponse> getData() { return data; }
        public void setData(List<ConversationSessionStatsResponse> data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
