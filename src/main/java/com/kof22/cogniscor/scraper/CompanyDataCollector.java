package com.kof22.cogniscor.scraper;

import com.kof22.cogniscor.linkedin.LinkedInData;
import com.kof22.cogniscor.linkedin.LinkedInIntelligence;
import com.kof22.cogniscor.llm.*;
import com.kof22.cogniscor.model.CompanyData;
import com.kof22.cogniscor.model.SourceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orchestrates data collection from multiple sources for a company.
 * Uses LLM for intelligent URL discovery and failure recovery.
 */
public class CompanyDataCollector {
    private static final Logger logger = LoggerFactory.getLogger(CompanyDataCollector.class);
    private final WebScraper webScraper;
    private OpenAIService openAIService;
    private LLMCompanyIdentifier companyIdentifier;
    private LLMCompanyVerifier companyVerifier;
    private LLMUrlDiscovery urlDiscovery;
    private LLMFailureRecovery failureRecovery;
    private LinkedInIntelligence linkedInIntel;
    
    public CompanyDataCollector() {
        this.webScraper = new WebScraper();
    }
    
    /**
     * Enable LLM-enhanced data collection.
     */
    public void setOpenAIService(OpenAIService openAIService) {
        this.openAIService = openAIService;
        if (openAIService != null && openAIService.isEnabled()) {
            this.companyIdentifier = new LLMCompanyIdentifier(openAIService);
            this.companyVerifier = new LLMCompanyVerifier(openAIService);
            this.urlDiscovery = new LLMUrlDiscovery(openAIService);
            this.failureRecovery = new LLMFailureRecovery(openAIService);
            this.linkedInIntel = new LinkedInIntelligence(openAIService, webScraper);
            logger.info("LLM-enhanced data collection enabled (including LinkedIn intelligence)");
        }
    }
    
    /**
     * Collects all available data about a company (company name determined from website).
     */
    public CompanyData collectData(String websiteUrl) {
        logger.info("Starting data collection for website: {}", websiteUrl);
        
        // Phase 1: Identify the company from website
        logger.info("Phase 1: Company identification");
        
        // Scrape the main website to get content for identification
        SourceData mainSite = webScraper.scrapeUrl(
            normalizeUrl(websiteUrl),
            "Company Website - Main Page",
            "official_website"
        );
        
        if (!mainSite.isSuccessful()) {
            throw new RuntimeException("Failed to access website: " + mainSite.getErrorMessage());
        }
        
        // Use LLM to identify company name (or fallback to URL-based guess)
        String companyName;
        if (companyIdentifier != null) {
            companyName = identifyCompanyWithLLM(websiteUrl, mainSite.getContent());
        } else {
            companyName = guessCompanyNameFromUrl(websiteUrl);
        }
        
        logger.info("Identified company: {}", companyName);
        CompanyData companyData = new CompanyData(companyName, websiteUrl);
        companyData.addSource(mainSite);
        
        // Phase 2: Collect additional website pages
        logger.info("Phase 2: Additional website pages");
        scrapeAdditionalWebsitePages(companyData, websiteUrl);
        
        // Phase 3: LinkedIn deep intelligence gathering (if enabled)
        if (linkedInIntel != null) {
            logger.info("Phase 3: LinkedIn intelligence gathering");
            LinkedInData linkedInData = linkedInIntel.gatherIntelligence(
                companyName, 
                websiteUrl, 
                mainSite.getContent()
            );
            companyData.setLinkedInData(linkedInData);
        }
        
        // Phase 4: Use LLM to discover optimal URLs (if enabled)
        if (urlDiscovery != null) {
            logger.info("Phase 4: LLM-based URL discovery (skipping hardcoded URL guessing)");
            discoverAndScrapeUrls(companyData);
            
            // Phase 5: Failure recovery (if we have failures)
            if (failureRecovery != null) {
                performFailureRecovery(companyData);
            }
        } else {
            // Phase 5: Fallback to standard sources (only when LLM is NOT enabled)
            logger.info("Phase 4: Standard source collection (LLM disabled - using URL guessing)");
            scrapeAndVerifyWikipedia(companyData);
            scrapeAndVerifyLinkedIn(companyData);
            scrapeAndVerifyCrunchbase(companyData);
            scrapeAndVerifyGlassdoor(companyData);
            scrapeAndVerifyNews(companyData);
        }
        
        // Phase 6: Final verification of all content
        if (companyIdentifier != null) {
            verifyAllContentMatchesCompany(companyData);
        }
        
        long successfulSources = companyData.getSources().stream()
            .filter(SourceData::isSuccessful)
            .count();
        
        logger.info("Data collection completed. Total sources: {} ({} successful)", 
            companyData.getSources().size(), successfulSources);
        
        return companyData;
    }
    
