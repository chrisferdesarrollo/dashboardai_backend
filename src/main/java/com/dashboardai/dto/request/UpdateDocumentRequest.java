package com.dashboardai.dto.request;

import java.util.List;
import java.util.UUID;

public class UpdateDocumentRequest {
    
    private String name;
    
    private String description;
    
    private List<String> tags;
    
    private UUID agentId;
    
    // Constructors
    public UpdateDocumentRequest() {}
    
    public UpdateDocumentRequest(String name, String description, List<String> tags, UUID agentId) {
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
        return "UpdateDocumentRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", agentId=" + agentId +
                '}';
    }
}