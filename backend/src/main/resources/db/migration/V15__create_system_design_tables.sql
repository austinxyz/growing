-- Phase 5.2: 创建系统设计案例相关表

-- 1. 系统设计案例主表
CREATE TABLE system_design_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL COMMENT '关联到系统设计skill (固定为2)',

    -- 基本信息
    title VARCHAR(500) NOT NULL COMMENT '案例标题(如:设计Twitter)',
    case_description TEXT COMMENT '案例描述(Markdown格式)',
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    difficulty_rating INT COMMENT '难度评分 1-10',

    -- 元数据
    company_tags TEXT COMMENT '面试公司标签 (JSON数组: ["Google", "Meta"])',
    related_focus_areas TEXT COMMENT '关联的Focus Area IDs (JSON数组: [1, 5, 12])',

    -- 管理字段
    is_official BOOLEAN DEFAULT TRUE COMMENT '是否官方案例',
    created_by_user_id BIGINT COMMENT '创建人ID',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_skill_id (skill_id),
    INDEX idx_difficulty (difficulty_rating),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='系统设计案例表';

-- 2. 案例学习资源表（视频、文章）
CREATE TABLE case_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL COMMENT '关联的案例ID',

    -- 资源信息
    resource_type ENUM('VIDEO', 'ARTICLE') NOT NULL COMMENT '资源类型',
    title VARCHAR(500) NOT NULL COMMENT '资源标题',
    url VARCHAR(1000) NOT NULL COMMENT '资源URL',
    description TEXT COMMENT '资源描述',

    -- 管理字段
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='案例学习资源表';

-- 3. 案例参考答案表（支持多个方案）
CREATE TABLE case_solutions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL COMMENT '关联的案例ID',

    -- 方案信息
    solution_name VARCHAR(200) NOT NULL COMMENT '方案名称(如:方案A-基础版本,方案B-高级版本)',
    author VARCHAR(200) COMMENT '作者',

    -- 6个步骤内容（Markdown格式）
    step1_requirements TEXT COMMENT '步骤1:需求澄清与功能列表',
    step2_entities TEXT COMMENT '步骤2:核心实体定义',
    step3_api TEXT COMMENT '步骤3:API设计',
    step4_data_flow TEXT COMMENT '步骤4:数据流设计',
    step5_architecture TEXT COMMENT '步骤5:高层架构设计',
    step6_deep_dive TEXT COMMENT '步骤6:深入讨论',

    -- 管理字段
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    INDEX idx_case_id (case_id),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='案例参考答案表(支持多方案)';

-- 4. 参考答案配图表
CREATE TABLE solution_diagrams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    solution_id BIGINT NOT NULL COMMENT '关联的参考答案ID',

    -- 配图信息
    diagram_type ENUM('ARCHITECTURE', 'DATA_FLOW', 'ENTITY', 'OTHER') NOT NULL COMMENT '配图类型',
    title VARCHAR(200) COMMENT '配图标题',
    image_url VARCHAR(1000) NOT NULL COMMENT '图片URL',
    description TEXT COMMENT '配图说明',

    -- 管理字段
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (solution_id) REFERENCES case_solutions(id) ON DELETE CASCADE,
    INDEX idx_solution_id (solution_id),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='参考答案配图表';

-- 5. 用户答题记录表
CREATE TABLE user_case_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL COMMENT '关联的案例ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',

    -- 用户答案（6个步骤，Markdown格式）
    step1_requirements TEXT COMMENT '步骤1:需求澄清与功能列表',
    step2_entities TEXT COMMENT '步骤2:核心实体定义',
    step3_api TEXT COMMENT '步骤3:API设计',
    step4_data_flow TEXT COMMENT '步骤4:数据流设计',
    step5_architecture TEXT COMMENT '步骤5:高层架构设计',
    step6_deep_dive TEXT COMMENT '步骤6:深入讨论',

    -- 用户架构图
    architecture_diagram_url VARCHAR(1000) COMMENT '用户上传的架构图URL',

    -- 要点总结
    key_points TEXT COMMENT '要点总结(Markdown格式)',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (case_id) REFERENCES system_design_cases(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_case (case_id, user_id) COMMENT '一个用户对一个案例只能有一条记录',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='用户答题记录表';
