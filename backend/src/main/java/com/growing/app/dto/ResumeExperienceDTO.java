package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// From ResumeExperience entity
@Data
public class ResumeExperienceDTO {
    private Long id;
    private Long resumeId;
    private String companyName;
    private String position;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String responsibilities;
    private String achievements;
    private List<Long> projectIds;  // From ResumeExperience.projectIds JSON
    private List<String> projectNames;  // 项目名称列表（用于显示）
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
