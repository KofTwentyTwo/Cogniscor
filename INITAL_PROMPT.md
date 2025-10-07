You are to write a complete Java Maven project.

Project name: Cogniscor  
Base package: com.kof22.cogniscor  

Goal:
Create a command-line application named `Cogniscor` that takes two arguments:
1. Company name
2. Company website URL

The program must:
1. Use all publicly accessible web sources (official website, LinkedIn, Crunchbase, Glassdoor, Wikipedia, news, etc.) to collect information about the company.
2. Summarize what a person should know before applying to that company — including history, size, leadership, mission, culture, products, financials, technology stack, reviews, and recent news.
3. Save every retrieved piece of data (with metadata) into a local Markdown "database":
   - Each source saved as its own `.md` file under `/data/<company_name>/sources/`
   - Each file must include the title, source URL, and retrieved content in Markdown format.

After all data collection:
4. Generate two top-level Markdown reports under `/data/<company_name>/reports/`:
   - `executive_summary.md` — concise, 1–2 page overview.
   - `detailed_report.md` — comprehensive, structured deep-dive covering all major aspects of the company.

Technical Requirements:
- Language: Java 17+
- Build system: Maven
- Root package: `com.kof22.cogniscor`
- Main entry point: `com.kof22.cogniscor.Main`
- Use `java.net.http.HttpClient` for network access and `org.jsoup` for parsing.
- Include modular structure:
  - `scraper` package: handles HTTP fetching and parsing.
  - `analyzer` package: organizes and summarizes text.
  - `writer` package: handles Markdown file generation.
- Support CLI parsing via Picocli.
- Include configuration file `config.properties` for API keys and settings.
- Implement structured logging (SLF4J + Logback).
- Gracefully handle rate limits, connection errors, and missing data.
- Use Markdown-safe text escaping.
- Include a `README.md` explaining usage, configuration, and build steps.

Deliverables:
- Full Maven project directory structure.
- Complete, working source code (no placeholders).
- Functional `pom.xml` with dependencies.
- Example output (Markdown files) for a sample company.
- All source files included in Markdown code blocks with their paths clearly labeled.

Output format:
Present the entire project as a structured code listing, with headings for each file. Use Markdown fenced code blocks for every file’s content.
