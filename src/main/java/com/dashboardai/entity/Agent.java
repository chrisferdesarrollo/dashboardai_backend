package com.dashboardai.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agents")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Agent {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 50)
    private Platform platform;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AgentStatus status = AgentStatus.inactive;
    
    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    private String prompt;
    
    @Column(name = "workflow_id", length = 255)
    private String workflowId;
    
    @Column(name = "platform_config", columnDefinition = "TEXT")
    private String platformConfig;
    
    @Column(name = "total_executions")
    private Integer totalExecutions = 0;
    
    @Column(name = "last_execution_at")
    private LocalDateTime lastExecutionAt;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum Platform {
        whatsapp, telegram
    }
    
    public enum AgentStatus {
        active, inactive, error
    }
    
    // Constructors
    public Agent() {}
    
    public Agent(String name, String description, Platform platform, String prompt) {
        this.name = name;
        this.description = description;
        this.platform = platform;
        this.prompt = prompt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
    
    public Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    
    public AgentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgentStatus status) {
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
