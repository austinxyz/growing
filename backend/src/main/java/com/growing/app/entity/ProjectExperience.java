package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 技术项目经验实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "project_experiences",
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_dates", columnList = "start_date, end_date")
       })
@Data
public class ProjectExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_type", length = 50)
    private String projectType;

    // What/When/Who/Why
    @Column(name = "what_description", columnDefinition = "TEXT")
    private String whatDescription;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "my_role")
    private String myRole;

    @Column(name = "background", columnDefinition = "TEXT")
    private String background;

    // Problem Statement & Challenges
    @Column(name = "problem_statement", columnDefinition = "TEXT")
    private String problemStatement;

    @Column(name = "challenges", columnDefinition = "TEXT")
    private String challenges;

    @Column(name = "constraints", columnDefinition = "TEXT")
    private String constraints;

    // How
    @Column(name = "tech_stack", columnDefinition = "TEXT")
    private String techStack;

    @Column(name = "architecture", columnDefinition = "TEXT")
    private String architecture;

    @Column(name = "innovation", columnDefinition = "TEXT")
    private String innovation;

    @Column(name = "my_contribution", columnDefinition = "TEXT")
    private String myContribution;

    // Result
    @Column(name = "quantitative_results", columnDefinition = "TEXT")
    private String quantitativeResults;

    @Column(name = "business_impact", columnDefinition = "TEXT")
    private String businessImpact;

    @Column(name = "personal_growth", columnDefinition = "TEXT")
    private String personalGrowth;

    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;

    // Tags and Classification
    @Column(name = "tech_tags", columnDefinition = "JSON")
    private String techTags;

    @Column(name = "skill_ids", columnDefinition = "JSON")
    private String skillIds;

    @Column(name = "difficulty", length = 50)
    private String difficulty;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
