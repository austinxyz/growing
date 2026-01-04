package com.growing.app.service;

import com.growing.app.dto.RecruiterCommunicationDTO;
import com.growing.app.entity.RecruiterCommunication;
import com.growing.app.entity.Recruiter;
import com.growing.app.entity.JobApplication;
import com.growing.app.repository.RecruiterCommunicationRepository;
import com.growing.app.repository.RecruiterRepository;
import com.growing.app.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruiterCommunicationService {

    @Autowired
    private RecruiterCommunicationRepository recruiterCommunicationRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    public List<RecruiterCommunicationDTO> getCommunicationsByRecruiter(Long recruiterId, Long userId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此Recruiter的沟通记录");
        }

        return recruiterCommunicationRepository.findByRecruiterIdOrderByCommunicationDateDesc(recruiterId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecruiterCommunicationDTO createCommunication(Long userId, RecruiterCommunicationDTO dto) {
        Recruiter recruiter = recruiterRepository.findById(dto.getRecruiterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此Recruiter添加沟通记录");
        }

        // 验证JobApplication存在且属于用户（如果提供）
        if (dto.getJobApplicationId() != null) {
            JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

            if (!application.getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "求职申请不属于当前用户");
            }
        }

        RecruiterCommunication communication = new RecruiterCommunication();
        communication.setRecruiterId(dto.getRecruiterId());
        communication.setJobApplicationId(dto.getJobApplicationId());
        communication.setCommunicationDate(dto.getCommunicationDate());
        communication.setCommunicationMethod(dto.getCommunicationMethod());
        communication.setCommunicationContent(dto.getCommunicationContent());
        communication.setNextAction(dto.getNextAction());

        communication = recruiterCommunicationRepository.save(communication);
        return convertToDTO(communication);
    }

    @Transactional
    public RecruiterCommunicationDTO updateCommunication(Long id, Long userId, RecruiterCommunicationDTO dto) {
        RecruiterCommunication communication = recruiterCommunicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "沟通记录不存在"));

        Recruiter recruiter = recruiterRepository.findById(communication.getRecruiterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此沟通记录");
        }

        communication.setCommunicationDate(dto.getCommunicationDate());
        communication.setCommunicationMethod(dto.getCommunicationMethod());
        communication.setCommunicationContent(dto.getCommunicationContent());
        communication.setNextAction(dto.getNextAction());

        if (dto.getJobApplicationId() != null && !dto.getJobApplicationId().equals(communication.getJobApplicationId())) {
            JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

            if (!application.getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "求职申请不属于当前用户");
            }
            communication.setJobApplicationId(dto.getJobApplicationId());
        }

        communication = recruiterCommunicationRepository.save(communication);
        return convertToDTO(communication);
    }

    @Transactional
    public void deleteCommunication(Long id, Long userId) {
        RecruiterCommunication communication = recruiterCommunicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "沟通记录不存在"));

        Recruiter recruiter = recruiterRepository.findById(communication.getRecruiterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此沟通记录");
        }

        recruiterCommunicationRepository.delete(communication);
    }

    private RecruiterCommunicationDTO convertToDTO(RecruiterCommunication communication) {
        RecruiterCommunicationDTO dto = new RecruiterCommunicationDTO();
        dto.setId(communication.getId());
        dto.setRecruiterId(communication.getRecruiterId());
        dto.setJobApplicationId(communication.getJobApplicationId());
        dto.setCommunicationDate(communication.getCommunicationDate());
        dto.setCommunicationMethod(communication.getCommunicationMethod());
        dto.setCommunicationContent(communication.getCommunicationContent());
        dto.setNextAction(communication.getNextAction());
        dto.setCreatedAt(communication.getCreatedAt());
        return dto;
    }
}
