package com.kof22.cogniscor.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kof22.cogniscor.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses LLM to verify we found the right company and suggest corrections.
 */
public class LLMCompanyVerifier {
    private static final Logger logger = LoggerFactory.getLogger(LLMCompanyVerifier.class);
    private final OpenAIService openAIService;
    private final Configuration config;
    private final Gson gson;
    
    public LLMCompanyVerifier(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.config = Configuration.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Verify company and get corrected information if needed.
     */
    public CompanyVerification verifyCompany(String companyName, String websiteUrl, String scrapedContent) {
        if (!openAIService.isEnabled()) {
            logger.debug("LLM company verification is disabled");
            return null;
        }
        
        logger.info("Using LLM to verify company: {}", companyName);
        
        String systemPrompt = """
            You are a company research expert. Your task is to verify if the scraped content
            matches the intended company and suggest corrections if needed.
            
            Analyze the provided information and respond with a JSON object:
            {
              "isCorrectCompany": true/false,
              "confidence": "high|medium|low",
              "actualCompanyName": "corrected name if different",
              "reasoning": "brief explanation",
              "suggestedWebsite": "corrected URL if the provided one was wrong"
            }
            """;
        
        String userPrompt = String.format(
            """
            Intended Company: %s
            Provided URL: %s
            
            Scraped Content Preview (first 500 chars):
            %s
            
            Is this the correct company? If not, what should we search for instead?
            """,
            companyName,
            websiteUrl,
            truncate(scrapedContent, 500)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                String json = extractJson(response);
                JsonObject obj = gson.fromJson(json, JsonObject.class);
                
                CompanyVerification verification = new CompanyVerification();
                verification.isCorrectCompany = obj.get("isCorrectCompany").getAsBoolean();
                verification.confidence = obj.get("confidence").getAsString();
                verification.actualCompanyName = obj.has("actualCompanyName") ? 
                    obj.get("actualCompanyName").getAsString() : companyName;
                verification.reasoning = obj.get("reasoning").getAsString();
                verification.suggestedWebsite = obj.has("suggestedWebsite") ? 
                    obj.get("suggestedWebsite").getAsString() : websiteUrl;
                
                logger.info("Company verification: {} (confidence: {})", 
                    verification.isCorrectCompany ? "MATCH" : "MISMATCH", 
                    verification.confidence);
                
                return verification;
            } catch (Exception e) {
                logger.error("Error parsing verification response: {}", e.getMessage());
            }
        }
        
        return null;
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
     * Company verification result.
     */
    public static class CompanyVerification {
        public boolean isCorrectCompany;
        public String confidence;
        public String actualCompanyName;
        public String reasoning;
        public String suggestedWebsite;
    }
}
