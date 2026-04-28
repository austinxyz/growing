package com.growing.app.repository;

import com.growing.app.entity.InterviewStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试流程阶段Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface InterviewStageRepository extends JpaRepository<InterviewStage, Long> {

    /**
     * 根据职位申请ID查找所有面试阶段
     */
    List<InterviewStage> findByJobApplicationIdOrderByStageOrder(Long jobApplicationId);

    /**
     * 统计职位的面试阶段数量
     */
    int countByJobApplicationId(Long jobApplicationId);

    /**
     * 删除指定职位的所有面试阶段
     */
    void deleteByJobApplicationId(Long jobApplicationId);

    /**
     * 批量加载多个职位申请的所有阶段（用于面试进展看板，避免 N+1）
     */
    List<InterviewStage> findByJobApplicationIdInOrderByStageOrder(List<Long> jobApplicationIds);
}
