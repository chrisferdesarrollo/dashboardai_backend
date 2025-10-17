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
    private String userPhone;
    private String platform;
    private ZonedDateTime timestamp;
    private ZonedDateTime createdAt;
    private Long userId;
    private UUID agentId;
    private String agentName;
    
    // Constructors
    public ConversationLogResponse() {}
    
    public ConversationLogResponse(ConversationLog conversationLog) {
        this.id = conversationLog.getId();
        this.sessionName = conversationLog.getSessionName();
        this.userMessage = conversationLog.getUserMessage();
        this.aiResponse = conversationLog.getAiResponse();
        this.userName = conversationLog.getUserName();
        this.userPhone = conversationLog.getUserPhone();
        this.platform = conversationLog.getPlatform();
        this.timestamp = conversationLog.getTimestamp();
        this.createdAt = conversationLog.getCreatedAt();
        this.userId = conversationLog.getUserId();
        this.agentId = conversationLog.getAgentId();
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
    
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public UUID getAgentId() {
        return agentId;
    }
    
    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    
    @Override
    public String toString() {
        return "ConversationLogResponse{" +
                "id=" + id +
                ", sessionName='" + sessionName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", aiResponse='" + aiResponse + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", platform='" + platform + '\'' +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                ", userId=" + userId +
                ", agentId=" + agentId +
                ", agentName='" + agentName + '\'' +
                '}';
    }
}
