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

-- ========================================
-- Phase 7: Job Search Management Module
-- Created: 2026-01-03
-- ========================================

-- 1. 简历主表
CREATE TABLE `resumes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `version_name` varchar(100) NOT NULL COMMENT '简历版本名称',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否为默认简历',
  `about` text COMMENT '个人简介（Markdown）',
  `career_objective` text COMMENT '职业目标（Markdown）',

  -- Contact & Links
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `phone` varchar(50) DEFAULT NULL COMMENT '电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `linkedin_url` varchar(500) DEFAULT NULL COMMENT 'LinkedIn URL',
  `github_url` varchar(500) DEFAULT NULL COMMENT 'GitHub URL',
  `website_url` varchar(500) DEFAULT NULL COMMENT '个人网站',
  `other_links` json DEFAULT NULL COMMENT '其他链接（JSON数组）',

  -- Languages & Hobbies
  `languages` json DEFAULT NULL COMMENT '语言能力（JSON数组）',
  `hobbies` text COMMENT '兴趣爱好',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_version` (`user_id`,`version_name`),
  KEY `idx_user` (`user_id`),
  KEY `idx_default` (`user_id`,`is_default`),
  CONSTRAINT `resumes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历主表';

-- 2. 工作经历
CREATE TABLE `resume_experiences` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `company_name` varchar(255) NOT NULL COMMENT '公司名称',
  `position` varchar(255) NOT NULL COMMENT '职位名称',
  `location` varchar(255) DEFAULT NULL COMMENT '所在地（城市、国家）',
  `start_date` date NOT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间（NULL表示至今）',
  `is_current` tinyint(1) DEFAULT '0' COMMENT '是否为当前工作',
  `responsibilities` text COMMENT '职责描述（Markdown）',
  `achievements` text COMMENT '主要成就（Markdown）',

  -- 关联项目经验
  `project_ids` json DEFAULT NULL COMMENT '关联的项目经验ID列表（JSON数组）',

  `sort_order` int DEFAULT '0' COMMENT '排序（按时间倒序）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_resume` (`resume_id`),
  KEY `idx_dates` (`start_date`,`end_date`),
  CONSTRAINT `resume_experiences_ibfk_1` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作经历';

-- 3. 简历技能列表
CREATE TABLE `resume_skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `skill_category` varchar(50) NOT NULL COMMENT '技能分类：Technical, Soft, Tools',
  `skill_name` varchar(255) NOT NULL COMMENT '技能名称',
  `proficiency` varchar(50) DEFAULT NULL COMMENT '熟练程度：Beginner, Intermediate, Advanced, Expert',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_resume` (`resume_id`),
  KEY `idx_category` (`resume_id`,`skill_category`),
  CONSTRAINT `resume_skills_ibfk_1` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历技能列表';

