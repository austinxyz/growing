-- Add skill_id column to major_categories table
ALTER TABLE major_categories
ADD COLUMN skill_id BIGINT NOT NULL DEFAULT 1 COMMENT '所属Skill ID';

-- Add foreign key constraint
ALTER TABLE major_categories
ADD CONSTRAINT fk_major_categories_skill
FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE;

-- Add index
ALTER TABLE major_categories
ADD INDEX idx_skill_id (skill_id);

-- Update existing algorithm categories (数据结构, 搜索, 动规, 其他) to skill_id=1 (算法与数据结构)
UPDATE major_categories
SET skill_id = 1
WHERE name IN ('数据结构', '搜索', '动规', '其他');

-- Update system design categories (核心概念, 关键技术, 设计模式) to skill_id=2 (系统设计)
UPDATE major_categories
SET skill_id = 2
WHERE name IN ('核心概念', '关键技术', '设计模式');

-- Remove default constraint (we added it temporarily for migration)
ALTER TABLE major_categories
ALTER COLUMN skill_id DROP DEFAULT;
