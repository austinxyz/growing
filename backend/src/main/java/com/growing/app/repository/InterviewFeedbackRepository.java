package com.growing.app.repository;

import com.growing.app.entity.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试反馈Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {

    /**
     * 根据面试记录ID查找所有反馈
     */
    List<InterviewFeedback> findByInterviewRecordIdOrderByFeedbackDate(Long interviewRecordId);

    /**
     * 删除指定面试记录的所有反馈
     */
    void deleteByInterviewRecordId(Long interviewRecordId);
}
