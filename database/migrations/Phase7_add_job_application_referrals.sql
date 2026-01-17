-- Phase 7: 添加职位-人脉关联表
-- 用于将referrals（招聘人员或内推人员）关联到具体的job_application

CREATE TABLE IF NOT EXISTS `job_application_referrals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_application_id` bigint NOT NULL COMMENT '职位申请ID',
  `referral_id` bigint NOT NULL COMMENT '人脉ID（来自referrals表）',
  `role_type` varchar(50) NOT NULL COMMENT '在此职位中的角色：Recruiter（招聘人员）, Referrer（内推人）',
  `notes` text COMMENT '针对此职位的备注（如：负责Phone Screen面试）',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_job_application` (`job_application_id`),
  KEY `idx_referral` (`referral_id`),
  UNIQUE KEY `uk_job_referral` (`job_application_id`, `referral_id`),
  CONSTRAINT `job_application_referrals_ibfk_1` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `job_application_referrals_ibfk_2` FOREIGN KEY (`referral_id`) REFERENCES `referrals` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位申请-人脉关联表';
