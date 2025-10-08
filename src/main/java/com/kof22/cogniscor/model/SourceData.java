package com.kof22.cogniscor.model;

import java.time.LocalDateTime;

/**
 * Represents a single data source with metadata.
 */
public class SourceData {
    private String title;
    private String sourceUrl;
    private String sourceType; // website, linkedin, wikipedia, news, etc.
    private String content;
    private LocalDateTime retrievalDate;
    private boolean successful;
    private String errorMessage;
    
    public SourceData(String title, String sourceUrl, String sourceType) {
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.sourceType = sourceType;
        this.retrievalDate = LocalDateTime.now();
        this.successful = false;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getRetrievalDate() {
        return retrievalDate;
    }
    
    public void setRetrievalDate(LocalDateTime retrievalDate) {
        this.retrievalDate = retrievalDate;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
