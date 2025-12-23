-- Phase 1: 用户和职业路径 - 数据库表创建
-- 创建时间: 2025-12-20

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

-- 初始化职业路径数据
INSERT INTO career_paths (name, code, description, icon, is_active) VALUES
('软件工程师', 'software-engineer', '全栈开发、系统设计、算法优化', 'code', TRUE),
('职业经理', 'engineering-manager', '团队管理、项目交付、技术领导力', 'users', TRUE);

-- 初始化测试管理员账号
INSERT INTO users (username, email, password_hash, full_name, role) VALUES
('austinxu', 'austin@example.com', '$2a$10$wP3UnBbGMpZ7BRmqbucM9uYpnflgV5du/6Qb3Ez7/ewI4Sa1jfbdW', 'Austin Xu', 'admin');
-- 密码: helloworld (BCrypt hash)
-- =====================================================
-- Phase 2: 技能体系管理 - 数据库Schema
-- 版本: v2.0
-- 创建时间: 2025-12-20
-- 更新时间: 2025-12-20
-- =====================================================

-- =====================================================
-- 1. skills 表 - 技能
-- =====================================================
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

-- =====================================================
-- 2. career_path_skills 表 - 职业路径与技能关联表
-- =====================================================
CREATE TABLE career_path_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    career_path_id BIGINT NOT NULL COMMENT '职业路径ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 唯一约束：一个职业路径下不能重复关联同一个技能
    UNIQUE KEY uk_career_skill (career_path_id, skill_id),

    -- 索引
    INDEX idx_career_path_id (career_path_id),
    INDEX idx_skill_id (skill_id),

    -- 外键约束
    FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职业路径-技能关联表';

-- =====================================================
-- 3. focus_areas 表 - 专注领域
-- =====================================================
CREATE TABLE focus_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    skill_id BIGINT NOT NULL COMMENT '所属技能ID',
    name VARCHAR(200) NOT NULL COMMENT '领域名称，如"动态规划"',
    description TEXT COMMENT '描述',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引
    INDEX idx_skill_id (skill_id),
    INDEX idx_display_order (display_order),

    -- 外键约束
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专注领域表（技能细分方向）';

-- =====================================================
-- 4. learning_resources 表 - 学习资源
-- =====================================================
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

    -- 索引
    INDEX idx_skill_id (skill_id),
    INDEX idx_is_official (is_official),
    INDEX idx_created_by_user_id (created_by_user_id),
    INDEX idx_display_order (display_order),

    -- 外键约束
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学习资源表';

-- =====================================================
-- 业务规则说明
-- =====================================================

-- 1. 资源可见性规则：
--    用户查看技能时，只能看到：
--    SELECT * FROM learning_resources
--    WHERE skill_id = ?
--      AND (is_official = true OR created_by_user_id = ?)
--    ORDER BY display_order ASC, created_at ASC

-- 2. 级联删除规则：
--    - 删除 Career Path → CASCADE 删除 career_path_skills（关联技能不删除）
--    - 删除 Skill → CASCADE 删除 career_path_skills, focus_areas, learning_resources
--    - 删除 User → CASCADE 删除 learning_resources.created_by_user_id

-- 3. 显示顺序规则：
--    - 技能：按 display_order 升序，相同则按 id 升序
--    - 专注领域：按 display_order 升序，相同则按 id 升序
--    - 学习资源：官方资源优先，然后按 display_order 和创建时间排序

-- 4. 重要技能标记：
--    - is_important = true 的技能会在列表页显示⭐标记
--    - 用户可以通过"重要技能"过滤器只查看重要技能
-- Phase 2: 技能体系管理 - 初始化核心技能数据
-- 创建时间: 2025-12-20

-- 插入9个核心技能
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

-- 关联技能到职业路径
-- 软件工程师路径 (假设ID=1)
INSERT INTO career_path_skills (career_path_id, skill_id) VALUES
(1, 1),  -- 编程和数据结构
(1, 2),  -- 系统设计
(1, 3),  -- Behavioral
(1, 4),  -- 云计算
(1, 5);  -- AI/机器学习

-- 职业经理路径 (假设ID=2)
INSERT INTO career_path_skills (career_path_id, skill_id) VALUES
(2, 1),  -- 编程和数据结构（职业经理也需要技术背景）
(2, 2),  -- 系统设计
(2, 3),  -- Behavioral
(2, 8),  -- 项目管理
(2, 9);  -- 人员管理
-- =====================================================
-- Phase 2: 更新 learning_resources 资源类型枚举
-- 版本: v4.0
-- 创建时间: 2025-12-20
-- =====================================================

-- 更新 learning_resources 表的 resource_type 枚举类型
-- 添加新的资源类型: VIDEO, DOCUMENT, BLOG, PRACTICE, TOOL
-- 从原来的 5 种类型扩展到 10 种类型

