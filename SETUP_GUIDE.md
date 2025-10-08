# Cogniscor - Setup Guide

## 🚀 Quick Start (First Time Users)

### Step 1: Run Setup Wizard

```bash
./cogniscor.sh --setup
```

The interactive wizard will:
1. Create your configuration file at `~/.cogniscor/config.properties`
2. Prompt for your OpenAI API key (optional)
3. Configure AI features
4. Set output directory and logging preferences

### Step 2: Use Cogniscor

```bash
./cogniscor.sh "Company Name" "https://company.com"
```

That's it! 🎉

## 📋 What the Setup Wizard Does

### 1. Creates Configuration Directory

The wizard creates `~/.cogniscor/` in your home directory containing:
- `config.properties` - Your personal configuration file

### 2. Prompts for OpenAI API Key

You'll be asked:
```
Do you have an OpenAI API key? (yes/no) [no]:
```

**If you have one:**
- Paste your API key (starts with `sk-`)
- Choose your preferred model (gpt-4o-mini recommended)
- Enable/disable AI features

**If you don't:**
- No problem! The tool works great without AI
- You can add it later by running `--setup` again

### 3. Configures AI Features

If you enable AI, you can choose:
- ✅ **AI-enhanced extraction** - Fills gaps in data extraction
- ✅ **AI report review** - Polishes and improves reports
- ❌ Source planning (future feature)

### 4. Sets Other Preferences

- Output directory (default: `./data`)
- Logging level (INFO or DEBUG)

## 📁 Configuration File Location

Your configuration is stored at:
```
~/.cogniscor/config.properties
```

**Benefits:**
- ✅ Works from any directory
- ✅ Shared across all projects
- ✅ Easy to back up and restore
- ✅ Secure (in your home directory)

## 🔧 Manual Configuration

You can also edit the config file directly:

```bash
# Open in your favorite editor
nano ~/.cogniscor/config.properties

# Or
vim ~/.cogniscor/config.properties
```

### Example Configuration

```properties
# HTTP Settings
http.timeout.seconds=30
rate.limit.delay.ms=2000

# OpenAI Configuration (Optional)
openai.api.key=sk-your-key-here
openai.model=gpt-4o-mini
llm.enabled=true
llm.enhance.extraction=true
llm.review.reports=true

# Output
output.base.dir=./data
log.level=INFO
```

## 🔑 Getting an OpenAI API Key

### Step 1: Create OpenAI Account
1. Go to https://platform.openai.com/signup
2. Sign up with email or Google/Microsoft account
3. Verify your email

### Step 2: Add Payment Method
1. Go to https://platform.openai.com/account/billing
2. Add a payment method (credit card)
3. Add credits ($5 minimum)

### Step 3: Create API Key
1. Go to https://platform.openai.com/api-keys
2. Click "Create new secret key"
3. Give it a name (e.g., "Cogniscor")
4. Copy the key (starts with `sk-`)
5. Save it securely!

**Note**: You'll only see the key once. If you lose it, create a new one.

### Step 4: Run Setup
```bash
./cogniscor.sh --setup
```

Paste your API key when prompted.

## 💰 Cost Expectations

With **gpt-4o-mini** (recommended):
- Per company: ~$0.025
- 10 companies: ~$0.25
- 100 companies: ~$2.50
- 1,000 companies: ~$25

With **gpt-4o** (more capable):
- Per company: ~$0.15
- 10 companies: ~$1.50
- 100 companies: ~$15

**Free tier**: 3 requests/minute • Upgrade for higher limits

## ⚙️ Re-running Setup

You can run setup again anytime:

```bash
./cogniscor.sh --setup
```

This will:
- ✅ Overwrite your existing configuration
- ✅ Let you update your API key
- ✅ Change AI feature settings
- ✅ Modify output directory

## 🔍 Checking Your Configuration

### View Current Config
```bash
cat ~/.cogniscor/config.properties
```

### Check if Config Exists
```bash
ls -la ~/.cogniscor/
```

### Test Configuration
```bash
./cogniscor.sh "Test Company" "https://example.com"
```

## 🚨 Troubleshooting

### "Configuration Required" Error

**Problem**: Running without setup
```
⚠ CONFIGURATION REQUIRED ⚠
```

**Solution**:
```bash
./cogniscor.sh --setup
```

### API Key Not Working

