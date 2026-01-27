package com.growing.app.repository;

import com.growing.app.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 职位申请Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    /**
     * 根据用户ID查找所有职位申请
     */
    List<JobApplication> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据公司ID查找所有职位申请
     */
    List<JobApplication> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    /**
     * 根据用户ID和申请状态查找职位
     */
    List<JobApplication> findByUserIdAndApplicationStatusOrderByCreatedAtDesc(Long userId, String applicationStatus);

    /**
     * 根据用户ID、公司ID和职位名称查找职位
     */
    Optional<JobApplication> findByUserIdAndCompanyIdAndPositionName(Long userId, Long companyId, String positionName);

    /**
     * 删除指定公司的所有职位申请
     */
    void deleteByCompanyId(Long companyId);

    /**
     * 统计用户的职位申请数量
     */
    Long countByUserId(Long userId);
}
