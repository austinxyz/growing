package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ActiveProgressDTO;
import com.growing.app.dto.JobApplicationDTO;
import com.growing.app.dto.InterviewStageDTO;
import com.growing.app.dto.InterviewRecordDTO;
import com.growing.app.dto.PriorityLevel;
import com.growing.app.dto.RecruiterInsightsDTO;
import com.growing.app.entity.*;
import com.growing.app.repository.*;
import com.growing.app.service.progress.ProgressCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    // Includes English canonical names, legacy names, and Chinese values found in production data.
    // ProgressCalculator.normalizeStatus() handles the mapping to canonical for all derived logic.
    private static final List<String> ACTIVE_STATUSES = List.of(
            "Applied", "Screening", "Interviewing", "Offer",
            "PhoneScreen", "Onsite",                   // legacy English
            "已投递", "筛选中", "面试中"                 // Chinese variants stored in DB
    );

    private static final java.util.Set<String> ALLOWED_SUBMISSION_TYPES =
            java.util.Set.of("Direct", "Referral", "RecruiterInbound", "Other");

    private static String validateSubmissionType(String value) {
        String v = (value == null || value.isBlank()) ? "Direct" : value;
        if (!ALLOWED_SUBMISSION_TYPES.contains(v)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "submissionType 不合法: " + value
                            + " (allowed: " + ALLOWED_SUBMISSION_TYPES + ")");
        }
        return v;
    }

    private static final List<String> CLOSED_STATUSES = List.of(
            "Rejected", "Withdrawn",
            "已拒绝", "已撤回"
    );

    /**
     * 面试进展看板：返回当前 active 申请，附带派生字段，按 priority+days 排序。
     */
    public List<ActiveProgressDTO> getActiveProgress(Long userId) {
        return loadProgressForStatuses(userId, ACTIVE_STATUSES);
    }

    /**
     * 已结案申请（Rejected / Withdrawn / 已拒绝 / 已撤回），用于"显示已结案" toggle 的复盘视图。
     * 复用 ActiveProgressDTO 形状；前端通过 applicationStatus 判定是否做 muted 视觉处理。
     */
    public List<ActiveProgressDTO> getClosedProgress(Long userId) {
        return loadProgressForStatuses(userId, CLOSED_STATUSES);
    }

    private List<ActiveProgressDTO> loadProgressForStatuses(Long userId, List<String> statuses) {
        List<JobApplication> apps = jobApplicationRepository
                .findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(userId, statuses);
        if (apps.isEmpty()) return List.of();

        List<Long> appIds = apps.stream().map(JobApplication::getId).toList();
        Map<Long, List<InterviewStage>> stagesByApp = interviewStageRepository
                .findByJobApplicationIdInOrderByStageOrder(appIds).stream()
                .collect(Collectors.groupingBy(InterviewStage::getJobApplicationId));
        Map<Long, List<InterviewRecord>> recordsByApp = interviewRecordRepository
                .findByJobApplicationIdInOrderByInterviewDateDesc(appIds).stream()
                .collect(Collectors.groupingBy(InterviewRecord::getJobApplicationId));
        // Batch-fetch companies to avoid N+1 (was 1 SELECT per active application)
        List<Long> companyIds = apps.stream()
                .map(JobApplication::getCompanyId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> companyNameById = companyRepository.findAllById(companyIds).stream()
                .collect(Collectors.toMap(Company::getId, Company::getCompanyName));

        return apps.stream()
                .map(a -> toActiveProgressDTO(a,
                        stagesByApp.getOrDefault(a.getId(), List.of()),
                        recordsByApp.getOrDefault(a.getId(), List.of()),
                        companyNameById.get(a.getCompanyId())))
                .sorted(Comparator
                        .comparingInt((ActiveProgressDTO d) -> d.priorityLevel().ordinal())
                        .thenComparing(Comparator.comparingInt(ActiveProgressDTO::daysSinceApplied).reversed()))
                .toList();
    }

    private ActiveProgressDTO toActiveProgressDTO(
            JobApplication app, List<InterviewStage> stages, List<InterviewRecord> records,
            String companyName) {
        int daysApplied = ProgressCalculator.daysSinceApplied(app, objectMapper);
        java.time.LocalDateTime latestRecordActivity = records.stream()
                .map(InterviewRecord::getUpdatedAt)
                .filter(java.util.Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        int daysSinceUpdate = ProgressCalculator.daysSinceLastUpdate(app, latestRecordActivity);
        LocalDate nextDate = nextFutureInterviewDate(records);
        String nextStageName = nextDate == null ? null : resolveStageNameForRecord(
                records.stream()
                        .filter(r -> r.getInterviewDate() != null
                                && nextDate.equals(r.getInterviewDate().toLocalDate()))
                        .findFirst().orElse(null),
                stages);
        PriorityLevel priority = ProgressCalculator.computePriority(
                app.getApplicationStatus(), app.getOfferDeadline(), daysSinceUpdate, nextDate);
        String microLabel = ProgressCalculator.buildMicroStageLabel(
                app.getApplicationStatus(), stages.size(), records.size(),
                latestRecordStageName(records, stages));
        ProgressCalculator.NextAction action = ProgressCalculator.buildNextAction(
                priority, app.getOfferDeadline(), nextDate, nextStageName);

        LocalDate appliedAt = ProgressCalculator.appliedAtDate(app, objectMapper);

        return new ActiveProgressDTO(
                app.getId(), app.getCompanyId(), companyName, app.getPositionName(),
                ProgressCalculator.normalizeStatus(app.getApplicationStatus()),
                ProgressCalculator.macroStageStep(app.getApplicationStatus()),
                microLabel, daysApplied, appliedAt, daysSinceUpdate,
                priority, action.type(), action.label(), action.date(),
                app.getSubmissionType());
    }

    private LocalDate nextFutureInterviewDate(List<InterviewRecord> records) {
        LocalDate today = LocalDate.now();
        return records.stream()
                .map(r -> r.getInterviewDate() == null ? null : r.getInterviewDate().toLocalDate())
                .filter(d -> d != null && !d.isBefore(today))
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private String latestRecordStageName(List<InterviewRecord> records, List<InterviewStage> stages) {
        // records arrive sorted by interviewDate DESC, so first one is latest
        return records.stream().findFirst()
                .map(r -> resolveStageNameForRecord(r, stages))
                .orElse(null);
    }

    private String resolveStageNameForRecord(InterviewRecord record, List<InterviewStage> stages) {
        if (record == null || record.getInterviewStageId() == null) return null;
        return stages.stream()
                .filter(s -> record.getInterviewStageId().equals(s.getId()))
                .map(InterviewStage::getStageName)
                .findFirst()
                .orElse(null);
    }

    // 获取用户所有求职申请
    public List<JobApplicationDTO> getAllApplicationsByUserId(Long userId) {
        return toListDTOs(jobApplicationRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    // 按状态获取
    public List<JobApplicationDTO> getApplicationsByStatus(Long userId, String status) {
        return toListDTOs(jobApplicationRepository.findByUserIdAndApplicationStatusOrderByCreatedAtDesc(userId, status));
    }

    // Batch-safe list conversion: 4 queries total regardless of list size (was 1 + 3N)
    private List<JobApplicationDTO> toListDTOs(List<JobApplication> apps) {
        if (apps.isEmpty()) return List.of();

        List<Long> appIds = apps.stream().map(JobApplication::getId).toList();
        List<Long> companyIds = apps.stream().map(JobApplication::getCompanyId)
                .filter(java.util.Objects::nonNull).distinct().toList();

        Map<Long, String> companyNameById = companyRepository.findAllById(companyIds).stream()
                .collect(Collectors.toMap(Company::getId, Company::getCompanyName));
        Map<Long, Integer> stageCounts = interviewStageRepository
                .findByJobApplicationIdInOrderByStageOrder(appIds).stream()
                .collect(Collectors.groupingBy(InterviewStage::getJobApplicationId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        Map<Long, Integer> recordCounts = interviewRecordRepository
                .findByJobApplicationIdInOrderByInterviewDateDesc(appIds).stream()
                .collect(Collectors.groupingBy(InterviewRecord::getJobApplicationId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        return apps.stream().map(a -> {
            JobApplicationDTO dto = mapApplicationFields(a);
            dto.setCompanyName(companyNameById.get(a.getCompanyId()));
            dto.setInterviewStageCount(stageCounts.getOrDefault(a.getId(), 0));
            dto.setInterviewRecordCount(recordCounts.getOrDefault(a.getId(), 0));
            return dto;
        }).collect(Collectors.toList());
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

        // Fetch stages once, build map to avoid N+1 in convertToInterviewDTO
        List<InterviewStage> stages = interviewStageRepository.findByJobApplicationIdOrderByStageOrder(id);
        Map<Long, String> stageNameById = stages.stream()
                .collect(Collectors.toMap(InterviewStage::getId, InterviewStage::getStageName));

        List<InterviewRecordDTO> interviews = interviewRecordRepository
                .findByJobApplicationIdOrderByInterviewDateDesc(id).stream()
                .map(r -> convertToInterviewDTO(r, stageNameById))
                .collect(Collectors.toList());

        dto.setStages(stages.stream().map(this::convertToStageDTO).collect(Collectors.toList()));
        dto.setInterviews(interviews);
        // Override counts with exact sizes — avoids redundant COUNT queries from convertToDTO
        dto.setInterviewStageCount(stages.size());
        dto.setInterviewRecordCount(interviews.size());

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
        application.setSubmissionType(validateSubmissionType(dto.getSubmissionType()));
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
        application.setSubmissionType(validateSubmissionType(dto.getSubmissionType()));
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

    // Pure field mapping — no DB queries. Used by both single and batch paths.
    private JobApplicationDTO mapApplicationFields(JobApplication application) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUserId());
        dto.setCompanyId(application.getCompanyId());
        dto.setPositionName(application.getPositionName());
        dto.setPositionLevel(application.getPositionLevel());
        dto.setPostedDate(application.getPostedDate());
        dto.setJobUrl(application.getJobUrl());
        dto.setJobStatus(application.getJobStatus());
        dto.setQualifications(application.getQualifications());
        dto.setResponsibilities(application.getResponsibilities());
        dto.setApplicationStatus(application.getApplicationStatus());
        dto.setSubmissionType(application.getSubmissionType());
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

        try {
            if (application.getStatusHistory() != null) {
                dto.setStatusHistory(objectMapper.readValue(application.getStatusHistory(),
                        new TypeReference<List<JobApplicationDTO.StatusHistoryDTO>>() {}));
            }
            if (application.getRejectionReasons() != null) {
                dto.setRejectionReasons(objectMapper.readValue(application.getRejectionReasons(),
                        new TypeReference<List<String>>() {}));
            }
            if (application.getRecruiterInsights() != null) {
                dto.setRecruiterInsights(objectMapper.readValue(application.getRecruiterInsights(),
                        RecruiterInsightsDTO.class));
            }
        } catch (Exception e) {
            dto.setStatusHistory(Collections.emptyList());
            dto.setRejectionReasons(Collections.emptyList());
            dto.setRecruiterInsights(null);
        }

        return dto;
    }

    // Single-entity path: used for create / update / getByCompany (3 extra queries acceptable for 1 item)
    private JobApplicationDTO convertToDTO(JobApplication application) {
        JobApplicationDTO dto = mapApplicationFields(application);
        companyRepository.findById(application.getCompanyId())
                .ifPresent(company -> dto.setCompanyName(company.getCompanyName()));
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

    private InterviewRecordDTO convertToInterviewDTO(InterviewRecord record, Map<Long, String> stageNameById) {
        InterviewRecordDTO dto = new InterviewRecordDTO();
        dto.setId(record.getId());
        dto.setJobApplicationId(record.getJobApplicationId());
        dto.setInterviewStageId(record.getInterviewStageId());

        if (record.getInterviewStageId() != null) {
            dto.setStageName(stageNameById.get(record.getInterviewStageId()));
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
