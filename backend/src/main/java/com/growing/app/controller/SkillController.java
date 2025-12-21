package com.growing.app.controller;

import com.growing.app.dto.LearningResourceDTO;
import com.growing.app.dto.SkillDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.LearningResourceService;
import com.growing.app.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private LearningResourceService learningResourceService;

    @Autowired
    private AuthService authService;

    // 获取所有技能
    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    // 获取技能详情（用户视角）
    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkillById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(skillService.getSkillById(id, userId));
    }

    // 根据职业路径获取技能
    @GetMapping("/career-path/{careerPathId}")
    public ResponseEntity<List<SkillDTO>> getSkillsByCareerPath(@PathVariable Long careerPathId) {
        return ResponseEntity.ok(skillService.getSkillsByCareerPathId(careerPathId));
    }

    // 获取技能下的学习资源（用户视角）
    @GetMapping("/{id}/resources")
    public ResponseEntity<List<LearningResourceDTO>> getSkillResources(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(learningResourceService.getResourcesBySkillIdForUser(id, userId));
    }

    // 用户添加学习资源
    @PostMapping("/{skillId}/resources")
    public ResponseEntity<LearningResourceDTO> addUserResource(
            @PathVariable Long skillId,
            @RequestBody LearningResourceDTO resourceDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        resourceDTO.setSkillId(skillId);
        LearningResourceDTO created = learningResourceService.createUserResource(resourceDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新用户资源
    @PutMapping("/resources/{resourceId}")
    public ResponseEntity<LearningResourceDTO> updateUserResource(
            @PathVariable Long resourceId,
            @RequestBody LearningResourceDTO resourceDTO,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        Long userId = authService.getUserIdByUsername(username);
        boolean isAdmin = authService.isAdminByToken(token);

        LearningResourceDTO updated = learningResourceService.updateResource(resourceId, resourceDTO, userId, isAdmin);
        return ResponseEntity.ok(updated);
    }

    // 删除用户资源
    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<Void> deleteUserResource(
            @PathVariable Long resourceId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        Long userId = authService.getUserIdByUsername(username);
        boolean isAdmin = authService.isAdminByToken(token);

        learningResourceService.deleteResource(resourceId, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // 获取用户创建的所有资源
    @GetMapping("/my-resources")
    public ResponseEntity<List<LearningResourceDTO>> getMyResources(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(learningResourceService.getUserCreatedResources(userId));
    }
}
