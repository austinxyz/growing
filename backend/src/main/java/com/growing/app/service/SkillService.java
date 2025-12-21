package com.growing.app.service;

import com.growing.app.dto.FocusAreaDTO;
import com.growing.app.dto.LearningResourceDTO;
import com.growing.app.dto.SkillDTO;
import com.growing.app.model.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

        skill = skillRepository.save(skill);
        return convertToDTO(skill);
    }

    // 更新技能（管理员）
    @Transactional
    public SkillDTO updateSkill(Long skillId, SkillDTO skillDTO) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        skill.setName(skillDTO.getName());
        skill.setDescription(skillDTO.getDescription());
        skill.setIsImportant(skillDTO.getIsImportant());
        skill.setIcon(skillDTO.getIcon());
        skill.setDisplayOrder(skillDTO.getDisplayOrder());

        skill = skillRepository.save(skill);
        return convertToDTO(skill);
    }

    // 删除技能（管理员）
    @Transactional
    public void deleteSkill(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在");
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
}
