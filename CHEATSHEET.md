# Cogniscor - Quick Reference Cheat Sheet

## 🚀 Quick Start

```bash
# First time: Build the project
mvn clean package

# Run a search
./cogniscor.sh "Company Name" "https://company.com"

# With AI enhancement (requires OpenAI API key)
# Edit config.properties first, then:
./cogniscor.sh "Company Name" "https://company.com"
```

## 🤖 AI Setup (Optional)

```bash
# 1. Get API key from https://platform.openai.com/api-keys
# 2. Edit src/main/resources/config.properties:
openai.api.key=sk-your-key-here
llm.enabled=true

# 3. Rebuild and run
mvn clean package
./cogniscor.sh "Company" "https://company.com"
```

**Cost**: ~$0.025 per company • See [LLM_SETUP_GUIDE.md](LLM_SETUP_GUIDE.md)

## 📝 Common Commands

```bash
# Basic usage
./cogniscor.sh "GitHub" "https://github.com"

# With verbose logging
./cogniscor.sh -v "Stripe" "https://stripe.com"

# Custom output directory
./cogniscor.sh -o ./research "Google" "https://google.com"

# Force rebuild
./cogniscor.sh --build "Microsoft" "https://microsoft.com"

# Show help
./cogniscor.sh --help

# Show version
./cogniscor.sh --version
```

## 🎯 Popular Companies

| Company | Command |
|---------|---------|
| GitHub | `./cogniscor.sh "https://github.com"` |
| Stripe | `./cogniscor.sh "https://stripe.com"` |
| Notion | `./cogniscor.sh "https://notion.so"` |
| Figma | `./cogniscor.sh "https://figma.com"` |
| Vercel | `./cogniscor.sh "https://vercel.com"` |
| OpenAI | `./cogniscor.sh "https://openai.com"` |

## 📁 Output Structure

```
data/
└── <company_name>/
    ├── sources/               ← Raw scraped data
    │   ├── 01_official_website.md
    │   ├── 02_wikipedia.md
    │   ├── 03_linkedin.md
    │   ├── 04_crunchbase.md
    │   ├── 05_glassdoor.md
    │   └── 06_news.md
    └── reports/               ← Generated reports
        ├── executive_summary.md    ← Quick overview
        └── detailed_report.md      ← Full analysis
```

## 👀 View Results

```bash
# Quick summary
cat data/github/reports/executive_summary.md

# Full report
cat data/github/reports/detailed_report.md

# View in editor (macOS)
open data/github/reports/executive_summary.md

# View in editor (Linux)
xdg-open data/github/reports/executive_summary.md
```

## 🔧 Troubleshooting

```bash
# Check Java version (need 17+)
java -version

# Rebuild if needed
mvn clean package

# View logs
cat logs/cogniscor.log

# Clean old data
rm -rf data/
```

## ⚙️ Configuration

Edit `src/main/resources/config.properties`:

```properties
# Slow network? Increase timeout
http.timeout.seconds=60

# Getting rate limited? Increase delay
rate.limit.delay.ms=5000

# Change output location
output.base.dir=./my-research
```

## 💡 Tips

✅ **DO:**
- Use official company website
- Include `https://` in URL
- Wait for completion (30-60 sec)
- Check logs if something fails

❌ **DON'T:**
- Use shortened URLs
- Interrupt mid-execution
- Run too frequently

## 🐛 Common Issues

**"JAR not found"**
```bash
mvn clean package
```

**"Java version too old"**
```bash
# Install Java 17+ or update JAVA_HOME
export JAVA_HOME=/path/to/java17
```

**"Connection timeout"**
```bash
# Increase timeout in config.properties
http.timeout.seconds=60
```

**"Rate limited (429)"**
```bash
# Increase delay in config.properties
rate.limit.delay.ms=5000
```

## 📚 More Help

- Full docs: `README.md`
- Quick start: `QUICKSTART.md`
- Technical: `PROJECT_SUMMARY.md`
- Shell help: `./cogniscor.sh --help`

## 🎉 That's It!

**The simplest command to remember:**
```bash
./cogniscor.sh "https://company-website.com"
```

AI automatically detects the company name and verifies all information! 🚀

Then check `data/company_name/reports/` for your results!
