package com.growing.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成AI分析Prompt的请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePromptRequest {

    @NotNull(message = "jobApplicationId is required")
    private Long jobApplicationId;
}
