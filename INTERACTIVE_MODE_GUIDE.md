# Cogniscor - Interactive Mode Guide

## 🎯 What is Interactive Mode?

After Cogniscor generates your company research, it shows you the **executive summary** and asks:

**"Is this executive summary good?"**

You can:
- ✅ **Accept** - Keep the results as-is
- 🔄 **Request improvements** - Ask for specific additional information
- ❌ **Cancel** - Keep current results without changes

This feature uses AI to perform **focused follow-up research** based on your specific needs!

---

## 🚀 How It Works

### Step 1: Initial Research

```bash
./cogniscor.sh https://github.com
```

Cogniscor collects and analyzes company data...

### Step 2: Review Executive Summary

After analysis, you'll see:

```
╔════════════════════════════════════════════════════════════╗
║              📄 EXECUTIVE SUMMARY PREVIEW                  ║
╚════════════════════════════════════════════════════════════╝

# Executive Summary: GitHub, Inc.

*Generated: 2025-10-07 17:30:00*

## Overview
GitHub is a developer platform that allows developers to create, store...

## Key Facts
**Industry:** Software Development Platform
**Founded:** 2008
**Headquarters:** San Francisco, California
...

(showing first 50 lines)

════════════════════════════════════════════════════════════

╔════════════════════════════════════════════════════════════╗
║                    REVIEW & FEEDBACK                       ║
╚════════════════════════════════════════════════════════════╝

Is this executive summary good? (yes/no/improve) [yes]: 
```

### Step 3: Your Choice

#### Option A: Accept (Default)
```
Is this executive summary good? yes

✓ Great! Reports saved and ready to use.
```

#### Option B: Request Improvements
```
Is this executive summary good? improve

╔════════════════════════════════════════════════════════════╗
║              🔍 CUSTOM SEARCH REFINEMENT                   ║
╚════════════════════════════════════════════════════════════╝

Describe what you want to find or improve:
Examples:
  - 'Find more information about their engineering culture'
  - 'Get details about their remote work policy'
  - 'Focus on recent product launches and roadmap'

Your request: Find more about their engineering culture and tech stack
```

### Step 4: Focused Research

Cogniscor then:

1. **Plans focused search** with AI
2. **Discovers specific URLs** likely to have that information
3. **Scrapes focused sources** (company blog, engineering pages, etc.)
4. **Verifies content** matches the company
5. **Enhances the report** with new information
6. **Shows updated summary**

```
🔍 Performing focused research based on your request...

📋 Search Plan:
   Focus: Engineering culture, tech stack details, development practices
   URLs to try: 5
   Search queries: 3

🌐 Collecting focused information...
   Trying: https://github.blog/engineering
   Trying: https://github.com/about/careers/engineering
   Trying: https://github.com/about/engineering-culture
   ✓ Collected information from 3 sources

🔍 Re-analyzing with focused information...
   ✓ Analysis complete

📝 Enhancing executive summary...
   ✓ Executive summary updated with focused information

✨ Research refinement complete!
```

---

## 💡 Example Improvement Requests

### 1. Engineering Culture
```
Your request: Find more about their engineering culture and development practices
```

**What AI does:**
- Searches for engineering blog
- Looks for careers/engineering pages
- Finds tech talks and presentations
- Extracts culture information
- Adds "Engineering Culture" section to summary

### 2. Remote Work Policy
```
Your request: Get details about remote work and work-life balance
```

**What AI does:**
- Finds careers page
- Looks for "remote" and "work from home" mentions
- Checks employee reviews for WLB mentions
- Searches for remote work announcements
- Adds to "Company Culture" section

### 3. Recent Products
```
Your request: Focus on recent product launches in the last 6 months
```

**What AI does:**
- Searches newsroom/press releases
- Looks for blog announcements
- Finds product pages
- Filters for recent dates
- Creates "Recent Product Launches" section

### 4. Compensation & Benefits
```
Your request: Find information about salary, benefits, and stock options
```

**What AI does:**
- Searches careers page for benefits
- Looks for Glassdoor compensation data
- Finds press releases about equity
- Checks for compensation philosophy
- Adds "Compensation & Benefits" section

### 5. Tech Stack Details
```
Your request: Get comprehensive details about their technology stack and infrastructure
```

