package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 面试记录实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "interview_records",
       indexes = {
           @Index(name = "idx_job", columnList = "job_application_id"),
           @Index(name = "idx_date", columnList = "interview_date")
       })
@Data
public class InterviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;

    @Column(name = "interview_stage_id")
    private Long interviewStageId;

    @Column(name = "interview_date", nullable = false)
    private LocalDateTime interviewDate;

    @Column(name = "interviewer_name")
    private String interviewerName;

    @Column(name = "interviewer_position")
    private String interviewerPosition;

    @Column(name = "interviewer_linkedin", length = 500)
    private String interviewerLinkedin;

    @Column(name = "interview_format", nullable = false, length = 50)
    private String interviewFormat;

    @Column(name = "interview_duration")
    private Integer interviewDuration;

    // Self Assessment
    @Column(name = "overall_performance")
    private Integer overallPerformance;

    @Column(name = "technical_depth")
    private Integer technicalDepth;

    @Column(name = "communication")
    private Integer communication;

    @Column(name = "problem_solving")
    private Integer problemSolving;

    @Column(name = "self_summary", columnDefinition = "TEXT")
    private String selfSummary;

    @Column(name = "result", length = 20, nullable = false)
    private String result = "Pending";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
