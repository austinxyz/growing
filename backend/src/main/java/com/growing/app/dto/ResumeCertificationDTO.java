package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// From ResumeCertification entity
@Data
public class ResumeCertificationDTO {
    private Long id;
    private Long resumeId;
    private String certName;
    private String issuer;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String certUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
