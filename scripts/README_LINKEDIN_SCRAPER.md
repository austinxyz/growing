# LinkedIn Job Scraper

Two approaches for scraping LinkedIn company job listings programmatically.

## 📋 Overview

| Script | Method | Authentication | Speed | Reliability |
|--------|--------|----------------|-------|-------------|
| `linkedin_job_scraper.py` | API/HTML | Optional | ⚡ Fast | ⚠️ Limited (authwall) |
| `linkedin_browser_scraper.py` | Browser automation | Required | 🐢 Slower | ✅ Full access |

## 🚀 Quick Start

### Option 1: Simple API Scraper (Limited)

**Pros**: Fast, no browser needed
**Cons**: LinkedIn blocks most requests with authwall

```bash
# Install dependencies
pip install requests

# Run
python3 scripts/linkedin_job_scraper.py --company intuit --output jobs.json
```

**Expected Result**: May only get job IDs without full details due to authentication wall.

---

### Option 2: Browser Scraper (Recommended) ✅

**Pros**: Full access to job details, handles JavaScript
**Cons**: Requires Playwright, slower

#### Installation

```bash
# Install Playwright
pip install playwright

# Install browser binaries
playwright install chromium
```

#### Usage

**Guest Mode** (Limited, no login):
```bash
python3 scripts/linkedin_browser_scraper.py --company intuit --headless
```

**Authenticated Mode** (Recommended, full access):
```bash
# First time: Opens browser for manual login, saves session
python3 scripts/linkedin_browser_scraper.py \
    --company intuit \
    --auth \
    --output intuit_jobs.json

# Subsequent runs: Reuses saved session (no login needed)
python3 scripts/linkedin_browser_scraper.py \
    --company intuit \
    --headless \
    --auth \
    --auth-storage linkedin_auth.json \
    --output intuit_jobs.json
```

## 📊 Output Format

Both scripts output JSON:

```json
{
  "company": "intuit",
  "total_jobs": 45,
  "scraped_at": "2026-01-17 10:30:00",
  "jobs": [
    {
      "id": "1234567890",
      "title": "Senior Software Engineer",
      "company": "Intuit",
      "location": "Mountain View, CA",
      "datePosted": "2026-01-15",
      "url": "https://www.linkedin.com/jobs/view/1234567890",
      "metadata": "Full-time · Mid-Senior level"
    },
    ...
  ]
}
```

## 🔧 Integration with Growing App

### Backend Integration (Java Spring Boot)

Create a service to call the Python scraper:

```java
@Service
public class LinkedInScraperService {

    public List<JobDTO> fetchCompanyJobs(String companySlug) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
            "python3",
            "scripts/linkedin_browser_scraper.py",
            "--company", companySlug,
            "--headless",
            "--auth",
            "--auth-storage", "linkedin_auth.json"
        );

        pb.directory(new File(System.getProperty("user.dir")));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        String output = new String(process.getInputStream().readAllBytes());

        // Parse JSON output
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(output);

        List<JobDTO> jobs = new ArrayList<>();
        for (JsonNode jobNode : root.get("jobs")) {
            JobDTO job = new JobDTO();
            job.setTitle(jobNode.get("title").asText());
            job.setCompany(jobNode.get("company").asText());
            job.setLocation(jobNode.get("location").asText());
            job.setUrl(jobNode.get("url").asText());
            job.setPostedDate(jobNode.get("datePosted").asText());
            jobs.add(job);
        }

        return jobs;
    }
}
```

### REST API Endpoint

```java
@RestController
@RequestMapping("/api/linkedin")
public class LinkedInController {

    @Autowired
    private LinkedInScraperService scraperService;

    @GetMapping("/companies/{companySlug}/jobs")
    public ResponseEntity<List<JobDTO>> getCompanyJobs(@PathVariable String companySlug) {
        try {
            List<JobDTO> jobs = scraperService.fetchCompanyJobs(companySlug);
            return ResponseEntity.ok(jobs);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
```

