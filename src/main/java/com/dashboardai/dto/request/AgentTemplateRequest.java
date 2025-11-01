package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AgentTemplateRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
    private String description;
    
    @NotBlank(message = "El prompt del sistema es requerido")
    private String systemPrompt;
    
    private String knowledgeBaseId;
    
    private String knowledgeBaseName;
    
    private Boolean isActive = true;
    
    // Getters and Setters
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
}
