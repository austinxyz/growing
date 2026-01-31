package com.growing.app.service;

import com.growing.app.dto.TemplateQuestionDTO;
import com.growing.app.model.TemplateQuestion;
import com.growing.app.repository.LearningContentRepository;
import com.growing.app.repository.ProgrammingQuestionDetailsRepository;
import com.growing.app.repository.QuestionRepository;
import com.growing.app.repository.TemplateQuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemplateQuestionService {

    @Autowired
    private TemplateQuestionRepository templateQuestionRepository;

    @Autowired
    private LearningContentRepository learningContentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ProgrammingQuestionDetailsRepository programmingQuestionDetailsRepository;

    /**
     * 添加模板-题目关联
     */
    @Transactional
    public TemplateQuestionDTO addTemplateQuestion(Long templateId, Long questionId, Long userId) {
        // 检查是否已存在
        if (templateQuestionRepository.findByTemplateIdAndQuestionId(templateId, questionId).isPresent()) {
            throw new RuntimeException("该题目已关联到此模板");
        }

        TemplateQuestion tq = new TemplateQuestion();
        tq.setTemplateId(templateId);
        tq.setQuestionId(questionId);
        tq.setCreatedBy(userId);

        TemplateQuestion saved = templateQuestionRepository.save(tq);
        return convertToDTO(saved);
    }

    /**
     * 删除模板-题目关联
     */
    @Transactional
    public void removeTemplateQuestion(Long templateId, Long questionId) {
        templateQuestionRepository.deleteByTemplateIdAndQuestionId(templateId, questionId);
    }

    /**
     * 获取某个模板的所有关联题目
     */
    public List<TemplateQuestionDTO> getQuestionsByTemplate(Long templateId) {
        return templateQuestionRepository.findByTemplateId(templateId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取某道题关联的所有模板
     */
    public List<TemplateQuestionDTO> getTemplatesByQuestion(Long questionId) {
        return templateQuestionRepository.findByQuestionId(questionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     */
    private TemplateQuestionDTO convertToDTO(TemplateQuestion tq) {
        TemplateQuestionDTO dto = new TemplateQuestionDTO();
        dto.setId(tq.getId());
        dto.setTemplateId(tq.getTemplateId());
        dto.setQuestionId(tq.getQuestionId());

        // 填充模板信息
        learningContentRepository.findById(tq.getTemplateId()).ifPresent(template -> {
            dto.setTemplateTitle(template.getTitle());
        });

        // 填充题目信息
        questionRepository.findById(tq.getQuestionId()).ifPresent(question -> {
            dto.setQuestionTitle(question.getTitle());
            dto.setDifficulty(question.getDifficulty() != null ? question.getDifficulty().name() : null);

            // 如果是编程题，填充LeetCode题号
            programmingQuestionDetailsRepository.findByQuestionId(question.getId()).ifPresent(details -> {
                dto.setLeetcodeNumber(details.getLeetcodeNumber());
            });
        });

        return dto;
    }
}
