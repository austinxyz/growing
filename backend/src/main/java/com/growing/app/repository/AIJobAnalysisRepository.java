package com.growing.app.repository;

import com.growing.app.entity.AIJobAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI Job Analysis Repository
 */
@Repository
public interface AIJobAnalysisRepository extends JpaRepository<AIJobAnalysis, Long> {

    /**
     * 查找用户对某个Job的最新分析记录（用于upsert逻辑）
     */
    Optional<AIJobAnalysis> findFirstByJobApplicationIdAndResumeIdAndUserIdOrderByCreatedAtDesc(
            Long jobApplicationId, Long resumeId, Long userId
    );

    /**
     * 查询某个Job Application的所有分析记录（按创建时间倒序）
     */
    List<AIJobAnalysis> findByJobApplicationIdOrderByCreatedAtDesc(Long jobApplicationId);

    /**
     * 查询用户的所有分析记录
     */
    List<AIJobAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);
}
