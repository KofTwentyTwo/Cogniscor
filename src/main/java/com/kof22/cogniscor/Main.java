package com.kof22.cogniscor;

import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.controller.AnalysisController;
import com.kof22.cogniscor.interactive.InteractiveReview;
import com.kof22.cogniscor.llm.OpenAIService;
import com.kof22.cogniscor.setup.SetupWizard;
import com.kof22.cogniscor.ui.ConsoleView;
import com.kof22.cogniscor.ui.TerminalUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * Main entry point for Cogniscor application.
 * 
 * Cogniscor is a company research tool that collects and analyzes
 * publicly available information to help job applicants prepare
 * for interviews and application processes.
 */
@Command(
    name = "cogniscor",
    description = "Company research and analysis tool for job applicants",
    version = "2.0.0",
    mixinStandardHelpOptions = true
)
public class Main implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    // View layer
    private final ConsoleView view = new ConsoleView();
    
    // CLI parameters
    @Parameters(
        index = "0",
        description = "Company website URL (e.g., 'https://www.github.com', 'stripe.com')",
        arity = "0..1"
    )
    private String websiteUrl;
    
    @Option(
        names = {"-v", "--verbose"},
        description = "Enable verbose logging"
    )
    private boolean verbose;
    
    @Option(
        names = {"-o", "--output"},
        description = "Output directory (default: ./data)"
    )
    private String outputDir;
    
    @Option(
        names = {"--setup"},
        description = "Run interactive setup wizard"
    )
    private boolean runSetup;
    
    @Option(
        names = {"--no-interactive"},
        description = "Skip interactive review (auto-accept results)"
    )
    private boolean noInteractive;
    
    public static void main(String[] args) {
        TerminalUI.initialize();
        try {
            int exitCode = new CommandLine(new Main()).execute(args);
            System.exit(exitCode);
        } finally {
            TerminalUI.shutdown();
        }
    }
    
    @Override
    public Integer call() {
        try {
            // Check if setup was requested
            if (runSetup) {
                SetupWizard wizard = new SetupWizard();
                return wizard.runSetup() ? 0 : 1;
            }
            
            // Check if configuration exists
            if (!Configuration.userConfigExists()) {
                view.showConfigurationRequired(SetupWizard.getConfigFilePath().toString());
                return 1;
            }
            
            // Show banner
            view.showBanner();
            
            // Validate inputs
            if (websiteUrl == null || websiteUrl.trim().isEmpty()) {
                view.showUsageError();
                return 1;
            }
            
            // Run the analysis using controller
            return executeAnalysisWorkflow(websiteUrl.trim());
            
        } catch (Exception e) {
            logger.error("Fatal error", e);
            view.showError("Fatal error: " + e.getMessage());
            return 1;
        }
    }
    
    /**
     * Execute the complete analysis workflow following MVC pattern.
     * This method coordinates between Controller and View layers.
     */
    private int executeAnalysisWorkflow(String websiteUrl) {
        // Create controller
        AnalysisController controller = new AnalysisController(view);
        
        // Execute analysis
        AnalysisController.AnalysisResult result = controller.executeAnalysis(websiteUrl);
        
        if (!result.isSuccess()) {
            return 1;
        }
        
        // Interactive review (if enabled and AI available)
        if (!noInteractive && hasAIEnabled()) {
            return handleInteractiveReview(controller, result);
        }
        
        // Show completion
        controller.showCompletionSummary(result.getCompanyData());
        return 0;
    }
    
    /**
     * Check if AI is enabled.
     */
    private boolean hasAIEnabled() {
        Configuration config = Configuration.getInstance();
        if (!config.isLLMEnabled()) {
            return false;
        }
        
        OpenAIService aiService = new OpenAIService();
        return aiService.isEnabled();
    }
    
    /**
     * Handle interactive review workflow.
     */
    private int handleInteractiveReview(AnalysisController controller, 
                                        AnalysisController.AnalysisResult result) {
        try {
            InteractiveReview review = new InteractiveReview();
            
            // Show summary and get feedback
            InteractiveReview.ReviewResult reviewResult = review.reviewWithUser(
                result.getSummaryPath(), 
                result.getCompanyData()
            );
            
            if (reviewResult.isAccepted()) {
                controller.showCompletionSummary(result.getCompanyData());
                return 0;
            }
            
            // User wants improvements
            if (reviewResult.needsImprovement()) {
                OpenAIService aiService = new OpenAIService();
                
                AnalysisController.FocusedResearchResult focusedResult = 
                    controller.executeFocusedResearch(
                        result.getCompanyData(),
                        reviewResult.getImprovementPrompt(),
                        aiService,
                        result.getSummaryPath()
                    );
                
                if (focusedResult.isSuccess()) {
                    // Recursively review again
                    return handleInteractiveReview(controller, result);
                }
            }
            
            controller.showCompletionSummary(result.getCompanyData());
            return 0;
            
        } catch (Exception e) {
            logger.error("Interactive review failed", e);
            view.showError("Interactive review failed: " + e.getMessage());
            return 0; // Still show results
        }
    }
}
