package com.kof22.cogniscor.interactive;

import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.llm.OpenAIService;
import com.kof22.cogniscor.model.CompanyData;
import com.kof22.cogniscor.scraper.CompanyDataCollector;
import com.kof22.cogniscor.ui.TerminalUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Interactive review system that shows results to user and allows refinement.
 */
public class InteractiveReview {
    private static final Logger logger = LoggerFactory.getLogger(InteractiveReview.class);
    private final Scanner scanner;
    private final Configuration config;
    
    public InteractiveReview() {
        this.scanner = new Scanner(System.in);
        this.config = Configuration.getInstance();
    }
    
    /**
     * Display executive summary and ask for user feedback.
     */
    public ReviewResult reviewWithUser(Path executiveSummaryPath, CompanyData companyData) {
        try {
            // Read and display the executive summary
            String summary = Files.readString(executiveSummaryPath);
            
            displaySummary(summary);
            
            // Ask for user feedback
            return getUserFeedback(companyData);
            
        } catch (IOException e) {
            logger.error("Error reading executive summary", e);
            return new ReviewResult(true, null);
        }
    }
    
    private void displaySummary(String summary) {
        TerminalUI.printSummaryPreview(summary);
    }
    
    private ReviewResult getUserFeedback(CompanyData companyData) {
        TerminalUI.printReviewPrompt();
        
        TerminalUI.printPrompt("Is this executive summary good? (yes/no/improve) [yes]");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.isEmpty() || response.equals("yes") || response.equals("y")) {
            TerminalUI.printBlankLine();
            TerminalUI.success("Great! Reports saved and ready to use.");
            TerminalUI.printBlankLine();
            return new ReviewResult(true, null);
        }
        
        if (response.equals("improve") || response.equals("i")) {
            return handleImprovementRequest(companyData);
        }
        
        if (response.equals("no") || response.equals("n")) {
            TerminalUI.printBlankLine();
            TerminalUI.info("What would you like to improve?");
            TerminalUI.printChoice("1", "Redo the search with different focus");
            TerminalUI.printChoice("2", "Get more specific information");
            TerminalUI.printChoice("3", "Cancel (keep current results)");
            TerminalUI.printBlankLine();
            TerminalUI.printPrompt("Choose (1-3) [3]");
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("1") || choice.equals("2")) {
                return handleImprovementRequest(companyData);
            }
        }
        
        TerminalUI.printBlankLine();
        TerminalUI.success("Keeping current results.");
        return new ReviewResult(true, null);
    }
    
    private ReviewResult handleImprovementRequest(CompanyData companyData) {
        TerminalUI.printImprovementPrompt();
        
        TerminalUI.info("Describe what you want to find or improve:");
        TerminalUI.printBlankLine();
        TerminalUI.bullet("'Find more information about their engineering culture'");
        TerminalUI.bullet("'Get details about their remote work policy'");
        TerminalUI.bullet("'Focus on recent product launches and roadmap'");
        TerminalUI.bullet("'Find information about compensation and benefits'");
        TerminalUI.bullet("'Get more details about the tech stack they use'");
        TerminalUI.printBlankLine();
        TerminalUI.printPrompt("Your request");
        
        String userPrompt = scanner.nextLine().trim();
        
        if (userPrompt.isEmpty()) {
            TerminalUI.printBlankLine();
            TerminalUI.success("No changes requested. Keeping current results.");
            return new ReviewResult(true, null);
        }
        
        TerminalUI.printBlankLine();
        TerminalUI.success("Got it! I'll search for: \"" + userPrompt + "\"");
        TerminalUI.printBlankLine();
        
        return new ReviewResult(false, userPrompt);
    }
    
    /**
     * Result of user review.
     */
    public static class ReviewResult {
        private final boolean accepted;
        private final String improvementPrompt;
        
        public ReviewResult(boolean accepted, String improvementPrompt) {
            this.accepted = accepted;
            this.improvementPrompt = improvementPrompt;
        }
        
        public boolean isAccepted() {
            return accepted;
        }
        
        public String getImprovementPrompt() {
            return improvementPrompt;
        }
        
        public boolean needsImprovement() {
            return !accepted && improvementPrompt != null && !improvementPrompt.isEmpty();
        }
    }
}
