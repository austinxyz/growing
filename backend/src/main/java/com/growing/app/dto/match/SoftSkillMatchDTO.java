package com.growing.app.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 软技能匹配结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftSkillMatchDTO {

    /**
     * 展示的软技能列表
     */
    private List<String> demonstratedSkills;

    /**
     * 缺失的软技能列表
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
