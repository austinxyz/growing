-- 删除 experience_type 字段从 management_experiences 表
-- Phase 7: 求职管理模块 - 完全使用 focus_area_id 替代 experience_type

ALTER TABLE `management_experiences`
DROP COLUMN `experience_type`;
