package com.kof22.cogniscor.ui;

import com.kof22.cogniscor.model.CompanyData;
import com.kof22.cogniscor.model.SourceData;

import java.util.List;

/**
 * View layer - handles all presentation logic and output formatting.
 * Follows MVC pattern by separating presentation from business logic.
 */
public class ConsoleView {
    
    /**
     * Display application startup banner.
     */
    public void showBanner() {
        TerminalUI.printMainBanner();
    }
    
    /**
     * Display configuration required message.
     */
    public void showConfigurationRequired(String configPath) {
        TerminalUI.printSectionHeader("⚠  CONFIGURATION REQUIRED  ⚠");
        TerminalUI.warning("Cogniscor is not configured yet.");
        TerminalUI.printBlankLine();
        TerminalUI.info("Please run the setup wizard:");
        TerminalUI.bullet("./cogniscor.sh --setup");
        TerminalUI.printBlankLine();
        TerminalUI.info("Or with the JAR directly:");
        TerminalUI.bullet("java -jar target/cogniscor-1.0-SNAPSHOT.jar --setup");
        TerminalUI.printBlankLine();
        TerminalUI.info("The wizard will create your configuration file at:");
        TerminalUI.bullet(configPath);
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display usage error.
     */
    public void showUsageError() {
        TerminalUI.error("Website URL is required");
        TerminalUI.printBlankLine();
        TerminalUI.info("Usage: cogniscor <website-url>");
        TerminalUI.info("Example: cogniscor https://github.com");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display analysis start header.
     */
    public void showAnalysisStart(String websiteUrl) {
        TerminalUI.printSectionHeader("Analyzing: " + websiteUrl);
    }
    
    /**
     * Display AI enhancement status.
     */
    public void showAIStatus(boolean enabled, String model) {
        if (enabled) {
            TerminalUI.ai("AI Enhancement: ENABLED");
            TerminalUI.success("Using " + model + " for intelligent analysis");
            TerminalUI.success("AI will identify company name from website");
            TerminalUI.success("AI will verify all content is about the same company");
        } else {
            TerminalUI.warning("AI features disabled (no API key configured)");
        }
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display recommendation to enable AI.
     */
    public void showAIRecommendation() {
        TerminalUI.info("Note: AI is recommended for accurate company identification");
        TerminalUI.bullet("Run './cogniscor.sh --setup' to configure AI features");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display phase header.
     */
    public void showPhase(int current, int total, String phaseName) {
        TerminalUI.printPhaseHeader(phaseName, current, total);
    }
    
    /**
     * Display AI capabilities enabled.
     */
    public void showAICapabilities() {
        TerminalUI.ai("AI-powered company identification enabled");
        TerminalUI.ai("AI-powered URL discovery enabled");
        TerminalUI.ai("AI-powered content verification enabled");
    }
    
    /**
     * Display identified company.
     */
    public void showIdentifiedCompany(String companyName, String websiteUrl) {
        TerminalUI.printCompanyHeader(companyName, websiteUrl);
    }
    
    /**
     * Display collection statistics.
     */
    public void showCollectionStats(int successful, int total, int aiDiscovered) {
        TerminalUI.success("Collected data from " + successful + "/" + total + " sources");
        if (aiDiscovered > 0) {
            TerminalUI.ai("AI discovered " + aiDiscovered + " additional sources");
        }
    }
    
    /**
     * Display discovered sources.
     */
    public void showDiscoveredSources(List<SourceData> sources) {
        TerminalUI.printBlankLine();
        TerminalUI.info("AI-Discovered Sources:");
        for (SourceData source : sources) {
            if (source.isSuccessful()) {
                TerminalUI.bullet(source.getSourceType() + ": " + source.getSourceUrl());
            }
        }
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display verification status.
     */
    public void showVerificationStatus(int verified, String companyName) {
        TerminalUI.success("All " + verified + " sources verified to be about " + companyName);
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display analysis complete.
     */
    public void showAnalysisComplete() {
        TerminalUI.success("Analysis complete");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display reports generation.
     */
    public void showReportsGenerating() {
        TerminalUI.ai("AI report review enabled");
    }
    
    /**
     * Display reports generated.
     */
    public void showReportsGenerated() {
        TerminalUI.success("Reports generated");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display final completion.
     */
    public void showCompletion(String outputDir) {
        TerminalUI.printCompletion(outputDir);
    }
    
    /**
     * Display focused research start.
     */
    public void showFocusedResearchStart(String userRequest) {
        TerminalUI.printSectionHeader("🔍 Focused Research");
        TerminalUI.info("User requested: \"" + userRequest + "\"");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display search plan.
     */
    public void showSearchPlan(String focus, int urlCount, int queryCount) {
        TerminalUI.info("Search Plan:");
        TerminalUI.bullet("Focus: " + focus);
        TerminalUI.bullet("URLs to try: " + urlCount);
        TerminalUI.bullet("Search queries: " + queryCount);
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display focused collection progress.
     */
    public void showFocusedCollection(String url) {
        TerminalUI.step("Trying: " + url);
    }
    
    /**
     * Display focused collection results.
     */
    public void showFocusedResults(int successCount) {
        TerminalUI.success("Collected information from " + successCount + " sources");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display report enhancement.
     */
    public void showReportEnhancement() {
        TerminalUI.ai("Enhancing executive summary with focused information...");
    }
    
    /**
     * Display enhancement complete.
     */
    public void showEnhancementComplete() {
        TerminalUI.success("Executive summary updated with focused information");
        TerminalUI.printBlankLine();
    }
    
    /**
     * Display refinement complete.
     */
    public void showRefinementComplete() {
        TerminalUI.printSuccess("Research Refinement Complete", null);
    }
    
    /**
     * Display error.
     */
    public void showError(String message) {
        TerminalUI.error(message);
    }
    
    /**
     * Display summary with statistics table.
     */
    public void showSummaryTable(CompanyData companyData) {
        TerminalUI.printSectionHeader("Research Summary");
        
        TerminalUI.printTableHeader("Metric", "Value");
        TerminalUI.printTableRow("Company", companyData.getCompanyName());
        TerminalUI.printTableRow("Website", companyData.getWebsiteUrl());
        TerminalUI.printTableRow("Sources", String.valueOf(companyData.getSources().size()));
        TerminalUI.printTableRow("Executives", String.valueOf(companyData.getLeadership().size()));
        TerminalUI.printTableRow("Products", String.valueOf(companyData.getProducts().size()));
        TerminalUI.printTableRow("Technologies", String.valueOf(companyData.getTechnologies().size()));
        TerminalUI.printTableRow("News Items", String.valueOf(companyData.getRecentNews().size()));
        TerminalUI.printTableFooter();
    }
}
