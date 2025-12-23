package com.growing.app.dto;

import lombok.Data;
import java.util.List;

/**
 * Focus Area DTO with category information
 * 用于算法内容管理页面的左侧Focus Area列表
 */
@Data
public class FocusAreaWithCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long skillId;
    private List<String> categoryNames; // 关联的大分类名称列表
    private List<Long> categoryIds;     // 关联的大分类ID列表
}