**What AI does:**
- Finds engineering blog posts
- Looks for tech talks and conference presentations
- Searches for "built with" or "powered by" mentions
- Checks job postings for required skills
- Expands "Technology Stack" section

---

## 🎯 Best Practices

### Be Specific
❌ Bad: "Find more information"
✅ Good: "Find information about their internship program and new grad hiring"

❌ Bad: "Improve the report"
✅ Good: "Get details about their diversity and inclusion initiatives"

### Focus on One Topic
✅ Good: "Focus on their AI/ML engineering team and projects"
❌ Too broad: "Find everything about engineering, products, culture, and benefits"

### Ask for Findable Information
✅ Likely available: "Engineering culture", "recent news", "product roadmap"
❌ Unlikely: "Internal politics", "secret projects", "unpublished financials"

---

## ⚙️ Configuration

### Enable/Disable Interactive Mode

Interactive mode is **enabled by default** when AI is configured.

To disable it:

```bash
# Use the --no-interactive flag
./cogniscor.sh --no-interactive https://github.com
```

Or in scripts/automation:
```bash
# Always disable for batch processing
for url in $(cat companies.txt); do
  ./cogniscor.sh --no-interactive "$url"
done
```

### Interactive Mode Requirements

- ✅ AI must be enabled (requires OpenAI API key)
- ✅ Terminal must support user input
- ✅ Not using `--no-interactive` flag

If AI is disabled, interactive mode is automatically skipped.

---

## 💰 Cost Impact

Each improvement cycle adds:
- Focused search planning: ~$0.003
- URL discovery: ~$0.002
- Additional scraping: ~3-5 new sources
- Content verification: ~$0.003
- Report enhancement: ~$0.005

**Total per improvement**: ~$0.015

**Example**: 
- Initial research: $0.035
- One improvement: +$0.015
- **Total: $0.050**

Still very affordable!

---

## 🔄 Multiple Improvements

You can iterate multiple times:

```
Round 1: Initial research → "Find engineering culture"
  → Enhanced with engineering info

Round 2: Review again → "Now get details about benefits"
  → Enhanced with benefits info

Round 3: Review again → "Perfect, looks good!"
  → Final report saved
```

Each round adds ~$0.015 in API costs.

---

## 🎨 Example Session

```bash
$ ./cogniscor.sh https://stripe.com

... [research happens] ...

╔════════════════════════════════════════════════════════════╗
║              📄 EXECUTIVE SUMMARY PREVIEW                  ║
╚════════════════════════════════════════════════════════════╝

# Executive Summary: Stripe, Inc.

## Overview
Stripe is a financial infrastructure platform...

## Key Facts
**Industry:** Financial Technology (Fintech)
**Founded:** 2010

... [showing first 50 lines] ...

════════════════════════════════════════════════════════════

Is this executive summary good? (yes/no/improve) [yes]: improve

Describe what you want to find or improve:
Your request: Find more about their payment processing fees and pricing model

✓ Got it! I'll search for: "Find more about their payment processing fees and pricing model"

🔍 Performing focused research based on your request...

📋 Search Plan:
   Focus: Payment processing fees, pricing structure, transaction costs
   URLs to try: 4
   
🌐 Collecting focused information...
   Trying: https://stripe.com/pricing
   Trying: https://stripe.com/docs/pricing
   Trying: https://stripe.com/payments/pricing
   ✓ Collected information from 3 sources

🔍 Re-analyzing with focused information...
   ✓ Analysis complete

📝 Enhancing executive summary...
   ✓ Executive summary updated with focused information

✨ Research refinement complete!

📁 Results saved to: ./data/stripe/

✓ Reports now include detailed pricing information!
```

---

## 🎁 Benefits

### For Interview Preparation
- 🎯 **Get exactly what you need** - Ask for specific info
- 🔍 **Deep dives** - Focus on areas most relevant to the role
- ✅ **Verify completeness** - Make sure nothing important is missing

### For Research Quality
- 🤖 **AI-guided follow-up** - Smart suggestions for additional sources
- 📚 **Comprehensive coverage** - Fill in any gaps
- ✨ **Polished output** - Enhanced reports with focused information

