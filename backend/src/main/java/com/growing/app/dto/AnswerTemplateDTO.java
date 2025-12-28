package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 答题模版DTO
 * Phase 6: 通用技能学习模块
 */
@Data
public class AnswerTemplateDTO {
    private Long id;
    private String templateName;
    private String description;

    /**
     * 模版字段列表（已解析为对象列表）
     */
    private List<TemplateField> templateFields;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 模版字段定义
     */
    @Data
    public static class TemplateField {
        private String key;
        private String label;
        private String placeholder;
    }
}
