package com.growing.app.controller;

import com.growing.app.dto.AnswerTemplateDTO;
import com.growing.app.dto.SkillTemplateDTO;
import com.growing.app.service.AnswerTemplateService;
import com.growing.app.service.SkillTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 技能-模版关联Controller
 * Phase 6: 通用技能学习模块
 * ⚠️ Guardrail #4: 所有 /api/admin/* 需要管理员权限
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SkillTemplateController {

    @Autowired
    private SkillTemplateService skillTemplateService;

    @Autowired
    private AnswerTemplateService answerTemplateService;

    /**
     * 公开API: 获取技能的所有关联模版（供用户答题使用）
     * GET /api/skills/{skillId}/templates
     */
    @GetMapping("/skills/{skillId}/templates")
    public ResponseEntity<List<SkillTemplateDTO>> getSkillTemplatesPublic(@PathVariable Long skillId) {
        List<SkillTemplateDTO> templates = skillTemplateService.getSkillTemplates(skillId);
        return ResponseEntity.ok(templates);
    }

    /**
     * 公开API: 获取技能的默认模版（供用户答题使用）
     * GET /api/skills/{skillId}/templates/default
     * 返回完整的AnswerTemplateDTO（包含templateFields）
     */
    @GetMapping("/skills/{skillId}/templates/default")
    public ResponseEntity<AnswerTemplateDTO> getDefaultTemplatePublic(@PathVariable Long skillId) {
        SkillTemplateDTO skillTemplate = skillTemplateService.getDefaultTemplate(skillId);
        if (skillTemplate == null || skillTemplate.getTemplateId() == null) {
            return ResponseEntity.noContent().build();
        }

        // 获取完整的AnswerTemplateDTO
        try {
            AnswerTemplateDTO template = answerTemplateService.getTemplate(skillTemplate.getTemplateId());
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * 获取技能的所有关联模版
     * GET /api/admin/skill-templates?skillId=X
     */
    @GetMapping("/admin/skill-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SkillTemplateDTO>> getSkillTemplates(@RequestParam Long skillId) {
        List<SkillTemplateDTO> templates = skillTemplateService.getSkillTemplates(skillId);
        return ResponseEntity.ok(templates);
    }

    /**
     * 获取技能的默认模版
     * GET /api/admin/skill-templates/default?skillId=X
     */
    @GetMapping("/admin/skill-templates/default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillTemplateDTO> getDefaultTemplate(@RequestParam Long skillId) {
        SkillTemplateDTO template = skillTemplateService.getDefaultTemplate(skillId);
        if (template == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(template);
    }

    /**
     * 关联技能与模版
     * POST /api/admin/skill-templates
     * Body: { "skillId": 1, "templateId": 2 }
     */
    @PostMapping("/admin/skill-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillTemplateDTO> associateTemplate(@RequestBody AssociateRequest request) {
        SkillTemplateDTO created = skillTemplateService.associateTemplate(
                request.getSkillId(),
                request.getTemplateId()
        );
        return ResponseEntity.ok(created);
    }

    /**
     * 设置默认模版
     * PUT /api/admin/skill-templates/default
     * Body: { "skillId": 1, "templateId": 2 }
     */
    @PutMapping("/admin/skill-templates/default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillTemplateDTO> setDefaultTemplate(@RequestBody AssociateRequest request) {
        SkillTemplateDTO updated = skillTemplateService.setDefaultTemplate(
                request.getSkillId(),
                request.getTemplateId()
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * 取消关联
     * DELETE /api/admin/skill-templates?skillId=X&templateId=Y
     */
    @DeleteMapping("/admin/skill-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disassociateTemplate(
            @RequestParam Long skillId,
            @RequestParam Long templateId) {
        skillTemplateService.disassociateTemplate(skillId, templateId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取模版关联的所有技能（反向查询）
     * GET /api/admin/skill-templates/by-template?templateId=X
     * 返回: [{ skillId, skillName, isDefault }, ...]
     */
    @GetMapping("/admin/skill-templates/by-template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SkillTemplateDTO>> getSkillsByTemplate(@RequestParam Long templateId) {
        List<SkillTemplateDTO> skills = skillTemplateService.getSkillsByTemplate(templateId);
        return ResponseEntity.ok(skills);
    }

    /**
     * 请求体类（用于关联操作）
     */
    public static class AssociateRequest {
        private Long skillId;
        private Long templateId;

        public Long getSkillId() {
            return skillId;
        }

        public void setSkillId(Long skillId) {
            this.skillId = skillId;
        }

        public Long getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Long templateId) {
            this.templateId = templateId;
        }
    }
}
