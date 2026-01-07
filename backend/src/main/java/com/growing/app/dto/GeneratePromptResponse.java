package com.growing.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成AI分析Prompt的响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePromptResponse {

    private String prompt;  // 生成的完整Prompt
    private String setupInstructions;  // 首次使用的配置说明
    private Long jobId;
    private Long userId;
}
