package com.growing.app.controller;

import com.growing.app.dto.FocusAreaLearningDTO;
import com.growing.app.dto.LearningContentDTO;
import com.growing.app.dto.UserTemplateNoteDTO;
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
}
