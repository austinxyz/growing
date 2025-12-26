package com.growing.app.dto;

import com.growing.app.model.Difficulty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SystemDesignCaseDTO {

    private Long id;
    private Long skillId;
    private String skillName;

    private String title;  // 案例标题(如:设计Twitter)
    private String caseDescription;  // 案例描述(Markdown格式)
    private Difficulty difficulty;  // 难度级别
    private Integer difficultyRating;  // 难度评分 1-10

    private List<String> companyTags;  // 公司标签
    private List<Long> relatedFocusAreas;  // 关联的Focus Area IDs

    private Boolean isOfficial;  // 是否官方案例
    private Long createdByUserId;
    private String createdByUsername;
    private Integer displayOrder;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联数据（根据API需求按需包含）
    private List<CaseResourceDTO> resources;  // 学习资源列表
    private List<CaseSolutionDTO> solutions;  // 参考答案列表（支持多方案）
    private UserCaseNoteDTO userNote;  // 用户答题记录（仅用户端包含）
}
