package com.growing.app.repository;

import com.growing.app.entity.CompanyLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公司链接Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface CompanyLinkRepository extends JpaRepository<CompanyLink, Long> {

    /**
     * 根据公司ID查找所有链接
     */
    List<CompanyLink> findByCompanyIdOrderBySortOrder(Long companyId);

    /**
     * 删除指定公司的所有链接
     */
    void deleteByCompanyId(Long companyId);
}
