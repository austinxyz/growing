package com.growing.app.controller;

import com.growing.app.dto.QuestionDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.QuestionService;
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
     * 获取试题详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        requireAdmin(authHeader);
        QuestionDTO question = questionService.getQuestionById(id);
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
}
