-- =====================================================
-- Growing App - Complete Database Schema
-- 完整数据库架构（包含V1-V10所有migration的最终状态）
-- Last Updated: 2025-12-24
-- Database: MySQL 8.0+
-- Charset: utf8mb4
-- =====================================================

-- =====================================================
-- Phase 1: 用户认证和职业路径管理
-- =====================================================

-- 1. 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `password_hash` varchar(255) NOT NULL COMMENT '密码哈希',
  `full_name` varchar(100) DEFAULT NULL COMMENT '全名',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `bio` text COMMENT '个人简介',
  `role` varchar(20) DEFAULT 'user' COMMENT '角色: user, admin',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- 2. 职业路径表
CREATE TABLE `career_paths` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '职业路径名称',
  `code` varchar(50) NOT NULL COMMENT '唯一标识代码',
  `description` text COMMENT '描述',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标（Emoji）',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职业路径表';

-- 3. 用户-职业路径关联表
CREATE TABLE `user_career_paths` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `career_path_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_career` (`user_id`,`career_path_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_career_path_id` (`career_path_id`),
  CONSTRAINT `user_career_paths_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_career_paths_ibfk_2` FOREIGN KEY (`career_path_id`) REFERENCES `career_paths` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-职业路径关联表';

-- =====================================================
-- Phase 2: 技能体系和学习资源管理
-- =====================================================

-- 4. 技能表
CREATE TABLE `skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '技能名称，如"编程"',
  `description` text COMMENT '技能描述',
  `is_important` tinyint(1) DEFAULT '0' COMMENT '是否重要技能（⭐标记）',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标名称（保留字段，暂不使用）',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_display_order` (`display_order`),
  KEY `idx_is_important` (`is_important`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能表';

-- 5. 职业路径-技能关联表
CREATE TABLE `career_path_skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `career_path_id` bigint NOT NULL COMMENT '职业路径ID',
  `skill_id` bigint NOT NULL COMMENT '技能ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_career_skill` (`career_path_id`,`skill_id`),
  KEY `idx_career_path_id` (`career_path_id`),
  KEY `idx_skill_id` (`skill_id`),
  CONSTRAINT `career_path_skills_ibfk_1` FOREIGN KEY (`career_path_id`) REFERENCES `career_paths` (`id`) ON DELETE CASCADE,
  CONSTRAINT `career_path_skills_ibfk_2` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职业路径-技能关联表';

-- 6. 专注领域表 (Focus Areas)
CREATE TABLE `focus_areas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `skill_id` bigint NOT NULL COMMENT '所属技能ID',
  `name` varchar(200) NOT NULL COMMENT '领域名称，如"动态规划"',
  `description` text COMMENT '描述',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_skill_id` (`skill_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `focus_areas_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专注领域表（技能细分方向）';

-- 7. 学习资源表
CREATE TABLE `learning_resources` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `skill_id` bigint NOT NULL COMMENT '所属技能ID（直接关联技能，不关联Focus Area）',
  `resource_type` enum('BOOK','COURSE','WEBSITE','ARTICLE','OTHER') NOT NULL COMMENT '资源类型',
  `title` varchar(200) NOT NULL COMMENT '资源标题',
  `url` varchar(500) DEFAULT NULL COMMENT '资源链接',
  `author` varchar(100) DEFAULT NULL COMMENT '作者/来源',
  `description` text COMMENT '资源描述',
  `is_official` tinyint(1) DEFAULT '0' COMMENT '是否官方资源（管理员添加）',
  `created_by_user_id` bigint DEFAULT NULL COMMENT '创建者ID（用户添加的资源）',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_skill_id` (`skill_id`),
  KEY `idx_is_official` (`is_official`),
  KEY `idx_created_by_user_id` (`created_by_user_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `learning_resources_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE,
  CONSTRAINT `learning_resources_ibfk_2` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学习资源表';

-- =====================================================
-- Phase 3: 试题库基础功能
-- =====================================================

-- 8. 试题表
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `focus_area_id` bigint NOT NULL COMMENT '所属Focus Area',
  `title` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `question_description` text COLLATE utf8mb4_unicode_ci,
  `difficulty` enum('EASY','MEDIUM','HARD') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '难度级别',
  `question_type` enum('behavioral','technical','design','programming') COLLATE utf8mb4_unicode_ci DEFAULT 'technical',
  `answer_requirement` text COLLATE utf8mb4_unicode_ci COMMENT '答案要求（Markdown）- 说明如何回答这个问题',
  `target_position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '针对职位（如"软件工程师"）',
  `target_level` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '针对级别（如"Senior"）',
  `red_flags` text COLLATE utf8mb4_unicode_ci COMMENT 'Red Flags列表（JSON数组格式）',
  `is_official` tinyint(1) DEFAULT '0' COMMENT '是否公共试题',
  `created_by_user_id` bigint DEFAULT NULL COMMENT '创建者ID（admin创建则为null）',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_focus_area_id` (`focus_area_id`),
  KEY `idx_is_official` (`is_official`),
  KEY `idx_created_by_user_id` (`created_by_user_id`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_target_position` (`target_position`),
  KEY `idx_target_level` (`target_level`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`focus_area_id`) REFERENCES `focus_areas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `questions_ibfk_2` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试题表';

-- 9. 用户试题笔记表
CREATE TABLE `user_question_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '所属问题ID',
  `user_id` bigint NOT NULL COMMENT '创建者ID',
  `note_content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '笔记内容（Markdown）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `core_strategy` text COLLATE utf8mb4_unicode_ci COMMENT '核心思路（Markdown格式，仅编程题使用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_user` (`question_id`,`user_id`) COMMENT '一个用户对一个试题只能有一条笔记',
  KEY `idx_question_id` (`question_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_question_notes_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_question_notes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户试题笔记表';

-- =====================================================
-- Phase 4: 算法与数据结构学习模块
-- =====================================================

-- 10. 大分类表（Major Categories）
CREATE TABLE `major_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '分类描述',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='大分类表（数据结构、搜索、动规、其他）';

-- 11. 学习阶段定义表（Learning Stages）
CREATE TABLE `learning_stages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `skill_id` bigint NOT NULL COMMENT '所属技能',
  `stage_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '阶段名称，如"基础原理"',
  `stage_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '阶段类型标识，如"theory"',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '阶段说明',
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_skill_stage` (`skill_id`,`stage_name`),
  KEY `idx_skill_order` (`skill_id`,`sort_order`),
  CONSTRAINT `learning_stages_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习阶段定义表（Skill级别配置）';

-- 12. 学习内容统一表（Learning Contents）
CREATE TABLE `learning_contents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `focus_area_id` bigint DEFAULT NULL COMMENT 'Focus Area ID（算法模版为NULL）',
  `stage_id` bigint DEFAULT NULL COMMENT '所属学习阶段（算法模版为NULL）',
  `content_type` enum('article','video','code_example','template','case_study') COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '外部资源链接',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content_data` json DEFAULT NULL COMMENT '扩展数据，如代码、模版、案例详情等',
  `sort_order` int DEFAULT '0',
  `visibility` enum('public','private') COLLATE utf8mb4_unicode_ci DEFAULT 'public',
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_focus_stage` (`focus_area_id`,`stage_id`,`sort_order`),
  KEY `idx_stage_type` (`stage_id`,`content_type`),
  CONSTRAINT `learning_contents_ibfk_1` FOREIGN KEY (`focus_area_id`) REFERENCES `focus_areas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `learning_contents_ibfk_2` FOREIGN KEY (`stage_id`) REFERENCES `learning_stages` (`id`) ON DELETE CASCADE,
  CONSTRAINT `learning_contents_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习内容统一表';

-- 13. Focus Area - 大分类关联表
CREATE TABLE `focus_area_categories` (
  `focus_area_id` bigint NOT NULL COMMENT 'Focus Area ID',
  `category_id` bigint NOT NULL COMMENT '大分类ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`focus_area_id`,`category_id`),
  KEY `idx_category` (`category_id`),
  CONSTRAINT `focus_area_categories_ibfk_1` FOREIGN KEY (`focus_area_id`) REFERENCES `focus_areas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `focus_area_categories_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `major_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Focus Area与大分类关联表';

-- 14. 编程题专属字段表（Programming Question Details）
CREATE TABLE `programming_question_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '关联的题目ID',
  `leetcode_number` int DEFAULT NULL,
  `leetcode_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'LeetCode题目链接',
  `labuladong_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'labuladong题解链接',
  `hellointerview_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Hello Interview题解链接',
  `is_important` tinyint(1) DEFAULT '0' COMMENT '是否为重要算法题（⭐标记）',
  `tags` json DEFAULT NULL COMMENT '算法技巧标签，如["双指针","滑动窗口"]',
  `complexity` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '算法复杂度，如"时间O(n), 空间O(1)"',
  `similar_questions` json DEFAULT NULL COMMENT '类似题目列表，如[{"id": 15, "title": "3Sum"}]',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `question_id` (`question_id`),
  KEY `idx_important` (`is_important`),
  KEY `idx_leetcode_number` (`leetcode_number`),
  CONSTRAINT `programming_question_details_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='编程题专属字段表（仅用于算法与数据结构Skill）';

-- 15. 用户算法模版笔记表（User Template Notes）
CREATE TABLE `user_template_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_id` bigint NOT NULL COMMENT '关联的模版ID (learning_contents表中content_type=template的记录)',
  `user_id` bigint NOT NULL COMMENT '创建者ID',
  `note_content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '笔记内容（Markdown格式）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_user` (`template_id`,`user_id`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_template_notes_ibfk_1` FOREIGN KEY (`template_id`) REFERENCES `learning_contents` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_template_notes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户算法模版笔记表（一个用户对一个模版只能有一条笔记）';

-- =====================================================
-- 业务规则说明
-- =====================================================

-- 1. 资源可见性规则（learning_resources）：
--    用户查看技能时，只能看到：
--    SELECT * FROM learning_resources
--    WHERE skill_id = ?
--      AND (is_official = 1 OR created_by_user_id = ?)
--    ORDER BY display_order ASC, created_at ASC

-- 2. 试题可见性规则（questions）：
--    用户查看专注领域时，只能看到：
--    SELECT * FROM questions
--    WHERE focus_area_id = ?
--      AND (is_official = 1 OR created_by_user_id = ?)
--    ORDER BY display_order ASC, created_at ASC

-- 3. 学习内容可见性规则（learning_contents）：
--    用户查看Focus Area的学习内容时，只能看到公共内容：
--    SELECT * FROM learning_contents
--    WHERE focus_area_id = ? AND stage_id = ? AND visibility = 'public'
--    ORDER BY sort_order ASC
--    （算法模版：WHERE focus_area_id IS NULL AND content_type = 'template' AND visibility = 'public'）

-- 4. 级联删除规则：
--    - 删除 Career Path → CASCADE 删除 career_path_skills, user_career_paths
--    - 删除 Skill → CASCADE 删除 career_path_skills, focus_areas, learning_resources, learning_stages
--    - 删除 Focus Area → CASCADE 删除 questions, learning_contents, focus_area_categories
--    - 删除 Question → CASCADE 删除 user_question_notes, programming_question_details
--    - 删除 User → CASCADE 删除 user_career_paths, learning_resources.created_by_user_id,
--                           questions.created_by_user_id, user_question_notes, user_template_notes
--    - 删除 LearningStage → CASCADE 删除 learning_contents
--    - 删除 LearningContent (template) → CASCADE 删除 user_template_notes

-- 5. 用户笔记UPSERT逻辑：
--    - 一个用户对一个试题只能有一条笔记（UNIQUE约束: uk_question_user）
--    - 一个用户对一个模版只能有一条笔记（UNIQUE约束: uk_template_user）
--    - 添加笔记时，如果已存在则更新，否则创建新记录

-- =====================================================
-- 版本历史（Flyway Migrations）
-- =====================================================
-- V1: 用户和职业路径表（Phase 1）
-- V2: 技能体系管理表（Phase 2）
-- V3: 初始化核心技能数据（Phase 2）
-- V4: 更新资源类型枚举（Phase 2）
-- V5: 试题库基础功能表（Phase 3）
-- V6: 未使用（跳过）
-- V7: 算法与数据结构学习模块（Phase 4）
--     - learning_stages表（学习阶段定义，Skill级别配置）
--     - learning_contents表（统一学习内容管理）
--     - major_categories表（4个大分类）
--     - focus_area_categories表（Focus Area大分类关联）
--     - programming_question_details表（编程题专属字段）
-- V8: 用户算法模版笔记表（user_template_notes）
-- V9: 添加leetcode_number字段到programming_question_details表
-- V10: 删除questions表的question_text字段（已废弃，统一使用question_description）

-- =====================================================
-- 注意事项
-- =====================================================
-- 1. 本schema文件是从实际数据库导出的完整定义（mysqldump）
-- 2. 包含所有V1-V10 migration的最终状态
-- 3. 如需初始化新数据库，请依次执行：
--    - database/schema.sql（本文件，创建所有表）
--    - database/init_data.sql（初始化数据）
-- 4. 生产环境使用Flyway自动管理schema版本
-- 5. 编程题的核心思路（core_strategy）存储在user_question_notes表中，
--    不是programming_question_details表，支持用户个性化解题方法
