package com.growing.app.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "system_design_cases")
@Data
public class SystemDesignCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;  // 关联到系统设计skill (固定为2)

    @Column(length = 500, nullable = false)
    private String title;  // 案例标题(如:设计Twitter)

    @Column(name = "case_description", columnDefinition = "TEXT")
    private String caseDescription;  // 案例描述(Markdown格式)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;  // 难度级别

    @Column(name = "difficulty_rating")
    private Integer difficultyRating;  // 难度评分 1-10

    @Column(name = "company_tags", columnDefinition = "TEXT")
    private String companyTags;  // JSON数组字符串: ["Google", "Meta"]

    @Column(name = "related_focus_areas", columnDefinition = "TEXT")
    private String relatedFocusAreas;  // JSON数组字符串: [1, 5, 12]

    @Column(name = "is_official")
    private Boolean isOfficial = true;  // 是否官方案例

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;  // 创建人

    @Column(name = "display_order")
    private Integer displayOrder = 0;  // 显示顺序

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JSON辅助方法: 公司标签
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getCompanyTagsList() {
        if (companyTags == null || companyTags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(companyTags, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setCompanyTagsList(List<String> tags) {
        try {
            this.companyTags = objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException e) {
            this.companyTags = "[]";
        }
    }

    // JSON辅助方法: 关联Focus Areas
    public List<Long> getRelatedFocusAreasList() {
        if (relatedFocusAreas == null || relatedFocusAreas.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(relatedFocusAreas, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setRelatedFocusAreasList(List<Long> focusAreaIds) {
        try {
            this.relatedFocusAreas = objectMapper.writeValueAsString(focusAreaIds);
        } catch (JsonProcessingException e) {
            this.relatedFocusAreas = "[]";
        }
    }
}
