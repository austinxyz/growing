package com.growing.app.service;

import com.growing.app.dto.FocusAreaDTO;
import com.growing.app.dto.LearningResourceDTO;
import com.growing.app.dto.SkillDTO;
import com.growing.app.model.*;
import com.growing.app.repository.*;
import com.growing.app.repository.MajorCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private LearningResourceRepository learningResourceRepository;

    @Autowired
    private CareerPathSkillRepository careerPathSkillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerPathRepository careerPathRepository;

    @Autowired
    private FocusAreaCategoryRepository focusAreaCategoryRepository;

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    // 获取所有技能（按显示顺序）
    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(skill -> {
                    SkillDTO dto = convertToDTO(skill);
                    // 添加统计信息
                    dto.setFocusAreaCount(focusAreaRepository.countBySkillId(skill.getId()));
                    dto.setResourceCount(learningResourceRepository.countBySkillId(skill.getId()));
                    // 添加关联的职业路径
                    dto.setCareerPaths(careerPathSkillRepository.findBySkillId(skill.getId()).stream()
                            .map(cps -> {
                                CareerPath cp = cps.getCareerPath();
                                return new SkillDTO.CareerPathInfo(cp.getId(), cp.getName());
                            })
                            .collect(Collectors.toList()));
                    // DTO Completeness Checklist: 添加Focus Areas列表（用于编辑重点领域modal）
                    dto.setFocusAreas(focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skill.getId()).stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 获取未关联到任何职业路径的技能
    public List<SkillDTO> getUnassociatedSkills() {
        return skillRepository.findUnassociatedSkills().stream()
                .map(skill -> {
                    SkillDTO dto = convertToDTO(skill);
                    // 添加统计信息
                    dto.setFocusAreaCount(focusAreaRepository.countBySkillId(skill.getId()));
                    dto.setResourceCount(learningResourceRepository.countBySkillId(skill.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 获取技能详情（用户视角 - 资源可见性过滤）
    public SkillDTO getSkillById(Long skillId, Long userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        SkillDTO dto = convertToDTO(skill);

        // 添加统计信息
        dto.setFocusAreaCount(focusAreaRepository.countBySkillId(skillId));
        dto.setResourceCount(learningResourceRepository.countBySkillId(skillId));

        // 加载Focus Areas
        dto.setFocusAreas(focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        // 加载Learning Resources（应用可见性过滤）
        dto.setLearningResources(learningResourceRepository.findBySkillIdForUser(skillId, userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // 获取技能详情（管理员视角 - 无过滤）
    public SkillDTO getSkillByIdForAdmin(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        SkillDTO dto = convertToDTO(skill);

        // 添加统计信息
        dto.setFocusAreaCount(focusAreaRepository.countBySkillId(skillId));
        dto.setResourceCount(learningResourceRepository.countBySkillId(skillId));

        // 添加关联的职业路径
        dto.setCareerPaths(careerPathSkillRepository.findBySkillId(skillId).stream()
                .map(cps -> {
                    CareerPath cp = cps.getCareerPath();
                    return new SkillDTO.CareerPathInfo(cp.getId(), cp.getName());
                })
                .collect(Collectors.toList()));

        // 添加职业路径ID列表（用于编辑）
        dto.setCareerPathIds(dto.getCareerPaths().stream()
                .map(SkillDTO.CareerPathInfo::getId)
                .collect(Collectors.toList()));

        dto.setFocusAreas(focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        dto.setLearningResources(learningResourceRepository.findBySkillIdForAdmin(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // 根据Career Path获取技能
    public List<SkillDTO> getSkillsByCareerPathId(Long careerPathId) {
        return skillRepository.findByCareerPathId(careerPathId).stream()
                .map(skill -> {
                    SkillDTO dto = convertToDTO(skill);
                    // 添加统计信息
                    dto.setFocusAreaCount(focusAreaRepository.countBySkillId(skill.getId()));
                    dto.setResourceCount(learningResourceRepository.countBySkillId(skill.getId()));
                    // 添加Focus Areas列表
                    dto.setFocusAreas(focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skill.getId()).stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 创建技能（管理员）
    @Transactional
    public SkillDTO createSkill(SkillDTO skillDTO) {
        Skill skill = new Skill();
        skill.setName(skillDTO.getName());
        skill.setDescription(skillDTO.getDescription());
        skill.setIsImportant(skillDTO.getIsImportant() != null ? skillDTO.getIsImportant() : false);
        skill.setIcon(skillDTO.getIcon());
        skill.setDisplayOrder(skillDTO.getDisplayOrder() != null ? skillDTO.getDisplayOrder() : 0);
        skill.setIsGeneralOnly(skillDTO.getIsGeneralOnly() != null ? skillDTO.getIsGeneralOnly() : false);

        skill = skillRepository.save(skill);

        // 如果是通用分类模式，自动创建 General 大分类
        if (skill.getIsGeneralOnly()) {
            ensureGeneralCategory(skill);
        }

        // 处理职业路径关联
        if (skillDTO.getCareerPathIds() != null && !skillDTO.getCareerPathIds().isEmpty()) {
            for (Long careerPathId : skillDTO.getCareerPathIds()) {
                try {
                    associateSkillToCareerPath(careerPathId, skill.getId());
                } catch (ResponseStatusException e) {
                    // 忽略已存在的关联
                    if (!e.getMessage().contains("已关联")) {
                        throw e;
                    }
                }
            }
        }

        return convertToDTO(skill);
    }

    // 更新技能（管理员）
    @Transactional
    public SkillDTO updateSkill(Long skillId, SkillDTO skillDTO) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        // 检查 isGeneralOnly 的修改是否合法
        boolean oldIsGeneralOnly = skill.getIsGeneralOnly() != null ? skill.getIsGeneralOnly() : false;
        boolean newIsGeneralOnly = skillDTO.getIsGeneralOnly() != null ? skillDTO.getIsGeneralOnly() : false;

        // 如果要从 false 改为 true，检查是否有非 General 的大分类
        if (!oldIsGeneralOnly && newIsGeneralOnly) {
            if (hasNonGeneralCategories(skillId)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "该技能下存在非 General 的大分类，请先删除这些大分类后再启用通用分类模式"
                );
            }
            // 没有非 General 大分类，可以切换，确保创建 General 大分类
            skill.setIsGeneralOnly(true);
            skill = skillRepository.save(skill);
            ensureGeneralCategory(skill);
        } else if (oldIsGeneralOnly && !newIsGeneralOnly) {
            // 从 true 改为 false，允许（用户可以手动添加其他大分类）
            skill.setIsGeneralOnly(false);
        } else {
            // 没有改变或保持原值
            skill.setIsGeneralOnly(newIsGeneralOnly);
        }

        skill.setName(skillDTO.getName());
        skill.setDescription(skillDTO.getDescription());
        skill.setIsImportant(skillDTO.getIsImportant());
        skill.setIcon(skillDTO.getIcon());
        skill.setDisplayOrder(skillDTO.getDisplayOrder());

        skill = skillRepository.save(skill);

        // 如果是 isGeneralOnly 模式且没有其他大分类，将所有 Focus Areas 关联到 General 大分类
        if (newIsGeneralOnly) {
            reassignOrphanedFocusAreasToGeneral(skillId);
        }

        // 更新职业路径关联
        if (skillDTO.getCareerPathIds() != null) {
            // 获取当前的职业路径关联
            List<CareerPathSkill> currentAssociations = careerPathSkillRepository.findBySkillId(skillId);
            Set<Long> currentCareerPathIds = currentAssociations.stream()
                    .map(cps -> cps.getCareerPath().getId())
                    .collect(Collectors.toSet());

            Set<Long> newCareerPathIds = new HashSet<>(skillDTO.getCareerPathIds());

            // 删除不再关联的职业路径
            for (Long currentId : currentCareerPathIds) {
                if (!newCareerPathIds.contains(currentId)) {
                    dissociateSkillFromCareerPath(currentId, skillId);
                }
            }

            // 添加新关联的职业路径
            for (Long newId : newCareerPathIds) {
                if (!currentCareerPathIds.contains(newId)) {
                    try {
                        associateSkillToCareerPath(newId, skillId);
                    } catch (ResponseStatusException e) {
                        // 忽略已存在的关联
                        if (!e.getMessage().contains("已关联")) {
                            throw e;
                        }
                    }
                }
            }
        }

        return convertToDTO(skill);
    }

    // 删除技能（管理员）
    @Transactional
    public void deleteSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在");
        }

        // 检查是否有关联的 Focus Area
        long focusAreaCount = focusAreaRepository.countBySkillId(skillId);
        if (focusAreaCount > 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "该技能下还有 " + focusAreaCount + " 个 Focus Area，请先删除所有 Focus Area 后再删除技能"
            );
        }

        skillRepository.deleteById(skillId);
    }

    // 关联技能到Career Path
    @Transactional
    public void associateSkillToCareerPath(Long careerPathId, Long skillId) {
        CareerPath careerPath = careerPathRepository.findById(careerPathId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职业路径不存在"));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        if (careerPathSkillRepository.existsByCareerPathIdAndSkillId(careerPathId, skillId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该技能已关联到此职业路径");
        }

        CareerPathSkill cps = new CareerPathSkill();
        cps.setCareerPath(careerPath);
        cps.setSkill(skill);
        careerPathSkillRepository.save(cps);
    }

    // 取消关联
    @Transactional
    public void dissociateSkillFromCareerPath(Long careerPathId, Long skillId) {
        careerPathSkillRepository.deleteByCareerPathIdAndSkillId(careerPathId, skillId);
    }

    // DTO转换
    private SkillDTO convertToDTO(Skill skill) {
        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setDescription(skill.getDescription());
        dto.setIsImportant(skill.getIsImportant());
        dto.setIcon(skill.getIcon());
        dto.setDisplayOrder(skill.getDisplayOrder());
        dto.setIsGeneralOnly(skill.getIsGeneralOnly());
        dto.setCreatedAt(skill.getCreatedAt());
        dto.setUpdatedAt(skill.getUpdatedAt());
        return dto;
    }

    private FocusAreaDTO convertToDTO(FocusArea focusArea) {
        FocusAreaDTO dto = new FocusAreaDTO();
        dto.setId(focusArea.getId());
        dto.setSkillId(focusArea.getSkill().getId());
        dto.setName(focusArea.getName());
        dto.setDescription(focusArea.getDescription());
        dto.setDisplayOrder(focusArea.getDisplayOrder());
        dto.setCreatedAt(focusArea.getCreatedAt());
        dto.setUpdatedAt(focusArea.getUpdatedAt());

        // 为有大分类的skill添加categoryIds (算法与数据结构、系统设计、Behavioral、云计算)
        Long skillId = focusArea.getSkill().getId();
        if (skillId == 1L || skillId == 2L || skillId == 3L || skillId == 4L) {
            List<Long> categoryIds = focusAreaCategoryRepository.findByFocusAreaId(focusArea.getId())
                    .stream()
                    .map(fac -> fac.getCategoryId())
                    .collect(Collectors.toList());
            dto.setCategoryIds(categoryIds);
        }

        return dto;
    }

    private LearningResourceDTO convertToDTO(LearningResource resource) {
        LearningResourceDTO dto = new LearningResourceDTO();
        dto.setId(resource.getId());
        dto.setSkillId(resource.getSkill().getId());
        dto.setResourceType(resource.getResourceType());
        dto.setTitle(resource.getTitle());
        dto.setUrl(resource.getUrl());
        dto.setAuthor(resource.getAuthor());
        dto.setDescription(resource.getDescription());
        dto.setIsOfficial(resource.getIsOfficial());
        dto.setDisplayOrder(resource.getDisplayOrder());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());

        if (resource.getCreatedByUser() != null) {
            dto.setCreatedByUserId(resource.getCreatedByUser().getId());
            dto.setCreatedByUsername(resource.getCreatedByUser().getUsername());
        }

        return dto;
    }

    /**
     * 确保 General 大分类存在（用于 isGeneralOnly=true 的技能）
     */
    private void ensureGeneralCategory(Skill skill) {
        // 查找是否已有 General 大分类
        List<MajorCategory> categories = majorCategoryRepository.findBySkillIdOrderBySortOrderAsc(skill.getId());
        boolean hasGeneral = categories.stream().anyMatch(c -> "General".equals(c.getName()));

        if (!hasGeneral) {
            MajorCategory general = new MajorCategory();
            general.setSkillId(skill.getId());
            general.setName("General");
            general.setDescription("通用分类");
            general.setSortOrder(0);
            majorCategoryRepository.save(general);
        }
    }

    /**
     * 获取 Skill 的 General 大分类（如果存在）
     */
    private MajorCategory getGeneralCategory(Long skillId) {
        List<MajorCategory> categories = majorCategoryRepository.findBySkillIdOrderBySortOrderAsc(skillId);
        return categories.stream()
                .filter(c -> "General".equals(c.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 检查技能是否有非 General 的大分类
     */
    private boolean hasNonGeneralCategories(Long skillId) {
        List<MajorCategory> categories = majorCategoryRepository.findBySkillIdOrderBySortOrderAsc(skillId);
        return categories.stream().anyMatch(c -> !"General".equals(c.getName()));
    }

    /**
     * 将孤立的 Focus Areas 重新关联到 General 大分类
     * 当 isGeneralOnly=true 且没有其他大分类时使用
     */
    private void reassignOrphanedFocusAreasToGeneral(Long skillId) {
        // 确保 General 大分类存在
        MajorCategory generalCategory = getGeneralCategory(skillId);
        if (generalCategory == null) {
            // 如果不存在，创建它
            generalCategory = new MajorCategory();
            generalCategory.setSkillId(skillId);
            generalCategory.setName("General");
            generalCategory.setDescription("通用分类");
            generalCategory.setSortOrder(0);
            generalCategory = majorCategoryRepository.save(generalCategory);
        }

        // 获取所有的大分类
        List<MajorCategory> categories = majorCategoryRepository.findBySkillIdOrderBySortOrderAsc(skillId);
        boolean hasOnlyGeneral = categories.size() == 1 && "General".equals(categories.get(0).getName());

        // 只有当仅有 General 大分类时，才重新关联 Focus Areas
        if (hasOnlyGeneral) {
            // 获取该 Skill 下的所有 Focus Areas
            List<FocusArea> focusAreas = focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(skillId);

            Long generalCategoryId = generalCategory.getId();

            for (FocusArea focusArea : focusAreas) {
                // 检查该 Focus Area 是否已关联到 General 大分类
                List<FocusAreaCategory> existingCategories = focusAreaCategoryRepository.findByFocusAreaId(focusArea.getId());
                boolean alreadyLinkedToGeneral = existingCategories.stream()
                        .anyMatch(fac -> fac.getCategoryId().equals(generalCategoryId));

                if (!alreadyLinkedToGeneral) {
                    // 如果没有关联，则创建关联
                    FocusAreaCategory fac = new FocusAreaCategory();
                    fac.setFocusAreaId(focusArea.getId());
                    fac.setCategoryId(generalCategoryId);
                    focusAreaCategoryRepository.save(fac);
                }

                // 清理关联到已删除大分类的记录（保留 General 大分类的关联）
                for (FocusAreaCategory fac : existingCategories) {
                    if (!fac.getCategoryId().equals(generalCategoryId)) {
                        // 检查该分类是否还存在
                        boolean categoryExists = majorCategoryRepository.existsById(fac.getCategoryId());
                        if (!categoryExists) {
                            // 如果分类已被删除，移除关联
                            focusAreaCategoryRepository.delete(fac);
                        }
                    }
                }
            }
        }
    }
}
