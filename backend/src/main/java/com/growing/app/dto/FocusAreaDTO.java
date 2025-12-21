package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FocusAreaDTO {
    private Long id;
    private Long skillId;
    private String name;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
