package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class N8nConfigurationRequest {
    
    @NotBlank(message = "N8n URL is required")
    @Pattern(regexp = "^https?://.*", message = "N8n URL must be a valid HTTP or HTTPS URL")
    private String n8nUrl;
    
    private String apiKey;
    
    public N8nConfigurationRequest() {}
    
    public N8nConfigurationRequest(String n8nUrl, String apiKey) {
        this.n8nUrl = n8nUrl;
        this.apiKey = apiKey;
    }
    
    public String getN8nUrl() {
        return n8nUrl;
    }
    
    public void setN8nUrl(String n8nUrl) {
        this.n8nUrl = n8nUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    public String toString() {
        return "N8nConfigurationRequest{" +
                "n8nUrl='" + n8nUrl + '\'' +
                ", apiKey='" + (apiKey != null ? "***" : null) + '\'' +
                '}';
    }
}
