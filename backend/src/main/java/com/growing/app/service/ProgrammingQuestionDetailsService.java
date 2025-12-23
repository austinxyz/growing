package com.growing.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ProgrammingQuestionDetailsDTO;
import com.growing.app.model.ProgrammingQuestionDetails;
import com.growing.app.model.Question;
import com.growing.app.repository.ProgrammingQuestionDetailsRepository;
import com.growing.app.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProgrammingQuestionDetailsService {

    @Autowired
    private ProgrammingQuestionDetailsRepository detailsRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取编程题详情（通过Question ID）
     */
    public Optional<ProgrammingQuestionDetailsDTO> getDetailsByQuestionId(Long questionId) {
        return detailsRepository.findByQuestionId(questionId)
                .map(this::convertToDTO);
    }

    /**
     * 获取编程题详情（通过Details ID）
     */
    public ProgrammingQuestionDetailsDTO getDetailsById(Long detailsId) {
        ProgrammingQuestionDetails details = detailsRepository.findById(detailsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "编程题详情不存在"));

        return convertToDTO(details);
    }

    /**
     * 创建编程题详情
     */
    @Transactional
    public ProgrammingQuestionDetailsDTO createDetails(Long questionId, ProgrammingQuestionDetailsDTO dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        // 检查是否已存在详情
        if (detailsRepository.findByQuestionId(questionId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该试题已存在编程题详情");
        }

        ProgrammingQuestionDetails details = new ProgrammingQuestionDetails();
        details.setQuestion(question);
        details.setLeetcodeUrl(dto.getLeetcodeUrl());
        details.setLabuladongUrl(dto.getLabuladongUrl());
        details.setHellointerviewUrl(dto.getHellointerviewUrl());
        details.setIsImportant(dto.getIsImportant() != null ? dto.getIsImportant() : false);
        details.setComplexity(dto.getComplexity());

        // 序列化tags和similarQuestions为JSON
        setTagsToEntity(details, dto.getTags());
        setSimilarQuestionsToEntity(details, dto.getSimilarQuestions());

        details = detailsRepository.save(details);
        return convertToDTO(details);
    }

    /**
     * 更新编程题详情
     */
    @Transactional
    public ProgrammingQuestionDetailsDTO updateDetails(Long detailsId, ProgrammingQuestionDetailsDTO dto) {
        ProgrammingQuestionDetails details = detailsRepository.findById(detailsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "编程题详情不存在"));

        details.setLeetcodeUrl(dto.getLeetcodeUrl());
        details.setLabuladongUrl(dto.getLabuladongUrl());
        details.setHellointerviewUrl(dto.getHellointerviewUrl());
        details.setIsImportant(dto.getIsImportant());
        details.setComplexity(dto.getComplexity());

        // 更新tags和similarQuestions
        setTagsToEntity(details, dto.getTags());
        setSimilarQuestionsToEntity(details, dto.getSimilarQuestions());

        details = detailsRepository.save(details);
        return convertToDTO(details);
    }

    /**
     * 更新编程题详情（通过Question ID）
     */
    @Transactional
    public ProgrammingQuestionDetailsDTO updateDetailsByQuestionId(Long questionId, ProgrammingQuestionDetailsDTO dto) {
        ProgrammingQuestionDetails details = detailsRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "编程题详情不存在"));

        return updateDetails(details.getId(), dto);
    }

    /**
     * 删除编程题详情
     */
    @Transactional
    public void deleteDetails(Long detailsId) {
        if (!detailsRepository.existsById(detailsId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "编程题详情不存在");
        }

        detailsRepository.deleteById(detailsId);
    }

    /**
     * 删除编程题详情（通过Question ID）
     */
    @Transactional
    public void deleteDetailsByQuestionId(Long questionId) {
        detailsRepository.findByQuestionId(questionId)
                .ifPresent(details -> detailsRepository.deleteById(details.getId()));
    }

    // ==================== DTO转换 ====================

    private ProgrammingQuestionDetailsDTO convertToDTO(ProgrammingQuestionDetails details) {
        ProgrammingQuestionDetailsDTO dto = new ProgrammingQuestionDetailsDTO();
        dto.setId(details.getId());
        dto.setQuestionId(details.getQuestion().getId());
        dto.setLeetcodeUrl(details.getLeetcodeUrl());
        dto.setLabuladongUrl(details.getLabuladongUrl());
        dto.setHellointerviewUrl(details.getHellointerviewUrl());
        dto.setIsImportant(details.getIsImportant());
        dto.setComplexity(details.getComplexity());
        dto.setCreatedAt(details.getCreatedAt());
        dto.setUpdatedAt(details.getUpdatedAt());

        // 反序列化tags
        dto.setTags(getTagsFromEntity(details));

        // 反序列化similarQuestions
        dto.setSimilarQuestions(getSimilarQuestionsFromEntity(details));

        return dto;
    }

    // ==================== JSON序列化辅助方法 ====================

    private List<String> getTagsFromEntity(ProgrammingQuestionDetails details) {
        if (details.getTags() == null || details.getTags().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(details.getTags(), new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private void setTagsToEntity(ProgrammingQuestionDetails details, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            details.setTags(null);
            return;
        }
        try {
            details.setTags(objectMapper.writeValueAsString(tags));
        } catch (JsonProcessingException e) {
            details.setTags(null);
        }
    }

    private List<ProgrammingQuestionDetailsDTO.SimilarQuestion> getSimilarQuestionsFromEntity(ProgrammingQuestionDetails details) {
        if (details.getSimilarQuestions() == null || details.getSimilarQuestions().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(details.getSimilarQuestions(),
                    new TypeReference<List<ProgrammingQuestionDetailsDTO.SimilarQuestion>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private void setSimilarQuestionsToEntity(ProgrammingQuestionDetails details,
                                            List<ProgrammingQuestionDetailsDTO.SimilarQuestion> similarQuestions) {
        if (similarQuestions == null || similarQuestions.isEmpty()) {
            details.setSimilarQuestions(null);
            return;
        }
        try {
            details.setSimilarQuestions(objectMapper.writeValueAsString(similarQuestions));
        } catch (JsonProcessingException e) {
            details.setSimilarQuestions(null);
        }
    }
}
