# Cogniscor

**From Latin _cognoscere_ ("to learn") — a tool for discovering company insights**

Cogniscor is a command-line application that researches companies by collecting and analyzing publicly available information from the web. It's designed to help job applicants prepare thoroughly before applying to a company.

## Features

- 🔍 **Multi-Source Research**: Collects data from company websites, Wikipedia, LinkedIn, Crunchbase, Glassdoor, and news sources
- 📊 **Intelligent Analysis**: Extracts structured information about:
  - **Executive Team**: C-suite, VP-level executives, and board members
  - **Recent News**: Latest developments, funding rounds, partnerships, acquisitions
  - **Products & Services**: Complete product catalog with descriptions
  - **Technology Stack**: Programming languages, frameworks, cloud platforms, databases, DevOps tools
  - Company history, culture, reviews, and more
- 🤖 **AI-Enhanced Analysis** (Optional): Use ChatGPT to:
  - **Identify company name** from website automatically
  - **Verify 100%** that all sources are about the same company
  - **Discover optimal URLs** for each company
  - **Recover from failures** with intelligent alternatives
  - Fill in gaps in extracted information
  - Improve report quality, grammar, and formatting
  - **Interactive mode**: Review results and request focused improvements
  - **Never hallucinates or adds unsupported facts**
- 📝 **Professional Reports**: Generates both executive summaries and detailed reports in Markdown format
- 💾 **Data Preservation**: Saves all raw source data with metadata for reference
- ⚡ **Rate Limiting**: Respects web servers with configurable rate limiting
- 🛡️ **Error Handling**: Gracefully handles connection errors, timeouts, and missing data

## Requirements

- Java 17 or higher
- Maven 3.6+
- Internet connection

## Installation & Build

