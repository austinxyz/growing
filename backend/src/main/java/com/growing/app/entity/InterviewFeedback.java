package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 面试反馈实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "interview_feedback",
       indexes = @Index(name = "idx_record", columnList = "interview_record_id"))
@Data
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_record_id", nullable = false)
    private Long interviewRecordId;

    @Column(name = "feedback_date", nullable = false)
    private LocalDateTime feedbackDate;

    @Column(name = "feedback_source", length = 100)
    private String feedbackSource;

    @Column(name = "feedback_content", columnDefinition = "TEXT")
    private String feedbackContent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
