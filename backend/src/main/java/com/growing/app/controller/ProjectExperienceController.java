package com.growing.app.controller;

import com.growing.app.dto.ProjectExperienceDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.ProjectExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 技术项目经验Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/project-experiences")
public class ProjectExperienceController {

    @Autowired
    private ProjectExperienceService projectExperienceService;

    @Autowired
    private AuthService authService;

    // 获取用户所有技术项目
    @GetMapping
    public ResponseEntity<List<ProjectExperienceDTO>> getAllProjects(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(projectExperienceService.getAllProjectsByUserId(userId));
    }

    // 按项目类型获取
    @GetMapping("/type/{projectType}")
    public ResponseEntity<List<ProjectExperienceDTO>> getProjectsByType(
            @PathVariable String projectType,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(projectExperienceService.getProjectsByType(userId, projectType));
    }

    // 获取项目详情
    @GetMapping("/{id}")
    public ResponseEntity<ProjectExperienceDTO> getProjectById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(projectExperienceService.getProjectById(id, userId));
    }

    // 创建技术项目
    @PostMapping
    public ResponseEntity<ProjectExperienceDTO> createProject(
            @RequestBody ProjectExperienceDTO projectDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ProjectExperienceDTO created = projectExperienceService.createProject(userId, projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新技术项目
    @PutMapping("/{id}")
    public ResponseEntity<ProjectExperienceDTO> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectExperienceDTO projectDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(projectExperienceService.updateProject(id, userId, projectDTO));
    }

    // 删除技术项目
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        projectExperienceService.deleteProject(id, userId);
        return ResponseEntity.noContent().build();
    }
}
