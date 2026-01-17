package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * JobApplicationReferral DTO
 * Phase 7: 求职管理模块
 */
@Data
public class JobApplicationReferralDTO {
    private Long id;
    private Long jobApplicationId;
    private Long referralId;
    private String roleType; // Recruiter, Referrer
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 嵌套的referral信息（方便前端显示）
    private ReferralDTO referral;
}
