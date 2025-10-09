package com.dashboardai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public class UpdateAgentRequest {
    
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @Size(max = 10000, message = "El prompt no puede exceder 10000 caracteres")
    private String prompt;
    
    @JsonProperty("sessionName")
    private String sessionName;
    
    @JsonProperty("platformConfig")
    private String platformConfig;
    
    // Constructors
    public UpdateAgentRequest() {}
    
    public UpdateAgentRequest(String name, String description, String prompt, String sessionName, String platformConfig) {
        this.name = name;
        this.description = description;
        this.prompt = prompt;
        this.sessionName = sessionName;
        this.platformConfig = platformConfig;
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
    
    public String getPlatformConfig() {
        return platformConfig;
    }
    
    public void setPlatformConfig(String platformConfig) {
        this.platformConfig = platformConfig;
    }
    
    @Override
    public String toString() {
        return "UpdateAgentRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", prompt='" + (prompt != null ? prompt.substring(0, Math.min(prompt.length(), 50)) + "..." : null) + '\'' +
                ", sessionName='" + sessionName + '\'' +
                ", platformConfig='" + platformConfig + '\'' +
                '}';
    }
}