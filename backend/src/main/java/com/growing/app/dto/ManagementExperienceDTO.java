package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// From ManagementExperience entity
@Data
public class ManagementExperienceDTO {
    private Long id;
    private Long userId;
    private String experienceName;
    private String experienceType;  // ManageUp, CrossTeam, TeamGrowth
    private String teamGrowthSubtype;  // Hiring, HighPerformer, LowPerformer (only for TeamGrowth)

    private LocalDate startDate;
    private LocalDate endDate;

    private String background;
    private String actionsTaken;
    private String results;
    private String lessonsLearned;

    // Team Growth specific fields
    private Integer hiringCount;
    private String improvementResult;  // Improved, Terminated (only for LowPerformer)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
