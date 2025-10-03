package com.dashboardai.dto.response;

import com.dashboardai.entity.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DocumentResponse {
    
    private UUID id;
    private String name;
    private String description;
    private String originalFilename;
    private String fileType;
    private Long fileSize;
    private List<String> tags;
    private UUID agentId;
    private LocalDateTime uploadDate;
    private Boolean processed;
    private String processingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public DocumentResponse() {}
    
    public DocumentResponse(Document document) {
        this.id = document.getId();
        this.name = document.getName();
        this.description = document.getDescription();
        this.originalFilename = document.getOriginalFilename();
        this.fileType = document.getFileType();
        this.fileSize = document.getFileSize();
        this.tags = document.getTags() != null ? List.of(document.getTags()) : null;
        this.agentId = document.getAgentId();
        this.uploadDate = document.getUploadDate();
        this.processed = document.getProcessed();
        this.processingStatus = document.getProcessingStatus() != null ? 
            document.getProcessingStatus().toString() : null;
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }
    
    // Static factory method
    public static DocumentResponse fromEntity(Document document) {
        return new DocumentResponse(document);
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
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
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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
    
    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    public Boolean getProcessed() {
        return processed;
    }
    
    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
    
    public String getProcessingStatus() {
        return processingStatus;
    }
    
    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
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
}