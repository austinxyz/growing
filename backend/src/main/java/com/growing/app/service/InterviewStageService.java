package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.FocusAreaBriefDTO;
import com.growing.app.dto.InterviewPreparationChecklistDTO;
import com.growing.app.dto.InterviewStageDTO;
import com.growing.app.entity.InterviewPreparationChecklist;
import com.growing.app.entity.InterviewStage;
import com.growing.app.entity.JobApplication;
import com.growing.app.model.FocusArea;
import com.growing.app.model.Skill;
import com.growing.app.repository.FocusAreaRepository;
import com.growing.app.repository.InterviewPreparationChecklistRepository;
import com.growing.app.repository.InterviewStageRepository;
import com.growing.app.repository.JobApplicationRepository;
import com.growing.app.repository.SkillRepository;
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

    @Autowired
    private InterviewPreparationChecklistRepository checklistRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private SkillRepository skillRepository;

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
            if (dto.getFocusAreaIds() != null) {
                stage.setFocusAreaIds(objectMapper.writeValueAsString(dto.getFocusAreaIds()));
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
            if (dto.getFocusAreaIds() != null) {
                stage.setFocusAreaIds(objectMapper.writeValueAsString(dto.getFocusAreaIds()));
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
            if (stage.getFocusAreaIds() != null) {
                dto.setFocusAreaIds(objectMapper.readValue(stage.getFocusAreaIds(),
                        new TypeReference<List<Long>>() {}));
            }
        } catch (Exception e) {
            dto.setSkillIds(Collections.emptyList());
            dto.setFocusAreaIds(Collections.emptyList());
        }

        // DTO Completeness Checklist: Populate focusAreas with names
        if (dto.getFocusAreaIds() != null && !dto.getFocusAreaIds().isEmpty()) {
            List<FocusAreaBriefDTO> focusAreas = dto.getFocusAreaIds().stream()
                    .map(focusAreaId -> {
                        return focusAreaRepository.findById(focusAreaId)
                                .map(this::convertFocusAreaToBriefDTO)
                                .orElse(null);
                    })
                    .filter(fa -> fa != null)
                    .collect(Collectors.toList());
            dto.setFocusAreas(focusAreas);
        } else {
            dto.setFocusAreas(Collections.emptyList());
        }

        // DTO Completeness Checklist: Load checklist items
        List<InterviewPreparationChecklist> checklists = checklistRepository
                .findByInterviewStageIdOrderBySortOrderAsc(stage.getId());
        dto.setChecklistItems(checklists.stream()
                .map(this::convertChecklistToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private FocusAreaBriefDTO convertFocusAreaToBriefDTO(FocusArea focusArea) {
        FocusAreaBriefDTO dto = new FocusAreaBriefDTO();
        dto.setId(focusArea.getId());
        dto.setName(focusArea.getName());
        dto.setDescription(focusArea.getDescription());

        // FocusArea uses @ManyToOne Skill relationship
        if (focusArea.getSkill() != null) {
            dto.setSkillId(focusArea.getSkill().getId());
            dto.setSkillName(focusArea.getSkill().getName());
        }

        return dto;
    }

    private InterviewPreparationChecklistDTO convertChecklistToDTO(InterviewPreparationChecklist checklist) {
        InterviewPreparationChecklistDTO dto = new InterviewPreparationChecklistDTO();
        dto.setId(checklist.getId());
        dto.setInterviewStageId(checklist.getInterviewStageId());
        dto.setChecklistItem(checklist.getChecklistItem());
        dto.setIsPriority(checklist.getIsPriority());
        dto.setCategory(checklist.getCategory());
        dto.setNotes(checklist.getNotes());
        dto.setSortOrder(checklist.getSortOrder());
        dto.setCreatedAt(checklist.getCreatedAt());
        dto.setUpdatedAt(checklist.getUpdatedAt());
        return dto;
    }
}
