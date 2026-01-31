package com.growing.app.repository;

import com.growing.app.model.TemplateQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateQuestionRepository extends JpaRepository<TemplateQuestion, Long> {

    /**
     * 查询某个模板关联的所有题目
     */
    List<TemplateQuestion> findByTemplateId(Long templateId);

    /**
     * 查询某道题关联的所有模板
     */
    List<TemplateQuestion> findByQuestionId(Long questionId);

    /**
     * 检查模板和题目是否已关联
     */
    Optional<TemplateQuestion> findByTemplateIdAndQuestionId(Long templateId, Long questionId);

    /**
     * 删除特定的模板-题目关联
     */
    void deleteByTemplateIdAndQuestionId(Long templateId, Long questionId);
}
