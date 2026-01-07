Base directory for this skill: /Users/yanzxu/claude/growing/.claude/skills/resume-analysis

# AI Resume Analysis Skill

Intelligently analyze resume-job matching using Claude's semantic understanding and contextual analysis.

## Usage

The skill accepts:
- `job_id`: ID of the job application (from job_applications table)
- `user_id`: ID of the user (uses their default resume)

Example invocations:
- "Analyze resume for job 13 user 3"
- "Run AI resume analysis for job_id=13, user_id=3"
- "Give me intelligent resume analysis for Austin's application to job 13"

## Implementation Steps

When this skill is invoked:

### 1. Load Database Credentials
```bash
source backend/.env
MYSQL_CLIENT=$(brew --prefix mysql-client)/bin/mysql
```

### 2. Query Job Description
```sql
SELECT id, company_id, job_title, qualifications, responsibilities, description
FROM job_applications
WHERE id = {job_id};
```

### 3. Query Resume Data
```sql
-- Get default resume
SELECT id, user_id, about, education, certifications, is_default
FROM resumes
WHERE user_id = {user_id} AND is_default = 1;

-- Get education background
SELECT school, degree, major, start_date, end_date, gpa, achievements
FROM resume_education
WHERE resume_id = {resume_id}
ORDER BY sort_order;

-- Get project experiences
SELECT id, project_name, tech_stack, what_description, my_contribution,
       situation, task, action_taken, result, start_date, end_date
FROM project_experiences
WHERE user_id = {user_id}
ORDER BY start_date DESC;

-- Get management experiences
SELECT id, title, team_info, actions_taken, results, focus_area_id,
       situation, task, action_taken AS mgmt_action, result AS mgmt_result,
       start_date, end_date
FROM management_experiences
WHERE user_id = {user_id}
ORDER BY start_date DESC;
```

### 4. Perform AI Analysis

Use Claude's semantic understanding to analyze:

**A. Education Match (15% weight)**
- Does degree level meet requirements? (Bachelor's, Master's, PhD)
- Is major relevant to the position?
- Consider: Computer Science, Engineering, related technical fields
- Output:
  - `hasRequiredDegree`: boolean
  - `majorRelevance`: 0.0-1.0
  - `highestDegree`: string
  - `major`: string
  - `score`: 0-100
  - `explanation`: detailed reasoning

**B. Experience Match (25% weight)**
- Extract total years of experience from resume.about or calculate from graduation date
- Extract required years from JD (pattern: "X+ years")
- Calculate management years from management_experiences
- Compare total years vs required years
- Output:
  - `totalYears`: number
  - `managementYears`: number
  - `meetsMinimum`: boolean
  - `requiredYears`: string
  - `score`: 0-100
  - `explanation`: detailed reasoning

**C. Technical Skills Match (30% weight)**
- **SEMANTIC MATCHING** - Don't just match keywords!
- Understand that "K8s framework" = "Kubernetes"
- Recognize similar technologies: Docker/Containerization, ES/Elasticsearch
- Extract skills from JD's qualifications + responsibilities
- Extract skills from project tech_stack, what_description, my_contribution
- Identify matched skills (with semantic equivalence)
- Identify missing critical skills
- Output:
  - `matchedSkills`: [string]
  - `missingSkills`: [string]
  - `score`: 0-100
  - `explanation`: why each skill matched or didn't match

**D. Responsibility Match (20% weight)**
- Analyze if resume demonstrates similar responsibilities to JD
- Look for:
  - Platform Building: "build platform", "operate platform", "infrastructure"
  - Team Coaching: "coach team", "mentor", "train developers", "up-level"
  - Technical Strategy: "strategy", "roadmap", "vision", "architecture"
- Match project descriptions + management actions to JD responsibilities
- Output:
  - `matchedCount`: number
  - `totalCount`: number
  - `matchedKeywords`: [string]
  - `score`: 0-100
  - `explanation`: which responsibilities match

**E. Soft Skills Match (10% weight)**
- Identify soft skills demonstrated in management experiences + resume.about
- Leadership: "lead", "manage", "direct", "supervise"
- Coaching: "coach", "mentor", "train", "develop", "grow"
- Communication: "communicate", "collaborate", "partner", "coordinate"
- Strategy: "strategy", "strategic", "vision", "roadmap"
- Problem Solving: "solve", "troubleshoot", "debug", "investigate"
- Output:
  - `demonstratedSkills`: [string]
  - `missingSkills`: [string]
  - `score`: 0-100
  - `explanation`: evidence for each soft skill

### 5. Calculate Overall Match Score

Weighted average:
```
matchScore = educationScore * 0.15 +
             experienceScore * 0.25 +
             skillScore * 0.30 +
             responsibilityScore * 0.20 +
             softSkillScore * 0.10
```

### 6. Generate Insights

**Strengths** - What makes this candidate strong:
- Highlight positive aspects from each dimension
- Focus on unique value propositions
- Examples:
  - "Master's degree from top university matches requirement"
  - "20+ years experience far exceeds 5+ years requirement"
  - "Deep expertise in Kubernetes ecosystem (K8s, operators, controllers)"

