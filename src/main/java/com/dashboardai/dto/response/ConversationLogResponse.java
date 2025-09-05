package com.dashboardai.dto.response;

import com.dashboardai.entity.ConversationLog;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ConversationLogResponse {
    
    private UUID id;
    private String sessionName;
    private String userMessage;
    private String aiResponse;
    private String userName;
    private ZonedDateTime timestamp;
    private ZonedDateTime createdAt;
    
    // Constructors
    public ConversationLogResponse() {}
    
    public ConversationLogResponse(ConversationLog conversationLog) {
        this.id = conversationLog.getId();
        this.sessionName = conversationLog.getSessionName();
        this.userMessage = conversationLog.getUserMessage();
        this.aiResponse = conversationLog.getAiResponse();
        this.userName = conversationLog.getUserName();
        this.timestamp = conversationLog.getTimestamp();
        this.createdAt = conversationLog.getCreatedAt();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
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
    
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "ConversationLogResponse{" +
                "id=" + id +
                ", sessionName='" + sessionName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", aiResponse='" + aiResponse + '\'' +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                '}';
    }
}
