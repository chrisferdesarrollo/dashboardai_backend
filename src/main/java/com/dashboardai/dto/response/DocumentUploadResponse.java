package com.dashboardai.dto.response;

import java.util.UUID;

public class DocumentUploadResponse {
    
    private UUID id;
    private String message;
    private String status;
    private DocumentResponse document;
    
    // Constructors
    public DocumentUploadResponse() {}
    
    public DocumentUploadResponse(UUID id, String message, String status, DocumentResponse document) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.document = document;
    }
    
    // Static factory methods
    public static DocumentUploadResponse success(DocumentResponse document) {
        return new DocumentUploadResponse(
            document.getId(),
            "Documento subido exitosamente",
            "success",
            document
        );
    }
    
    public static DocumentUploadResponse error(String message) {
        return new DocumentUploadResponse(
            null,
            message,
            "error",
            null
        );
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public DocumentResponse getDocument() {
        return document;
    }
    
    public void setDocument(DocumentResponse document) {
        this.document = document;
    }
}