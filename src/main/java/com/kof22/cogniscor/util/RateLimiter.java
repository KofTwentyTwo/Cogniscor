package com.kof22.cogniscor.util;

import com.kof22.cogniscor.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple rate limiter to prevent overwhelming servers.
 */
public class RateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    private static RateLimiter instance;
    private long lastRequestTime = 0;
    private final long delayMs;
    private final boolean enabled;
    
    private RateLimiter() {
        Configuration config = Configuration.getInstance();
        this.delayMs = config.getRateLimitDelay();
        this.enabled = config.isRateLimitEnabled();
    }
    
    public static RateLimiter getInstance() {
        if (instance == null) {
            instance = new RateLimiter();
        }
        return instance;
    }
    
    /**
     * Blocks until enough time has passed since the last request.
     */
    public synchronized void waitIfNeeded() {
        if (!enabled) {
            return;
        }
        
        long now = System.currentTimeMillis();
        long timeSinceLastRequest = now - lastRequestTime;
        
        if (timeSinceLastRequest < delayMs) {
            long waitTime = delayMs - timeSinceLastRequest;
            try {
                logger.debug("Rate limiting: waiting {}ms", waitTime);
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Rate limiter interrupted", e);
            }
        }
        
        lastRequestTime = System.currentTimeMillis();
    }
}
