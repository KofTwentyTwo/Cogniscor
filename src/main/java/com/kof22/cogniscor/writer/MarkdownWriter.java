package com.kof22.cogniscor.writer;

import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.linkedin.LinkedInData;
import com.kof22.cogniscor.linkedin.LinkedInEmployee;
import com.kof22.cogniscor.linkedin.LinkedInPost;
import com.kof22.cogniscor.llm.LLMReportReviewer;
import com.kof22.cogniscor.llm.OpenAIService;
import com.kof22.cogniscor.model.CompanyData;
import com.kof22.cogniscor.model.SourceData;
import com.kof22.cogniscor.util.MarkdownEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Handles writing data to Markdown files.
 * Can optionally use LLM to review and improve reports.
 */
public class MarkdownWriter {
    private static final Logger logger = LoggerFactory.getLogger(MarkdownWriter.class);
    private final Configuration config;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LLMReportReviewer llmReviewer;
    
    public MarkdownWriter() {
        this.config = Configuration.getInstance();
    }
    
    /**
     * Set LLM reviewer for AI-powered report improvement.
     */
    public void setLLMReviewer(OpenAIService openAIService) {
        if (openAIService != null && openAIService.isEnabled()) {
            this.llmReviewer = new LLMReportReviewer(openAIService);
            logger.info("LLM report review enabled");
        }
    }
    
    /**
     * Writes all company data to organized Markdown files.
     */
    public void writeAll(CompanyData companyData) throws IOException {
        String companyName = companyData.getCompanyName();
        String sanitizedName = MarkdownEscaper.sanitizeFilename(companyName);
        
        logger.info("Writing data for: {}", companyName);
        
        // Create directory structure
        String baseDir = config.getOutputBaseDir();
        Path companyDir = Paths.get(baseDir, sanitizedName);
        Path sourcesDir = companyDir.resolve("sources");
        Path reportsDir = companyDir.resolve("reports");
        
        Files.createDirectories(sourcesDir);
        Files.createDirectories(reportsDir);
        
        // Write individual source files
        writeSourceFiles(companyData, sourcesDir);
        
        // Write reports
        writeExecutiveSummary(companyData, reportsDir);
        writeDetailedReport(companyData, reportsDir);
        
        logger.info("Successfully wrote all files to: {}", companyDir);
    }
    
    /**
     * Writes individual source data files.
     */
    private void writeSourceFiles(CompanyData companyData, Path sourcesDir) throws IOException {
        int index = 1;
        for (SourceData source : companyData.getSources()) {
            String filename = String.format("%02d_%s.md", 
                index++, 
                MarkdownEscaper.sanitizeFilename(source.getSourceType()));
            
            Path filePath = sourcesDir.resolve(filename);
            String content = formatSourceFile(source);
            Files.writeString(filePath, content);
            logger.debug("Wrote source file: {}", filename);
        }
    }
    
    /**
     * Formats a single source file.
     */
    private String formatSourceFile(SourceData source) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# ").append(source.getTitle()).append("\n\n");
        sb.append("---\n\n");
        sb.append("**Source Type:** ").append(source.getSourceType()).append("\n\n");
        sb.append("**URL:** ").append(source.getSourceUrl()).append("\n\n");
        sb.append("**Retrieved:** ").append(source.getRetrievalDate().format(formatter)).append("\n\n");
        sb.append("**Status:** ").append(source.isSuccessful() ? "✓ Success" : "✗ Failed").append("\n\n");
        
        if (!source.isSuccessful() && source.getErrorMessage() != null) {
            sb.append("**Error:** ").append(source.getErrorMessage()).append("\n\n");
        }
        
        sb.append("---\n\n");
        
