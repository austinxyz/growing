package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.InterviewStageDTO;
import com.growing.app.entity.InterviewStage;
import com.growing.app.entity.JobApplication;
import com.growing.app.repository.InterviewStageRepository;
import com.growing.app.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewStageService {

    @Autowired
    private InterviewStageRepository interviewStageRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<InterviewStageDTO> getStagesByApplicationId(Long applicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此求职申请的面试阶段");
        }

        return interviewStageRepository.findByJobApplicationIdOrderByStageOrder(applicationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterviewStageDTO createStage(Long userId, InterviewStageDTO dto) {
        JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此求职申请添加面试阶段");
        }

        InterviewStage stage = new InterviewStage();
        stage.setJobApplicationId(dto.getJobApplicationId());
        stage.setStageName(dto.getStageName());
        stage.setStageOrder(dto.getStageOrder() != null ? dto.getStageOrder() : 0);
        stage.setPreparationNotes(dto.getPreparationNotes());

        // Convert JSON fields
        try {
            if (dto.getSkillIds() != null) {
                stage.setSkillIds(objectMapper.writeValueAsString(dto.getSkillIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        stage = interviewStageRepository.save(stage);
        return convertToDTO(stage);
    }

    @Transactional
    public InterviewStageDTO updateStage(Long id, Long userId, InterviewStageDTO dto) {
        InterviewStage stage = interviewStageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此面试阶段");
        }

        stage.setStageName(dto.getStageName());
        stage.setStageOrder(dto.getStageOrder());
        stage.setPreparationNotes(dto.getPreparationNotes());

        // Update JSON fields
        try {
            if (dto.getSkillIds() != null) {
                stage.setSkillIds(objectMapper.writeValueAsString(dto.getSkillIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        stage = interviewStageRepository.save(stage);
        return convertToDTO(stage);
    }

    @Transactional
    public void deleteStage(Long id, Long userId) {
        InterviewStage stage = interviewStageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此面试阶段");
        }

        interviewStageRepository.delete(stage);
    }

    private InterviewStageDTO convertToDTO(InterviewStage stage) {
        InterviewStageDTO dto = new InterviewStageDTO();
        dto.setId(stage.getId());
        dto.setJobApplicationId(stage.getJobApplicationId());
        dto.setStageName(stage.getStageName());
        dto.setStageOrder(stage.getStageOrder());
        dto.setPreparationNotes(stage.getPreparationNotes());
        dto.setCreatedAt(stage.getCreatedAt());
        dto.setUpdatedAt(stage.getUpdatedAt());

        // Parse JSON fields
        try {
            if (stage.getSkillIds() != null) {
                dto.setSkillIds(objectMapper.readValue(stage.getSkillIds(),
                        new TypeReference<List<Long>>() {}));
            }
        } catch (Exception e) {
            dto.setSkillIds(Collections.emptyList());
        }

        return dto;
    }
}
