package com.growing.app.dto;

import com.growing.app.model.CaseResource;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseResourceDTO {

    private Long id;
    private Long caseId;
    private CaseResource.ResourceType resourceType;  // VIDEO, ARTICLE
    private String title;
    private String url;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
