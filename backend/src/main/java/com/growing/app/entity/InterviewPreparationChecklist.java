package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 面试准备清单实体类
 * Phase 7: 求职管理模块 - Enhancements
 */
@Entity
@Table(name = "interview_preparation_checklist",
       indexes = {
           @Index(name = "idx_stage", columnList = "interview_stage_id"),
           @Index(name = "idx_priority", columnList = "interview_stage_id, is_priority"),
           @Index(name = "idx_order", columnList = "interview_stage_id, sort_order")
       })
@Data
public class InterviewPreparationChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_stage_id", nullable = false)
    private Long interviewStageId;

    @Column(name = "checklist_item", nullable = false, length = 500)
    private String checklistItem;

    @Column(name = "is_priority")
    private Boolean isPriority = false;

    @Column(name = "category", length = 50)
    private String category; // Study, Practice, Research, Review, Other

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
