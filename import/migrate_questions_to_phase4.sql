-- =====================================================
-- 将questions表从Phase 3迁移到Phase 4结构
-- =====================================================
-- 主要变更:
-- 1. question_text 拆分为 title + question_description
-- 2. 保持其他字段不变
-- 3. 不添加 stage_id (按用户要求)
-- =====================================================

USE growing;

-- Step 1: 添加新字段
ALTER TABLE questions 
ADD COLUMN title VARCHAR(500) AFTER focus_area_id,
ADD COLUMN question_description TEXT AFTER title;

-- Step 2: 迁移现有数据 (将question_text内容暂时放到question_description)
UPDATE questions 
SET 
  title = CONCAT('[', id, '] Question'),
  question_description = question_text;

-- Step 3: 将title设为NOT NULL
ALTER TABLE questions 
MODIFY COLUMN title VARCHAR(500) NOT NULL;

-- Step 4: 验证数据迁移
SELECT 
  id, 
  LEFT(title, 50) as title, 
  LEFT(question_description, 50) as description
FROM questions 
LIMIT 5;

-- =====================================================
-- 迁移完成提示
-- =====================================================
-- 下一步操作:
-- 1. 手动编辑questions记录，将question_description拆分为合适的title
-- 2. 如果确认不再需要question_text字段，可以执行:
--    ALTER TABLE questions DROP COLUMN question_text;
-- 3. 导入新数据使用 import_sample_10_questions.sql
-- =====================================================
