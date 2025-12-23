package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 学习阶段实体类
 * Purpose: 为每个Skill定义自定义的学习阶段序列
 * Examples: 编程(基础原理→实现代码→实战题目), 系统设计(案例→模版→权衡)
 */
@Entity
@Table(name = "learning_stages",
       uniqueConstraints = @UniqueConstraint(name = "uk_skill_stage",
                                            columnNames = {"skill_id", "stage_name"}))
@Data
public class LearningStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "stage_name", nullable = false, length = 50)
    private String stageName;

    @Column(name = "stage_type", nullable = false, length = 50)
    private String stageType; // theory, implementation, practice, case_study, etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
