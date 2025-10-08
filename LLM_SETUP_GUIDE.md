# Cogniscor - LLM Setup Guide

## 🤖 AI-Enhanced Analysis with ChatGPT

Cogniscor now supports **LLM-powered analysis** using OpenAI's GPT models to dramatically improve the quality and accuracy of company research!

## Features

### 1. 🎯 **Intelligent Source Planning** (Coming Soon)
- LLM suggests the best sources and URLs to research
- Identifies relevant blog posts, press releases, and technical content
- Provides insights about what information would be most valuable

### 2. 🔍 **Enhanced Content Extraction**
- AI fills in gaps when regex patterns miss information
- Extracts executive team members with better accuracy
- Identifies products and services more reliably
- Detects technology stack from natural language descriptions
- Finds recent news with proper context and dates

### 3. ✍️ **Report Quality Improvement**
- Reviews reports for clarity, grammar, and professionalism
- Ensures consistent formatting and structure
- Verifies factual accuracy against source material
- Improves readability while maintaining all information
- **Does NOT hallucinate or add unsupported facts**

## Setup Instructions

### Step 1: Get an OpenAI API Key

1. Go to https://platform.openai.com/api-keys
2. Sign in or create an account
3. Click "Create new secret key"
4. Copy the key (it starts with `sk-...`)

**Cost**: Cogniscor uses `gpt-4o-mini` by default, which costs approximately $0.15 per million input tokens and $0.60 per million output tokens. A typical company analysis costs **less than $0.05**.

### Step 2: Configure Cogniscor

Edit `src/main/resources/config.properties` and add your API key:

```properties
# OpenAI API Configuration
openai.api.key=sk-your-actual-api-key-here
openai.model=gpt-4o-mini
openai.max.tokens=4000
openai.temperature=0.3

# LLM Features (can be disabled individually)
llm.enabled=true
llm.plan.sources=true
llm.enhance.extraction=true
llm.review.reports=true
```

### Step 3: Rebuild the Project

```bash
mvn clean package
```

### Step 4: Run with AI Enhancement

```bash
./cogniscor.sh "Company Name" "https://company.com"
```

You'll see:
```
🤖 AI Enhancement: ENABLED
   ✓ Using gpt-4o-mini for intelligent analysis

📊 Step 1/3: Collecting data from web sources...
   ✓ Collected data from 6 sources

🔍 Step 2/3: Analyzing collected data...
   🤖 AI-enhanced extraction enabled
   ✓ Analysis complete

📝 Step 3/3: Generating reports...
   🤖 AI report review enabled
   ✓ Reports generated
```

## Configuration Options

### Models

You can use different OpenAI models:

```properties
# Recommended: Fastest and cheapest, great quality
openai.model=gpt-4o-mini

# Alternative: More capable, higher cost
openai.model=gpt-4o

# Alternative: Older, faster
openai.model=gpt-3.5-turbo
```

### Temperature

Controls creativity vs consistency (0.0 - 1.0):

```properties
# Conservative (recommended for factual extraction)
openai.temperature=0.3

# Balanced
openai.temperature=0.5

# Creative (not recommended for company research)
openai.temperature=0.8
```

### Feature Toggles

Disable specific LLM features:

```properties
# Master switch
llm.enabled=true

# Disable source planning (if you prefer default sources)
llm.plan.sources=false

# Disable extraction enhancement (use only regex)
llm.enhance.extraction=false

# Disable report review (faster, but less polished)
llm.review.reports=false
```

### Running WITHOUT LLM

Simply don't set an API key or disable LLM:

```properties
llm.enabled=false
```

The tool will work perfectly fine with just regex-based extraction!

## How It Works

### 1. Enhanced Extraction Process

When regex patterns find few results, the LLM steps in:

```
Regex found: 2 executives
↓
LLM analyzes content: "From this content, extract executive team members..."
↓
LLM finds: 6 additional executives with proper titles
↓
Final result: 8 executives total
```

### 2. Report Review Process

After generating reports, the LLM reviews them:

```
Generated Report (regex-based)
↓
LLM Review: "Check grammar, clarity, formatting, factual accuracy..."
↓
Improved Report (polished and professional)
↓
Save to file
```

**Important**: The LLM is instructed to NEVER add information that wasn't in the original report. It only improves presentation, not content.