-- 4. 教育背景
CREATE TABLE `resume_education` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `school_name` varchar(255) NOT NULL COMMENT '学校名称',
  `degree` varchar(100) NOT NULL COMMENT '学位：Bachelor, Master, PhD, Other',
  `major` varchar(255) DEFAULT NULL COMMENT '专业',
  `start_date` date DEFAULT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间',
  `gpa` decimal(3,2) DEFAULT NULL COMMENT 'GPA（如3.8）',
  `courses` text COMMENT '主要课程（Markdown）',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_resume` (`resume_id`),
  CONSTRAINT `resume_education_ibfk_1` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教育背景';

-- 5. 培训和证书
CREATE TABLE `resume_certifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `cert_name` varchar(255) NOT NULL COMMENT '证书名称',
  `issuer` varchar(255) NOT NULL COMMENT '颁发机构',
  `issue_date` date DEFAULT NULL COMMENT '获得时间',
  `expiry_date` date DEFAULT NULL COMMENT '有效期（NULL表示永久有效）',
  `cert_url` varchar(500) DEFAULT NULL COMMENT '证书链接',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_resume` (`resume_id`),
  CONSTRAINT `resume_certifications_ibfk_1` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='培训和证书';

-- 6. 技术项目经验
CREATE TABLE `project_experiences` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `project_name` varchar(255) NOT NULL COMMENT '项目名称',
  `project_type` varchar(50) DEFAULT NULL COMMENT '项目类型：Feature, Optimization, Migration, BugFix, Infrastructure, Other',

  -- What/When/Who/Why
  `what_description` text COMMENT '项目是什么（Markdown）',
  `start_date` date DEFAULT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间',
  `team_size` int DEFAULT NULL COMMENT '团队规模',
  `my_role` varchar(255) DEFAULT NULL COMMENT '我的角色',
  `background` text COMMENT '项目背景和目标（Markdown）',

  -- Problem Statement & Challenges
  `problem_statement` text COMMENT '核心问题（Markdown）',
  `challenges` text COMMENT '主要挑战和难点（Markdown）',
  `constraints` text COMMENT '约束条件（Markdown）',

  -- How
  `tech_stack` text COMMENT '技术选型和理由（Markdown）',
  `architecture` text COMMENT '架构设计（Markdown）',
  `innovation` text COMMENT '创新点/差异化做法（Markdown）',
  `my_contribution` text COMMENT '个人贡献（Markdown）',

  -- Result
  `quantitative_results` text COMMENT '量化数据（Markdown）',
  `business_impact` text COMMENT '业务影响（Markdown）',
  `personal_growth` text COMMENT '团队/个人成长（Markdown）',
  `lessons_learned` text COMMENT '经验教训（Markdown）',

  -- 标签和分类
  `tech_tags` json DEFAULT NULL COMMENT '技术标签（JSON数组）',
  `skill_ids` json DEFAULT NULL COMMENT '关联的技能ID（JSON数组，关联到skills表）',
  `difficulty` varchar(50) DEFAULT NULL COMMENT '难度级别：Low, Medium, High, Critical',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_dates` (`start_date`,`end_date`),
  CONSTRAINT `project_experiences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技术项目经验';

-- 7. 人员管理经验
CREATE TABLE `management_experiences` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `experience_name` varchar(255) NOT NULL COMMENT '经验名称',
  `experience_type` varchar(50) NOT NULL COMMENT '经验类型：ManageUp, CrossTeam, TeamGrowth',

  -- Team Growth 细分（仅当 experience_type = 'TeamGrowth' 时使用）
  `team_growth_subtype` varchar(50) DEFAULT NULL COMMENT 'Hiring, HighPerformer, LowPerformer',

  `start_date` date DEFAULT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间',

  `background` text COMMENT '背景和挑战（Markdown）',
  `actions_taken` text COMMENT '采取的行动（Markdown）',
  `results` text COMMENT '结果和影响（Markdown）',
  `lessons_learned` text COMMENT '经验教训（Markdown）',

  -- Team Growth 特定字段
  `hiring_count` int DEFAULT NULL COMMENT '招聘人数（仅 Hiring）',
  `improvement_result` varchar(50) DEFAULT NULL COMMENT '改进结果（仅 LowPerformer）：Improved, Terminated',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`user_id`,`experience_type`),
  CONSTRAINT `management_experiences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员管理经验';

-- 8. 公司档案
CREATE TABLE `companies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_name` varchar(255) NOT NULL COMMENT '公司名称',
  `company_description` text COMMENT '公司简介（Markdown）',
  `company_culture` text COMMENT '企业文化（Markdown）',
  `location` varchar(255) DEFAULT NULL COMMENT '所在地（总部城市、国家）',
  `company_size` varchar(50) DEFAULT NULL COMMENT '公司规模：<50, 50-200, 200-1000, 1000-5000, >5000',
  `industry` varchar(100) DEFAULT NULL COMMENT '行业分类：Technology, Finance, Healthcare, etc.',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_company` (`user_id`,`company_name`),
  KEY `idx_user` (`user_id`),
  KEY `idx_industry` (`industry`),
  CONSTRAINT `companies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公司档案';

-- 9. 公司有用链接
CREATE TABLE `company_links` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `link_title` varchar(255) NOT NULL COMMENT '链接标题',
  `link_url` varchar(500) NOT NULL COMMENT 'URL',
  `notes` text COMMENT '备注',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_company` (`company_id`),
  CONSTRAINT `company_links_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公司有用链接';

