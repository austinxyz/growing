-- =====================================================
-- Phase 7: 合并 interview_preparation_checklist 和 interview_preparation_todos
-- =====================================================
-- 说明:
-- 1. AI生成的清单项也应该可以直接标记完成
-- 2. AI生成的清单项也需要关联资源
-- 3. 添加source字段区分AI生成还是用户创建
-- 4. 用户通过 interview_stages → job_applications → user_id 间接绑定
-- =====================================================

-- Step 1: 修改 interview_preparation_todos 表
-- 1.1 添加 source 字段
ALTER TABLE interview_preparation_todos
ADD COLUMN source VARCHAR(20) NOT NULL DEFAULT 'User' COMMENT 'TODO来源: AI(AI生成) / User(用户创建)' AFTER todo_type;

-- 1.2 修改 user_id 允许为 NULL（因为通过 interview_stage_id 间接关联到用户）
ALTER TABLE interview_preparation_todos
MODIFY COLUMN user_id bigint NULL COMMENT '用户ID（可为NULL，通过interview_stage_id间接关联）';

-- Step 2: 迁移 interview_preparation_checklist 中的数据到 interview_preparation_todos
INSERT INTO interview_preparation_todos
  (interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, checklist_item_id, is_completed)
SELECT
  c.interview_stage_id,
  NULL AS user_id,  -- user_id为NULL，通过interview_stage_id间接关联
  c.checklist_item AS title,
  c.notes AS description,
  COALESCE(c.category, 'General') AS todo_type,
  'AI' AS source,
  CASE WHEN c.is_priority = 1 THEN 4 ELSE 2 END AS priority,
  c.sort_order AS order_index,
  c.id AS checklist_item_id,
  0 AS is_completed
FROM interview_preparation_checklist c
WHERE NOT EXISTS (
  SELECT 1 FROM interview_preparation_todos t
  WHERE t.checklist_item_id = c.id
);

-- Step 3: 更新索引
ALTER TABLE interview_preparation_todos
ADD INDEX idx_source (source),
ADD INDEX idx_stage_user_null (interview_stage_id, user_id);

-- Step 4: 重命名旧表作为备份（不删除）
RENAME TABLE interview_preparation_checklist TO interview_preparation_checklist_backup;

-- =====================================================
-- 新表结构说明:
-- =====================================================
-- interview_preparation_todos 现在包含:
-- - AI生成的清单项 (source = 'AI', user_id = NULL)
-- - 用户自定义的TODO (source = 'User', user_id 可为NULL)
--
-- 用户关联方式:
-- - interview_preparation_todos.interview_stage_id
-- - → interview_stages.job_application_id
-- - → job_applications.user_id
--
-- 所有记录都可以:
-- - 标记完成 (is_completed)
-- - 关联资源 (通过 interview_preparation_todo_resources 表)
-- - 添加详细描述 (description)
-- =====================================================
