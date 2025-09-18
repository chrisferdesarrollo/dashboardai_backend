package com.dashboardai.dto.response;

import com.dashboardai.entity.AgentWhatsApp;
import com.dashboardai.entity.AgentTelegram;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO unificado para respuestas de agentes que combina WhatsApp y Telegram
 */
public class UnifiedAgentResponse {
    private UUID id;
    private String name;
    private String description;
    private String platform;
    private String status;
    private String prompt;
    private String sessionName; // Para WhatsApp
    private String botName;     // Para Telegram
    private String platformConfig;
    private Integer totalExecutions;
    private LocalDateTime lastExecutionAt;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor para AgentWhatsApp
    public UnifiedAgentResponse(AgentWhatsApp agent, String platform) {
        this.id = agent.getId();
        this.name = agent.getName();
        this.description = agent.getDescription();
        this.platform = platform;
        this.status = agent.getStatus() != null ? agent.getStatus().name() : "inactive";
        this.prompt = agent.getPrompt();
        this.sessionName = agent.getSessionName();
        this.botName = null; // No aplica para WhatsApp
        this.platformConfig = agent.getPlatformConfig();
        this.totalExecutions = agent.getTotalExecutions();
        this.lastExecutionAt = agent.getLastExecutionAt();
        this.userId = agent.getUserId();
        this.createdAt = agent.getCreatedAt();
        this.updatedAt = agent.getUpdatedAt();
    }
    
    // Constructor para AgentTelegram
    public UnifiedAgentResponse(AgentTelegram agent, String platform) {
        this.id = agent.getId();
        this.name = agent.getName();
        this.description = agent.getDescription();
        this.platform = platform;
        this.status = agent.getStatus() != null ? agent.getStatus().name() : "inactive";
        this.prompt = agent.getPrompt();
        this.sessionName = null; // No aplica para Telegram
        this.botName = agent.getBotName();
        this.platformConfig = agent.getPlatformConfig();
        this.totalExecutions = agent.getTotalExecutions();
        this.lastExecutionAt = agent.getLastExecutionAt();
        this.userId = agent.getUserId();
        this.createdAt = agent.getCreatedAt();
        this.updatedAt = agent.getUpdatedAt();
    }
    
    // Constructor para WhatsAppAgentResponse
    public UnifiedAgentResponse(WhatsAppAgentResponse response, String platform) {
        this.id = response.getId();
        this.name = response.getName();
        this.description = response.getDescription();
        this.platform = platform;
        this.status = response.getStatus() != null ? response.getStatus().name() : "inactive";
        this.prompt = response.getPrompt();
        this.sessionName = response.getSessionName();
        this.botName = null; // No aplica para WhatsApp
        this.platformConfig = response.getPlatformConfig();
        this.totalExecutions = response.getTotalExecutions();
        this.lastExecutionAt = response.getLastExecutionAt();
        this.userId = response.getUserId();
        this.createdAt = response.getCreatedAt();
        this.updatedAt = response.getUpdatedAt();
    }
    
    // Constructor para TelegramAgentResponse
    public UnifiedAgentResponse(TelegramAgentResponse response, String platform) {
        this.id = response.getId();
        this.name = response.getName();
        this.description = response.getDescription();
        this.platform = platform;
        this.status = response.getStatus() != null ? response.getStatus().name() : "inactive";
        this.prompt = response.getPrompt();
        this.sessionName = null; // No aplica para Telegram
        this.botName = response.getBotName();
        this.platformConfig = response.getPlatformConfig();
        this.totalExecutions = response.getTotalExecutions();
        this.lastExecutionAt = response.getLastExecutionAt();
        this.userId = response.getUserId();
        this.createdAt = response.getCreatedAt();
        this.updatedAt = response.getUpdatedAt();
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
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
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