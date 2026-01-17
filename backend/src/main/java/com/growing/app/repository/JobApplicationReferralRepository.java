package com.growing.app.repository;

import com.growing.app.entity.JobApplicationReferral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JobApplicationReferral Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface JobApplicationReferralRepository extends JpaRepository<JobApplicationReferral, Long> {

    // 查找某个职位的所有关联人脉
    List<JobApplicationReferral> findByJobApplicationId(Long jobApplicationId);

    // 查找某个人脉关联的所有职位
    List<JobApplicationReferral> findByReferralId(Long referralId);

    // 检查特定职位-人脉关联是否存在
    Optional<JobApplicationReferral> findByJobApplicationIdAndReferralId(Long jobApplicationId, Long referralId);

    // 删除职位的所有关联
    void deleteByJobApplicationId(Long jobApplicationId);

    // 删除特定的职位-人脉关联
    void deleteByJobApplicationIdAndReferralId(Long jobApplicationId, Long referralId);
}
