-- =====================================================
-- Migration V8: User Template Notes
-- 用户算法模版笔记表（类似user_question_notes）
-- =====================================================

CREATE TABLE user_template_notes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_id BIGINT NOT NULL COMMENT '关联的模版ID (learning_contents表中content_type=algorithm_template的记录)',
  user_id BIGINT NOT NULL COMMENT '创建者ID',
  note_content TEXT NOT NULL COMMENT '笔记内容（Markdown格式）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (template_id) REFERENCES learning_contents(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uk_template_user (template_id, user_id),
  INDEX idx_template_id (template_id),
  INDEX idx_user_id (user_id)
) COMMENT='用户算法模版笔记表（一个用户对一个模版只能有一条笔记）';
