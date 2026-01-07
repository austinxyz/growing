package com.growing.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存AI分析结果的请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAnalysisRequest {

    @NotNull(message = "jobId is required")
    private Long jobId;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "analysisResult is required")
    private String analysisResult;  // JSON string from resume-analysis skill
}
