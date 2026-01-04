package com.growing.app.repository;

import com.growing.app.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 简历Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /**
     * 根据用户ID查找所有简历
     */
    List<Resume> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID和版本名称查找简历
     */
    Optional<Resume> findByUserIdAndVersionName(Long userId, String versionName);

    /**
     * 查找用户的默认简历
     */
    Optional<Resume> findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 检查版本名称是否存在
     */
    boolean existsByUserIdAndVersionName(Long userId, String versionName);
}
