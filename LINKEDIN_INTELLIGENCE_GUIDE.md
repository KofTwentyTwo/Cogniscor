# LinkedIn Intelligence Module

## Overview

The LinkedIn Intelligence Module is a comprehensive system for gathering deep intelligence about companies through LinkedIn. It uses LLM-powered discovery to find real LinkedIn URLs and extract structured information about companies, their employees, and their content strategy.

## Features

### 1. **Smart URL Discovery**
- Uses LLM knowledge to find the **actual** LinkedIn company page URL
- No more guessing URL patterns or getting 404 errors
- Considers company name variations, acquisitions, and rebrandings

### 2. **Company Profile Analysis**
Extracts comprehensive company information:
- Employee count and company size
- Headquarters location
- Industry and specialties
- Founded year
- Company tagline
- Recent company updates

### 3. **Employee Network Mapping**
Discovers and analyzes key employees:
- **C-suite executives** (CEO, CTO, CFO, etc.)
- **Founders** and co-founders
- **VPs and Directors** across departments
- **Organizational structure** by department and seniority
- Individual LinkedIn profiles (when available)
- Notable information about key people

### 4. **Content & Engagement Intelligence**
Analyzes the company's LinkedIn presence:
- **Posting frequency** (very active, active, occasional, rare)
- **Main content themes** and topics
- **Thought leadership areas**
- **Recent posts** with engagement levels
- **Content strategy** description

### 5. **Organizational Analytics**
- Department distribution (Engineering, Sales, Marketing, etc.)
- Seniority breakdown (Executive, Senior, Mid, Entry)
- Founder retention analysis

## How It Works

### Architecture

```
CompanyDataCollector
    ↓
LinkedInIntelligence (Phase 3)
    ↓
├── 1. Discover LinkedIn URL (LLM)
├── 2. Scrape Company Page
├── 3. Extract Company Info (LLM)
├── 4. Discover Key Employees (LLM)
├── 5. Gather Content Analysis (LLM)
└── 6. Analyze Org Structure
```

### LLM Integration

The module leverages OpenAI's LLM in multiple ways:

1. **URL Discovery**: Asks the LLM to use its knowledge to find the real LinkedIn URL
2. **Information Extraction**: Parses LinkedIn page content into structured data
3. **Employee Discovery**: Identifies key people based on the LLM's knowledge
4. **Content Analysis**: Analyzes posting patterns and engagement strategies

### Key Classes

- **`LinkedInIntelligence`**: Main orchestrator class
- **`LinkedInData`**: Data model for all LinkedIn information
- **`LinkedInEmployee`**: Represents an employee profile
- **`LinkedInPost`**: Represents a company post/update

## Usage

### Automatic Integration

When AI features are enabled, LinkedIn Intelligence runs automatically:

```bash
./cogniscor.sh --url https://company.com --ai
```

The module runs in **Phase 3** of data collection, right after scraping the main website.

### Configuration

No special configuration needed! LinkedIn Intelligence is automatically enabled when:
- AI features are enabled (`--ai` flag)
- OpenAI API key is configured

### Output

LinkedIn data appears in the detailed report under:
```markdown
## LinkedIn Intelligence

### Company Information
- Employee count, industry, headquarters, etc.

### Key Employees & Leadership
- Founders, executives, notable people
- With LinkedIn profiles and notable information

### Organizational Structure  
- Department and seniority breakdowns

### LinkedIn Activity & Content
- Posting frequency and themes
- Thought leadership areas
- Content strategy

### Recent LinkedIn Posts
- Recent company updates and posts
```

## Example Output

### Company Profile
```
**LinkedIn Profile:** https://www.linkedin.com/company/stripe/
**Employee Count:** 1,001-5,000
**Industry:** Financial Services
**Headquarters:** San Francisco, CA
**Specialties:** Payments, APIs, Developer Tools, Infrastructure
```

### Key Employee Example
```
#### Patrick Collison 👔 (Founder)

**Title:** CEO & Co-Founder
**Department:** Executive
**LinkedIn:** https://www.linkedin.com/in/patrickcollison/

*Co-founded Stripe in 2010, known for thought leadership on technology and economics*
```

### Content Analysis
```
**Posting Frequency:** very active

**Main Content Themes:**
- Product launches and feature updates
- Developer education and resources
- Economic insights and analysis
- Company culture and hiring

**Thought Leadership Areas:**
- Online payments infrastructure
- Developer tools and APIs
- Economic growth and technology
```

## Technical Details

### LLM Prompts

The module uses carefully crafted prompts for each phase:

1. **URL Discovery**: Asks LLM to provide ACTUAL URL, not guess patterns
2. **Employee Discovery**: Asks for specific known people, not made-up names
3. **Content Analysis**: Requests analysis based on LLM's knowledge of the company

### Error Handling

- Gracefully handles LinkedIn scraping failures (common due to anti-bot protection)
- Falls back to LLM knowledge even if scraping fails
- Logs warnings but continues with partial data

### Data Validation

- Validates JSON responses from LLM
- Checks for null/missing fields
- Handles optional data gracefully

## Benefits

### For Competitive Intelligence
- **Employee insights**: Understand team composition and key players
- **Content strategy**: Learn what themes and messages resonate
- **Growth indicators**: Track employee count and hiring patterns

### For Sales & BD
- **Decision makers**: Identify the right people to contact
- **Company priorities**: Understand focus areas from content
- **Org structure**: Navigate the organization effectively

### For Research
- **Comprehensive profiles**: Get structured company data
- **Network analysis**: Understand leadership and founders
- **Engagement patterns**: See how actively they use LinkedIn

## Future Enhancements

Potential improvements:
- Employee connection graph (who worked where before)
- Skills analysis across the organization
- Growth trends over time
- Competitor comparisons
- Alumni tracking

## Limitations

1. **LinkedIn's Anti-Bot Protection**: Direct scraping may fail, so we rely on LLM knowledge
2. **LLM Knowledge Cutoff**: Information limited to LLM's training data
3. **Privacy Considerations**: Only gathers publicly available information
4. **API Costs**: Each phase makes multiple LLM API calls

## Troubleshooting

### "Could not discover LinkedIn URL"
- Company may be too small or doesn't have a LinkedIn presence
- Try manually checking if they have a LinkedIn page

### "No employee data discovered"
- Company may not have notable public figures
- LLM may not have knowledge of smaller companies

### "Failed to scrape LinkedIn company page"
- LinkedIn's anti-bot protection is blocking access
- The module will still attempt to use LLM knowledge as fallback

## Best Practices

1. **Use with AI enabled**: LinkedIn Intelligence requires LLM features
2. **Check the logs**: Watch for discovery successes and failures
3. **Verify critical data**: LLM knowledge may be outdated for recent changes
4. **Respect rate limits**: Multiple LLM calls per company analysis

---

*This module demonstrates the power of combining traditional web scraping with LLM intelligence for comprehensive company research.*
