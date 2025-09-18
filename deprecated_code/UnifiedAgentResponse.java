package com.dashboardai.dto.response;

import com.dashboardai.entity.Agent;

import java.time.LocalDateTime;
import java.util.UUID;

public class UnifiedAgentResponse {
    
    private UUID id;
    private String name;
    private String description;
    private String platform; // "whatsapp" o "telegram"
    private String status;
    private String prompt;
    private String sessionName; // Para WhatsApp será sessionName, para Telegram será botName
    private String platformConfig;
    private Integer totalExecutions;
    private LocalDateTime lastExecutionAt;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public UnifiedAgentResponse() {}
    
    // Constructor para agentes WhatsApp
    public UnifiedAgentResponse(WhatsAppAgentResponse whatsappAgent, String platform) {
        this.id = whatsappAgent.getId();
        this.name = whatsappAgent.getName();
        this.description = whatsappAgent.getDescription();
        this.platform = platform;
        this.status = whatsappAgent.getStatus().toString();
        this.prompt = whatsappAgent.getPrompt();
        this.sessionName = whatsappAgent.getSessionName();
        this.platformConfig = whatsappAgent.getPlatformConfig();
        this.totalExecutions = whatsappAgent.getTotalExecutions();
        this.lastExecutionAt = whatsappAgent.getLastExecutionAt();
        this.userId = whatsappAgent.getUserId();
        this.createdAt = whatsappAgent.getCreatedAt();
        this.updatedAt = whatsappAgent.getUpdatedAt();
    }
    
    // Constructor para agentes Telegram
    public UnifiedAgentResponse(TelegramAgentResponse telegramAgent, String platform) {
        this.id = telegramAgent.getId();
        this.name = telegramAgent.getName();
        this.description = telegramAgent.getDescription();
        this.platform = platform;
        this.status = telegramAgent.getStatus().toString();
        this.prompt = telegramAgent.getPrompt();
        this.sessionName = telegramAgent.getBotName(); // botName se mapea a sessionName para compatibilidad
        this.platformConfig = telegramAgent.getPlatformConfig();
        this.totalExecutions = telegramAgent.getTotalExecutions();
        this.lastExecutionAt = telegramAgent.getLastExecutionAt();
        this.userId = telegramAgent.getUserId();
        this.createdAt = telegramAgent.getCreatedAt();
        this.updatedAt = telegramAgent.getUpdatedAt();
    }
    
    // Constructor para compatibilidad con Agent original
    public UnifiedAgentResponse(Agent agent) {
        this.id = agent.getId();
        this.name = agent.getName();
        this.description = agent.getDescription();
        this.platform = agent.getPlatform().toString();
        this.status = agent.getStatus().toString();
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