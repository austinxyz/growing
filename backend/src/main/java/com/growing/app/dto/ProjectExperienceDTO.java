package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// From ProjectExperience entity
@Data
public class ProjectExperienceDTO {
    private Long id;
    private Long userId;
    private String projectName;
    private String projectType;  // Feature, Optimization, Migration, BugFix, Infrastructure, Other

    // What/When/Who/Why
    private String whatDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer teamSize;
    private String myRole;
    private String background;

    // Problem Statement & Challenges
    private String problemStatement;
    private String challenges;
    private String constraints;

    // How
    private String techStack;
    private String architecture;
    private String innovation;
    private String myContribution;

    // Result
    private String quantitativeResults;
    private String businessImpact;
    private String personalGrowth;
    private String lessonsLearned;

    // Tags and Classification
    private List<String> techTags;  // From ProjectExperience.techTags JSON
    private List<Long> focusAreaIds;  // Behavioral Focus Area IDs (input)
    private List<FocusAreaBriefDTO> focusAreas;  // Complete Focus Area details with skill info (output)
    private List<Long> skillIds;  // Dynamically calculated from focusAreas (output only)
    private String difficulty;  // Low, Medium, High, Critical

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
