package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserQuestionNoteDTO {

    private Long id;
    private Long questionId;
    private Long userId;
    private String noteContent;
    private String coreStrategy;

    /**
     * 关联的知识点ID列表（已解析为List）
     * Phase 6: 通用技能学习模块
     */
    private List<Long> relatedKnowledgePointIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
