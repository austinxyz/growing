package com.growing.app.controller;

import com.growing.app.dto.FocusAreaLearningDTO;
import com.growing.app.dto.LearningContentDTO;
import com.growing.app.dto.UserTemplateNoteDTO;
import com.growing.app.dto.UserLearningContentNoteDTO;
import com.growing.app.dto.UserLearningContentKnowledgePointDTO;
import com.growing.app.model.User;
import com.growing.app.repository.UserRepository;
import com.growing.app.service.LearningContentService;
import com.growing.app.service.UserTemplateNoteService;
import com.growing.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户端学习内容Controller
 *
 * 用户端学习内容API：
 * - 获取Focus Area的完整学习内容（按阶段分组）
 * - 获取单个学习内容详情
 * - 获取算法模版列表（支持搜索）
 * - 获取单个算法模版详情
 */
@RestController
@RequestMapping("/api/learning-contents")
@CrossOrigin(origins = "http://localhost:3000")
public class LearningContentController {

    @Autowired
    private LearningContentService learningContentService;

    @Autowired
    private UserTemplateNoteService userTemplateNoteService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取Focus Area的完整学习内容（按学习阶段分组）
     *
     * GET /api/learning-contents/focus-areas/{focusAreaId}
     *
     * 返回结构:
     * {
     *   "focusAreaId": 1,
     *   "focusAreaName": "二分查找",
     *   "stages": [
     *     {
     *       "stageId": 1,
     *       "stageName": "基础原理",
     *       "stageDescription": "...",
     *       "contents": [...]
     *     }
     *   ]
     * }
     */
    @GetMapping("/focus-areas/{focusAreaId}")
    public ResponseEntity<FocusAreaLearningDTO> getContentsByFocusArea(
            @PathVariable Long focusAreaId,
            @RequestHeader("Authorization") String authHeader) {

        // 验证用户身份
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        FocusAreaLearningDTO learningDTO = learningContentService.getContentsByFocusArea(focusAreaId);
        return ResponseEntity.ok(learningDTO);
    }

    /**
     * 获取单个学习内容详情
     *
     * GET /api/learning-contents/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningContentDTO> getContentById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        // 验证用户身份
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        LearningContentDTO content = learningContentService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    /**
     * 获取算法模版列表（支持搜索）
     *
     * GET /api/learning-contents/algorithm-templates?search=xxx&page=0&size=20
     *
     * 查询参数:
     * - search (可选): 搜索关键词（按标题、描述模糊匹配）
     * - page (可选): 页码，默认0
     * - size (可选): 每页大小，默认20
     */
    @GetMapping("/algorithm-templates")
    public ResponseEntity<Page<LearningContentDTO>> getAlgorithmTemplates(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {

        // 验证用户身份
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        Page<LearningContentDTO> templates = learningContentService.getAlgorithmTemplates(search, page, size);
        return ResponseEntity.ok(templates);
    }

    /**
     * 获取单个算法模版详情
     *
     * GET /api/learning-contents/algorithm-templates/{id}
     */
    @GetMapping("/algorithm-templates/{id}")
    public ResponseEntity<LearningContentDTO> getAlgorithmTemplateById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        // 验证用户身份
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        LearningContentDTO template = learningContentService.getAlgorithmTemplateById(id);
        return ResponseEntity.ok(template);
    }

    /**
     * 获取用户对指定模版的笔记
     *
     * GET /api/learning-contents/algorithm-templates/{templateId}/note
     */
    @GetMapping("/algorithm-templates/{templateId}/note")
    public ResponseEntity<UserTemplateNoteDTO> getTemplateNote(
            @PathVariable Long templateId,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        Optional<UserTemplateNoteDTO> noteOpt = userTemplateNoteService
                .getNoteByTemplateAndUser(templateId, user.getId());

        return noteOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 保存或更新用户模版笔记
     *
     * POST /api/learning-contents/algorithm-templates/{templateId}/note
     * Request Body: { "noteContent": "..." }
     */
    @PostMapping("/algorithm-templates/{templateId}/note")
    public ResponseEntity<UserTemplateNoteDTO> saveOrUpdateTemplateNote(
            @PathVariable Long templateId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        String noteContent = request.get("noteContent");
        if (noteContent == null || noteContent.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "笔记内容不能为空");
        }

        UserTemplateNoteDTO savedNote = userTemplateNoteService
                .saveOrUpdateNote(templateId, user.getId(), noteContent);

        return ResponseEntity.ok(savedNote);
    }