-- 10. 职位申请
CREATE TABLE `job_applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `position_name` varchar(255) NOT NULL COMMENT '职位名称',
  `position_level` varchar(50) DEFAULT NULL COMMENT '职位级别：Junior, Mid, Senior, Staff, Principal',
  `posted_date` date DEFAULT NULL COMMENT '发布时间',
  `job_url` varchar(500) DEFAULT NULL COMMENT '职位链接',

  -- JD 核心内容
  `qualifications` text COMMENT '技能要求（Markdown）',
  `responsibilities` text COMMENT '岗位职责（Markdown）',

  -- 申请状态
  `application_status` varchar(50) DEFAULT 'NotApplied' COMMENT '申请状态：NotApplied, Applied, PhoneScreen, Onsite, Offer, Rejected',
  `status_updated_at` timestamp NULL DEFAULT NULL COMMENT '状态更新时间',
  `status_history` json DEFAULT NULL COMMENT '状态变更历史（JSON数组）',

  -- Offer 详情（如果状态为 Offer）
  `offer_received_at` timestamp NULL DEFAULT NULL COMMENT 'Offer 接收时间',
  `base_salary` decimal(12,2) DEFAULT NULL COMMENT 'Base Salary（加密存储）',
  `bonus` decimal(12,2) DEFAULT NULL COMMENT 'Bonus',
  `stock_value` decimal(12,2) DEFAULT NULL COMMENT 'Stock/RSU',
  `total_compensation` decimal(12,2) DEFAULT NULL COMMENT 'Total Compensation',
  `offer_deadline` date DEFAULT NULL COMMENT 'Offer 截止时间',
  `offer_decision` varchar(50) DEFAULT NULL COMMENT 'Offer 决策：Accepted, Declined, Pending',
  `offer_notes` text COMMENT '决策备注（Markdown）',

  -- 拒绝详情（如果状态为 Rejected）
  `rejected_at` timestamp NULL DEFAULT NULL COMMENT '拒绝时间',
  `rejected_stage` varchar(50) DEFAULT NULL COMMENT '拒绝阶段：PhoneScreen, Onsite, AfterOnsite',
  `rejection_reasons` json DEFAULT NULL COMMENT '拒绝原因（JSON数组）',
  `improvement_plan` text COMMENT '改进计划（Markdown）',

  `notes` text COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_company_position` (`user_id`,`company_id`,`position_name`),
  KEY `idx_user` (`user_id`),
  KEY `idx_company` (`company_id`),
  KEY `idx_status` (`application_status`),
  CONSTRAINT `job_applications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `job_applications_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职位申请';

-- 11. 面试流程阶段
CREATE TABLE `interview_stages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_application_id` bigint NOT NULL COMMENT '职位申请ID',
  `stage_name` varchar(255) NOT NULL COMMENT '阶段名称：Phone Screen, Technical Interview, Onsite Round 1, etc.',
  `stage_order` int NOT NULL COMMENT '阶段顺序',

  -- 关联技能
  `skill_ids` json DEFAULT NULL COMMENT '关联的技能ID（JSON数组，关联到skills表）',
  `preparation_notes` text COMMENT '准备重点（Markdown）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_job` (`job_application_id`),
  KEY `idx_order` (`job_application_id`,`stage_order`),
  CONSTRAINT `interview_stages_ibfk_1` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试流程阶段';

-- 12. 招聘人员
CREATE TABLE `recruiters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_id` bigint NOT NULL COMMENT '所属公司ID',
  `recruiter_name` varchar(255) NOT NULL COMMENT '姓名',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) DEFAULT NULL COMMENT '电话',
  `linkedin_url` varchar(500) DEFAULT NULL COMMENT 'LinkedIn URL',
  `notes` text COMMENT '备注',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_company` (`company_id`),
  CONSTRAINT `recruiters_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `recruiters_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='招聘人员';

-- 13. 招聘沟通记录
CREATE TABLE `recruiter_communications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recruiter_id` bigint NOT NULL COMMENT 'Recruiter ID',
  `job_application_id` bigint DEFAULT NULL COMMENT '关联的职位申请ID（可选）',

  `communication_date` timestamp NOT NULL COMMENT '沟通时间',
  `communication_method` varchar(50) NOT NULL COMMENT '沟通方式：Email, Phone, LinkedIn, InPerson',
  `communication_content` text COMMENT '沟通内容（Markdown）',
  `next_action` text COMMENT '下一步行动',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_recruiter` (`recruiter_id`),
  KEY `idx_date` (`communication_date`),
  CONSTRAINT `recruiter_communications_ibfk_1` FOREIGN KEY (`recruiter_id`) REFERENCES `recruiters` (`id`) ON DELETE CASCADE,
  CONSTRAINT `recruiter_communications_ibfk_2` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='招聘沟通记录';

