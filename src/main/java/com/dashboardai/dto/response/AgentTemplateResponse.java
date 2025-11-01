package com.dashboardai.dto.response;

import com.dashboardai.entity.AgentTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

public class AgentTemplateResponse {
    
    private UUID id;
    private String name;
    private String description;
    private String systemPrompt;
    private String knowledgeBaseId;
    private String knowledgeBaseName;
    private Boolean isActive;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    
    // Constructor from entity
    public AgentTemplateResponse(AgentTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.description = template.getDescription();
        this.systemPrompt = template.getSystemPrompt();
        this.knowledgeBaseId = template.getKnowledgeBaseId();
        this.knowledgeBaseName = template.getKnowledgeBaseName();
        this.isActive = template.getIsActive();
        this.usageCount = template.getUsageCount();
        this.createdAt = template.getCreatedAt();
        this.updatedAt = template.getUpdatedAt();
        
        // Información del creador
        if (template.getUser() != null) {
            this.userId = template.getUser().getId();
            this.username = template.getUser().getUsername();
        }
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
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }
    
    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }
    
    public String getKnowledgeBaseName() {
        return knowledgeBaseName;
    }
    
    public void setKnowledgeBaseName(String knowledgeBaseName) {
        this.knowledgeBaseName = knowledgeBaseName;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
