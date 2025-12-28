package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 技能-模版关联DTO
 * Phase 6: 通用技能学习模块
 */
@Data
public class SkillTemplateDTO {
    private Long skillId;
    private Long templateId;
    private String templateName;  // 从AnswerTemplate关联获取
    private String skillName;     // 从Skill关联获取
    private Boolean isDefault;
    private LocalDateTime createdAt;
}
