package com.growing.app.repository;

import com.growing.app.entity.ResumeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教育背景Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, Long> {

    /**
     * 根据简历ID查找所有教育背景
     */
    List<ResumeEducation> findByResumeIdOrderBySortOrder(Long resumeId);

    /**
     * 删除指定简历的所有教育背景
     */
    void deleteByResumeId(Long resumeId);
}
