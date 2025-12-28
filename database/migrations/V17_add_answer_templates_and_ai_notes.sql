-- V17: Phase 6 - 通用技能学习模块
-- 创建时间: 2025-12-27
-- 功能: 答题模版系统 + AI学习笔记系统

-- 1. 创建答题模版表
CREATE TABLE IF NOT EXISTS answer_templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_name VARCHAR(100) NOT NULL UNIQUE COMMENT '模版名称',
    description TEXT COMMENT '模版说明',
    template_fields JSON NOT NULL COMMENT '模版字段定义 JSON数组，格式: [{"key":"situation","label":"Situation","placeholder":"描述当时的情境..."}]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_template_name (template_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题模版表';

-- 2. 创建技能-模版关联表（复合主键）
CREATE TABLE IF NOT EXISTS skill_templates (
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    template_id BIGINT NOT NULL COMMENT '模版ID',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否为默认模版',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (skill_id, template_id),
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES answer_templates(id) ON DELETE CASCADE,
    INDEX idx_skill_id (skill_id),
    INDEX idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能-模版关联表';

-- 3. 扩展用户试题笔记表（新增知识点关联字段）
-- 注意：如果字段已存在则跳过
-- ALTER TABLE user_question_notes
-- ADD COLUMN related_knowledge_point_ids JSON COMMENT '关联的知识点ID列表 JSON数组，格式: [1,2,3]' AFTER note_content;

-- 4. 插入STAR模版预置数据
INSERT INTO answer_templates (template_name, description, template_fields) VALUES
('STAR', 'STAR方法论答题模版，适用于Behavioral题目',
'[
  {"key":"situation","label":"Situation (情境)","placeholder":"描述当时的情境和背景"},
  {"key":"task","label":"Task (任务)","placeholder":"说明你需要完成的任务或目标"},
  {"key":"action","label":"Action (行动)","placeholder":"详细描述你采取的具体行动"},
  {"key":"result","label":"Result (结果)","placeholder":"说明最终的结果和影响"}
]');

-- 5. 插入Technical模版预置数据
INSERT INTO answer_templates (template_name, description, template_fields) VALUES
('Technical', '技术题答题模版，适用于技术类问题',
'[
  {"key":"core_concept","label":"核心概念","placeholder":"解释关键技术概念"},
  {"key":"implementation","label":"实现方式","placeholder":"描述具体实现方法"},
  {"key":"tradeoffs","label":"权衡考虑","placeholder":"分析优缺点和适用场景"}
]');

-- 6. 为第二类技能创建"General"大分类数据
-- 注意：这个操作需要在实际运行时根据具体的第二类技能ID动态执行
-- 这里仅作为示例，实际插入会在管理员页面或数据导入脚本中完成
-- INSERT INTO major_categories (skill_id, category_name) VALUES
-- (?, 'General');  -- ? 为第二类技能的ID

-- 7. 创建AI特殊用户（user_id=-1）
-- 注意：需要先检查是否已存在，避免冲突
INSERT IGNORE INTO users (id, username, email, password_hash, role, created_at, updated_at) VALUES
(-1, 'AI', 'ai@system.local', '$2a$10$dummyHashForAIUser000000000000000000000000000000000000', 'system', NOW(), NOW());

-- 8. 数据库迁移完成
-- 验证命令:
-- SELECT * FROM answer_templates;  -- 应返回2条记录（STAR、Technical）
-- SELECT * FROM users WHERE id=-1;  -- 应返回AI用户
-- DESCRIBE user_question_notes;  -- 应包含related_knowledge_point_ids字段
