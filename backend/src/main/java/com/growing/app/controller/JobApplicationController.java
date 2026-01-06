package com.growing.app.controller;

import com.growing.app.dto.JobApplicationDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职位申请Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private AuthService authService;

    // 获取用户所有求职申请
    @GetMapping
    public ResponseEntity<List<JobApplicationDTO>> getAllApplications(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(jobApplicationService.getAllApplicationsByUserId(userId));
    }

    // 按状态获取
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplicationDTO>> getApplicationsByStatus(
            @PathVariable String status,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(jobApplicationService.getApplicationsByStatus(userId, status));
    }

    // 按公司获取
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobApplicationDTO>> getJobsByCompany(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(jobApplicationService.getJobsByCompany(companyId, userId));
    }

    // 获取申请详情（包含所有nested resources）
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDTO> getApplicationById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(jobApplicationService.getApplicationById(id, userId));
    }

    // 创建求职申请
    @PostMapping
    public ResponseEntity<JobApplicationDTO> createApplication(
            @RequestBody JobApplicationDTO applicationDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        JobApplicationDTO created = jobApplicationService.createApplication(userId, applicationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新求职申请
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDTO> updateApplication(
            @PathVariable Long id,
            @RequestBody JobApplicationDTO applicationDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(jobApplicationService.updateApplication(id, userId, applicationDTO));
    }

    // 删除求职申请（cascade delete所有关联资源）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        jobApplicationService.deleteApplication(id, userId);
        return ResponseEntity.noContent().build();
    }
}
