package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuestionNoteDTO {

    private Long id;
    private Long questionId;
    private Long userId;
    private String noteContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
