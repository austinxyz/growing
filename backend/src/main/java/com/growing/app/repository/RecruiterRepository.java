package com.growing.app.repository;

import com.growing.app.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Recruiter Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {

    /**
     * 根据用户ID查找所有Recruiter
     */
    List<Recruiter> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据公司ID查找所有Recruiter
     */
    List<Recruiter> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    /**
     * 删除指定公司的所有Recruiter
     */
    void deleteByCompanyId(Long companyId);
}