## What LLM Does NOT Do

❌ **Does NOT** make up facts or hallucinate information
❌ **Does NOT** add executives, products, or news that aren't in source data
❌ **Does NOT** change the meaning of extracted information
❌ **Does NOT** replace regex extraction (it enhances it)

✅ **DOES** improve grammar and readability
✅ **DOES** find information that regex patterns missed
✅ **DOES** ensure consistent formatting
✅ **DOES** verify facts against source material

## Cost Estimation

Based on typical usage with `gpt-4o-mini`:

| Operation | Tokens | Cost |
|-----------|--------|------|
| Extract executives | ~1,500 | $0.0003 |
| Extract products | ~1,500 | $0.0003 |
| Extract technologies | ~1,500 | $0.0003 |
| Extract news | ~1,500 | $0.0003 |
| Review exec summary | ~2,000 | $0.0004 |
| Review detailed report | ~4,000 | $0.0008 |
| **Total per company** | **~12,000** | **~$0.025** |

**100 companies researched = ~$2.50**

## Troubleshooting

### "OpenAI API key not configured"

Check that your API key is properly set in `config.properties`:
```properties
openai.api.key=sk-your-key-here
```

### "Error calling OpenAI API"

1. **Check your API key is valid**
   - Go to https://platform.openai.com/api-keys
   - Verify the key exists and hasn't been revoked

2. **Check you have credits**
   - Go to https://platform.openai.com/account/billing
   - Ensure you have available credits

3. **Check rate limits**
   - Free tier: 3 requests per minute
   - Paid tier: Much higher limits
   - Solution: Run one company at a time if on free tier

### "LLM features not working"

Check the logs at `logs/cogniscor.log`:
```bash
tail -f logs/cogniscor.log
```

Look for messages like:
- "OpenAI service initialized with model: gpt-4o-mini"
- "LLM content enhancement enabled"
- "LLM report review enabled"

### LLM is Too Slow

Try these options:

1. **Use a faster model:**
   ```properties
   openai.model=gpt-3.5-turbo
   ```

2. **Reduce max tokens:**
   ```properties
   openai.max.tokens=2000
   ```

3. **Disable report review** (keeps extraction enhancement):
   ```properties
   llm.review.reports=false
   ```

## Privacy & Security

⚠️ **Important**: 
- Your company research data is sent to OpenAI's API
- OpenAI may use this data to improve their models (unless you opt out)
- Do not use this tool with confidential or sensitive information
- API keys should be kept secure and never committed to version control

To opt out of OpenAI using your data:
- Set up a Business/Enterprise plan with OpenAI
- Or use Azure OpenAI Service with private deployments

## Comparison: With vs Without LLM

### Without LLM (Regex Only)
- ✅ Fast (10-30 seconds)
- ✅ No external API calls
- ✅ No cost
- ❌ Misses some information
- ❌ Less polished reports
- ❌ May have formatting inconsistencies

### With LLM (AI-Enhanced)
- ✅ More comprehensive extraction
- ✅ Professional, polished reports
- ✅ Better formatting and readability
- ✅ Finds information regex misses
- ❌ Slower (20-60 seconds)
- ❌ Requires API key
- ❌ Small cost (~$0.025 per company)

## Example: Executive Team Extraction

**Source Content:**
```
Jane Smith is our CEO, with 20 years in enterprise software. 
The CTO, John Doe, leads our engineering team of 500.
Sarah Johnson joined as CFO from Fortune 500 companies.
```

**Regex Extraction:** Found 2 executives
- CTO: John Doe
- CFO: Sarah Johnson

**LLM Enhancement:** Found all 3 executives
- CEO: Jane Smith
- CTO: John Doe  
- CFO: Sarah Johnson

**Result:** More complete picture for interview preparation!

## Support

Having issues? Check:
1. `logs/cogniscor.log` for detailed error messages
2. OpenAI status page: https://status.openai.com/
3. Your API key and billing settings

## Best Practices

1. **Start with LLM disabled** to see baseline results
2. **Enable LLM** to see the improvement
3. **Review costs** in OpenAI dashboard after a few runs
4. **Use `gpt-4o-mini`** for best cost/performance balance
5. **Keep API keys secure** - use environment variables in production

---

**Happy AI-enhanced researching!** 🚀
