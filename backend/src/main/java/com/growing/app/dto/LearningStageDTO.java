package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LearningStageDTO {

    private Long id;
    private Long skillId;
    private String stageName;
    private String stageType;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;

    // 阶段下的学习内容列表（仅在详情查询时包含）
    private List<LearningContentDTO> contents;
}
