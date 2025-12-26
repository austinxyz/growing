package com.growing.app.dto;

import com.growing.app.model.SolutionDiagram;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolutionDiagramDTO {

    private Long id;
    private Long solutionId;
    private SolutionDiagram.DiagramType diagramType;  // ARCHITECTURE, DATA_FLOW, ENTITY, OTHER
    private String title;
    private String imageUrl;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
