package com.growing.app.dto;

import lombok.Data;

/**
 * 试题列表DTO（轻量级，用于列表视图）
 *
 * 性能优化：
 * - 不包含questionDescription（节省带宽）
 * - 不包含用户笔记内容（只包含isPriority和hasUserNote标志）
 * - 用户点击试题时，再通过GET /api/questions/{id}加载完整详情
 */
@Data
public class QuestionListDTO {
    private Long id;
    private String title;
    private String questionType;    // behavioral/technical/design/programming
    private String difficulty;      // EASY/MEDIUM/HARD

    // 用户笔记状态（不包含笔记内容）
    private Boolean hasUserNote;    // 是否有用户笔记
    private Boolean isPriority;     // 是否标记为重点题

    // 关联信息（用于显示面包屑）
    private Long focusAreaId;
    private String focusAreaName;
}
