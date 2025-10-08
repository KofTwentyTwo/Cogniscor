# Changelog

All notable changes to Cogniscor will be documented in this file.

## [2.0.0] - 2025-10-07

### 🚀 Major Release: Simplified Interface + 100% Verification + Interactive Refinement

#### Breaking Changes
- **Single parameter interface**: Only requires website URL (company name auto-detected)
- Command changed from `cogniscor "Company" "URL"` to `cogniscor "URL"`

#### Major Features

**Interactive Review & Refinement** 🆕
- Shows executive summary after generation
- Asks user: "Is this executive summary good?"
- User can accept, reject, or request improvements
- Custom improvement prompts (e.g., "Find more about engineering culture")
- AI performs focused follow-up research based on user request
- Iterative refinement until user is satisfied
- Enhanced reports with user-requested information

**Automatic Company Identification** 🆕
- `LLMCompanyIdentifier.java` - AI identifies company name from website
- No manual company name entry required
- Extracts official company name, alternative names, industry
- Confidence scoring (high/medium/low)
- Falls back to URL-based guess if AI unavailable

**100% Content Verification** 🆕
- Every scraped source verified to be about the target company
- Per-source verification during collection
- Final verification pass before report generation
- Mismatches automatically marked as failed and excluded
- Guarantees all report content is about the correct company

**Intelligent URL Discovery** 🆕
- `LLMUrlDiscovery.java` - AI suggests optimal URLs for each company
- Discovers company-specific pages (blogs, newsrooms, careers)
- Categorizes by purpose (leadership, news, products, tech, culture)
- Prioritizes sources (high/medium/low priority)
- Typically discovers 10-15 additional high-value URLs

**Failure Recovery System** 🆕
- `LLMFailureRecovery.java` - Analyzes why sources failed
- Diagnoses issues (rate limiting, wrong URLs, authentication)
- Suggests alternative URLs and recovery strategies
- Attempts intelligent retries with different approaches
- Recovers information even when standard sources fail

**Focused Search on Demand** 🆕
- `LLMFocusedSearch.java` - Plans targeted searches based on user requests
- User provides custom prompt (e.g., "Find their remote work policy")
- AI discovers specific URLs for that information
- Collects focused content
- Enhances reports with newly found information
- Multiple iterations supported

**Multi-Phase Collection Strategy** 🆕
- Phase 1: Identify company from website
- Phase 2: Scrape additional website pages
- Phase 3: AI discovers optimal URLs
- Phase 4: Standard sources with verification
- Phase 5: Failure recovery
- Phase 6: Final verification pass

**Interactive Setup Wizard** (from v1.2.0)
- `SetupWizard.java` - 311-line interactive configuration wizard
- Creates `~/.cogniscor/config.properties` in user's home directory
- Prompts for OpenAI API key with validation
- Model selection (gpt-4o-mini, gpt-4o, gpt-3.5-turbo)
- AI feature toggles (extraction, review)
- Output directory and logging configuration
- Can be run anytime with `--setup` flag

**Configuration System Updates** 🆕
- Checks user home directory first (`~/.cogniscor/`)
- Falls back to bundled configuration
- Detects missing config and prompts user to run setup
- Secure storage of API keys outside project directory

**Command Line Enhancements** 🆕
- `--setup` flag to run setup wizard
- Config existence check before analysis
- Helpful error messages with setup instructions
- Updated help text with setup examples

### 🤖 AI-Enhanced Analysis with ChatGPT

#### Added
- **OpenAI Integration** via `openai-gpt3-java` library
  - Support for GPT-4o-mini, GPT-4o, and GPT-3.5-turbo models
  - Configurable via `config.properties`
  - Optional feature that can be disabled

- **LLM Content Enhancement** (`LLMContentEnhancer`)
  - AI fills in gaps when regex patterns find few results
  - Enhanced executive team extraction
  - Enhanced product/service identification
  - Enhanced technology stack detection
  - Enhanced recent news extraction
  - Only activates when regex finds insufficient information

