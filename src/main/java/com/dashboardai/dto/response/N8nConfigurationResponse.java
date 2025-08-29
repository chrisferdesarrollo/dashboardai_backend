package com.dashboardai.dto.response;

import java.time.LocalDateTime;

public class N8nConfigurationResponse {
    
    private Long id;
    private String n8nUrl;
    private String webhookBase;
    private String apiBase;
    private Boolean isActive;
    private LocalDateTime lastTestDate;
    private String lastTestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public N8nConfigurationResponse() {}
    
    public N8nConfigurationResponse(Long id, String n8nUrl, String webhookBase, String apiBase, 
                                   Boolean isActive, LocalDateTime lastTestDate, String lastTestStatus,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.n8nUrl = n8nUrl;
        this.webhookBase = webhookBase;
        this.apiBase = apiBase;
        this.isActive = isActive;
        this.lastTestDate = lastTestDate;
        this.lastTestStatus = lastTestStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getN8nUrl() {
        return n8nUrl;
    }
    
    public void setN8nUrl(String n8nUrl) {
        this.n8nUrl = n8nUrl;
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
        return "N8nConfigurationResponse{" +
                "id=" + id +
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
