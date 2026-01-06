package com.growing.app.dto;

import lombok.Data;

/**
 * Brief Focus Area DTO for project experience associations
 * Note: Icon is managed on frontend using focusAreaGuidance.js constants
 */
@Data
public class FocusAreaBriefDTO {
    private Long id;
    private String name;
    private String description;
    private Long skillId;
    private String skillName;  // Populated from associated skill
}