        if (source.getContent() != null) {
            sb.append("## Content\n\n");
            sb.append(source.getContent()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Writes executive summary report.
     */
    private void writeExecutiveSummary(CompanyData companyData, Path reportsDir) throws IOException {
        Path filePath = reportsDir.resolve("executive_summary.md");
        String content = formatExecutiveSummary(companyData);
        
        // Review with LLM if available
        if (llmReviewer != null) {
            content = llmReviewer.reviewExecutiveSummary(content, companyData.getCompanyName());
        }
        
        Files.writeString(filePath, content);
        logger.info("Wrote executive summary");
    }
    
    /**
     * Formats the executive summary.
     */
    private String formatExecutiveSummary(CompanyData companyData) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# Executive Summary: ").append(companyData.getCompanyName()).append("\n\n");
        sb.append("*Generated: ").append(companyData.getCollectionDate().format(formatter)).append("*\n\n");
        sb.append("---\n\n");
        
        // Overview
        if (companyData.getOverview() != null) {
            sb.append("## Overview\n\n");
            sb.append(companyData.getOverview()).append("\n\n");
        }
        
        // Key Facts
        sb.append("## Key Facts\n\n");
        
        if (companyData.getIndustry() != null) {
            sb.append("**Industry:** ").append(companyData.getIndustry()).append("\n\n");
        }
        
        if (companyData.getFoundedYear() != null) {
            sb.append("**Founded:** ").append(companyData.getFoundedYear());
            if (!companyData.getFounders().isEmpty()) {
                sb.append(" by ").append(String.join(", ", companyData.getFounders()));
            }
            sb.append("\n\n");
        }
        
        if (companyData.getHeadquarters() != null) {
            sb.append("**Headquarters:** ").append(companyData.getHeadquarters()).append("\n\n");
        }
        
        if (companyData.getCompanySize() != null) {
            sb.append("**Company Size:** ").append(companyData.getCompanySize()).append("\n\n");
        }
        
        if (companyData.getRevenue() != null) {
            sb.append("**Revenue:** ").append(companyData.getRevenue()).append("\n\n");
        }
        
        if (companyData.getWebsiteUrl() != null) {
            sb.append("**Website:** ").append(companyData.getWebsiteUrl()).append("\n\n");
        }
        
        // Mission
        if (companyData.getMission() != null) {
            sb.append("## Mission & Values\n\n");
            sb.append(companyData.getMission()).append("\n\n");
        }
        
        // Products
        if (!companyData.getProducts().isEmpty()) {
            sb.append("## Products & Services\n\n");
            companyData.getProducts().forEach(product -> 
                sb.append("- ").append(product).append("\n"));
            sb.append("\n");
        }
        
        // Reviews
        if (companyData.getReviews().getOverallRating() != null) {
            sb.append("## Employee Reviews\n\n");
            sb.append("**Overall Rating:** ").append(companyData.getReviews().getOverallRating()).append("/5");
            if (companyData.getReviews().getTotalReviews() != null) {
                sb.append(" (").append(companyData.getReviews().getTotalReviews()).append(" reviews)");
            }
            sb.append("\n\n");
            
            if (!companyData.getReviews().getPros().isEmpty()) {
                sb.append("**Pros:** ").append(companyData.getReviews().getPros().get(0)).append("\n\n");
            }
            
            if (!companyData.getReviews().getCons().isEmpty()) {
                sb.append("**Cons:** ").append(companyData.getReviews().getCons().get(0)).append("\n\n");
            }
        }
        
        // Recent News
        if (!companyData.getRecentNews().isEmpty()) {
            sb.append("## Recent News\n\n");
            companyData.getRecentNews().stream()
                .limit(3)
                .forEach(news -> sb.append("- ").append(news).append("\n"));
            sb.append("\n");
        }
        
        sb.append("---\n\n");
        sb.append("*This summary was automatically generated from publicly available sources.*\n");
        
        return sb.toString();
    }
    
    /**
     * Writes detailed report.
     */
    private void writeDetailedReport(CompanyData companyData, Path reportsDir) throws IOException {
        Path filePath = reportsDir.resolve("detailed_report.md");
        String content = formatDetailedReport(companyData);
        
        // Review with LLM if available  
        if (llmReviewer != null) {
            content = llmReviewer.reviewDetailedReport(content, companyData.getCompanyName());
        }
        
        Files.writeString(filePath, content);
        logger.info("Wrote detailed report");
    }
    
    /**
     * Formats the detailed report.
     */
    private String formatDetailedReport(CompanyData companyData) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# Detailed Company Report: ").append(companyData.getCompanyName()).append("\n\n");
        sb.append("*Generated: ").append(companyData.getCollectionDate().format(formatter)).append("*\n\n");
        sb.append("---\n\n");
        
        // Table of Contents
        sb.append("## Table of Contents\n\n");
        sb.append("1. [Company Overview](#company-overview)\n");
        sb.append("2. [History & Background](#history--background)\n");
        sb.append("3. [Leadership](#leadership)\n");
        sb.append("4. [LinkedIn Intelligence](#linkedin-intelligence)\n");
        sb.append("5. [Products & Services](#products--services)\n");
        sb.append("6. [Technology Stack](#technology-stack)\n");
        sb.append("7. [Company Culture](#company-culture)\n");
        sb.append("8. [Employee Reviews](#employee-reviews)\n");
        sb.append("9. [Financials](#financials)\n");
        sb.append("10. [Recent News & Developments](#recent-news--developments)\n");
        sb.append("11. [Data Sources](#data-sources)\n\n");
        sb.append("---\n\n");
        
        // 1. Company Overview
        sb.append("## Company Overview\n\n");
        if (companyData.getOverview() != null) {
            sb.append(companyData.getOverview()).append("\n\n");
        }
        
        sb.append("### Basic Information\n\n");
        appendIfNotNull(sb, "**Industry:**", companyData.getIndustry());
        appendIfNotNull(sb, "**Founded:**", companyData.getFoundedYear());
        appendIfNotNull(sb, "**Headquarters:**", companyData.getHeadquarters());
        appendIfNotNull(sb, "**Company Size:**", companyData.getCompanySize());
        appendIfNotNull(sb, "**Revenue:**", companyData.getRevenue());
        appendIfNotNull(sb, "**Website:**", companyData.getWebsiteUrl());
        sb.append("\n");
        
        // 2. History & Background
        sb.append("## History & Background\n\n");
        if (companyData.getFoundedYear() != null) {
            sb.append("Founded in ").append(companyData.getFoundedYear());
            if (!companyData.getFounders().isEmpty()) {
                sb.append(" by ").append(String.join(", ", companyData.getFounders()));
            }
            sb.append(".\n\n");
        }
        
        // 3. Leadership
        if (!companyData.getLeadership().isEmpty()) {
            sb.append("## Leadership\n\n");
            companyData.getLeadership().forEach(leader -> 
                sb.append("- ").append(leader).append("\n"));
            sb.append("\n");
        }
        
        // 4. LinkedIn Intelligence
        appendLinkedInSection(sb, companyData);
        
        // 5. Products & Services
        if (!companyData.getProducts().isEmpty()) {
            sb.append("## Products & Services\n\n");
            companyData.getProducts().forEach(product -> 
                sb.append("- ").append(product).append("\n"));
            sb.append("\n");
        }
        
        // 5. Technology Stack
        if (!companyData.getTechnologies().isEmpty()) {
            sb.append("## Technology Stack\n\n");
            companyData.getTechnologies().forEach(tech -> 
                sb.append("- ").append(tech).append("\n"));
            sb.append("\n");
        }
        
        // 6. Company Culture
        sb.append("## Company Culture\n\n");
        if (companyData.getMission() != null) {
            sb.append("### Mission & Values\n\n");
            sb.append(companyData.getMission()).append("\n\n");
        }
        if (companyData.getCulture() != null) {
            sb.append("### Culture\n\n");
            sb.append(companyData.getCulture()).append("\n\n");
        }
        
        // 7. Employee Reviews
        sb.append("## Employee Reviews\n\n");
        var reviews = companyData.getReviews();
        if (reviews.getOverallRating() != null) {
            sb.append("### Overall Rating\n\n");
            sb.append("**").append(reviews.getOverallRating()).append("/5**");
            if (reviews.getTotalReviews() != null) {
                sb.append(" based on ").append(reviews.getTotalReviews()).append(" reviews");
            }
            sb.append("\n\n");
        }
        
        if (!reviews.getPros().isEmpty()) {
            sb.append("### Pros\n\n");
            reviews.getPros().forEach(pro -> sb.append("- ").append(pro).append("\n"));
            sb.append("\n");
        }
        
        if (!reviews.getCons().isEmpty()) {
            sb.append("### Cons\n\n");
            reviews.getCons().forEach(con -> sb.append("- ").append(con).append("\n"));
            sb.append("\n");
        }
        
        // 8. Financials
        if (companyData.getFinancials() != null) {
            sb.append("## Financials\n\n");
            sb.append(companyData.getFinancials()).append("\n\n");
        }
        
        // 9. Recent News
        if (!companyData.getRecentNews().isEmpty()) {
            sb.append("## Recent News & Developments\n\n");
            companyData.getRecentNews().forEach(news -> 
                sb.append("- ").append(news).append("\n"));
            sb.append("\n");
        }
        
        // 10. Data Sources
        sb.append("## Data Sources\n\n");
        sb.append("This report was compiled from the following sources:\n\n");
        
        long successfulSources = companyData.getSources().stream()
            .filter(SourceData::isSuccessful)
            .count();
        
        sb.append("**Total sources accessed:** ").append(companyData.getSources().size())
          .append(" (").append(successfulSources).append(" successful)\n\n");
        
        companyData.getSources().forEach(source -> {
            sb.append("- **").append(source.getSourceType()).append("**: ");
            sb.append(source.getTitle()).append(" - ");
            sb.append(source.isSuccessful() ? "✓" : "✗");
            sb.append("\n  ").append(source.getSourceUrl()).append("\n");
        });
        
        sb.append("\n---\n\n");
        sb.append("*This report was automatically generated using publicly available data. ");
        sb.append("Information may be incomplete or outdated. Please verify critical details independently.*\n");
        
        return sb.toString();
    }
    
