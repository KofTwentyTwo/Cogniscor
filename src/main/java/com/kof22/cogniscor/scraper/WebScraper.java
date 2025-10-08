package com.kof22.cogniscor.scraper;

import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.model.SourceData;
import com.kof22.cogniscor.util.RateLimiter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Core web scraping functionality using HttpClient and Jsoup.
 */
public class WebScraper {
    private static final Logger logger = LoggerFactory.getLogger(WebScraper.class);
    private final HttpClient httpClient;
    private final Configuration config;
    private final RateLimiter rateLimiter;
    
    public WebScraper() {
        this.httpClient = HttpClientFactory.createClient();
        this.config = Configuration.getInstance();
        this.rateLimiter = RateLimiter.getInstance();
    }
    
    /**
     * Fetches and parses content from a URL.
     */
    public SourceData scrapeUrl(String url, String title, String sourceType) {
        SourceData sourceData = new SourceData(title, url, sourceType);
        
        int maxRetries = config.getMaxRetries();
        int retryDelay = config.getRetryDelay();
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Scraping {} (attempt {}/{}): {}", sourceType, attempt, maxRetries, url);
                
                // Apply rate limiting
                rateLimiter.waitIfNeeded();
                
                // Build and send request
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", config.getUserAgent())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    // Parse HTML with Jsoup
                    Document doc = Jsoup.parse(response.body(), url);
                    String content = extractContent(doc, sourceType);
                    
                    sourceData.setContent(content);
                    sourceData.setSuccessful(true);
                    logger.info("Successfully scraped: {}", url);
                    return sourceData;
                } else if (response.statusCode() == 429) {
                    // Rate limited - retry with longer delay
                    logger.warn("Rate limited (429) on {}, waiting longer...", url);
                    Thread.sleep(retryDelay * 2);
                } else if (response.statusCode() == 404 || response.statusCode() == 403 || 
                           response.statusCode() == 401 || response.statusCode() == 410) {
                    // Permanent failures - don't retry
                    logger.warn("HTTP {} for {} (permanent failure, not retrying)", response.statusCode(), url);
                    sourceData.setErrorMessage("HTTP " + response.statusCode());
                    return sourceData;
                } else if (response.statusCode() >= 500) {
                    // Server errors - retry
                    logger.warn("HTTP {} for {} (server error, will retry)", response.statusCode(), url);
                    sourceData.setErrorMessage("HTTP " + response.statusCode());
                    if (attempt < maxRetries) {
                        Thread.sleep(retryDelay);
                    }
                } else {
                    // Other errors - don't retry
                    logger.warn("HTTP {} for {}", response.statusCode(), url);
                    sourceData.setErrorMessage("HTTP " + response.statusCode());
                    return sourceData;
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Scraping interrupted for {}", url, e);
                sourceData.setErrorMessage("Interrupted: " + e.getMessage());
                return sourceData;
            } catch (Exception e) {
                logger.error("Error scraping {} (attempt {}/{}): {}", url, attempt, maxRetries, e.getMessage());
                sourceData.setErrorMessage(e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return sourceData;
                    }
                }
            }
        }
        
        return sourceData;
    }
    
    /**
     * Extracts relevant content from HTML document based on source type.
     */
    private String extractContent(Document doc, String sourceType) {
        StringBuilder content = new StringBuilder();
        
        // Remove script and style elements
        doc.select("script, style, nav, footer, header").remove();
        
        // Extract title
        String title = doc.title();
        if (title != null && !title.isEmpty()) {
            content.append("# ").append(title).append("\n\n");
        }
        
        // Extract meta description
        String description = doc.select("meta[name=description]").attr("content");
        if (!description.isEmpty()) {
            content.append("**Description:** ").append(description).append("\n\n");
        }
        
        // Extract main content based on common patterns
        String mainContent = extractMainContent(doc);
        content.append(mainContent);
        
        return content.toString().trim();
    }
    
    /**
     * Extracts main textual content from document.
     */
    private String extractMainContent(Document doc) {
        StringBuilder text = new StringBuilder();
        
        // Try common main content selectors
        String[] selectors = {
            "main", "article", "[role=main]", ".content", "#content",
            ".main-content", "#main-content", "body"
        };
        
        for (String selector : selectors) {
            var elements = doc.select(selector);
            if (!elements.isEmpty()) {
                elements.forEach(el -> {
                    // Extract paragraphs
                    el.select("p, h1, h2, h3, h4, h5, h6, li").forEach(element -> {
                        String elementText = element.text().trim();
                        if (elementText.length() > 20) { // Filter out very short text
                            text.append(elementText).append("\n\n");
                        }
                    });
                });
                break;
            }
        }
        
        // Fallback: just get all text
        if (text.length() == 0) {
            text.append(doc.body().text());
        }
        
        return text.toString();
    }
    
    /**
     * Simple check if URL is accessible.
     */
    public boolean isUrlAccessible(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", config.getUserAgent())
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
            
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() >= 200 && response.statusCode() < 400;
        } catch (Exception e) {
            logger.debug("URL not accessible: {}", url);
            return false;
        }
    }
}
