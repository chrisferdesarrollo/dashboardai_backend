package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateWhatsAppAgentRequest {
    
    @NotBlank(message = "El nombre del agente es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @NotBlank(message = "El prompt es requerido")
    private String prompt;
    
    private String sessionName;
    
    // Campos para soporte de múltiples workflows
    private List<String> workflowIds;
    
    private String primaryWorkflowId;
    
    private String platformConfig;
    
    private Long userId;
    
    // Constructors
    public CreateWhatsAppAgentRequest() {}
    
    public CreateWhatsAppAgentRequest(String name, String description, String prompt) {
        this.name = name;
        this.description = description;
        this.prompt = prompt;
    }
    
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
    
    public List<String> getWorkflowIds() {
        return workflowIds;
    }
    
    public void setWorkflowIds(List<String> workflowIds) {
        this.workflowIds = workflowIds;
    }
    
    public String getPrimaryWorkflowId() {
        return primaryWorkflowId;
    }
    
    public void setPrimaryWorkflowId(String primaryWorkflowId) {
        this.primaryWorkflowId = primaryWorkflowId;
    }
    
    public String getPlatformConfig() {
        return platformConfig;
    }
    
    public void setPlatformConfig(String platformConfig) {
        this.platformConfig = platformConfig;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "CreateWhatsAppAgentRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", prompt='" + prompt + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", workflowIds=" + workflowIds +
                ", primaryWorkflowId='" + primaryWorkflowId + '\'' +
                ", platformConfig='" + platformConfig + '\'' +
                ", userId=" + userId +
                '}';
    }
}