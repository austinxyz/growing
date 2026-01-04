package com.growing.app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// From ResumeEducation entity
@Data
public class ResumeEducationDTO {
    private Long id;
    private Long resumeId;
    private String schoolName;
    private String degree;  // Bachelor, Master, PhD, Other
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal gpa;
    private String courses;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