**Check:**
1. Key starts with `sk-`
2. No extra spaces
3. Key is valid at https://platform.openai.com/api-keys
4. You have available credits

**Fix**:
```bash
./cogniscor.sh --setup  # Re-enter your key
```

### Wrong Model Selected

**Change model**:
```bash
# Edit config file
nano ~/.cogniscor/config.properties

# Change this line:
openai.model=gpt-4o-mini
```

Or re-run setup:
```bash
./cogniscor.sh --setup
```

### Want to Disable AI

**Option 1**: Set in config
```bash
nano ~/.cogniscor/config.properties
# Change to:
llm.enabled=false
```

**Option 2**: Remove API key
```bash
nano ~/.cogniscor/config.properties
# Change to:
openai.api.key=
```

### Config File Corrupted

**Delete and recreate**:
```bash
rm -rf ~/.cogniscor/
./cogniscor.sh --setup
```

## 📝 Configuration Options Reference

### HTTP Settings
```properties
http.timeout.seconds=30          # Request timeout
http.user.agent=Mozilla/5.0...   # User agent string
http.max.redirects=5             # Max HTTP redirects
```

### Rate Limiting
```properties
rate.limit.delay.ms=2000         # Delay between requests
rate.limit.enabled=true          # Enable rate limiting
```

### Scraping
```properties
scraper.max.retries=3            # Retry attempts
scraper.retry.delay.ms=5000      # Delay between retries
```

### OpenAI / LLM
```properties
openai.api.key=sk-...            # Your API key
openai.model=gpt-4o-mini         # Model name
openai.max.tokens=4000           # Max response length
openai.temperature=0.3           # Creativity (0.0-1.0)
```

### LLM Features
```properties
llm.enabled=true                 # Master switch
llm.enhance.extraction=true      # Enhance extraction
llm.review.reports=true          # Review reports
llm.plan.sources=false           # Future feature
```

### Output & Logging
```properties
output.base.dir=./data           # Output directory
log.level=INFO                   # Logging level
analyzer.max.summary.words=500   # Summary length
```

## 🔒 Security Best Practices

### Keep API Keys Secure
- ✅ Never commit `config.properties` to git
- ✅ Don't share your API key
- ✅ Use separate keys for different projects
- ✅ Revoke keys you're not using

### File Permissions
```bash
# Secure your config file
chmod 600 ~/.cogniscor/config.properties
```

### Backup Your Config
```bash
# Backup
cp ~/.cogniscor/config.properties ~/cogniscor-config-backup.properties

# Restore
cp ~/cogniscor-config-backup.properties ~/.cogniscor/config.properties
```

## 🎯 Different Setup Scenarios

### Scenario 1: No AI (Free)
```
Do you have an OpenAI API key? no

Result: Fast, free, regex-based extraction
```

### Scenario 2: AI-Enhanced
```
Do you have an OpenAI API key? yes
Enter your OpenAI API key: sk-...
Choose model: 1 (gpt-4o-mini)
Enable AI-enhanced extraction? yes
Enable AI report review? yes

Result: Better extraction + polished reports (~$0.025/company)
```

### Scenario 3: Budget-Conscious
```
Choose model: 3 (gpt-3.5-turbo)
Enable AI-enhanced extraction? yes
Enable AI report review? no

Result: Enhanced extraction only (~$0.01/company)
```

### Scenario 4: Best Quality
```
Choose model: 2 (gpt-4o)
Enable all features: yes

Result: Highest quality (~$0.15/company)
```

## 🚀 Next Steps

After setup:

1. **Test it out:**
   ```bash
   ./cogniscor.sh "GitHub" "https://github.com"
   ```

2. **Check results:**
   ```bash
   ls data/github/reports/
   cat data/github/reports/executive_summary.md
   ```

3. **Monitor costs** (if using AI):
   - https://platform.openai.com/usage

4. **Read more:**
   - `README.md` - Full documentation
   - `LLM_SETUP_GUIDE.md` - Detailed AI guide
   - `CHEATSHEET.md` - Quick reference

## 💡 Tips

- Start without AI to see baseline results
- Enable AI to compare the improvement
- Monitor your first few API calls for cost
- Use `gpt-4o-mini` for best price/performance
- Run setup again anytime to change settings

---

**Ready to research companies?** 
Run `./cogniscor.sh --setup` to get started! 🎉
