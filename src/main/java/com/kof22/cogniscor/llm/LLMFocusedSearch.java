package com.kof22.cogniscor.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses LLM to perform focused searches based on user requests.
 */
public class LLMFocusedSearch {
    private static final Logger logger = LoggerFactory.getLogger(LLMFocusedSearch.class);
    private final OpenAIService openAIService;
    private final Gson gson;
    
    public LLMFocusedSearch(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.gson = new Gson();
    }
    
    /**
     * Get focused search URLs based on user's custom request.
     */
    public FocusedSearchPlan planFocusedSearch(String companyName, 
                                                String websiteUrl, 
                                                String userRequest,
                                                String existingContent) {
        if (!openAIService.isEnabled()) {
            logger.debug("LLM focused search is disabled");
            return null;
        }
        
        logger.info("Planning focused search for: {}", userRequest);
        
        String systemPrompt = """
            You are a research expert helping to find specific information about a company.
            The user has made a specific request for additional information.
            
            Your task is to suggest:
            1. Specific URLs where this information might be found
            2. Search queries to find this information
            3. What to look for in the content
            
            Respond with a JSON object:
            {
              "urls": [
                {
                  "url": "specific URL to try",
                  "reason": "why this URL might have the requested info"
                }
              ],
              "searchQueries": ["query 1", "query 2"],
              "extractionFocus": "What specific things to extract from the content",
              "reportEnhancement": "How to incorporate this into the executive summary"
            }
            """;
        
        String userPrompt = String.format(
            """
            Company: %s
            Website: %s
            
            User's Request: "%s"
            
            Current Information Summary:
            %s
            
            Suggest specific URLs and search strategies to find the requested information.
            Be specific and actionable.
            """,
            companyName,
            websiteUrl,
            userRequest,
            truncate(existingContent, 1000)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                String json = extractJson(response);
                JsonObject obj = gson.fromJson(json, JsonObject.class);
                
                FocusedSearchPlan plan = new FocusedSearchPlan();
                plan.userRequest = userRequest;
                
                // Parse URLs
                if (obj.has("urls")) {
                    JsonArray urls = obj.getAsJsonArray("urls");
                    for (int i = 0; i < urls.size(); i++) {
                        JsonObject urlObj = urls.get(i).getAsJsonObject();
                        String url = urlObj.get("url").getAsString();
                        String reason = urlObj.has("reason") ? urlObj.get("reason").getAsString() : "";
                        plan.addUrl(url, reason);
                    }
                }
                
                // Parse search queries
                if (obj.has("searchQueries")) {
                    JsonArray queries = obj.getAsJsonArray("searchQueries");
                    for (int i = 0; i < queries.size(); i++) {
                        plan.searchQueries.add(queries.get(i).getAsString());
                    }
                }
                
                // Parse extraction focus
                if (obj.has("extractionFocus")) {
                    plan.extractionFocus = obj.get("extractionFocus").getAsString();
                }
                
                // Parse report enhancement
                if (obj.has("reportEnhancement")) {
                    plan.reportEnhancement = obj.get("reportEnhancement").getAsString();
                }
                
                logger.info("Focused search plan created with {} URLs and {} queries",
                    plan.urls.size(), plan.searchQueries.size());
                
                return plan;
                
            } catch (Exception e) {
                logger.error("Error parsing focused search plan: {}", e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Enhance report based on newly collected focused information.
     */
    public String enhanceReport(String originalReport, 
                                String focusedContent, 
                                String userRequest,
                                String companyName) {
        if (!openAIService.isEnabled()) {
            return originalReport;
        }
        
        logger.info("Enhancing report based on user request");
        
        String systemPrompt = """
            You are an expert technical writer enhancing a company research report.
            
            The user requested specific additional information. You now have that information
            and need to integrate it into the executive summary.
            
            Requirements:
            1. Add a new section or enhance existing sections with the new information
            2. Keep the markdown format and structure
            3. Maintain professional tone
            4. Only include facts from the provided content
            5. Clearly indicate what information was added based on the user's request
            6. Keep the summary concise (1-2 pages)
            
            Return the enhanced executive summary in markdown format.
            """;
        
        String userPrompt = String.format(
            """
            Company: %s
            
            User's Request: "%s"
            
            Original Executive Summary:
            %s
            
            Newly Collected Information:
            %s
            
            Enhance the executive summary by incorporating the newly collected information
            that addresses the user's request.
            """,
            companyName,
            userRequest,
            truncate(originalReport, 3000),
            truncate(focusedContent, 2000)
        );
        
        String enhanced = openAIService.complete(systemPrompt, userPrompt);
        
        if (enhanced != null && !enhanced.trim().isEmpty()) {
            logger.info("Report enhanced with focused information");
            return enhanced.trim();
        }
        
        logger.warn("Enhancement failed, returning original report");
        return originalReport;
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
    
    private String extractJson(String response) {
        String json = response.trim();
        if (json.contains("```json")) {
            int start = json.indexOf("```json") + 7;
            int end = json.indexOf("```", start);
            json = json.substring(start, end).trim();
        } else if (json.contains("```")) {
            int start = json.indexOf("```") + 3;
            int end = json.indexOf("```", start);
            json = json.substring(start, end).trim();
        }
        return json;
    }
    
    /**
     * Focused search plan based on user request.
     */
    public static class FocusedSearchPlan {
        public String userRequest;
        public List<FocusedUrl> urls = new ArrayList<>();
        public List<String> searchQueries = new ArrayList<>();
        public String extractionFocus;
        public String reportEnhancement;
        
        public void addUrl(String url, String reason) {
            urls.add(new FocusedUrl(url, reason));
        }
    }
    
    /**
     * URL for focused search.
     */
    public static class FocusedUrl {
        public final String url;
        public final String reason;
        
        public FocusedUrl(String url, String reason) {
            this.url = url;
            this.reason = reason;
        }
        
        @Override
        public String toString() {
            return url + " (" + reason + ")";
        }
    }
}
