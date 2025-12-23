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
@Table(name = "questions")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_area_id", nullable = false)
    private FocusArea focusArea;

    @Column(name = "title", length = 500, nullable = false)
    private String title;  // 题目标题

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;  // 详细描述

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(name = "answer_requirement", columnDefinition = "TEXT")
    private String answerRequirement;

    @Column(name = "target_position", length = 100)
    private String targetPosition;

    @Column(name = "target_level", length = 50)
    private String targetLevel;

    @Column(name = "red_flags", columnDefinition = "TEXT")
    private String redFlags; // JSON数组字符串

    @Column(name = "is_official")
    private Boolean isOfficial = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 枚举定义
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    // Red Flags JSON序列化/反序列化辅助方法
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getRedFlagsList() {
        if (redFlags == null || redFlags.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(redFlags, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setRedFlagsList(List<String> redFlagsList) {
        if (redFlagsList == null || redFlagsList.isEmpty()) {
            this.redFlags = null;
            return;
        }
        try {
            this.redFlags = objectMapper.writeValueAsString(redFlagsList);
        } catch (JsonProcessingException e) {
            this.redFlags = null;
        }
    }
}
