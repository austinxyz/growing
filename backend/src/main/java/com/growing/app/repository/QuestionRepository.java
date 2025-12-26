package com.growing.app.repository;

import com.growing.app.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 获取Focus Area下用户可见的试题（公共试题 + 用户自己的私有试题）
     * 按照LeetCode编号排序（编程题优先），无编号的题目按照难度和创建时间排序
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN ProgrammingQuestionDetails pqd ON pqd.question.id = q.id " +
           "WHERE q.focusArea.id = :focusAreaId " +
           "AND (q.isOfficial = true OR q.createdByUser.id = :userId) " +
           "ORDER BY CASE WHEN pqd.leetcodeNumber IS NULL THEN 1 ELSE 0 END, " +
           "pqd.leetcodeNumber ASC, q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    List<Question> findByFocusAreaIdAndVisibleToUser(
        @Param("focusAreaId") Long focusAreaId,
        @Param("userId") Long userId);

    /**
     * 获取Focus Area下的所有试题（管理员视角）
     * 按照LeetCode编号排序
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN ProgrammingQuestionDetails pqd ON pqd.question.id = q.id " +
           "WHERE q.focusArea.id = :focusAreaId " +
           "ORDER BY CASE WHEN pqd.leetcodeNumber IS NULL THEN 1 ELSE 0 END, " +
           "pqd.leetcodeNumber ASC, q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    List<Question> findByFocusAreaId(@Param("focusAreaId") Long focusAreaId);

    /**
     * 获取用户创建的所有试题
     */
    List<Question> findByCreatedByUserId(Long userId);

    /**
     * 获取所有公共试题
     */
    List<Question> findByIsOfficial(Boolean isOfficial);

    /**
     * 获取Focus Area下的公共试题（用于学习总结）
     */
    List<Question> findByFocusAreaIdAndIsOfficial(Long focusAreaId, Boolean isOfficial);

    /**
     * 批量获取某个Skill下所有公共编程题（带Focus Area和Programming Details）
     * 用于学习总结，一次性获取所有需要的数据，避免N+1查询
     */
    @Query("SELECT q FROM Question q " +
           "JOIN FETCH q.focusArea fa " +
           "JOIN FETCH fa.skill s " +
           "WHERE s.id = :skillId " +
           "AND q.isOfficial = true " +
           "AND EXISTS (SELECT 1 FROM ProgrammingQuestionDetails pqd WHERE pqd.question.id = q.id)")
    List<Question> findAllProgrammingQuestionsBySkillId(@Param("skillId") Long skillId);

    /**
     * 统计多个Focus Area的试题数量
     */
    long countByFocusAreaIdIn(List<Long> focusAreaIds);
}
