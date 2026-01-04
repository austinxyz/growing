package com.growing.app.controller;

import com.growing.app.dto.RecruiterDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Recruiter Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @Autowired
    private AuthService authService;

    // 获取用户所有Recruiter
    @GetMapping
    public ResponseEntity<List<RecruiterDTO>> getAllRecruiters(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(recruiterService.getAllRecruitersByUserId(userId));
    }

    // 按公司获取
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<RecruiterDTO>> getRecruitersByCompany(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(recruiterService.getRecruitersByCompany(userId, companyId));
    }

    // 获取Recruiter详情
    @GetMapping("/{id}")
    public ResponseEntity<RecruiterDTO> getRecruiterById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(recruiterService.getRecruiterById(id, userId));
    }

    // 创建Recruiter
    @PostMapping
    public ResponseEntity<RecruiterDTO> createRecruiter(
            @RequestBody RecruiterDTO recruiterDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        RecruiterDTO created = recruiterService.createRecruiter(userId, recruiterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新Recruiter
    @PutMapping("/{id}")
    public ResponseEntity<RecruiterDTO> updateRecruiter(
            @PathVariable Long id,
            @RequestBody RecruiterDTO recruiterDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(recruiterService.updateRecruiter(id, userId, recruiterDTO));
    }

    // 删除Recruiter
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruiter(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        recruiterService.deleteRecruiter(id, userId);
        return ResponseEntity.noContent().build();
    }
}
