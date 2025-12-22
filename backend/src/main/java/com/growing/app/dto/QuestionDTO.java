package com.growing.app.dto;

import com.growing.app.model.Question;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionDTO {

    private Long id;
    private Long focusAreaId;
    private String focusAreaName;
    private String questionText;
    private Question.Difficulty difficulty;
    private String answerRequirement;
    private String targetPosition;
    private String targetLevel;
    private List<String> redFlags;
    private Boolean isOfficial;
    private Long createdByUserId;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 用户笔记（仅在获取详情时包含）
    private UserQuestionNoteDTO userNote;
}
