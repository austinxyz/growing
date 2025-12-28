-- 添加 is_general_only 字段到 skills 表
-- 用于标识该技能是否只使用通用分类（不需要大分类）

ALTER TABLE skills
ADD COLUMN is_general_only BOOLEAN NOT NULL DEFAULT FALSE
COMMENT '是否为通用分类（无大分类，直接管理Focus Area）';

-- 更新现有的职业技能为通用分类
-- Phase 2 中的职业技能（如 Java, Spring Boot 等）不需要大分类
UPDATE skills SET is_general_only = TRUE WHERE career_path_id IS NOT NULL;
