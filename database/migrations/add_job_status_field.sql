-- Add job_status field to job_applications table
-- Phase 7: Job Search Management - Job Status Feature

ALTER TABLE `job_applications`
ADD COLUMN `job_status` VARCHAR(50) DEFAULT 'Open' COMMENT '职位招聘状态：Open（开放招聘）, ActivelyHiring（积极招聘）, Closed（已关闭）' AFTER `job_url`;

-- Add index for job_status
ALTER TABLE `job_applications`
ADD INDEX `idx_job_status` (`job_status`);

-- Update schema.sql comment
-- Possible values:
-- - Open: 职位开放，正常招聘
-- - ActivelyHiring: 积极招聘，优先级高
-- - Closed: 职位已关闭，不再招聘
