package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class CreateDocumentRequest {
    
    @NotBlank(message = "El nombre del documento es requerido")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    private List<String> tags;
    
    private UUID agentId;
    
    // Constructors
    public CreateDocumentRequest() {}
    
    public CreateDocumentRequest(String name, String description, List<String> tags, UUID agentId) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.agentId = agentId;
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
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public UUID getAgentId() {
        return agentId;
    }
    
    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }
    
    @Override
    public String toString() {
        return "CreateDocumentRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", agentId=" + agentId +
                '}';
    }
}