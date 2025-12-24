-- 删除多余的question_text字段，统一使用question_description
-- question_description用于存储题目描述（Markdown格式）

ALTER TABLE questions DROP COLUMN question_text;
