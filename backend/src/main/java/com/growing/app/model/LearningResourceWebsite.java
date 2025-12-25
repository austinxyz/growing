package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 学习资源网站实体类
 * 用于管理不同的学习资源网站配置，包括是否支持iframe嵌入等信息
 */
@Data
@Entity
@Table(name = "learning_resource_websites")
public class LearningResourceWebsite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // 网站名称（如 labuladong, hellointerview）

    @Column(nullable = false, length = 200)
    private String displayName; // 显示名称

    @Column(nullable = false, length = 500)
    private String baseUrl; // 网站起始地址

    @Column(length = 100)
    private String author; // 作者/团队

    @Column(columnDefinition = "TEXT")
    private String description; // 网站介绍/目的

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean supportsIframe = false; // 是否支持iframe嵌入

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
