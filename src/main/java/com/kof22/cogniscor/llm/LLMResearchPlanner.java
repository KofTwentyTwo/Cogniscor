package com.kof22.cogniscor.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kof22.cogniscor.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses LLM to intelligently plan what sources to scrape for a company.
 */
public class LLMResearchPlanner {
    private static final Logger logger = LoggerFactory.getLogger(LLMResearchPlanner.class);
    private final OpenAIService openAIService;
    private final Configuration config;
    private final Gson gson;
    
    public LLMResearchPlanner(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.config = Configuration.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Get LLM-suggested URLs and sources to scrape for a company.
     */
    public ResearchPlan planResearch(String companyName, String websiteUrl) {
        if (!openAIService.isEnabled() || !config.getBooleanProperty("llm.plan.sources", false)) {
            logger.debug("LLM source planning is disabled");
            return null;
        }
        
        logger.info("Using LLM to plan research sources for: {}", companyName);
        
        String systemPrompt = """
            You are a research assistant helping to gather information about companies.
            Your task is to suggest specific URLs and sources that would provide valuable 
            information about a company for someone preparing for a job interview.
            
            Focus on finding:
            - Official company information (products, technology, team)
            - Recent news and developments
            - Company culture and reviews
            - Technical blog posts and engineering blogs
            - Press releases and announcements
            
            Respond with a JSON object containing:
            {
              "suggestedUrls": [
                {"url": "https://...", "description": "What to find here", "priority": "high|medium|low"}
              ],
              "searchQueries": ["query to find recent news", "query to find tech stack"],
              "insights": "Any insights about what information would be most valuable"
            }
            """;
        
        String userPrompt = String.format(
            "Company: %s\nWebsite: %s\n\nSuggest the best sources and URLs to research this company thoroughly.",
            companyName, websiteUrl
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                return parseResearchPlan(response);
            } catch (Exception e) {
                logger.error("Error parsing LLM research plan: {}", e.getMessage());
            }
        }
        
        return null;
    }
    
    private ResearchPlan parseResearchPlan(String response) {
        // Extract JSON from response (may have markdown code blocks)
        String json = response;
        if (response.contains("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.indexOf("```", start);
            json = response.substring(start, end).trim();
        } else if (response.contains("```")) {
            int start = response.indexOf("```") + 3;
            int end = response.indexOf("```", start);
            json = response.substring(start, end).trim();
        }
        
        JsonObject obj = gson.fromJson(json, JsonObject.class);
        ResearchPlan plan = new ResearchPlan();
        
        // Parse suggested URLs
        if (obj.has("suggestedUrls")) {
            JsonArray urls = obj.getAsJsonArray("suggestedUrls");
            for (int i = 0; i < urls.size(); i++) {
                JsonObject urlObj = urls.get(i).getAsJsonObject();
                String url = urlObj.get("url").getAsString();
                String description = urlObj.has("description") ? urlObj.get("description").getAsString() : "";
                String priority = urlObj.has("priority") ? urlObj.get("priority").getAsString() : "medium";
                plan.addSuggestedUrl(url, description, priority);
            }
        }
        
        // Parse search queries
        if (obj.has("searchQueries")) {
            JsonArray queries = obj.getAsJsonArray("searchQueries");
            for (int i = 0; i < queries.size(); i++) {
                plan.addSearchQuery(queries.get(i).getAsString());
            }
        }
        
        // Parse insights
        if (obj.has("insights")) {
            plan.setInsights(obj.get("insights").getAsString());
        }
        
        logger.info("LLM suggested {} URLs and {} search queries", 
            plan.getSuggestedUrls().size(), plan.getSearchQueries().size());
        
        return plan;
    }
    
    /**
     * Research plan with LLM suggestions.
     */
    public static class ResearchPlan {
        private final List<SuggestedSource> suggestedUrls = new ArrayList<>();
        private final List<String> searchQueries = new ArrayList<>();
        private String insights;
        
        public void addSuggestedUrl(String url, String description, String priority) {
            suggestedUrls.add(new SuggestedSource(url, description, priority));
        }
        
        public void addSearchQuery(String query) {
            searchQueries.add(query);
        }
        
        public void setInsights(String insights) {
            this.insights = insights;
        }
        
        public List<SuggestedSource> getSuggestedUrls() {
            return suggestedUrls;
        }
        
        public List<String> getSearchQueries() {
            return searchQueries;
        }
        
        public String getInsights() {
            return insights;
        }
    }
    
    /**
     * A suggested source from the LLM.
     */
    public static class SuggestedSource {
        private final String url;
        private final String description;
        private final String priority;
        
        public SuggestedSource(String url, String description, String priority) {
            this.url = url;
            this.description = description;
            this.priority = priority;
        }
        
        public String getUrl() {
            return url;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getPriority() {
            return priority;
        }
    }
}
