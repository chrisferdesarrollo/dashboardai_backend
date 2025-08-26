package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ConfigurationRequest {
    
    @NotBlank(message = "Key is required")
    private String key;
    
    @NotBlank(message = "Value is required")
    private String value;
    
    private String description;
    
    // Constructors
    public ConfigurationRequest() {}
    
    public ConfigurationRequest(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }
    
    // Getters and Setters
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
