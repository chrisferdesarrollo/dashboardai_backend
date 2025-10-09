package com.dashboardai.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateAvatarRequest {
    @NotBlank(message = "La URL del avatar es obligatoria")
    private String avatarUrl;

    public UpdateAvatarRequest() {}

    public UpdateAvatarRequest(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}