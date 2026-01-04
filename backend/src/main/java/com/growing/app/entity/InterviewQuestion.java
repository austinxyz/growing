package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 面试问题实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "interview_questions",
       indexes = {
           @Index(name = "idx_record", columnList = "interview_record_id"),
           @Index(name = "idx_type", columnList = "question_type")
       })
@Data
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_record_id", nullable = false)
    private Long interviewRecordId;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "question_description", columnDefinition = "TEXT", nullable = false)
    private String questionDescription;

    @Column(name = "question_type", nullable = false, length = 50)
    private String questionType;

    @Column(name = "my_answer", columnDefinition = "TEXT")
    private String myAnswer;

    @Column(name = "related_question_id")
    private Long relatedQuestionId;

    @Column(name = "answer_quality")
    private Integer answerQuality;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
