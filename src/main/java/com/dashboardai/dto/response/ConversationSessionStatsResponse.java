package com.dashboardai.dto.response;

import java.time.ZonedDateTime;

public class ConversationSessionStatsResponse {
    private String sessionName;
    private Long messageCount;
    private ZonedDateTime startTime;
    private ZonedDateTime lastActivity;
    private String agentName;
    private String platform;

    // Constructors
    public ConversationSessionStatsResponse() {}

    public ConversationSessionStatsResponse(String sessionName, Long messageCount, ZonedDateTime startTime, 
                                          ZonedDateTime lastActivity, String agentName, String platform) {
        this.sessionName = sessionName;
        this.messageCount = messageCount;
        this.startTime = startTime;
        this.lastActivity = lastActivity;
        this.agentName = agentName;
        this.platform = platform;
    }

    // Getters and setters
    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(ZonedDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "ConversationSessionStatsResponse{" +
                "sessionName='" + sessionName + '\'' +
                ", messageCount=" + messageCount +
                ", startTime=" + startTime +
                ", lastActivity=" + lastActivity +
                ", agentName='" + agentName + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}