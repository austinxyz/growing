package com.growing.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.AnswerTemplateDTO;
import com.growing.app.entity.AnswerTemplate;
import com.growing.app.repository.AnswerTemplateRepository;
import com.growing.app.repository.SkillTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 答题模版Service
 * Phase 6: 通用技能学习模块
 */
@Service
@RequiredArgsConstructor
public class AnswerTemplateService {

    private final AnswerTemplateRepository answerTemplateRepository;
    private final SkillTemplateRepository skillTemplateRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建答题模版
     */
    @Transactional
    public AnswerTemplateDTO createTemplate(AnswerTemplateDTO dto) {
        // 检查模版名称是否已存在
        if (answerTemplateRepository.existsByTemplateName(dto.getTemplateName())) {
            throw new RuntimeException("Template name already exists: " + dto.getTemplateName());
        }

        AnswerTemplate template = new AnswerTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setDescription(dto.getDescription());

        // 将templateFields转换为JSON字符串
        try {
            String templateFieldsJson = objectMapper.writeValueAsString(dto.getTemplateFields());
            template.setTemplateFields(templateFieldsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize template fields", e);
        }

        AnswerTemplate saved = answerTemplateRepository.save(template);
        return convertToDTO(saved);
    }

    /**
     * 更新答题模版
     */
    @Transactional
    public AnswerTemplateDTO updateTemplate(Long id, AnswerTemplateDTO dto) {
        AnswerTemplate template = answerTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));

        // 检查模版名称是否与其他模版冲突
        if (!template.getTemplateName().equals(dto.getTemplateName()) &&
                answerTemplateRepository.existsByTemplateName(dto.getTemplateName())) {
            throw new RuntimeException("Template name already exists: " + dto.getTemplateName());
        }

        template.setTemplateName(dto.getTemplateName());
        template.setDescription(dto.getDescription());

        // 将templateFields转换为JSON字符串
        try {
            String templateFieldsJson = objectMapper.writeValueAsString(dto.getTemplateFields());
            template.setTemplateFields(templateFieldsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize template fields", e);
        }

        AnswerTemplate updated = answerTemplateRepository.save(template);
        return convertToDTO(updated);
    }

    /**
     * 删除答题模版（级联删除skill_templates）
     */
    @Transactional
    public void deleteTemplate(Long id) {
        if (!answerTemplateRepository.existsById(id)) {
            throw new RuntimeException("Template not found: " + id);
        }

        // 级联删除由数据库外键约束处理 (ON DELETE CASCADE)
        answerTemplateRepository.deleteById(id);
    }

    /**
     * 获取模版详情
     */
    public AnswerTemplateDTO getTemplate(Long id) {
        AnswerTemplate template = answerTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        return convertToDTO(template);
    }

    /**
     * 获取所有模版
     */
    public List<AnswerTemplateDTO> getAllTemplates() {
        return answerTemplateRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将Entity转换为DTO
     * ⚠️ Guardrail #10: DTO完整性检查 - 确保所有字段都被填充
     */
    private AnswerTemplateDTO convertToDTO(AnswerTemplate template) {
        AnswerTemplateDTO dto = new AnswerTemplateDTO();
        dto.setId(template.getId());
        dto.setTemplateName(template.getTemplateName());
        dto.setDescription(template.getDescription());

        // 解析JSON字段为对象列表
        try {
            List<AnswerTemplateDTO.TemplateField> fields = objectMapper.readValue(
                    template.getTemplateFields(),
                    new TypeReference<List<AnswerTemplateDTO.TemplateField>>() {}
            );
            dto.setTemplateFields(fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize template fields", e);
        }

        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());

        return dto;
    }
}
