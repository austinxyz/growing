package com.growing.app.dto;

import com.growing.app.model.LearningResource.ResourceType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningResourceDTO {
    private Long id;
    private Long skillId;
    private ResourceType resourceType;
    private String title;
    private String url;
    private String author;
    private String description;
    private Boolean isOfficial;
    private Long createdByUserId;
    private String createdByUsername;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
