package com.dashboardai.dto.response;

import com.dashboardai.entity.AgentTelegram;

import java.time.LocalDateTime;
import java.util.UUID;

public class TelegramAgentResponse {
    
    private UUID id;
    private String name;
    private String description;
    private AgentTelegram.AgentStatus status;
    private String prompt;
    private String botName;
    private String platformConfig;
    private Integer totalExecutions;
    private LocalDateTime lastExecutionAt;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public TelegramAgentResponse() {}
    
    public TelegramAgentResponse(AgentTelegram agent) {
        this.id = agent.getId();
        this.name = agent.getName();
        this.description = agent.getDescription();
        this.status = agent.getStatus();
        this.prompt = agent.getPrompt();
        this.botName = agent.getBotName();
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
    
    public AgentTelegram.AgentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgentTelegram.AgentStatus status) {
        this.status = status;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getBotName() {
        return botName;
    }
    
    public void setBotName(String botName) {
        this.botName = botName;
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