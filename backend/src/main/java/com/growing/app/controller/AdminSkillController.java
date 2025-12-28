package com.growing.app.controller;

import com.growing.app.dto.FocusAreaDTO;
import com.growing.app.dto.FocusAreaWithCategoryDTO;
import com.growing.app.dto.LearningResourceDTO;
import com.growing.app.dto.SkillDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.FocusAreaService;
import com.growing.app.service.LearningResourceService;
import com.growing.app.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/skills")
public class AdminSkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private FocusAreaService focusAreaService;

    @Autowired
    private LearningResourceService learningResourceService;

    @Autowired
    private AuthService authService;

    // 管理员权限检查
    private void requireAdmin(String authHeader) {
        if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
        }
    }

    // ============ 技能管理 ============

    // 获取技能详情（管理员视角 - 无过滤）
    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkillById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(skillService.getSkillByIdForAdmin(id));
    }

    // 创建技能
    @PostMapping
    public ResponseEntity<SkillDTO> createSkill(
            @RequestBody SkillDTO skillDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        SkillDTO created = skillService.createSkill(skillDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新技能
    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkill(
            @PathVariable Long id,
            @RequestBody SkillDTO skillDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(skillService.updateSkill(id, skillDTO));
    }

    // 删除技能
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // 关联技能到职业路径
    @PostMapping("/career-path/{careerPathId}/skill/{skillId}")
    public ResponseEntity<Void> associateSkillToCareerPath(
            @PathVariable Long careerPathId,
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        skillService.associateSkillToCareerPath(careerPathId, skillId);
        return ResponseEntity.ok().build();
    }

    // 取消关联
    @DeleteMapping("/career-path/{careerPathId}/skill/{skillId}")
    public ResponseEntity<Void> dissociateSkillFromCareerPath(
            @PathVariable Long careerPathId,
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        skillService.dissociateSkillFromCareerPath(careerPathId, skillId);
        return ResponseEntity.noContent().build();
    }

    // ============ Focus Area 管理 ============

    // 获取技能的Focus Areas
    @GetMapping("/{skillId}/focus-areas")
    public ResponseEntity<List<FocusAreaDTO>> getFocusAreas(
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(focusAreaService.getFocusAreasBySkillId(skillId));
    }

    // 创建Focus Area
    @PostMapping("/{skillId}/focus-areas")
    public ResponseEntity<FocusAreaDTO> createFocusArea(
            @PathVariable Long skillId,
            @RequestBody FocusAreaDTO focusAreaDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        focusAreaDTO.setSkillId(skillId);
        FocusAreaDTO created = focusAreaService.createFocusArea(focusAreaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新Focus Area
    @PutMapping("/focus-areas/{id}")
    public ResponseEntity<FocusAreaDTO> updateFocusArea(
            @PathVariable Long id,
            @RequestBody FocusAreaDTO focusAreaDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(focusAreaService.updateFocusArea(id, focusAreaDTO));
    }

    // 删除Focus Area
    @DeleteMapping("/focus-areas/{id}")
    public ResponseEntity<Void> deleteFocusArea(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        focusAreaService.deleteFocusArea(id);
        return ResponseEntity.noContent().build();
    }

    // 获取技能下未分类的Focus Areas
    @GetMapping("/{skillId}/focus-areas/uncategorized")
    public ResponseEntity<List<FocusAreaWithCategoryDTO>> getUncategorizedFocusAreas(
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(focusAreaService.getUncategorizedFocusAreasBySkillId(skillId));
    }

    // ============ 学习资源管理 ============

    // 获取技能的所有学习资源（管理员视角 - 无过滤）
    @GetMapping("/{skillId}/resources")
    public ResponseEntity<List<LearningResourceDTO>> getResources(
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(learningResourceService.getResourcesBySkillIdForAdmin(skillId));
    }

    // 创建官方资源
    @PostMapping("/{skillId}/resources")
    public ResponseEntity<LearningResourceDTO> createOfficialResource(
            @PathVariable Long skillId,
            @RequestBody LearningResourceDTO resourceDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        resourceDTO.setSkillId(skillId);
        LearningResourceDTO created = learningResourceService.createOfficialResource(resourceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新资源
    @PutMapping("/resources/{id}")
    public ResponseEntity<LearningResourceDTO> updateResource(
            @PathVariable Long id,
            @RequestBody LearningResourceDTO resourceDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        LearningResourceDTO updated = learningResourceService.updateResource(id, resourceDTO, userId, true);
        return ResponseEntity.ok(updated);
    }

    // 删除资源
    @DeleteMapping("/resources/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        learningResourceService.deleteResource(id, userId, true);
        return ResponseEntity.noContent().build();
    }
}
