package com.growing.app.controller;

import com.growing.app.dto.ResumeAnalysisDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.ResumeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 简历分析Controller
 * Phase 7: Job Search Module - Resume Analysis
 */
@RestController
@RequestMapping("/api/resume-analysis")
public class ResumeAnalysisController {

    @Autowired
    private ResumeAnalysisService resumeAnalysisService;

    @Autowired
    private AuthService authService;

    /**
     * 分析简历与职位的匹配度 (通过jobApplicationId - 已申请的职位)
     */
    @GetMapping("/job-application/{jobApplicationId}")
    public ResponseEntity<ResumeAnalysisDTO> analyzeResumeByApplication(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ResumeAnalysisDTO analysis = resumeAnalysisService.analyzeResumeByApplication(jobApplicationId, userId);
        return ResponseEntity.ok(analysis);
    }

    /**
     * 分析简历与职位的匹配度 (通过jobId - 未申请的职位，用于决定是否申请)
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ResumeAnalysisDTO> analyzeResumeByJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ResumeAnalysisDTO analysis = resumeAnalysisService.analyzeResumeByJob(jobId, userId);
        return ResponseEntity.ok(analysis);
    }
}
