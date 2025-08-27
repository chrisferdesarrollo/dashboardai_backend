package com.dashboardai.dto.response;

import com.dashboardai.entity.Workflow;
import java.time.LocalDateTime;
import java.util.List;

public class WorkflowResponse {
    
    private String id;
    private String name;
    private String description;
    private boolean active;
    private String nodeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tags;
    private String workflowJson;
    
    // Constructors
    public WorkflowResponse() {}
    
    // Constructor from Entity
    public WorkflowResponse(Workflow workflow) {
        this.id = workflow.getN8nWorkflowId();
        this.name = workflow.getName();
        this.description = workflow.getDescription();
        this.active = workflow.getActive();
        this.nodeCount = String.valueOf(workflow.getNodeCount());
        this.createdAt = workflow.getCreatedAt();
        this.updatedAt = workflow.getUpdatedAt();
        
        // Convertir tags List<String> a String
        List<String> tagsList = workflow.getTags();
        this.tags = tagsList != null ? String.join(", ", tagsList) : "";
        
        this.workflowJson = workflow.getWorkflowData();
    }
    
    public WorkflowResponse(String id, String name, String description, boolean active, 
                           String nodeCount, LocalDateTime createdAt, LocalDateTime updatedAt,
                           String tags, String workflowJson) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.nodeCount = nodeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tags = tags;
        this.workflowJson = workflowJson;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
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
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getNodeCount() {
        return nodeCount;
    }
    
    public void setNodeCount(String nodeCount) {
        this.nodeCount = nodeCount;
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
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getWorkflowJson() {
        return workflowJson;
    }
    
    public void setWorkflowJson(String workflowJson) {
        this.workflowJson = workflowJson;
    }
}
