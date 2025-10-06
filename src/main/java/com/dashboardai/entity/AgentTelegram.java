package com.dashboardai.entity;

import com.dashboardai.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agents_telegram")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AgentTelegram {
    
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
    @Column(name = "status", length = 20)
    private AgentStatus status = AgentStatus.inactive;
    
    @Column(name = "prompt", nullable = false, columnDefinition = "TEXT")
    private String prompt;
    
    @Column(name = "bot_name", length = 255)
    private String botName;
    
    @Column(name = "bot_token", length = 512)
    private String botToken;
    
    @Column(name = "session_name", length = 255)
    private String sessionName;
    
    @Column(name = "platform_config", columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String platformConfig;
    
    @Column(name = "total_executions")
    private Integer totalExecutions = 0;
    
    @Column(name = "last_execution_at")
    private LocalDateTime lastExecutionAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_agents_telegram_user_id"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum AgentStatus {
        active, inactive, error
    }
    
    // Constructors
    public AgentTelegram() {}
    
    public AgentTelegram(String name, String description, String prompt) {
        this.name = name;
        this.description = description;
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
    
    public String getBotName() {
        return botName;
    }
    
    public void setBotName(String botName) {
        this.botName = botName;
    }
    
    public String getBotToken() {
        return botToken;
    }
    
    public void setBotToken(String botToken) {
        this.botToken = botToken;
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
        return user != null ? user.getId() : null;
    }
    
    public void setUserId(Long userId) {
        if (userId != null) {
            this.user = new User();
            this.user.setId(userId);
        } else {
            this.user = null;
        }
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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