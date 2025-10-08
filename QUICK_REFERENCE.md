# Cogniscor v2.0 - Quick Reference Card

## 🚀 One-Line Usage

```bash
./cogniscor.sh https://company.com
```

**That's it!** AI does everything else.

---

## 📋 First Time Setup

```bash
./cogniscor.sh --setup
```

Prompts you for:
- OpenAI API key (optional but recommended)
- AI model selection
- Feature preferences

---

## 💡 Common Commands

```bash
# Basic usage
./cogniscor.sh https://github.com

# Skip interactive review (automation)
./cogniscor.sh --no-interactive https://stripe.com

# Verbose output
./cogniscor.sh -v https://notion.so

# Custom output directory
./cogniscor.sh -o ./research https://figma.com

# Force rebuild
./cogniscor.sh --build https://vercel.com

# Show help
./cogniscor.sh --help
```

---

## 🎯 Interactive Mode Flow

```
1. Run: ./cogniscor.sh https://company.com
   ↓
2. [AI collects and analyzes data]
   ↓
3. Shows executive summary (first 50 lines)
   ↓
4. Asks: "Is this executive summary good?"
   ↓
5. Your options:
   • yes      → Accept and finish
   • improve  → Request custom improvements
   • no       → Same as improve
   ↓
6. If improve: "Describe what you want to find"
   Example: "Find their engineering culture"
   ↓
7. [AI performs focused search]
   ↓
8. Shows updated summary → Repeat from step 4
```

---

## 💬 Example Improvement Requests

### Role-Specific
```
"Find details about their engineering internship program"
"Get information about design system and tools"
"Look for sales commission structure"
"Find ML/AI team and projects"
```

### Topic-Specific
```
"Get comprehensive benefits and perks information"
"Find remote work and WFH policies"
"Look for recent product launches"
"Get details about company culture"
```

### Technical
```
"Find their complete technology stack"
"Get infrastructure and DevOps details"
"Look for engineering blog posts"
"Find development practices and code review process"
```

---

## 🎁 What AI Does

### With AI Enabled
✅ Identifies company name from website  
✅ Discovers 10-15 optimal URLs  
✅ Verifies 100% of sources match company  
✅ Recovers from failures with alternatives  
✅ Enhances extraction (finds what regex misses)  
✅ Reviews and polishes reports  
✅ Interactive refinement with custom prompts  

### Without AI
✅ URL-based company name guess  
✅ Standard source patterns  
✅ No verification  
✅ Basic reports  
✅ Fast and free  

---

## ⚙️ Configuration Location

```bash
~/.cogniscor/config.properties
```

Edit directly:
```bash
nano ~/.cogniscor/config.properties
```

Or re-run setup:
```bash
./cogniscor.sh --setup
```

---

## 📁 Output Location

```
./data/<company_name>/
├── reports/
│   ├── executive_summary.md    ← AI-reviewed, customizable
│   └── detailed_report.md      ← Comprehensive analysis
└── sources/
    ├── 01_official_website.md
    ├── 02_wikipedia.md
    └── ... (all sources with metadata)
```

---

## 💰 Cost (with AI)

| Usage | Cost per Company |
|-------|-----------------|
| Base research | $0.035 |
| + 1 improvement | $0.050 |
| + 2 improvements | $0.065 |

**Volume**: 100 companies = $3.50-6.50

---

## 🚨 Common Issues

### No config found
```bash
./cogniscor.sh --setup
```

### Want to skip interactive mode
```bash
./cogniscor.sh --no-interactive https://url.com
```

### API key issues
```bash
./cogniscor.sh --setup  # Re-enter key
```

### Check logs
```bash
cat logs/cogniscor.log
```

---

## 📚 More Help

- Full guide: `README.md`
- Quick start: `QUICKSTART.md`
- AI features: `AI_FEATURES_GUIDE.md`
- Interactive: `INTERACTIVE_MODE_GUIDE.md`
- Setup help: `SETUP_GUIDE.md`

---

## ✅ Quick Checklist

- [ ] Run `./cogniscor.sh --setup` (first time)
- [ ] Enter OpenAI API key (optional but recommended)
- [ ] Run `./cogniscor.sh https://company.com`
- [ ] Review executive summary
- [ ] Request improvements if needed
- [ ] Check reports in `./data/company/reports/`

---

**Cogniscor v2.0** - Just the URL, AI handles the rest! 🚀
