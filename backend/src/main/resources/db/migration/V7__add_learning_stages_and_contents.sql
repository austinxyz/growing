-- =====================================================
-- Migration V7: Learning Stages + Learning Contents
-- =====================================================
-- Created: 2025-12-22
-- Purpose: Phase 4 - 编程与数据结构学习模块
-- Architecture: v2.0 (learning_stages + learning_contents)
-- =====================================================

-- 1. 创建learning_stages表（学习阶段定义表，Skill级别配置）
CREATE TABLE learning_stages (
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
CREATE TABLE learning_contents (
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

-- 3. 修改questions表（增加stage_id字段关联到学习阶段）
ALTER TABLE questions
ADD COLUMN stage_id BIGINT COMMENT '所属学习阶段（实战题目阶段）',
ADD FOREIGN KEY (stage_id) REFERENCES learning_stages(id) ON DELETE SET NULL;

-- 4. 删除major_categories中的2个分类（保留4个：数据结构、搜索、动规、其他）
DELETE FROM major_categories WHERE name IN ('核心刷题框架', '基础篇');

-- 5. 初始化"编程与数据结构"的学习阶段
-- 假设skill_id=1为"编程与数据结构"（需根据实际数据调整）
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
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
-- 3. questions表扩展
--    - stage_id关联到"实战题目"阶段
--    - 允许题目按学习阶段组织
--
-- 4. major_categories调整
--    - 删除"核心刷题框架"和"基础篇"
--    - 保留4个主分类：数据结构、搜索、动规、其他
--
-- 5. 初始数据
--    - 为"编程与数据结构"Skill初始化3个学习阶段
--    - 实际导入内容将在后续通过管理端或脚本完成
-- =====================================================

-- =====================================================
-- 级联删除规则
-- =====================================================
-- 删除Skill → 级联删除所有learning_stages → 级联删除所有learning_contents
-- 删除Focus Area → 级联删除关联的learning_contents（但不影响算法模版）
-- 删除LearningStage → 级联删除该阶段的所有learning_contents
-- 删除Question → 不影响learning_contents（题目独立存在）
--
-- 注意: 实际场景中很少删除大分类和Focus Area，这些规则主要用于数据清理和测试。
-- =====================================================
