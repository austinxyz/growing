package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLearningContentKnowledgePointDTO {
    private Long id;
    private Long learningContentId;
    private Long userId;
    private String title;
    private String summary;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
