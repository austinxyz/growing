package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// From InterviewRecord entity + nested resources
@Data
public class InterviewRecordDTO {
    private Long id;
    private Long jobApplicationId;
    private Long interviewStageId;
    private String stageName;  // Populated from InterviewStage

    private LocalDateTime interviewDate;
    private String interviewerName;
    private String interviewerPosition;
    private String interviewerLinkedin;
    private String interviewFormat;  // VideoCall, InPerson, Phone
    private Integer interviewDuration;

    // Self Assessment
    private Integer overallPerformance;
    private Integer technicalDepth;
    private Integer communication;
    private Integer problemSolving;
    private String selfSummary;

    // Nested collections (DTO Completeness Checklist: all collections populated)
    private List<InterviewQuestionDTO> questions;
    private List<InterviewFeedbackDTO> feedback;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
