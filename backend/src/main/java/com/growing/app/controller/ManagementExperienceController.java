package com.growing.app.controller;

import com.growing.app.dto.ManagementExperienceDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.ManagementExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理经验Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/management-experiences")
public class ManagementExperienceController {

    @Autowired
    private ManagementExperienceService managementExperienceService;

    @Autowired
    private AuthService authService;

    // 获取用户所有管理经验
    @GetMapping
    public ResponseEntity<List<ManagementExperienceDTO>> getAllExperiences(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(managementExperienceService.getAllExperiencesByUserId(userId));
    }

    // 获取管理经验详情
    @GetMapping("/{id}")
    public ResponseEntity<ManagementExperienceDTO> getExperienceById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(managementExperienceService.getExperienceById(id, userId));
    }

    // 创建管理经验
    @PostMapping
    public ResponseEntity<ManagementExperienceDTO> createExperience(
            @RequestBody ManagementExperienceDTO experienceDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ManagementExperienceDTO created = managementExperienceService.createExperience(userId, experienceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新管理经验
    @PutMapping("/{id}")
    public ResponseEntity<ManagementExperienceDTO> updateExperience(
            @PathVariable Long id,
            @RequestBody ManagementExperienceDTO experienceDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(managementExperienceService.updateExperience(id, userId, experienceDTO));
    }

    // 删除管理经验
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        managementExperienceService.deleteExperience(id, userId);
        return ResponseEntity.noContent().build();
    }
}
