package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_resources")
@Data
public class LearningResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String url;

    @Column(length = 100)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_official")
    private Boolean isOfficial = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ResourceType {
        BOOK,           // 书籍
        COURSE,         // 课程
        WEBSITE,        // 网站
        ARTICLE,        // 文章
        VIDEO,          // 视频
        DOCUMENT,       // 文档
        BLOG,           // 博客
        PRACTICE,       // 练习
        TOOL,           // 工具
        OTHER           // 其他
    }
}
