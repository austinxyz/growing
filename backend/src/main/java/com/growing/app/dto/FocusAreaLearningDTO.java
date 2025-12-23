package com.growing.app.dto;

import lombok.Data;
import java.util.List;

/**
 * 用于返回Focus Area的完整学习内容
 * API: GET /api/learning-contents?focusAreaId=X
 */
@Data
public class FocusAreaLearningDTO {

    private FocusAreaInfo focusArea;
    private List<StageContentDTO> stages;

    @Data
    public static class FocusAreaInfo {
        private Long id;
        private String name;
        private String categoryName; // 大分类名称
    }
}
