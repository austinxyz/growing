# Generate AI Answer Skill

Automatically generate AI reference answers for questions based on their skill's answer templates.

## Usage

This skill will:
1. Query the database to get question details (description, requirements, skill)
2. Fetch the associated answer template from skill_templates/answer_templates
3. Generate AI answer using the template framework (e.g., STAR for Behavioral)
4. Insert the answer into user_question_notes table with user_id=-1

## Example Usage

```bash
# Preview mode (default) - saves to note/ai/ directory
claude: generate AI answer for question 204
# or explicitly
./.claude/skills/generate-ai-answer/run.sh 204 500 preview

# Run mode - saves to database (with overwrite confirmation)
./.claude/skills/generate-ai-answer/run.sh 204 500 run

# Generate shorter AI answer (300 words, ~3 minutes)
./.claude/skills/generate-ai-answer/run.sh 204 300 preview
```

## Modes

### Preview Mode (default)
- **Purpose**: Generate and review AI answer before committing to database
- **Output**: Saves to `note/ai/question_{id}_ai_answer.md`
- **Use case**: Iterating on answer quality, experimenting with different prompts
- **No database changes**: Safe to run multiple times

### Run Mode
- **Purpose**: Insert AI answer into production database
- **Output**: Saves to `user_question_notes` table with `user_id = -1`
- **Safety check**: If answer exists, prompts for confirmation before overwrite
- **Use case**: Final answer ready for production use

## How It Works

### 1. Database Queries

```sql
-- Get question details
SELECT q.id, q.title, q.question_description, q.answer_requirement,
       q.focus_area_id, fa.skill_id
FROM questions q
JOIN focus_areas fa ON q.focus_area_id = fa.id
WHERE q.id = ?;

-- Get skill's answer template
SELECT at.template_name, at.template_fields
FROM skill_templates st
JOIN answer_templates at ON st.template_id = at.id
WHERE st.skill_id = ? AND st.is_default = 1;
```

### 2. Template Structure Examples

**STAR Template** (for Behavioral questions):
```json
[
  {"key": "situation", "label": "Situation (情境)"},
  {"key": "task", "label": "Task (任务)"},
  {"key": "action", "label": "Action (行动)"},
  {"key": "result", "label": "Result (结果)"}
]
```

**Technical Template** (for Technical questions):
```json
[
  {"key": "core_concept", "label": "核心概念"},
  {"key": "implementation", "label": "实现方式"},
  {"key": "tradeoffs", "label": "权衡考虑"}
]
```

### 3. AI Prompt Construction

The skill constructs a prompt like:
```
You are generating a concise interview answer (max {max_words} words, ~5 minutes).

Question: {question_title}
Description: {question_description}
Requirements: {answer_requirement}

Answer using {template_name} framework:
{template_fields}

Guidelines:
- Keep each section concise and focused
- Use specific examples with metrics/results
- Target length: ~{max_words} words total
- Format in Markdown with clear section headers
```

### 4. Database Insert

```sql
INSERT INTO user_question_notes (question_id, user_id, note_content)
VALUES (?, -1, ?)
ON DUPLICATE KEY UPDATE
  note_content = VALUES(note_content),
  updated_at = CURRENT_TIMESTAMP;
```

## Parameters

- **question_id** (required): The question ID to generate answer for
- **max_words** (optional): Maximum words for the answer
  - Default: 500 words (~5 minutes)
  - Shorter: 300 words (~3 minutes)
  - Note: Programming questions may exceed limit due to code
- **mode** (optional): preview (default) or run
  - preview: Save to note/ai/ directory
  - run: Insert into database (with overwrite confirmation)

## Output

The skill will:
1. Display the question title and template being used
2. Show the generated AI answer
3. Confirm successful insertion into database
4. Provide preview of the stored answer

## Error Handling

- **Question not found**: Error if question_id doesn't exist
- **No template found**: Error if skill has no associated template
- **Database error**: Show detailed error message
- **LLM error**: Retry once, then show error

## Dependencies

- MySQL database with proper credentials in `backend/.env`
- Access to LLM API for answer generation
- Tables: questions, focus_areas, skills, skill_templates, answer_templates, user_question_notes

## Quality Control

The skill applies different quality standards based on question type:

### Behavioral Questions
- **Length**: ~500 Chinese characters
- **Must include**: Realistic challenges, learning process, growth mindset
- **Avoid**: Unrealistic timelines (3 weeks to master new tech), perfect outcomes
- **Task difficulty matching**:
  - Learning new tech → Internal tool (low risk)
  - Crisis handling/Leadership → Core system OK (shows impact)
  - Process improvement → Core system OK (shows initiative)

### Technical Questions
- **Length**: ~500 Chinese characters
- **Focus**: Direct technical answer (no forced failure stories)
- **Structure**: Core concept → Implementation → Trade-offs
- **Depth**: Match question difficulty (Easy/Medium/Hard)

### Programming Questions
- **Length**: Can exceed 500 chars (code examples included)
- **Must include**: Problem analysis, clean code, complexity analysis
- **Depth**: Match question difficulty (Easy=1 solution, Hard=optimized+edge cases)

### Design Questions
- **Length**: ~500 Chinese characters
- **Focus**: Requirements → Design decisions → Trade-offs
- **Scope**: Match question difficulty (Easy=basic, Hard=comprehensive)

## Notes

- AI answers are stored with `user_id = -1` to distinguish from user-created notes
- Existing AI answers will be updated (ON DUPLICATE KEY UPDATE)
- The skill automatically selects the default template for the question's skill
- If no template is found, generates a generic structured answer
- Quality checklist is displayed after skill execution for manual verification
