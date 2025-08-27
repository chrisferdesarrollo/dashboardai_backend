package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UploadWorkflowRequest {
    
    @NotBlank(message = "El nombre del workflow es requerido")
    private String name;
    
    private String description;
    
    @NotNull(message = "El contenido JSON del workflow es requerido")
    private String workflowJson;
    
    private boolean activate = false;
    
    // Constructors
    public UploadWorkflowRequest() {}
    
    public UploadWorkflowRequest(String name, String description, String workflowJson, boolean activate) {
        this.name = name;
        this.description = description;
        this.workflowJson = workflowJson;
        this.activate = activate;
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
    
    public String getWorkflowJson() {
        return workflowJson;
    }
    
    public void setWorkflowJson(String workflowJson) {
        this.workflowJson = workflowJson;
    }
    
    public boolean isActivate() {
        return activate;
    }
    
    public void setActivate(boolean activate) {
        this.activate = activate;
    }
}
