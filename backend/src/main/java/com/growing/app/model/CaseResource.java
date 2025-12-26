package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_resources")
@Data
public class CaseResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private SystemDesignCase systemDesignCase;  // 关联的案例

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;  // 资源类型: VIDEO, ARTICLE

    @Column(length = 500, nullable = false)
    private String title;  // 资源标题

    @Column(length = 1000, nullable = false)
    private String url;  // 资源URL

    @Column(columnDefinition = "TEXT")
    private String description;  // 资源描述

    @Column(name = "display_order")
    private Integer displayOrder = 0;  // 显示顺序

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ResourceType {
        VIDEO,
        ARTICLE
    }
}
