-- Growing App Database Schema
-- 不使用Flyway，通过mysql-exec执行SQL迁移
-- 最后更新: 2025-12-28 (Phase 6 完成)

-- 用户表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 职业路径表
CREATE TABLE `career_paths` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '职业路径名称',
  `code` varchar(50) NOT NULL COMMENT '唯一标识代码',
  `description` text COMMENT '描述',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否激活',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业路径表';

-- 技能表
CREATE TABLE `skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '技能名称，如"编程"',
  `description` text COMMENT '技能描述',
  `is_important` tinyint(1) DEFAULT '0' COMMENT '是否重要技能（⭐标记）',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标名称（保留字段，暂不使用）',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `is_general_only` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为通用分类（无大分类，直接管理Focus Area）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_display_order` (`display_order`),
  KEY `idx_is_important` (`is_important`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能表';

-- 职业路径-技能关联表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业路径-技能关联表';

-- 用户-职业路径关联表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-职业路径关联表';

-- 专注领域表（技能细分方向）
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专注领域表（技能细分方向）';

-- 学习资源表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习资源表';

-- 试题表
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

-- 用户试题笔记表
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

-- 学习阶段定义表（Skill级别配置）
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

-- 学习资源网站配置表
CREATE TABLE `learning_resource_websites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称（如 labuladong, hellointerview）',
  `display_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '显示名称',
  `base_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站起始地址',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者/团队',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '网站介绍/目的',
  `supports_iframe` tinyint(1) DEFAULT '0' COMMENT '是否支持iframe嵌入',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习资源网站配置表';

-- 学习内容统一表
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
  `website_id` bigint DEFAULT NULL COMMENT '关联的学习资源网站ID',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_focus_stage` (`focus_area_id`,`stage_id`,`sort_order`),
  KEY `idx_stage_type` (`stage_id`,`content_type`),
  KEY `fk_learning_contents_website` (`website_id`),
  CONSTRAINT `fk_learning_contents_website` FOREIGN KEY (`website_id`) REFERENCES `learning_resource_websites` (`id`) ON DELETE SET NULL,
  CONSTRAINT `learning_contents_ibfk_1` FOREIGN KEY (`focus_area_id`) REFERENCES `focus_areas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `learning_contents_ibfk_2` FOREIGN KEY (`stage_id`) REFERENCES `learning_stages` (`id`) ON DELETE CASCADE,
  CONSTRAINT `learning_contents_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习内容统一表';

-- 用户学习内容笔记表
CREATE TABLE `user_learning_content_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `learning_content_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `note_content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_learning_content_user` (`learning_content_id`,`user_id`),
  KEY `idx_user_learning_content_notes_user_id` (`user_id`),
  KEY `idx_user_learning_content_notes_content_id` (`learning_content_id`),
  CONSTRAINT `fk_note_learning_content` FOREIGN KEY (`learning_content_id`) REFERENCES `learning_contents` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_note_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户学习内容知识点表
CREATE TABLE `user_learning_content_knowledge_points` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `learning_content_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `summary` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_learning_content_kp_user_id` (`user_id`),
  KEY `idx_user_learning_content_kp_content_id` (`learning_content_id`),
  KEY `idx_user_learning_content_kp_order` (`learning_content_id`,`user_id`,`display_order`),
  CONSTRAINT `fk_kp_learning_content` FOREIGN KEY (`learning_content_id`) REFERENCES `learning_contents` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_kp_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 大分类表（数据结构、搜索、动规、其他）
CREATE TABLE `major_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '分类描述',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `skill_id` bigint NOT NULL COMMENT '所属Skill ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_skill_id` (`skill_id`),
  CONSTRAINT `fk_major_categories_skill` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='大分类表（数据结构、搜索、动规、其他）';

-- Focus Area与大分类关联表
CREATE TABLE `focus_area_categories` (
  `focus_area_id` bigint NOT NULL COMMENT 'Focus Area ID',
  `category_id` bigint NOT NULL COMMENT '大分类ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`focus_area_id`,`category_id`),
  KEY `idx_category` (`category_id`),
  CONSTRAINT `focus_area_categories_ibfk_1` FOREIGN KEY (`focus_area_id`) REFERENCES `focus_areas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `focus_area_categories_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `major_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Focus Area与大分类关联表';

-- 编程题专属字段表（仅用于编程与数据结构Skill）
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='编程题专属字段表（仅用于编程与数据结构Skill）';

-- 用户算法模版笔记表（一个用户对一个模版只能有一条笔记）
CREATE TABLE `user_template_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_id` bigint NOT NULL COMMENT '关联的模版ID (learning_contents表中content_type=algorithm_template的记录)',
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

-- 系统设计案例表
CREATE TABLE `system_design_cases` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `skill_id` bigint NOT NULL COMMENT '关联到系统设计skill (固定为2)',
  `title` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '案例标题(如:设计Twitter)',
  `case_description` text COLLATE utf8mb4_unicode_ci COMMENT '案例描述(Markdown格式)',
  `difficulty` enum('EASY','MEDIUM','HARD') COLLATE utf8mb4_unicode_ci NOT NULL,
  `difficulty_rating` int DEFAULT NULL COMMENT '难度评分 1-10',
  `company_tags` text COLLATE utf8mb4_unicode_ci COMMENT '面试公司标签 (JSON数组: ["Google", "Meta"])',
  `related_focus_areas` text COLLATE utf8mb4_unicode_ci COMMENT '关联的Focus Area IDs (JSON数组: [1, 5, 12])',
  `is_official` tinyint(1) DEFAULT '1' COMMENT '是否官方案例',
  `created_by_user_id` bigint DEFAULT NULL COMMENT '创建人ID',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `created_by_user_id` (`created_by_user_id`),
  KEY `idx_skill_id` (`skill_id`),
  KEY `idx_difficulty` (`difficulty_rating`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `system_design_cases_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE,
  CONSTRAINT `system_design_cases_ibfk_2` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设计案例表';

-- 案例学习资源表
CREATE TABLE `case_resources` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_id` bigint NOT NULL COMMENT '关联的案例ID',
  `resource_type` enum('VIDEO','ARTICLE') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源类型',
  `title` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源标题',
  `url` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源URL',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '资源描述',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `case_resources_ibfk_1` FOREIGN KEY (`case_id`) REFERENCES `system_design_cases` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例学习资源表';

-- 案例参考答案表(支持多方案)
CREATE TABLE `case_solutions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_id` bigint NOT NULL COMMENT '关联的案例ID',
  `solution_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方案名称(如:方案A-基础版本,方案B-高级版本)',
  `author` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
  `step1_requirements` text COLLATE utf8mb4_unicode_ci COMMENT '步骤1:需求澄清与功能列表',
  `step2_entities` text COLLATE utf8mb4_unicode_ci COMMENT '步骤2:核心实体定义',
  `step3_api` text COLLATE utf8mb4_unicode_ci COMMENT '步骤3:API设计',
  `step4_data_flow` text COLLATE utf8mb4_unicode_ci COMMENT '步骤4:数据流设计',
  `step5_architecture` text COLLATE utf8mb4_unicode_ci COMMENT '步骤5:高层架构设计',
  `step6_deep_dive` text COLLATE utf8mb4_unicode_ci COMMENT '步骤6:深入讨论',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_case_id` (`case_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `case_solutions_ibfk_1` FOREIGN KEY (`case_id`) REFERENCES `system_design_cases` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例参考答案表(支持多方案)';

-- 参考答案配图表
CREATE TABLE `solution_diagrams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `solution_id` bigint NOT NULL COMMENT '关联的参考答案ID',
  `diagram_type` enum('ARCHITECTURE','DATA_FLOW','ENTITY','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配图类型',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配图标题',
  `image_url` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '配图说明',
  `display_order` int DEFAULT '0' COMMENT '显示顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_solution_id` (`solution_id`),
  KEY `idx_display_order` (`display_order`),
  CONSTRAINT `solution_diagrams_ibfk_1` FOREIGN KEY (`solution_id`) REFERENCES `case_solutions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参考答案配图表';

-- 用户答题记录表
CREATE TABLE `user_case_notes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `case_id` bigint NOT NULL COMMENT '关联的案例ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `kp_requirement` text COLLATE utf8mb4_unicode_ci COMMENT '需求分析(Requirement)',
  `kp_nfr` text COLLATE utf8mb4_unicode_ci COMMENT '非功能性需求(Non-Functional Requirements)',
  `kp_entity` text COLLATE utf8mb4_unicode_ci COMMENT '核心实体定义(Entity)',
  `kp_components` text COLLATE utf8mb4_unicode_ci COMMENT '关键组件(Key Components)',
  `kp_api` text COLLATE utf8mb4_unicode_ci COMMENT 'API设计(API)',
  `kp_object1` text COLLATE utf8mb4_unicode_ci COMMENT '核心对象1设计(Object1)',
  `kp_object2` text COLLATE utf8mb4_unicode_ci COMMENT '核心对象2设计(Object2)',
  `step1_requirements` text COLLATE utf8mb4_unicode_ci COMMENT '步骤1:需求澄清与功能列表',
  `step2_entities` text COLLATE utf8mb4_unicode_ci COMMENT '步骤2:核心实体定义',
  `step3_api` text COLLATE utf8mb4_unicode_ci COMMENT '步骤3:API设计',
  `step4_data_flow` text COLLATE utf8mb4_unicode_ci COMMENT '步骤4:数据流设计',
  `step5_architecture` text COLLATE utf8mb4_unicode_ci COMMENT '步骤5:高层架构设计',
  `step6_deep_dive` text COLLATE utf8mb4_unicode_ci COMMENT '步骤6:深入讨论',
  `architecture_diagram_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户上传的架构图URL',
  `key_points` text COLLATE utf8mb4_unicode_ci COMMENT '要点总结(Markdown格式)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_case` (`case_id`,`user_id`) COMMENT '一个用户对一个案例只能有一条记录',
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `user_case_notes_ibfk_1` FOREIGN KEY (`case_id`) REFERENCES `system_design_cases` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_case_notes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户答题记录表';

-- 答题模版表（Phase 6）
CREATE TABLE `answer_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) NOT NULL COMMENT '模版名称',
  `description` text COMMENT '模版说明',
  `template_fields` json NOT NULL COMMENT '模版字段定义 JSON数组，格式: [{"key":"situation","label":"Situation","placeholder":"描述当时的情境..."}]',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `template_name` (`template_name`),
  KEY `idx_template_name` (`template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题模版表';

-- 技能-模版关联表（Phase 6）
CREATE TABLE `skill_templates` (
  `skill_id` bigint NOT NULL COMMENT '技能ID',
  `template_id` bigint NOT NULL COMMENT '模版ID',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否为默认模版',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`skill_id`,`template_id`),
  KEY `idx_skill_id` (`skill_id`),
  KEY `idx_template_id` (`template_id`),
  CONSTRAINT `skill_templates_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE,
  CONSTRAINT `skill_templates_ibfk_2` FOREIGN KEY (`template_id`) REFERENCES `answer_templates` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能-模版关联表';
