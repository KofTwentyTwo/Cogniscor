package com.kof22.cogniscor.llm;

import com.kof22.cogniscor.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses LLM to review and improve generated reports.
 */
public class LLMReportReviewer {
    private static final Logger logger = LoggerFactory.getLogger(LLMReportReviewer.class);
    private final OpenAIService openAIService;
    private final Configuration config;
    
    public LLMReportReviewer(OpenAIService openAIService) {
        this.openAIService = openAIService;
        this.config = Configuration.getInstance();
    }
    
    /**
     * Review and improve an executive summary.
     */
    public String reviewExecutiveSummary(String summary, String companyName) {
        if (!openAIService.isEnabled() || !config.getBooleanProperty("llm.review.reports", false)) {
            logger.debug("LLM report review is disabled");
            return summary;
        }
        
        logger.info("Using LLM to review executive summary for: {}", companyName);
        
        String systemPrompt = """
            You are an expert technical writer reviewing company research reports.
            
            Your task is to review and improve an executive summary about a company.
            
            Requirements:
            1. Ensure the summary is well-written, clear, and professional
            2. Check that facts are presented objectively
            3. Improve grammar, style, and readability
            4. Maintain the markdown format and structure
            5. Keep it concise (1-2 pages maximum)
            6. DO NOT add information that wasn't in the original
            7. DO NOT make up or hallucinate facts
            8. Fix any markdown formatting issues
            9. Ensure consistent formatting of sections
            
            Return the improved version of the summary in the EXACT same markdown format.
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nReview and improve this executive summary:\n\n%s",
            companyName, summary
        );
        
        String reviewed = openAIService.complete(systemPrompt, userPrompt);
        
        if (reviewed != null && !reviewed.trim().isEmpty()) {
            logger.info("Executive summary reviewed and improved by LLM");
            return reviewed.trim();
        }
        
        logger.warn("LLM review failed, returning original summary");
        return summary;
    }
    
    /**
     * Review and improve a detailed report.
     */
    public String reviewDetailedReport(String report, String companyName) {
        if (!openAIService.isEnabled() || !config.getBooleanProperty("llm.review.reports", false)) {
            logger.debug("LLM report review is disabled");
            return report;
        }
        
        logger.info("Using LLM to review detailed report for: {}", companyName);
        
        String systemPrompt = """
            You are an expert technical writer reviewing company research reports.
            
            Your task is to review and improve a detailed company report.
            
            Requirements:
            1. Ensure the report is well-written, clear, and professional
            2. Check that all sections follow a logical structure
            3. Verify facts are presented objectively without speculation
            4. Improve grammar, style, and readability
            5. Maintain the markdown format with proper headings
            6. Ensure table of contents matches actual sections
            7. Fix any markdown formatting issues
            8. Ensure consistent formatting throughout
            9. DO NOT add information that wasn't in the original
            10. DO NOT make up or hallucinate facts
            11. Keep all data sources and attribution
            
            Return the improved version of the report in the EXACT same markdown format.
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nReview and improve this detailed report:\n\n%s",
            companyName, truncate(report, 12000)
        );
        
        String reviewed = openAIService.complete(systemPrompt, userPrompt);
        
        if (reviewed != null && !reviewed.trim().isEmpty()) {
            logger.info("Detailed report reviewed and improved by LLM");
            return reviewed.trim();
        }
        
        logger.warn("LLM review failed, returning original report");
        return report;
    }
    
    /**
     * Verify factual accuracy of extracted information.
     */
    public String verifyFactualAccuracy(String companyName, String content, String extractedInfo) {
        if (!openAIService.isEnabled()) {
            return "LLM verification disabled";
        }
        
        logger.info("Using LLM to verify factual accuracy for: {}", companyName);
        
        String systemPrompt = """
            You are a fact-checker reviewing extracted company information.
            
            Your task is to verify that extracted information is supported by the source content.
            
            Check for:
            1. Facts that are directly stated in the source
            2. Reasonable inferences from the source
            3. Potential hallucinations or unsupported claims
            
            Respond with:
            - "VERIFIED" if all information is supported
            - "CONCERNS: [specific issues]" if there are problems
            """;
        
        String userPrompt = String.format(
            "Company: %s\n\nSource content:\n%s\n\nExtracted information:\n%s\n\nVerify accuracy:",
            companyName, truncate(content, 3000), truncate(extractedInfo, 1000)
        );
        
        String verification = openAIService.complete(systemPrompt, userPrompt);
        
        if (verification != null && verification.contains("CONCERNS")) {
            logger.warn("LLM found potential accuracy issues: {}", verification);
        } else {
            logger.info("LLM verified extracted information");
        }
        
        return verification;
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
