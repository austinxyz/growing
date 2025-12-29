#!/bin/bash

# Generate AI Answer Skill
# Automatically generates AI reference answers for questions based on skill templates

set -e

# Parse arguments
QUESTION_ID=""
MAX_WORDS=500
MODE="preview"  # preview or run

# Parse skill arguments
if [ ! -z "$1" ]; then
    QUESTION_ID="$1"
fi

if [ ! -z "$2" ]; then
    MAX_WORDS="$2"
fi

if [ ! -z "$3" ]; then
    MODE="$3"
fi

# Validate required arguments
if [ -z "$QUESTION_ID" ]; then
    echo "Error: question_id is required"
    echo "Usage: generate-ai-answer <question_id> [max_words] [mode]"
    echo "  mode: preview (default) or run"
    exit 1
fi

echo "🤖 Generating AI answer for question ID: $QUESTION_ID"
echo "📏 Max words: $MAX_WORDS"
echo "🎯 Mode: $MODE"
echo ""

# Load database credentials
if [ ! -f "backend/.env" ]; then
    echo "Error: backend/.env not found"
    exit 1
fi

source backend/.env

# MySQL client
MYSQL_CLIENT=$(brew --prefix mysql-client)/bin/mysql
MYSQL_CMD="$MYSQL_CLIENT -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME"

