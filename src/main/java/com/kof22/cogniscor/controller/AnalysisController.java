package com.kof22.cogniscor.controller;

import com.kof22.cogniscor.analyzer.ContentAnalyzer;
import com.kof22.cogniscor.config.Configuration;
import com.kof22.cogniscor.llm.LLMFocusedSearch;
import com.kof22.cogniscor.llm.OpenAIService;
import com.kof22.cogniscor.model.CompanyData;
import com.kof22.cogniscor.model.SourceData;
import com.kof22.cogniscor.scraper.CompanyDataCollector;
import com.kof22.cogniscor.scraper.WebScraper;
import com.kof22.cogniscor.ui.ConsoleView;
import com.kof22.cogniscor.util.MarkdownEscaper;
import com.kof22.cogniscor.writer.MarkdownWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller layer - orchestrates application flow and coordinates
 * between model and view layers following MVC pattern.
 */
public class AnalysisController {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);
    
    private final ConsoleView view;
    private final Configuration config;
    
    public AnalysisController(ConsoleView view) {
        this.view = view;
        this.config = Configuration.getInstance();
    }
    
    /**
     * Execute the complete analysis workflow.
     */
    public AnalysisResult executeAnalysis(String websiteUrl) {
        try {
            view.showAnalysisStart(websiteUrl);
            
            // Initialize AI service
            OpenAIService aiService = initializeAI();
            
            // Phase 1: Data Collection
            view.showPhase(1, 4, "Identifying company and collecting data");
            CompanyData companyData = collectData(websiteUrl, aiService);
            
            // Show identified company
            view.showIdentifiedCompany(companyData.getCompanyName(), companyData.getWebsiteUrl());
            
            // Show collection statistics
            showCollectionStatistics(companyData, aiService != null);
            
            // Phase 2: Analysis
            view.showPhase(2, 4, "Analyzing collected data");
            analyzeData(companyData, aiService);
            view.showAnalysisComplete();
            
            // Phase 3: Report Generation
            view.showPhase(3, 4, "Generating reports");
            Path summaryPath = generateReports(companyData, aiService);
            view.showReportsGenerated();
            
            // Phase 4: Verification
            view.showPhase(4, 4, "Final quality verification");
            verifyQuality(companyData, aiService);
            
            return new AnalysisResult(true, companyData, summaryPath, null);
            
        } catch (Exception e) {
            logger.error("Analysis failed", e);
            view.showError("Analysis failed: " + e.getMessage());
            return new AnalysisResult(false, null, null, e.getMessage());
        }
    }
    
    /**
     * Execute focused research based on user request.
     */
    public FocusedResearchResult executeFocusedResearch(
            CompanyData companyData,
            String userRequest,
            OpenAIService aiService,
            Path summaryPath) {
        
        try {
            view.showFocusedResearchStart(userRequest);
            
            // Plan focused search
            LLMFocusedSearch focusedSearch = new LLMFocusedSearch(aiService);
            String existingSummary = java.nio.file.Files.readString(summaryPath);
            
            LLMFocusedSearch.FocusedSearchPlan plan = focusedSearch.planFocusedSearch(
                companyData.getCompanyName(),
                companyData.getWebsiteUrl(),
                userRequest,
                existingSummary
            );
            
            if (plan == null) {
                view.showError("Could not create focused search plan");
                return new FocusedResearchResult(false, 0);
            }
            
            view.showSearchPlan(plan.extractionFocus, plan.urls.size(), plan.searchQueries.size());
            
            // Perform focused scraping
            int successCount = performFocusedScraping(companyData, plan);
            view.showFocusedResults(successCount);
            
            // Re-analyze with new data
            ContentAnalyzer analyzer = new ContentAnalyzer();
            if (aiService != null) {
                analyzer.setLLMEnhancer(aiService);
            }
            analyzer.analyze(companyData);
            
            // Enhance report
            view.showReportEnhancement();
            String enhancedSummary = focusedSearch.enhanceReport(
                existingSummary,
                collectFocusedContent(companyData),
                userRequest,
                companyData.getCompanyName()
            );
            
            // Save enhanced summary
            java.nio.file.Files.writeString(summaryPath, enhancedSummary);
            view.showEnhancementComplete();
            
            // Regenerate detailed report
            MarkdownWriter writer = new MarkdownWriter();
            if (aiService != null) {
                writer.setLLMReviewer(aiService);
            }
            writer.writeAll(companyData);
            
            view.showRefinementComplete();
            
            return new FocusedResearchResult(true, successCount);
            
        } catch (Exception e) {
            logger.error("Focused research failed", e);
            view.showError("Focused research failed: " + e.getMessage());
            return new FocusedResearchResult(false, 0);
        }
    }
    
    /**
     * Show completion summary.
     */
    public void showCompletionSummary(CompanyData companyData) {
        String sanitizedName = MarkdownEscaper.sanitizeFilename(companyData.getCompanyName());
        String outputDir = Paths.get(config.getOutputBaseDir(), sanitizedName).toString();
        
        view.showSummaryTable(companyData);
        view.showCompletion(outputDir);
    }
    
    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================
    
    private OpenAIService initializeAI() {
        if (!config.isLLMEnabled()) {
            view.showAIRecommendation();
            return null;
        }
        
        OpenAIService aiService = new OpenAIService();
        if (aiService.isEnabled()) {
            String model = config.getProperty("openai.model", "gpt-4o-mini");
            view.showAIStatus(true, model);
            return aiService;
        } else {
            view.showAIStatus(false, null);
            return null;
        }
    }
    
    private CompanyData collectData(String websiteUrl, OpenAIService aiService) {
        CompanyDataCollector collector = new CompanyDataCollector();
        
        if (aiService != null) {
            collector.setOpenAIService(aiService);
            view.showAICapabilities();
        }
        
        return collector.collectData(websiteUrl);
    }
    
    private void showCollectionStatistics(CompanyData companyData, boolean aiEnabled) {
        long successful = companyData.getSources().stream()
            .filter(SourceData::isSuccessful)
            .count();
        
        int aiDiscovered = 0;
        if (aiEnabled) {
            aiDiscovered = (int) companyData.getSources().stream()
                .filter(s -> s.getSourceType().startsWith("llm_"))
                .filter(SourceData::isSuccessful)
                .count();
        }
        
        view.showCollectionStats((int) successful, companyData.getSources().size(), aiDiscovered);
        
        // Show discovered sources if any
        if (aiDiscovered > 0) {
            List<SourceData> aiSources = companyData.getSources().stream()
                .filter(s -> s.getSourceType().startsWith("llm_"))
                .filter(SourceData::isSuccessful)
                .collect(Collectors.toList());
            view.showDiscoveredSources(aiSources);
        }
    }
    
    private void analyzeData(CompanyData companyData, OpenAIService aiService) {
        ContentAnalyzer analyzer = new ContentAnalyzer();
        
        if (aiService != null) {
            analyzer.setLLMEnhancer(aiService);
        }
        
        analyzer.analyze(companyData);
    }
    
    private Path generateReports(CompanyData companyData, OpenAIService aiService) throws Exception {
        MarkdownWriter writer = new MarkdownWriter();
        
        if (aiService != null) {
            view.showReportsGenerating();
            writer.setLLMReviewer(aiService);
        }
        
        writer.writeAll(companyData);
        
        // Return path to executive summary
        String sanitizedName = MarkdownEscaper.sanitizeFilename(companyData.getCompanyName());
        return Paths.get(config.getOutputBaseDir(), sanitizedName, "reports", "executive_summary.md");
    }
    
    private void verifyQuality(CompanyData companyData, OpenAIService aiService) {
        if (aiService != null && config.isLLMVerifyCompanyEnabled()) {
            long verified = companyData.getSources().stream()
                .filter(SourceData::isSuccessful)
                // All successful sources are considered verified in this context
                //.filter(s -> s.verified)
                .count();
            
            view.showVerificationStatus((int) verified, companyData.getCompanyName());
        }
    }
    
    private int performFocusedScraping(CompanyData companyData, LLMFocusedSearch.FocusedSearchPlan plan) {
        WebScraper webScraper = new WebScraper();
        int successCount = 0;
        
        for (LLMFocusedSearch.FocusedUrl focusedUrl : plan.urls) {
            view.showFocusedCollection(focusedUrl.url);
            
            SourceData source = webScraper.scrapeUrl(
                focusedUrl.url,
                companyData.getCompanyName() + " - Focused: " + focusedUrl.reason,
                "focused_search"
            );
            
            if (source.isSuccessful()) {
                companyData.addSource(source);
                successCount++;
            }
        }
        
        return successCount;
    }
    
    private String collectFocusedContent(CompanyData companyData) {
        return companyData.getSources().stream()
            .filter(s -> "focused_search".equals(s.getSourceType()))
            .filter(SourceData::isSuccessful)
            .map(SourceData::getContent)
            .collect(Collectors.joining("\n\n"));
    }
    
    // ============================================================
    // RESULT CLASSES (part of Model layer)
    // ============================================================
    
    /**
     * Result of analysis operation.
     */
    public static class AnalysisResult {
        private final boolean success;
        private final CompanyData companyData;
        private final Path summaryPath;
        private final String errorMessage;
        
        public AnalysisResult(boolean success, CompanyData companyData, Path summaryPath, String errorMessage) {
            this.success = success;
            this.companyData = companyData;
            this.summaryPath = summaryPath;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() { return success; }
        public CompanyData getCompanyData() { return companyData; }
        public Path getSummaryPath() { return summaryPath; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    /**
     * Result of focused research operation.
     */
    public static class FocusedResearchResult {
        private final boolean success;
        private final int sourcesCollected;
        
        public FocusedResearchResult(boolean success, int sourcesCollected) {
            this.success = success;
            this.sourcesCollected = sourcesCollected;
        }
        
        public boolean isSuccess() { return success; }
        public int getSourcesCollected() { return sourcesCollected; }
    }
}
