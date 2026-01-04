package com.growing.app.repository;

import com.growing.app.entity.ResumeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 简历技能Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {

    /**
     * 根据简历ID查找所有技能
     */
    List<ResumeSkill> findByResumeIdOrderBySortOrder(Long resumeId);

    /**
     * 根据简历ID和技能分类查找技能
     */
    List<ResumeSkill> findByResumeIdAndSkillCategoryOrderBySortOrder(Long resumeId, String skillCategory);

    /**
     * 删除指定简历的所有技能
     */
    void deleteByResumeId(Long resumeId);
}
