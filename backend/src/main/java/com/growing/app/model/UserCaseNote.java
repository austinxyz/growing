package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_case_notes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_user_case",
            columnNames = {"case_id", "user_id"}
        )
    }
)
@Data
public class UserCaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private SystemDesignCase systemDesignCase;  // 关联的案例

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 用户

    // 结构化答案字段（Key Points）
    @Column(name = "kp_requirement", columnDefinition = "TEXT")
    private String kpRequirement;  // 需求分析

    @Column(name = "kp_nfr", columnDefinition = "TEXT")
    private String kpNfr;  // 非功能性需求

    @Column(name = "kp_entity", columnDefinition = "TEXT")
    private String kpEntity;  // 核心实体定义

    @Column(name = "kp_components", columnDefinition = "TEXT")
    private String kpComponents;  // 关键组件

    @Column(name = "kp_api", columnDefinition = "TEXT")
    private String kpApi;  // API设计

    @Column(name = "kp_object1", columnDefinition = "TEXT")
    private String kpObject1;  // 核心对象1设计

    @Column(name = "kp_object2", columnDefinition = "TEXT")
    private String kpObject2;  // 核心对象2设计

    // 用户答案（6个步骤，Markdown格式）
    @Column(name = "step1_requirements", columnDefinition = "TEXT")
    private String step1Requirements;  // 步骤1:需求澄清与功能列表

    @Column(name = "step2_entities", columnDefinition = "TEXT")
    private String step2Entities;  // 步骤2:核心实体定义

    @Column(name = "step3_api", columnDefinition = "TEXT")
    private String step3Api;  // 步骤3:API设计

    @Column(name = "step4_data_flow", columnDefinition = "TEXT")
    private String step4DataFlow;  // 步骤4:数据流设计

    @Column(name = "step5_architecture", columnDefinition = "TEXT")
    private String step5Architecture;  // 步骤5:高层架构设计

    @Column(name = "step6_deep_dive", columnDefinition = "TEXT")
    private String step6DeepDive;  // 步骤6:深入讨论

    // 用户架构图
    @Column(name = "architecture_diagram_url", length = 1000)
    private String architectureDiagramUrl;  // 用户上传的架构图URL

    // 要点总结
    @Column(name = "key_points", columnDefinition = "TEXT")
    private String keyPoints;  // 要点总结(Markdown格式)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
