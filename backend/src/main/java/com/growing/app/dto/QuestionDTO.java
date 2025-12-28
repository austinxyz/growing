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
    private String title;  // 题目标题（如 "[5] Longest Palindromic Substring"）
    private String questionDescription;  // 问题描述（Markdown格式）
    private Question.Difficulty difficulty;
    private String answerRequirement;
    private String targetPosition;
    private String targetLevel;
    private List<String> redFlags;
    private Boolean isOfficial;
    private Long createdByUserId;
    private Integer displayOrder;
    private String questionType;  // 试题类型：behavioral, technical, design, programming
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 用户笔记（仅在获取详情时包含）
    private UserQuestionNoteDTO userNote;

    // AI笔记（user_id = -1的笔记，仅在获取详情时包含）
    private UserQuestionNoteDTO aiNote;

    // 编程题详情（仅在编程题时包含）
    private ProgrammingQuestionDetailsDTO programmingDetails;

    // note字段作为userNote的别名，方便前端使用
    public void setNote(UserQuestionNoteDTO note) {
        this.userNote = note;
    }

    public UserQuestionNoteDTO getNote() {
        return this.userNote;
    }
}
