package com.growing.app.dto;

import lombok.Data;

/**
 * 大分类DTO
 */
@Data
public class MajorCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Long skillId;
}
