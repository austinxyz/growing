package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 学习内容实体类
 * Purpose: 统一管理所有类型的学习内容（文章、视频、代码、模版等）
 * Note: focus_area_id为NULL时表示算法模版（不关联Focus Area）
 */
@Entity
@Table(name = "learning_contents")
@Data
public class LearningContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_area_id")
    private FocusArea focusArea; // NULL for algorithm templates

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    private LearningStage stage; // NULL for algorithm templates

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 1000)
    private String url; // 外部资源链接

    @Column(length = 100)
    private String author;

    @Column(name = "content_data", columnDefinition = "JSON")
    private String contentData; // JSON格式扩展数据

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Convert(converter = VisibilityConverter.class)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 内容类型枚举
    // 注意：枚举名称必须与数据库ENUM值一致（小写+下划线）
    public enum ContentType {
        article,        // 文章/博客
        video,          // 视频教程
        code_example,   // 代码示例
        template,       // 算法模版
        case_study      // 案例分析（系统设计用）
    }

    // 可见性枚举
    // 注意：枚举名称必须与数据库ENUM值一致（小写）
    // 由于public/private是Java关键字，使用大写形式并配置PostLoad转换
    public enum Visibility {
        PUBLIC,         // 所有用户可见
        PRIVATE         // 仅创建者可见（预留）
    }

    // Visibility枚举转换器：数据库存小写，Java用大写
    @jakarta.persistence.Converter
    public static class VisibilityConverter implements jakarta.persistence.AttributeConverter<Visibility, String> {
        @Override
        public String convertToDatabaseColumn(Visibility visibility) {
            return visibility == null ? null : visibility.name().toLowerCase();
        }

        @Override
        public Visibility convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Visibility.valueOf(dbData.toUpperCase());
        }
    }
}
