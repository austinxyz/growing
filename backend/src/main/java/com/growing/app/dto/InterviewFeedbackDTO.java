package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

// From InterviewFeedback entity
@Data
public class InterviewFeedbackDTO {
    private Long id;
    private Long interviewRecordId;
    private LocalDateTime feedbackDate;
    private String feedbackSource;  // Recruiter, HiringManager, etc.
    private String feedbackContent;
    private LocalDateTime createdAt;
}
