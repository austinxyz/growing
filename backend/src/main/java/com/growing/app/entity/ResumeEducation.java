package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育背景实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "resume_education",
       indexes = @Index(name = "idx_resume", columnList = "resume_id"))
@Data
public class ResumeEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(name = "degree", nullable = false, length = 100)
    private String degree;

    @Column(name = "major")
    private String major;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa;

    @Column(name = "courses", columnDefinition = "TEXT")
    private String courses;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
