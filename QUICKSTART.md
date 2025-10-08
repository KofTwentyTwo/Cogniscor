# Cogniscor - Quick Start Guide

Get up and running with Cogniscor in 5 minutes!

## Prerequisites

Ensure you have:
- ✅ Java 17 or higher installed
- ✅ Maven 3.6+ installed
- ✅ Internet connection

Check your Java version:
```bash
java -version
```

## Step 1: Build the Project

```bash
mvn clean package
```

This will:
- Download all dependencies
- Compile the source code
- Run tests
- Create an executable JAR file

Expected output: `BUILD SUCCESS`

## Step 2: Run Setup (First Time)

```bash
./cogniscor.sh --setup
```

Follow the prompts to configure (takes 2 minutes).

## Step 3: Run Your First Analysis

**Easy way** - Use the friendly shell script:

```bash
./cogniscor.sh "https://github.com"
```

**Or directly** with the JAR:

```bash
java -jar target/cogniscor-1.0-SNAPSHOT.jar "https://github.com"
```

**Note**: Just provide the URL - AI automatically detects the company name!

You'll see:
```
╔════════════════════════════════════════════════════════════╗
║                      COGNISCOR                             ║
║              Company Research & Analysis                   ║
╚════════════════════════════════════════════════════════════╝

📊 Step 1/3: Collecting data from web sources...
   ✓ Collected data from X sources

🔍 Step 2/3: Analyzing collected data...
   ✓ Analysis complete

📝 Step 3/3: Generating reports...
   ✓ Reports generated
```

## Step 3: View Results

Results are saved in the `data/` directory:

```bash
ls data/github/
```

You'll find:
- `sources/` - Individual Markdown files for each data source
- `reports/` - Executive summary and detailed report

View the executive summary:
```bash
cat data/github/reports/executive_summary.md
```

View the detailed report:
```bash
cat data/github/reports/detailed_report.md
```

## Example Companies to Try

```bash
# Tech Companies
./cogniscor.sh "https://stripe.com"
./cogniscor.sh "https://notion.so"
./cogniscor.sh "https://figma.com"

# Enterprise
./cogniscor.sh "https://salesforce.com"
./cogniscor.sh "https://ibm.com"

# Startups
./cogniscor.sh "https://vercel.com"
```

## Common Options

Show help:
```bash
./cogniscor.sh --help
```

Enable verbose logging:
```bash
./cogniscor.sh -v "Company" "https://url.com"
```

Custom output directory:
```bash
./cogniscor.sh --output ./results "Company" "https://url.com"
```

Force rebuild:
```bash
./cogniscor.sh --build "Company" "https://url.com"
```

## Troubleshooting

### Build Fails
**Solution**: Make sure Java 17+ is active
```bash
export JAVA_HOME=/path/to/java17
mvn clean package
```

### Slow Execution
**Normal**: Each company takes 30-60 seconds due to rate limiting (respectful scraping)

### Few Sources Found
**Tip**: Try the exact official website URL (with or without `www`)

### Connection Errors
**Check**: Your internet connection and firewall settings

## Next Steps

1. **Read the README.md** for detailed documentation
2. **Check example output** in `data/example_company/`
3. **Customize configuration** in `src/main/resources/config.properties`
4. **View logs** in `logs/cogniscor.log` for debugging

## Tips for Best Results

✅ **DO:**
- Use the official company website URL
- Include `https://` in the URL
- Wait for the process to complete (30-60 seconds)
- Check the logs if something goes wrong

❌ **DON'T:**
- Use shortened URLs or redirects
- Interrupt the process mid-execution
- Run too frequently (respect rate limits)

## What to Expect

**Successful Sources** (usually work well):
- Official company website ✅
- Wikipedia (for established companies) ✅
- Public company pages ✅

**May Not Work:**
- Sites requiring login (LinkedIn, Glassdoor)
- Sites with heavy JavaScript
- Sites with aggressive bot protection

**This is normal!** The tool gracefully handles failures and still generates useful reports from available sources.

## Example Output Structure

```
data/
└── github/
    ├── sources/
    │   ├── 01_official_website.md    ← Raw data from GitHub.com
    │   ├── 02_wikipedia.md           ← GitHub Wikipedia page
    │   ├── 03_linkedin.md            ← Attempt at LinkedIn (may fail)
    │   ├── 04_crunchbase.md          ← Crunchbase data (if available)
    │   ├── 05_glassdoor.md           ← Reviews (may fail)
    │   └── 06_news.md                ← Recent news
    │
    └── reports/
        ├── executive_summary.md      ← Quick overview (1-2 pages)
        └── detailed_report.md        ← Full analysis (10+ sections)
```

## That's It! 🎉

You're ready to research companies with Cogniscor. For more details, see:
- `README.md` - Full documentation
- `PROJECT_SUMMARY.md` - Technical details
- `data/example_company/` - Sample output

Happy researching! 🚀
