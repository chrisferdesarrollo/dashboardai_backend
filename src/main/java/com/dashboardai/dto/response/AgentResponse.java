package com.dashboardai.dto.response;

import com.dashboardai.entity.Agent;

import java.time.LocalDateTime;
import java.util.UUID;

public class AgentResponse {
    
    private UUID id;
    private String name;
    private String description;
    private Agent.Platform platform;
    private Agent.AgentStatus status;
    private String prompt;
    private String workflowId;
    private String platformConfig;
    private Integer totalExecutions;
    private LocalDateTime lastExecutionAt;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public AgentResponse() {}
    
    public AgentResponse(Agent agent) {
        this.id = agent.getId();
        this.name = agent.getName();
        this.description = agent.getDescription();
        this.platform = agent.getPlatform();
        this.status = agent.getStatus();
        this.prompt = agent.getPrompt();
        this.workflowId = agent.getWorkflowId();
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
    
    public Agent.Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Agent.Platform platform) {
        this.platform = platform;
    }
    
    public Agent.AgentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Agent.AgentStatus status) {
        this.status = status;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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
