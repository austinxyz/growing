package com.growing.app.service;

import com.growing.app.dto.InterviewFeedbackDTO;
import com.growing.app.entity.InterviewFeedback;
import com.growing.app.entity.InterviewRecord;
import com.growing.app.entity.JobApplication;
import com.growing.app.repository.InterviewFeedbackRepository;
import com.growing.app.repository.InterviewRecordRepository;
import com.growing.app.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewFeedbackService {

    @Autowired
    private InterviewFeedbackRepository interviewFeedbackRepository;

    @Autowired
    private InterviewRecordRepository interviewRecordRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    public List<InterviewFeedbackDTO> getFeedbackByRecordId(Long recordId, Long userId) {
        InterviewRecord record = interviewRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此面试记录的反馈");
        }

        return interviewFeedbackRepository.findByInterviewRecordIdOrderByFeedbackDate(recordId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterviewFeedbackDTO createFeedback(Long userId, InterviewFeedbackDTO dto) {
        InterviewRecord record = interviewRecordRepository.findById(dto.getInterviewRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此面试记录添加反馈");
        }

        InterviewFeedback feedback = new InterviewFeedback();
        feedback.setInterviewRecordId(dto.getInterviewRecordId());
        feedback.setFeedbackDate(dto.getFeedbackDate());
        feedback.setFeedbackSource(dto.getFeedbackSource());
        feedback.setFeedbackContent(dto.getFeedbackContent());

        feedback = interviewFeedbackRepository.save(feedback);
        return convertToDTO(feedback);
    }

    @Transactional
    public InterviewFeedbackDTO updateFeedback(Long id, Long userId, InterviewFeedbackDTO dto) {
        InterviewFeedback feedback = interviewFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试反馈不存在"));

        InterviewRecord record = interviewRecordRepository.findById(feedback.getInterviewRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此面试反馈");
        }

        feedback.setFeedbackDate(dto.getFeedbackDate());
        feedback.setFeedbackSource(dto.getFeedbackSource());
        feedback.setFeedbackContent(dto.getFeedbackContent());

        feedback = interviewFeedbackRepository.save(feedback);
        return convertToDTO(feedback);
    }

    @Transactional
    public void deleteFeedback(Long id, Long userId) {
        InterviewFeedback feedback = interviewFeedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试反馈不存在"));

        InterviewRecord record = interviewRecordRepository.findById(feedback.getInterviewRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此面试反馈");
        }

        interviewFeedbackRepository.delete(feedback);
    }

    private InterviewFeedbackDTO convertToDTO(InterviewFeedback feedback) {
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
