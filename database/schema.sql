-- =====================================================
-- Growing App - Complete Database Schema
-- Consolidated from Flyway migrations V1-V5
-- Last Updated: 2025-12-21
-- Database: MySQL 8.0/9.4
-- Charset: utf8mb4
-- =====================================================

-- =====================================================
-- Phase 1: 用户和职业路径
-- =====================================================

-- 1. 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    full_name VARCHAR(100) COMMENT '全名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: user, admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 职业路径表
CREATE TABLE career_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '职业路径名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '唯一标识代码',
    description TEXT COMMENT '描述',
    icon VARCHAR(50) COMMENT '图标',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_code (code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职业路径表';

-- 3. 用户-职业路径关联表
CREATE TABLE user_career_paths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    career_path_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_career (user_id, career_path_id),
    INDEX idx_user_id (user_id),
    INDEX idx_career_path_id (career_path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-职业路径关联表';

-- =====================================================
-- Phase 2: 技能体系管理
-- =====================================================

-- 4. 技能表
CREATE TABLE skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(100) NOT NULL COMMENT '技能名称，如"编程"',
    description TEXT COMMENT '技能描述',
    is_important TINYINT(1) DEFAULT 0 COMMENT '是否重要技能（⭐标记）',
    icon VARCHAR(50) DEFAULT NULL COMMENT 'Emoji图标，如"💻"',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_display_order (display_order),
    INDEX idx_is_important (is_important)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能表';

-- 5. 职业路径-技能关联表
CREATE TABLE career_path_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    career_path_id BIGINT NOT NULL COMMENT '职业路径ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 唯一约束：一个职业路径下不能重复关联同一个技能
    UNIQUE KEY uk_career_skill (career_path_id, skill_id),

    INDEX idx_career_path_id (career_path_id),
    INDEX idx_skill_id (skill_id),

    FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职业路径-技能关联表';

-- 6. 专注领域表 (Focus Areas)
CREATE TABLE focus_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    skill_id BIGINT NOT NULL COMMENT '所属技能ID',
    name VARCHAR(200) NOT NULL COMMENT '领域名称，如"动态规划"',
    description TEXT COMMENT '描述',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_skill_id (skill_id),
    INDEX idx_display_order (display_order),

    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专注领域表（技能细分方向）';

-- 7. 学习资源表
CREATE TABLE learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    skill_id BIGINT NOT NULL COMMENT '所属技能ID（直接关联技能，不关联Focus Area）',
    resource_type ENUM(
        'DOCUMENT',     -- 文档
        'VIDEO',        -- 视频
        'COURSE',       -- 课程
        'BOOK',         -- 书籍
        'BLOG',         -- 博客
        'PRACTICE',     -- 练习
        'TOOL',         -- 工具
        'ARTICLE',      -- 文章
        'WEBSITE',      -- 网站
        'OTHER'         -- 其他
    ) NOT NULL COMMENT '资源类型',
    title VARCHAR(200) NOT NULL COMMENT '资源标题',
    url VARCHAR(500) DEFAULT NULL COMMENT '资源链接',
    author VARCHAR(100) DEFAULT NULL COMMENT '作者/来源',
    description TEXT COMMENT '资源描述',
    is_official TINYINT(1) DEFAULT 0 COMMENT '是否官方资源（管理员添加）',
    created_by_user_id BIGINT DEFAULT NULL COMMENT '创建者ID（用户添加的资源）',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_skill_id (skill_id),
    INDEX idx_is_official (is_official),
    INDEX idx_created_by_user_id (created_by_user_id),
    INDEX idx_display_order (display_order),

    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学习资源表';

-- =====================================================
-- Phase 3: 试题库基础功能
-- =====================================================

-- 8. 试题表
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    focus_area_id BIGINT NOT NULL COMMENT '所属Focus Area',
    question_text TEXT NOT NULL COMMENT '问题描述（支持Markdown）',
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL COMMENT '难度级别',
    answer_requirement TEXT COMMENT '答案要求（Markdown）- 说明如何回答这个问题',
    target_position VARCHAR(100) COMMENT '针对职位（如"软件工程师"）',
    target_level VARCHAR(50) COMMENT '针对级别（如"Senior"）',
    red_flags TEXT COMMENT 'Red Flags列表（JSON数组格式）',
    is_official TINYINT(1) DEFAULT 0 COMMENT '是否公共试题',
    created_by_user_id BIGINT COMMENT '创建者ID（admin创建则为null）',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_focus_area_id (focus_area_id),
    INDEX idx_is_official (is_official),
    INDEX idx_created_by_user_id (created_by_user_id),
    INDEX idx_difficulty (difficulty),
    INDEX idx_target_position (target_position),
    INDEX idx_target_level (target_level),
    INDEX idx_display_order (display_order),

    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试题表';

-- 9. 用户试题笔记表
CREATE TABLE user_question_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '所属问题ID',
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    note_content TEXT NOT NULL COMMENT '笔记内容（Markdown）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_question_id (question_id),
    INDEX idx_user_id (user_id),

    -- 唯一约束：一个用户对一个试题只能有一条笔记
    UNIQUE KEY uk_question_user (question_id, user_id),

    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户试题笔记表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 职业路径数据
INSERT INTO career_paths (name, code, description, icon, is_active) VALUES
('软件工程师', 'software-engineer', '全栈开发、系统设计、算法优化', 'code', TRUE),
('职业经理', 'engineering-manager', '团队管理、项目交付、技术领导力', 'users', TRUE);

-- 初始化测试管理员账号
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('austinxu', 'austin@example.com', '$2a$10$wP3UnBbGMpZ7BRmqbucM9uYpnflgV5du/6Qb3Ez7/ewI4Sa1jfbdW', 'Austin Xu', 'admin');
-- 密码: helloworld (BCrypt hash)

-- 核心技能数据
INSERT INTO skills (name, description, is_important, icon, display_order) VALUES
('编程和数据结构', '算法、数据结构、编程基础，面试必备核心技能', TRUE, 'code', 1),
('系统设计', '系统架构、设计模式、可扩展性设计', TRUE, 'layers', 2),
('Behavioral技能', '行为面试、领导力原则、软技能', TRUE, 'users', 3),
('云计算', '云服务、云架构、容器编排', TRUE, 'cloud', 4),
('AI/机器学习', '机器学习算法、深度学习、AI应用', TRUE, 'brain', 5),
('Infrastructure', '基础设施、网络、运维', FALSE, 'server', 6),
('DevOps', 'CI/CD、自动化运维、监控', FALSE, 'tool', 7),
('项目管理', '项目计划、执行、风险控制', FALSE, 'briefcase', 8),
('人员管理', '团队管理、人才发展、绩效管理', TRUE, 'user-check', 9);

-- 职业路径-技能关联
-- 软件工程师路径 (ID=1)
INSERT INTO career_path_skills (career_path_id, skill_id) VALUES
(1, 1),  -- 编程和数据结构
(1, 2),  -- 系统设计
(1, 3),  -- Behavioral
(1, 4),  -- 云计算
(1, 5);  -- AI/机器学习

-- 职业经理路径 (ID=2)
INSERT INTO career_path_skills (career_path_id, skill_id) VALUES
(2, 1),  -- 编程和数据结构（职业经理也需要技术背景）
(2, 2),  -- 系统设计
(2, 3),  -- Behavioral
(2, 8),  -- 项目管理
(2, 9);  -- 人员管理

-- =====================================================
-- 业务规则说明
-- =====================================================

-- 1. 资源可见性规则（learning_resources）：
--    用户查看技能时，只能看到：
--    SELECT * FROM learning_resources
--    WHERE skill_id = ?
--      AND (is_official = true OR created_by_user_id = ?)
--    ORDER BY display_order ASC, created_at ASC

-- 2. 试题可见性规则（questions）：
--    用户查看专注领域时，只能看到：
--    SELECT * FROM questions
--    WHERE focus_area_id = ?
--      AND (is_official = true OR created_by_user_id = ?)
--    ORDER BY display_order ASC, created_at ASC

-- 3. 级联删除规则：
--    - 删除 Career Path → CASCADE 删除 career_path_skills（关联技能不删除）
--    - 删除 Skill → CASCADE 删除 career_path_skills, focus_areas, learning_resources
--    - 删除 Focus Area → CASCADE 删除 questions → CASCADE 删除 user_question_notes
--    - 删除 User → CASCADE 删除 learning_resources.created_by_user_id, questions.created_by_user_id, user_question_notes

-- 4. 用户笔记UPSERT逻辑：
--    - 一个用户对一个试题只能有一条笔记（UNIQUE约束: uk_question_user）
--    - 添加笔记时，如果已存在则更新，否则创建新记录

-- =====================================================
-- 版本历史
-- =====================================================
-- V1: 用户和职业路径表（Phase 1）
-- V2: 技能体系管理表（Phase 2）
-- V3: 初始化核心技能数据（Phase 2）
-- V4: 更新资源类型枚举（Phase 2）
-- V5: 试题库基础功能表（Phase 3）
