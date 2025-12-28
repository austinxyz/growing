package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 答题模版实体类
 * Phase 6: 通用技能学习模块
 */
@Entity
@Table(name = "answer_templates")
@Data
public class AnswerTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false, unique = true, length = 100)
    private String templateName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 模版字段定义
     * JSON格式: [{"key":"situation","label":"Situation","placeholder":"描述..."}]
     */
    @Column(name = "template_fields", columnDefinition = "JSON", nullable = false)
    private String templateFields;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
