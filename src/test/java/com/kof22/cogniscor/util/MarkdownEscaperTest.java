package com.kof22.cogniscor.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MarkdownEscaper utility.
 */
public class MarkdownEscaperTest {
    
    @Test
    public void testEscape() {
        String input = "Test * _ # text";
        String escaped = MarkdownEscaper.escape(input);
        assertTrue(escaped.contains("\\*"));
        assertTrue(escaped.contains("\\_"));
        assertTrue(escaped.contains("\\#"));
    }
    
    @Test
    public void testSanitizeFilename() {
        String input = "Company Name! @#$%";
        String sanitized = MarkdownEscaper.sanitizeFilename(input);
        assertEquals("company_name_", sanitized);
    }
    
    @Test
    public void testTruncate() {
        String input = "This is a long text that needs truncation";
        String truncated = MarkdownEscaper.truncate(input, 20);
        assertEquals("This is a long te...", truncated);
    }
    
    @Test
    public void testTruncateShortText() {
        String input = "Short";
        String truncated = MarkdownEscaper.truncate(input, 20);
        assertEquals("Short", truncated);
    }
    
    @Test
    public void testClean() {
        String input = "Text   with   multiple    spaces";
        String cleaned = MarkdownEscaper.clean(input);
        assertEquals("Text with multiple spaces", cleaned);
    }
}
