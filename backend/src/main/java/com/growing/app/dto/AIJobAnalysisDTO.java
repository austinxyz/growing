package com.growing.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI驱动的JD-Resume匹配分析DTO
 *
 * 用于返回AI分析结果，包含：
 * - 生成的Prompt
 * - AI分析结果（Markdown）
 * - 解析后的元数据（JSON）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIJobAnalysisDTO {

    private Long id;
    private Long jobApplicationId;
    private Long resumeId;

    // Prompt & Analysis
    private String generatedPrompt;
    private String aiAnalysisResult;  // Markdown format

    // Parsed Metadata
    private AnalysisMetadata metadata;

    // Status
    private String status;  // "prompt_generated", "completed"

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 分析元数据（从AI结果中解析或用户手动输入）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisMetadata {
        private Integer skillMatchScore;       // 0-100
        private Integer experienceMatchScore;  // 0-100
        private Integer overallScore;          // 0-100
        private List<String> keyStrengths;
        private List<String> keyWeaknesses;
        private String recommendation;         // "Strong Match", "Good Match", "Fair Match", "Poor Match"
    }
}
