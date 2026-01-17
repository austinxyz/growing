package com.growing.app.controller;

import com.growing.app.dto.JobApplicationReferralDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.JobApplicationReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * JobApplicationReferral Controller
 * Phase 7: 求职管理模块 - 职位-人脉关联管理
 */
@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationReferralController {

    @Autowired
    private JobApplicationReferralService jobApplicationReferralService;

    @Autowired
    private AuthService authService;

    /**
     * 获取职位的所有关联人脉
     */
    @GetMapping("/{jobApplicationId}/referrals")
    public ResponseEntity<List<JobApplicationReferralDTO>> getReferralsByJob(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(jobApplicationReferralService.getReferralsByJobApplication(jobApplicationId, userId));
    }

    /**
     * 添加职位-人脉关联
     */
    @PostMapping("/{jobApplicationId}/referrals")
    public ResponseEntity<JobApplicationReferralDTO> addReferralToJob(
            @PathVariable Long jobApplicationId,
            @RequestBody JobApplicationReferralDTO dto,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        JobApplicationReferralDTO created = jobApplicationReferralService.addReferralToJob(jobApplicationId, userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新职位-人脉关联
     */
    @PutMapping("/{jobApplicationId}/referrals/{id}")
    public ResponseEntity<JobApplicationReferralDTO> updateJobReferral(
            @PathVariable Long jobApplicationId,
            @PathVariable Long id,
            @RequestBody JobApplicationReferralDTO dto,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(jobApplicationReferralService.updateJobReferral(id, userId, dto));
    }

    /**
     * 删除职位-人脉关联
     */
    @DeleteMapping("/{jobApplicationId}/referrals/{id}")
    public ResponseEntity<Void> removeReferralFromJob(
            @PathVariable Long jobApplicationId,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        jobApplicationReferralService.removeReferralFromJob(id, userId);
        return ResponseEntity.noContent().build();
    }
}
