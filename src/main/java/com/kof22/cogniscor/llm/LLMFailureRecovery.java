package com.kof22.cogniscor.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.model.SourceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses LLM to analyze failures and suggest recovery strategies.
 */
public class LLMFailureRecovery {
    private static final Logger logger = LoggerFactory.getLogger(LLMFailureRecovery.class);
    private final OpenAIService openAIService;
    private final Gson gson;
    
    public LLMFailureRecovery(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.gson = new Gson();
    }
    
    /**
     * Analyze failed sources and get recovery suggestions.
     */
    public RecoveryPlan analyzeFailures(String companyName, String websiteUrl, List<SourceData> failedSources) {
        if (!openAIService.isEnabled() || failedSources.isEmpty()) {
            return null;
        }
        
        logger.info("Analyzing {} failed sources for recovery strategies", failedSources.size());
        
        // Build failure summary
        StringBuilder failureSummary = new StringBuilder();
        for (SourceData source : failedSources) {
            failureSummary.append(String.format("- %s (%s): %s\n", 
                source.getSourceType(),
                source.getSourceUrl(),
                source.getErrorMessage()));
        }
        
        String systemPrompt = """
            You are a web scraping troubleshooting expert. Analyze why data collection
            failed and provide specific recovery strategies.
            
            Respond with a JSON object:
            {
              "diagnosis": "Why the failures likely occurred",
              "alternativeUrls": [
                {
                  "url": "alternative URL to try",
                  "reason": "why this might work better"
                }
              ],
              "searchQueries": ["query 1", "query 2"],
              "recommendations": ["specific action 1", "specific action 2"]
            }
            """;
        
        String userPrompt = String.format(
            """
            Company: %s
            Official Website: %s
            
            Failed Sources:
            %s
            
            Analyze these failures and suggest:
            1. Why they failed (rate limiting, wrong URLs, authentication, etc.)
            2. Alternative URLs to try
            3. Search queries to find the right pages
            4. Other recovery strategies
            """,
            companyName,
            websiteUrl,
            failureSummary.toString()
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                String json = extractJson(response);
                JsonObject obj = gson.fromJson(json, JsonObject.class);
                
                RecoveryPlan plan = new RecoveryPlan();
                plan.diagnosis = obj.get("diagnosis").getAsString();
                
                // Parse alternative URLs
                if (obj.has("alternativeUrls")) {
                    JsonArray urls = obj.getAsJsonArray("alternativeUrls");
                    for (int i = 0; i < urls.size(); i++) {
                        JsonObject urlObj = urls.get(i).getAsJsonObject();
                        AlternativeUrl altUrl = new AlternativeUrl();
                        altUrl.url = urlObj.get("url").getAsString();
                        altUrl.reason = urlObj.get("reason").getAsString();
                        plan.alternativeUrls.add(altUrl);
                    }
                }
                
                // Parse search queries
                if (obj.has("searchQueries")) {
                    JsonArray queries = obj.getAsJsonArray("searchQueries");
                    for (int i = 0; i < queries.size(); i++) {
                        plan.searchQueries.add(queries.get(i).getAsString());
                    }
                }
                
                // Parse recommendations
                if (obj.has("recommendations")) {
                    JsonArray recs = obj.getAsJsonArray("recommendations");
                    for (int i = 0; i < recs.size(); i++) {
                        plan.recommendations.add(recs.get(i).getAsString());
                    }
                }
                
                logger.info("Recovery plan generated with {} alternative URLs and {} recommendations",
                    plan.alternativeUrls.size(), plan.recommendations.size());
                
                return plan;
                
            } catch (Exception e) {
                logger.error("Error parsing recovery plan: {}", e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Get LLM suggestions for specific missing information.
     */
    public List<String> suggestUrlsForMissingInfo(String companyName, String missingInfoType) {
        if (!openAIService.isEnabled()) {
            return new ArrayList<>();
        }
        
        logger.info("Getting URL suggestions for missing: {}", missingInfoType);
        
        String systemPrompt = """
            You are a research expert. Suggest specific URLs where we can find the requested
            information about a company. Respond with a JSON array of URLs:
            ["url1", "url2", "url3"]
            """;
        
        String userPrompt = String.format(
            """
            Company: %s
            Missing Information: %s
            
            Suggest 3-5 specific URLs or search queries where we can find this information.
            Be as specific as possible about where this information typically appears.
            """,
            companyName,
            missingInfoType
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                String json = extractJson(response);
                JsonArray array = gson.fromJson(json, JsonArray.class);
                List<String> urls = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    urls.add(array.get(i).getAsString());
                }
                return urls;
            } catch (Exception e) {
                logger.error("Error parsing URL suggestions: {}", e.getMessage());
            }
        }
        
        return new ArrayList<>();
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
     * Recovery plan with alternative strategies.
     */
    public static class RecoveryPlan {
        public String diagnosis;
        public List<AlternativeUrl> alternativeUrls = new ArrayList<>();
        public List<String> searchQueries = new ArrayList<>();
        public List<String> recommendations = new ArrayList<>();
    }
    
    /**
     * Alternative URL with reasoning.
     */
    public static class AlternativeUrl {
        public String url;
        public String reason;
        
        @Override
        public String toString() {
            return url + " (" + reason + ")";
        }
    }
}