    private void appendIfNotNull(StringBuilder sb, String label, String value) {
        if (value != null) {
            sb.append(label).append(" ").append(value).append("\n\n");
        }
    }
    
    /**
     * Appends LinkedIn intelligence section to the report.
     */
    private void appendLinkedInSection(StringBuilder sb, CompanyData companyData) {
        LinkedInData linkedin = companyData.getLinkedInData();
        
        if (linkedin == null || !linkedin.hasData()) {
            return;
        }
        
        sb.append("## LinkedIn Intelligence\n\n");
        
        // Company Profile
        if (linkedin.getCompanyUrl() != null) {
            sb.append("**LinkedIn Profile:** ").append(linkedin.getCompanyUrl()).append("\n\n");
        }
        
        sb.append("### Company Information\n\n");
        
        if (linkedin.getTagline() != null) {
            sb.append("*\"").append(linkedin.getTagline()).append("\"*\n\n");
        }
        
        if (linkedin.getEmployeeCount() != null) {
            sb.append("**Employee Count:** ").append(linkedin.getEmployeeCount()).append("\n\n");
        }
        
        if (linkedin.getIndustry() != null) {
            sb.append("**Industry:** ").append(linkedin.getIndustry()).append("\n\n");
        }
        
        if (linkedin.getHeadquarters() != null) {
            sb.append("**Headquarters:** ").append(linkedin.getHeadquarters()).append("\n\n");
        }
        
        if (!linkedin.getSpecialties().isEmpty()) {
            sb.append("**Specialties:** ");
            sb.append(String.join(", ", linkedin.getSpecialties())).append("\n\n");
        }
        
        // Key Employees
        if (!linkedin.getEmployees().isEmpty()) {
            sb.append("### Key Employees & Leadership\n\n");
            
            // Founders first
            linkedin.getEmployees().stream()
                .filter(LinkedInEmployee::isFounder)
                .forEach(emp -> {
                    sb.append("#### ").append(emp.getName());
                    if (emp.isFounder()) {
                        sb.append(" 👔 (Founder)");
                    }
                    sb.append("\n\n");
                    sb.append("**Title:** ").append(emp.getTitle()).append("\n\n");
                    if (emp.getDepartment() != null) {
                        sb.append("**Department:** ").append(emp.getDepartment()).append("\n\n");
                    }
                    if (emp.getLinkedInUrl() != null) {
                        sb.append("**LinkedIn:** ").append(emp.getLinkedInUrl()).append("\n\n");
                    }
                    if (emp.getNotableInfo() != null) {
                        sb.append("*").append(emp.getNotableInfo()).append("*\n\n");
                    }
                    sb.append("---\n\n");
                });
            
            // Then executives
            linkedin.getEmployees().stream()
                .filter(emp -> !emp.isFounder() && "executive".equals(emp.getSeniority()))
                .forEach(emp -> {
                    sb.append("#### ").append(emp.getName()).append("\n\n");
                    sb.append("**Title:** ").append(emp.getTitle()).append("\n\n");
                    if (emp.getDepartment() != null) {
                        sb.append("**Department:** ").append(emp.getDepartment()).append("\n\n");
                    }
                    if (emp.getLinkedInUrl() != null) {
                        sb.append("**LinkedIn:** ").append(emp.getLinkedInUrl()).append("\n\n");
                    }
                    if (emp.getNotableInfo() != null) {
                        sb.append("*").append(emp.getNotableInfo()).append("*\n\n");
                    }
                    sb.append("---\n\n");
                });
            
            // Organizational breakdown
            if (!linkedin.getDepartmentCounts().isEmpty()) {
                sb.append("### Organizational Structure\n\n");
                sb.append("**Key Employees by Department:**\n\n");
                linkedin.getDepartmentCounts().entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .forEach(entry -> {
                        sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    });
                sb.append("\n");
            }
        }
        
        // Content & Engagement
        if (linkedin.getPostingFrequency() != null || !linkedin.getContentThemes().isEmpty()) {
            sb.append("### LinkedIn Activity & Content\n\n");
            
            if (linkedin.getPostingFrequency() != null) {
                sb.append("**Posting Frequency:** ").append(linkedin.getPostingFrequency().replace("_", " ")).append("\n\n");
            }
            
            if (!linkedin.getContentThemes().isEmpty()) {
                sb.append("**Main Content Themes:**\n\n");
                linkedin.getContentThemes().forEach(theme -> 
                    sb.append("- ").append(theme).append("\n"));
                sb.append("\n");
            }
            
            if (!linkedin.getThoughtLeadershipAreas().isEmpty()) {
                sb.append("**Thought Leadership Areas:**\n\n");
                linkedin.getThoughtLeadershipAreas().forEach(area -> 
                    sb.append("- ").append(area).append("\n"));
                sb.append("\n");
            }
            
            if (linkedin.getContentStrategy() != null) {
                sb.append("**Content Strategy:**\n\n");
                sb.append(linkedin.getContentStrategy()).append("\n\n");
            }
        }
        
        // Recent Posts
        if (!linkedin.getRecentPosts().isEmpty()) {
            sb.append("### Recent LinkedIn Posts\n\n");
            linkedin.getRecentPosts().forEach(post -> {
                sb.append("#### ").append(post.getTopic());
                if (post.getEngagementLevel() != null) {
                    sb.append(" [").append(post.getEngagementLevel()).append(" engagement]");
                }
                sb.append("\n\n");
                sb.append(post.getDescription()).append("\n\n");
                sb.append("---\n\n");
            });
        }
        
        // Recent Updates
        if (!linkedin.getRecentUpdates().isEmpty()) {
            sb.append("### Recent Company Updates\n\n");
            linkedin.getRecentUpdates().forEach(update -> 
                sb.append("- ").append(update).append("\n"));
            sb.append("\n");
        }
        
        sb.append("\n");
    }
}
