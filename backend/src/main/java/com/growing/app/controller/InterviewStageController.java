package com.growing.app.controller;

import com.growing.app.dto.InterviewStageDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.InterviewStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 面试阶段Controller
 * Phase 7: 求职管理模块 - Enhancements
 */
@RestController
public class InterviewStageController {

    @Autowired
    private InterviewStageService interviewStageService;

    @Autowired
    private AuthService authService;

    /**
     * 获取职位的所有面试阶段（旧路径，保留兼容性）
     */
    @GetMapping("/api/job-applications/{jobId}/stages")
    public ResponseEntity<List<InterviewStageDTO>> getStagesByJob(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(interviewStageService.getStagesByApplicationId(jobId, userId));
    }

    /**
     * 获取Job Application的所有面试阶段（新路径）
     */
    @GetMapping("/api/interview-stages/job-application/{jobApplicationId}")
    public ResponseEntity<List<InterviewStageDTO>> getByJobApplication(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(interviewStageService.getStagesByApplicationId(jobApplicationId, userId));
    }

    /**
     * 创建面试阶段（旧路径，保留兼容性）
     */
    @PostMapping("/api/job-applications/{jobId}/stages")
    public ResponseEntity<InterviewStageDTO> createStage(
            @PathVariable Long jobId,
            @RequestBody InterviewStageDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        // Set jobApplicationId from path variable
        dto.setJobApplicationId(jobId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewStageService.createStage(userId, dto));
    }

    /**
     * 创建面试阶段（新路径）
     */
    @PostMapping("/api/interview-stages")
    public ResponseEntity<InterviewStageDTO> createStageNew(
            @RequestBody InterviewStageDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewStageService.createStage(userId, dto));
    }

    /**
     * 更新面试阶段
     */
    @PutMapping("/api/interview-stages/{stageId}")
    public ResponseEntity<InterviewStageDTO> updateStage(
            @PathVariable Long stageId,
            @RequestBody InterviewStageDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(interviewStageService.updateStage(stageId, userId, dto));
    }

    /**
     * 删除面试阶段
     */
    @DeleteMapping("/api/interview-stages/{stageId}")
    public ResponseEntity<Void> deleteStage(
            @PathVariable Long stageId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        interviewStageService.deleteStage(stageId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 生成面试准备AI Prompt (单个阶段)
     * 用于前端调用Claude Code生成准备建议
     */
    @GetMapping("/api/interview-stages/{stageId}/ai-prompt")
    public ResponseEntity<String> generateAIPrompt(
            @PathVariable Long stageId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        String prompt = interviewStageService.generatePreparationPrompt(stageId, userId);
        return ResponseEntity.ok(prompt);
    }

    /**
     * 生成整个职位所有阶段的面试准备AI Prompt
     * 包含JD + 所有面试阶段信息
     */
    @GetMapping("/api/job-applications/{jobApplicationId}/interview-prep-prompt")
    public ResponseEntity<String> generateJobInterviewPrepPrompt(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        String prompt = interviewStageService.generateJobPreparationPrompt(jobApplicationId, userId);
        return ResponseEntity.ok(prompt);
    }
}
