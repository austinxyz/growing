-- ==========================================
-- 面试准备笔记TODO功能 - 数据库迁移
-- 创建时间: 2026-01-15
-- 描述: 为面试阶段添加准备清单模板和TODO管理功能
-- ==========================================

-- 1. 创建准备清单模板表
CREATE TABLE IF NOT EXISTS `interview_preparation_checklists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_stage_id` bigint NOT NULL COMMENT '面试阶段ID',

  -- 清单项信息
  `item_title` varchar(500) NOT NULL COMMENT '清单项标题',
  `item_description` text COMMENT '清单项描述（Markdown）',
  `item_type` varchar(50) NOT NULL COMMENT '类型：SkillReview, PracticeQuestions, ProjectReview, CompanyResearch, Other',
  `order_index` int NOT NULL DEFAULT 0 COMMENT '排序索引',

  -- 关联资源
  `skill_ids` json DEFAULT NULL COMMENT '关联技能ID（JSON数组）',
  `question_ids` json DEFAULT NULL COMMENT '关联试题ID（JSON数组）',
  `project_ids` json DEFAULT NULL COMMENT '关联项目ID（JSON数组）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_stage` (`interview_stage_id`),
  KEY `idx_stage_order` (`interview_stage_id`, `order_index`),
  CONSTRAINT `interview_preparation_checklists_ibfk_1` FOREIGN KEY (`interview_stage_id`) REFERENCES `interview_stages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试准备清单模板';

-- 2. 创建准备TODO表
CREATE TABLE IF NOT EXISTS `interview_preparation_todos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_stage_id` bigint NOT NULL COMMENT '面试阶段ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',

  -- TODO基本信息
  `title` varchar(500) NOT NULL COMMENT 'TODO标题',
  `description` text COMMENT 'TODO描述（Markdown）',
  `todo_type` varchar(50) NOT NULL DEFAULT 'General' COMMENT 'TODO类型：General, StudyMaterial, Practice, ProjectReview, Checklist',
  `priority` int DEFAULT 0 COMMENT '优先级（0-5，数字越大优先级越高）',
  `order_index` int NOT NULL DEFAULT 0 COMMENT '排序索引',

  -- 关联资源
  `checklist_item_id` bigint DEFAULT NULL COMMENT '关联的准备清单项ID（如果有）',
  `resource_links` json DEFAULT NULL COMMENT '资源链接（JSON数组：[{title, url, type}]）',

  -- 完成状态
  `is_completed` boolean NOT NULL DEFAULT false COMMENT '是否完成',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `completion_notes` text COMMENT '完成备注（如：刷了哪些题、学了什么）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_stage` (`interview_stage_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_stage_order` (`interview_stage_id`, `order_index`),
  KEY `idx_checklist` (`checklist_item_id`),
  KEY `idx_completed` (`is_completed`, `completed_at`),
  CONSTRAINT `interview_preparation_todos_ibfk_1` FOREIGN KEY (`interview_stage_id`) REFERENCES `interview_stages` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_preparation_todos_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_preparation_todos_ibfk_3` FOREIGN KEY (`checklist_item_id`) REFERENCES `interview_preparation_checklists` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试准备TODO清单';

-- 3. 插入示例准备清单模板（可选，用于演示）
-- 注意：这里的interview_stage_id需要根据实际数据调整，或在应用层动态创建
-- INSERT INTO `interview_preparation_checklists` (`interview_stage_id`, `item_title`, `item_description`, `item_type`, `order_index`) VALUES
-- (1, '复习核心技术栈', '复习Java并发编程、Spring框架、MySQL优化', 'SkillReview', 1),
-- (1, '练习算法题', '刷LeetCode Medium难度题20道', 'PracticeQuestions', 2),
-- (1, '准备项目经验', '准备2-3个项目的STAR回答', 'ProjectReview', 3),
-- (1, '了解公司背景', '研究公司产品、技术栈、企业文化', 'CompanyResearch', 4);
