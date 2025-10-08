package com.kof22.cogniscor.ui;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color;

/**
 * Beautiful Terminal UI with colors, boxes, and formatting.
 */
public class TerminalUI {
    
    private static boolean initialized = false;
    private static final int WIDTH = 80;
    
    public static void initialize() {
        if (!initialized) {
            AnsiConsole.systemInstall();
            initialized = true;
        }
    }
    
    public static void shutdown() {
        if (initialized) {
            AnsiConsole.systemUninstall();
        }
    }
    
    // ============================================================
    // HEADERS & BANNERS
    // ============================================================
    
    public static void printMainBanner() {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.CYAN).a(
            "╔════════════════════════════════════════════════════════════════════════════╗\n" +
            "║                                                                            ║\n" +
            "║                          🤖 COGNISCOR v2.0 🤖                              ║\n" +
            "║                                                                            ║\n" +
            "║                AI-Powered Company Research & Analysis                      ║\n" +
            "║                                                                            ║\n" +
            "╚════════════════════════════════════════════════════════════════════════════╝"
        ).reset());
        System.out.println();
    }
    
    public static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.YELLOW).a("╔" + "═".repeat(WIDTH - 2) + "╗").reset());
        System.out.println(ansi().bold().fg(Color.YELLOW).a("║  " + title + " ".repeat(WIDTH - title.length() - 5) + "║").reset());
        System.out.println(ansi().bold().fg(Color.YELLOW).a("╚" + "═".repeat(WIDTH - 2) + "╝").reset());
        System.out.println();
    }
    
    public static void printPhaseHeader(String phase, int current, int total) {
        String title = String.format("Step %d/%d: %s", current, total, phase);
        System.out.println();
        System.out.println(ansi().bold().fg(Color.MAGENTA).a("▓▓▓ " + title + " " + "▓".repeat(Math.max(0, WIDTH - title.length() - 6))).reset());
        System.out.println();
    }
    
    public static void printBox(String title, String content) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.CYAN).a("┌" + "─".repeat(WIDTH - 2) + "┐").reset());
        if (title != null && !title.isEmpty()) {
            String titleLine = "│  " + title + " ".repeat(WIDTH - title.length() - 5) + "│";
            System.out.println(ansi().bold().fg(Color.CYAN).a(titleLine).reset());
            System.out.println(ansi().bold().fg(Color.CYAN).a("├" + "─".repeat(WIDTH - 2) + "┤").reset());
        }
        
        for (String line : content.split("\n")) {
            String padded = "│ " + line + " ".repeat(Math.max(0, WIDTH - line.length() - 3)) + "│";
            System.out.println(ansi().fg(Color.CYAN).a(padded).reset());
        }
        
        System.out.println(ansi().bold().fg(Color.CYAN).a("└" + "─".repeat(WIDTH - 2) + "┘").reset());
        System.out.println();
    }
    
    // ============================================================
    // STATUS MESSAGES
    // ============================================================
    
    public static void success(String message) {
        System.out.println(ansi().bold().fg(Color.GREEN).a("   ✓ ").reset().a(message));
    }
    
    public static void info(String message) {
        System.out.println(ansi().fg(Color.BLUE).a("   ℹ ").reset().a(message));
    }
    
    public static void warning(String message) {
        System.out.println(ansi().fg(Color.YELLOW).a("   ⚠ ").reset().a(message));
    }
    
    public static void error(String message) {
        System.out.println(ansi().bold().fg(Color.RED).a("   ✗ ").reset().a(message));
    }
    
    public static void ai(String message) {
        System.out.println(ansi().fg(Color.MAGENTA).a("   🤖 ").reset().a(message));
    }
    
    public static void step(String message) {
        System.out.println(ansi().fg(Color.CYAN).a("   → ").reset().a(message));
    }
    
    public static void bullet(String message) {
        System.out.println(ansi().fg(Color.WHITE).a("      • ").reset().a(message));
    }
    
    // ============================================================
    // PROGRESS & STATS
    // ============================================================
    
    public static void printProgressBar(String label, int current, int total) {
        int barWidth = 40;
        int filled = (int) ((double) current / total * barWidth);
        int empty = barWidth - filled;
        
        String bar = ansi()
            .fg(Color.GREEN).a("█".repeat(filled)).reset()
            .fg(Color.BLACK).a("░".repeat(empty)).reset()
            .toString();
        
        String percentage = String.format("%3d%%", (current * 100 / total));
        System.out.println(ansi().a("   " + label + ": [").a(bar).a("] " + percentage + " (" + current + "/" + total + ")").reset());
    }
    
    public static void printStatistic(String label, String value) {
        System.out.println(ansi().fg(Color.CYAN).a("   " + label + ": ").reset().bold().a(value).reset());
    }
    
    public static void printKeyValue(String key, String value) {
        String dots = ".".repeat(Math.max(1, 40 - key.length() - value.length()));
        System.out.println(ansi().fg(Color.CYAN).a("   " + key + " ").fg(Color.BLACK).a(dots + " ").reset().bold().a(value).reset());
    }
    
    // ============================================================
    // TABLES
    // ============================================================
    
    public static void printTableHeader(String... columns) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.CYAN).a("   ┌" + "─".repeat(WIDTH - 5) + "┐").reset());
        
        StringBuilder header = new StringBuilder("   │ ");
        int colWidth = (WIDTH - 8) / columns.length;
        for (String col : columns) {
            header.append(String.format("%-" + colWidth + "s", col));
        }
        header.append("│");
        System.out.println(ansi().bold().a(header.toString()).reset());
        System.out.println(ansi().bold().fg(Color.CYAN).a("   ├" + "─".repeat(WIDTH - 5) + "┤").reset());
    }
    
    public static void printTableRow(String... values) {
        StringBuilder row = new StringBuilder("   │ ");
        int colWidth = (WIDTH - 8) / values.length;
        for (String val : values) {
            String truncated = val.length() > colWidth ? val.substring(0, colWidth - 3) + "..." : val;
            row.append(String.format("%-" + colWidth + "s", truncated));
        }
        row.append("│");
        System.out.println(ansi().fg(Color.WHITE).a(row.toString()).reset());
    }
    
    public static void printTableFooter() {
        System.out.println(ansi().bold().fg(Color.CYAN).a("   └" + "─".repeat(WIDTH - 5) + "┘").reset());
        System.out.println();
    }
    
    // ============================================================
    // INTERACTIVE PROMPTS
    // ============================================================
    
    public static void printPrompt(String message) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.YELLOW).a("   ❓ " + message).reset());
        System.out.print(ansi().bold().fg(Color.CYAN).a("   ➤ ").reset());
    }
    
    public static void printChoice(String number, String description) {
        System.out.println(ansi().fg(Color.GREEN).a("      [" + number + "] ").reset().a(description));
    }
    
    // ============================================================
    // SPECIAL DISPLAYS
    // ============================================================
    
    public static void printCompanyHeader(String companyName, String website) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ╔" + "═".repeat(WIDTH - 5) + "╗"
        ).reset());
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ║  🏢 " + companyName + " ".repeat(Math.max(0, WIDTH - companyName.length() - 12)) + "║"
        ).reset());
        System.out.println(ansi().fg(Color.GREEN).a(
            "   ║  🌐 " + website + " ".repeat(Math.max(0, WIDTH - website.length() - 12)) + "║"
        ).reset());
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ╚" + "═".repeat(WIDTH - 5) + "╝"
        ).reset());
        System.out.println();
    }
    
    public static void printSummaryPreview(String summary) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.MAGENTA).a(
            "   ╔" + "═".repeat(WIDTH - 5) + "╗"
        ).reset());
        System.out.println(ansi().bold().fg(Color.MAGENTA).a(
            "   ║" + centerText("📄 EXECUTIVE SUMMARY PREVIEW", WIDTH - 4) + "║"
        ).reset());
        System.out.println(ansi().bold().fg(Color.MAGENTA).a(
            "   ╚" + "═".repeat(WIDTH - 5) + "╝"
        ).reset());
        System.out.println();
        
        String[] lines = summary.split("\n");
        int maxLines = 50;
        
        for (int i = 0; i < Math.min(lines.length, maxLines); i++) {
            String line = lines[i];
            if (line.startsWith("#")) {
                System.out.println(ansi().bold().fg(Color.YELLOW).a("   " + line).reset());
            } else if (line.startsWith("**")) {
                System.out.println(ansi().bold().a("   " + line).reset());
            } else if (line.trim().startsWith("-")) {
                System.out.println(ansi().fg(Color.CYAN).a("   " + line).reset());
            } else {
                System.out.println(ansi().fg(Color.WHITE).a("   " + line).reset());
            }
        }
        
        if (lines.length > maxLines) {
            System.out.println();
            System.out.println(ansi().fg(Color.BLACK).a("   ... (" + (lines.length - maxLines) + " more lines) ...").reset());
        }
        
        System.out.println();
        System.out.println(ansi().bold().fg(Color.BLACK).a("   " + "═".repeat(WIDTH - 4)).reset());
        System.out.println();
    }
    
    public static void printReviewPrompt() {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.CYAN).a(
            "   ╔" + "═".repeat(WIDTH - 5) + "╗"
        ).reset());
        System.out.println(ansi().bold().fg(Color.CYAN).a(
            "   ║" + centerText("🎯 REVIEW & FEEDBACK", WIDTH - 4) + "║"
        ).reset());
        System.out.println(ansi().bold().fg(Color.CYAN).a(
            "   ╚" + "═".repeat(WIDTH - 5) + "╝"
        ).reset());
        System.out.println();
    }
    
    public static void printImprovementPrompt() {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.MAGENTA).a(
            "   ╔" + "═".repeat(WIDTH - 5) + "╗"
        ).reset());
        System.out.println(ansi().bold().fg(Color.MAGENTA).a(
            "   ║" + centerText("🔍 CUSTOM SEARCH REFINEMENT", WIDTH - 4) + "║"
        ).reset());
        System.out.println(ansi().bold().fg(Color.MAGENTA).a(
            "   ╚" + "═".repeat(WIDTH - 5) + "╝"
        ).reset());
        System.out.println();
    }
    
    // ============================================================
    // ANIMATIONS & SPINNERS
    // ============================================================
    
    public static void printLoading(String message) {
        System.out.print(ansi().fg(Color.YELLOW).a("   ⏳ " + message + "...").reset());
        System.out.flush();
    }
    
    public static void clearLine() {
        System.out.print("\r" + " ".repeat(WIDTH));
        System.out.print("\r");
        System.out.flush();
    }
    
    public static void printDone() {
        System.out.println(ansi().bold().fg(Color.GREEN).a(" ✓").reset());
    }
    
    // ============================================================
    // COMPLETION & RESULTS
    // ============================================================
    
    public static void printSuccess(String title, String message) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ╔" + "═".repeat(WIDTH - 5) + "╗"
        ).reset());
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ║" + centerText("✨ " + title + " ✨", WIDTH - 4) + "║"
        ).reset());
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ╚" + "═".repeat(WIDTH - 5) + "╝"
        ).reset());
        System.out.println();
        if (message != null && !message.isEmpty()) {
            System.out.println(ansi().fg(Color.GREEN).a("   " + message).reset());
            System.out.println();
        }
    }
    
    public static void printCompletion(String outputDir) {
        System.out.println();
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ╔" + "═".repeat(WIDTH - 5) + "╗"
        ).reset());
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ║" + centerText("🎉 RESEARCH COMPLETE 🎉", WIDTH - 4) + "║"
        ).reset());
        System.out.println(ansi().bold().fg(Color.GREEN).a(
            "   ╚" + "═".repeat(WIDTH - 5) + "╝"
        ).reset());
        System.out.println();
        
        System.out.println(ansi().fg(Color.CYAN).a("   📁 Results saved to:").reset());
        System.out.println(ansi().bold().a("      " + outputDir).reset());
        System.out.println();
        
        System.out.println(ansi().fg(Color.CYAN).a("   📄 View reports:").reset());
        System.out.println(ansi().fg(Color.WHITE).a("      cat " + outputDir + "/reports/executive_summary.md").reset());
        System.out.println(ansi().fg(Color.WHITE).a("      cat " + outputDir + "/reports/detailed_report.md").reset());
        System.out.println();
        
        System.out.println(ansi().fg(Color.YELLOW).a("   ✨ Review the generated reports for detailed insights!").reset());
        System.out.println();
    }
    
    // ============================================================
    // UTILITIES
    // ============================================================
    
    private static String centerText(String text, int width) {
        // Remove emoji for length calculation
        String textNoEmoji = text.replaceAll("[^\\x00-\\x7F]", "");
        int padding = (width - textNoEmoji.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - textNoEmoji.length() - padding));
    }
    
    public static void printSeparator() {
        System.out.println(ansi().fg(Color.BLACK).a("   " + "─".repeat(WIDTH - 4)).reset());
    }
    
    public static void printBlankLine() {
        System.out.println();
    }
}
