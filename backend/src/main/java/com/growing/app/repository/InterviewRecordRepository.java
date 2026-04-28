package com.growing.app.repository;

import com.growing.app.entity.InterviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试记录Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface InterviewRecordRepository extends JpaRepository<InterviewRecord, Long> {

    /**
     * 根据职位申请ID查找所有面试记录
     */
    List<InterviewRecord> findByJobApplicationIdOrderByInterviewDateDesc(Long jobApplicationId);

    /**
     * 统计职位的面试记录数量
     */
    int countByJobApplicationId(Long jobApplicationId);

    /**
     * 根据面试阶段ID查找所有面试记录
     */
    List<InterviewRecord> findByInterviewStageIdOrderByInterviewDateDesc(Long interviewStageId);

    /**
     * 删除指定职位的所有面试记录
     */
    void deleteByJobApplicationId(Long jobApplicationId);

    /**
     * 批量加载多个职位申请的所有面试记录（用于面试进展看板，避免 N+1）
     */
    List<InterviewRecord> findByJobApplicationIdInOrderByInterviewDateDesc(
            List<Long> jobApplicationIds);
}