### For Efficiency
- ⚡ **Iterative refinement** - Improve until satisfied
- 🎯 **Targeted research** - Only collect what you need
- 💰 **Cost-effective** - Only pay for improvements you request

---

## 🚨 Important Notes

### Interactive Mode is Optional
```bash
# Skip interactive mode (faster, automated)
./cogniscor.sh --no-interactive https://company.com

# Enable interactive mode (default with AI)
./cogniscor.sh https://company.com
```

### AI is Required
Interactive mode only works with AI enabled. Without AI:
- Reports are generated normally
- No interactive review
- No focused improvements

### Automated Workflows
For scripts or CI/CD, always use `--no-interactive`:

```bash
#!/bin/bash
# Batch processing script
for company in github.com stripe.com notion.so; do
  ./cogniscor.sh --no-interactive "https://$company"
done
```

---

## 🎓 Tips for Great Results

### 1. Review the Initial Summary First
- Read through what was found
- Identify what's missing or weak
- Be specific in your improvement request

### 2. One Topic at a Time
- Focus on one area per improvement
- You can iterate multiple times
- Quality over quantity

### 3. Use Domain Knowledge
- If you're interviewing for engineering: "Find tech stack details"
- If you're interviewing for sales: "Find customer base and market position"
- If you're interviewing for product: "Find product roadmap and vision"

### 4. Check Focused Sources
- New sources are saved with type "focused_search"
- Review them to see where info came from
- Helps verify quality of improvements

---

## 📊 Improvement Request Examples by Role

### Software Engineer
```
- "Get comprehensive details about their technology stack and infrastructure"
- "Find information about their engineering culture and development practices"
- "Look for details about their code review process and testing"
```

### Product Manager
```
- "Find detailed information about their product roadmap and vision"
- "Get insights into their product development process"
- "Look for customer testimonials and product feedback"
```

### Designer
```
- "Find information about their design system and tools"
- "Get details about their design team and culture"
- "Look for their design philosophy and process"
```

### Sales/Business
```
- "Find detailed pricing and business model information"
- "Get information about their customer base and market segments"
- "Look for case studies and customer success stories"
```

### Data Scientist
```
- "Find details about their data infrastructure and ML stack"
- "Get information about their AI/ML team and projects"
- "Look for publications or talks about their data work"
```

---

## 🔍 How AI Plans Focused Searches

When you request improvements, AI:

1. **Analyzes your request** - Understands what you're looking for
2. **Reviews existing summary** - Identifies gaps
3. **Suggests specific URLs** - Company blog, docs, careers page
4. **Creates search queries** - Targeted searches for the information
5. **Defines extraction focus** - What to look for in content
6. **Plans report enhancement** - How to integrate into summary

**Example Plan for "engineering culture":**
```json
{
  "urls": [
    {
      "url": "https://github.blog/engineering",
      "reason": "Engineering blog likely has culture posts"
    },
    {
      "url": "https://github.com/about/careers/engineering",
      "reason": "Careers page describes engineering culture"
    }
  ],
  "searchQueries": [
    "GitHub engineering culture",
    "GitHub development practices"
  ],
  "extractionFocus": "Engineering practices, team structure, culture values",
  "reportEnhancement": "Add Engineering Culture subsection under Company Culture"
}
```

---

## ✨ Advanced Usage

### Multiple Iterations

```
Round 1: "Find engineering culture" → Enhanced
         Is this good? improve

Round 2: "Now get benefits and compensation" → Enhanced
         Is this good? improve

Round 3: "Add details about interview process" → Enhanced
         Is this good? yes → Done!
```

### Combining with Verbose Mode

```bash
./cogniscor.sh -v https://company.com
```

See detailed logs of:
- What URLs AI discovers
- Why sources are chosen
- What content is extracted
- How reports are enhanced

---

## 🎉 Summary

Interactive Mode makes Cogniscor a **collaborative research assistant**:

- 🤖 AI does the heavy lifting
- 👤 You guide what matters most
- 🔄 Iterate until perfect
- ✅ Get exactly the information you need

**Perfect for interview prep** where you need specific insights about the company!

---

**Try it now:**
```bash
./cogniscor.sh https://github.com
# Review the summary
# Request improvements as needed
# Get the perfect research report!
```
