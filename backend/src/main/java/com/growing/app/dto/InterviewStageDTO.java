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
    private List<String> skillNames;  // Skill names for display
    private List<Long> focusAreaIds;  // From InterviewStage.focusAreaIds JSON
    private List<FocusAreaBriefDTO> focusAreas;  // Focus Area详细信息（用于显示名称）
    private String preparationNotes;
    private List<InterviewPreparationChecklistDTO> checklistItems;  // 准备清单列表
    private Integer checklistCount;  // 准备清单数量（从interview_preparation_todos表）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