-- 14. 内推人脉
CREATE TABLE `referrals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_id` bigint NOT NULL COMMENT '所属公司ID',
  `referral_name` varchar(255) NOT NULL COMMENT '推荐人姓名',
  `relationship` varchar(50) NOT NULL COMMENT '关系：FormerColleague, Classmate, Friend, Mentor, Other',
  `position` varchar(255) DEFAULT NULL COMMENT '职位',

  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) DEFAULT NULL COMMENT '电话',
  `linkedin_url` varchar(500) DEFAULT NULL COMMENT 'LinkedIn URL',
  `notes` text COMMENT '备注（如何认识、关系亲密度）',

  -- 推荐状态
  `referral_status` varchar(50) DEFAULT 'NotRequested' COMMENT '推荐状态：NotRequested, Requested, Agreed, Declined, Submitted',
  `request_date` date DEFAULT NULL COMMENT '推荐请求时间',
  `submission_date` date DEFAULT NULL COMMENT '推荐提交时间',
  `referral_result` varchar(50) DEFAULT NULL COMMENT '推荐结果：InterviewScheduled, NoResponse, Rejected',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_company` (`company_id`),
  KEY `idx_status` (`referral_status`),
  CONSTRAINT `referrals_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `referrals_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内推人脉';

-- 15. 面试记录
CREATE TABLE `interview_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_application_id` bigint NOT NULL COMMENT '职位申请ID',
  `interview_stage_id` bigint DEFAULT NULL COMMENT '面试阶段ID（可选，关联到interview_stages）',

  `interview_date` timestamp NOT NULL COMMENT '面试时间',
  `interviewer_name` varchar(255) DEFAULT NULL COMMENT '面试官姓名',
  `interviewer_position` varchar(255) DEFAULT NULL COMMENT '面试官职位',
  `interviewer_linkedin` varchar(500) DEFAULT NULL COMMENT '面试官LinkedIn',

  `interview_format` varchar(50) NOT NULL COMMENT '面试形式：VideoCall, InPerson, Phone',
  `interview_duration` int DEFAULT NULL COMMENT '面试时长（分钟）',

  -- 自我评估
  `overall_performance` int DEFAULT NULL COMMENT '整体表现（1-5）',
  `technical_depth` int DEFAULT NULL COMMENT '技术深度（1-5）',
  `communication` int DEFAULT NULL COMMENT '沟通表达（1-5）',
  `problem_solving` int DEFAULT NULL COMMENT '问题回答（1-5）',
  `self_summary` text COMMENT '自我总结（Markdown）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_job` (`job_application_id`),
  KEY `idx_date` (`interview_date`),
  CONSTRAINT `interview_records_ibfk_1` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_records_ibfk_2` FOREIGN KEY (`interview_stage_id`) REFERENCES `interview_stages` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试记录';

-- 16. 面试问题
CREATE TABLE `interview_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_record_id` bigint NOT NULL COMMENT '面试记录ID',
  `question_order` int NOT NULL COMMENT '问题顺序',

  `question_description` text NOT NULL COMMENT '问题描述',
  `question_type` varchar(50) NOT NULL COMMENT '问题类型：Coding, SystemDesign, Behavioral, Technical, Other',

  `my_answer` text COMMENT '我的回答（Markdown）',
  `related_question_id` bigint DEFAULT NULL COMMENT '关联的试题ID（关联到questions表）',

  `answer_quality` int DEFAULT NULL COMMENT '回答质量自我评估（1-5）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_record` (`interview_record_id`),
  KEY `idx_type` (`question_type`),
  CONSTRAINT `interview_questions_ibfk_1` FOREIGN KEY (`interview_record_id`) REFERENCES `interview_records` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_questions_ibfk_2` FOREIGN KEY (`related_question_id`) REFERENCES `questions` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试问题';

-- 17. 面试反馈
CREATE TABLE `interview_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_record_id` bigint NOT NULL COMMENT '面试记录ID',

  `feedback_date` timestamp NOT NULL COMMENT '反馈时间',
  `feedback_source` varchar(100) DEFAULT NULL COMMENT '反馈来源：Recruiter, HiringManager, etc.',
  `feedback_content` text COMMENT '反馈内容（Markdown）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_record` (`interview_record_id`),
  CONSTRAINT `interview_feedback_ibfk_1` FOREIGN KEY (`interview_record_id`) REFERENCES `interview_records` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试反馈';