ALTER TABLE learning_resources
MODIFY COLUMN resource_type ENUM(
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
) NOT NULL COMMENT '资源类型';

-- =====================================================
-- 说明
-- =====================================================
-- 原枚举值: BOOK, COURSE, WEBSITE, ARTICLE, OTHER
-- 新增枚举值: DOCUMENT, VIDEO, BLOG, PRACTICE, TOOL
-- 总共 10 种资源类型,与后端 Java enum 保持一致
-- Phase 3: 试题库基础功能
-- 创建questions表和user_question_notes表

-- =====================================================
-- questions 表
-- =====================================================
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

    FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试题表';

-- questions表索引
CREATE INDEX idx_focus_area_id ON questions(focus_area_id);
CREATE INDEX idx_is_official ON questions(is_official);
CREATE INDEX idx_created_by_user_id ON questions(created_by_user_id);
CREATE INDEX idx_difficulty ON questions(difficulty);
CREATE INDEX idx_target_position ON questions(target_position);
CREATE INDEX idx_target_level ON questions(target_level);
CREATE INDEX idx_display_order ON questions(display_order);

-- =====================================================
-- user_question_notes 表
-- =====================================================
CREATE TABLE user_question_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL COMMENT '所属问题ID',
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    note_content TEXT NOT NULL COMMENT '笔记内容（Markdown）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    UNIQUE KEY uk_question_user (question_id, user_id) COMMENT '一个用户对一个试题只能有一条笔记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户试题笔记表';

-- user_question_notes表索引
CREATE INDEX idx_question_id ON user_question_notes(question_id);
CREATE INDEX idx_user_id ON user_question_notes(user_id);
-- =====================================================
-- Migration V7: Learning Stages + Learning Contents
-- =====================================================
-- Created: 2025-12-22
-- Purpose: Phase 4 - 编程与数据结构学习模块
-- Architecture: v2.0 (learning_stages + learning_contents)
-- =====================================================

-- 1. 创建learning_stages表（学习阶段定义表，Skill级别配置）
CREATE TABLE IF NOT EXISTS learning_stages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  skill_id BIGINT NOT NULL COMMENT '所属技能',
  stage_name VARCHAR(50) NOT NULL COMMENT '阶段名称，如"基础原理"',
  stage_type VARCHAR(50) NOT NULL COMMENT '阶段类型标识，如"theory"',
  description TEXT COMMENT '阶段说明',
  sort_order INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
  UNIQUE KEY uk_skill_stage (skill_id, stage_name),
  INDEX idx_skill_order (skill_id, sort_order)
) COMMENT='学习阶段定义表（Skill级别配置）';

-- 2. 创建learning_contents表（学习内容统一表）
CREATE TABLE IF NOT EXISTS learning_contents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  focus_area_id BIGINT COMMENT 'Focus Area ID（算法模版为NULL）',
  stage_id BIGINT NOT NULL COMMENT '所属学习阶段',
  content_type ENUM('article', 'video', 'code_example', 'template', 'case_study') NOT NULL,

  -- 通用字段
  title VARCHAR(500) NOT NULL,
  description TEXT,
  url VARCHAR(1000) COMMENT '外部资源链接',
  author VARCHAR(100),

  -- 扩展内容（JSON存储，根据content_type不同）
  content_data JSON COMMENT '扩展数据，如代码、模版、案例详情等',

  -- 元信息
  sort_order INT DEFAULT 0,
  visibility ENUM('public', 'private') DEFAULT 'public',
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
  FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE CASCADE,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_focus_stage (focus_area_id, stage_id, sort_order),
  INDEX idx_stage_type (stage_id, content_type)
) COMMENT='学习内容统一表';

-- 3. 创建major_categories表（大分类表）
CREATE TABLE IF NOT EXISTS major_categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  description TEXT COMMENT '分类描述',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_name (name),
  INDEX idx_sort_order (sort_order)
) COMMENT='大分类表（数据结构、搜索、动规、其他）';

-- 初始化4个大分类
INSERT IGNORE INTO major_categories (name, description, sort_order) VALUES
('数据结构', '数组、链表、树、图等数据结构', 1),
('搜索', '二分搜索、DFS、BFS等搜索算法', 2),
('动规', '动态规划相关算法', 3),
('其他', '其他算法和技巧', 4);