- **LLM Report Review** (`LLMReportReviewer`)
  - Reviews and improves executive summaries
  - Reviews and improves detailed reports
  - Fixes grammar, style, and formatting issues
  - Ensures consistent structure
  - Verifies factual accuracy against source material
  - **Never adds information not in the original report**

- **LLM Research Planner** (`LLMResearchPlanner`) - Framework ready
  - Suggests optimal sources and URLs to scrape
  - Identifies relevant blog posts and press releases
  - Provides insights about valuable information sources
  - (To be activated in future release)

- **Configuration Options**
  - `openai.api.key` - Your OpenAI API key
  - `openai.model` - Model to use (default: gpt-4o-mini)
  - `openai.max.tokens` - Max tokens per request (default: 4000)
  - `openai.temperature` - Creativity level (default: 0.3)
  - `llm.enabled` - Master switch for all LLM features
  - `llm.plan.sources` - Enable intelligent source planning
  - `llm.enhance.extraction` - Enable AI extraction enhancement
  - `llm.review.reports` - Enable AI report review

#### Improved
- Main analysis workflow now shows AI status
- Console output indicates when AI features are active
- Logging includes AI enhancement results
- Graceful fallback when API key not configured
- Error handling for API failures

#### Documentation
- **LLM_SETUP_GUIDE.md** - Comprehensive setup and usage guide
- Updated README.md with AI feature overview
- Updated CHANGELOG.md with complete feature list
- Configuration examples and best practices
- Cost estimation and troubleshooting guide

#### New Classes & Components
- `interactive/InteractiveReview.java` - User feedback and refinement system
- `llm/LLMCompanyIdentifier.java` - Company name identification
- `llm/LLMUrlDiscovery.java` - Intelligent URL discovery
- `llm/LLMFailureRecovery.java` - Failure analysis and recovery
- `llm/LLMFocusedSearch.java` - Focused search planning

#### Updated Classes
- `CompanyDataCollector.java` - Multi-phase collection with verification
- `Main.java` - Single URL parameter, interactive review workflow
- `Configuration.java` - Additional LLM feature flags
- `cogniscor.sh` - Updated for single parameter

#### New Configuration Options
- `llm.verify.company=true` - Company identification & verification
- `llm.discover.urls=true` - Intelligent URL discovery
- `llm.recover.failures=true` - Failure recovery
- `--no-interactive` - Skip interactive review (for automation)

#### Documentation
- **INTERACTIVE_MODE_GUIDE.md** - Complete interactive mode guide
- **AI_FEATURES_GUIDE.md** - v2.0 AI capabilities overview
- **V2_RELEASE_NOTES.md** - Detailed release notes
- **FINAL_SUMMARY.md** - Complete project summary
- Updated all existing documentation for v2.0

#### Dependencies
- Added `com.theokanning.openai-gpt3-java:service:0.18.2` (from v1.2.0)

#### Cost Impact
- Base research (no improvements): ~$0.035/company
- With 1 interactive improvement: ~$0.050/company
- With 2 interactive improvements: ~$0.065/company

## [1.2.0] - 2025-10-07 (Rolled into v2.0.0)

### Interactive Setup Wizard + AI Integration

## [1.1.0] - 2025-10-07 (Rolled into v2.0.0)

### 🎉 Enhanced Analysis Capabilities

#### Added
- **Comprehensive Executive Team Extraction**
  - Multiple pattern matching for different name/title formats
  - Extracts C-suite executives (CEO, CTO, CFO, COO)
  - Identifies VPs, Directors, and department heads
  - Captures executive backgrounds and experience
  - Detects board member information

- **Advanced Recent News Detection**
  - Date-aware news extraction (recognizes months and years)
  - Captures funding announcements (Series A/B/C/D, valuations)
  - Identifies acquisitions and partnerships
  - Extracts product launch announcements
  - Finds awards and industry recognition
  - Detects expansion and hiring news
  - Categorizes news by type (2024 highlights, product launches, etc.)

