package com.growing.app.repository;

import com.growing.app.entity.ResumeExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工作经历Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, Long> {

    /**
     * 根据简历ID查找所有工作经历
     */
    List<ResumeExperience> findByResumeIdOrderBySortOrder(Long resumeId);

    /**
     * 删除指定简历的所有工作经历
     */
    void deleteByResumeId(Long resumeId);
}
