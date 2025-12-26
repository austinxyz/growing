package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FocusAreaDTO {
    private Long id;
    private Long skillId;
    private String name;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 所属的Major Category IDs (仅算法与数据结构、系统设计skill使用)
    private List<Long> categoryIds;
}