- **Detailed Product & Service Cataloging**
  - Multiple extraction strategies for product names
  - Captures product descriptions and features
  - Identifies product categories and offerings
  - Extracts service details and capabilities
  - Recognizes SaaS, PaaS, and IaaS offerings

- **Comprehensive Technology Stack Detection**
  - **Programming Languages**: Java, Python, JavaScript, TypeScript, Go, Rust, C++, C#, Ruby, PHP, Swift, Kotlin, Scala, and more
  - **Frontend Technologies**: React, Vue.js, Angular, Svelte, Next.js, Tailwind CSS, Material UI
  - **Backend Frameworks**: Node.js, Django, Flask, FastAPI, Spring Boot, Laravel, Rails
  - **Cloud Platforms**: AWS, Azure, Google Cloud, Oracle Cloud, DigitalOcean, Heroku
  - **DevOps & Containers**: Docker, Kubernetes, Jenkins, GitHub Actions, Terraform, Ansible
  - **Databases**: PostgreSQL, MySQL, MongoDB, Redis, Elasticsearch, DynamoDB, Snowflake
  - **AI/ML Tools**: TensorFlow, PyTorch, Scikit-learn, OpenAI, GPT
  - **Mobile**: iOS, Android, React Native, Flutter
  - **Other Technologies**: Blockchain, Microservices, Serverless, GraphQL, Kafka, WebSockets
  - Pattern-based detection for "built with", "using", "powered by" statements
  - Categorized output by technology type

#### Improved
- Enhanced logging to show extraction counts (executives, products, technologies, news items)
- More robust pattern matching with word boundaries to avoid false positives
- Better handling of multi-format name/title combinations
- Improved date recognition in news items
- More comprehensive keyword lists for industry and technology detection

#### Updated Documentation
- Enhanced example output files showcasing new capabilities
- Updated README with detailed feature descriptions
- Updated reports to show rich executive team, product, and technology information

### 🐚 Shell Script Frontend

#### Added
- **cogniscor.sh** - Friendly shell script wrapper for easy execution
  - Automatic Java version checking
  - Auto-build functionality if JAR is missing
  - Colorful, emoji-rich output
  - Clear error messages with suggestions
  - Force rebuild option (--build flag)
  - Complete help system (--help)
  - Version information (--version)

#### Documentation
- **CHEATSHEET.md** - One-page quick reference guide
- Updated **README.md** to feature shell script as primary usage method
- Updated **QUICKSTART.md** with shell script examples

## [1.0.0] - 2025-10-07

### Initial Release

#### Core Features
- Command-line application using Picocli
- Multi-source web scraping (6+ sources)
- Java HttpClient for HTTP requests
- Jsoup for HTML parsing
- Rate limiting and retry logic
- Markdown report generation
- Structured logging with SLF4J + Logback
- Configuration via properties file
- Comprehensive error handling

#### Data Collection
- Official company websites
- Wikipedia pages
- LinkedIn company pages
- Crunchbase profiles
- Glassdoor reviews
- News sources

#### Analysis
- Company overview extraction
- Industry identification
- Founding information and founders
- Headquarters location
- Company size and revenue
- Basic leadership extraction
- Mission and culture
- Product identification
- Basic technology detection
- Employee reviews and ratings

#### Output
- Executive summary (1-2 pages)
- Detailed report (comprehensive analysis)
- Individual source files with metadata
- Organized directory structure

#### Documentation
- README.md with comprehensive usage guide
- QUICKSTART.md for getting started in 5 minutes
- PROJECT_SUMMARY.md with technical details
- FILE_LISTING.md with complete file overview
- Example output files

---

## Version History

- **v1.1.0** - Enhanced analysis for executives, news, products, and technology + Shell script
- **v1.0.0** - Initial release with core functionality

## Future Enhancements

Potential features for future releases:
- API integrations (LinkedIn, Crunchbase APIs)
- Natural language processing for better extraction
- Sentiment analysis of reviews
- Competitor analysis
- PDF export option
- Web UI for results
- Database backend for historical tracking
- Multi-language support
- Headless browser integration for JavaScript-heavy sites