    /**
     * Identify company name using LLM.
     */
    private String identifyCompanyWithLLM(String websiteUrl, String content) {
        logger.info("Using LLM to identify company name...");
        
        LLMCompanyIdentifier.CompanyIdentity identity = companyIdentifier.identifyCompany(
            websiteUrl, content);
        
        if (identity != null && identity.companyName != null) {
            logger.info("LLM identified company: '{}' (confidence: {})", 
                identity.companyName, identity.confidence);
            logger.info("Reasoning: {}", identity.reasoning);
            
            if (!identity.alternativeNames.isEmpty()) {
                logger.info("Alternative names: {}", String.join(", ", identity.alternativeNames));
            }
            
            return identity.companyName;
        }
        
        // Fallback to URL-based guess
        logger.warn("LLM identification failed, falling back to URL-based guess");
        return guessCompanyNameFromUrl(websiteUrl);
    }
    
    /**
     * Guess company name from URL (fallback when LLM not available).
     */
    private String guessCompanyNameFromUrl(String url) {
        String domain = url.replaceAll("https?://", "")
                          .replaceAll("www\\.", "")
                          .replaceAll("/.*", "")
                          .split("\\.")[0];
        
        // Capitalize first letter
        return domain.substring(0, 1).toUpperCase() + domain.substring(1);
    }
    
    /**
     * Scrape additional pages from the official website.
     */
    private void scrapeAdditionalWebsitePages(CompanyData companyData, String websiteUrl) {
        List<String> commonPages = List.of(
            "/about", "/about-us", "/company", "/who-we-are",
            "/team", "/leadership", "/executives",
            "/careers", "/mission", "/products", "/solutions",
            "/newsroom", "/press", "/blog", "/news"
        );
        
        String baseUrl = normalizeUrl(websiteUrl);
        for (String path : commonPages) {
            String url = baseUrl + path;
            if (webScraper.isUrlAccessible(url)) {
                SourceData page = webScraper.scrapeUrl(
                    url,
                    companyData.getCompanyName() + " - " + path.substring(1),
                    "official_website"
                );
                companyData.addSource(page);
            }
        }
    }
    
    /**
     * Scrape and verify Wikipedia page.
     */
    private void scrapeAndVerifyWikipedia(CompanyData companyData) {
        SourceData source = scrapeWikipedia(companyData, companyData.getCompanyName());
        if (source != null) {
            verifyAndMarkSource(companyData, source);
        }
    }
    
    /**
     * Scrape and verify LinkedIn page.
     */
    private void scrapeAndVerifyLinkedIn(CompanyData companyData) {
        SourceData source = scrapeLinkedIn(companyData, companyData.getCompanyName());
        if (source != null) {
            verifyAndMarkSource(companyData, source);
        }
    }
    
    /**
     * Scrape and verify Crunchbase page.
     */
    private void scrapeAndVerifyCrunchbase(CompanyData companyData) {
        SourceData source = scrapeCrunchbase(companyData, companyData.getCompanyName());
        if (source != null) {
            verifyAndMarkSource(companyData, source);
        }
    }
    
    /**
     * Scrape and verify Glassdoor page.
     */
    private void scrapeAndVerifyGlassdoor(CompanyData companyData) {
        SourceData source = scrapeGlassdoor(companyData, companyData.getCompanyName());
        if (source != null) {
            verifyAndMarkSource(companyData, source);
        }
    }
    
    /**
     * Scrape and verify news.
     */
    private void scrapeAndVerifyNews(CompanyData companyData) {
        SourceData source = scrapeNews(companyData, companyData.getCompanyName());
        if (source != null) {
            verifyAndMarkSource(companyData, source);
        }
    }
    
