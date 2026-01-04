package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 内推人脉实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "referrals",
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_company", columnList = "company_id"),
           @Index(name = "idx_status", columnList = "referral_status")
       })
@Data
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "referral_name", nullable = false)
    private String referralName;

    @Column(name = "relationship", nullable = false, length = 50)
    private String relationship;

    @Column(name = "position")
    private String position;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "referral_status", length = 50)
    private String referralStatus = "NotRequested";

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "submission_date")
    private LocalDate submissionDate;

    @Column(name = "referral_result", length = 50)
    private String referralResult;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
