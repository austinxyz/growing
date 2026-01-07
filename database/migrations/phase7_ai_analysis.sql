-- ========================================
-- Phase 7 Enhancement: AI-Driven JD-Resume Matching Analysis
-- Created: 2026-01-06
-- ========================================

-- AI分析记录表
CREATE TABLE `ai_job_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `job_application_id` bigint NOT NULL COMMENT '职位申请ID',
  `resume_id` bigint NOT NULL COMMENT '简历ID',

  -- Prompt & Analysis
  `generated_prompt` text NOT NULL COMMENT '生成的AI分析Prompt',
  `ai_analysis_result` text COMMENT 'AI分析结果（Markdown格式）',

  -- Metadata (JSON)
  `analysis_metadata` json DEFAULT NULL COMMENT '分析元数据: {
    "skillMatchScore": 85,
    "experienceMatchScore": 90,
    "overallScore": 87,
    "keyStrengths": ["..."],
    "keyWeaknesses": ["..."],
    "recommendation": "Strong Match"
  }',

  -- Analysis Status
  `status` varchar(50) DEFAULT 'prompt_generated' COMMENT '分析状态: prompt_generated, completed',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_job` (`job_application_id`),
  KEY `idx_resume` (`resume_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `ai_job_analysis_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_job_analysis_ibfk_2` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_job_analysis_ibfk_3` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI驱动的JD-Resume匹配分析记录';
