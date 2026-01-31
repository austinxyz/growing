package com.growing.app.controller;

import com.growing.app.dto.TemplateQuestionDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.TemplateQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员 - 模板题目关联管理
 */
@RestController
@RequestMapping("/api/admin/template-questions")
public class AdminTemplateQuestionController {

    @Autowired
    private TemplateQuestionService templateQuestionService;

    @Autowired
    private AuthService authService;

    /**
     * 添加模板-题目关联
     */
    @PostMapping
    public ResponseEntity<?> addTemplateQuestion(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Long> request) {

        // 验证管理员权限
        if (!authService.isAdminByToken(token)) {
            return ResponseEntity.status(403).body("需要管理员权限");
        }

        Long userId = authService.getUserIdFromToken(token);
        Long templateId = request.get("templateId");
        Long questionId = request.get("questionId");

        try {
            TemplateQuestionDTO result = templateQuestionService.addTemplateQuestion(
                    templateId, questionId, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 删除模板-题目关联
     */
    @DeleteMapping("/{templateId}/{questionId}")
    public ResponseEntity<?> removeTemplateQuestion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long templateId,
            @PathVariable Long questionId) {

        if (!authService.isAdminByToken(token)) {
            return ResponseEntity.status(403).body("需要管理员权限");
        }

        try {
            templateQuestionService.removeTemplateQuestion(templateId, questionId);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 获取某个模板的所有关联题目（管理端）
     */
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<TemplateQuestionDTO>> getQuestionsByTemplate(
            @RequestHeader("Authorization") String token,
            @PathVariable Long templateId) {

        if (!authService.isAdminByToken(token)) {
            return ResponseEntity.status(403).build();
        }

        List<TemplateQuestionDTO> questions = templateQuestionService.getQuestionsByTemplate(templateId);
        return ResponseEntity.ok(questions);
    }
}
