package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 技能-模版关联实体类
 * Phase 6: 通用技能学习模块
 */
@Entity
@Table(name = "skill_templates")
@Data
@IdClass(SkillTemplate.SkillTemplateId.class)
public class SkillTemplate {

    @Id
    @Column(name = "skill_id")
    private Long skillId;

    @Id
    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 复合主键类
     */
    @Data
    public static class SkillTemplateId implements Serializable {
        private Long skillId;
        private Long templateId;
    }
}
