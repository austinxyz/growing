package com.growing.app.repository;

import com.growing.app.entity.InterviewPreparationTodoResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试准备TODO资源关联Repository
 */
@Repository
public interface InterviewPreparationTodoResourceRepository extends JpaRepository<InterviewPreparationTodoResource, Long> {

    /**
     * 根据TODO ID查询所有资源
     */
    List<InterviewPreparationTodoResource> findByTodoId(Long todoId);

    /**
     * 根据TODO ID列表批量查询资源
     */
    @Query("SELECT r FROM InterviewPreparationTodoResource r WHERE r.todo.id IN :todoIds")
    List<InterviewPreparationTodoResource> findByTodoIdIn(List<Long> todoIds);

    /**
     * 删除指定TODO的所有资源
     */
    @Modifying
    @Query("DELETE FROM InterviewPreparationTodoResource r WHERE r.todo.id = :todoId")
    void deleteByTodoId(Long todoId);
}
