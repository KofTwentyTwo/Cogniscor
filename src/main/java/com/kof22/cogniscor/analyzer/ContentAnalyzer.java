package com.kof22.cogniscor.analyzer;

import com.kof22.cogniscor.llm.LLMContentEnhancer;
import com.kof22.cogniscor.llm.OpenAIService;
import com.kof22.cogniscor.model.CompanyData;
import com.kof22.cogniscor.model.SourceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Enhanced analyzer that extracts structured information from collected data.
 * Specializes in executive team, recent news, products, and technology stacks.
 * Can optionally use LLM to enhance extraction.
 */
public class ContentAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ContentAnalyzer.class);
    private LLMContentEnhancer llmEnhancer;
    
    /**
     * Set LLM enhancer for AI-powered extraction enhancement.
     */
    public void setLLMEnhancer(OpenAIService openAIService) {
        if (openAIService != null && openAIService.isEnabled()) {
            this.llmEnhancer = new LLMContentEnhancer(openAIService);
            logger.info("LLM content enhancement enabled");
        }
    }
    
    /**
     * Analyzes all source data and populates CompanyData fields.
     */
    public void analyze(CompanyData companyData) {
        logger.info("Analyzing collected data for: {}", companyData.getCompanyName());
        
        // Combine all successful source content
        String allContent = companyData.getSources().stream()
            .filter(SourceData::isSuccessful)
            .map(SourceData::getContent)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n\n"));
        
        if (allContent.isEmpty()) {
            logger.warn("No content available to analyze");
            return;
        }
        
        // Extract various company information using regex patterns
        extractOverview(companyData, allContent);
        extractIndustry(companyData, allContent);
        extractFoundingInfo(companyData, allContent);
        extractHeadquarters(companyData, allContent);
        extractCompanySize(companyData, allContent);
        extractRevenue(companyData, allContent);
        extractLeadership(companyData, allContent);  // Enhanced
        extractMission(companyData, allContent);
        extractCulture(companyData, allContent);
        extractProducts(companyData, allContent);    // Enhanced
        extractTechnologies(companyData, allContent); // Enhanced
        extractNews(companyData, allContent);         // Enhanced
        extractReviews(companyData, allContent);
        
        logger.info("Regex extraction completed - Executive Team: {}, Products: {}, Technologies: {}, News Items: {}", 
            companyData.getLeadership().size(),
            companyData.getProducts().size(),
            companyData.getTechnologies().size(),
            companyData.getRecentNews().size());
        
        // Use LLM to enhance extraction if available
        if (llmEnhancer != null) {
            llmEnhancer.enhanceExtraction(companyData, allContent);
            logger.info("LLM enhancement completed - Executive Team: {}, Products: {}, Technologies: {}, News Items: {}", 
                companyData.getLeadership().size(),
                companyData.getProducts().size(),
                companyData.getTechnologies().size(),
                companyData.getRecentNews().size());
        }
    }
    
    private void extractOverview(CompanyData companyData, String content) {
        // Look for overview/description sections
        String overview = extractSection(content, 
            "(?i)(overview|about|description|what we do|company profile).*?\\n\\n([^\\n]{100,500})");
        
        if (overview == null) {
            // Fallback: use first substantial paragraph
            Pattern pattern = Pattern.compile("^([^\\n]{100,500})", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                overview = matcher.group(1);
            }
        }
        
        companyData.setOverview(overview);
    }
    
    private void extractIndustry(CompanyData companyData, String content) {
        // Look for industry mentions
        Pattern pattern = Pattern.compile("(?i)industry:?\\s*([^\\n]{5,100})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            companyData.setIndustry(matcher.group(1).trim());
        } else {
            // Look for common industry keywords
            String[] industries = {
                "Technology", "Software", "Healthcare", "Finance", "Fintech",
                "E-commerce", "Retail", "Manufacturing", "Consulting", "Education",
                "Media", "Entertainment", "Telecommunications", "Energy", "Automotive",
                "Artificial Intelligence", "Cloud Computing", "Cybersecurity", "SaaS"
            };
            
            for (String industry : industries) {
                if (content.toLowerCase().contains(industry.toLowerCase())) {
                    companyData.setIndustry(industry);
                    break;
                }
            }
        }
    }
    
    private void extractFoundingInfo(CompanyData companyData, String content) {
        // Look for founding year
        Pattern pattern = Pattern.compile("(?i)(founded|established|started|created)\\s+(?:in\\s+)?(\\d{4})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            companyData.setFoundedYear(matcher.group(2));
        }
        
        // Look for founders with multiple patterns
        Pattern founderPattern = Pattern.compile("(?i)(?:founded|co-founded|started)\\s+by\\s+([^\\n]{10,150})");
        Matcher founderMatcher = founderPattern.matcher(content);
        if (founderMatcher.find()) {
            String foundersText = founderMatcher.group(1);
            Arrays.stream(foundersText.split("(?:,|and)"))
                .map(String::trim)
                .filter(s -> s.length() > 2 && s.length() < 50)
                .forEach(companyData::addFounder);
        }
        
        // Alternative pattern: "founders: Name1, Name2"
        Pattern founderPattern2 = Pattern.compile("(?i)founders?:?\\s*([^\\n]{10,150})");
        Matcher founderMatcher2 = founderPattern2.matcher(content);
        if (founderMatcher2.find() && companyData.getFounders().isEmpty()) {
            String foundersText = founderMatcher2.group(1);
            Arrays.stream(foundersText.split("(?:,|and)"))
                .map(String::trim)
                .filter(s -> s.length() > 2 && s.length() < 50)
                .forEach(companyData::addFounder);
        }
    }
    
    private void extractHeadquarters(CompanyData companyData, String content) {
        Pattern pattern = Pattern.compile("(?i)(?:headquarters|headquartered|based)\\s+(?:in|at)?\\s*:?\\s*([^\\n]{5,100})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            companyData.setHeadquarters(matcher.group(1).trim());
        }
    }
    
    private void extractCompanySize(CompanyData companyData, String content) {
        Pattern pattern = Pattern.compile("(?i)(\\d+[,\\s]*\\d*)\\+?\\s*employees");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            companyData.setCompanySize(matcher.group(1) + " employees");
        }
    }
    
    private void extractRevenue(CompanyData companyData, String content) {
        Pattern pattern = Pattern.compile("(?i)revenue:?\\s*([\\$€£][^\\n]{5,50})");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            companyData.setRevenue(matcher.group(1).trim());
        }
    }
    
    /**
     * Enhanced executive team extraction with multiple patterns and role detection.
     */
    private void extractLeadership(CompanyData companyData, String content) {
        Set<String> leaders = new LinkedHashSet<>();
        
        // Pattern 1: Title: Name format
        Pattern pattern1 = Pattern.compile("(?i)(CEO|CTO|CFO|COO|President|Chief[\\w\\s]*Officer|Vice President|VP|Director|Head of[\\w\\s]*)[:\\s-]+([A-Z][a-z]+\\s+[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)");
        Matcher matcher1 = pattern1.matcher(content);
        while (matcher1.find()) {
            String title = matcher1.group(1).trim();
            String name = matcher1.group(2).trim();
            leaders.add(title + ": " + name);
        }
        
        // Pattern 2: Name, Title format
        Pattern pattern2 = Pattern.compile("([A-Z][a-z]+\\s+[A-Z][a-z]+),\\s+(CEO|CTO|CFO|COO|President|Chief[\\w\\s]*Officer)");
        Matcher matcher2 = pattern2.matcher(content);
        while (matcher2.find()) {
            String name = matcher2.group(1).trim();
            String title = matcher2.group(2).trim();
            leaders.add(title + ": " + name);
        }
        
        // Pattern 3: Name - Title format
        Pattern pattern3 = Pattern.compile("([A-Z][a-z]+\\s+[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)\\s+-\\s+(CEO|CTO|CFO|COO|President|Chief[\\w\\s]*Officer)");
        Matcher matcher3 = pattern3.matcher(content);
        while (matcher3.find()) {
            String name = matcher3.group(1).trim();
            String title = matcher3.group(2).trim();
            leaders.add(title + ": " + name);
        }
        
        // Pattern 4: Leadership team / Executive team sections
        Pattern sectionPattern = Pattern.compile("(?i)(?:leadership|executive)\\s+(?:team|board)[^\\n]*\\n([^\\n]{0,1000})");
        Matcher sectionMatcher = sectionPattern.matcher(content);
        if (sectionMatcher.find()) {
            String leadershipSection = sectionMatcher.group(1);
            // Extract names with titles from this section
            Pattern namePattern = Pattern.compile("([A-Z][a-z]+\\s+[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)[\\s-]+(CEO|CTO|CFO|COO|Chief|President|VP|Director|Head)");
            Matcher nameMatcher = namePattern.matcher(leadershipSection);
            while (nameMatcher.find()) {
                leaders.add(nameMatcher.group(2) + ": " + nameMatcher.group(1));
            }
        }
        
        logger.info("Extracted {} executive team members", leaders.size());
        leaders.forEach(companyData::addLeader);
    }
    
    private void extractMission(CompanyData companyData, String content) {
        String mission = extractSection(content, 
            "(?i)(mission|vision|purpose|our goal).*?\\n\\n([^\\n]{50,500})");
        companyData.setMission(mission);
    }
    
    private void extractCulture(CompanyData companyData, String content) {
        String culture = extractSection(content,
            "(?i)(culture|values|principles|what we believe).*?\\n\\n([^\\n]{50,500})");
        companyData.setCulture(culture);
    }
    
    /**
     * Enhanced product extraction with multiple detection strategies.
     */
    private void extractProducts(CompanyData companyData, String content) {
        Set<String> products = new LinkedHashSet<>();
        
        // Pattern 1: "Products: X, Y, Z" format
        Pattern pattern1 = Pattern.compile("(?i)(?:products?|services?|offerings?|solutions?)\\s*:?\\s*([^\\n]{10,300})");
        Matcher matcher1 = pattern1.matcher(content);
        while (matcher1.find()) {
            String productsText = matcher1.group(1);
            Arrays.stream(productsText.split("(?:,|;|\\.|•|and)"))
                .map(String::trim)
                .filter(s -> s.length() > 3 && s.length() < 100)
                .filter(s -> !s.matches("(?i)^(the|our|their|includes?|such as).*"))
                .forEach(products::add);
        }
        
        // Pattern 2: Bullet points with product names (capitalized)
        Pattern pattern2 = Pattern.compile("(?m)^[•\\-*]\\s*([A-Z][\\w\\s]{3,50})(?:\\s*[-:]|$)");
        Matcher matcher2 = pattern2.matcher(content);
        while (matcher2.find()) {
            String product = matcher2.group(1).trim();
            if (!product.matches("(?i).*(about|contact|careers|team|news|blog).*")) {
                products.add(product);
            }
        }
        
        // Pattern 3: "Product Name" - description format
        Pattern pattern3 = Pattern.compile("([A-Z][\\w\\s]{2,40})\\s*[-–—]\\s*(?:a|an|the)?\\s*([a-z][^\\n]{20,150})");
        Matcher matcher3 = pattern3.matcher(content);
        while (matcher3.find()) {
            String productName = matcher3.group(1).trim();
            String description = matcher3.group(2).trim();
            // Check if description indicates this is a product
            if (description.matches("(?i).*(platform|tool|solution|software|service|product|application|app).*")) {
                products.add(productName + " - " + description.substring(0, Math.min(description.length(), 80)));
            }
        }
        
        // Pattern 4: Section-based extraction
        Pattern sectionPattern = Pattern.compile("(?i)(?:our\\s+)?(?:products?|solutions?|offerings?)\\s*\\n([^\\n]{0,1500})");
        Matcher sectionMatcher = sectionPattern.matcher(content);
        if (sectionMatcher.find()) {
            String productSection = sectionMatcher.group(1);
            // Extract product names (capitalized words/phrases)
            Pattern namePattern = Pattern.compile("\\b([A-Z][\\w\\s]{3,40})\\b");
            Matcher nameMatcher = namePattern.matcher(productSection);
            while (nameMatcher.find() && products.size() < 20) {
                String name = nameMatcher.group(1).trim();
                if (!name.matches("(?i).*(about|contact|learn|more|click|here).*")) {
                    products.add(name);
                }
            }
        }
        
        logger.info("Extracted {} products/services", products.size());
        products.forEach(companyData::addProduct);
    }
    
    /**
     * Enhanced technology stack extraction with comprehensive detection.
     */
    private void extractTechnologies(CompanyData companyData, String content) {
        Set<String> foundTech = new LinkedHashSet<>();
        String lowerContent = content.toLowerCase();
        
        // Comprehensive technology lists
        Map<String, String[]> techCategories = new HashMap<>();
        
        // Programming Languages
        techCategories.put("Languages", new String[]{
            "Java", "Python", "JavaScript", "TypeScript", "Go", "Golang", "Rust", "C++", "C#", 
            "Ruby", "PHP", "Swift", "Kotlin", "Scala", "R", "Julia", "Dart", "Elixir"
        });
        
        // Frontend Technologies
        techCategories.put("Frontend", new String[]{
            "React", "Vue.js", "Vue", "Angular", "Svelte", "Next.js", "Nuxt.js", 
            "jQuery", "Bootstrap", "Tailwind CSS", "Material UI", "Webpack", "Vite"
        });
        
        // Backend Technologies
        techCategories.put("Backend", new String[]{
            "Node.js", "Express", "Django", "Flask", "FastAPI", "Spring Boot", "Spring", 
            ".NET", "ASP.NET", "Laravel", "Ruby on Rails", "Rails", "GraphQL", "REST API"
        });
        
        // Cloud & Infrastructure
        techCategories.put("Cloud", new String[]{
            "AWS", "Amazon Web Services", "Azure", "Microsoft Azure", "Google Cloud", "GCP", 
            "Oracle Cloud", "IBM Cloud", "DigitalOcean", "Heroku", "Vercel", "Netlify",
            "CloudFlare", "Cloudflare"
        });
        
        // DevOps & Containers
        techCategories.put("DevOps", new String[]{
            "Docker", "Kubernetes", "K8s", "Jenkins", "GitLab CI", "GitHub Actions", 
            "CircleCI", "Travis CI", "Terraform", "Ansible", "Chef", "Puppet"
        });
        
        // Databases
        techCategories.put("Databases", new String[]{
            "PostgreSQL", "MySQL", "MongoDB", "Redis", "Elasticsearch", "Cassandra",
            "DynamoDB", "Oracle Database", "SQL Server", "MariaDB", "CouchDB", 
            "Neo4j", "InfluxDB", "Snowflake", "BigQuery"
        });
        
        // AI/ML & Data
        techCategories.put("AI/ML", new String[]{
            "Machine Learning", "Deep Learning", "TensorFlow", "PyTorch", "Keras",
            "Scikit-learn", "OpenAI", "GPT", "LLM", "Neural Networks", "NLP",
            "Computer Vision", "Artificial Intelligence", "AI"
        });
        
        // Mobile
        techCategories.put("Mobile", new String[]{
            "iOS", "Android", "React Native", "Flutter", "Xamarin", "Ionic",
            "Swift", "SwiftUI", "Objective-C"
        });
        
        // Other Technologies
        techCategories.put("Other", new String[]{
            "Blockchain", "Ethereum", "Web3", "Cryptocurrency", "Bitcoin",
            "Microservices", "Serverless", "Lambda", "API", "SaaS", "PaaS", "IaaS",
            "Apache Kafka", "Kafka", "RabbitMQ", "gRPC", "WebSockets", "WebRTC"
        });
        
        // Search for all technologies
        for (Map.Entry<String, String[]> category : techCategories.entrySet()) {
            for (String tech : category.getValue()) {
                String techLower = tech.toLowerCase();
                // Use word boundaries to avoid partial matches
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(techLower) + "\\b");
                if (pattern.matcher(lowerContent).find()) {
                    foundTech.add(tech);
                }
            }
        }
        
        // Pattern-based detection for "built with", "using", "powered by"
        Pattern techPattern = Pattern.compile("(?i)(?:built with|using|powered by|written in|based on)\\s+([^\\n.]{5,100})");
        Matcher matcher = techPattern.matcher(content);
        while (matcher.find()) {
            String techText = matcher.group(1);
            // Extract individual technologies from this phrase
            for (String[] techs : techCategories.values()) {
                for (String tech : techs) {
                    if (techText.toLowerCase().contains(tech.toLowerCase())) {
                        foundTech.add(tech);
                    }
                }
            }
        }
        
        logger.info("Extracted {} technologies", foundTech.size());
        companyData.setTechnologies(new ArrayList<>(foundTech));
    }
    
    /**
     * Enhanced news extraction with date detection and headline formatting.
     */
    private void extractNews(CompanyData companyData, String content) {
        Set<String> newsItems = new LinkedHashSet<>();
        
        // Pattern 1: News/announcement sections
        Pattern pattern1 = Pattern.compile("(?i)(?:news|announcement|press release)[:\\s]*([^\\n]{30,250})");
        Matcher matcher1 = pattern1.matcher(content);
        while (matcher1.find() && newsItems.size() < 10) {
            String news = matcher1.group(1).trim();
            if (news.length() > 20) {
                newsItems.add(news);
            }
        }
        
        // Pattern 2: Recent developments (with dates)
        Pattern pattern2 = Pattern.compile("(?i)(?:in|on|during)\\s+(january|february|march|april|may|june|july|august|september|october|november|december|\\d{4}|20\\d{2})\\s*,?\\s*([^\\n]{30,200})");
        Matcher matcher2 = pattern2.matcher(content);
        while (matcher2.find() && newsItems.size() < 10) {
            String date = matcher2.group(1);
            String news = matcher2.group(2).trim();
            if (news.length() > 20 && !news.matches("(?i).*(founded|established|started).*")) {
                newsItems.add("(" + date + ") " + news);
            }
        }
        
        // Pattern 3: Headline-style news (sentences starting with action verbs)
        Pattern pattern3 = Pattern.compile("(?i)(?:^|\\n)\\s*(?:launched|announced|released|introduced|acquired|raised|partnered|expanded|opened)\\s+([^\\n]{20,200})");
        Matcher matcher3 = pattern3.matcher(content);
        while (matcher3.find() && newsItems.size() < 10) {
            String news = matcher3.group(0).trim();
            newsItems.add(news);
        }
        
        // Pattern 4: Bullet point news items
        Pattern pattern4 = Pattern.compile("(?m)^[•\\-*]\\s*([^\\n]{30,200})");
        Matcher matcher4 = pattern4.matcher(content);
        while (matcher4.find() && newsItems.size() < 10) {
            String item = matcher4.group(1).trim();
            // Check if it looks like a news item (has action words or dates)
            if (item.matches("(?i).*(launched|announced|released|raised|expanded|acquired|2023|2024|2025).*")) {
                newsItems.add(item);
            }
        }
        
        // Pattern 5: Award and recognition news
        Pattern pattern5 = Pattern.compile("(?i)((?:named|recognized|awarded|received|won)[^\\n]{20,200})");
        Matcher matcher5 = pattern5.matcher(content);
        while (matcher5.find() && newsItems.size() < 10) {
            String news = matcher5.group(1).trim();
            if (news.length() > 25) {
                newsItems.add(news);
            }
        }
        
        logger.info("Extracted {} news items", newsItems.size());
        newsItems.forEach(companyData::addNews);
    }
    
    private void extractReviews(CompanyData companyData, String content) {
        // Look for ratings
        Pattern ratingPattern = Pattern.compile("(?i)(\\d\\.\\d)\\s*(?:out of|/)\\s*5|rating:?\\s*(\\d\\.\\d)");
        Matcher ratingMatcher = ratingPattern.matcher(content);
        if (ratingMatcher.find()) {
            String rating = ratingMatcher.group(1) != null ? ratingMatcher.group(1) : ratingMatcher.group(2);
            companyData.getReviews().setOverallRating(Double.parseDouble(rating));
        }
        
        // Look for review counts
        Pattern reviewCountPattern = Pattern.compile("(?i)(\\d+[,\\s]*\\d*)\\s*reviews");
        Matcher reviewCountMatcher = reviewCountPattern.matcher(content);
        if (reviewCountMatcher.find()) {
            String count = reviewCountMatcher.group(1).replaceAll("[,\\s]", "");
            companyData.getReviews().setTotalReviews(Integer.parseInt(count));
        }
        
        // Extract pros/cons if available
        String prosSection = extractSection(content, "(?i)pros?:?\\s*([^\\n]{20,300})");
        if (prosSection != null) {
            companyData.getReviews().addPro(prosSection);
        }
        
        String consSection = extractSection(content, "(?i)cons?:?\\s*([^\\n]{20,300})");
        if (consSection != null) {
            companyData.getReviews().addCon(consSection);
        }
    }
    
    /**
     * Helper method to extract content matching a pattern.
     */
    private String extractSection(String content, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            int groupCount = matcher.groupCount();
            return groupCount > 0 ? matcher.group(groupCount).trim() : matcher.group(0).trim();
        }
        return null;
    }
}
