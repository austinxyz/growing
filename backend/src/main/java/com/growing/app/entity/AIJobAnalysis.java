package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * AI驱动的JD-Resume匹配分析记录
 *
 * 存储用户生成的分析Prompt和AI返回的分析结果
 */
@Entity
@Table(name = "ai_job_analysis",
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_job", columnList = "job_application_id"),
                @Index(name = "idx_resume", columnList = "resume_id"),
                @Index(name = "idx_status", columnList = "status")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIJobAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "generated_prompt", nullable = false, columnDefinition = "TEXT")
    private String generatedPrompt;

    @Column(name = "ai_analysis_result", columnDefinition = "TEXT")
    private String aiAnalysisResult;

    @Column(name = "analysis_metadata", columnDefinition = "JSON")
    private String analysisMetadata;  // JSON string

    @Column(name = "status", length = 50)
    private String status = "prompt_generated";  // "prompt_generated", "completed"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
