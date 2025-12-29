package com.growing.app.controller;

import com.growing.app.dto.CreateQuestionWithDetailsRequest;
import com.growing.app.dto.QuestionDTO;
import com.growing.app.dto.UserQuestionNoteDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.QuestionService;
import com.growing.app.service.UserQuestionNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/questions")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserQuestionNoteService noteService;

    /**
     * 检查管理员权限
     */
    private void requireAdmin(String authHeader) {
        if (!authService.isAdminByToken(authHeader.replace("Bearer ", ""))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
        }
    }

    /**
     * 获取所有试题（支持Focus Area筛选）
     */
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions(
            @RequestParam(required = false) Long focusAreaId,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);

        List<QuestionDTO> questions;
        if (focusAreaId != null) {
            questions = questionService.getAllQuestionsByFocusAreaId(focusAreaId);
        } else {
            // 如果需要获取所有试题，需要实现相应的service方法
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请指定Focus Area ID");
        }

        return ResponseEntity.ok(questions);
    }

    /**
     * 获取试题详情（含编程题详情）
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        QuestionDTO question = questionService.getQuestionWithDetailsForAdmin(id);
        return ResponseEntity.ok(question);
    }

    /**
     * 管理员添加公共试题
     */
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(
            @RequestBody QuestionDTO questionDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        QuestionDTO created = questionService.createQuestion(questionDTO, null, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新任意试题
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionDTO questionDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        QuestionDTO updated = questionService.updateQuestion(id, questionDTO, null, true);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除任意试题
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        questionService.deleteQuestion(id, null, true);
        return ResponseEntity.noContent().build();
    }

    // ============ 编程题详情管理 ============

    /**
     * 创建编程题（含详情）
     * POST /api/admin/questions/with-details
     * Body: { "question": {...}, "programmingDetails": {...} }
     */
    @PostMapping("/with-details")
    public ResponseEntity<QuestionDTO> createQuestionWithDetails(
            @RequestBody CreateQuestionWithDetailsRequest request,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);

        if (request.getQuestion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question字段不能为空");
        }

        QuestionDTO created = questionService.createQuestionWithDetails(
                request.getQuestion(),
                request.getProgrammingDetails(),
                null,
                true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新编程题（含详情）
     * PUT /api/admin/questions/{id}/with-details
     * Body: { "question": {...}, "programmingDetails": {...} }
     */
    @PutMapping("/{id}/with-details")
    public ResponseEntity<QuestionDTO> updateQuestionWithDetails(
            @PathVariable Long id,
            @RequestBody CreateQuestionWithDetailsRequest request,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);

        if (request.getQuestion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question字段不能为空");
        }

        QuestionDTO updated = questionService.updateQuestionWithDetails(
                id,
                request.getQuestion(),
                request.getProgrammingDetails(),
                null,
                true
        );
        return ResponseEntity.ok(updated);
    }

    // ============ AI笔记管理 ============

    /**
     * 为试题添加/更新AI笔记（user_id = -1）
     * POST /api/admin/questions/{id}/ai-note
     * Body: { "noteContent": "...", "coreStrategy": "..." }
     */
    @PostMapping("/{id}/ai-note")
    public ResponseEntity<UserQuestionNoteDTO> saveOrUpdateAINote(
            @PathVariable Long id,
            @RequestBody UserQuestionNoteDTO requestDTO,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);

        // 验证至少有一个字段不为空
        if ((requestDTO.getNoteContent() == null || requestDTO.getNoteContent().trim().isEmpty()) &&
            (requestDTO.getCoreStrategy() == null || requestDTO.getCoreStrategy().trim().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "笔记内容或核心思路至少填写一项");
        }

        // 设置questionId和userId为-1（表示AI笔记）
        requestDTO.setQuestionId(id);
        requestDTO.setUserId(-1L);

        // 使用AI用户ID（-1）保存笔记
        UserQuestionNoteDTO note = noteService.saveOrUpdateNote(id, requestDTO, -1L);

        // 判断是创建还是更新
        HttpStatus status = note.getCreatedAt().equals(note.getUpdatedAt())
            ? HttpStatus.CREATED
            : HttpStatus.OK;

        return ResponseEntity.status(status).body(note);
    }

    /**
     * 获取试题的AI笔记
     * GET /api/admin/questions/{id}/ai-note
     */
    @GetMapping("/{id}/ai-note")
    public ResponseEntity<UserQuestionNoteDTO> getAINote(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);

        try {
            UserQuestionNoteDTO aiNote = noteService.getNoteByQuestionIdAndUserId(id, -1L);
            return ResponseEntity.ok(aiNote);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    /**
     * 删除试题的AI笔记
     * DELETE /api/admin/questions/{id}/ai-note
     */
    @DeleteMapping("/{id}/ai-note")
    public ResponseEntity<Void> deleteAINote(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);

        noteService.deleteNote(id, -1L);
        return ResponseEntity.noContent().build();
    }
}
