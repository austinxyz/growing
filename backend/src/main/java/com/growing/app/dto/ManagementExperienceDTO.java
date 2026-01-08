package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// From ManagementExperience entity
@Data
public class ManagementExperienceDTO {
    private Long id;
    private Long userId;
    private Long focusAreaId;  // Focus Area ID (关联人员管理的Focus Area)
    private String experienceName;
    private String teamGrowthSubtype;  // Hiring, HighPerformer, LowPerformer (only for TeamGrowth)

    private LocalDate startDate;
    private LocalDate endDate;

    private String background;
    private String actionsTaken;
    private String results;
    private String lessonsLearned;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