    /**
     * Verify source content matches the target company.
     */
    private void verifyAndMarkSource(CompanyData companyData, SourceData source) {
        if (!source.isSuccessful() || companyIdentifier == null) {
            return;
        }
        
        boolean matches = companyIdentifier.verifyContentMatchesCompany(
            companyData.getCompanyName(),
            source.getContent(),
            source.getSourceUrl(),
            source.getSourceType()
        );
        
        if (!matches) {
            logger.warn("Content verification FAILED for {}: content is about a different company", 
                source.getSourceType());
            source.setSuccessful(false);
            source.setErrorMessage("Content verification failed - different company");
        }
    }
    
    /**
     * Final verification pass - ensure all content is about the target company.
     */
    private void verifyAllContentMatchesCompany(CompanyData companyData) {
        logger.info("Running final verification of all content...");
        
        int verifiedCount = 0;
        int mismatchCount = 0;
        
        for (SourceData source : companyData.getSources()) {
            if (!source.isSuccessful() || source.getContent() == null) {
                continue;
            }
            
            boolean matches = companyIdentifier.verifyContentMatchesCompany(
                companyData.getCompanyName(),
                source.getContent(),
                source.getSourceUrl(),
                source.getSourceType()
            );
            
            if (matches) {
                verifiedCount++;
            } else {
                mismatchCount++;
                source.setSuccessful(false);
                source.setErrorMessage("Final verification failed - content about different company");
            }
        }
        
        logger.info("Final verification complete: {} verified, {} mismatches", 
            verifiedCount, mismatchCount);
    }
    
    
    /**
     * Use LLM to discover and scrape optimal URLs.
     */
    private void discoverAndScrapeUrls(CompanyData companyData) {
        logger.info("Discovering URLs with LLM...");
        
        List<String> failedSources = companyData.getSources().stream()
            .filter(s -> !s.isSuccessful())
            .map(SourceData::getSourceType)
            .collect(Collectors.toList());
        
        List<LLMUrlDiscovery.UrlSuggestion> suggestions = urlDiscovery.discoverUrls(
            companyData.getCompanyName(),
            companyData.getWebsiteUrl(),
            failedSources
        );
        
        if (!suggestions.isEmpty()) {
            logger.info("LLM suggested {} URLs to try", suggestions.size());
            
            // Prioritize high-priority URLs
            suggestions.sort((a, b) -> {
                int priorityOrder = getPriorityOrder(b.priority) - getPriorityOrder(a.priority);
                return priorityOrder;
            });
            
            // Try top URLs (limit to avoid too many requests)
            int maxUrls = Math.min(suggestions.size(), 10);
            for (int i = 0; i < maxUrls; i++) {
                LLMUrlDiscovery.UrlSuggestion suggestion = suggestions.get(i);
                logger.info("Trying LLM-suggested URL: {}", suggestion);
                
                SourceData source = webScraper.scrapeUrl(
                    suggestion.url,
                    companyData.getCompanyName() + " - " + suggestion.purpose,
                    "llm_discovery_" + suggestion.category
                );
                companyData.addSource(source);
            }
        }
    }
    
    /**
     * Analyze failures and attempt recovery.
     */
    private void performFailureRecovery(CompanyData companyData) {
        List<SourceData> failedSources = companyData.getSources().stream()
            .filter(s -> !s.isSuccessful())
            .collect(Collectors.toList());
        
        if (failedSources.isEmpty()) {
            logger.info("No failures to recover from");
            return;
        }
        
        logger.info("Performing failure recovery for {} failed sources", failedSources.size());
        
        LLMFailureRecovery.RecoveryPlan plan = failureRecovery.analyzeFailures(
            companyData.getCompanyName(),
            companyData.getWebsiteUrl(),
            failedSources
        );
        
        if (plan != null) {
            logger.info("Recovery diagnosis: {}", plan.diagnosis);
            
            // Try alternative URLs
            for (LLMFailureRecovery.AlternativeUrl altUrl : plan.alternativeUrls) {
                logger.info("Trying alternative URL: {}", altUrl);
                
                SourceData source = webScraper.scrapeUrl(
                    altUrl.url,
                    companyData.getCompanyName() + " - Recovery",
                    "llm_recovery"
                );
                companyData.addSource(source);
                
                // Stop if we got enough successful recoveries
                if (source.isSuccessful()) {
                    long successfulRecoveries = companyData.getSources().stream()
                        .filter(s -> s.getSourceType().startsWith("llm_recovery"))
                        .filter(SourceData::isSuccessful)
                        .count();
                    
                    if (successfulRecoveries >= 3) {
                        logger.info("Sufficient recovery sources obtained");
                        break;
                    }
                }
            }
            
            // Log recommendations
            if (!plan.recommendations.isEmpty()) {
                logger.info("LLM recommendations:");
                plan.recommendations.forEach(rec -> logger.info("  - {}", rec));
            }
        }
    }
    
