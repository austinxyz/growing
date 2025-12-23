package com.growing.app.service;

import com.growing.app.dto.MajorCategoryDTO;
import com.growing.app.model.MajorCategory;
import com.growing.app.repository.MajorCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 大分类Service
 */
@Service
public class MajorCategoryService {

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    /**
     * 获取所有大分类（按sort_order排序）
     */
    public List<MajorCategoryDTO> getAllMajorCategories() {
        return majorCategoryRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     */
    private MajorCategoryDTO convertToDTO(MajorCategory category) {
        MajorCategoryDTO dto = new MajorCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        return dto;
    }
}
