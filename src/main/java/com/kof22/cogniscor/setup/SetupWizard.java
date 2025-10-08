package com.kof22.cogniscor.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

/**
 * Interactive setup wizard for first-time configuration.
 */
public class SetupWizard {
    private static final Logger logger = LoggerFactory.getLogger(SetupWizard.class);
    private static final String CONFIG_DIR_NAME = ".cogniscor";
    private static final String CONFIG_FILE_NAME = "config.properties";
    
    private final Scanner scanner;
    private final Path configDir;
    private final Path configFile;
    
    public SetupWizard() {
        this.scanner = new Scanner(System.in);
        String homeDir = System.getProperty("user.home");
        this.configDir = Paths.get(homeDir, CONFIG_DIR_NAME);
        this.configFile = configDir.resolve(CONFIG_FILE_NAME);
    }
    
    /**
     * Get the path to the user's config file.
     */
    public static Path getConfigFilePath() {
        String homeDir = System.getProperty("user.home");
        return Paths.get(homeDir, CONFIG_DIR_NAME, CONFIG_FILE_NAME);
    }
    
    /**
     * Check if configuration exists.
     */
    public static boolean configExists() {
        return Files.exists(getConfigFilePath());
    }
    
    /**
     * Run the interactive setup wizard.
     */
    public boolean runSetup() {
        try {
            printWelcome();
            
            // Create config directory
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                logger.info("Created config directory: {}", configDir);
            }
            
            // Create properties with defaults
            Properties props = createDefaultProperties();
            
            // Prompt for OpenAI API key
            promptForOpenAIKey(props);
            
            // Prompt for LLM features
            promptForLLMFeatures(props);
            
            // Prompt for other settings
            promptForOtherSettings(props);
            
            // Save configuration
            saveConfiguration(props);
            
            printSuccess();
            
            return true;
            
        } catch (Exception e) {
            logger.error("Setup failed", e);
            System.err.println("\n❌ Setup failed: " + e.getMessage());
            return false;
        }
    }
    
    private void printWelcome() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║              COGNISCOR SETUP WIZARD                        ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        System.out.println("Welcome! This wizard will help you configure Cogniscor.");
        System.out.println("Your configuration will be saved to:");
        System.out.println("  " + configFile);
        System.out.println();
    }
    
    private Properties createDefaultProperties() {
        Properties props = new Properties();
        
        // HTTP Client Settings
        props.setProperty("http.timeout.seconds", "30");
        props.setProperty("http.user.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        props.setProperty("http.max.redirects", "5");
        
        // Rate Limiting
        props.setProperty("rate.limit.delay.ms", "2000");
        props.setProperty("rate.limit.enabled", "true");
        
        // Scraping Settings
        props.setProperty("scraper.max.retries", "3");
        props.setProperty("scraper.retry.delay.ms", "5000");
        
        // Output Settings
        props.setProperty("output.base.dir", "./data");
        props.setProperty("output.create.examples", "true");
        
        // Logging
        props.setProperty("log.level", "INFO");
        
        // Analysis Settings
        props.setProperty("analyzer.min.content.length", "100");
        props.setProperty("analyzer.max.summary.words", "500");
        props.setProperty("analyzer.executive.summary.max.words", "800");
        
        // Search Settings
        props.setProperty("search.max.results", "10");
        props.setProperty("search.include.news", "true");
        props.setProperty("search.include.wikipedia", "true");
        
        return props;
    }
    
    private void promptForOpenAIKey(Properties props) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  OpenAI API Configuration (Optional)");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        System.out.println("Cogniscor can use ChatGPT to enhance analysis quality.");
        System.out.println("This is OPTIONAL - the tool works great without it!\n");
        
        System.out.print("Do you have an OpenAI API key? (yes/no) [no]: ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("yes") || response.equals("y")) {
            System.out.println("\nGreat! You can get your API key from:");
            System.out.println("  https://platform.openai.com/api-keys\n");
            
            System.out.print("Enter your OpenAI API key (starts with 'sk-'): ");
            String apiKey = scanner.nextLine().trim();
            
            if (apiKey.isEmpty()) {
                System.out.println("⚠ No API key entered. AI features will be disabled.");
                props.setProperty("openai.api.key", "");
                props.setProperty("llm.enabled", "false");
            } else if (!apiKey.startsWith("sk-")) {
                System.out.println("⚠ Warning: API key should start with 'sk-'");
                System.out.print("Use this key anyway? (yes/no) [yes]: ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("no") || confirm.equals("n")) {
                    props.setProperty("openai.api.key", "");
                    props.setProperty("llm.enabled", "false");
                } else {
                    props.setProperty("openai.api.key", apiKey);
                    props.setProperty("llm.enabled", "true");
                    configureOpenAIModel(props);
                }
            } else {
                props.setProperty("openai.api.key", apiKey);
                props.setProperty("llm.enabled", "true");
                System.out.println("✓ API key saved!");
                configureOpenAIModel(props);
            }
        } else {
            System.out.println("\nNo problem! You can add an API key later by editing:");
            System.out.println("  " + configFile);
            System.out.println("Or run: cogniscor --setup again\n");
            props.setProperty("openai.api.key", "");
            props.setProperty("llm.enabled", "false");
        }
    }
    
    private void configureOpenAIModel(Properties props) {
        System.out.println("\n───────────────────────────────────────────────────────────");
        System.out.println("  Model Selection");
        System.out.println("───────────────────────────────────────────────────────────\n");
        
        System.out.println("Available models:");
        System.out.println("  1. gpt-4o-mini  (Recommended - Fast & Cheap, ~$0.025/company)");
        System.out.println("  2. gpt-4o       (More capable, ~$0.15/company)");
        System.out.println("  3. gpt-3.5-turbo (Older, ~$0.01/company)");
        System.out.println();
        
        System.out.print("Choose model (1-3) [1]: ");
        String choice = scanner.nextLine().trim();
        
        String model = switch (choice) {
            case "2" -> "gpt-4o";
            case "3" -> "gpt-3.5-turbo";
            default -> "gpt-4o-mini";
        };
        
        props.setProperty("openai.model", model);
        props.setProperty("openai.max.tokens", "4000");
        props.setProperty("openai.temperature", "0.3");
        
        System.out.println("✓ Selected: " + model);
    }
    
    private void promptForLLMFeatures(Properties props) {
        if (!props.getProperty("llm.enabled", "false").equals("true")) {
            // LLM disabled, set defaults
            props.setProperty("llm.plan.sources", "false");
            props.setProperty("llm.enhance.extraction", "false");
            props.setProperty("llm.review.reports", "false");
            return;
        }
        
        System.out.println("\n───────────────────────────────────────────────────────────");
        System.out.println("  AI Features");
        System.out.println("───────────────────────────────────────────────────────────\n");
        
        System.out.println("Which AI features would you like to enable?");
        System.out.println("(Each adds to API cost but improves quality)\n");
        
        // Enhanced extraction
        System.out.print("Enable AI-enhanced extraction? (yes/no) [yes]: ");
        String enhance = scanner.nextLine().trim().toLowerCase();
        props.setProperty("llm.enhance.extraction", 
            !enhance.equals("no") && !enhance.equals("n") ? "true" : "false");
        
        // Report review
        System.out.print("Enable AI report review? (yes/no) [yes]: ");
        String review = scanner.nextLine().trim().toLowerCase();
        props.setProperty("llm.review.reports", 
            !review.equals("no") && !review.equals("n") ? "true" : "false");
        
        // Source planning (future feature)
        props.setProperty("llm.plan.sources", "false");
        
        System.out.println();
    }
    
    private void promptForOtherSettings(Properties props) {
        System.out.println("───────────────────────────────────────────────────────────");
        System.out.println("  Additional Settings");
        System.out.println("───────────────────────────────────────────────────────────\n");
        
        System.out.print("Output directory [./data]: ");
        String outputDir = scanner.nextLine().trim();
        if (!outputDir.isEmpty()) {
            props.setProperty("output.base.dir", outputDir);
        }
        
        System.out.print("Enable verbose logging? (yes/no) [no]: ");
        String verbose = scanner.nextLine().trim().toLowerCase();
        if (verbose.equals("yes") || verbose.equals("y")) {
            props.setProperty("log.level", "DEBUG");
        }
        
        System.out.println();
    }
    
    private void saveConfiguration(Properties props) throws IOException {
        System.out.println("💾 Saving configuration...");
        
        try (OutputStream output = new FileOutputStream(configFile.toFile())) {
            props.store(output, "Cogniscor Configuration - Generated by Setup Wizard");
        }
        
        logger.info("Configuration saved to: {}", configFile);
    }
    
    private void printSuccess() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║                  ✓ SETUP COMPLETE!                         ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("Configuration saved to:");
        System.out.println("  " + configFile);
        System.out.println();
        System.out.println("You can now run Cogniscor:");
        System.out.println("  ./cogniscor.sh \"Company Name\" \"https://company.com\"");
        System.out.println();
        System.out.println("To reconfigure at any time, run:");
        System.out.println("  ./cogniscor.sh --setup");
        System.out.println();
        System.out.println("To edit config manually:");
        System.out.println("  nano " + configFile);
        System.out.println();
    }
    
    /**
     * Load configuration from user's home directory.
     */
    public static Properties loadUserConfig() throws IOException {
        Path configPath = getConfigFilePath();
        Properties props = new Properties();
        
        if (Files.exists(configPath)) {
            try (InputStream input = new FileInputStream(configPath.toFile())) {
                props.load(input);
                logger.info("Loaded configuration from: {}", configPath);
            }
        }
        
        return props;
    }
}