**Improvements** - What could be better:
- Identify gaps in each dimension
- Provide actionable suggestions
- Be specific, not generic
- Examples:
  - "Add experience with Elasticsearch for data platform work"
  - "Highlight more leadership examples in project descriptions"

**Customization Suggestions** (only if matchScore > 70):
- Keyword suggestions: which skills to emphasize
- Project optimizations: how to reframe projects for this JD
- Skill highlights: how to demonstrate proficiency
- Structural suggestions: how to reorganize resume

### 7. Return JSON Result

Output **VALID JSON** matching this exact structure:

```json
{
  "jobApplicationId": 13,
  "resumeId": 2,
  "matchScore": 90,
  "educationMatch": {
    "hasRequiredDegree": true,
    "majorRelevance": 0.7,
    "highestDegree": "Master Degree",
    "major": "EE/Automation",
    "score": 85,
    "explanation": "Master's degree from Zhejiang University in related technical field"
  },
  "experienceMatch": {
    "totalYears": 20,
    "managementYears": 1,
    "meetsMinimum": true,
    "requiredYears": "5+ years",
    "score": 100,
    "explanation": "20 years of experience far exceeds the 5+ years requirement"
  },
  "skillMatch": {
    "matchedSkills": ["Kubernetes", "Distributed Systems", "Cloud Native"],
    "missingSkills": ["specific tools if any"],
    "score": 90,
    "explanation": "Strong match on core technologies. 'K8s framework' clearly indicates Kubernetes expertise."
  },
  "responsibilityMatch": {
    "matchedCount": 2,
    "totalCount": 3,
    "matchedKeywords": ["Platform Building", "Technical Strategy"],
    "score": 66,
    "explanation": "Cloud Native Migration project demonstrates platform building experience"
  },
  "softSkillMatch": {
    "demonstratedSkills": ["Leadership", "Coaching"],
    "missingSkills": [],
    "score": 100,
    "explanation": "Management experience shows leadership in team coaching and mentoring"
  },
  "strengths": [
    "Strong educational background with Master's degree",
    "Extensive 20+ years of industry experience",
    "Deep Kubernetes and cloud-native expertise",
    "Proven track record in platform engineering"
  ],
  "improvements": [
    "Could highlight more specific metrics/outcomes in projects",
    "Consider adding more details about team size managed"
  ],
  "customization": {
    "keywordSuggestions": [
      {
        "keyword": "Kubernetes",
        "section": "Projects section",
        "reason": "Critical skill for this role - emphasize operator and controller development"
      }
    ],
    "projectOptimizations": [
      {
        "projectId": 2,
        "projectName": "Cloud Native Migration",
        "suggestions": [
          "Quantify impact: How many services migrated? Performance improvements?",
          "Highlight K8s operator development - this matches the JD's platform engineering focus",
          "Emphasize observability dashboard as it shows full-stack capability"
        ]
      }
    ],
    "skillHighlights": [
      {
        "skill": "Kubernetes",
        "currentMention": "Listed in tech stack",
        "suggestedMention": "Detail specific K8s components used: operators, controllers, CRDs, admission webhooks"
      }
    ],
    "structuralSuggestions": [
      "Lead with Cloud Native Migration project as it's most relevant",
      "Add a 'Core Competencies' section highlighting Kubernetes, Distributed Systems",
      "Quantify achievements with specific metrics (uptime, performance gains, cost savings)"
    ]
  }
}
```

## Critical Requirements

1. **Semantic Understanding** - Don't just match exact keywords:
   - "K8s" = "Kubernetes"
   - "container" technologies ≠ specifically "Docker" (understand context)
   - "distributed systems" encompasses many concepts
   - "cloud native" relates to Kubernetes, microservices, etc.

2. **Context Awareness** - Understand relationships:
   - Project descriptions reveal skill proficiency
   - Management experiences demonstrate soft skills
   - About section often contains years of experience
   - Date ranges indicate recency and relevance

3. **Actionable Insights** - Provide specific, helpful suggestions:
   - ✅ "Quantify K8s operator impact: how many CRDs, controllers?"
   - ❌ "Add more details"

4. **Valid JSON** - Output must be parseable JSON:
   - Use double quotes for strings
   - Escape special characters
   - No trailing commas
   - Valid boolean/number types

## Error Handling

- If job_id not found: Return error message
- If user has no default resume: Return error message
- If database query fails: Return error with details
- If unable to generate analysis: Explain what went wrong

## Database Connection

- **Host**: 10.0.0.7:37719
- **Database**: growing
- **Credentials**: From `backend/.env` (DB_USER, DB_PASSWORD)
- **MySQL Client**: Use Homebrew installation path

## Output Format

Return **ONLY** the JSON analysis result. No additional commentary or explanation outside the JSON structure.

The frontend will parse this JSON and display it in the resume analysis UI.
