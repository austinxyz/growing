---
name: job-prepare
description: AI-powered interview preparation planner. Analyzes job requirements and user's resume/experiences to create detailed interview preparation plan with skill focus areas, key projects, and management experiences to prepare.
---

# AI Interview Preparation Planner

Intelligently analyze job requirements and create comprehensive interview preparation plan with specific skills, focus areas, projects, and management experiences to prepare.

## Usage

The skill accepts:
- `job_id`: ID of the job application (from job_applications table)
- `user_id`: ID of the user
- `resume_id`: (optional) Specific resume ID, defaults to user's default resume

Example invocations:
- "Prepare for interview job 13 user 3"
- "Create interview prep plan for job_id=13, user_id=3"
- "Generate preparation checklist for Austin's job application 13"

## Implementation Steps

When this skill is invoked:

### 1. Load Database Credentials
```bash
source backend/.env
MYSQL_CLIENT=$(brew --prefix mysql-client)/bin/mysql
```

### 2. Query Job Description
```sql
-- Get job details
SELECT id, company_id, position_name, position_level, qualifications, responsibilities
FROM job_applications
WHERE id = {job_id};
```

### 3. Query Resume and User Experiences

```sql
-- Step 1: Get resume (use specified resume_id or default)
SELECT id, user_id, about, career_objective
FROM resumes
WHERE (id = {resume_id} OR (user_id = {user_id} AND is_default = 1))
LIMIT 1;

-- Step 2: Get education background
SELECT school_name, degree, major, start_date, end_date, gpa, courses
FROM resume_education
WHERE resume_id = {resume_id}
ORDER BY sort_order;

-- Step 3: Get work experience
SELECT company_name, position, location, start_date, end_date, is_current,
       responsibilities, achievements
FROM resume_experiences
WHERE resume_id = {resume_id}
ORDER BY start_date DESC;

-- Step 4: Get ALL project experiences (with full details)
SELECT id, project_name, project_type, what_description, my_contribution,
       background, problem_statement, challenges, my_role,
       tech_stack, architecture, innovation,
       quantitative_results, business_impact, personal_growth,
       start_date, end_date, tech_tags
FROM project_experiences
WHERE user_id = {user_id}
ORDER BY start_date DESC;

-- Step 5: Get ALL management experiences (with full details)
SELECT id, focus_area_id, experience_name, team_growth_subtype,
       background, actions_taken, results, lessons_learned,
       start_date, end_date
FROM management_experiences
WHERE user_id = {user_id}
ORDER BY start_date DESC;

-- Step 6: Get available skills and focus areas (for recommendation)
SELECT s.id as skill_id, s.name as skill_name,
       f.id as focus_area_id, f.name as focus_area_name
FROM skills s
LEFT JOIN focus_areas f ON f.skill_id = s.id
ORDER BY s.id, f.id;
```

### 4. Check Existing Interview Stages and Checklists

```sql
-- Check if user has already defined interview stages for this job
SELECT id, stage_name, stage_order, skill_ids, focus_area_ids, preparation_notes
FROM interview_stages
WHERE job_application_id = {job_id}
ORDER BY stage_order;

-- For each existing stage, get current TODO items (to avoid duplicates)
-- Note: AI-generated items have source='AI', user-created items have source='User'
SELECT interview_stage_id, title, priority, todo_type, description
FROM interview_preparation_todos
WHERE interview_stage_id IN ({stage_ids})
ORDER BY interview_stage_id, sort_order;
```

### 5. Perform AI Analysis

Use Claude's semantic understanding to analyze and recommend:

**A. Required Skills & Focus Areas (Per Stage)**

For EACH existing interview stage (or create default if none exist):

1. **Match stage skills to JD requirements**:
   - If stage has `skill_ids`, analyze THOSE skills in context of JD
   - Example: Stage has skill_id=3 (People Management) → analyze JD for leadership/management requirements

2. **Recommend focus areas for each stage skill**:
   - Query available focus_areas for each skill_id
   - Use JD keywords to match relevant focus areas
   - Example: JD mentions "hiring" + "coaching" → recommend focus_area_ids [33 (Hiring), 27 (Coaching)]

3. **If stage has NO skills, recommend skills + focus areas from JD**:
   - Analyze JD to identify needed skills
   - Then recommend focus areas for those skills

