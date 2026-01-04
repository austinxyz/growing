package com.growing.app.repository;

import com.growing.app.entity.RecruiterCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Recruiter沟通记录Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface RecruiterCommunicationRepository extends JpaRepository<RecruiterCommunication, Long> {

    /**
     * 根据Recruiter ID查找所有沟通记录
     */
    List<RecruiterCommunication> findByRecruiterIdOrderByCommunicationDateDesc(Long recruiterId);

    /**
     * 根据职位申请ID查找所有沟通记录
     */
    List<RecruiterCommunication> findByJobApplicationIdOrderByCommunicationDateDesc(Long jobApplicationId);

    /**
     * 删除指定Recruiter的所有沟通记录
     */
    void deleteByRecruiterId(Long recruiterId);
}
