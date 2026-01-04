package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 面试流程阶段实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "interview_stages",
       indexes = {
           @Index(name = "idx_job", columnList = "job_application_id"),
           @Index(name = "idx_order", columnList = "job_application_id, stage_order")
       })
@Data
public class InterviewStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "skill_ids", columnDefinition = "JSON")
    private String skillIds;

    @Column(name = "preparation_notes", columnDefinition = "TEXT")
    private String preparationNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
