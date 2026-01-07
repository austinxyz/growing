package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ResumeDTO;
import com.growing.app.dto.ResumeExperienceDTO;
import com.growing.app.dto.ResumeSkillDTO;
import com.growing.app.dto.ResumeEducationDTO;
import com.growing.app.dto.ResumeCertificationDTO;
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
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeExperienceRepository resumeExperienceRepository;

    @Autowired
    private ResumeSkillRepository resumeSkillRepository;

    @Autowired
    private ResumeEducationRepository resumeEducationRepository;

    @Autowired
    private ResumeCertificationRepository resumeCertificationRepository;

    @Autowired
    private ProjectExperienceRepository projectExperienceRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 获取用户所有简历版本
    public List<ResumeDTO> getAllResumesByUserId(Long userId) {
        return resumeRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取用户默认简历
    public ResumeDTO getDefaultResumeByUserId(Long userId) {
        return resumeRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到默认简历"));
    }

    // 获取简历详情（包含所有nested resources）
    public ResumeDTO getResumeById(Long id, Long userId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        // 验证所有权
        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此简历");
        }

        ResumeDTO dto = convertToDTO(resume);

        // DTO Completeness Checklist: all collections populated
        dto.setExperiences(resumeExperienceRepository.findByResumeIdOrderBySortOrder(id).stream()
                .map(this::convertToExperienceDTO)
                .collect(Collectors.toList()));

        dto.setSkills(resumeSkillRepository.findByResumeIdOrderBySortOrder(id).stream()
                .map(this::convertToSkillDTO)
                .collect(Collectors.toList()));

        dto.setEducation(resumeEducationRepository.findByResumeIdOrderBySortOrder(id).stream()
                .map(this::convertToEducationDTO)
                .collect(Collectors.toList()));

        dto.setCertifications(resumeCertificationRepository.findByResumeIdOrderBySortOrder(id).stream()
                .map(this::convertToCertificationDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // 创建新简历版本
    @Transactional
    public ResumeDTO createResume(Long userId, ResumeDTO dto) {
        // 检查版本名是否已存在
        if (resumeRepository.existsByUserIdAndVersionName(userId, dto.getVersionName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "简历版本名已存在");
        }

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setVersionName(dto.getVersionName());
        resume.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        resume.setAbout(dto.getAbout());
        resume.setCareerObjective(dto.getCareerObjective());
        resume.setEmail(dto.getEmail());
        resume.setPhone(dto.getPhone());
        resume.setAddress(dto.getAddress());
        resume.setLinkedinUrl(dto.getLinkedinUrl());
        resume.setGithubUrl(dto.getGithubUrl());
        resume.setWebsiteUrl(dto.getWebsiteUrl());
        resume.setHobbies(dto.getHobbies());

        // Convert JSON fields
        try {
            if (dto.getOtherLinks() != null) {
                resume.setOtherLinks(objectMapper.writeValueAsString(dto.getOtherLinks()));
            }
            // Languages is already a String, no conversion needed
            resume.setLanguages(dto.getLanguages());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        // 如果设置为默认，清除其他默认简历
        if (resume.getIsDefault()) {
            resumeRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        resumeRepository.save(existing);
                    });
        }

        resume = resumeRepository.save(resume);
        return convertToDTO(resume);
    }

    // 更新简历
    @Transactional
    public ResumeDTO updateResume(Long id, Long userId, ResumeDTO dto) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        // 验证所有权
        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此简历");
        }

        // 如果修改版本名，检查是否冲突
        if (!resume.getVersionName().equals(dto.getVersionName())) {
            if (resumeRepository.existsByUserIdAndVersionName(userId, dto.getVersionName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "简历版本名已存在");
            }
            resume.setVersionName(dto.getVersionName());
        }

        resume.setAbout(dto.getAbout());
        resume.setCareerObjective(dto.getCareerObjective());
        resume.setEmail(dto.getEmail());
        resume.setPhone(dto.getPhone());
        resume.setAddress(dto.getAddress());
        resume.setLinkedinUrl(dto.getLinkedinUrl());
        resume.setGithubUrl(dto.getGithubUrl());
        resume.setWebsiteUrl(dto.getWebsiteUrl());
        resume.setHobbies(dto.getHobbies());

        // Update JSON fields
        try {
            if (dto.getOtherLinks() != null) {
                resume.setOtherLinks(objectMapper.writeValueAsString(dto.getOtherLinks()));
            }
            // Languages is already a String, no conversion needed
            if (dto.getLanguages() != null) {
                resume.setLanguages(dto.getLanguages());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        // 处理默认简历设置
        if (dto.getIsDefault() != null && dto.getIsDefault() && !resume.getIsDefault()) {
            resumeRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        resumeRepository.save(existing);
                    });
            resume.setIsDefault(true);
        } else if (dto.getIsDefault() != null) {
            resume.setIsDefault(dto.getIsDefault());
        }

        resume = resumeRepository.save(resume);
        return convertToDTO(resume);
    }

    // 删除简历（cascade delete所有关联资源）
    @Transactional
    public void deleteResume(Long id, Long userId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        // 验证所有权
        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此简历");
        }

        resumeRepository.delete(resume);
    }

    // DTO Conversion
    private ResumeDTO convertToDTO(Resume resume) {
        ResumeDTO dto = new ResumeDTO();
        dto.setId(resume.getId());
        dto.setUserId(resume.getUserId());
        dto.setVersionName(resume.getVersionName());
        dto.setIsDefault(resume.getIsDefault());
        dto.setAbout(resume.getAbout());
        dto.setCareerObjective(resume.getCareerObjective());
        dto.setEmail(resume.getEmail());
        dto.setPhone(resume.getPhone());
        dto.setAddress(resume.getAddress());
        dto.setLinkedinUrl(resume.getLinkedinUrl());
        dto.setGithubUrl(resume.getGithubUrl());
        dto.setWebsiteUrl(resume.getWebsiteUrl());
        dto.setHobbies(resume.getHobbies());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setUpdatedAt(resume.getUpdatedAt());

        // Parse JSON fields
        try {
            if (resume.getOtherLinks() != null) {
                dto.setOtherLinks(objectMapper.readValue(resume.getOtherLinks(),
                        new TypeReference<List<ResumeDTO.LinkDTO>>() {}));
            }
            // Languages is kept as String (JSON format)
            dto.setLanguages(resume.getLanguages());
        } catch (Exception e) {
            // If JSON parsing fails, set empty lists
            dto.setOtherLinks(Collections.emptyList());
        }

        return dto;
    }

    private ResumeExperienceDTO convertToExperienceDTO(ResumeExperience exp) {
        ResumeExperienceDTO dto = new ResumeExperienceDTO();
        dto.setId(exp.getId());
        dto.setResumeId(exp.getResumeId());
        dto.setCompanyName(exp.getCompanyName());
        dto.setPosition(exp.getPosition());
        dto.setLocation(exp.getLocation());
        dto.setStartDate(exp.getStartDate());
        dto.setEndDate(exp.getEndDate());
        dto.setIsCurrent(exp.getIsCurrent());
        dto.setResponsibilities(exp.getResponsibilities());
        dto.setAchievements(exp.getAchievements());
        dto.setSortOrder(exp.getSortOrder());
        dto.setCreatedAt(exp.getCreatedAt());
        dto.setUpdatedAt(exp.getUpdatedAt());

        // Parse JSON field and populate project names
        try {
            if (exp.getProjectIds() != null) {
                List<Long> projectIds = objectMapper.readValue(exp.getProjectIds(),
                        new TypeReference<List<Long>>() {});
                dto.setProjectIds(projectIds);

                // 填充项目名称列表
                if (projectIds != null && !projectIds.isEmpty()) {
                    List<String> projectNames = projectExperienceRepository.findAllById(projectIds).stream()
                            .map(ProjectExperience::getProjectName)
                            .collect(Collectors.toList());
                    dto.setProjectNames(projectNames);
                }
            }
        } catch (Exception e) {
            dto.setProjectIds(Collections.emptyList());
            dto.setProjectNames(Collections.emptyList());
        }

        return dto;
    }

    private ResumeSkillDTO convertToSkillDTO(ResumeSkill skill) {
        ResumeSkillDTO dto = new ResumeSkillDTO();
        dto.setId(skill.getId());
        dto.setResumeId(skill.getResumeId());
        dto.setSkillCategory(skill.getSkillCategory());
        dto.setSkillName(skill.getSkillName());
        dto.setProficiency(skill.getProficiency());
        dto.setSortOrder(skill.getSortOrder());
        dto.setCreatedAt(skill.getCreatedAt());
        return dto;
    }

    private ResumeEducationDTO convertToEducationDTO(ResumeEducation edu) {
        ResumeEducationDTO dto = new ResumeEducationDTO();
        dto.setId(edu.getId());
        dto.setResumeId(edu.getResumeId());
        dto.setSchoolName(edu.getSchoolName());
        dto.setDegree(edu.getDegree());
        dto.setMajor(edu.getMajor());
        dto.setStartDate(edu.getStartDate());
        dto.setEndDate(edu.getEndDate());
        dto.setGpa(edu.getGpa());
        dto.setCourses(edu.getCourses());
        dto.setSortOrder(edu.getSortOrder());
        dto.setCreatedAt(edu.getCreatedAt());
        return dto;
    }

    private ResumeCertificationDTO convertToCertificationDTO(ResumeCertification cert) {
        ResumeCertificationDTO dto = new ResumeCertificationDTO();
        dto.setId(cert.getId());
        dto.setResumeId(cert.getResumeId());
        dto.setCertName(cert.getCertName());
        dto.setIssuer(cert.getIssuer());
        dto.setIssueDate(cert.getIssueDate());
        dto.setExpiryDate(cert.getExpiryDate());
        dto.setCertUrl(cert.getCertUrl());
        dto.setSortOrder(cert.getSortOrder());
        dto.setCreatedAt(cert.getCreatedAt());
        return dto;
    }

    // ========== Phase 7 扩展：定制简历功能 ==========

    /**
     * Clone默认简历并关联到职位
     * @param userId 用户ID
     * @param jobApplicationId 职位申请ID
     * @return 克隆后的简历DTO
     */
    @Transactional
    public ResumeDTO cloneDefaultResumeForJob(Long userId, Long jobApplicationId) {
        // 检查是否已存在该职位的定制简历
        Resume existingResume = resumeRepository.findByUserIdAndJobApplicationId(userId, jobApplicationId);
        if (existingResume != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该职位已有定制简历");
        }

        // 获取默认简历
        Resume defaultResume = resumeRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到默认简历"));

        // 获取职位和公司信息用于命名
        JobApplication jobApp = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位申请不存在"));
        Company company = companyRepository.findById(jobApp.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // Clone简历主体
        Resume clonedResume = new Resume();
        clonedResume.setUserId(userId);
        // 使用公司名和职位名生成友好的版本名
        String versionName = String.format("%s - %s", company.getCompanyName(), jobApp.getPositionName());
        clonedResume.setVersionName(versionName);
        clonedResume.setIsDefault(false);
        clonedResume.setJobApplicationId(jobApplicationId);  // 关联到职位
        clonedResume.setAbout(defaultResume.getAbout());
        clonedResume.setCareerObjective(defaultResume.getCareerObjective());
        clonedResume.setEmail(defaultResume.getEmail());
        clonedResume.setPhone(defaultResume.getPhone());
        clonedResume.setAddress(defaultResume.getAddress());
        clonedResume.setLinkedinUrl(defaultResume.getLinkedinUrl());
        clonedResume.setGithubUrl(defaultResume.getGithubUrl());
        clonedResume.setWebsiteUrl(defaultResume.getWebsiteUrl());
        clonedResume.setOtherLinks(defaultResume.getOtherLinks());
        clonedResume.setLanguages(defaultResume.getLanguages());
        clonedResume.setHobbies(defaultResume.getHobbies());

        Resume savedResume = resumeRepository.save(clonedResume);

        // Clone工作经历
        List<ResumeExperience> experiences = resumeExperienceRepository.findByResumeIdOrderBySortOrder(defaultResume.getId());
        for (ResumeExperience exp : experiences) {
            ResumeExperience clonedExp = new ResumeExperience();
            clonedExp.setResumeId(savedResume.getId());
            clonedExp.setCompanyName(exp.getCompanyName());
            clonedExp.setPosition(exp.getPosition());
            clonedExp.setLocation(exp.getLocation());
            clonedExp.setStartDate(exp.getStartDate());
            clonedExp.setEndDate(exp.getEndDate());
            clonedExp.setIsCurrent(exp.getIsCurrent());
            clonedExp.setResponsibilities(exp.getResponsibilities());
            clonedExp.setAchievements(exp.getAchievements());
            clonedExp.setProjectIds(exp.getProjectIds());
            clonedExp.setSortOrder(exp.getSortOrder());
            resumeExperienceRepository.save(clonedExp);
        }

        // Clone技能
        List<ResumeSkill> skills = resumeSkillRepository.findByResumeIdOrderBySortOrder(defaultResume.getId());
        for (ResumeSkill skill : skills) {
            ResumeSkill clonedSkill = new ResumeSkill();
            clonedSkill.setResumeId(savedResume.getId());
            clonedSkill.setSkillCategory(skill.getSkillCategory());
            clonedSkill.setSkillName(skill.getSkillName());
            clonedSkill.setProficiency(skill.getProficiency());
            clonedSkill.setSortOrder(skill.getSortOrder());
            resumeSkillRepository.save(clonedSkill);
        }

        // Clone教育背景
        List<ResumeEducation> education = resumeEducationRepository.findByResumeIdOrderBySortOrder(defaultResume.getId());
        for (ResumeEducation edu : education) {
            ResumeEducation clonedEdu = new ResumeEducation();
            clonedEdu.setResumeId(savedResume.getId());
            clonedEdu.setSchoolName(edu.getSchoolName());
            clonedEdu.setDegree(edu.getDegree());
            clonedEdu.setMajor(edu.getMajor());
            clonedEdu.setStartDate(edu.getStartDate());
            clonedEdu.setEndDate(edu.getEndDate());
            clonedEdu.setGpa(edu.getGpa());
            clonedEdu.setCourses(edu.getCourses());
            clonedEdu.setSortOrder(edu.getSortOrder());
            resumeEducationRepository.save(clonedEdu);
        }

        // Clone证书
        List<ResumeCertification> certifications = resumeCertificationRepository.findByResumeIdOrderBySortOrder(defaultResume.getId());
        for (ResumeCertification cert : certifications) {
            ResumeCertification clonedCert = new ResumeCertification();
            clonedCert.setResumeId(savedResume.getId());
            clonedCert.setCertName(cert.getCertName());
            clonedCert.setIssuer(cert.getIssuer());
            clonedCert.setIssueDate(cert.getIssueDate());
            clonedCert.setExpiryDate(cert.getExpiryDate());
            clonedCert.setCertUrl(cert.getCertUrl());
            clonedCert.setSortOrder(cert.getSortOrder());
            resumeCertificationRepository.save(clonedCert);
        }

        return getResumeById(savedResume.getId(), userId);
    }

    /**
     * 获取职位的定制简历
     * @param userId 用户ID
     * @param jobApplicationId 职位申请ID
     * @return 定制简历DTO，如果不存在返回null
     */
    public ResumeDTO getResumeByJobApplicationId(Long userId, Long jobApplicationId) {
        Resume resume = resumeRepository.findByUserIdAndJobApplicationId(userId, jobApplicationId);
        if (resume == null) {
            return null;
        }
        return getResumeById(resume.getId(), userId);
    }
}
