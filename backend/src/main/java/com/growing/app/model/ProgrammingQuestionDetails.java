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
import java.util.Map;

/**
 * 编程题专属字段实体类
 * Purpose: 存储编程题的专属信息（LeetCode链接、算法标签等）
 * Note: 与Question表一对一关系
 */
@Entity
@Table(name = "programming_question_details")
@Data
public class ProgrammingQuestionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;

    @Column(name = "leetcode_url", length = 500)
    private String leetcodeUrl;

    @Column(name = "labuladong_url", length = 500)
    private String labuladongUrl;

    @Column(name = "hellointerview_url", length = 500)
    private String hellointerviewUrl;

    @Column(name = "is_important")
    private Boolean isImportant = false;

    @Column(columnDefinition = "JSON")
    private String tags; // JSON数组: ["双指针", "滑动窗口"]

    @Column(length = 100)
    private String complexity; // 如: "时间O(n), 空间O(1)"

    @Column(name = "similar_questions", columnDefinition = "JSON")
    private String similarQuestions; // JSON数组: [{"id": 15, "title": "3Sum"}]

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JSON序列化/反序列化辅助方法
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Tags辅助方法
    public List<String> getTagsList() {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(tags, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setTagsList(List<String> tagsList) {
        if (tagsList == null || tagsList.isEmpty()) {
            this.tags = null;
            return;
        }
        try {
            this.tags = objectMapper.writeValueAsString(tagsList);
        } catch (JsonProcessingException e) {
            this.tags = null;
        }
    }

    // Similar Questions辅助方法
    public List<Map<String, Object>> getSimilarQuestionsList() {
        if (similarQuestions == null || similarQuestions.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(similarQuestions, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public void setSimilarQuestionsList(List<Map<String, Object>> similarQuestionsList) {
        if (similarQuestionsList == null || similarQuestionsList.isEmpty()) {
            this.similarQuestions = null;
            return;
        }
        try {
            this.similarQuestions = objectMapper.writeValueAsString(similarQuestionsList);
        } catch (JsonProcessingException e) {
            this.similarQuestions = null;
        }
    }
}
