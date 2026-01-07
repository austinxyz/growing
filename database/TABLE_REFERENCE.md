# Database Table Reference Guide

> **Purpose**: Quick reference for table structures to avoid SQL field name errors
> **Last Updated**: 2026-01-06 (After Phase 7 resume analysis fixes)

## 🚨 Critical Notes

1. **ALWAYS run `DESCRIBE table_name` before writing SQL queries**
2. **Common field name patterns that differ from assumptions**:
   - Job fields: `position_name` (NOT `job_title`)
   - School fields: `school_name` (NOT `school`)
   - Project fields: `background` (NOT `situation`)
   - Management fields: `experience_name` (NOT `title`)

## Resume & Job Search Tables

### 📋 resumes
**Purpose**: User's resume versions

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| user_id | bigint | Foreign key to users |
| version_name | varchar(100) | Resume version name |
| is_default | tinyint(1) | Default resume flag |
| about | text | **Summary/bio (may contain total years)** |
| career_objective | text | Career goals |
| email | varchar(255) | Contact email |
| phone | varchar(50) | Phone number |
| address | varchar(255) | Address |
| linkedin_url | varchar(500) | LinkedIn profile |
| github_url | varchar(500) | GitHub profile |
| website_url | varchar(500) | Personal website |
| other_links | json | Other links |
| languages | text | Languages |
| hobbies | text | Hobbies |
| created_at | timestamp | |
| updated_at | timestamp | |

**Key for AI Analysis**:
- `about`: May contain total years of experience (e.g., "20+ years")
- Use `is_default = 1` to find user's primary resume

---

### 💼 resume_experiences
**Purpose**: Complete work history (PRIMARY source for management years)

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| resume_id | bigint | Foreign key to resumes |
| company_name | varchar(255) | **Company name** |
| position | varchar(255) | **Job title/position** |
| location | varchar(255) | Work location |
| start_date | date | **Start date** |
| end_date | date | **End date (NULL if current)** |
| is_current | tinyint(1) | Currently working flag |
| responsibilities | text | **Job responsibilities** |
| achievements | text | **Key achievements** |
| project_ids | json | Linked project IDs |
| sort_order | int | Display order |
| created_at | timestamp | |
| updated_at | timestamp | |

**🚨 CRITICAL for AI Analysis**:
- **This is the PRIMARY source for calculating total work years and management years**
- Calculate management years by:
  1. Find positions with management keywords: "Manager", "Lead", "Director", "VP", "Team Lead", "Project Manager"
  2. Sum date ranges: `(end_date OR CURRENT_DATE) - start_date` for all management positions
- Extract skills from `responsibilities` and `achievements` fields
- Use `is_current = 1` to identify current role

**Common Mistake**: Don't confuse with `management_experiences` table (see below)

---

### 🎓 resume_education
**Purpose**: Educational background

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| resume_id | bigint | Foreign key to resumes |
| school_name | varchar(255) | **NOT "school"!** |
| degree | varchar(100) | Degree level |
| major | varchar(255) | Major/field of study |
| start_date | date | |
| end_date | date | |
| gpa | decimal(3,2) | GPA score |
| courses | text | **NOT "achievements"!** |
| sort_order | int | Display order |
| created_at | timestamp | |

**Key Points**:
- Field is `school_name`, NOT `school`
- Field is `courses`, NOT `achievements`
- Use to calculate education match and graduation year

---

### 🚀 project_experiences
**Purpose**: Project portfolio

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| user_id | bigint | Foreign key to users |
| project_name | varchar(255) | Project name |
| project_type | varchar(50) | Project type |
| what_description | text | What was built |
| start_date | date | |
| end_date | date | |
| team_size | int | Team size |
| my_role | varchar(255) | **User's role** |
| background | text | **NOT "situation"!** |
| problem_statement | text | Problem to solve |
| challenges | text | Challenges faced |
| constraints | text | Constraints |
| tech_stack | text | **Technologies used** |
| architecture | text | Architecture design |
| innovation | text | Innovations |
| my_contribution | text | **User's contribution** |
| quantitative_results | text | **Measurable results** |
| business_impact | text | **Business impact** |
| personal_growth | text | Personal learning |
| lessons_learned | text | Lessons learned |
| tech_tags | json | Tech tags |
| difficulty | varchar(50) | Difficulty level |
| focus_area_ids | json | Related focus areas |
| created_at | timestamp | |
| updated_at | timestamp | |

