package com.growing.app.service;

import com.growing.app.dto.JobSearchStatsDTO;
import com.growing.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 求职管理统计服务
 * Phase 7 - Dashboard statistics service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobSearchStatsService {

    private final ResumeRepository resumeRepository;
    private final ProjectExperienceRepository projectExperienceRepository;
    private final ManagementExperienceRepository managementExperienceRepository;
    private final CompanyRepository companyRepository;
    private final JobApplicationRepository jobApplicationRepository;

    /**
     * 获取用户的求职管理统计数据
     */
    public JobSearchStatsDTO getUserStats(Long userId) {
        JobSearchStatsDTO stats = new JobSearchStatsDTO();

        // 统计简历数量
        stats.setResumeCount(resumeRepository.countByUserId(userId));

        // 统计项目经验数量
        stats.setProjectCount(projectExperienceRepository.countByUserId(userId));

        // 统计人员管理案例数量
        stats.setManagementCount(managementExperienceRepository.countByUserId(userId));

        // 统计公司数量
        stats.setCompanyCount(companyRepository.countByUserId(userId));

        // 统计职位申请数量
        stats.setJobApplicationCount(jobApplicationRepository.countByUserId(userId));

        return stats;
    }
}
