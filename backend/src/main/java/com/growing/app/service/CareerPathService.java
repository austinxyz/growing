package com.growing.app.service;

import com.growing.app.dto.CareerPathWithSkillsDTO;
import com.growing.app.model.CareerPath;
import com.growing.app.model.CareerPathSkill;
import com.growing.app.model.Skill;
import com.growing.app.repository.CareerPathRepository;
import com.growing.app.repository.CareerPathSkillRepository;
import com.growing.app.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerPathService {

    private final CareerPathRepository careerPathRepository;
    private final CareerPathSkillRepository careerPathSkillRepository;
    private final SkillRepository skillRepository;

    public List<CareerPath> getAllCareerPaths() {
        return careerPathRepository.findAll();
    }

    public List<CareerPath> getActiveCareerPaths() {
        return careerPathRepository.findByIsActiveTrue();
    }

    public CareerPath getCareerPathById(Long id) {
        return careerPathRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Career path not found with id: " + id));
    }

    /**
     * 获取所有激活的职业路径(包含关联的技能列表)
     */
    @Transactional(readOnly = true)
    public List<CareerPathWithSkillsDTO> getActiveCareerPathsWithSkills() {
        List<CareerPath> careerPaths = careerPathRepository.findActiveWithSkills();

        return careerPaths.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 将CareerPath转换为CareerPathWithSkillsDTO
     */
    private CareerPathWithSkillsDTO convertToDTO(CareerPath careerPath) {
        CareerPathWithSkillsDTO dto = new CareerPathWithSkillsDTO();
        dto.setId(careerPath.getId());
        dto.setName(careerPath.getName());
        dto.setCode(careerPath.getCode());
        dto.setDescription(careerPath.getDescription());
        dto.setIcon(careerPath.getIcon());
        dto.setIsActive(careerPath.getIsActive());
        dto.setCreatedAt(careerPath.getCreatedAt());
        dto.setUpdatedAt(careerPath.getUpdatedAt());

        // 转换关联的技能列表
        List<CareerPathWithSkillsDTO.SkillDTO> skills = careerPath.getCareerPathSkills().stream()
            .map(CareerPathSkill::getSkill)
            .sorted((s1, s2) -> {
                Integer order1 = s1.getDisplayOrder() != null ? s1.getDisplayOrder() : 0;
                Integer order2 = s2.getDisplayOrder() != null ? s2.getDisplayOrder() : 0;
                return order1.compareTo(order2);
            })
            .map(this::convertSkillToDTO)
            .collect(Collectors.toList());

        dto.setSkills(skills);

        return dto;
    }

    /**
     * 将Skill转换为SkillDTO
     */
    private CareerPathWithSkillsDTO.SkillDTO convertSkillToDTO(Skill skill) {
        CareerPathWithSkillsDTO.SkillDTO dto = new CareerPathWithSkillsDTO.SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setDescription(skill.getDescription());
        dto.setIcon(skill.getIcon());
        dto.setDisplayOrder(skill.getDisplayOrder());
        return dto;
    }

    /**
     * 创建职业路径（管理员）
     */
    @Transactional
    public CareerPath createCareerPath(CareerPath careerPath, List<Long> skillIds) {
        careerPath = careerPathRepository.save(careerPath);

        // 关联技能
        if (skillIds != null && !skillIds.isEmpty()) {
            for (Long skillId : skillIds) {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在: " + skillId));

                CareerPathSkill cps = new CareerPathSkill();
                cps.setCareerPath(careerPath);
                cps.setSkill(skill);
                careerPathSkillRepository.save(cps);
            }
        }

        return careerPath;
    }

    /**
     * 更新职业路径（管理员）
     */
    @Transactional
    public CareerPath updateCareerPath(Long id, CareerPath careerPathUpdate, List<Long> skillIds) {
        CareerPath careerPath = careerPathRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职业路径不存在"));

        careerPath.setName(careerPathUpdate.getName());
        careerPath.setCode(careerPathUpdate.getCode());
        careerPath.setDescription(careerPathUpdate.getDescription());
        careerPath.setIcon(careerPathUpdate.getIcon());
        careerPath.setIsActive(careerPathUpdate.getIsActive());

        careerPath = careerPathRepository.save(careerPath);

        // 更新技能关联
        if (skillIds != null) {
            // 获取当前的技能关联
            List<CareerPathSkill> currentAssociations = careerPathSkillRepository.findByCareerPathId(id);
            Set<Long> currentSkillIds = currentAssociations.stream()
                    .map(cps -> cps.getSkill().getId())
                    .collect(Collectors.toSet());

            Set<Long> newSkillIds = new HashSet<>(skillIds);

            // 删除不再关联的技能
            for (Long currentSkillId : currentSkillIds) {
                if (!newSkillIds.contains(currentSkillId)) {
                    careerPathSkillRepository.deleteByCareerPathIdAndSkillId(id, currentSkillId);
                }
            }

            // 添加新关联的技能
            for (Long newSkillId : newSkillIds) {
                if (!currentSkillIds.contains(newSkillId)) {
                    Skill skill = skillRepository.findById(newSkillId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在: " + newSkillId));

                    CareerPathSkill cps = new CareerPathSkill();
                    cps.setCareerPath(careerPath);
                    cps.setSkill(skill);
                    careerPathSkillRepository.save(cps);
                }
            }
        }

        return careerPath;
    }

    /**
     * 删除职业路径（管理员）
     * 只删除职业路径和关联关系，不删除技能
     */
    @Transactional
    public void deleteCareerPath(Long id) {
        if (!careerPathRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "职业路径不存在");
        }

        // 先删除关联关系
        careerPathSkillRepository.deleteByCareerPathId(id);

        // 再删除职业路径
        careerPathRepository.deleteById(id);
    }
}
