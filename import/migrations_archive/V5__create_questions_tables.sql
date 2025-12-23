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
