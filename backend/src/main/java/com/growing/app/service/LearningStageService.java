package com.growing.app.service;

import com.growing.app.dto.LearningStageDTO;
import com.growing.app.model.LearningStage;
import com.growing.app.model.Skill;
import com.growing.app.repository.LearningStageRepository;
import com.growing.app.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningStageService {

    @Autowired
    private LearningStageRepository learningStageRepository;

    @Autowired
    private SkillRepository skillRepository;

    // 获取Skill的所有学习阶段（按sort_order排序）
    public List<LearningStageDTO> getStagesBySkillId(Long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在");
        }

        return learningStageRepository.findBySkillIdOrderBySortOrderAsc(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取单个学习阶段详情
    public LearningStageDTO getStageById(Long stageId) {
        LearningStage stage = learningStageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习阶段不存在"));

        return convertToDTO(stage);
    }

    // 创建学习阶段（管理员）
    @Transactional
    public LearningStageDTO createStage(LearningStageDTO dto) {
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        // 检查阶段名称是否重复
        if (learningStageRepository.existsBySkillIdAndStageName(dto.getSkillId(), dto.getStageName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该技能下已存在同名学习阶段");
        }

        LearningStage stage = new LearningStage();
        stage.setSkill(skill);
        stage.setStageName(dto.getStageName());
        stage.setStageType(dto.getStageType());
        stage.setDescription(dto.getDescription());
        stage.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        stage = learningStageRepository.save(stage);
        return convertToDTO(stage);
    }

    // 更新学习阶段（管理员）
    @Transactional
    public LearningStageDTO updateStage(Long stageId, LearningStageDTO dto) {
        LearningStage stage = learningStageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习阶段不存在"));

        // 如果修改了阶段名称，检查是否与同Skill下其他阶段重复
        if (!stage.getStageName().equals(dto.getStageName())) {
            if (learningStageRepository.existsBySkillIdAndStageNameAndIdNot(
                    stage.getSkill().getId(), dto.getStageName(), stageId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该技能下已存在同名学习阶段");
            }
        }

        stage.setStageName(dto.getStageName());
        stage.setStageType(dto.getStageType());
        stage.setDescription(dto.getDescription());
        stage.setSortOrder(dto.getSortOrder());

        stage = learningStageRepository.save(stage);
        return convertToDTO(stage);
    }

    // 删除学习阶段（管理员）
    @Transactional
    public void deleteStage(Long stageId) {
        if (!learningStageRepository.existsById(stageId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "学习阶段不存在");
        }

        // 级联删除由数据库外键约束处理：
        // - learning_contents 会被级联删除
        learningStageRepository.deleteById(stageId);
    }

    // DTO转换
    private LearningStageDTO convertToDTO(LearningStage stage) {
        LearningStageDTO dto = new LearningStageDTO();
        dto.setId(stage.getId());
        dto.setSkillId(stage.getSkill().getId());
        dto.setStageName(stage.getStageName());
        dto.setStageType(stage.getStageType());
        dto.setDescription(stage.getDescription());
        dto.setSortOrder(stage.getSortOrder());
        dto.setCreatedAt(stage.getCreatedAt());
        // contents字段不在此处填充，由需要时单独查询
        return dto;
    }
}
