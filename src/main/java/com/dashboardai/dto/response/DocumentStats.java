package com.dashboardai.dto.response;

public class DocumentStats {
    
    private long total;
    private long processed;
    private long pending;
    private long failed;
    private double successRate;
    
    // Constructors
    public DocumentStats() {}
    
    public DocumentStats(long total, long processed, long pending, long failed, double successRate) {
        this.total = total;
        this.processed = processed;
        this.pending = pending;
        this.failed = failed;
        this.successRate = successRate;
    }
    
    // Getters and Setters
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
    }
    
    public long getProcessed() {
        return processed;
    }
    
    public void setProcessed(long processed) {
        this.processed = processed;
    }
    
    public long getPending() {
        return pending;
    }
    
    public void setPending(long pending) {
        this.pending = pending;
    }
    
    public long getFailed() {
        return failed;
    }
    
    public void setFailed(long failed) {
        this.failed = failed;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
}