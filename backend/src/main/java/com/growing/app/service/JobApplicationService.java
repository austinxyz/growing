package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.JobApplicationDTO;
import com.growing.app.dto.InterviewStageDTO;
import com.growing.app.dto.InterviewRecordDTO;
import com.growing.app.dto.RecruiterInsightsDTO;
import com.growing.app.entity.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private InterviewStageRepository interviewStageRepository;

    @Autowired
    private InterviewRecordRepository interviewRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 获取用户所有求职申请
    public List<JobApplicationDTO> getAllApplicationsByUserId(Long userId) {
        return jobApplicationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按状态获取
    public List<JobApplicationDTO> getApplicationsByStatus(Long userId, String status) {
        return jobApplicationRepository.findByUserIdAndApplicationStatusOrderByCreatedAtDesc(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按公司获取
    public List<JobApplicationDTO> getJobsByCompany(Long companyId, Long userId) {
        // 验证公司属于该用户
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此公司");
        }

        return jobApplicationRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取申请详情（包含所有nested resources）
    public JobApplicationDTO getApplicationById(Long id, Long userId) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        // 验证所有权
        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此求职申请");
        }

        JobApplicationDTO dto = convertToDTO(application);

        // DTO Completeness Checklist: all collections populated
        dto.setStages(interviewStageRepository.findByJobApplicationIdOrderByStageOrder(id).stream()
                .map(this::convertToStageDTO)
                .collect(Collectors.toList()));

        dto.setInterviews(interviewRecordRepository.findByJobApplicationIdOrderByInterviewDateDesc(id).stream()
                .map(this::convertToInterviewDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // 创建求职申请
    @Transactional
    public JobApplicationDTO createApplication(Long userId, JobApplicationDTO dto) {
        // 验证公司存在
        if (!companyRepository.existsById(dto.getCompanyId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在");
        }

        JobApplication application = new JobApplication();
        application.setUserId(userId);
        application.setCompanyId(dto.getCompanyId());
        application.setPositionName(dto.getPositionName());
        application.setPositionLevel(dto.getPositionLevel());
        application.setPostedDate(dto.getPostedDate());
        application.setJobUrl(dto.getJobUrl());
        application.setJobStatus(dto.getJobStatus() != null ? dto.getJobStatus() : "Open");
        application.setQualifications(dto.getQualifications());
        application.setResponsibilities(dto.getResponsibilities());
        application.setApplicationStatus(dto.getApplicationStatus() != null ? dto.getApplicationStatus() : "NotApplied");
        application.setOfferDecision(dto.getOfferDecision());
        application.setOfferNotes(dto.getOfferNotes());
        application.setRejectedStage(dto.getRejectedStage());
        application.setImprovementPlan(dto.getImprovementPlan());
        application.setNotes(dto.getNotes());

        // Convert JSON fields
        try {
            if (dto.getStatusHistory() != null) {
                application.setStatusHistory(objectMapper.writeValueAsString(dto.getStatusHistory()));
            }
            if (dto.getRejectionReasons() != null) {
                application.setRejectionReasons(objectMapper.writeValueAsString(dto.getRejectionReasons()));
            }
            if (dto.getRecruiterInsights() != null) {
                application.setRecruiterInsights(objectMapper.writeValueAsString(dto.getRecruiterInsights()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        // Set offer/rejection timestamps and amounts
        application.setOfferReceivedAt(dto.getOfferReceivedAt());
        application.setBaseSalary(dto.getBaseSalary());
        application.setBonus(dto.getBonus());
        application.setStockValue(dto.getStockValue());
        application.setTotalCompensation(dto.getTotalCompensation());
        application.setOfferDeadline(dto.getOfferDeadline());
        application.setRejectedAt(dto.getRejectedAt());

        application = jobApplicationRepository.save(application);
        return convertToDTO(application);
    }

    // 更新求职申请
    @Transactional
    public JobApplicationDTO updateApplication(Long id, Long userId, JobApplicationDTO dto) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        // 验证所有权
        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此求职申请");
        }

        // 如果更换公司，验证新公司存在
        if (!application.getCompanyId().equals(dto.getCompanyId())) {
            if (!companyRepository.existsById(dto.getCompanyId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在");
            }
            application.setCompanyId(dto.getCompanyId());
        }

        application.setPositionName(dto.getPositionName());
        application.setPositionLevel(dto.getPositionLevel());
        application.setPostedDate(dto.getPostedDate());
        application.setJobUrl(dto.getJobUrl());
        application.setJobStatus(dto.getJobStatus() != null ? dto.getJobStatus() : "Open");
        application.setQualifications(dto.getQualifications());
        application.setResponsibilities(dto.getResponsibilities());
        application.setApplicationStatus(dto.getApplicationStatus());
        application.setOfferDecision(dto.getOfferDecision());
        application.setOfferNotes(dto.getOfferNotes());
        application.setRejectedStage(dto.getRejectedStage());
        application.setImprovementPlan(dto.getImprovementPlan());
        application.setNotes(dto.getNotes());

        // Update JSON fields
        try {
            if (dto.getStatusHistory() != null) {
                application.setStatusHistory(objectMapper.writeValueAsString(dto.getStatusHistory()));
            }
            if (dto.getRejectionReasons() != null) {
                application.setRejectionReasons(objectMapper.writeValueAsString(dto.getRejectionReasons()));
            }
            if (dto.getRecruiterInsights() != null) {
                application.setRecruiterInsights(objectMapper.writeValueAsString(dto.getRecruiterInsights()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        application.setOfferReceivedAt(dto.getOfferReceivedAt());
        application.setBaseSalary(dto.getBaseSalary());
        application.setBonus(dto.getBonus());
        application.setStockValue(dto.getStockValue());
        application.setTotalCompensation(dto.getTotalCompensation());
        application.setOfferDeadline(dto.getOfferDeadline());
        application.setRejectedAt(dto.getRejectedAt());

        application = jobApplicationRepository.save(application);
        return convertToDTO(application);
    }

    // 删除求职申请（cascade delete所有关联资源）
    @Transactional
    public void deleteApplication(Long id, Long userId) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        // 验证所有权
        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此求职申请");
        }

        jobApplicationRepository.delete(application);
    }

    // DTO Conversion
    private JobApplicationDTO convertToDTO(JobApplication application) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUserId());
        dto.setCompanyId(application.getCompanyId());

        // Populate company name
        companyRepository.findById(application.getCompanyId())
                .ifPresent(company -> dto.setCompanyName(company.getCompanyName()));

        dto.setPositionName(application.getPositionName());
        dto.setPositionLevel(application.getPositionLevel());
        dto.setPostedDate(application.getPostedDate());
        dto.setJobUrl(application.getJobUrl());
        dto.setJobStatus(application.getJobStatus());
        dto.setQualifications(application.getQualifications());
        dto.setResponsibilities(application.getResponsibilities());
        dto.setApplicationStatus(application.getApplicationStatus());
        dto.setStatusUpdatedAt(application.getStatusUpdatedAt());
        dto.setOfferReceivedAt(application.getOfferReceivedAt());
        dto.setBaseSalary(application.getBaseSalary());
        dto.setBonus(application.getBonus());
        dto.setStockValue(application.getStockValue());
        dto.setTotalCompensation(application.getTotalCompensation());
        dto.setOfferDeadline(application.getOfferDeadline());
        dto.setOfferDecision(application.getOfferDecision());
        dto.setOfferNotes(application.getOfferNotes());
        dto.setRejectedAt(application.getRejectedAt());
        dto.setRejectedStage(application.getRejectedStage());
        dto.setImprovementPlan(application.getImprovementPlan());
        dto.setNotes(application.getNotes());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());

        // Parse JSON fields
        try {
            if (application.getStatusHistory() != null) {
                dto.setStatusHistory(objectMapper.readValue(application.getStatusHistory(),
                        new TypeReference<List<JobApplicationDTO.StatusHistoryDTO>>() {}));
            }
            if (application.getRejectionReasons() != null) {
                dto.setRejectionReasons(objectMapper.readValue(application.getRejectionReasons(),
                        new TypeReference<List<String>>() {}));
            }
            // DTO Completeness Checklist: Parse recruiterInsights
            if (application.getRecruiterInsights() != null) {
                dto.setRecruiterInsights(objectMapper.readValue(application.getRecruiterInsights(),
                        RecruiterInsightsDTO.class));
            }
        } catch (Exception e) {
            // If JSON parsing fails, set empty lists/null
            dto.setStatusHistory(Collections.emptyList());
            dto.setRejectionReasons(Collections.emptyList());
            dto.setRecruiterInsights(null);
        }

        // Populate statistics (for list view)
        dto.setInterviewStageCount(interviewStageRepository.countByJobApplicationId(application.getId()));
        dto.setInterviewRecordCount(interviewRecordRepository.countByJobApplicationId(application.getId()));

        return dto;
    }

    private InterviewStageDTO convertToStageDTO(InterviewStage stage) {
        InterviewStageDTO dto = new InterviewStageDTO();
        dto.setId(stage.getId());
        dto.setJobApplicationId(stage.getJobApplicationId());
        dto.setStageName(stage.getStageName());
        dto.setStageOrder(stage.getStageOrder());
        dto.setPreparationNotes(stage.getPreparationNotes());
        dto.setCreatedAt(stage.getCreatedAt());
        dto.setUpdatedAt(stage.getUpdatedAt());
        return dto;
    }

    private InterviewRecordDTO convertToInterviewDTO(InterviewRecord record) {
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
}
