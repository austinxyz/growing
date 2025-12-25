package com.growing.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class StageContentDTO {
    private Long id;
    private String stageName;
    private String stageType;
    private String description;
    private Integer sortOrder;
    private List<LearningContentDTO> contents;
}
