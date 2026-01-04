package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 简历技能列表实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "resume_skills",
       indexes = {
           @Index(name = "idx_resume", columnList = "resume_id"),
           @Index(name = "idx_category", columnList = "resume_id, skill_category")
       })
@Data
public class ResumeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "skill_category", nullable = false, length = 50)
    private String skillCategory;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "proficiency", length = 50)
    private String proficiency;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
