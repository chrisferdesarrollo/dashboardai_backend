package com.dashboardai.dto.response;

import com.dashboardai.entity.AgentWhatsApp;

import java.time.LocalDateTime;
import java.util.UUID;

public class WhatsAppAgentResponse {
    
    private UUID id;
    private String name;
    private String description;
    private AgentWhatsApp.AgentStatus status;
    private String prompt;
    private String sessionName;
    private String platformConfig;
    private Integer totalExecutions;
    private LocalDateTime lastExecutionAt;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public WhatsAppAgentResponse() {}
    
    public WhatsAppAgentResponse(AgentWhatsApp agent) {
        this.id = agent.getId();
        this.name = agent.getName();
        this.description = agent.getDescription();
        this.status = agent.getStatus();
        this.prompt = agent.getPrompt();
        this.sessionName = agent.getSessionName();
        this.platformConfig = agent.getPlatformConfig();
        this.totalExecutions = agent.getTotalExecutions();
        this.lastExecutionAt = agent.getLastExecutionAt();
        this.userId = agent.getUserId();
        this.createdAt = agent.getCreatedAt();
        this.updatedAt = agent.getUpdatedAt();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public AgentWhatsApp.AgentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgentWhatsApp.AgentStatus status) {
        this.status = status;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getSessionName() {
        return sessionName;
    }
    
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
    
    public String getPlatformConfig() {
        return platformConfig;
    }
    
    public void setPlatformConfig(String platformConfig) {
        this.platformConfig = platformConfig;
    }
    
    public Integer getTotalExecutions() {
        return totalExecutions;
    }
    
    public void setTotalExecutions(Integer totalExecutions) {
        this.totalExecutions = totalExecutions;
    }
    
    public LocalDateTime getLastExecutionAt() {
        return lastExecutionAt;
    }
    
    public void setLastExecutionAt(LocalDateTime lastExecutionAt) {
        this.lastExecutionAt = lastExecutionAt;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}