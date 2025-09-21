package com.dashboardai.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversation_logs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ConversationLog {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "session_name", nullable = false)
    private String sessionName;
    
    @Column(name = "user_message", columnDefinition = "TEXT")
    private String userMessage;
    
    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "user_phone", length = 50)
    private String userPhone;
    
    @Column(name = "timestamp", columnDefinition = "timestamp with time zone")
    private ZonedDateTime timestamp;
    
    @Column(name = "created_at", columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;
    
    // Constructors
    public ConversationLog() {}
    
    public ConversationLog(String sessionName, String userMessage, String aiResponse, String userName) {
        this.sessionName = sessionName;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.userName = userName;
        this.createdAt = ZonedDateTime.now();
    }
    
    public ConversationLog(String sessionName, String userMessage, String aiResponse, String userName, String userPhone) {
        this.sessionName = sessionName;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.userName = userName;
        this.userPhone = userPhone;
        this.createdAt = ZonedDateTime.now();
    }
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getSessionName() {
        return sessionName;
    }
    
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
    
    public String getAiResponse() {
        return aiResponse;
    }
    
    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserPhone() {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // toString method
    @Override
    public String toString() {
        return "ConversationLog{" +
                "id=" + id +
                ", sessionName='" + sessionName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", aiResponse='" + aiResponse + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", timestamp=" + timestamp +
                ", createdAt=" + createdAt +
                '}';
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationLog)) return false;
        
        ConversationLog that = (ConversationLog) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
