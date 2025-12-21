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
