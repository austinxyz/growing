package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试准备清单DTO
 * Phase 7: 求职管理模块 - Enhancements
 */
@Data
public class InterviewPreparationChecklistDTO {

    private Long id;

    private Long interviewStageId;

    /**
     * 准备项内容
     */
    private String checklistItem;

    /**
     * 是否为重点项（用于打印1-2页重点材料）
     */
    private Boolean isPriority;

    /**
     * 分类：Study, Practice, Research, Review, Other
     */
    private String category;

    /**
     * 备注说明（Markdown）
     */
    private String notes;

    /**
     * 排序
     */
    private Integer sortOrder;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
