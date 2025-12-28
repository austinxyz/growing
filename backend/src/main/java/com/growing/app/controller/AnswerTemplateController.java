package com.growing.app.controller;

import com.growing.app.dto.AnswerTemplateDTO;
import com.growing.app.service.AnswerTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 答题模版Controller
 * Phase 6: 通用技能学习模块
 * ⚠️ Guardrail #4: 所有 /api/admin/* 需要管理员权限
 */
@RestController
@RequestMapping("/api/admin/answer-templates")
@PreAuthorize("hasRole('ADMIN')")
public class AnswerTemplateController {

    @Autowired
    private AnswerTemplateService templateService;

    /**
     * 获取所有答题模版
     * GET /api/admin/answer-templates
     */
    @GetMapping
    public ResponseEntity<List<AnswerTemplateDTO>> getAllTemplates() {
        List<AnswerTemplateDTO> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    /**
     * 获取单个答题模版
     * GET /api/admin/answer-templates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnswerTemplateDTO> getTemplate(@PathVariable Long id) {
        AnswerTemplateDTO template = templateService.getTemplate(id);
        return ResponseEntity.ok(template);
    }

    /**
     * 创建答题模版
     * POST /api/admin/answer-templates
     */
    @PostMapping
    public ResponseEntity<AnswerTemplateDTO> createTemplate(@RequestBody AnswerTemplateDTO dto) {
        AnswerTemplateDTO created = templateService.createTemplate(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新答题模版
     * PUT /api/admin/answer-templates/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnswerTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @RequestBody AnswerTemplateDTO dto) {
        AnswerTemplateDTO updated = templateService.updateTemplate(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除答题模版
     * DELETE /api/admin/answer-templates/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
