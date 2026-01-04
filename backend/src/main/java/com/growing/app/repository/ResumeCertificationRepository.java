package com.growing.app.repository;

import com.growing.app.entity.ResumeCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 培训证书Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface ResumeCertificationRepository extends JpaRepository<ResumeCertification, Long> {

    /**
     * 根据简历ID查找所有培训证书
     */
    List<ResumeCertification> findByResumeIdOrderBySortOrder(Long resumeId);

    /**
     * 删除指定简历的所有培训证书
     */
    void deleteByResumeId(Long resumeId);
}