### Frontend Integration (Vue 3)

```javascript
// src/api/linkedinApi.js
import apiClient from './index'

export default {
  fetchCompanyJobs(companySlug) {
    return apiClient.get(`/linkedin/companies/${companySlug}/jobs`)
  }
}
```

```vue
<!-- JobApplicationList.vue -->
<script setup>
import linkedinApi from '@/api/linkedinApi'

const importFromLinkedIn = async (companyName) => {
  try {
    const jobs = await linkedinApi.fetchCompanyJobs(companyName)
    // Process and save jobs to database
    jobs.forEach(job => {
      // Create job application entry
    })
  } catch (error) {
    console.error('Failed to import LinkedIn jobs:', error)
  }
}
</script>
```

## ⚠️ Important Notes

### Authentication

LinkedIn **requires login** for full job access. Two approaches:

1. **Manual login (first time only)**:
   - Run with `--auth` flag (without `--headless`)
   - Browser opens, you log in manually
   - Session saved to `linkedin_auth.json`
   - Subsequent runs reuse the session

2. **Provide li_at cookie**:
   - Log into LinkedIn in your browser
   - Copy `li_at` cookie value
   - Pass to simple scraper: `--cookie "YOUR_LI_AT_COOKIE"`

### Rate Limiting

- LinkedIn may rate limit aggressive scraping
- Add delays between requests: `time.sleep(2)`
- Don't scrape hundreds of companies in short time
- Recommended: Max 10-20 companies per hour

### Legal Considerations

- Review LinkedIn's Terms of Service
- Scraping for personal use generally acceptable
- Don't resell scraped data
- Respect robots.txt
- Consider using LinkedIn's official API if available (requires partnership)

## 🐛 Troubleshooting

**"Authentication required" error**:
- Solution: Use `--auth` mode and log in manually
- Or: Provide valid `li_at` cookie

**"Playwright not installed"**:
```bash
pip install playwright
playwright install chromium
```

**Jobs array is empty**:
- Check if company slug is correct (e.g., 'intuit' not 'Intuit Inc.')
- Verify authentication worked (check `linkedin_auth.json` exists)
- Try running without `--headless` to see what's happening

**SSL/OpenSSL warnings**:
- These are warnings, not errors (script still works)
- To fix: `pip install --upgrade urllib3`

## 📝 Example Companies

Common company slugs:
- `intuit` - Intuit
- `google` - Google
- `microsoft` - Microsoft
- `amazon` - Amazon
- `meta` - Meta (Facebook)
- `apple` - Apple
- `netflix` - Netflix

Find company slug from LinkedIn URL:
```
https://www.linkedin.com/company/{COMPANY_SLUG}/jobs/
                                  ^^^^^^^^^^^^^
```

## 🔄 Automation

### Scheduled Job Scraping

Use cron to scrape jobs daily:

```bash
# Add to crontab: scrape Intuit jobs every day at 9 AM
0 9 * * * cd /path/to/growing && python3 scripts/linkedin_browser_scraper.py --company intuit --headless --auth --output /tmp/intuit_jobs.json
```

### Batch Scraping Multiple Companies

```bash
#!/bin/bash
# scrape_companies.sh

COMPANIES=("intuit" "google" "microsoft" "amazon")

for company in "${COMPANIES[@]}"; do
    echo "Scraping $company..."
    python3 scripts/linkedin_browser_scraper.py \
        --company "$company" \
        --headless \
        --auth \
        --output "jobs_${company}.json"

    # Rate limiting delay
    sleep 60
done
```

## 📚 Further Reading

- [Playwright Documentation](https://playwright.dev/python/)
- [LinkedIn Jobs Search Parameters](https://www.linkedin.com/help/linkedin/answer/a524335)
- [Growing App Architecture](../docs/ARCHITECTURE.md)
