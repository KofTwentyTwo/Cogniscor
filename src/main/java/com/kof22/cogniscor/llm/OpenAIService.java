package com.kof22.cogniscor.llm;

import com.kof22.cogniscor.config.Configuration;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for interacting with OpenAI's API.
 */
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    private final OpenAiService service;
    private final Configuration config;
    private final String model;
    private final int maxTokens;
    private final double temperature;
    
    public OpenAIService() {
        this.config = Configuration.getInstance();
        String apiKey = config.getProperty("openai.api.key");
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warn("OpenAI API key not configured. LLM features will be disabled.");
            this.service = null;
            this.model = null;
            this.maxTokens = 0;
            this.temperature = 0;
        } else {
            this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
            this.model = config.getProperty("openai.model", "gpt-4o-mini");
            this.maxTokens = config.getIntProperty("openai.max.tokens", 4000);
            this.temperature = Double.parseDouble(config.getProperty("openai.temperature", "0.3"));
            logger.info("OpenAI service initialized with model: {}", model);
        }
    }
    
    /**
     * Check if LLM features are enabled and configured.
     */
    public boolean isEnabled() {
        return service != null && config.getBooleanProperty("llm.enabled", false);
    }
    
    /**
     * Send a prompt to the LLM and get a response.
     */
    public String complete(String systemPrompt, String userPrompt) {
        if (!isEnabled()) {
            logger.debug("LLM is disabled, skipping completion");
            return null;
        }
        
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), userPrompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();
            
            logger.debug("Sending request to OpenAI API (model: {})", model);
            ChatCompletionResult result = service.createChatCompletion(request);
            
            if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                String response = result.getChoices().get(0).getMessage().getContent();
                logger.debug("Received response from OpenAI ({} tokens)", 
                    result.getUsage() != null ? result.getUsage().getTotalTokens() : "unknown");
                return response;
            }
            
            logger.warn("No response from OpenAI API");
            return null;
            
        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Simple completion with just user prompt (no system prompt).
     */
    public String complete(String prompt) {
        return complete("You are a helpful AI assistant.", prompt);
    }
}
