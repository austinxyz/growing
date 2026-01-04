package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// From Referral entity
@Data
public class ReferralDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private String companyName;  // Populated from Company
    private String referralName;
    private String relationship;  // FormerColleague, Classmate, Friend, Mentor, Other
    private String position;
    private String email;
    private String phone;
    private String linkedinUrl;
    private String notes;
    private String referralStatus;  // NotRequested, Requested, Agreed, Declined, Submitted
    private LocalDate requestDate;
    private LocalDate submissionDate;
    private String referralResult;  // InterviewScheduled, NoResponse, Rejected
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
