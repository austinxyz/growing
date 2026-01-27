package com.growing.app.controller;

import com.growing.app.dto.JobSearchStatsDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.JobSearchStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 求职管理统计控制器
 * Phase 7 - Dashboard statistics API
 */
@RestController
@RequestMapping("/api/job-search/stats")
@RequiredArgsConstructor
public class JobSearchStatsController {

    private final JobSearchStatsService statsService;
    private final AuthService authService;

    /**
     * 获取当前用户的求职管理统计数据
     */
    @GetMapping
    public ResponseEntity<JobSearchStatsDTO> getUserStats(
            @RequestHeader("Authorization") String authHeader) {
        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);
        JobSearchStatsDTO stats = statsService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
}
