package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Focus Area与大分类关联实体类
 * Purpose: 管理Focus Area与Major Category的多对多关系
 * Note: 一个Focus Area可以属于多个大分类
 */
@Entity
@Table(name = "focus_area_categories")
@IdClass(FocusAreaCategoryId.class)
@Data
public class FocusAreaCategory {

    @Id
    @Column(name = "focus_area_id")
    private Long focusAreaId;

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_area_id", insertable = false, updatable = false)
    private FocusArea focusArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private MajorCategory category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
