package com.kof22.cogniscor.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all LinkedIn intelligence data for a company.
 */
public class LinkedInData {
    private final String companyName;
    private String companyUrl;
    
    // Company Information
    private String employeeCount;
    private String headquarters;
    private String industry;
    private String foundedYear;
    private String companySize;
    private String tagline;
    private List<String> specialties = new ArrayList<>();
    private List<String> recentUpdates = new ArrayList<>();
    
    // Employee Network
    private List<LinkedInEmployee> employees = new ArrayList<>();
    private Map<String, Integer> departmentCounts = new HashMap<>();
    private Map<String, Integer> seniorityCounts = new HashMap<>();
    
    // Content & Engagement
    private String postingFrequency;
    private String contentStrategy;
    private List<String> contentThemes = new ArrayList<>();
    private List<String> thoughtLeadershipAreas = new ArrayList<>();
    private List<LinkedInPost> recentPosts = new ArrayList<>();
    
    public LinkedInData(String companyName) {
        this.companyName = companyName;
    }
    
    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }
    
    public String getCompanyUrl() {
        return companyUrl;
    }
    
    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }
    
    public String getEmployeeCount() {
        return employeeCount;
    }
    
    public void setEmployeeCount(String employeeCount) {
        this.employeeCount = employeeCount;
    }
    
    public String getHeadquarters() {
        return headquarters;
    }
    
    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getFoundedYear() {
        return foundedYear;
    }
    
    public void setFoundedYear(String foundedYear) {
        this.foundedYear = foundedYear;
    }
    
    public String getCompanySize() {
        return companySize;
    }
    
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }
    
    public String getTagline() {
        return tagline;
    }
    
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
    
    public List<String> getSpecialties() {
        return specialties;
    }
    
    public void addSpecialty(String specialty) {
        this.specialties.add(specialty);
    }
    
    public List<String> getRecentUpdates() {
        return recentUpdates;
    }
    
    public void addRecentUpdate(String update) {
        this.recentUpdates.add(update);
    }
    
    public List<LinkedInEmployee> getEmployees() {
        return employees;
    }
    
    public void addEmployee(LinkedInEmployee employee) {
        this.employees.add(employee);
    }
    
    public Map<String, Integer> getDepartmentCounts() {
        return departmentCounts;
    }
    
    public void incrementDepartmentCount(String department) {
        departmentCounts.put(department, departmentCounts.getOrDefault(department, 0) + 1);
    }
    
    public Map<String, Integer> getSeniorityCounts() {
        return seniorityCounts;
    }
    
    public void incrementSeniorityCount(String seniority) {
        seniorityCounts.put(seniority, seniorityCounts.getOrDefault(seniority, 0) + 1);
    }
    
    public String getPostingFrequency() {
        return postingFrequency;
    }
    
    public void setPostingFrequency(String postingFrequency) {
        this.postingFrequency = postingFrequency;
    }
    
    public String getContentStrategy() {
        return contentStrategy;
    }
    
    public void setContentStrategy(String contentStrategy) {
        this.contentStrategy = contentStrategy;
    }
    
    public List<String> getContentThemes() {
        return contentThemes;
    }
    
    public void addContentTheme(String theme) {
        this.contentThemes.add(theme);
    }
    
    public List<String> getThoughtLeadershipAreas() {
        return thoughtLeadershipAreas;
    }
    
    public void addThoughtLeadershipArea(String area) {
        this.thoughtLeadershipAreas.add(area);
    }
    
    public List<LinkedInPost> getRecentPosts() {
        return recentPosts;
    }
    
    public void addPost(LinkedInPost post) {
        this.recentPosts.add(post);
    }
    
    /**
     * Check if we have meaningful LinkedIn data.
     */
    public boolean hasData() {
        return companyUrl != null || !employees.isEmpty() || !recentPosts.isEmpty();
    }
}
