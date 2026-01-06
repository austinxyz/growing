-- ========================================
-- Phase 7 Enhancements: Job Application Preparation Features
-- Created: 2026-01-04
-- Description: 添加面试准备清单、Focus Area关联、Recruiter Insights
-- ========================================

-- 1. 添加 focus_area_ids 到 interview_stages
ALTER TABLE interview_stages
ADD COLUMN focus_area_ids JSON DEFAULT NULL COMMENT '关联的Focus Area ID（JSON数组）'
AFTER skill_ids;

-- 2. 添加 recruiter_insights 到 job_applications（不包括薪资范围）
ALTER TABLE job_applications
ADD COLUMN recruiter_insights JSON DEFAULT NULL COMMENT 'Recruiter提供的结构化信息（JSON对象）'
AFTER notes;

-- 3. 创建面试准备清单表
CREATE TABLE `interview_preparation_checklist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_stage_id` bigint NOT NULL COMMENT '关联的面试阶段ID',
  `checklist_item` varchar(500) NOT NULL COMMENT '准备项内容',
  `is_priority` tinyint(1) DEFAULT '0' COMMENT '是否为重点项（用于打印1-2页重点材料）',
  `category` varchar(50) DEFAULT NULL COMMENT '分类：Study, Practice, Research, Review, Other',
  `notes` text COMMENT '备注说明（Markdown）',
  `sort_order` int DEFAULT '0' COMMENT '排序',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_stage` (`interview_stage_id`),
  KEY `idx_priority` (`interview_stage_id`, `is_priority`),
  KEY `idx_order` (`interview_stage_id`, `sort_order`),
  CONSTRAINT `fk_checklist_stage` FOREIGN KEY (`interview_stage_id`)
    REFERENCES `interview_stages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试准备清单表';

-- 示例：recruiter_insights JSON格式说明
-- {
--   "team_size": "10-15人",
--   "team_culture": "扁平化管理，鼓励创新",
--   "tech_stack_preference": ["Java", "Spring Boot", "Kubernetes"],
--   "interview_focus": "系统设计和编程基础",
--   "process_tips": "第一轮技术面试会很深入，建议准备常见算法题"
-- }
