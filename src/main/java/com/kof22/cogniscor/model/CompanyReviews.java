package com.kof22.cogniscor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents company reviews and ratings aggregated from various sources.
 */
public class CompanyReviews {
    private Double overallRating;
    private Integer totalReviews;
    private List<String> pros;
    private List<String> cons;
    private String workLifeBalance;
    private String compensation;
    private String careerGrowth;
    private String management;
    
    public CompanyReviews() {
        this.pros = new ArrayList<>();
        this.cons = new ArrayList<>();
    }
    
    // Getters and Setters
    public Double getOverallRating() {
        return overallRating;
    }
    
    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public List<String> getPros() {
        return pros;
    }
    
    public void setPros(List<String> pros) {
        this.pros = pros;
    }
    
    public void addPro(String pro) {
        this.pros.add(pro);
    }
    
    public List<String> getCons() {
        return cons;
    }
    
    public void setCons(List<String> cons) {
        this.cons = cons;
    }
    
    public void addCon(String con) {
        this.cons.add(con);
    }
    
    public String getWorkLifeBalance() {
        return workLifeBalance;
    }
    
    public void setWorkLifeBalance(String workLifeBalance) {
        this.workLifeBalance = workLifeBalance;
    }
    
    public String getCompensation() {
        return compensation;
    }
    
    public void setCompensation(String compensation) {
        this.compensation = compensation;
    }
    
    public String getCareerGrowth() {
        return careerGrowth;
    }
    
    public void setCareerGrowth(String careerGrowth) {
        this.careerGrowth = careerGrowth;
    }
    
    public String getManagement() {
        return management;
    }
    
    public void setManagement(String management) {
        this.management = management;
    }
}
