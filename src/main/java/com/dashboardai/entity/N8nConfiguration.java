package com.dashboardai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "n8n_configurations")
public class N8nConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "n8n_url", nullable = false)
    private String n8nUrl;
    
    @Column(name = "webhook_base", nullable = false)
    private String webhookBase;
    
    @Column(name = "api_base")
    private String apiBase;
    
    @Column(name = "api_key")
    private String apiKey;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "last_test_date")
    private LocalDateTime lastTestDate;
    
    @Column(name = "last_test_status")
    private String lastTestStatus;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public N8nConfiguration() {}
    
    public N8nConfiguration(Long userId, String n8nUrl) {
        this.userId = userId;
        this.n8nUrl = n8nUrl;
        this.webhookBase = n8nUrl + "/webhook";
        this.apiBase = n8nUrl + "/api/v1";
        this.isActive = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getN8nUrl() {
        return n8nUrl;
    }
    
    public void setN8nUrl(String n8nUrl) {
        this.n8nUrl = n8nUrl;
        // Auto-update derived URLs
        if (n8nUrl != null) {
            this.webhookBase = n8nUrl + "/webhook";
            this.apiBase = n8nUrl + "/api/v1";
        }
    }
    
    public String getWebhookBase() {
        return webhookBase;
    }
    
    public void setWebhookBase(String webhookBase) {
        this.webhookBase = webhookBase;
    }
    
    public String getApiBase() {
        return apiBase;
    }
    
    public void setApiBase(String apiBase) {
        this.apiBase = apiBase;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getLastTestDate() {
        return lastTestDate;
    }
    
    public void setLastTestDate(LocalDateTime lastTestDate) {
        this.lastTestDate = lastTestDate;
    }
    
    public String getLastTestStatus() {
        return lastTestStatus;
    }
    
    public void setLastTestStatus(String lastTestStatus) {
        this.lastTestStatus = lastTestStatus;
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
    
    @Override
    public String toString() {
        return "N8nConfiguration{" +
                "id=" + id +
                ", userId=" + userId +
                ", n8nUrl='" + n8nUrl + '\'' +
                ", webhookBase='" + webhookBase + '\'' +
                ", apiBase='" + apiBase + '\'' +
                ", isActive=" + isActive +
                ", lastTestDate=" + lastTestDate +
                ", lastTestStatus='" + lastTestStatus + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
