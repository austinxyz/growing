package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 职位申请-人脉关联实体
 * Phase 7: 求职管理模块 - 将referrals关联到具体的job_application
 */
@Entity
@Table(name = "job_application_referrals",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_job_referral", columnNames = {"job_application_id", "referral_id"})
       },
       indexes = {
           @Index(name = "idx_job_application", columnList = "job_application_id"),
           @Index(name = "idx_referral", columnList = "referral_id")
       })
@Data
public class JobApplicationReferral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;

    @Column(name = "referral_id", nullable = false)
    private Long referralId;

    @Column(name = "role_type", nullable = false, length = 50)
    private String roleType; // Recruiter, Referrer

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