    private int getPriorityOrder(String priority) {
        return switch (priority.toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }
    
    private SourceData scrapeWikipedia(CompanyData companyData, String companyName) {
        try {
            String encodedName = URLEncoder.encode(companyName, StandardCharsets.UTF_8);
            String url = "https://en.wikipedia.org/wiki/" + encodedName.replace("+", "_");
            
            logger.info("Attempting Wikipedia: {}", url);
            SourceData wikipediaData = webScraper.scrapeUrl(
                url,
                companyName + " - Wikipedia",
                "wikipedia"
            );
            companyData.addSource(wikipediaData);
            return wikipediaData;
        } catch (Exception e) {
            logger.warn("Could not fetch Wikipedia page: {}", e.getMessage());
            return null;
        }
    }
    
    private SourceData scrapeLinkedIn(CompanyData companyData, String companyName) {
        try {
            // LinkedIn company pages follow pattern: linkedin.com/company/company-name
            String slug = companyName.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
            String url = "https://www.linkedin.com/company/" + slug;
            
            logger.info("Attempting LinkedIn: {}", url);
            SourceData linkedInData = webScraper.scrapeUrl(
                url,
                companyName + " - LinkedIn",
                "linkedin"
            );
            companyData.addSource(linkedInData);
            return linkedInData;
        } catch (Exception e) {
            logger.warn("Could not fetch LinkedIn page: {}", e.getMessage());
            return null;
        }
    }
    
    private SourceData scrapeCrunchbase(CompanyData companyData, String companyName) {
        try {
            String slug = companyName.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
            String url = "https://www.crunchbase.com/organization/" + slug;
            
            logger.info("Attempting Crunchbase: {}", url);
            SourceData crunchbaseData = webScraper.scrapeUrl(
                url,
                companyName + " - Crunchbase",
                "crunchbase"
            );
            companyData.addSource(crunchbaseData);
            return crunchbaseData;
        } catch (Exception e) {
            logger.warn("Could not fetch Crunchbase page: {}", e.getMessage());
            return null;
        }
    }
    
    private SourceData scrapeGlassdoor(CompanyData companyData, String companyName) {
        try {
            String slug = companyName.toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
            String url = "https://www.glassdoor.com/Overview/Working-at-" + slug + ".htm";
            
            logger.info("Attempting Glassdoor: {}", url);
            SourceData glassdoorData = webScraper.scrapeUrl(
                url,
                companyName + " - Glassdoor Reviews",
                "glassdoor"
            );
            companyData.addSource(glassdoorData);
            return glassdoorData;
        } catch (Exception e) {
            logger.warn("Could not fetch Glassdoor page: {}", e.getMessage());
            return null;
        }
    }
    
    private SourceData scrapeNews(CompanyData companyData, String companyName) {
        try {
            // Use Google News search (simplified)
            String encodedName = URLEncoder.encode(companyName + " company news", StandardCharsets.UTF_8);
            String url = "https://news.google.com/search?q=" + encodedName;
            
            logger.info("Attempting news search: {}", url);
            SourceData newsData = webScraper.scrapeUrl(
                url,
                companyName + " - Recent News",
                "news"
            );
            companyData.addSource(newsData);
            return newsData;
        } catch (Exception e) {
            logger.warn("Could not fetch news: {}", e.getMessage());
            return null;
        }
    }
    
    private String normalizeUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        // Remove trailing slash
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
