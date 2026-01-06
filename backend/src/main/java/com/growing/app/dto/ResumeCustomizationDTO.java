package com.growing.app.dto;

import lombok.Data;
import java.util.List;

/**
 * 简历定制化建议DTO
 * Phase 7: Job Search Module - Resume Customization
 */
@Data
public class ResumeCustomizationDTO {
    // 关键词优化建议
    private List<KeywordSuggestion> keywordSuggestions;

    // 项目经验优化建议
    private List<ProjectOptimization> projectOptimizations;

    // 技能突出建议
    private List<SkillHighlight> skillHighlights;

    // 整体结构建议
    private List<String> structuralSuggestions;

    @Data
    public static class KeywordSuggestion {
        private String keyword;  // 需要添加的关键词
        private String section;  // 建议添加到哪个部分
        private String reason;   // 原因
    }

    @Data
    public static class ProjectOptimization {
        private Long projectId;
        private String projectName;
        private List<String> suggestions;  // 具体优化建议
    }

    @Data
    public static class SkillHighlight {
        private String skill;
        private String currentMention;  // 当前提及方式
        private String suggestedMention; // 建议提及方式
    }
}
