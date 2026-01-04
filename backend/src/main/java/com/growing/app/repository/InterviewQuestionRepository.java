package com.growing.app.repository;

import com.growing.app.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试问题Repository
 * Phase 7: 求职管理模块
 */
@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    /**
     * 根据面试记录ID查找所有问题
     */
    List<InterviewQuestion> findByInterviewRecordIdOrderByQuestionOrder(Long interviewRecordId);

    /**
     * 根据问题类型查找问题
     */
    List<InterviewQuestion> findByQuestionTypeOrderByQuestionOrder(String questionType);

    /**
     * 删除指定面试记录的所有问题
     */
    void deleteByInterviewRecordId(Long interviewRecordId);
}
