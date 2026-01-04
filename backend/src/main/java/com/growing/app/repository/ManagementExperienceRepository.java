package com.growing.app.repository;

import com.growing.app.entity.ManagementExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 人员管理经验Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ManagementExperienceRepository extends JpaRepository<ManagementExperience, Long> {

    /**
     * 根据用户ID查找所有管理经验
     */
    List<ManagementExperience> findByUserIdOrderByStartDateDesc(Long userId);

    /**
     * 根据用户ID和经验类型查找管理经验
     */
    List<ManagementExperience> findByUserIdAndExperienceTypeOrderByStartDateDesc(Long userId, String experienceType);

    /**
     * 删除指定用户的所有管理经验
     */
    void deleteByUserId(Long userId);
}