Output format (per stage):
```json
{
  "stageId": 5,
  "stageName": "Behavioral Round",
  "existingSkillIds": [3, 9],  // From interview_stages.skill_ids
  "recommendedFocusAreas": [
    {
      "skillId": 3,
      "skillName": "People Management",
      "existingFocusAreaIds": [33, 88],  // Already in stage
      "newFocusAreaIds": [27, 95],       // Recommended to add
      "focusAreaNames": ["Coaching", "Conflict Resolution"],
      "reason": "JD emphasizes 'coaching junior engineers' and 'resolving team conflicts'"
    },
    {
      "skillId": 9,
      "skillName": "Communication",
      "existingFocusAreaIds": [],
      "newFocusAreaIds": [101, 102],
      "focusAreaNames": ["Written Communication", "Presentation Skills"],
      "reason": "JD requires 'technical writing' and 'presenting to leadership'"
    }
  ]
}
```

**B. Relevant Project Experiences**

Match user's projects to JD requirements:
1. **Semantic matching** - understand that "K8s operator" relates to "platform engineering"
2. **Identify key projects** that demonstrate required skills
3. **Rank by relevance** - which projects are most important to prepare?
4. **Provide preparation tips** for each project

Output format:
```json
{
  "projectId": 2,
  "projectName": "Cloud Native Migration",
  "relevanceScore": 95,
  "matchedSkills": ["Kubernetes", "Distributed Systems"],
  "preparationTips": [
    "Be ready to explain K8s operator architecture in detail",
    "Prepare specific metrics: how many services migrated, performance improvements",
    "Highlight observability dashboard implementation"
  ]
}
```

**C. Relevant Management Experiences**

Match user's management experiences to JD's leadership requirements:
1. **Identify coaching/leadership stories** relevant to this role
2. **Match to focus areas** mentioned in JD
3. **Rank by relevance**
4. **Provide preparation tips** using STARL framework

Output format:
```json
{
  "experienceId": 1,
  "experienceName": "Cross-team API Standardization",
  "relevanceScore": 90,
  "matchedFocusAreas": ["Technical Leadership", "Communication"],
  "preparationTips": [
    "Prepare STARL narrative: Situation → Task → Action → Result → Learned",
    "Quantify impact: how many teams affected, bug reduction percentage",
    "Emphasize stakeholder management and communication"
  ]
}
```

### 6. Create or Update Interview Stages

**Case 1: No existing stages (create default stage)**

```sql
-- Create default "Overall Preparation" stage
INSERT INTO interview_stages (
  job_application_id, stage_name, stage_order,
  skill_ids, focus_area_ids, preparation_notes
) VALUES (
  {job_id},
  'Overall Preparation',
  1,
  JSON_ARRAY({skill_ids}),
  JSON_ARRAY({focus_area_ids}),
  {ai_generated_notes}
);

-- Get the new stage_id
SET @stage_id = LAST_INSERT_ID();

-- Add TODO items (AI-generated with source='AI')
INSERT INTO interview_preparation_todos (
  interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, is_completed
) VALUES
  (@stage_id, NULL, 'Project: {project_name}', '{preparation_tips}', 'Project', 'AI', 4, {order}, 0),
  (@stage_id, NULL, 'Management: {experience_name}', '{preparation_tips}', 'Management', 'AI', 4, {order}, 0),
  (@stage_id, NULL, 'Skill: {skill_name} - {focus_area_name}', '{reason}', 'Skill', 'AI', 2, {order}, 0);
```

**Case 2: Existing stages (smart distribution with deduplication)**

Analyze existing stage names and distribute recommendations:
- "Phone Screen" / "Recruiter" → Usually behavioral questions + basic technical
  - Add: High-priority management experiences + general skills
- "Technical" / "Coding" → Deep technical skills
  - Add: Relevant projects + technical focus areas
- "System Design" → Architecture and design skills
  - Add: System design projects + scalability focus areas
- "Behavioral" / "Leadership" → Management and soft skills
  - Add: Management experiences + leadership focus areas
- "Onsite" / "Final" → Comprehensive
  - Add: All remaining items

**CRITICAL: Deduplication Logic**

Before adding ANY checklist items to a stage:

1. **Load existing checklist items** from step 4
2. **Compare new items against existing** using these rules:
   - Match by `title` text (case-insensitive, trim whitespace)
   - For projects: match if both titles contain same project_name
   - For management: match if both titles contain same experience_name
   - For skills/focus areas: match if both titles mention same skill_name AND focus_area_name

