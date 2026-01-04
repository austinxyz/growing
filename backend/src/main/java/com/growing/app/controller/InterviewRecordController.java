package com.growing.app.controller;

import com.growing.app.dto.InterviewRecordDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.InterviewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 面试记录Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/interview-records")
public class InterviewRecordController {

    @Autowired
    private InterviewRecordService interviewRecordService;

    @Autowired
    private AuthService authService;

    // 获取求职申请的所有面试记录
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<InterviewRecordDTO>> getRecordsByApplication(
            @PathVariable Long applicationId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(interviewRecordService.getRecordsByApplicationId(applicationId, userId));
    }

    // 获取面试记录详情（包含questions和feedback）
    @GetMapping("/{id}")
    public ResponseEntity<InterviewRecordDTO> getRecordById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(interviewRecordService.getRecordById(id, userId));
    }

    // 创建面试记录
    @PostMapping
    public ResponseEntity<InterviewRecordDTO> createRecord(
            @RequestBody InterviewRecordDTO recordDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        InterviewRecordDTO created = interviewRecordService.createRecord(userId, recordDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新面试记录
    @PutMapping("/{id}")
    public ResponseEntity<InterviewRecordDTO> updateRecord(
            @PathVariable Long id,
            @RequestBody InterviewRecordDTO recordDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(interviewRecordService.updateRecord(id, userId, recordDTO));
    }

    // 删除面试记录（cascade delete questions和feedback）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        interviewRecordService.deleteRecord(id, userId);
        return ResponseEntity.noContent().build();
    }
}
