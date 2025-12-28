package com.growing.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerPathWithSkillsDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillDTO> skills;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDTO {
        private Long id;
        private String name;
        private String description;
        private String icon;
        private Integer displayOrder;
    }
}
