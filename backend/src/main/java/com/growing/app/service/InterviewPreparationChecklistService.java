package com.growing.app.service;

import com.growing.app.dto.InterviewPreparationChecklistDTO;
import com.growing.app.entity.InterviewPreparationChecklist;
import com.growing.app.entity.InterviewStage;
import com.growing.app.entity.JobApplication;
import com.growing.app.repository.InterviewPreparationChecklistRepository;
import com.growing.app.repository.InterviewStageRepository;
import com.growing.app.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 面试准备清单Service
 * Phase 7: 求职管理模块 - Enhancements
 */
@Service
public class InterviewPreparationChecklistService {

    @Autowired
    private InterviewPreparationChecklistRepository checklistRepository;

    @Autowired
    private InterviewStageRepository interviewStageRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    /**
     * 根据面试阶段ID获取所有准备清单项
     */
    public List<InterviewPreparationChecklistDTO> getChecklistByStageId(Long stageId, Long userId) {
        // Verify ownership
        InterviewStage stage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此面试阶段的准备清单");
        }

        return checklistRepository.findByInterviewStageIdOrderBySortOrderAsc(stageId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据面试阶段ID获取重点准备清单项（用于打印）
     */
    public List<InterviewPreparationChecklistDTO> getPriorityChecklistByStageId(Long stageId, Long userId) {
        // Verify ownership
        InterviewStage stage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此面试阶段的准备清单");
        }

        return checklistRepository.findByInterviewStageIdAndIsPriorityOrderBySortOrderAsc(stageId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建准备清单项
     */
    @Transactional
    public InterviewPreparationChecklistDTO createChecklist(Long userId, InterviewPreparationChecklistDTO dto) {
        // Verify ownership
        InterviewStage stage = interviewStageRepository.findById(dto.getInterviewStageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此面试阶段添加准备清单");
        }

        InterviewPreparationChecklist checklist = new InterviewPreparationChecklist();
        checklist.setInterviewStageId(dto.getInterviewStageId());
        checklist.setChecklistItem(dto.getChecklistItem());
        checklist.setIsPriority(dto.getIsPriority() != null ? dto.getIsPriority() : false);
        checklist.setCategory(dto.getCategory());
        checklist.setNotes(dto.getNotes());
        checklist.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        checklist = checklistRepository.save(checklist);
        return convertToDTO(checklist);
    }

    /**
     * 批量创建准备清单项
     */
    @Transactional
    public List<InterviewPreparationChecklistDTO> batchCreateChecklist(Long userId, List<InterviewPreparationChecklistDTO> dtos) {
        if (dtos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "准备清单列表不能为空");
        }

        // Verify ownership using first item
        Long stageId = dtos.get(0).getInterviewStageId();
        InterviewStage stage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此面试阶段添加准备清单");
        }

        // Create all items
        return dtos.stream()
                .map(dto -> {
                    InterviewPreparationChecklist checklist = new InterviewPreparationChecklist();
                    checklist.setInterviewStageId(dto.getInterviewStageId());
                    checklist.setChecklistItem(dto.getChecklistItem());
                    checklist.setIsPriority(dto.getIsPriority() != null ? dto.getIsPriority() : false);
                    checklist.setCategory(dto.getCategory());
                    checklist.setNotes(dto.getNotes());
                    checklist.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
                    return convertToDTO(checklistRepository.save(checklist));
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新准备清单项
     */
    @Transactional
    public InterviewPreparationChecklistDTO updateChecklist(Long id, Long userId, InterviewPreparationChecklistDTO dto) {
        InterviewPreparationChecklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "准备清单项不存在"));

        // Verify ownership
        InterviewStage stage = interviewStageRepository.findById(checklist.getInterviewStageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此准备清单项");
        }

        checklist.setChecklistItem(dto.getChecklistItem());
        checklist.setIsPriority(dto.getIsPriority());
        checklist.setCategory(dto.getCategory());
        checklist.setNotes(dto.getNotes());
        checklist.setSortOrder(dto.getSortOrder());

        checklist = checklistRepository.save(checklist);
        return convertToDTO(checklist);
    }

    /**
     * 删除准备清单项
     */
    @Transactional
    public void deleteChecklist(Long id, Long userId) {
        InterviewPreparationChecklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "准备清单项不存在"));

        // Verify ownership
        InterviewStage stage = interviewStageRepository.findById(checklist.getInterviewStageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此准备清单项");
        }

        checklistRepository.delete(checklist);
    }

    private InterviewPreparationChecklistDTO convertToDTO(InterviewPreparationChecklist checklist) {
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
