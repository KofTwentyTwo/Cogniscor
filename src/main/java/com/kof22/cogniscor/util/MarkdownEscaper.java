package com.kof22.cogniscor.util;

/**
 * Utility class for escaping text to be safely used in Markdown files.
 */
public class MarkdownEscaper {
    
    /**
     * Escapes special Markdown characters in text.
     */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        
        return text
            .replace("\\", "\\\\")
            .replace("`", "\\`")
            .replace("*", "\\*")
            .replace("_", "\\_")
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("#", "\\#");
    }
    
    /**
     * Cleans text for use in Markdown without full escaping.
     * Removes problematic characters while preserving readability.
     */
    public static String clean(String text) {
        if (text == null) {
            return "";
        }
        
        return text
            .replaceAll("\\s+", " ")  // Normalize whitespace
            .trim();
    }
    
    /**
     * Sanitizes text for use as a filename.
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unknown";
        }
        
        return filename
            .replaceAll("[^a-zA-Z0-9._-]", "_")
            .replaceAll("_+", "_")
            .toLowerCase();
    }
    
    /**
     * Truncates text to a maximum length, adding ellipsis if needed.
     */
    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        
        return text.substring(0, maxLength - 3) + "...";
    }
}
