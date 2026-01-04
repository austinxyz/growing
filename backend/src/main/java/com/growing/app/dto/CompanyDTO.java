package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// From Company entity + nested resources
@Data
public class CompanyDTO {
    private Long id;
    private Long userId;
    private String companyName;
    private String companyDescription;
    private String companyCulture;
    private String location;
    private String companySize;
    private String industry;

    // Nested collections (DTO Completeness Checklist: all collections populated)
    private List<CompanyLinkDTO> links;
    private List<JobApplicationDTO> jobs;
    private List<RecruiterDTO> recruiters;
    private List<ReferralDTO> referrals;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
