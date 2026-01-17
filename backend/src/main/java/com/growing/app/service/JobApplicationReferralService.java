package com.growing.app.service;

import com.growing.app.dto.JobApplicationReferralDTO;
import com.growing.app.dto.ReferralDTO;
import com.growing.app.entity.JobApplication;
import com.growing.app.entity.JobApplicationReferral;
import com.growing.app.entity.Referral;
import com.growing.app.repository.JobApplicationReferralRepository;
import com.growing.app.repository.JobApplicationRepository;
import com.growing.app.repository.ReferralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JobApplicationReferral Service
 * Phase 7: 求职管理模块
 */
@Service
public class JobApplicationReferralService {

    @Autowired
    private JobApplicationReferralRepository jobApplicationReferralRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ReferralRepository referralRepository;

    /**
     * 获取职位的所有关联人脉
     */
    public List<JobApplicationReferralDTO> getReferralsByJobApplication(Long jobApplicationId, Long userId) {
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位申请不存在"));

        if (!jobApplication.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此职位申请");
        }

        List<JobApplicationReferral> referrals = jobApplicationReferralRepository
                .findByJobApplicationId(jobApplicationId);

        return referrals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 添加职位-人脉关联
     */
    @Transactional
    public JobApplicationReferralDTO addReferralToJob(Long jobApplicationId, Long userId, JobApplicationReferralDTO dto) {
        JobApplication jobApplication = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位申请不存在"));

        if (!jobApplication.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此职位申请");
        }

        Referral referral = referralRepository.findById(dto.getReferralId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "人脉不存在"));

        if (!referral.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权使用此人脉");
        }

        // 检查是否已存在
        jobApplicationReferralRepository.findByJobApplicationIdAndReferralId(jobApplicationId, dto.getReferralId())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该人脉已关联到此职位");
                });

        JobApplicationReferral entity = new JobApplicationReferral();
        entity.setJobApplicationId(jobApplicationId);
        entity.setReferralId(dto.getReferralId());
        entity.setRoleType(dto.getRoleType());
        entity.setNotes(dto.getNotes());

        entity = jobApplicationReferralRepository.save(entity);
        return convertToDTO(entity);
    }

    /**
     * 更新职位-人脉关联
     */
    @Transactional
    public JobApplicationReferralDTO updateJobReferral(Long id, Long userId, JobApplicationReferralDTO dto) {
        JobApplicationReferral entity = jobApplicationReferralRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "关联不存在"));

        JobApplication jobApplication = jobApplicationRepository.findById(entity.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位申请不存在"));

        if (!jobApplication.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此关联");
        }

        entity.setRoleType(dto.getRoleType());
        entity.setNotes(dto.getNotes());

        entity = jobApplicationReferralRepository.save(entity);
        return convertToDTO(entity);
    }

    /**
     * 删除职位-人脉关联
     */
    @Transactional
    public void removeReferralFromJob(Long id, Long userId) {
        JobApplicationReferral entity = jobApplicationReferralRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "关联不存在"));

        JobApplication jobApplication = jobApplicationRepository.findById(entity.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位申请不存在"));

        if (!jobApplication.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此关联");
        }

        jobApplicationReferralRepository.delete(entity);
    }

    private JobApplicationReferralDTO convertToDTO(JobApplicationReferral entity) {
        JobApplicationReferralDTO dto = new JobApplicationReferralDTO();
        dto.setId(entity.getId());
        dto.setJobApplicationId(entity.getJobApplicationId());
        dto.setReferralId(entity.getReferralId());
        dto.setRoleType(entity.getRoleType());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // 加载referral详情
        referralRepository.findById(entity.getReferralId()).ifPresent(referral -> {
            dto.setReferral(convertReferralToDTO(referral));
        });

        return dto;
    }

    private ReferralDTO convertReferralToDTO(Referral referral) {
        ReferralDTO dto = new ReferralDTO();
        dto.setId(referral.getId());
        dto.setReferralName(referral.getReferralName());
        dto.setRelationship(referral.getRelationship());
        dto.setPosition(referral.getPosition());
        dto.setEmail(referral.getEmail());
        dto.setPhone(referral.getPhone());
        dto.setLinkedinUrl(referral.getLinkedinUrl());
        dto.setNotes(referral.getNotes());
        return dto;
    }
}
