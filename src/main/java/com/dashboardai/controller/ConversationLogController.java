package com.dashboardai.controller;

import com.dashboardai.dto.request.CreateConversationLogRequest;
import com.dashboardai.dto.response.ConversationLogResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.entity.ConversationLog;
import com.dashboardai.service.ConversationLogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversation-logs")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173", "http://localhost:8443", "http://host.docker.internal:8443", "http://192.168.128.6:8443", "https://ba15be3b1392.ngrok-free.app", "https://*.ngrok-free.app"}, maxAge = 3600)
public class ConversationLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversationLogController.class);
    
    @Autowired
    private ConversationLogService conversationLogService;
    
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
            logger.info("Timestamp: {}", request.getTimestamp());
            
            ConversationLog savedLog;
            
            if (request.getTimestamp() != null) {
                if (request.getUserPhone() != null) {
                    savedLog = conversationLogService.saveConversationLog(
                        request.getSessionName(),
                        request.getUserMessage(),
                        request.getAiResponse(),
                        request.getUserName(),
                        request.getUserPhone(),
                        request.getTimestamp()
                    );
                } else {
                    savedLog = conversationLogService.saveConversationLog(
                        request.getSessionName(),
                        request.getUserMessage(),
                        request.getAiResponse(),
                        request.getUserName(),
                        request.getTimestamp()
                    );
                }
            } else {
                if (request.getUserPhone() != null) {
                    savedLog = conversationLogService.saveConversationLog(
                        request.getSessionName(),
                        request.getUserMessage(),
                        request.getAiResponse(),
                        request.getUserName(),
                        request.getUserPhone()
                    );
                } else {
                    savedLog = conversationLogService.saveConversationLog(
                        request.getSessionName(),
                        request.getUserMessage(),
                        request.getAiResponse(),
                        request.getUserName()
                    );
                }
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
     * Obtener todos los logs de conversación
     */
    @GetMapping
    public ResponseEntity<?> getAllConversationLogs() {
        try {
            logger.info("GET /api/conversation-logs - Fetching all conversation logs");
            
            List<ConversationLog> logs = conversationLogService.getAllConversationLogs();
            List<ConversationLogResponse> responses = logs.stream()
                    .map(ConversationLogResponse::new)
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
                    .map(ConversationLogResponse::new)
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
                    .map(ConversationLogResponse::new)
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
                    .map(ConversationLogResponse::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new GetConversationLogsResponseWrapper(true, responses, null));
            
        } catch (Exception e) {
            logger.error("Error fetching recent conversation logs: {}", e.getMessage(), e);
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
                    .map(ConversationLogResponse::new)
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
                    .map(ConversationLogResponse::new)
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
}
