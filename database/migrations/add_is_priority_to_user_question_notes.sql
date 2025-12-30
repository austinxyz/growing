-- Add is_priority field to user_question_notes table
-- This allows users to mark questions as priority/important for focused review

ALTER TABLE `user_question_notes`
ADD COLUMN `is_priority` TINYINT(1) DEFAULT 0 COMMENT '是否标记为重点题（用户个人标记）' AFTER `core_strategy`;

-- Add index for efficient filtering by priority
ALTER TABLE `user_question_notes`
ADD INDEX `idx_user_priority` (`user_id`, `is_priority`);

-- Update comments
ALTER TABLE `user_question_notes` COMMENT='用户试题笔记表（包含笔记内容和重点标记）';
