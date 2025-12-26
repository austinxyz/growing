package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "solution_diagrams")
@Data
public class SolutionDiagram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    private CaseSolution caseSolution;  // 关联的参考答案

    @Enumerated(EnumType.STRING)
    @Column(name = "diagram_type", nullable = false)
    private DiagramType diagramType;  // 配图类型

    @Column(length = 200)
    private String title;  // 配图标题

    @Column(name = "image_url", length = 1000, nullable = false)
    private String imageUrl;  // 图片URL

    @Column(columnDefinition = "TEXT")
    private String description;  // 配图说明

    @Column(name = "display_order")
    private Integer displayOrder = 0;  // 显示顺序

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum DiagramType {
        ARCHITECTURE,  // 架构图
        DATA_FLOW,     // 数据流图
        ENTITY,        // 实体图
        OTHER          // 其他
    }
}
