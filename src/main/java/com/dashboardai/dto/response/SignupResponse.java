package com.dashboardai.dto.response;

public class SignupResponse {
    private String message;
    private boolean requiresEmailVerification;
    private String email;

    public SignupResponse() {
    }

    public SignupResponse(String message) {
        this.message = message;
        this.requiresEmailVerification = false;
    }

    public SignupResponse(String message, boolean requiresEmailVerification, String email) {
        this.message = message;
        this.requiresEmailVerification = requiresEmailVerification;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRequiresEmailVerification() {
        return requiresEmailVerification;
    }

    public void setRequiresEmailVerification(boolean requiresEmailVerification) {
        this.requiresEmailVerification = requiresEmailVerification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
