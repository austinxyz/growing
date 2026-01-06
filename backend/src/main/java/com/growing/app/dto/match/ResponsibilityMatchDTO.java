package com.growing.app.dto.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 职责匹配结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibilityMatchDTO {

    /**
     * 匹配的职责数量
     */
    private Integer matchedCount;

    /**
     * 总职责数量
     */
    private Integer totalCount;

    /**
     * 匹配的职责关键词
     */
    private List<String> matchedKeywords;

    /**
     * 匹配分数 (0-100)
     */
    private Integer score;

    /**
     * 匹配说明
     */
    private String explanation;
}
