package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// From Recruiter entity + nested resources
@Data
public class RecruiterDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private String companyName;  // Populated from Company
    private String recruiterName;
    private String email;
    private String phone;
    private String linkedinUrl;
    private String notes;

    // Nested collections (DTO Completeness Checklist: all collections populated)
    private List<RecruiterCommunicationDTO> communications;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
