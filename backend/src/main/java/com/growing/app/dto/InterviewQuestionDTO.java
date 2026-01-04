package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

// From InterviewQuestion entity
@Data
public class InterviewQuestionDTO {
    private Long id;
    private Long interviewRecordId;
    private Integer questionOrder;
    private String questionDescription;
    private String questionType;  // Coding, SystemDesign, Behavioral, Technical, Other
    private String myAnswer;
    private Long relatedQuestionId;  // Links to questions table
    private Integer answerQuality;  // 1-5
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
