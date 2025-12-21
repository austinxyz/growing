package com.growing.app.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SkillDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isImportant;
    private String icon;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FocusAreaDTO> focusAreas;
    private List<LearningResourceDTO> learningResources;

    // 统计字段
    private Long focusAreaCount;
    private Long resourceCount;

    // 关联的职业路径
    private List<CareerPathInfo> careerPaths;
    private List<Long> careerPathIds; // 用于编辑

    // 内部类：职业路径信息
    @Data
    @AllArgsConstructor
    public static class CareerPathInfo {
        private Long id;
        private String name;
    }
}