1. **Clone the repository** (or ensure you're in the project directory):
```bash
cd Cogniscor
```

2. **Build the project**:
```bash
mvn clean package
```

This will create an executable JAR file: `target/cogniscor-1.0-SNAPSHOT.jar`

## Usage

### First Time Setup

Run the interactive setup wizard:

```bash
./cogniscor.sh --setup
```

This will:
- Create configuration at `~/.cogniscor/config.properties`
- Prompt for your OpenAI API key (optional)
- Configure AI features and preferences
- Takes less than 2 minutes!

### Running Analysis

After setup, use the shell script:

```bash
./cogniscor.sh "Company Name" "https://company-website.com"
```

The shell script (`cogniscor.sh`) provides a friendly interface that:
- ✅ Checks for configuration (prompts setup if needed)
- ✅ Automatically checks for Java
- ✅ Builds the project if needed
- ✅ Provides colorful, helpful output
- ✅ Shows you where results are saved

### Examples

Research GitHub:
```bash
./cogniscor.sh "https://github.com"
```

Research Stripe:
```bash
./cogniscor.sh "https://stripe.com"
```

With verbose logging:
```bash
./cogniscor.sh -v "https://www.google.com"
```

Custom output directory:
```bash
./cogniscor.sh --output ./research "https://microsoft.com"
```

**Note**: Company name is automatically detected from the website! 

**With AI enabled**, you'll also get:
- ✅ Interactive review of the executive summary
- ✅ Ability to request focused improvements
- ✅ 100% verification all sources are about the same company

To skip interactive review (for automation):
```bash
./cogniscor.sh --no-interactive "https://company.com"
```

### Direct JAR Usage (Alternative)

If you prefer to run the JAR directly:

```bash
java -jar target/cogniscor-1.0-SNAPSHOT.jar "https://company-website.com"
```

### Shell Script Options

```
Usage: ./cogniscor.sh [OPTIONS] <website-url>

Arguments:
  <website-url>     Company's official website URL

Options:
  -h, --help        Show this help message
  -v, --verbose     Enable verbose logging
  -o, --output DIR  Output directory (default: ./data)
  --setup           Run interactive setup wizard
  --build           Force rebuild before running
  --version         Show version information
```

### Direct JAR Options

```
Usage: java -jar target/cogniscor-1.0-SNAPSHOT.jar [-hVv] [--setup] [-o=<outputDir>] [<websiteUrl>]

Parameters:
  <websiteUrl>          Company website URL (e.g., 'https://www.github.com', 'stripe.com')

Options:
  -h, --help            Show this help message and exit
  -V, --version         Print version information and exit
  -v, --verbose         Enable verbose logging
  -o, --output=<dir>    Output directory (default: ./data)
  --setup               Run interactive setup wizard
```

## Output Structure

After running Cogniscor, you'll find organized data in the following structure:

```
data/
└── company_name/
    ├── sources/
    │   ├── 01_official_website.md
    │   ├── 02_wikipedia.md
    │   ├── 03_linkedin.md
    │   ├── 04_crunchbase.md
    │   ├── 05_glassdoor.md
    │   └── 06_news.md
    └── reports/
        ├── executive_summary.md
        └── detailed_report.md
```

### Report Contents

**Executive Summary** (`executive_summary.md`):
- Company overview and key facts
- **Executive team** (CEO, CTO, CFO, VPs, etc.)
- Mission, values, and culture
- **Complete product lineup** with descriptions
- **Technology stack** (languages, frameworks, platforms)
- Employee review highlights
- **Recent news** with dates and details

**Detailed Report** (`detailed_report.md`):
- Comprehensive company overview
- Complete history and background
- **Full executive team** with backgrounds and experience
- **Detailed product catalog** with features and capabilities
- **Comprehensive technology stack**:
  - Programming languages
  - Frontend/backend frameworks
  - Cloud platforms and infrastructure
  - Databases and storage
  - AI/ML tools
  - Security measures
- Company culture deep-dive
- Complete employee reviews
- Financial information
- **Extensive recent news** categorized by:
  - Major announcements
  - Product launches
  - Funding and acquisitions
  - Awards and recognition
  - Expansion and growth
- List of all data sources with links

## AI Enhancement (Optional)

Cogniscor can use OpenAI's ChatGPT to dramatically improve analysis quality:

### Quick Setup

1. Get an API key from https://platform.openai.com/api-keys
2. Edit `src/main/resources/config.properties`:
```properties
openai.api.key=sk-your-key-here
llm.enabled=true
```
3. Rebuild: `mvn clean package`
4. Run normally: `./cogniscor.sh "Company" "https://company.com"`

**Cost**: ~$0.025 per company with `gpt-4o-mini` (recommended)

See **[LLM_SETUP_GUIDE.md](LLM_SETUP_GUIDE.md)** for detailed setup instructions.

### What AI Does

✅ Finds executives, products, and tech stack that regex patterns miss
✅ Improves report clarity, grammar, and formatting
✅ Verifies facts against source material
❌ Never adds information that isn't in the sources

### Without AI

The tool works great without AI using advanced regex patterns. AI just makes it better!

## Configuration

Edit `src/main/resources/config.properties` to customize behavior:

```properties
# HTTP Client Settings
http.timeout.seconds=30
http.user.agent=Mozilla/5.0 ...
http.max.redirects=5

# Rate Limiting
rate.limit.delay.ms=2000
rate.limit.enabled=true

# Scraping Settings
scraper.max.retries=3
scraper.retry.delay.ms=5000

# Output Settings
output.base.dir=./data

# Analysis Settings
analyzer.max.summary.words=500
analyzer.executive.summary.max.words=800
```

## Architecture

The project follows a modular architecture:

```
com.kof22.cogniscor/
├── Main.java                    # CLI entry point
├── config/
│   └── Configuration.java       # Configuration management
├── model/
│   ├── CompanyData.java         # Main data model
│   ├── SourceData.java          # Source metadata
│   └── CompanyReviews.java      # Review aggregation
├── scraper/
│   ├── HttpClientFactory.java   # HTTP client creation
│   ├── WebScraper.java          # Core scraping logic
│   └── CompanyDataCollector.java # Multi-source orchestration
├── analyzer/
│   └── ContentAnalyzer.java     # Data extraction & analysis
├── writer/
│   └── MarkdownWriter.java      # Report generation
└── util/
    ├── MarkdownEscaper.java     # Text sanitization
    └── RateLimiter.java         # Rate limiting
```

## Dependencies

- **Picocli** (4.7.5) - CLI framework
- **Jsoup** (1.17.2) - HTML parsing
- **SLF4J** (2.0.9) + **Logback** (1.4.14) - Logging
- **Gson** (2.10.1) - JSON processing
- **JUnit** (5.10.1) - Testing

## Logging

Logs are written to:
- Console (INFO level by default)
- `logs/cogniscor.log` (rotating daily, 30-day retention)

Set log level in `src/main/resources/logback.xml`

## Limitations & Considerations

1. **Public Data Only**: Cogniscor only accesses publicly available information. No authentication or API keys are required for basic operation.

2. **Rate Limiting**: The tool respects rate limits and includes delays between requests. Large companies with many sources may take several minutes to research.

3. **Dynamic Content**: Websites using heavy JavaScript rendering may not be fully scraped. The tool works best with static or server-rendered content.

4. **Data Accuracy**: Information is automatically extracted and may be incomplete or outdated. Always verify critical details independently.

5. **Legal Compliance**: Users are responsible for ensuring their use of this tool complies with applicable laws and website terms of service.

## Troubleshooting

### Build Issues

**Problem**: Maven build fails
```bash
# Solution: Ensure Java 17+ is installed
java -version

# Update JAVA_HOME if needed
export JAVA_HOME=/path/to/java17
```

### Runtime Issues

**Problem**: "Connection timeout" errors
- Check your internet connection
- Increase timeout in `config.properties`: `http.timeout.seconds=60`
- Some websites may block automated requests

**Problem**: "Rate limited" (HTTP 429)
- Increase rate limit delay: `rate.limit.delay.ms=5000`
- The tool will automatically retry with longer delays

**Problem**: Few or no successful sources
- Some companies use different URL structures
- Try adding "www" or removing it from the URL
- Check logs in `logs/cogniscor.log` for details

## Development

### Running Tests
```bash
mvn test
```

### Running Without Building JAR
```bash
mvn exec:java -Dexec.mainClass="com.kof22.cogniscor.Main" \
  -Dexec.args="'Company Name' 'https://website.com'"
```

### Code Style
The project follows standard Java conventions with:
- Clear separation of concerns
- Comprehensive logging
- Graceful error handling
- Immutable where possible

## Contributing

Contributions are welcome! Areas for enhancement:
- Additional data sources
- Improved content extraction algorithms
- API integrations (LinkedIn, Crunchbase, etc.)
- Natural language processing for better analysis
- Export formats (PDF, HTML, JSON)

## License

See LICENSE file for details.

## Disclaimer

This tool is for educational and personal use. Users are responsible for:
- Complying with website terms of service
- Respecting robots.txt directives
- Adhering to applicable laws and regulations
- Verifying information accuracy

The authors are not responsible for misuse or any damages resulting from the use of this software.

## Version History

### 1.0.0 (2025-10-07)
- Initial release
- Multi-source web scraping
- Automated analysis
- Executive summary and detailed report generation
- Configurable rate limiting
- Comprehensive error handling

---

**Cogniscor** - Learn before you apply. 🚀
