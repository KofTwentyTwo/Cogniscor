package com.kof22.cogniscor.linkedin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kof22.cogniscor.llm.OpenAIService;
import com.kof22.cogniscor.model.SourceData;
import com.kof22.cogniscor.scraper.WebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * LinkedIn Intelligence Module
 * 
 * Performs deep intelligence gathering from LinkedIn:
 * - Discovers the correct LinkedIn company page
 * - Gathers company information, news, and updates
 * - Maps employee network and organizational structure
 * - Collects articles, posts, and thought leadership content
 * 
 * Strategy: Uses LLM to discover real LinkedIn URLs since direct scraping
 * is limited by LinkedIn's anti-bot protections.
 */
public class LinkedInIntelligence {
    private static final Logger logger = LoggerFactory.getLogger(LinkedInIntelligence.class);
    private final OpenAIService openAIService;
    private final WebScraper webScraper;
    private final Gson gson = new Gson();
    
    public LinkedInIntelligence(OpenAIService openAIService, WebScraper webScraper) {
        this.openAIService = openAIService;
        this.webScraper = webScraper;
    }
    
    /**
     * Performs comprehensive LinkedIn intelligence gathering for a company.
     */
    public LinkedInData gatherIntelligence(String companyName, String websiteUrl, String mainSiteContent) {
        logger.info("Starting LinkedIn intelligence gathering for: {}", companyName);
        
        LinkedInData data = new LinkedInData(companyName);
        
        // Phase 1: Discover the correct LinkedIn company URL
        String linkedInUrl = discoverLinkedInUrl(companyName, websiteUrl, mainSiteContent);
        if (linkedInUrl == null) {
            logger.warn("Could not discover LinkedIn URL for {}", companyName);
            return data;
        }
        
        data.setCompanyUrl(linkedInUrl);
        logger.info("Discovered LinkedIn URL: {}", linkedInUrl);
        
        // Phase 2: Scrape the LinkedIn company page
        SourceData companyPage = webScraper.scrapeUrl(
            linkedInUrl,
            companyName + " - LinkedIn Company Page",
            "linkedin_company"
        );
        
        if (!companyPage.isSuccessful()) {
            logger.warn("Failed to scrape LinkedIn company page: {}", companyPage.getErrorMessage());
            // Even if scraping fails, we can still use LLM to extract information
        }
        
        // Phase 3: Use LLM to extract structured information from LinkedIn
        extractCompanyInformation(data, companyPage.getContent(), companyName);
        
        // Phase 4: Discover and gather key employees
        discoverKeyEmployees(data, companyName, websiteUrl);
        
        // Phase 5: Gather company posts and thought leadership
        gatherCompanyContent(data, companyName);
        
        // Phase 6: Analyze employee network and organizational structure
        analyzeOrganizationalStructure(data);
        
        logger.info("LinkedIn intelligence gathering complete for {}", companyName);
        return data;
    }
    
    /**
     * Uses LLM to discover the correct LinkedIn company URL.
     * The LLM can use its knowledge to find the actual URL rather than guessing.
     */
    private String discoverLinkedInUrl(String companyName, String websiteUrl, String mainSiteContent) {
        if (!openAIService.isEnabled()) {
            logger.debug("LLM is disabled, cannot discover LinkedIn URL");
            return null;
        }
        
        String systemPrompt = """
            You are a LinkedIn research expert. Your job is to find the ACTUAL LinkedIn company page URL
            for a given company.
            
            CRITICAL RULES:
            1. Use your knowledge of this specific company to find their real LinkedIn URL
            2. Do NOT guess URL patterns - only provide URLs you believe actually exist
            3. LinkedIn company URLs follow the format: https://www.linkedin.com/company/{slug}/
            4. Consider company name variations, acquisitions, and rebrandings
            5. If the company is too small or doesn't have a LinkedIn presence, respond with null
            
            Respond with ONLY the LinkedIn URL or the word "null" if not found.
            """;
        
        String userPrompt = String.format("""
            Company Name: %s
            Website: %s
            
            Website Content (first 1000 chars):
            %s
            
            What is the actual LinkedIn company page URL for this company?
            Respond with ONLY the URL or "null".
            """,
            companyName,
            websiteUrl,
            mainSiteContent.substring(0, Math.min(1000, mainSiteContent.length()))
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null && !response.trim().equalsIgnoreCase("null") && response.contains("linkedin.com")) {
            String url = response.trim();
            // Ensure it ends with a slash
            if (!url.endsWith("/")) {
                url += "/";
            }
            return url;
        }
        
        return null;
    }
    
