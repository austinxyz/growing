-- =====================================================
-- Validation Script for Migration V7
-- Purpose: Verify tables and data after V7 migration
-- =====================================================

-- 1. Check if new tables exist
SELECT 'Checking new tables...' AS status;

SELECT TABLE_NAME, CREATE_TIME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'growing'
AND TABLE_NAME IN (
    'learning_stages',
    'learning_contents',
    'programming_question_details',
    'major_categories',
    'focus_area_categories'
);

-- 2. Check learning_stages initial data
SELECT '--- Learning Stages (should have 3 rows) ---' AS status;
SELECT * FROM learning_stages ORDER BY sort_order;

-- 3. Check major_categories initial data
SELECT '--- Major Categories (should have 4 rows) ---' AS status;
SELECT * FROM major_categories ORDER BY sort_order;

-- 4. Check if user_question_notes has core_strategy column
SELECT '--- user_question_notes columns ---' AS status;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'growing'
AND TABLE_NAME = 'user_question_notes'
AND COLUMN_NAME = 'core_strategy';

-- 5. Check learning_contents table structure
SELECT '--- learning_contents columns ---' AS status;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'growing'
AND TABLE_NAME = 'learning_contents'
ORDER BY ORDINAL_POSITION;

-- 6. Check programming_question_details table structure
SELECT '--- programming_question_details columns ---' AS status;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'growing'
AND TABLE_NAME = 'programming_question_details'
ORDER BY ORDINAL_POSITION;

SELECT 'Validation complete!' AS status;
