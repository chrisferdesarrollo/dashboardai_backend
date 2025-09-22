package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;

public class CreateConversationLogRequest {
    
    @NotBlank(message = "El nombre de la sesión es requerido")
    @Size(max = 255, message = "El nombre de la sesión no puede exceder 255 caracteres")
    private String sessionName;
    
    @Size(max = 10000, message = "El mensaje del usuario no puede exceder 10000 caracteres")
    private String userMessage;
    
    @Size(max = 10000, message = "La respuesta de la IA no puede exceder 10000 caracteres")
    private String aiResponse;
    
    @Size(max = 255, message = "El nombre del usuario no puede exceder 255 caracteres")
    private String userName;
    
    @Size(max = 50, message = "El teléfono del usuario no puede exceder 50 caracteres")
    private String userPhone;
    
    @Size(max = 20, message = "La plataforma no puede exceder 20 caracteres")
    private String platform;
    
    private ZonedDateTime timestamp;
    
    // Constructors
    public CreateConversationLogRequest() {}
    
    public CreateConversationLogRequest(String sessionName, String userMessage, String aiResponse, String userName) {
        this.sessionName = sessionName;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.userName = userName;
    }
    
    public CreateConversationLogRequest(String sessionName, String userMessage, String aiResponse, String userName, String userPhone) {
        this.sessionName = sessionName;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.userName = userName;
        this.userPhone = userPhone;
    }
    
    // Getters and Setters
    public String getSessionName() {
        return sessionName;
    }
    
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    
    public String getAiResponse() {
        return aiResponse;
    }
    
    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserPhone() {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "CreateConversationLogRequest{" +
                "sessionName='" + sessionName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", aiResponse='" + aiResponse + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