-- 4. 创建focus_area_categories关联表（Focus Area与大分类的多对多关系）
CREATE TABLE IF NOT EXISTS focus_area_categories (
  focus_area_id BIGINT NOT NULL COMMENT 'Focus Area ID',
  category_id BIGINT NOT NULL COMMENT '大分类ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (focus_area_id, category_id),
  FOREIGN KEY (focus_area_id) REFERENCES focus_areas(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES major_categories(id) ON DELETE CASCADE,
  INDEX idx_category (category_id)
) COMMENT='Focus Area与大分类关联表';

-- 5. 创建programming_question_details表（编程题专属字段表）
CREATE TABLE IF NOT EXISTS programming_question_details (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL UNIQUE COMMENT '关联的题目ID',

  -- 外部资源链接
  leetcode_url VARCHAR(500) COMMENT 'LeetCode题目链接',
  labuladong_url VARCHAR(500) COMMENT 'labuladong题解链接',
  hellointerview_url VARCHAR(500) COMMENT 'Hello Interview题解链接',

  -- 重要性标记
  is_important BOOLEAN DEFAULT FALSE COMMENT '是否为重要算法题（⭐标记）',

  -- 算法技巧标签
  tags JSON COMMENT '算法技巧标签，如["双指针","滑动窗口"]',

  -- 复杂度和相关题目
  complexity VARCHAR(100) COMMENT '算法复杂度，如"时间O(n), 空间O(1)"',
  similar_questions JSON COMMENT '类似题目列表，如[{"id": 15, "title": "3Sum"}]',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
  INDEX idx_important (is_important)
) COMMENT='编程题专属字段表（仅用于编程与数据结构Skill）';

-- 6. 修改user_question_notes表（增加core_strategy字段，仅编程题使用）
-- 检查列是否存在，不存在则添加
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                   AND TABLE_NAME = 'user_question_notes'
                   AND COLUMN_NAME = 'core_strategy');

SET @sql = IF(@col_exists = 0,
              'ALTER TABLE user_question_notes ADD COLUMN core_strategy TEXT COMMENT ''核心思路（Markdown格式，仅编程题使用）''',
              'SELECT ''Column core_strategy already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 7. 初始化"编程与数据结构"的学习阶段
-- 假设skill_id=1为"编程与数据结构"（需根据实际数据调整）
-- 使用INSERT IGNORE避免重复插入（基于uk_skill_stage唯一约束）
INSERT IGNORE INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(1, '基础原理', 'theory', '数据结构的基本概念、特点、适用场景', 1),
(1, '实现代码', 'implementation', '数据结构的代码实现、API设计、算法技巧', 2),
(1, '实战题目', 'practice', 'LeetCode题目练习、算法应用', 3);

-- =====================================================
-- 迁移说明
-- =====================================================
-- 1. learning_stages表
--    - 为每个Skill定义自定义的学习阶段序列
--    - 支持未来扩展（如系统设计Skill可定义不同的阶段）
--    - stage_type用于前端识别不同阶段的渲染方式
--
-- 2. learning_contents表
--    - 统一管理所有类型的学习内容（文章、视频、代码、模版等）
--    - focus_area_id = NULL表示算法模版（不关联Focus Area）
--    - content_data JSON字段支持不同类型内容的扩展数据
--    - visibility字段区分公共内容和用户自定义内容
--
-- 3. major_categories表
--    - 新增大分类表：数据结构、搜索、动规、其他
--    - 通过focus_area_categories关联表关联到Focus Area
--    - 支持按大分类组织Focus Area
--
-- 4. focus_area_categories关联表
--    - Focus Area与大分类的多对多关系
--    - 一个Focus Area可以属于多个大分类（例如"哈希表"可以同时属于"数据结构"和"搜索"）
--    - 级联删除：删除Focus Area或Category时自动删除关联记录
--
-- 5. programming_question_details表
--    - 存储编程题的专属字段（LeetCode链接、算法标签等）
--    - 与questions表一对一关系（question_id UNIQUE）
--    - 核心思路(core_strategy)存储在user_question_notes表（支持用户个性化）
--    - 级联删除：删除question时自动删除对应的programming_question_details
--
-- 6. user_question_notes表扩展
--    - core_strategy字段：用户的解题核心思路（Markdown格式）
--    - 仅编程题使用，其他类型题目该字段为NULL
--    - 支持不同用户对同一道题有不同的解法
--
-- 7. 初始数据
--    - 创建4个大分类：数据结构、搜索、动规、其他
--    - 为"编程与数据结构"Skill初始化3个学习阶段
--    - 实际导入内容将在后续通过管理端或脚本完成
-- =====================================================

-- =====================================================
-- 级联删除规则
-- =====================================================
-- 删除Skill → 级联删除所有learning_stages → 级联删除所有learning_contents
-- 删除Focus Area → 级联删除关联的learning_contents（但不影响算法模版）
--               → 级联删除focus_area_categories关联记录
-- 删除LearningStage → 级联删除该阶段的所有learning_contents
-- 删除Question → 级联删除programming_question_details（一对一关系）
-- 删除MajorCategory → 级联删除focus_area_categories关联记录
--
-- 注意: 实际场景中很少删除大分类和Focus Area，这些规则主要用于数据清理和测试。
-- =====================================================
