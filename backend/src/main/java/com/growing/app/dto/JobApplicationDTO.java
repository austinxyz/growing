package com.growing.app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// From JobApplication entity + nested resources
@Data
public class JobApplicationDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private String companyName;  // Populated from Company
    private String positionName;
    private String positionLevel;
    private LocalDate postedDate;
    private String jobUrl;

    // JD Core Content
    private String qualifications;
    private String responsibilities;

    // Application Status
    private String applicationStatus;  // NotApplied, Applied, PhoneScreen, Onsite, Offer, Rejected
    private LocalDateTime statusUpdatedAt;
    private List<StatusHistoryDTO> statusHistory;  // From JobApplication.statusHistory JSON

    // Offer Details
    private LocalDateTime offerReceivedAt;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal stockValue;
    private BigDecimal totalCompensation;
    private LocalDate offerDeadline;
    private String offerDecision;  // Accepted, Declined, Pending
    private String offerNotes;

    // Rejection Details
    private LocalDateTime rejectedAt;
    private String rejectedStage;
    private List<String> rejectionReasons;  // From JobApplication.rejectionReasons JSON
    private String improvementPlan;

    private String notes;

    // Nested collections (DTO Completeness Checklist: all collections populated)
    private List<InterviewStageDTO> stages;
    private List<InterviewRecordDTO> interviews;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class StatusHistoryDTO {
        private String status;
        private LocalDateTime timestamp;
    }
}
