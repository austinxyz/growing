package com.growing.app.controller;

import com.growing.app.dto.LearningContentDTO;
import com.growing.app.dto.LearningStageDTO;
import com.growing.app.model.LearningContent;
import com.growing.app.service.AuthService;
import com.growing.app.service.LearningContentService;
import com.growing.app.service.LearningStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminLearningContentController {

    @Autowired
    private LearningContentService learningContentService;

    @Autowired
    private LearningStageService learningStageService;

    @Autowired
    private AuthService authService;

    // 管理员权限检查
    private void requireAdmin(String authHeader) {
        if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
        }
    }

    private Long getUserIdFromToken(String authHeader) {
        return authService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
    }

    // ============ 学习阶段管理 ============

    /**
     * 获取Skill的所有学习阶段
     * GET /api/admin/learning-stages?skillId=X
     */
    @GetMapping("/learning-stages")
    public ResponseEntity<List<LearningStageDTO>> getStagesBySkillId(
            @RequestParam Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(learningStageService.getStagesBySkillId(skillId));
    }

    /**
     * 获取单个学习阶段详情
     * GET /api/admin/learning-stages/{id}
     */
    @GetMapping("/learning-stages/{id}")
    public ResponseEntity<LearningStageDTO> getStageById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(learningStageService.getStageById(id));
    }

    /**
     * 创建学习阶段
     * POST /api/admin/learning-stages
     */
    @PostMapping("/learning-stages")
    public ResponseEntity<LearningStageDTO> createStage(
            @RequestBody LearningStageDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        LearningStageDTO created = learningStageService.createStage(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新学习阶段
     * PUT /api/admin/learning-stages/{id}
     */
    @PutMapping("/learning-stages/{id}")
    public ResponseEntity<LearningStageDTO> updateStage(
            @PathVariable Long id,
            @RequestBody LearningStageDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(learningStageService.updateStage(id, dto));
    }

    /**
     * 删除学习阶段
     * DELETE /api/admin/learning-stages/{id}
     */
    @DeleteMapping("/learning-stages/{id}")
    public ResponseEntity<Void> deleteStage(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        learningStageService.deleteStage(id);
        return ResponseEntity.noContent().build();
    }

    // ============ 学习内容管理 ============

    /**
     * 分页查询学习内容（支持多条件过滤）
     * GET /api/admin/learning-contents?focusAreaId=X&stageId=Y&contentType=article&page=0&size=20
     */
    @GetMapping("/learning-contents")
    public ResponseEntity<Page<LearningContentDTO>> getContents(
            @RequestParam(required = false) Long focusAreaId,
            @RequestParam(required = false) Long stageId,
            @RequestParam(required = false) LearningContent.ContentType contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        Page<LearningContentDTO> contents = learningContentService.getContentsForAdmin(
                focusAreaId, stageId, contentType, page, size);
        return ResponseEntity.ok(contents);
    }

    /**
     * 获取单个学习内容详情
     * GET /api/admin/learning-contents/{id}
     */
    @GetMapping("/learning-contents/{id}")
    public ResponseEntity<LearningContentDTO> getContentById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(learningContentService.getContentById(id));
    }

    /**
     * 创建学习内容
     * POST /api/admin/learning-contents
     */
    @PostMapping("/learning-contents")
    public ResponseEntity<LearningContentDTO> createContent(
            @RequestBody LearningContentDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        Long adminUserId = getUserIdFromToken(authHeader);
        LearningContentDTO created = learningContentService.createContent(dto, adminUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新学习内容
     * PUT /api/admin/learning-contents/{id}
     */
    @PutMapping("/learning-contents/{id}")
    public ResponseEntity<LearningContentDTO> updateContent(
            @PathVariable Long id,
            @RequestBody LearningContentDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        return ResponseEntity.ok(learningContentService.updateContent(id, dto));
    }

    /**
     * 删除学习内容
     * DELETE /api/admin/learning-contents/{id}
     */
    @DeleteMapping("/learning-contents/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        learningContentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量调整排序
     * PUT /api/admin/learning-contents/reorder
     * Body: { "contentIds": [3, 1, 2, 5, 4] }
     */
    @PutMapping("/learning-contents/reorder")
    public ResponseEntity<Void> reorderContents(
            @RequestBody Map<String, List<Long>> request,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        List<Long> contentIds = request.get("contentIds");
        if (contentIds == null || contentIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "contentIds不能为空");
        }

        learningContentService.reorderContents(contentIds);
        return ResponseEntity.ok().build();
    }
}
