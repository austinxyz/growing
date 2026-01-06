package com.growing.app.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作经验匹配结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceMatchDTO {

    /**
     * 总工作年限
     */
    private Integer totalYears;

    /**
     * 管理经验年限
     */
    private Integer managementYears;

    /**
     * 是否满足最低年限要求
     */
    private Boolean meetsMinimum;

    /**
     * JD要求的年限
     */
    private String requiredYears;

    /**
     * 匹配分数 (0-100)
     */
    private Integer score;

    /**
     * 匹配说明
     */
    private String explanation;
}
