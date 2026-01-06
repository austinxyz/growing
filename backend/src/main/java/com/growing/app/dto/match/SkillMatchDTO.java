package com.growing.app.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 技能匹配结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillMatchDTO {

    /**
     * 匹配的技能列表
     */
    private List<String> matchedSkills;

    /**
     * 缺失的技能列表
     */
    private List<String> missingSkills;

    /**
     * 匹配分数 (0-100)
     */
    private Integer score;

    /**
     * 匹配说明
     */
    private String explanation;
}
