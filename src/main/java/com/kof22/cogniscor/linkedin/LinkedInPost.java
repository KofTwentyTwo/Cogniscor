package com.kof22.cogniscor.linkedin;

/**
 * Represents a LinkedIn post or update from the company.
 */
public class LinkedInPost {
    private final String topic;
    private final String description;
    private String engagementLevel;
    private String postDate;
    
    public LinkedInPost(String topic, String description) {
        this.topic = topic;
        this.description = description;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getEngagementLevel() {
        return engagementLevel;
    }
    
    public void setEngagementLevel(String engagementLevel) {
        this.engagementLevel = engagementLevel;
    }
    
    public String getPostDate() {
        return postDate;
    }
    
    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(topic);
        if (engagementLevel != null) {
            sb.append(" [").append(engagementLevel).append(" engagement]");
        }
        sb.append(": ").append(description);
        return sb.toString();
    }
}
