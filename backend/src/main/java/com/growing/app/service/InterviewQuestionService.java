package com.growing.app.service;

import com.growing.app.dto.InterviewQuestionDTO;
import com.growing.app.entity.InterviewQuestion;
import com.growing.app.entity.InterviewRecord;
import com.growing.app.entity.JobApplication;
import com.growing.app.repository.InterviewQuestionRepository;
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
public class InterviewQuestionService {

    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;

    @Autowired
    private InterviewRecordRepository interviewRecordRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    public List<InterviewQuestionDTO> getQuestionsByRecordId(Long recordId, Long userId) {
        InterviewRecord record = interviewRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此面试记录的问题");
        }

        return interviewQuestionRepository.findByInterviewRecordIdOrderByQuestionOrder(recordId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterviewQuestionDTO createQuestion(Long userId, InterviewQuestionDTO dto) {
        InterviewRecord record = interviewRecordRepository.findById(dto.getInterviewRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此面试记录添加问题");
        }

        InterviewQuestion question = new InterviewQuestion();
        question.setInterviewRecordId(dto.getInterviewRecordId());
        question.setQuestionOrder(dto.getQuestionOrder());
        question.setQuestionDescription(dto.getQuestionDescription());
        question.setQuestionType(dto.getQuestionType());
        question.setMyAnswer(dto.getMyAnswer());
        question.setRelatedQuestionId(dto.getRelatedQuestionId());
        question.setAnswerQuality(dto.getAnswerQuality());

        question = interviewQuestionRepository.save(question);
        return convertToDTO(question);
    }

    @Transactional
    public InterviewQuestionDTO updateQuestion(Long id, Long userId, InterviewQuestionDTO dto) {
        InterviewQuestion question = interviewQuestionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试问题不存在"));

        InterviewRecord record = interviewRecordRepository.findById(question.getInterviewRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此面试问题");
        }

        question.setQuestionOrder(dto.getQuestionOrder());
        question.setQuestionDescription(dto.getQuestionDescription());
        question.setQuestionType(dto.getQuestionType());
        question.setMyAnswer(dto.getMyAnswer());
        question.setRelatedQuestionId(dto.getRelatedQuestionId());
        question.setAnswerQuality(dto.getAnswerQuality());

        question = interviewQuestionRepository.save(question);
        return convertToDTO(question);
    }

    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        InterviewQuestion question = interviewQuestionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试问题不存在"));

        InterviewRecord record = interviewRecordRepository.findById(question.getInterviewRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试记录不存在"));

        JobApplication application = jobApplicationRepository.findById(record.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此面试问题");
        }

        interviewQuestionRepository.delete(question);
    }

    private InterviewQuestionDTO convertToDTO(InterviewQuestion question) {
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
}
