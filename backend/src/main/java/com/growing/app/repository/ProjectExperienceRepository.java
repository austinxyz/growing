package com.growing.app.repository;

import com.growing.app.entity.ProjectExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 项目经验Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ProjectExperienceRepository extends JpaRepository<ProjectExperience, Long> {

    /**
     * 根据用户ID查找所有项目经验
     */
    List<ProjectExperience> findByUserIdOrderByStartDateDesc(Long userId);

    /**
     * 根据用户ID和项目类型查找项目经验
     */
    List<ProjectExperience> findByUserIdAndProjectTypeOrderByStartDateDesc(Long userId, String projectType);

    /**
     * 删除指定用户的所有项目经验
     */
    void deleteByUserId(Long userId);
}
