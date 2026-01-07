-- Add job_application_id to resumes table
-- Phase 7: 定制简历功能

-- Add job_application_id column
ALTER TABLE resumes
ADD COLUMN job_application_id bigint DEFAULT NULL COMMENT '关联的职位申请ID，NULL表示通用简历',
ADD KEY idx_job_application_id (job_application_id);

-- Add foreign key constraint
ALTER TABLE resumes
ADD CONSTRAINT fk_resumes_job_application
FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE SET NULL;
