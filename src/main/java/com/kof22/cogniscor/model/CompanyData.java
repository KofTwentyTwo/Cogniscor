package com.kof22.cogniscor.model;

import com.kof22.cogniscor.linkedin.LinkedInData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main data model representing all collected information about a company.
 */
public class CompanyData {
    private String companyName;
    private String websiteUrl;
    private LocalDateTime collectionDate;
    private List<SourceData> sources;
    
    // Company information fields
    private String overview;
    private String industry;
    private String foundedYear;
    private String headquarters;
    private String companySize;
    private String revenue;
    private List<String> founders;
    private List<String> leadership;
    private String mission;
    private String culture;
    private List<String> products;
    private List<String> technologies;
    private String financials;
    private List<String> recentNews;
    private CompanyReviews reviews;
    
    // LinkedIn intelligence data
    private LinkedInData linkedInData;
    
    public CompanyData(String companyName, String websiteUrl) {
        this.companyName = companyName;
        this.websiteUrl = websiteUrl;
        this.collectionDate = LocalDateTime.now();
        this.sources = new ArrayList<>();
        this.founders = new ArrayList<>();
        this.leadership = new ArrayList<>();
        this.products = new ArrayList<>();
        this.technologies = new ArrayList<>();
        this.recentNews = new ArrayList<>();
        this.reviews = new CompanyReviews();
    }
    
    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
    
    public LocalDateTime getCollectionDate() {
        return collectionDate;
    }
    
    public void setCollectionDate(LocalDateTime collectionDate) {
        this.collectionDate = collectionDate;
    }
    
    public List<SourceData> getSources() {
        return sources;
    }
    
    public void addSource(SourceData source) {
        this.sources.add(source);
    }
    
    public String getOverview() {
        return overview;
    }
    
    public void setOverview(String overview) {
        this.overview = overview;
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
    
    public String getHeadquarters() {
        return headquarters;
    }
    
    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }
    
    public String getCompanySize() {
        return companySize;
    }
    
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }
    
    public String getRevenue() {
        return revenue;
    }
    
    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }
    
    public List<String> getFounders() {
        return founders;
    }
    
    public void setFounders(List<String> founders) {
        this.founders = founders;
    }
    
    public void addFounder(String founder) {
        this.founders.add(founder);
    }
    
    public List<String> getLeadership() {
        return leadership;
    }
    
    public void setLeadership(List<String> leadership) {
        this.leadership = leadership;
    }
    
    public void addLeader(String leader) {
        this.leadership.add(leader);
    }
    
    public String getMission() {
        return mission;
    }
    
    public void setMission(String mission) {
        this.mission = mission;
    }
    
    public String getCulture() {
        return culture;
    }
    
    public void setCulture(String culture) {
        this.culture = culture;
    }
    
    public List<String> getProducts() {
        return products;
    }
    
    public void setProducts(List<String> products) {
        this.products = products;
    }
    
    public void addProduct(String product) {
        this.products.add(product);
    }
    
    public List<String> getTechnologies() {
        return technologies;
    }
    
    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }
    
    public void addTechnology(String technology) {
        this.technologies.add(technology);
    }
    
    public String getFinancials() {
        return financials;
    }
    
    public void setFinancials(String financials) {
        this.financials = financials;
    }
    
    public List<String> getRecentNews() {
        return recentNews;
    }
    
    public void setRecentNews(List<String> recentNews) {
        this.recentNews = recentNews;
    }
    
    public void addNews(String news) {
        this.recentNews.add(news);
    }
    
    public CompanyReviews getReviews() {
        return reviews;
    }
    
    public void setReviews(CompanyReviews reviews) {
        this.reviews = reviews;
    }
    
    public LinkedInData getLinkedInData() {
        return linkedInData;
    }
    
    public void setLinkedInData(LinkedInData linkedInData) {
        this.linkedInData = linkedInData;
    }
}
