package com.growing.app.service;

import com.growing.app.dto.MajorCategoryDTO;
import com.growing.app.model.MajorCategory;
import com.growing.app.model.Skill;
import com.growing.app.repository.MajorCategoryRepository;
import com.growing.app.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 大分类Service
 */
@Service
public class MajorCategoryService {

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    @Autowired
    private SkillRepository skillRepository;

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
     * 获取指定skill的大分类（按sort_order排序）
     */
    public List<MajorCategoryDTO> getMajorCategoriesBySkillId(Long skillId) {
        return majorCategoryRepository.findBySkillIdOrderBySortOrderAsc(skillId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建大分类
     */
    public MajorCategoryDTO createMajorCategory(MajorCategoryDTO dto) {
        MajorCategory category = new MajorCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSkillId(dto.getSkillId());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        MajorCategory saved = majorCategoryRepository.save(category);
        return convertToDTO(saved);
    }

    /**
     * 更新大分类
     */
    public MajorCategoryDTO updateMajorCategory(Long id, MajorCategoryDTO dto) {
        MajorCategory category = majorCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Major category not found with id: " + id));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        if (dto.getSkillId() != null) {
            category.setSkillId(dto.getSkillId());
        }
        if (dto.getSortOrder() != null) {
            category.setSortOrder(dto.getSortOrder());
        }

        MajorCategory saved = majorCategoryRepository.save(category);
        return convertToDTO(saved);
    }

    /**
     * 删除大分类
     * 规则：
     * 1. 只有通用分类模式技能(isGeneralOnly=true)的 "General" 大分类不允许删除
     * 2. 普通技能(isGeneralOnly=false)的 "General" 大分类可以删除（这种情况本不应该存在）
     * 3. 删除后，focus_area_categories 中的关联会自动删除（级联删除）
     * 4. Focus Area 本身不会被删除，只是失去与该大分类的关联
     * 5. 这些 Focus Area 可以重新关联到其他大分类
     */
    public void deleteMajorCategory(Long id) {
        // 检查大分类是否存在
        MajorCategory category = majorCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "大分类不存在"));

        // 只保护通用分类模式技能的 General 大分类
        if ("General".equals(category.getName())) {
            Skill skill = skillRepository.findById(category.getSkillId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "关联的技能不存在"));

            // 只有 isGeneralOnly=true 的技能才禁止删除 General 分类
            if (skill.getIsGeneralOnly() != null && skill.getIsGeneralOnly()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "不允许删除通用分类模式技能的 General 大分类"
                );
            }
        }

        // 删除大分类
        // focus_area_categories 中的关联会通过数据库级联删除自动删除
        // Focus Area 本身不会被删除
        majorCategoryRepository.deleteById(id);
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
        dto.setSkillId(category.getSkillId());
        return dto;
    }
}
