package com.growing.app.controller;

import com.growing.app.dto.SystemDesignCaseDTO;
import com.growing.app.dto.UserCaseNoteDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.SystemDesignCaseService;
import com.growing.app.service.UserCaseNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 系统设计案例 - 用户端API
 */
@RestController
@RequestMapping("/api/system-design/cases")
public class UserSystemDesignController {

    @Autowired
    private SystemDesignCaseService caseService;

    @Autowired
    private UserCaseNoteService userNoteService;

    @Autowired
    private AuthService authService;

    /**
     * 获取所有官方案例（公开）
     */
    @GetMapping
    public ResponseEntity<List<SystemDesignCaseDTO>> getOfficialCases() {
        List<SystemDesignCaseDTO> cases = caseService.getOfficialCases();
        return ResponseEntity.ok(cases);
    }

    /**
     * 获取案例详情（包含用户答题记录）
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemDesignCaseDTO> getCaseById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        // 未登录用户也可以查看案例，但不加载用户笔记
        if (token == null || token.trim().isEmpty()) {
            SystemDesignCaseDTO caseDTO = caseService.getCaseById(id);
            return ResponseEntity.ok(caseDTO);
        }

        // 已登录用户：加载案例和用户笔记
        Long userId = authService.getUserIdFromToken(token);
        SystemDesignCaseDTO caseDTO = caseService.getCaseByIdForUser(id, userId);
        return ResponseEntity.ok(caseDTO);
    }

    /**
     * 获取用户对某个案例的答题记录
     */
    @GetMapping("/{caseId}/my-note")
    public ResponseEntity<UserCaseNoteDTO> getMyNote(
            @PathVariable Long caseId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        Optional<UserCaseNoteDTO> note = userNoteService.getUserNote(caseId, userId);

        return note.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 保存/更新用户答题记录
     */
    @PostMapping("/{caseId}/my-note")
    public ResponseEntity<UserCaseNoteDTO> saveOrUpdateMyNote(
            @PathVariable Long caseId,
            @RequestBody UserCaseNoteDTO dto,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        UserCaseNoteDTO saved = userNoteService.saveOrUpdateUserNote(caseId, userId, dto);
        return ResponseEntity.ok(saved);
    }

    /**
     * 删除用户答题记录
     */
    @DeleteMapping("/{caseId}/my-note")
    public ResponseEntity<Void> deleteMyNote(
            @PathVariable Long caseId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        userNoteService.deleteUserNote(caseId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取我的学习总结（所有案例的要点汇总）
     */
    @GetMapping("/my-summary")
    public ResponseEntity<List<Map<String, Object>>> getMySummary(
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        List<Map<String, Object>> summary = userNoteService.getUserSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
