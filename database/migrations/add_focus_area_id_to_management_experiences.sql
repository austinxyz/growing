-- 添加 focus_area_id 字段到 management_experiences 表
-- Phase 7: 求职管理模块 - 支持按Focus Area分组管理经验

ALTER TABLE `management_experiences`
ADD COLUMN `focus_area_id` bigint DEFAULT NULL COMMENT 'Focus Area ID (关联人员管理的Focus Area)' AFTER `user_id`,
ADD KEY `idx_focus_area_id` (`focus_area_id`);

-- 注意：experience_type 字段保留用于向后兼容，但现在主要通过 focus_area_id 关联
