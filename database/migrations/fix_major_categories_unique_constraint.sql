-- 修复 major_categories 表的唯一约束
-- 问题：uk_name 约束导致跨 Skill 的同名分类冲突（如多个 General）
-- 解决：改为 (skill_id, name) 组合唯一

-- 1. 删除旧的全局唯一约束
ALTER TABLE major_categories DROP INDEX uk_name;

-- 2. 添加新的组合唯一约束（skill_id + name）
ALTER TABLE major_categories ADD UNIQUE KEY uk_skill_name (skill_id, name);
