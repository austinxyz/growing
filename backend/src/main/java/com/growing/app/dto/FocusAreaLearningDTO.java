package com.growing.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class FocusAreaLearningDTO {

    private FocusAreaInfo focusArea;
    private List<StageContentDTO> stages;

    @Data
    public static class FocusAreaInfo {
        private Long id;
        private String name;
        private String categoryName;
    }
}
