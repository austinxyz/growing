package com.growing.app.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教育背景匹配结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationMatchDTO {

    /**
     * 是否满足学位要求
     */
    private Boolean hasRequiredDegree;

    /**
     * 专业相关度 (0.0 - 1.0)
     */
    private Double majorRelevance;

    /**
     * 最高学历
     */
    private String highestDegree;

    /**
     * 专业名称
     */
    private String major;

    /**
     * 匹配分数 (0-100)
     */
    private Integer score;

    /**
     * 匹配说明
     */
    private String explanation;
}
