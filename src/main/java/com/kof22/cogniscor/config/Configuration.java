package com.kof22.cogniscor.config;

import com.kof22.cogniscor.setup.SetupWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application configuration.
 * Checks user's home directory first (~/.cogniscor/config.properties),
 * then falls back to bundled config.properties.
 */
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static Configuration instance;
    private final Properties properties;
    private boolean userConfigLoaded = false;
    
    private Configuration() {
        properties = new Properties();
        loadProperties();
    }
    
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
    
    /**
     * Check if user configuration exists.
     */
    public static boolean userConfigExists() {
        return SetupWizard.configExists();
    }
    
    /**
     * Check if configuration was loaded from user's home directory.
     */
    public boolean isUserConfigLoaded() {
        return userConfigLoaded;
    }
    
    private void loadProperties() {
        // First try to load from user's home directory
        try {
            Properties userProps = SetupWizard.loadUserConfig();
            if (!userProps.isEmpty()) {
                properties.putAll(userProps);
                userConfigLoaded = true;
                logger.info("Loaded user configuration from ~/.cogniscor/config.properties");
                return;
            }
        } catch (IOException e) {
            logger.debug("No user configuration found, trying bundled config");
        }
        
        // Fall back to bundled config.properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warn("Unable to find config.properties, using defaults");
                setDefaults();
                return;
            }
            properties.load(input);
            logger.info("Configuration loaded from bundled config.properties");
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            setDefaults();
        }
    }
    
    private void setDefaults() {
        properties.setProperty("http.timeout.seconds", "30");
        properties.setProperty("http.user.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        properties.setProperty("rate.limit.delay.ms", "2000");
        properties.setProperty("rate.limit.enabled", "true");
        properties.setProperty("scraper.max.retries", "3");
        properties.setProperty("scraper.retry.delay.ms", "5000");
        properties.setProperty("output.base.dir", "./data");
        properties.setProperty("log.level", "INFO");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for {}, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    // Specific configuration getters
    public int getHttpTimeout() {
        return getIntProperty("http.timeout.seconds", 30);
    }
    
    public String getUserAgent() {
        return getProperty("http.user.agent", "Mozilla/5.0");
    }
    
    public int getRateLimitDelay() {
        return getIntProperty("rate.limit.delay.ms", 2000);
    }
    
    public boolean isRateLimitEnabled() {
        return getBooleanProperty("rate.limit.enabled", true);
    }
    
    public int getMaxRetries() {
        return getIntProperty("scraper.max.retries", 3);
    }
    
    public int getRetryDelay() {
        return getIntProperty("scraper.retry.delay.ms", 5000);
    }
    
    public String getOutputBaseDir() {
        return getProperty("output.base.dir", "./data");
    }
    
    public int getMaxSummaryWords() {
        return getIntProperty("analyzer.max.summary.words", 500);
    }
    
    public int getExecutiveSummaryMaxWords() {
        return getIntProperty("analyzer.executive.summary.max.words", 800);
    }
    
    // LLM configuration getters
    public boolean isLLMEnabled() {
        return getBooleanProperty("llm.enabled", false) && 
               getProperty("openai.api.key") != null && 
               !getProperty("openai.api.key").trim().isEmpty();
    }
    
    public boolean isLLMVerifyCompanyEnabled() {
        return isLLMEnabled() && getBooleanProperty("llm.verify.company", true);
    }
    
    public boolean isLLMDiscoverUrlsEnabled() {
        return isLLMEnabled() && getBooleanProperty("llm.discover.urls", true);
    }
    
    public boolean isLLMRecoverFailuresEnabled() {
        return isLLMEnabled() && getBooleanProperty("llm.recover.failures", true);
    }
    
    public boolean isLLMEnhanceExtractionEnabled() {
        return isLLMEnabled() && getBooleanProperty("llm.enhance.extraction", true);
    }
    
    public boolean isLLMReviewReportsEnabled() {
        return isLLMEnabled() && getBooleanProperty("llm.review.reports", true);
    }
}