# Step 1: Get question details
echo "📖 Fetching question details..."
QUESTION_DATA=$($MYSQL_CMD -e "
SELECT
    q.id,
    q.title,
    q.question_description,
    q.answer_requirement,
    q.question_type,
    fa.skill_id,
    s.name as skill_name
FROM questions q
JOIN focus_areas fa ON q.focus_area_id = fa.id
JOIN skills s ON fa.skill_id = s.id
WHERE q.id = $QUESTION_ID
" 2>/dev/null | tail -n +2)

if [ -z "$QUESTION_DATA" ]; then
    echo "❌ Question ID $QUESTION_ID not found"
    exit 1
fi

# Parse question data
QUESTION_TITLE=$(echo "$QUESTION_DATA" | cut -f2)
QUESTION_DESC=$(echo "$QUESTION_DATA" | cut -f3)
ANSWER_REQ=$(echo "$QUESTION_DATA" | cut -f4)
SKILL_ID=$(echo "$QUESTION_DATA" | cut -f6)
SKILL_NAME=$(echo "$QUESTION_DATA" | cut -f7)

echo "✅ Question: $QUESTION_TITLE"
echo "🏷️  Skill: $SKILL_NAME (ID: $SKILL_ID)"
echo ""

# Step 2: Get answer template
echo "📋 Fetching answer template..."
TEMPLATE_DATA=$($MYSQL_CMD -e "
SELECT
    at.template_name,
    at.template_fields
FROM skill_templates st
JOIN answer_templates at ON st.template_id = at.id
WHERE st.skill_id = $SKILL_ID AND st.is_default = 1
LIMIT 1
" 2>/dev/null | tail -n +2)

if [ -z "$TEMPLATE_DATA" ]; then
    echo "⚠️  No template found for skill $SKILL_NAME, using generic structure"
    TEMPLATE_NAME="Generic"
    TEMPLATE_FIELDS='[]'
else
    TEMPLATE_NAME=$(echo "$TEMPLATE_DATA" | cut -f1)
    TEMPLATE_FIELDS=$(echo "$TEMPLATE_DATA" | cut -f2)
    echo "✅ Using template: $TEMPLATE_NAME"
fi
echo ""

# Step 3: Create prompt file for AI generation
PROMPT_FILE="/tmp/ai_answer_prompt_${QUESTION_ID}.txt"
cat > "$PROMPT_FILE" <<EOF
You are generating a concise, high-quality interview answer for a ${SKILL_NAME} question.

**Target**: ${MAX_WORDS} words (~5 minutes speaking time)

**Question Title**:
${QUESTION_TITLE}

**Question Description**:
${QUESTION_DESC}

**Answer Requirements**:
${ANSWER_REQ}

**Answer Framework**: ${TEMPLATE_NAME}
EOF

# Add template structure if available
if [ "$TEMPLATE_NAME" != "Generic" ]; then
    echo "" >> "$PROMPT_FILE"
    echo "**Template Structure**:" >> "$PROMPT_FILE"
    echo "$TEMPLATE_FIELDS" | python3 -c "
import sys, json
try:
    fields = json.load(sys.stdin)
    for field in fields:
        print(f\"- **{field['label']}**: {field.get('placeholder', 'Provide relevant content')}\" )
except:
    pass
" >> "$PROMPT_FILE"
fi

cat >> "$PROMPT_FILE" <<EOF

**Guidelines**:
1. Follow the ${TEMPLATE_NAME} framework structure with clear markdown headers
2. Use specific, realistic examples with metrics/results
3. Keep language professional and concise
4. Format with markdown (## for main sections, ### for subsections)
5. Generate in Chinese for Chinese sections, English for technical terms and code examples

**Answer Quality Control**:

EOF

# Get question type for specific guidelines
QUESTION_TYPE=$(echo "$QUESTION_DATA" | cut -f5)

# Add question-type specific guidelines
cat >> "$PROMPT_FILE" <<EOF
Question Type: ${QUESTION_TYPE}

EOF

if [ "$QUESTION_TYPE" = "behavioral" ]; then
    cat >> "$PROMPT_FILE" <<EOF
**Behavioral Question Guidelines**:
- ✅ Length limit: ~500 Chinese characters (non-programming)
- ✅ Include realistic challenges and learning process
- ✅ Show growth mindset (what you learned from difficulties)
- ❌ Avoid: Unrealistic timelines (e.g., 3 weeks to master new tech from scratch)
- ❌ Avoid: Perfect outcomes (zero incidents, 50%+ improvement without context)
- ✅ Admit limitations: "still learning", "needed help", "made mistakes"

**Task Difficulty Matching**:
- Learning new tech → Start with internal tool/non-critical system
- Crisis handling/Leadership → Core system is appropriate (shows impact)
- Process improvement → Can be core system (shows initiative)
- Team conflict → Team size and project criticality should match seniority

**STAR Structure Length Guide**:
- Situation: ~80 chars (context, what was the challenge)
- Task: ~60 chars (clear goal, realistic for the scenario)
- Action: ~200 chars (specific steps, include failures/pivots)
- Result: ~160 chars (realistic outcomes, what you learned)
EOF
elif [ "$QUESTION_TYPE" = "technical" ]; then
    cat >> "$PROMPT_FILE" <<EOF
**Technical Question Guidelines**:
- ✅ Length limit: ~500 Chinese characters (non-programming)
- ✅ Direct technical answer (no need to force "failure stories")
- ✅ Balance: core concept → implementation → trade-offs
- ✅ Use code snippets when helpful (keep them concise)
- ✅ Match answer depth to question difficulty:
  * Easy: straightforward explanation with 1 example
  * Medium: compare 2 approaches with pros/cons
  * Hard: deep dive with edge cases and optimization strategies
EOF
elif [ "$QUESTION_TYPE" = "programming" ]; then
    cat >> "$PROMPT_FILE" <<EOF
**Programming Question Guidelines**:
- ✅ Length: Can exceed 500 chars due to code examples
- ✅ Include: problem analysis, solution approach, clean code, complexity analysis
- ✅ Code style: concise, well-commented, production-ready
- ✅ Explain key algorithmic insights (not just code dump)
- ✅ Match complexity to question difficulty:
  * Easy: single optimal solution with clear explanation
  * Medium: multiple solutions with trade-off analysis
  * Hard: optimized solution with edge case handling
EOF
else
    cat >> "$PROMPT_FILE" <<EOF
**Design Question Guidelines**:
- ✅ Length limit: ~500 Chinese characters
- ✅ Focus: requirements → design decisions → trade-offs
- ✅ Show thought process (why this choice over alternatives)
- ✅ Include realistic constraints (scale, budget, team size)
- ✅ Match scope to question difficulty:
  * Easy: basic design with 2-3 components
  * Medium: detailed design with data flow and API
  * Hard: comprehensive design with scaling/reliability strategies
EOF
fi

cat >> "$PROMPT_FILE" <<EOF

**Target Length**: ~${MAX_WORDS} words
EOF

echo "📝 Prompt created at: $PROMPT_FILE"
echo ""

# Check if answer already exists (for run mode)
if [ "$MODE" = "run" ]; then
    EXISTING_ANSWER=$($MYSQL_CMD -e "
    SELECT COUNT(*) as count
    FROM user_question_notes
    WHERE question_id = $QUESTION_ID AND user_id = -1
    " 2>/dev/null | tail -n +2)

    if [ "$EXISTING_ANSWER" != "0" ]; then
        echo "⚠️  AI answer already exists for question $QUESTION_ID"
        echo "❓ Do you want to overwrite it? (yes/no)"
        echo ""
        echo "EXISTING_ANSWER_FOUND=true"
        echo "PROMPT_FILE_PATH=$PROMPT_FILE"
        echo "QUESTION_ID=$QUESTION_ID"
        echo "TEMPLATE_NAME=$TEMPLATE_NAME"
        echo "MODE=$MODE"
        exit 0
    fi
fi

# Create note/ai directory if it doesn't exist
mkdir -p note/ai

# Output file path
if [ "$MODE" = "preview" ]; then
    OUTPUT_FILE="note/ai/question_${QUESTION_ID}_ai_answer.md"
    echo "📄 Preview mode: Answer will be saved to $OUTPUT_FILE"
else
    OUTPUT_FILE="/tmp/ai_answer_${QUESTION_ID}.md"
    echo "💾 Run mode: Answer will be inserted into database"
fi

echo ""
echo "💭 Ready to generate AI answer..."
echo ""
echo "PROMPT_FILE_PATH=$PROMPT_FILE"
echo "OUTPUT_FILE_PATH=$OUTPUT_FILE"
echo "QUESTION_ID=$QUESTION_ID"
echo "TEMPLATE_NAME=$TEMPLATE_NAME"
echo "MODE=$MODE"
echo ""
echo "✅ Skill preparation complete!"
echo ""
echo "📌 Next steps for Claude:"
echo "1. Read the prompt file: $PROMPT_FILE"
echo "2. Generate AI answer based on the prompt (target: $MAX_WORDS words)"
echo "3. Save answer to: $OUTPUT_FILE"
if [ "$MODE" = "run" ]; then
    echo "4. Insert into database with:"
    echo "   INSERT INTO user_question_notes (question_id, user_id, note_content)"
    echo "   SELECT $QUESTION_ID, -1, content FROM file"
    echo "   ON DUPLICATE KEY UPDATE note_content = VALUES(note_content);"
fi

echo ""
echo "📋 Answer Quality Checklist (for question type: ${QUESTION_TYPE}):"
if [ "$QUESTION_TYPE" = "behavioral" ]; then
    echo "  [ ] Length < 500 Chinese chars?"
    echo "  [ ] Includes realistic challenges/failures?"
    echo "  [ ] Task difficulty matches context (not too ambitious)?"
    echo "  [ ] Avoids perfect outcomes (zero issues, 50%+ improvements)?"
    echo "  [ ] Shows growth mindset (what learned from difficulties)?"
elif [ "$QUESTION_TYPE" = "programming" ]; then
    echo "  [ ] Code is concise and well-commented?"
    echo "  [ ] Includes complexity analysis?"
    echo "  [ ] Explains key algorithmic insights?"
    echo "  [ ] Solution depth matches question difficulty?"
else
    echo "  [ ] Length < 500 Chinese chars?"
    echo "  [ ] Answer depth matches question difficulty?"
    echo "  [ ] Focuses on core technical content?"
fi
