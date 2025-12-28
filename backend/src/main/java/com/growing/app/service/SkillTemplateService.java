package com.growing.app.service;

import com.growing.app.dto.SkillTemplateDTO;
import com.growing.app.entity.AnswerTemplate;
import com.growing.app.entity.SkillTemplate;
import com.growing.app.model.Skill;
import com.growing.app.repository.AnswerTemplateRepository;
import com.growing.app.repository.SkillRepository;
import com.growing.app.repository.SkillTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 技能-模版关联Service
 * Phase 6: 通用技能学习模块
 */
@Service
@RequiredArgsConstructor
public class SkillTemplateService {

    private final SkillTemplateRepository skillTemplateRepository;
    private final AnswerTemplateRepository answerTemplateRepository;
    private final SkillRepository skillRepository;

    /**
     * 关联Skill与模版
     */
    @Transactional
    public SkillTemplateDTO associateTemplate(Long skillId, Long templateId) {
        // 检查是否已关联
        if (skillTemplateRepository.existsBySkillIdAndTemplateId(skillId, templateId)) {
            throw new RuntimeException("Skill and template already associated");
        }

        // 检查模版是否存在
        if (!answerTemplateRepository.existsById(templateId)) {
            throw new RuntimeException("Template not found: " + templateId);
        }

        SkillTemplate skillTemplate = new SkillTemplate();
        skillTemplate.setSkillId(skillId);
        skillTemplate.setTemplateId(templateId);
        skillTemplate.setIsDefault(false);

        SkillTemplate saved = skillTemplateRepository.save(skillTemplate);
        return convertToDTO(saved);
    }

    /**
     * 取消关联
     */
    @Transactional
    public void disassociateTemplate(Long skillId, Long templateId) {
        SkillTemplate.SkillTemplateId id = new SkillTemplate.SkillTemplateId();
        id.setSkillId(skillId);
        id.setTemplateId(templateId);

        if (!skillTemplateRepository.existsById(id)) {
            throw new RuntimeException("Association not found");
        }

        skillTemplateRepository.deleteById(id);
    }

    /**
     * 设置默认模版
     */
    @Transactional
    public SkillTemplateDTO setDefaultTemplate(Long skillId, Long templateId) {
        // 检查关联是否存在
        SkillTemplate.SkillTemplateId id = new SkillTemplate.SkillTemplateId();
        id.setSkillId(skillId);
        id.setTemplateId(templateId);

        SkillTemplate skillTemplate = skillTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association not found"));

        // 清除该技能的所有默认模版标记
        skillTemplateRepository.clearDefaultFlagBySkillId(skillId);

        // 设置新的默认模版
        skillTemplate.setIsDefault(true);
        SkillTemplate updated = skillTemplateRepository.save(skillTemplate);

        return convertToDTO(updated);
    }

    /**
     * 获取Skill的所有模版
     */
    public List<SkillTemplateDTO> getSkillTemplates(Long skillId) {
        return skillTemplateRepository.findBySkillId(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取Skill的默认模版
     */
    public SkillTemplateDTO getDefaultTemplate(Long skillId) {
        return skillTemplateRepository.findBySkillIdAndIsDefaultTrue(skillId)
                .map(this::convertToDTO)
                .orElse(null);  // 可能没有设置默认模版
    }

    /**
     * 获取模版关联的所有技能（反向查询）
     */
    public List<SkillTemplateDTO> getSkillsByTemplate(Long templateId) {
        return skillTemplateRepository.findByTemplateId(templateId).stream()
                .map(st -> {
                    SkillTemplateDTO dto = convertToDTO(st);
                    // 填充skillName字段
                    skillRepository.findById(st.getSkillId())
                            .ifPresent(skill -> dto.setSkillName(skill.getName()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将Entity转换为DTO
     * ⚠️ Guardrail #10: DTO完整性检查 - 确保所有字段都被填充
     */
    private SkillTemplateDTO convertToDTO(SkillTemplate skillTemplate) {
        SkillTemplateDTO dto = new SkillTemplateDTO();
        dto.setSkillId(skillTemplate.getSkillId());
        dto.setTemplateId(skillTemplate.getTemplateId());
        dto.setIsDefault(skillTemplate.getIsDefault());
        dto.setCreatedAt(skillTemplate.getCreatedAt());

        // 填充templateName字段
        answerTemplateRepository.findById(skillTemplate.getTemplateId())
                .ifPresent(template -> dto.setTemplateName(template.getTemplateName()));

        return dto;
    }
}
