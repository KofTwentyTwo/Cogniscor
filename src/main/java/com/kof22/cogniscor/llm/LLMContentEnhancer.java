package com.kof22.cogniscor.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.model.CompanyData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses LLM to enhance extracted content and fill in gaps.
 */
public class LLMContentEnhancer {
    private static final Logger logger = LoggerFactory.getLogger(LLMContentEnhancer.class);
    private final OpenAIService openAIService;
    private final Configuration config;
    private final Gson gson;
    
    public LLMContentEnhancer(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.config = Configuration.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Use LLM to extract additional structured information from content.
     */
    public void enhanceExtraction(CompanyData companyData, String allContent) {
        if (!openAIService.isEnabled() || !config.getBooleanProperty("llm.enhance.extraction", false)) {
            logger.debug("LLM content enhancement is disabled");
            return;
        }
        
        logger.info("Using LLM to enhance content extraction for: {}", companyData.getCompanyName());
        
        // Enhance executive team if we found few
        if (companyData.getLeadership().size() < 3) {
            enhanceExecutiveTeam(companyData, allContent);
        }
        
        // Enhance products if we found few
        if (companyData.getProducts().size() < 2) {
            enhanceProducts(companyData, allContent);
        }
        
        // Enhance technologies if we found few
        if (companyData.getTechnologies().size() < 5) {
            enhanceTechnologies(companyData, allContent);
        }
        
        // Enhance recent news if we found few
        if (companyData.getRecentNews().size() < 3) {
            enhanceNews(companyData, allContent);
        }
    }
    
    private void enhanceExecutiveTeam(CompanyData companyData, String content) {
        String systemPrompt = """
            You are an expert at extracting executive team information from company websites and documents.
            Extract the names and titles of key executives (CEO, CTO, CFO, VPs, etc.).
            Return ONLY a JSON array of objects with 'name' and 'title' fields.
            Example: [{"name": "Jane Smith", "title": "CEO"}, {"name": "John Doe", "title": "CTO"}]
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nExtract executive team members from this content:\n\n%s",
            companyData.getCompanyName(),
            truncateContent(content, 3000)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        if (response != null) {
            try {
                String json = extractJson(response);
                var executives = gson.fromJson(json, com.google.gson.JsonArray.class);
                for (var exec : executives) {
                    JsonObject obj = exec.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    String title = obj.get("title").getAsString();
                    companyData.addLeader(title + ": " + name);
                }
                logger.info("LLM enhanced executive team with {} additional members", executives.size());
            } catch (Exception e) {
                logger.warn("Could not parse LLM executive team response: {}", e.getMessage());
            }
        }
    }
    
    private void enhanceProducts(CompanyData companyData, String content) {
        String systemPrompt = """
            You are an expert at identifying company products and services.
            Extract the main products/services offered by the company.
            Return ONLY a JSON array of product names and brief descriptions.
            Example: [{"name": "Product X", "description": "Brief description"}, ...]
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nExtract products and services from this content:\n\n%s",
            companyData.getCompanyName(),
            truncateContent(content, 3000)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        if (response != null) {
            try {
                String json = extractJson(response);
                var products = gson.fromJson(json, com.google.gson.JsonArray.class);
                for (var prod : products) {
                    JsonObject obj = prod.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    String desc = obj.has("description") ? obj.get("description").getAsString() : "";
                    companyData.addProduct(name + (desc.isEmpty() ? "" : " - " + desc));
                }
                logger.info("LLM enhanced products with {} additional items", products.size());
            } catch (Exception e) {
                logger.warn("Could not parse LLM products response: {}", e.getMessage());
            }
        }
    }
    
    private void enhanceTechnologies(CompanyData companyData, String content) {
        String systemPrompt = """
            You are an expert at identifying technology stacks used by companies.
            Extract programming languages, frameworks, cloud platforms, databases, and tools mentioned.
            Return ONLY a JSON array of technology names.
            Example: ["Python", "React", "AWS", "PostgreSQL", "Docker"]
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nExtract technologies from this content:\n\n%s",
            companyData.getCompanyName(),
            truncateContent(content, 3000)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        if (response != null) {
            try {
                String json = extractJson(response);
                var techs = gson.fromJson(json, com.google.gson.JsonArray.class);
                for (var tech : techs) {
                    String techName = tech.getAsString();
                    if (!companyData.getTechnologies().contains(techName)) {
                        companyData.addTechnology(techName);
                    }
                }
                logger.info("LLM enhanced technologies with {} additional items", techs.size());
            } catch (Exception e) {
                logger.warn("Could not parse LLM technologies response: {}", e.getMessage());
            }
        }
    }
    
    private void enhanceNews(CompanyData companyData, String content) {
        String systemPrompt = """
            You are an expert at identifying recent company news and developments.
            Extract recent news items, announcements, funding rounds, product launches, etc.
            Return ONLY a JSON array of news items with 'headline' and optional 'date' fields.
            Example: [{"headline": "Raised $100M Series C", "date": "2024"}, ...]
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nExtract recent news from this content:\n\n%s",
            companyData.getCompanyName(),
            truncateContent(content, 3000)
        );
        
        String response = openAIService.complete(systemPrompt, userPrompt);
        if (response != null) {
            try {
                String json = extractJson(response);
                var newsItems = gson.fromJson(json, com.google.gson.JsonArray.class);
                for (var item : newsItems) {
                    JsonObject obj = item.getAsJsonObject();
                    String headline = obj.get("headline").getAsString();
                    String date = obj.has("date") ? obj.get("date").getAsString() : "";
                    String formatted = date.isEmpty() ? headline : "(" + date + ") " + headline;
                    companyData.addNews(formatted);
                }
                logger.info("LLM enhanced news with {} additional items", newsItems.size());
            } catch (Exception e) {
                logger.warn("Could not parse LLM news response: {}", e.getMessage());
            }
        }
    }
    
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    private String extractJson(String response) {
        // Remove markdown code blocks if present
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
}
