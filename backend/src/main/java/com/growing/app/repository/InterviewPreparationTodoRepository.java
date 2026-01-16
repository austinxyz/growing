package com.growing.app.repository;

import com.growing.app.entity.InterviewPreparationTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewPreparationTodoRepository extends JpaRepository<InterviewPreparationTodo, Long> {

    /**
     * 根据面试阶段ID查找所有TODO，按orderIndex排序
     */
    List<InterviewPreparationTodo> findByInterviewStageIdOrderByOrderIndexAsc(Long interviewStageId);

    /**
     * 根据面试阶段ID和用户ID查找TODO
     */
    List<InterviewPreparationTodo> findByInterviewStageIdAndUserIdOrderByOrderIndexAsc(Long interviewStageId, Long userId);

    /**
     * 根据用户ID查找所有TODO
     */
    List<InterviewPreparationTodo> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 删除指定面试阶段的所有TODO
     */
    void deleteByInterviewStageId(Long interviewStageId);
}