3. **Skip duplicates** - if item already exists:
   - Do NOT insert it again
   - Do NOT update existing item (preserve user's manual edits)
   - Log skipped item in output

4. **Add only new items**:
   - Items that don't match any existing checklist_item
   - Set appropriate `sort_order` (max existing sort_order + 1)

Example deduplication check:
```javascript
// Existing checklist items (from step 4)
const existingItems = [
  "Project: Cloud Migration (user added manually)",
  "Management: API Standardization",
  "Skill: System Design - Distributed Systems"
];

// New AI recommendation
const newItem = "Project: Cloud Migration";

// Match check (case-insensitive substring match)
const isDuplicate = existingItems.some(item =>
  item.toLowerCase().includes("cloud migration")
);

if (isDuplicate) {
  console.log("Skipping duplicate:", newItem);
} else {
  // Insert new item
}
```

```sql
-- Step 1: Get max order_index for this stage
SELECT COALESCE(MAX(order_index), 0) as max_order
FROM interview_preparation_todos
WHERE interview_stage_id = {stage_id};

-- Step 2: Update stage with NEW focus areas only (avoid duplicates in JSON array)
UPDATE interview_stages
SET
  focus_area_ids = JSON_MERGE_PRESERVE(
    focus_area_ids,
    JSON_ARRAY({new_focus_area_ids_not_already_in_array})
  ),
  preparation_notes = CONCAT(IFNULL(preparation_notes, ''), '\n\n## AI Recommendations\n\n', {ai_notes})
WHERE id = {stage_id};

-- Step 3: Insert ONLY non-duplicate TODO items (AI-generated with source='AI')
-- (After deduplication check in code)
-- user_id is NULL because it's AI-generated (user determined via interview_stage_id chain)
INSERT INTO interview_preparation_todos (
  interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, is_completed
) VALUES
  ({stage_id}, NULL, {new_item_title_1}, {notes_1}, {category_1}, 'AI', {priority_1}, {max_order + 1}, 0),
  ({stage_id}, NULL, {new_item_title_2}, {notes_2}, {category_2}, 'AI', {priority_2}, {max_order + 2}, 0);
  -- Only items that passed deduplication check
```

### 7. Return JSON Result

Output **VALID JSON** matching this exact structure:

```json
{
  "jobApplicationId": 13,
  "resumeId": 2,
  "hasExistingStages": true,
  "stagesCreated": [],
  "stagesUpdated": [
    {
      "stageId": 5,
      "stageName": "Behavioral Round",
      "stageOrder": 1,
      "existingSkillIds": [3, 9],
      "newFocusAreaIds": [27, 95, 101, 102],
      "checklistItemsAdded": 8,
      "checklistItemsSkipped": 3,
      "skippedReasons": [
        "Project: Cloud Migration - already exists",
        "Management: API Standardization - already exists",
        "Skill: System Design - Distributed Systems - already exists"
      ]
    },
    {
      "stageId": 6,
      "stageName": "Technical Round",
      "stageOrder": 2,
      "existingSkillIds": [7],
      "newFocusAreaIds": [89, 90, 31],
      "checklistItemsAdded": 5,
      "checklistItemsSkipped": 0,
      "skippedReasons": []
    }
  ],
  "recommendations": {
    "perStage": [
      {
        "stageId": 5,
        "stageName": "Behavioral Round",
        "skillFocusAreas": [
          {
            "skillId": 3,
            "skillName": "People Management",
            "existingFocusAreaIds": [33, 88],
            "newFocusAreaIds": [27, 95],
            "newFocusAreaNames": ["Coaching", "Conflict Resolution"],
            "reason": "JD emphasizes 'coaching junior engineers' and 'resolving team conflicts'"
          },
          {
            "skillId": 9,
            "skillName": "Communication",
            "existingFocusAreaIds": [],
            "newFocusAreaIds": [101, 102],
            "newFocusAreaNames": ["Written Communication", "Presentation Skills"],
            "reason": "JD requires 'technical writing' and 'presenting to leadership'"
          }
        ]
      },
      {
        "stageId": 6,
        "stageName": "Technical Round",
        "skillFocusAreas": [
          {
            "skillId": 7,
            "skillName": "System Design",
            "existingFocusAreaIds": [],
            "newFocusAreaIds": [89, 90, 31],
            "newFocusAreaNames": ["Distributed Systems", "Scalability", "Data Modeling"],
            "reason": "JD emphasizes 'platform engineering' and 'distributed systems design'"
          }
        ]
      }
    ],
    "projects": [
      {
        "projectId": 2,
        "projectName": "Cloud Native Migration",
        "relevanceScore": 95,
        "matchedSkills": ["Kubernetes", "Distributed Systems", "Platform Engineering"],
        "preparationTips": [
          "Be ready to explain K8s operator architecture in detail",
          "Prepare specific metrics: how many services migrated? What were the performance improvements?",
          "Highlight observability dashboard implementation",
          "Practice explaining technical tradeoffs and design decisions"
        ],
        "starlFramework": {
          "situation": "Multiple teams needed to migrate to cloud-native platform",
          "task": "Design and implement migration strategy with zero downtime",
          "action": "Built K8s operators, automated migration tools, created observability dashboard",
          "result": "Successfully migrated X services, improved performance by Y%",
          "learned": "Importance of incremental migration and comprehensive monitoring"
        }
      }
    ],
    "managementExperiences": [
      {
        "experienceId": 1,
        "experienceName": "Cross-team API Standardization",
        "relevanceScore": 90,
        "matchedFocusAreas": ["Technical Leadership", "Communication", "Influence"],
        "preparationTips": [
          "Prepare full STARL narrative with specific metrics",
          "Emphasize stakeholder management across multiple teams",
          "Highlight how you built consensus and drove adoption",
          "Quantify impact: bug reduction, integration time saved"
        ],
        "keyMetrics": [
          "5 teams involved",
          "70% reduction in integration bugs",
          "API design guidelines adopted company-wide"
        ]
      }
    ]
  },
  "preparationSummary": {
    "totalSkills": 3,
    "totalFocusAreas": 6,
    "priorityProjects": 2,
    "priorityManagementExperiences": 1,
    "estimatedPrepTime": "8-12 hours",
    "nextSteps": [
      "Review all focus areas for People Management skill",
      "Practice STARL narratives for top 2 projects",
      "Prepare metrics and quantifiable results for management experiences",
      "Review system design basics (distributed systems, scalability)"
    ]
  }
}
```

## Stage Name Mapping Rules

When distributing recommendations to existing stages, use these rules:

### Phone Screen / Recruiter Screen
- **Content**: Behavioral questions + resume walkthrough + basic technical
- **Add**:
  - High-priority management experiences (1-2 stories)
  - Career overview talking points
  - General technical skills (not deep dive)

### Technical Interview / Coding Round
- **Content**: Coding problems + technical deep dive
- **Add**:
  - Technical projects with implementation details
  - Technical focus areas (algorithms, data structures, coding patterns)
  - Technical skills matching JD

### System Design Round
- **Content**: Architecture design + scalability discussion
- **Add**:
  - System design projects
  - Distributed systems / scalability focus areas
  - Platform engineering experiences

### Behavioral / Leadership Round
- **Content**: STARL stories + leadership style + conflict resolution
- **Add**:
  - All management experiences
  - Leadership/coaching focus areas
  - Soft skills demonstrations

### Onsite / Final Round
- **Content**: Comprehensive assessment across all areas
- **Add**:
  - Remaining projects and experiences
  - All focus areas not covered in other rounds
  - Culture fit and motivation talking points

## Critical Requirements

1. **Smart Stage Detection** - Understand stage intent from name:
   - Use keyword matching: "phone", "screen", "technical", "design", "behavioral", "onsite"
   - Handle variations: "Tech Screen" = "Technical Interview"

2. **Avoid Duplicates** - Don't add the same skill/project to multiple stages:
   - Track what's been added across all stages
   - Only add each item to the most relevant stage

3. **Prioritization** - Mark highest-priority items:
   - `is_priority = 1` for top 20% of recommendations
   - Sort by relevance score

4. **Semantic Matching** - Understand JD language:
   - "Platform engineering" → Kubernetes, distributed systems, infrastructure
   - "Team leadership" → coaching, hiring, performance management
   - "Full stack" → frontend + backend projects

5. **Valid JSON** - Output must be parseable JSON:
   - Use double quotes for strings
   - Escape special characters
   - No trailing commas
   - Valid boolean/number types

## Error Handling

- If job_id not found: Return error message
- If user has no resume: Return error message
- If user has no projects/management experiences: Still create stages with skill recommendations
- If database query fails: Return error with details
- If unable to generate analysis: Explain what went wrong

## Database Connection

- **Host**: 10.0.0.7:37719
- **Database**: growing
- **Credentials**: From `backend/.env` (DB_USER, DB_PASSWORD)
- **MySQL Client**: Use Homebrew installation path

## Output Format

Return **ONLY** the JSON analysis result. No additional commentary or explanation outside the JSON structure.

The result will be used to:
1. Display preparation plan in the UI
2. Populate interview stages and checklists in the database
3. Guide user's interview preparation workflow
