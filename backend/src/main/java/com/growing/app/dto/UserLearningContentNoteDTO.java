package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLearningContentNoteDTO {
    private Long id;
    private Long learningContentId;
    private Long userId;
    private String noteContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