**🚨 CRITICAL for AI Analysis**:
- Field is `background`, NOT `situation`
- Extract skills from: `tech_stack`, `what_description`, `my_contribution`, `architecture`
- Use `quantitative_results` and `business_impact` for achievement matching
- Use `my_role` to infer leadership/technical depth

---

### 👔 management_experiences
**Purpose**: Specific management/coaching stories (NOT total management years!)

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| user_id | bigint | Foreign key to users |
| focus_area_id | bigint | Related focus area |
| experience_name | varchar(255) | **NOT "title"!** |
| team_growth_subtype | varchar(50) | Growth type |
| start_date | date | |
| end_date | date | |
| background | text | Context |
| actions_taken | text | **Actions taken** |
| results | text | **Results achieved** |
| lessons_learned | text | Lessons learned |
| hiring_count | int | Number hired |
| improvement_result | varchar(50) | Improvement outcome |
| created_at | timestamp | |
| updated_at | timestamp | |

**🚨 WARNING - Common Mistake**:
- This table stores **specific coaching/mentoring stories**, NOT complete work history
- Use for: Soft skills evidence, leadership examples, coaching ability
- **DO NOT** calculate total management years from this table
- **Instead**: Calculate from `resume_experiences` table (see above)

**Field Notes**:
- Field is `experience_name`, NOT `title`
- Use `actions_taken` and `results` for soft skills matching

---

### 💼 job_applications
**Purpose**: Job postings user is applying to

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| user_id | bigint | Foreign key to users |
| company_id | bigint | Foreign key to companies |
| position_name | varchar(255) | **NOT "job_title"!** |
| position_level | varchar(50) | **Level: Entry, Mid, Senior, Principal** |
| posted_date | date | Job posting date |
| job_url | varchar(500) | Job posting URL |
| qualifications | text | **Required qualifications** |
| responsibilities | text | **Job responsibilities** |
| application_status | varchar(50) | Application status |
| status_updated_at | timestamp | Status update time |
| status_history | json | Status history |
| offer_received_at | timestamp | Offer received time |
| base_salary | decimal(12,2) | Base salary |
| bonus | decimal(12,2) | Bonus |
| stock_value | decimal(12,2) | Stock value |
| total_compensation | decimal(12,2) | Total comp |
| offer_deadline | date | Offer deadline |
| offer_decision | varchar(50) | Accept/reject |
| offer_notes | text | Offer notes |
| rejected_at | timestamp | Rejection time |
| rejected_stage | varchar(50) | Rejection stage |
| rejection_reasons | json | Rejection reasons |
| improvement_plan | text | Improvement plan |
| notes | text | General notes |
| recruiter_insights | json | Recruiter insights |
| created_at | timestamp | |
| updated_at | timestamp | |

**🚨 CRITICAL for AI Analysis**:
- Field is `position_name`, NOT `job_title`
- Field is `position_level`, NOT `level` or `seniority`
- Extract required skills from: `qualifications` + `responsibilities`
- Extract required years: Look for patterns like "X+ years", "X-Y years"

---

### 🤖 ai_job_analysis
**Purpose**: AI-powered resume-JD matching analysis results

| Field | Type | Notes |
|-------|------|-------|
| id | bigint | Primary key |
| user_id | bigint | Foreign key to users |
| job_application_id | bigint | Foreign key to job_applications |
| resume_id | bigint | Foreign key to resumes |
| generated_prompt | text | Generated analysis prompt |
| ai_analysis_result | text | **JSON analysis result** |
| analysis_metadata | json | **Metadata (scores, strengths, weaknesses)** |
| status | varchar(50) | Analysis status |
| created_at | timestamp | |
| updated_at | timestamp | |

**Result Structure** (stored in `ai_analysis_result` as JSON string):
```json
{
  "jobApplicationId": 3,
  "resumeId": 2,
  "matchScore": 85,
  "educationMatch": { "score": 75, ... },
  "experienceMatch": { "score": 100, "totalYears": 25, "managementYears": 24, ... },
  "skillMatch": { "score": 55, "matchedSkills": [...], "missingSkills": [...] },
  "responsibilityMatch": { "score": 95, ... },
  "softSkillMatch": { "score": 100, ... },
  "strengths": [...],
  "improvements": [...],
  "customization": {...}
}
```

