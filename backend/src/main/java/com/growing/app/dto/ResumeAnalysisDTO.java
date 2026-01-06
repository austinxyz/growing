package com.growing.app.dto;

import com.growing.app.dto.match.*;
import lombok.Data;
import java.util.List;

/**
 * 简历分析结果DTO
 * Phase 7: Job Search Module - Resume Analysis (Multi-dimensional Matching)
 */
@Data
public class ResumeAnalysisDTO {
    private Long jobApplicationId;
    private Long resumeId;
    private Integer matchScore;  // 0-100 (加权平均)

    // ========== 多维度匹配详情 ==========

    /**
     * 教育背景匹配 (权重: 15%)
     */
    private EducationMatchDTO educationMatch;

    /**
     * 工作经验匹配 (权重: 25%)
     */
    private ExperienceMatchDTO experienceMatch;

    /**
     * 技术技能匹配 (权重: 30%)
     */
    private SkillMatchDTO skillMatch;

    /**
     * 职责匹配 (权重: 20%)
     */
    private ResponsibilityMatchDTO responsibilityMatch;

    /**
     * 软技能匹配 (权重: 10%)
     */
    private SoftSkillMatchDTO softSkillMatch;

    // ========== 兼容旧字段（已弃用，保留用于向后兼容） ==========

    @Deprecated
    private List<String> matchedSkills;

    @Deprecated
    private List<String> missingSkills;

    @Deprecated
    private List<String> matchedExperiences;

    // ========== 通用字段 ==========

    // 优势分析
    private List<String> strengths;

    // 需要改进的地方
    private List<String> improvements;

    // 定制化建议（只在匹配度>70%时提供）
    private ResumeCustomizationDTO customization;
}
