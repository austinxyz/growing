package com.growing.app.dto;

import lombok.Data;
import java.util.List;

/**
 * 学习阶段及其内容的组合DTO
 * 用于FocusAreaLearningDTO的stages字段
 */
@Data
public class StageContentDTO {

    private Long id;
    private String stageName;
    private String stageType;
    private String description;
    private Integer sortOrder;
    private List<LearningContentDTO> contents;
}
