package com.growing.app.repository;

import com.growing.app.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 人脉推荐Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    /**
     * 根据用户ID查找所有推荐人
     */
    List<Referral> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据公司ID查找所有推荐人
     */
    List<Referral> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    /**
     * 根据用户ID和推荐状态查找推荐人
     */
    List<Referral> findByUserIdAndReferralStatusOrderByCreatedAtDesc(Long userId, String referralStatus);

    /**
     * 删除指定公司的所有推荐人
     */
    void deleteByCompanyId(Long companyId);
}
