package com.growing.app.service;

import com.growing.app.dto.InterviewRecordDTO;
import com.growing.app.dto.InterviewQuestionDTO;
import com.growing.app.dto.InterviewFeedbackDTO;
import com.growing.app.entity.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewRecordService {

    @Autowired
    private InterviewRecordRepository interviewRecordRepository;

    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;

    @Autowired
    private InterviewFeedbackRepository interviewFeedbackRepository;

    @Autowired
    private InterviewStageRepository interviewStageRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    // 获取求职申请的所有面试记录
    public List<InterviewRecordDTO> getRecordsByApplicationId(Long applicationId, Long userId) {
        // 验证求职申请所有权
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此求职申请的面试记录");
        }

        return interviewRecordRepository.findByJobApplicationIdOrderByInterviewDateDesc(applicationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取面试记录详情（包含questions和feedback）
    public InterviewRecordDTO getRecordById(Long id, Long userId) {
        InterviewRecord record = interviewRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        // 验证所有权（通过job_application）
        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此面试记录");
        }

        InterviewRecordDTO dto = convertToDTO(record);

        // DTO Completeness Checklist: all collections populated
        dto.setQuestions(interviewQuestionRepository.findByInterviewRecordIdOrderByQuestionOrder(id).stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList()));

        dto.setFeedback(interviewFeedbackRepository.findByInterviewRecordIdOrderByFeedbackDate(id).stream()
                .map(this::convertToFeedbackDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // 创建面试记录
    @Transactional
    public InterviewRecordDTO createRecord(Long userId, InterviewRecordDTO dto) {
        // 验证求职申请所有权
        JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此求职申请创建面试记录");
        }

        // 验证interview stage存在（如果提供）
        if (dto.getInterviewStageId() != null) {
            if (!interviewStageRepository.existsById(dto.getInterviewStageId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在");
            }
        }

        InterviewRecord record = new InterviewRecord();
        record.setJobApplicationId(dto.getJobApplicationId());
        record.setInterviewStageId(dto.getInterviewStageId());
        record.setInterviewDate(dto.getInterviewDate());
        record.setInterviewerName(dto.getInterviewerName());
        record.setInterviewerPosition(dto.getInterviewerPosition());
        record.setInterviewerLinkedin(dto.getInterviewerLinkedin());
        record.setInterviewFormat(dto.getInterviewFormat());
        record.setInterviewDuration(dto.getInterviewDuration());
        record.setOverallPerformance(dto.getOverallPerformance());
        record.setTechnicalDepth(dto.getTechnicalDepth());
        record.setCommunication(dto.getCommunication());
        record.setProblemSolving(dto.getProblemSolving());
        record.setSelfSummary(dto.getSelfSummary());

        record = interviewRecordRepository.save(record);
        return convertToDTO(record);
    }

    // 更新面试记录
    @Transactional
    public InterviewRecordDTO updateRecord(Long id, Long userId, InterviewRecordDTO dto) {
        InterviewRecord record = interviewRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        // 验证所有权
        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此面试记录");
        }

        // 验证interview stage存在（如果更换）
        if (dto.getInterviewStageId() != null && !dto.getInterviewStageId().equals(record.getInterviewStageId())) {
            if (!interviewStageRepository.existsById(dto.getInterviewStageId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在");
            }
            record.setInterviewStageId(dto.getInterviewStageId());
        }

        record.setInterviewDate(dto.getInterviewDate());
        record.setInterviewerName(dto.getInterviewerName());
        record.setInterviewerPosition(dto.getInterviewerPosition());
        record.setInterviewerLinkedin(dto.getInterviewerLinkedin());
        record.setInterviewFormat(dto.getInterviewFormat());
        record.setInterviewDuration(dto.getInterviewDuration());
        record.setOverallPerformance(dto.getOverallPerformance());
        record.setTechnicalDepth(dto.getTechnicalDepth());
        record.setCommunication(dto.getCommunication());
        record.setProblemSolving(dto.getProblemSolving());
        record.setSelfSummary(dto.getSelfSummary());

        record = interviewRecordRepository.save(record);
        return convertToDTO(record);
    }

    // 删除面试记录（cascade delete questions和feedback）
    @Transactional
    public void deleteRecord(Long id, Long userId) {
        InterviewRecord record = interviewRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        // 验证所有权
        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此面试记录");
        }

        interviewRecordRepository.delete(record);
    }

    // DTO Conversion
    private InterviewRecordDTO convertToDTO(InterviewRecord record) {
        InterviewRecordDTO dto = new InterviewRecordDTO();
        dto.setId(record.getId());
        dto.setJobApplicationId(record.getJobApplicationId());
        dto.setInterviewStageId(record.getInterviewStageId());

        // Populate stage name
        if (record.getInterviewStageId() != null) {
            interviewStageRepository.findById(record.getInterviewStageId())
                    .ifPresent(stage -> dto.setStageName(stage.getStageName()));
        }

        dto.setInterviewDate(record.getInterviewDate());
        dto.setInterviewerName(record.getInterviewerName());
        dto.setInterviewerPosition(record.getInterviewerPosition());
        dto.setInterviewerLinkedin(record.getInterviewerLinkedin());
        dto.setInterviewFormat(record.getInterviewFormat());
        dto.setInterviewDuration(record.getInterviewDuration());
        dto.setOverallPerformance(record.getOverallPerformance());
        dto.setTechnicalDepth(record.getTechnicalDepth());
        dto.setCommunication(record.getCommunication());
        dto.setProblemSolving(record.getProblemSolving());
        dto.setSelfSummary(record.getSelfSummary());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }

    private InterviewQuestionDTO convertToQuestionDTO(InterviewQuestion question) {
        InterviewQuestionDTO dto = new InterviewQuestionDTO();
        dto.setId(question.getId());
        dto.setInterviewRecordId(question.getInterviewRecordId());
        dto.setQuestionOrder(question.getQuestionOrder());
        dto.setQuestionDescription(question.getQuestionDescription());
        dto.setQuestionType(question.getQuestionType());
        dto.setMyAnswer(question.getMyAnswer());
        dto.setRelatedQuestionId(question.getRelatedQuestionId());
        dto.setAnswerQuality(question.getAnswerQuality());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        return dto;
    }

    private InterviewFeedbackDTO convertToFeedbackDTO(InterviewFeedback feedback) {
        InterviewFeedbackDTO dto = new InterviewFeedbackDTO();
        dto.setId(feedback.getId());
        dto.setInterviewRecordId(feedback.getInterviewRecordId());
        dto.setFeedbackDate(feedback.getFeedbackDate());
        dto.setFeedbackSource(feedback.getFeedbackSource());
        dto.setFeedbackContent(feedback.getFeedbackContent());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
