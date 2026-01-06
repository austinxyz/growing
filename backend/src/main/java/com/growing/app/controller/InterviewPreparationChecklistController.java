package com.growing.app.controller;

import com.growing.app.dto.InterviewPreparationChecklistDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.InterviewPreparationChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 面试准备清单Controller
 * Phase 7: 求职管理模块 - Enhancements
 */
@RestController
@RequestMapping("/api/interview-preparation-checklist")
public class InterviewPreparationChecklistController {

    @Autowired
    private InterviewPreparationChecklistService checklistService;

    @Autowired
    private AuthService authService;

    /**
     * 获取面试阶段的所有准备清单项
     */
    @GetMapping("/stage/{stageId}")
    public ResponseEntity<List<InterviewPreparationChecklistDTO>> getChecklistByStageId(
            @PathVariable Long stageId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(checklistService.getChecklistByStageId(stageId, userId));
    }

    /**
     * 获取面试阶段的重点准备清单项（用于打印）
     */
    @GetMapping("/stage/{stageId}/priority")
    public ResponseEntity<List<InterviewPreparationChecklistDTO>> getPriorityChecklistByStageId(
            @PathVariable Long stageId,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(checklistService.getPriorityChecklistByStageId(stageId, userId));
    }

    /**
     * 创建准备清单项
     */
    @PostMapping
    public ResponseEntity<InterviewPreparationChecklistDTO> createChecklist(
            @RequestBody InterviewPreparationChecklistDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(checklistService.createChecklist(userId, dto));
    }

    /**
     * 批量创建准备清单项
     */
    @PostMapping("/batch")
    public ResponseEntity<List<InterviewPreparationChecklistDTO>> batchCreateChecklist(
            @RequestBody List<InterviewPreparationChecklistDTO> dtos,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(checklistService.batchCreateChecklist(userId, dtos));
    }

    /**
     * 更新准备清单项
     */
    @PutMapping("/{id}")
    public ResponseEntity<InterviewPreparationChecklistDTO> updateChecklist(
            @PathVariable Long id,
            @RequestBody InterviewPreparationChecklistDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        return ResponseEntity.ok(checklistService.updateChecklist(id, userId, dto));
    }

    /**
     * 删除准备清单项
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token);
        checklistService.deleteChecklist(id, userId);
        return ResponseEntity.ok().build();
    }
}