    /**
     * 删除用户模版笔记
     *
     * DELETE /api/learning-contents/algorithm-templates/{templateId}/note
     */
    @DeleteMapping("/algorithm-templates/{templateId}/note")
    public ResponseEntity<Void> deleteTemplateNote(
            @PathVariable Long templateId,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        userTemplateNoteService.deleteNote(templateId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ==================== 学习资料笔记API ====================

    /**
     * 获取用户学习资料笔记
     *
     * GET /api/learning-contents/{contentId}/note
     */
    @GetMapping("/{contentId}/note")
    public ResponseEntity<UserLearningContentNoteDTO> getContentNote(
            @PathVariable Long contentId,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        UserLearningContentNoteDTO note = learningContentService.getNote(contentId, user.getId());
        if (note == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(note);
    }

    /**
     * 保存或更新用户学习资料笔记（UPSERT）
     *
     * POST /api/learning-contents/{contentId}/note
     * Request Body: { "noteContent": "..." }
     */
    @PostMapping("/{contentId}/note")
    public ResponseEntity<UserLearningContentNoteDTO> saveOrUpdateContentNote(
            @PathVariable Long contentId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        String noteContent = request.get("noteContent");
        if (noteContent == null || noteContent.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "笔记内容不能为空");
        }

        UserLearningContentNoteDTO savedNote = learningContentService
                .saveOrUpdateNote(contentId, user.getId(), noteContent);

        return ResponseEntity.ok(savedNote);
    }

    /**
     * 删除用户学习资料笔记
     *
     * DELETE /api/learning-contents/{contentId}/note
     */
    @DeleteMapping("/{contentId}/note")
    public ResponseEntity<Void> deleteContentNote(
            @PathVariable Long contentId,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        learningContentService.deleteNote(contentId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ==================== 知识点API ====================

    /**
     * 获取用户学习资料的所有知识点
     *
     * GET /api/learning-contents/{contentId}/knowledge-points
     */
    @GetMapping("/{contentId}/knowledge-points")
    public ResponseEntity<List<UserLearningContentKnowledgePointDTO>> getKnowledgePoints(
            @PathVariable Long contentId,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        List<UserLearningContentKnowledgePointDTO> points =
                learningContentService.getKnowledgePoints(contentId, user.getId());
        return ResponseEntity.ok(points);
    }

    /**
     * 创建知识点
     *
     * POST /api/learning-contents/{contentId}/knowledge-points
     * Request Body: { "title": "...", "summary": "..." }
     */
    @PostMapping("/{contentId}/knowledge-points")
    public ResponseEntity<UserLearningContentKnowledgePointDTO> createKnowledgePoint(
            @PathVariable Long contentId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        String title = request.get("title");
        String summary = request.get("summary");

        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "知识点标题不能为空");
        }
        if (summary == null || summary.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "知识点总结不能为空");
        }

        UserLearningContentKnowledgePointDTO savedPoint =
                learningContentService.createKnowledgePoint(contentId, user.getId(), title, summary);

        return ResponseEntity.ok(savedPoint);
    }

    /**
     * 更新知识点
     *
     * PUT /api/learning-contents/knowledge-points/{pointId}
     * Request Body: { "title": "...", "summary": "..." }
     */
    @PutMapping("/knowledge-points/{pointId}")
    public ResponseEntity<UserLearningContentKnowledgePointDTO> updateKnowledgePoint(
            @PathVariable Long pointId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        String title = request.get("title");
        String summary = request.get("summary");

        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "知识点标题不能为空");
        }
        if (summary == null || summary.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "知识点总结不能为空");
        }

        UserLearningContentKnowledgePointDTO updatedPoint =
                learningContentService.updateKnowledgePoint(pointId, user.getId(), title, summary);

        return ResponseEntity.ok(updatedPoint);
    }

    /**
     * 删除知识点
     *
     * DELETE /api/learning-contents/knowledge-points/{pointId}
     */
    @DeleteMapping("/knowledge-points/{pointId}")
    public ResponseEntity<Void> deleteKnowledgePoint(
            @PathVariable Long pointId,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        learningContentService.deleteKnowledgePoint(pointId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 重新排序知识点
     *
     * PUT /api/learning-contents/{contentId}/knowledge-points/reorder
     * Request Body: { "pointIds": [3, 1, 2, 5, 4] }
     */
    @PutMapping("/{contentId}/knowledge-points/reorder")
    public ResponseEntity<List<UserLearningContentKnowledgePointDTO>> reorderKnowledgePoints(
            @PathVariable Long contentId,
            @RequestBody Map<String, List<Long>> request,
            @RequestHeader("Authorization") String authHeader) {

        // 获取当前用户
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        List<Long> pointIds = request.get("pointIds");
        if (pointIds == null || pointIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "知识点ID列表不能为空");
        }

        List<UserLearningContentKnowledgePointDTO> reorderedPoints =
                learningContentService.reorderKnowledgePoints(contentId, user.getId(), pointIds);

        return ResponseEntity.ok(reorderedPoints);
    }
}