**Metadata Structure** (stored in `analysis_metadata` as JSON):
```json
{
  "overallScore": 85,
  "skillMatchScore": 85,
  "experienceMatchScore": 100,
  "keyStrengths": [...],
  "keyWeaknesses": [...],
  "recommendation": "Good Match"
}
```

---

## Common SQL Query Patterns

### Get Complete Resume Data for Analysis
```sql
-- Step 1: Get default resume
SELECT id, user_id, about, career_objective
FROM resumes
WHERE user_id = ? AND is_default = 1;

-- Step 2: Get education
SELECT school_name, degree, major, start_date, end_date, gpa
FROM resume_education
WHERE resume_id = ?
ORDER BY sort_order;

-- Step 3: 🚨 CRITICAL - Get work experience (PRIMARY for management years)
SELECT company_name, position, start_date, end_date, is_current,
       responsibilities, achievements
FROM resume_experiences
WHERE resume_id = ?
ORDER BY start_date DESC;

-- Step 4: Get projects
SELECT project_name, tech_stack, background, my_contribution,
       quantitative_results, business_impact
FROM project_experiences
WHERE user_id = ?
ORDER BY start_date DESC;

-- Step 5: Get management stories (for soft skills only)
SELECT experience_name, background, actions_taken, results
FROM management_experiences
WHERE user_id = ?
ORDER BY start_date DESC;
```

### Get Job Details
```sql
SELECT id, company_id, position_name, position_level,
       qualifications, responsibilities
FROM job_applications
WHERE id = ?;
```

### Calculate Management Years (Example Logic)
```sql
-- Find all management positions
SELECT position, start_date, end_date, is_current,
       DATEDIFF(COALESCE(end_date, CURRENT_DATE), start_date) / 365 AS years
FROM resume_experiences
WHERE resume_id = ?
  AND (position LIKE '%Manager%'
    OR position LIKE '%Lead%'
    OR position LIKE '%Director%'
    OR position LIKE '%VP%'
    OR position LIKE '%Head of%')
ORDER BY start_date DESC;

-- Sum the years for total management experience
```

## Field Name Quick Reference

### Commonly Confused Fields

| ❌ WRONG | ✅ CORRECT | Table |
|----------|-----------|-------|
| job_title | position_name | job_applications |
| school | school_name | resume_education |
| achievements | courses | resume_education |
| situation | background | project_experiences |
| title | experience_name | management_experiences |
| education | (use resume_education table) | resumes |
| certifications | (use resume_certifications table) | resumes |

### Management Experience Calculation

| ❌ WRONG SOURCE | ✅ CORRECT SOURCE |
|-----------------|-------------------|
| Count entries in management_experiences | Calculate from resume_experiences.position dates |
| Use management_experiences.start_date | Use resume_experiences WHERE position contains "Manager", "Lead", etc. |

## Best Practices

1. **Always DESCRIBE before SELECT**
   ```sql
   DESCRIBE table_name;
   -- Then write your SELECT based on actual columns
   ```

2. **Check for NULL-able fields**
   - Use `COALESCE(end_date, CURRENT_DATE)` for current positions
   - Check `is_current` flag when calculating date ranges

3. **Handle JSON fields properly**
   - `tech_tags`, `project_ids`, `other_links` are JSON
   - Use `JSON_EXTRACT()` or parse in application code

4. **Date calculations**
   - All dates are `date` type (not datetime)
   - Use `DATEDIFF()` for duration calculations
   - Convert to years: `DATEDIFF(end, start) / 365`

5. **Text field lengths**
   - `text` fields: Unlimited length (use for descriptions, responsibilities)
   - `varchar(255)`: Names, titles, URLs
   - `varchar(50)`: Codes, categories, statuses

## Troubleshooting

### "Unknown column" Errors
1. Run `DESCRIBE table_name` to see actual columns
2. Check this reference guide for common mistakes
3. Verify field names haven't changed in schema

### Incorrect Management Years
1. Verify you're querying `resume_experiences` (NOT `management_experiences`)
2. Check position field contains management keywords
3. Verify date range calculations include `is_current` flag

### Missing Data
1. Check foreign key relationships (resume_id, user_id)
2. Verify `is_default = 1` for resumes
3. Check `sort_order` for ordering
