package com.growing.app.service;

import com.growing.app.dto.FocusAreaDTO;
import com.growing.app.dto.FocusAreaWithCategoryDTO;
import com.growing.app.model.FocusArea;
import com.growing.app.model.FocusAreaCategory;
import com.growing.app.model.MajorCategory;
import com.growing.app.model.Skill;
import com.growing.app.repository.FocusAreaCategoryRepository;
import com.growing.app.repository.FocusAreaRepository;
import com.growing.app.repository.MajorCategoryRepository;
import com.growing.app.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FocusAreaService {

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private FocusAreaCategoryRepository focusAreaCategoryRepository;

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    // 获取技能下的所有Focus Areas
    public List<FocusAreaDTO> getFocusAreasBySkillId(Long skillId) {
        return focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取技能下的所有Focus Areas（包含分类信息）
    public List<FocusAreaWithCategoryDTO> getFocusAreasWithCategoriesBySkillId(Long skillId) {
        List<FocusArea> focusAreas = focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skillId);

        return focusAreas.stream()
                .map(this::convertToWithCategoryDTO)
                .collect(Collectors.toList());
    }

    // 获取技能下未分类的Focus Areas
    public List<FocusAreaWithCategoryDTO> getUncategorizedFocusAreasBySkillId(Long skillId) {
        List<FocusArea> focusAreas = focusAreaRepository.findUncategorizedBySkillId(skillId);

        return focusAreas.stream()
                .map(this::convertToWithCategoryDTO)
                .collect(Collectors.toList());
    }

    // 创建Focus Area（管理员）
    @Transactional
    public FocusAreaDTO createFocusArea(FocusAreaDTO dto) {
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        FocusArea focusArea = new FocusArea();
        focusArea.setSkill(skill);
        focusArea.setName(dto.getName());
        focusArea.setDescription(dto.getDescription());
        focusArea.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        focusArea = focusAreaRepository.save(focusArea);
        return convertToDTO(focusArea);
    }

    // 更新Focus Area（管理员）
    @Transactional
    public FocusAreaDTO updateFocusArea(Long id, FocusAreaDTO dto) {
        FocusArea focusArea = focusAreaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "专注领域不存在"));

        focusArea.setName(dto.getName());
        focusArea.setDescription(dto.getDescription());
        focusArea.setDisplayOrder(dto.getDisplayOrder());

        // 如果提供了新的skillId，则更新所属技能
        if (dto.getSkillId() != null && !dto.getSkillId().equals(focusArea.getSkill().getId())) {
            Skill newSkill = skillRepository.findById(dto.getSkillId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));
            focusArea.setSkill(newSkill);
        }

        focusArea = focusAreaRepository.save(focusArea);
        return convertToDTO(focusArea);
    }

    // 删除Focus Area（管理员）
    @Transactional
    public void deleteFocusArea(Long id) {
        if (!focusAreaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "专注领域不存在");
        }
        focusAreaRepository.deleteById(id);
    }

    // 创建Focus Area（包含分类信息）
    @Transactional
    public FocusAreaWithCategoryDTO createFocusArea(FocusAreaWithCategoryDTO dto) {
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        FocusArea focusArea = new FocusArea();
        focusArea.setSkill(skill);
        focusArea.setName(dto.getName());
        focusArea.setDescription(dto.getDescription());
        focusArea.setDisplayOrder(0); // Default display order

        focusArea = focusAreaRepository.save(focusArea);

        // 如果技能是通用分类模式（isGeneralOnly=true），自动关联到 General 大分类
        if (skill.getIsGeneralOnly() != null && skill.getIsGeneralOnly()) {
            // 查找或确保 General 大分类存在
            List<MajorCategory> categories = majorCategoryRepository.findBySkillIdOrderBySortOrderAsc(skill.getId());
            MajorCategory generalCategory = categories.stream()
                    .filter(c -> "General".equals(c.getName()))
                    .findFirst()
                    .orElse(null);

            if (generalCategory == null) {
                // 创建 General 大分类
                generalCategory = new MajorCategory();
                generalCategory.setSkillId(skill.getId());
                generalCategory.setName("General");
                generalCategory.setDescription("通用分类");
                generalCategory.setSortOrder(0);
                generalCategory = majorCategoryRepository.save(generalCategory);
            }

            // 关联到 General 大分类
            FocusAreaCategory fac = new FocusAreaCategory();
            fac.setFocusAreaId(focusArea.getId());
            fac.setCategoryId(generalCategory.getId());
            focusAreaCategoryRepository.save(fac);
        } else if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            // 非通用分类模式，使用提供的分类信息
            for (Long categoryId : dto.getCategoryIds()) {
                FocusAreaCategory fac = new FocusAreaCategory();
                fac.setFocusAreaId(focusArea.getId());
                fac.setCategoryId(categoryId);
                focusAreaCategoryRepository.save(fac);
            }
        }

        return convertToWithCategoryDTO(focusArea);
    }

    // 更新Focus Area（包含分类信息）
    @Transactional
    public FocusAreaWithCategoryDTO updateFocusArea(Long id, FocusAreaWithCategoryDTO dto) {
        FocusArea focusArea = focusAreaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "专注领域不存在"));

        focusArea.setName(dto.getName());
        focusArea.setDescription(dto.getDescription());

        // 如果提供了新的skillId，则更新所属技能
        if (dto.getSkillId() != null && !dto.getSkillId().equals(focusArea.getSkill().getId())) {
            Skill newSkill = skillRepository.findById(dto.getSkillId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));
            focusArea.setSkill(newSkill);
        }

        focusArea = focusAreaRepository.save(focusArea);

        // 更新分类关联：先删除现有关联，再创建新关联
        focusAreaCategoryRepository.deleteByFocusAreaId(focusArea.getId());

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            for (Long categoryId : dto.getCategoryIds()) {
                FocusAreaCategory fac = new FocusAreaCategory();
                fac.setFocusAreaId(focusArea.getId());
                fac.setCategoryId(categoryId);
                focusAreaCategoryRepository.save(fac);
            }
        }

        return convertToWithCategoryDTO(focusArea);
    }

    // DTO转换
    private FocusAreaDTO convertToDTO(FocusArea focusArea) {
        FocusAreaDTO dto = new FocusAreaDTO();
        dto.setId(focusArea.getId());
        dto.setSkillId(focusArea.getSkill().getId());
        dto.setName(focusArea.getName());
        dto.setDescription(focusArea.getDescription());
        dto.setDisplayOrder(focusArea.getDisplayOrder());
        dto.setCreatedAt(focusArea.getCreatedAt());
        dto.setUpdatedAt(focusArea.getUpdatedAt());
        return dto;
    }

    // DTO转换（包含分类信息）
    private FocusAreaWithCategoryDTO convertToWithCategoryDTO(FocusArea focusArea) {
        FocusAreaWithCategoryDTO dto = new FocusAreaWithCategoryDTO();
        dto.setId(focusArea.getId());
        dto.setName(focusArea.getName());
        dto.setDescription(focusArea.getDescription());
        dto.setSkillId(focusArea.getSkill().getId());

        // 获取关联的大分类
        List<FocusAreaCategory> categories = focusAreaCategoryRepository.findByFocusAreaId(focusArea.getId());

        List<Long> categoryIds = categories.stream()
                .map(FocusAreaCategory::getCategoryId)
                .collect(Collectors.toList());

        List<String> categoryNames = categories.stream()
                .map(fac -> majorCategoryRepository.findById(fac.getCategoryId())
                        .map(mc -> mc.getName())
                        .orElse(""))
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());

        dto.setCategoryIds(categoryIds);
        dto.setCategoryNames(categoryNames);

        return dto;
    }
}
