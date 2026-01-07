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
-- Check table structure first
DESCRIBE job_applications;

-- Query job details (use actual column names from DESCRIBE)
SELECT id, company_id, position_name, position_level, qualifications, responsibilities
FROM job_applications
WHERE id = {job_id};
```

### 3. Query Resume Data

**CRITICAL: Always check table structures first to avoid column name errors!**

```sql
-- Step 1: Get default resume
SELECT id, user_id, about, career_objective
FROM resumes
WHERE user_id = {user_id} AND is_default = 1;

-- Step 2: Get education background
SELECT school_name, degree, major, start_date, end_date, gpa, courses
FROM resume_education
WHERE resume_id = {resume_id}
ORDER BY sort_order;

-- Step 3: 🚨 CRITICAL - Get work experience (PRIMARY source for management years!)
-- This is the MOST IMPORTANT table for calculating management experience
SELECT company_name, position, location, start_date, end_date, is_current,
       responsibilities, achievements
FROM resume_experiences
WHERE resume_id = {resume_id}
ORDER BY start_date DESC;

-- Step 4: Get project experiences
SELECT project_name, project_type, what_description, my_contribution,
       background, problem_statement, challenges, my_role,
       tech_stack, architecture, innovation,
       quantitative_results, business_impact, personal_growth,
       start_date, end_date, tech_tags
FROM project_experiences
WHERE user_id = {user_id}
ORDER BY start_date DESC;

-- Step 5: Get management experiences (specific management cases/stories)
-- Note: This is for specific coaching/mentoring examples, NOT total management years
SELECT experience_name, team_growth_subtype, background, actions_taken,
       results, lessons_learned, hiring_count, improvement_result,
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

🚨 **CRITICAL: Calculate management years from resume_experiences, NOT management_experiences!**

**Step-by-step calculation:**
1. **Total Years**: Extract from resume.about (e.g., "20+ years") OR calculate from earliest start_date in resume_experiences
2. **Management Years**:
   - **PRIMARY SOURCE**: Calculate from `resume_experiences` where `position` contains management keywords:
     - "Manager", "Lead", "Director", "VP", "Head of", "Team Lead", "Project Manager"
   - Sum all date ranges where position indicates management role
   - Example: If 5 jobs with "Manager" titles spanning 2000-2024 = 24 years
3. **Required Years**: Extract from JD qualifications (pattern: "X+ years", "X-Y years")
4. **Compare**: Check if candidate meets minimums for both total and management experience

**Output:**
  - `totalYears`: number (total work experience)
  - `managementYears`: number (calculated from resume_experiences positions!)
  - `meetsMinimum`: boolean (meets both total AND management requirements)
  - `requiredYears`: string (what JD asks for)
  - `score`: 0-100 (how much they exceed requirements)
  - `explanation`: detailed reasoning with calculation breakdown

**Common Mistakes to Avoid:**
- ❌ WRONG: Only counting management_experiences entries (these are specific stories, not full history)
- ✅ CORRECT: Calculate from resume_experiences position titles and date ranges

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

## Common Errors & How to Avoid Them

### 🚨 Error #1: Using Wrong Column Names
**Problem**: SQL queries fail with "Unknown column" errors
**Root Cause**: Not checking table structure before querying
**Solution**:
```sql
-- ALWAYS run DESCRIBE first!
DESCRIBE job_applications;
DESCRIBE resumes;
DESCRIBE resume_experiences;
```
**Examples of Wrong Column Names**:
- ❌ `job_applications.job_title` → ✅ `position_name`
- ❌ `resumes.education` → ✅ Use `resume_education` table
- ❌ `resume_education.school` → ✅ `school_name`
- ❌ `project_experiences.situation` → ✅ `background`
- ❌ `management_experiences.title` → ✅ `experience_name`

### 🚨 Error #2: Missing resume_experiences Table (CRITICAL!)
**Problem**: Management years drastically underestimated (e.g., 1 year instead of 24 years)
**Root Cause**: Only querying `management_experiences` table, which contains specific coaching stories, NOT full work history
**Solution**:
- **ALWAYS query `resume_experiences` table FIRST** - this is the primary source of work history
- Calculate management years from `position` field (e.g., "Software Development Manager", "Team Lead")
- Use `management_experiences` only for specific coaching/mentoring examples
**Impact**: This error causes 20+ year management veterans to appear as 1-year managers!

### 🚨 Error #3: Confusing management_experiences vs resume_experiences
**What they are**:
- `resume_experiences`: **Complete work history** with all jobs, titles, companies, responsibilities
  - Use for: Total years, management years, career progression
- `management_experiences`: **Specific management stories** (coaching cases, team growth examples)
  - Use for: Soft skills evidence, coaching examples, leadership stories

**Calculation Example**:
```
resume_experiences:
  2023-now: Software Development Manager (1.3 years)
  2017-2023: Software Development Manager (6.5 years)
  2012-2017: Software Development Manager (5 years)
  2007-2012: Team Lead (4.6 years)
  2000-2007: Project Manager (7 years)
  Total Management Years: 24.4 years ✅

management_experiences:
  2024: Yiran's growing (1 year)
  Total: 1 entry
  ❌ WRONG if used for total management years!
```

### 🚨 Error #4: Not Extracting Skills from Work Experience
**Problem**: Missing critical skills mentioned in responsibilities/achievements
**Solution**: Parse skills from `resume_experiences.responsibilities` and `achievements` fields, not just project tech_stack

### 🚨 Error #5: Hardcoded Field Assumptions
**Problem**: Assuming field names based on common conventions
**Solution**: Check actual database schema - different projects use different naming conventions

## Error Handling

- If job_id not found: Return error message
- If user has no default resume: Return error message
- If database query fails: Return error with details
- If unable to generate analysis: Explain what went wrong
- If column name error occurs: Re-check table structure with DESCRIBE

## Database Connection

- **Host**: 10.0.0.7:37719
- **Database**: growing
- **Credentials**: From `backend/.env` (DB_USER, DB_PASSWORD)
- **MySQL Client**: Use Homebrew installation path

## Output Format

Return **ONLY** the JSON analysis result. No additional commentary or explanation outside the JSON structure.

The frontend will parse this JSON and display it in the resume analysis UI.
