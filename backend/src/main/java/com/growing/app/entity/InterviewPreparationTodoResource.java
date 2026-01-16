package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 面试准备TODO资源关联实体
 * 支持关联系统内部资源（试题、学习材料、系统设计案例、项目、管理经验）和外部链接
 */
@Entity
@Table(name = "interview_preparation_todo_resources")
@Data
public class InterviewPreparationTodoResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属TODO
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private InterviewPreparationTodo todo;

    /**
     * 资源类型
     * Question - 试题
     * LearningResource - 学习材料
     * SystemDesignCase - 系统设计案例
     * Project - 项目经验
     * ManagementExperience - 人员管理经验
     * ExternalLink - 外部链接
     */
    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    /**
     * 资源ID（外部链接时为NULL）
     */
    @Column(name = "resource_id")
    private Long resourceId;

    /**
     * 外部链接标题（仅当resourceType=ExternalLink时使用）
     */
    @Column(name = "title", length = 500)
    private String title;

    /**
     * 外部链接URL（仅当resourceType=ExternalLink时使用）
     */
    @Column(name = "url", length = 1000)
    private String url;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
