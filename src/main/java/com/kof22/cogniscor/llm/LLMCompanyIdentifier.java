package com.kof22.cogniscor.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses LLM to identify the company from website content.
 */
public class LLMCompanyIdentifier {
    private static final Logger logger = LoggerFactory.getLogger(LLMCompanyIdentifier.class);
    private final OpenAIService openAIService;
    private final Gson gson;
    
    public LLMCompanyIdentifier(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.gson = new Gson();
    }
    
    /**
     * Identify the company name and key details from website content.
     */
    public CompanyIdentity identifyCompany(String websiteUrl, String content) {
        if (!openAIService.isEnabled()) {
            logger.debug("LLM company identification is disabled");
            return null;
        }
        
        logger.info("Using LLM to identify company from website: {}", websiteUrl);
        
        String systemPrompt = """
            You are an expert at identifying companies from their website content.
            Your task is to extract the exact company name and verify its identity.
            
            Respond with a JSON object:
            {
              "companyName": "The exact official company name",
              "alternativeNames": ["alternate names", "DBA names", "former names"],
              "industry": "Primary industry",
              "confidence": "high|medium|low",
              "reasoning": "Brief explanation of how you identified the company"
            }
            
            Be precise with the company name - use the exact legal/official name if found.
            """;
        
        String userPrompt = String.format(
            """
            Website URL: %s
            
            Website Content (first 2000 chars):
            %s
            
            Identify the company name and provide details about this company.
            """,
            websiteUrl,
            truncate(content, 2000)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                String json = extractJson(response);
                JsonObject obj = gson.fromJson(json, JsonObject.class);
                
                CompanyIdentity identity = new CompanyIdentity();
                identity.companyName = obj.get("companyName").getAsString();
                identity.confidence = obj.get("confidence").getAsString();
                identity.reasoning = obj.has("reasoning") ? obj.get("reasoning").getAsString() : "";
                identity.industry = obj.has("industry") ? obj.get("industry").getAsString() : null;
                
                // Parse alternative names
                if (obj.has("alternativeNames")) {
                    var altNames = obj.getAsJsonArray("alternativeNames");
                    for (int i = 0; i < altNames.size(); i++) {
                        identity.alternativeNames.add(altNames.get(i).getAsString());
                    }
                }
                
                logger.info("Identified company: '{}' (confidence: {})", 
                    identity.companyName, identity.confidence);
                
                return identity;
                
            } catch (Exception e) {
                logger.error("Error parsing company identification: {}", e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Verify that content is about the expected company.
     */
    public boolean verifyContentMatchesCompany(String expectedCompanyName, 
                                               String content, 
                                               String sourceUrl,
                                               String sourceType) {
        if (!openAIService.isEnabled()) {
            return true; // Assume match if LLM disabled
        }
        
        String systemPrompt = """
            You are verifying if content is about a specific company.
            Respond with ONLY "YES" or "NO" followed by a brief reason.
            
            Example: "YES - Content clearly discusses [Company Name]'s products and services"
            Example: "NO - This content is about a different company called [Other Name]"
            """;
        
        String userPrompt = String.format(
            """
            Expected Company: %s
            Source: %s (%s)
            
            Content Preview (first 1000 chars):
            %s
            
            Is this content about %s?
            """,
            expectedCompanyName,
            sourceUrl,
            sourceType,
            truncate(content, 1000),
            expectedCompanyName
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            boolean matches = response.toUpperCase().startsWith("YES");
            logger.info("Content verification for {}: {} - {}", 
                sourceType, matches ? "MATCH" : "MISMATCH", 
                response.substring(0, Math.min(100, response.length())));
            return matches;
        }
        
        return true; // Default to accepting if verification fails
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
     * Company identity information.
     */
    public static class CompanyIdentity {
        public String companyName;
        public java.util.List<String> alternativeNames = new java.util.ArrayList<>();
        public String industry;
        public String confidence;
        public String reasoning;
    }
}
