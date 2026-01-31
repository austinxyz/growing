package com.growing.app.controller;

import com.growing.app.dto.TemplateQuestionDTO;
import com.growing.app.service.TemplateQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端 - 模板题目关联查询
 */
@RestController
@RequestMapping("/api/template-questions")
public class TemplateQuestionController {

    @Autowired
    private TemplateQuestionService templateQuestionService;

    /**
     * 获取某个模板的所有关联题目
     */
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<TemplateQuestionDTO>> getQuestionsByTemplate(
            @PathVariable Long templateId) {
        List<TemplateQuestionDTO> questions = templateQuestionService.getQuestionsByTemplate(templateId);
        return ResponseEntity.ok(questions);
    }

    /**
     * 获取某道题关联的所有模板
     */
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<TemplateQuestionDTO>> getTemplatesByQuestion(
            @PathVariable Long questionId) {
        List<TemplateQuestionDTO> templates = templateQuestionService.getTemplatesByQuestion(questionId);
        return ResponseEntity.ok(templates);
    }
}
