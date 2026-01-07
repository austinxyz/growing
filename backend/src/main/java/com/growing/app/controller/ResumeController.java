package com.growing.app.controller;

import com.growing.app.dto.ResumeDTO;
import com.growing.app.dto.ResumeSkillDTO;
import com.growing.app.dto.ResumeExperienceDTO;
import com.growing.app.dto.ResumeEducationDTO;
import com.growing.app.dto.ResumeCertificationDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.ResumeService;
import com.growing.app.service.ResumeSkillService;
import com.growing.app.service.ResumeExperienceService;
import com.growing.app.service.ResumeEducationService;
import com.growing.app.service.ResumeCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简历Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeSkillService resumeSkillService;

    @Autowired
    private ResumeExperienceService resumeExperienceService;

    @Autowired
    private ResumeEducationService resumeEducationService;

    @Autowired
    private ResumeCertificationService resumeCertificationService;

    @Autowired
    private AuthService authService;

    // 获取用户所有简历版本
    @GetMapping
    public ResponseEntity<List<ResumeDTO>> getAllResumes(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeService.getAllResumesByUserId(userId));
    }

    // 获取用户默认简历
    @GetMapping("/default")
    public ResponseEntity<ResumeDTO> getDefaultResume(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeService.getDefaultResumeByUserId(userId));
    }

    // 获取简历详情（包含所有nested resources）
    @GetMapping("/{id}")
    public ResponseEntity<ResumeDTO> getResumeById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeService.getResumeById(id, userId));
    }

    // 创建新简历版本
    @PostMapping
    public ResponseEntity<ResumeDTO> createResume(
            @RequestBody ResumeDTO resumeDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ResumeDTO created = resumeService.createResume(userId, resumeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新简历
    @PutMapping("/{id}")
    public ResponseEntity<ResumeDTO> updateResume(
            @PathVariable Long id,
            @RequestBody ResumeDTO resumeDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeService.updateResume(id, userId, resumeDTO));
    }

    // 删除简历（cascade delete所有关联资源）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        resumeService.deleteResume(id, userId);
        return ResponseEntity.noContent().build();
    }

    // 复制简历版本
    @PostMapping("/{id}/copy")
    public ResponseEntity<ResumeDTO> copyResume(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        // TODO: Implement copy logic in service
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // 设置为默认简历
    @PutMapping("/{id}/set-default")
    public ResponseEntity<Void> setDefaultResume(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        // TODO: Implement set default logic in service
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // ==================== 子资源: 技能 ====================

    // 获取简历的所有技能
    @GetMapping("/{resumeId}/skills")
    public ResponseEntity<List<ResumeSkillDTO>> getSkills(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeSkillService.getSkillsByResumeId(resumeId, userId));
    }

    // 添加技能
    @PostMapping("/{resumeId}/skills")
    public ResponseEntity<ResumeSkillDTO> createSkill(
            @PathVariable Long resumeId,
            @RequestBody ResumeSkillDTO skillDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        skillDTO.setResumeId(resumeId);
        ResumeSkillDTO created = resumeSkillService.createSkill(userId, skillDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新技能
    @PutMapping("/{resumeId}/skills/{skillId}")
    public ResponseEntity<ResumeSkillDTO> updateSkill(
            @PathVariable Long resumeId,
            @PathVariable Long skillId,
            @RequestBody ResumeSkillDTO skillDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeSkillService.updateSkill(skillId, userId, skillDTO));
    }

    // 删除技能
    @DeleteMapping("/{resumeId}/skills/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long resumeId,
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        resumeSkillService.deleteSkill(skillId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 子资源: 工作经历 ====================

    // 获取简历的所有工作经历
    @GetMapping("/{resumeId}/experiences")
    public ResponseEntity<List<ResumeExperienceDTO>> getExperiences(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeExperienceService.getExperiencesByResumeId(resumeId, userId));
    }

    // 添加工作经历
    @PostMapping("/{resumeId}/experiences")
    public ResponseEntity<ResumeExperienceDTO> createExperience(
            @PathVariable Long resumeId,
            @RequestBody ResumeExperienceDTO experienceDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        experienceDTO.setResumeId(resumeId);
        ResumeExperienceDTO created = resumeExperienceService.createExperience(userId, experienceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新工作经历
    @PutMapping("/{resumeId}/experiences/{experienceId}")
    public ResponseEntity<ResumeExperienceDTO> updateExperience(
            @PathVariable Long resumeId,
            @PathVariable Long experienceId,
            @RequestBody ResumeExperienceDTO experienceDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeExperienceService.updateExperience(experienceId, userId, experienceDTO));
    }

    // 删除工作经历
    @DeleteMapping("/{resumeId}/experiences/{experienceId}")
    public ResponseEntity<Void> deleteExperience(
            @PathVariable Long resumeId,
            @PathVariable Long experienceId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        resumeExperienceService.deleteExperience(experienceId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 子资源: 教育背景 ====================

    // 获取简历的所有教育背景
    @GetMapping("/{resumeId}/education")
    public ResponseEntity<List<ResumeEducationDTO>> getEducation(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeEducationService.getEducationByResumeId(resumeId, userId));
    }

    // 添加教育背景
    @PostMapping("/{resumeId}/education")
    public ResponseEntity<ResumeEducationDTO> createEducation(
            @PathVariable Long resumeId,
            @RequestBody ResumeEducationDTO educationDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        educationDTO.setResumeId(resumeId);
        ResumeEducationDTO created = resumeEducationService.createEducation(userId, educationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新教育背景
    @PutMapping("/{resumeId}/education/{educationId}")
    public ResponseEntity<ResumeEducationDTO> updateEducation(
            @PathVariable Long resumeId,
            @PathVariable Long educationId,
            @RequestBody ResumeEducationDTO educationDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeEducationService.updateEducation(educationId, userId, educationDTO));
    }

    // 删除教育背景
    @DeleteMapping("/{resumeId}/education/{educationId}")
    public ResponseEntity<Void> deleteEducation(
            @PathVariable Long resumeId,
            @PathVariable Long educationId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        resumeEducationService.deleteEducation(educationId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 子资源: 证书与培训 ====================

    // 获取简历的所有证书
    @GetMapping("/{resumeId}/certifications")
    public ResponseEntity<List<ResumeCertificationDTO>> getCertifications(
            @PathVariable Long resumeId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeCertificationService.getCertificationsByResumeId(resumeId, userId));
    }

    // ========== Phase 7 扩展：定制简历功能 ==========

    /**
     * Clone默认简历并关联到职位（创建定制简历）
     * @param jobApplicationId 职位申请ID
     * @return 克隆后的简历
     */
    @PostMapping("/clone-for-job/{jobApplicationId}")
    public ResponseEntity<ResumeDTO> cloneResumeForJob(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ResumeDTO clonedResume = resumeService.cloneDefaultResumeForJob(userId, jobApplicationId);
        return ResponseEntity.ok(clonedResume);
    }

    /**
     * 获取职位的定制简历
     * @param jobApplicationId 职位申请ID
     * @return 定制简历，如果不存在返回null
     */
    @GetMapping("/by-job/{jobApplicationId}")
    public ResponseEntity<ResumeDTO> getResumeByJob(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ResumeDTO resume = resumeService.getResumeByJobApplicationId(userId, jobApplicationId);
        if (resume == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resume);
    }

    // 添加证书
    @PostMapping("/{resumeId}/certifications")
    public ResponseEntity<ResumeCertificationDTO> createCertification(
            @PathVariable Long resumeId,
            @RequestBody ResumeCertificationDTO certificationDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        certificationDTO.setResumeId(resumeId);
        ResumeCertificationDTO created = resumeCertificationService.createCertification(userId, certificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新证书
    @PutMapping("/{resumeId}/certifications/{certificationId}")
    public ResponseEntity<ResumeCertificationDTO> updateCertification(
            @PathVariable Long resumeId,
            @PathVariable Long certificationId,
            @RequestBody ResumeCertificationDTO certificationDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(resumeCertificationService.updateCertification(certificationId, userId, certificationDTO));
    }

    // 删除证书
    @DeleteMapping("/{resumeId}/certifications/{certificationId}")
    public ResponseEntity<Void> deleteCertification(
            @PathVariable Long resumeId,
            @PathVariable Long certificationId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        resumeCertificationService.deleteCertification(certificationId, userId);
        return ResponseEntity.noContent().build();
    }
}
