-- Migration: Add focus_area_ids to project_experiences
-- Purpose: Replace skill_ids with finer-grained Focus Area linking
-- Date: 2026-01-04

-- Step 1: Add focus_area_ids field
ALTER TABLE `project_experiences`
ADD COLUMN `focus_area_ids` json DEFAULT NULL
COMMENT '关联的Focus Area ID列表（JSON数组，替代skill_ids，粒度更细）';

-- Step 2: Add index to optimize JSON queries
ALTER TABLE `project_experiences`
ADD INDEX `idx_focus_area_ids` ((CAST(`focus_area_ids` AS CHAR(255) ARRAY)));

-- Step 3: Remove deprecated skill_ids field
ALTER TABLE `project_experiences`
DROP COLUMN `skill_ids`;