    /**
     * Extracts structured company information from LinkedIn page content.
     */
    private void extractCompanyInformation(LinkedInData data, String pageContent, String companyName) {
        if (!openAIService.isEnabled() || pageContent == null || pageContent.isEmpty()) {
            return;
        }
        
        String systemPrompt = """
            You are analyzing LinkedIn company page content. Extract structured information.
            
            Respond with JSON:
            {
              "employee_count": "approximate number or range (e.g., '51-200', '1000+')",
              "headquarters": "city, state/country",
              "industry": "primary industry",
              "specialties": ["specialty1", "specialty2"],
              "founded_year": "year or null",
              "company_size": "startup|small|medium|large|enterprise",
              "tagline": "company tagline/description",
              "key_updates": ["recent update 1", "recent update 2"]
            }
            """;
        
        String userPrompt = String.format("""
            Company: %s
            
            LinkedIn Page Content:
            %s
            
            Extract the structured information as JSON.
            """,
            companyName,
            pageContent.substring(0, Math.min(3000, pageContent.length()))
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                JsonObject json = gson.fromJson(response, JsonObject.class);
                if (json.has("employee_count")) data.setEmployeeCount(json.get("employee_count").getAsString());
                if (json.has("headquarters")) data.setHeadquarters(json.get("headquarters").getAsString());
                if (json.has("industry")) data.setIndustry(json.get("industry").getAsString());
                if (json.has("founded_year")) data.setFoundedYear(json.get("founded_year").getAsString());
                if (json.has("company_size")) data.setCompanySize(json.get("company_size").getAsString());
                if (json.has("tagline")) data.setTagline(json.get("tagline").getAsString());
                
                // Extract specialties
                if (json.has("specialties")) {
                    JsonArray specialties = json.getAsJsonArray("specialties");
                    for (JsonElement elem : specialties) {
                        data.addSpecialty(elem.getAsString());
                    }
                }
                
                // Extract key updates
                if (json.has("key_updates")) {
                    JsonArray updates = json.getAsJsonArray("key_updates");
                    for (JsonElement elem : updates) {
                        data.addRecentUpdate(elem.getAsString());
                    }
                }
                
                logger.info("Extracted LinkedIn company information for {}", companyName);
            } catch (Exception e) {
                logger.warn("Failed to parse LinkedIn company information: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Discovers key employees and leadership team.
     */
    private void discoverKeyEmployees(LinkedInData data, String companyName, String websiteUrl) {
        if (!openAIService.isEnabled()) {
            return;
        }
        
        logger.info("Discovering key employees for {}", companyName);
        
        String systemPrompt = """
            You are a LinkedIn researcher discovering key employees at a company.
            
            Based on your knowledge, identify 10-15 key people at this company including:
            - C-suite executives (CEO, CTO, CFO, etc.)
            - VPs and Directors in key departments
            - Notable thought leaders or public faces
            - Founders (if still with company)
            
            Respond with JSON array:
            [
              {
                "name": "Full Name",
                "title": "Job Title",
                "department": "Engineering|Sales|Marketing|Product|Operations|Executive|Other",
                "linkedin_url": "their LinkedIn profile URL if known, else null",
                "seniority": "executive|senior|mid|entry",
                "is_founder": true/false,
                "notable_info": "brief note about why they're notable"
              }
            ]
            
            Only include people you have actual knowledge of. Don't make up names.
            """;
        
        String userPrompt = String.format("""
            Company: %s
            Website: %s
            
            Who are the key employees and leaders at this company?
            Return JSON array of key people (10-15 max).
            """,
            companyName,
            websiteUrl
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                JsonArray employees = gson.fromJson(response, JsonArray.class);
                for (JsonElement elem : employees) {
                    JsonObject emp = elem.getAsJsonObject();
                    
                    LinkedInEmployee employee = new LinkedInEmployee(
                        emp.get("name").getAsString(),
                        emp.get("title").getAsString()
                    );
                    
                    if (emp.has("department")) employee.setDepartment(emp.get("department").getAsString());
                    if (emp.has("linkedin_url") && !emp.get("linkedin_url").isJsonNull()) {
                        employee.setLinkedInUrl(emp.get("linkedin_url").getAsString());
                    }
                    if (emp.has("seniority")) employee.setSeniority(emp.get("seniority").getAsString());
                    if (emp.has("is_founder")) employee.setFounder(emp.get("is_founder").getAsBoolean());
                    if (emp.has("notable_info")) employee.setNotableInfo(emp.get("notable_info").getAsString());
                    
                    data.addEmployee(employee);
                }
                
                logger.info("Discovered {} key employees for {}", data.getEmployees().size(), companyName);
            } catch (Exception e) {
                logger.warn("Failed to parse employee information: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Gathers company posts, articles, and thought leadership content.
     */
    private void gatherCompanyContent(LinkedInData data, String companyName) {
        if (!openAIService.isEnabled()) {
            return;
        }
        
        logger.info("Gathering LinkedIn content for {}", companyName);
        
        String systemPrompt = """
            You are analyzing LinkedIn company activity and content.
            
            Based on your knowledge of this company's LinkedIn presence, describe:
            - Recent company posts and updates
            - Articles or thought leadership published
            - Engagement themes (what they talk about)
            - Notable announcements or milestones
            
            Respond with JSON:
            {
              "posting_frequency": "very_active|active|occasional|rare",
              "main_themes": ["theme1", "theme2", "theme3"],
              "recent_posts": [
                {
                  "topic": "post topic/title",
                  "description": "brief description",
                  "engagement_level": "high|medium|low"
                }
              ],
              "thought_leadership_areas": ["area1", "area2"],
              "content_strategy": "description of their LinkedIn content approach"
            }
            """;
        
        String userPrompt = String.format("""
            Company: %s
            
            What is this company's LinkedIn content and posting activity like?
            Return JSON with content analysis.
            """,
            companyName
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                JsonObject content = gson.fromJson(response, JsonObject.class);
                
                if (content.has("posting_frequency")) {
                    data.setPostingFrequency(content.get("posting_frequency").getAsString());
                }
                if (content.has("content_strategy")) {
                    data.setContentStrategy(content.get("content_strategy").getAsString());
                }
                
                // Extract themes
                if (content.has("main_themes")) {
                    JsonArray themes = content.getAsJsonArray("main_themes");
                    for (JsonElement elem : themes) {
                        data.addContentTheme(elem.getAsString());
                    }
                }
                
                // Extract thought leadership areas
                if (content.has("thought_leadership_areas")) {
                    JsonArray tlAreas = content.getAsJsonArray("thought_leadership_areas");
                    for (JsonElement elem : tlAreas) {
                        data.addThoughtLeadershipArea(elem.getAsString());
                    }
                }
                
                // Extract recent posts
                if (content.has("recent_posts")) {
                    JsonArray posts = content.getAsJsonArray("recent_posts");
                    for (JsonElement elem : posts) {
                        JsonObject post = elem.getAsJsonObject();
                        LinkedInPost linkedInPost = new LinkedInPost(
                            post.get("topic").getAsString(),
                            post.get("description").getAsString()
                        );
                        if (post.has("engagement_level")) {
                            linkedInPost.setEngagementLevel(post.get("engagement_level").getAsString());
                        }
                        data.addPost(linkedInPost);
                    }
                }
                
                logger.info("Gathered LinkedIn content analysis for {}", companyName);
            } catch (Exception e) {
                logger.warn("Failed to parse LinkedIn content: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Analyzes organizational structure based on employee data.
     */
    private void analyzeOrganizationalStructure(LinkedInData data) {
        if (data.getEmployees().isEmpty()) {
            return;
        }
        
        logger.info("Analyzing organizational structure");
        
        // Count by department
        data.getEmployees().stream()
            .filter(e -> e.getDepartment() != null)
            .forEach(e -> data.incrementDepartmentCount(e.getDepartment()));
        
        // Count by seniority
        data.getEmployees().stream()
            .filter(e -> e.getSeniority() != null)
            .forEach(e -> data.incrementSeniorityCount(e.getSeniority()));
        
        // Identify founders
        long founderCount = data.getEmployees().stream()
            .filter(LinkedInEmployee::isFounder)
            .count();
        
        logger.info("Organizational analysis: {} total employees tracked, {} founders identified",
            data.getEmployees().size(), founderCount);
    }
}
