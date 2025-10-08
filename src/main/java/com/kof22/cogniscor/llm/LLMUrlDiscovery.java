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
 * Uses LLM to discover the best URLs to scrape for company information.
 */
public class LLMUrlDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(LLMUrlDiscovery.class);
    private final OpenAIService openAIService;
    private final Configuration config;
    private final Gson gson;
    
    public LLMUrlDiscovery(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.config = Configuration.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Ask LLM to suggest URLs for finding specific information about a company.
     */
    public List<UrlSuggestion> discoverUrls(String companyName, String websiteUrl, List<String> failedSources) {
        if (!openAIService.isEnabled()) {
            logger.debug("LLM URL discovery is disabled");
            return new ArrayList<>();
        }
        
        logger.info("Using LLM to discover URLs for: {}", companyName);
        
        String failureContext = failedSources.isEmpty() ? "None" : String.join(", ", failedSources);
        
        String systemPrompt = """
            You are an expert web researcher who finds REAL, EXISTING URLs for companies.
            
            CRITICAL RULES:
            1. Do NOT guess URL patterns like "/about", "/team", "/blog"
            2. Do NOT make up URLs - only suggest ones you believe actually exist
            3. Use your knowledge of this specific company to find real pages
            4. For external sites (LinkedIn, Wikipedia, Crunchbase), provide ACTUAL profile URLs
            5. Consider real subdomains the company uses (e.g., "blog.stripe.com" not "stripe.com/blog")
            
            Your job is to think like a researcher and find where information ACTUALLY exists:
            - What is this company's actual website structure?
            - Do they have a blog subdomain? (blog.company.com, company.substack.com, etc.)
            - What is their real LinkedIn company slug?
            - Do they have a Wikipedia page? (only notable companies)
            - What careers platform do they use? (jobs.lever.co, careers.company.com, etc.)
            - Where do they actually post news? (company.com/news, newsroom.company.com, etc.)
            
            Respond with a JSON array:
            [
              {
                "url": "actual URL that likely exists",
                "purpose": "what info to find",
                "priority": "high|medium|low",
                "category": "leadership|news|products|tech|culture|financial|external",
                "source_type": "company_site|blog|linkedin|wikipedia|crunchbase|careers|press|social"
              }
            ]
            """;
        
        String userPrompt = String.format(
            """
            Company: %s
            Main Website: %s
            
            Failed sources (don't suggest these again): %s
            
            Based on your knowledge, suggest 15-20 REAL URLs to try:
            
            1. EXTERNAL PROFILES (use actual URLs, not guesses):
               - LinkedIn: Find their actual company profile URL
               - Wikipedia: Only if company is notable enough to have a page
               - Crunchbase: Find their actual organization URL
               - AngelList/Wellfound: If they're a startup
               - Product Hunt: If they're a tech product company
            
            2. COMPANY DOMAINS (find actual subdomains/paths they use):
               - Blog: Where do they actually blog? (e.g., blog.stripe.com, github.blog, medium.com/@company)
               - Careers: What do they actually use? (careers.company.com, jobs.lever.co/company, greenhouse.io)
               - Docs: If tech company, where are real docs? (docs.company.com, help.company.com)
               - News/Press: Do they have a real press page? (company.com/press, newsroom.company.com)
               - About pages: Only if you know the real paths they use
            
            3. RECENT COVERAGE:
               - Recent TechCrunch, Bloomberg, WSJ articles about them
               - Company Twitter/X profile
               - Recent press releases or funding announcements
            
            DO NOT GUESS. Only suggest URLs you believe actually exist for this specific company.
            
            Return ONLY valid JSON, no other text.
            """,
            companyName,
            websiteUrl,
            failureContext
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        
        if (response != null) {
            try {
                String json = extractJson(response);
                JsonArray array = gson.fromJson(json, JsonArray.class);
                
                List<UrlSuggestion> suggestions = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject obj = array.get(i).getAsJsonObject();
                    
                    UrlSuggestion suggestion = new UrlSuggestion();
                    suggestion.url = obj.get("url").getAsString();
                    suggestion.purpose = obj.get("purpose").getAsString();
                    suggestion.priority = obj.has("priority") ? obj.get("priority").getAsString() : "medium";
                    suggestion.category = obj.has("category") ? obj.get("category").getAsString() : "general";
                    suggestion.sourceType = obj.has("source_type") ? obj.get("source_type").getAsString() : "unknown";
                    
                    suggestions.add(suggestion);
                }
                
                logger.info("LLM suggested {} URLs for {}", suggestions.size(), companyName);
                return suggestions;
                
            } catch (Exception e) {
                logger.error("Error parsing URL discovery response: {}", e.getMessage());
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Ask LLM for alternative approaches when sources fail.
     */
    public String suggestAlternatives(String companyName, List<String> failedUrls, String errorSummary) {
        if (!openAIService.isEnabled()) {
            return null;
        }
        
        logger.info("Asking LLM for alternatives after failures");
        
        String systemPrompt = """
            You are a research troubleshooting expert. Analyze why data collection failed
            and suggest specific alternative approaches.
            """;
        
        String userPrompt = String.format(
            """
            Company: %s
            
            Failed URLs:
            %s
            
            Error Summary: %s
            
            What alternative approaches should we try? Consider:
            1. Different URL patterns or subdomains
            2. Alternative data sources (press releases, news sites, etc.)
            3. Search queries that might find the information
            4. Common mistakes in the company name or website
            
            Provide specific, actionable suggestions.
            """,
            companyName,
            String.join("\n", failedUrls),
            errorSummary
        );
        
        return openAIService.complete(systemPrompt, userPrompt);
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
     * URL suggestion from LLM with source type.
     */
    public static class UrlSuggestion {
        public String url;
        public String purpose;
        public String priority;
        public String category;
        public String sourceType;
        
        @Override
        public String toString() {
            return String.format("[%s] %s - %s (%s)", 
                priority.toUpperCase(), url, purpose, sourceType != null ? sourceType : "unknown");
        }
    }
}
