package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 职位申请实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "job_applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "company_id", "position_name"}),
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_company", columnList = "company_id"),
           @Index(name = "idx_status", columnList = "application_status")
       })
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "position_name", nullable = false)
    private String positionName;

    @Column(name = "position_level", length = 50)
    private String positionLevel;

    @Column(name = "posted_date")
    private LocalDate postedDate;

    @Column(name = "job_url", length = 500)
    private String jobUrl;

    @Column(name = "job_status", length = 50)
    private String jobStatus = "Open";

    // JD Core Content
    @Column(name = "qualifications", columnDefinition = "TEXT")
    private String qualifications;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    // Application Status
    @Column(name = "application_status", length = 50)
    private String applicationStatus = "NotApplied";

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Column(name = "status_history", columnDefinition = "JSON")
    private String statusHistory;

    // Offer Details
    @Column(name = "offer_received_at")
    private LocalDateTime offerReceivedAt;

    @Column(name = "base_salary", precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "bonus", precision = 12, scale = 2)
    private BigDecimal bonus;

    @Column(name = "stock_value", precision = 12, scale = 2)
    private BigDecimal stockValue;

    @Column(name = "total_compensation", precision = 12, scale = 2)
    private BigDecimal totalCompensation;

    @Column(name = "offer_deadline")
    private LocalDate offerDeadline;

    @Column(name = "offer_decision", length = 50)
    private String offerDecision;

    @Column(name = "offer_notes", columnDefinition = "TEXT")
    private String offerNotes;

    // Rejection Details
    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejected_stage", length = 50)
    private String rejectedStage;

    @Column(name = "rejection_reasons", columnDefinition = "JSON")
    private String rejectionReasons;

    @Column(name = "improvement_plan", columnDefinition = "TEXT")
    private String improvementPlan;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "recruiter_insights", columnDefinition = "JSON")
    private String recruiterInsights;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
