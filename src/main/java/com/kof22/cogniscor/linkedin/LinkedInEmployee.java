package com.kof22.cogniscor.linkedin;

/**
 * Represents an employee discovered on LinkedIn.
 */
public class LinkedInEmployee {
    private final String name;
    private final String title;
    private String department;
    private String linkedInUrl;
    private String seniority;
    private boolean isFounder;
    private String notableInfo;
    
    public LinkedInEmployee(String name, String title) {
        this.name = name;
        this.title = title;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getLinkedInUrl() {
        return linkedInUrl;
    }
    
    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }
    
    public String getSeniority() {
        return seniority;
    }
    
    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }
    
    public boolean isFounder() {
        return isFounder;
    }
    
    public void setFounder(boolean founder) {
        isFounder = founder;
    }
    
    public String getNotableInfo() {
        return notableInfo;
    }
    
    public void setNotableInfo(String notableInfo) {
        this.notableInfo = notableInfo;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" - ").append(title);
        if (isFounder) {
            sb.append(" (Founder)");
        }
        if (notableInfo != null && !notableInfo.isEmpty()) {
            sb.append(" | ").append(notableInfo);
        }
        return sb.toString();
    }
}
