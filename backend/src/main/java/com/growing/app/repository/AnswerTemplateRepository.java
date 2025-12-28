package com.growing.app.repository;

import com.growing.app.entity.AnswerTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 答题模版Repository
 * Phase 6: 通用技能学习模块
 */
@Repository
public interface AnswerTemplateRepository extends JpaRepository<AnswerTemplate, Long> {

    /**
     * 根据模版名称查找模版
     */
    Optional<AnswerTemplate> findByTemplateName(String templateName);

    /**
     * 检查模版名称是否存在
     */
    boolean existsByTemplateName(String templateName);
}
