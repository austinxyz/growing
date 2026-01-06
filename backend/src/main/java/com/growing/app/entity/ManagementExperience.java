package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员管理经验实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "management_experiences",
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_focus_area", columnList = "user_id, focus_area_id")
       })
@Data
public class ManagementExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "focus_area_id")
    private Long focusAreaId;

    @Column(name = "experience_name", nullable = false)
    private String experienceName;

    @Column(name = "team_growth_subtype", length = 50)
    private String teamGrowthSubtype;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "background", columnDefinition = "TEXT")
    private String background;

    @Column(name = "actions_taken", columnDefinition = "TEXT")
    private String actionsTaken;

    @Column(name = "results", columnDefinition = "TEXT")
    private String results;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "hiring_count")
    private Integer hiringCount;

    @Column(name = "improvement_result", length = 50)
    private String improvementResult;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
