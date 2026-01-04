package com.growing.app.repository;

import com.growing.app.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 公司Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * 根据用户ID查找所有公司
     */
    List<Company> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID和公司名称查找公司
     */
    Optional<Company> findByUserIdAndCompanyName(Long userId, String companyName);

    /**
     * 根据用户ID和行业查找公司
     */
    List<Company> findByUserIdAndIndustryOrderByCreatedAtDesc(Long userId, String industry);

    /**
     * 检查公司名称是否存在
     */
    boolean existsByUserIdAndCompanyName(Long userId, String companyName);
}
