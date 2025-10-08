package com.kof22.cogniscor.scraper;

import com.kof22.cogniscor.config.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Factory for creating configured HttpClient instances.
 */
public class HttpClientFactory {
    
    public static HttpClient createClient() {
        Configuration config = Configuration.getInstance();
        
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(config.getHttpTimeout()))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }
}
