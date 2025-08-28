package com.dashboardai.dto.request;

import com.dashboardai.entity.Agent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class CreateAgentRequest {
    
    @NotBlank(message = "El nombre del agente es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @NotNull(message = "La plataforma es requerida")
    private Agent.Platform platform;
    
    @NotBlank(message = "El prompt es requerido")
    private String prompt;
    
    private String workflowId;
    
    // Nuevos campos para soporte de múltiples workflows
    private List<String> workflowIds;
    
    private String primaryWorkflowId;
    
    private String platformConfig;
    
    private Long userId;
    
    // Constructors
    public CreateAgentRequest() {}
    
    public CreateAgentRequest(String name, String description, Agent.Platform platform, String prompt) {
        this.name = name;
        this.description = description;
        this.platform = platform;
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
    
    public Agent.Platform getPlatform() {
        return platform;
    }
    
    public void setPlatform(Agent.Platform platform) {
        this.platform = platform;
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
        return "CreateAgentRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", platform=" + platform +
                ", prompt='" + prompt + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", workflowIds=" + workflowIds +
                ", primaryWorkflowId='" + primaryWorkflowId + '\'' +
                ", platformConfig='" + platformConfig + '\'' +
                ", userId=" + userId +
                '}';
    }
}
