package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

// From ResumeSkill entity
@Data
public class ResumeSkillDTO {
    private Long id;
    private Long resumeId;
    private String skillCategory;  // Technical, Soft, Tools
    private String skillName;
    private String proficiency;  // Beginner, Intermediate, Advanced, Expert
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
