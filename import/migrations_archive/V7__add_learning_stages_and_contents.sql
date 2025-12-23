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
