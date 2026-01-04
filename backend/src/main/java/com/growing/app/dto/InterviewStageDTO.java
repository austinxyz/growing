package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// From InterviewStage entity
@Data
public class InterviewStageDTO {
    private Long id;
    private Long jobApplicationId;
    private String stageName;
    private Integer stageOrder;
    private List<Long> skillIds;  // From InterviewStage.skillIds JSON
    private String preparationNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
