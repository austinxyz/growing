package com.growing.app.repository;

import com.growing.app.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * 获取Focus Area下用户可见的试题（分页版本）
     * ✅ 优化：支持分页，减少数据传输量
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN ProgrammingQuestionDetails pqd ON pqd.question.id = q.id " +
           "WHERE q.focusArea.id = :focusAreaId " +
           "AND (q.isOfficial = true OR q.createdByUser.id = :userId) " +
           "ORDER BY CASE WHEN pqd.leetcodeNumber IS NULL THEN 1 ELSE 0 END, " +
           "pqd.leetcodeNumber ASC, q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    Page<Question> findByFocusAreaIdAndVisibleToUserPaged(
        @Param("focusAreaId") Long focusAreaId,
        @Param("userId") Long userId,
        Pageable pageable);

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

    /**
     * 批量获取多个Focus Area下用户可见的试题
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN ProgrammingQuestionDetails pqd ON pqd.question.id = q.id " +
           "WHERE q.focusArea.id IN :focusAreaIds " +
           "AND (q.isOfficial = true OR q.createdByUser.id = :userId) " +
           "ORDER BY CASE WHEN pqd.leetcodeNumber IS NULL THEN 1 ELSE 0 END, " +
           "pqd.leetcodeNumber ASC, q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    List<Question> findByFocusAreaIdInAndVisibleToUser(
        @Param("focusAreaIds") List<Long> focusAreaIds,
        @Param("userId") Long userId);

    /**
     * 获取所有用户可见的试题（公共试题 + 用户自己的私有试题）
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN ProgrammingQuestionDetails pqd ON pqd.question.id = q.id " +
           "WHERE (q.isOfficial = true OR q.createdByUser.id = :userId) " +
           "ORDER BY CASE WHEN pqd.leetcodeNumber IS NULL THEN 1 ELSE 0 END, " +
           "pqd.leetcodeNumber ASC, q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    List<Question> findAllVisibleToUser(@Param("userId") Long userId);

    /**
     * 高性能搜索试题（支持分页 + 多条件过滤）
     *
     * 性能优化：
     * 1. 单次数据库查询（避免N+1问题）
     * 2. 数据库层面过滤（避免内存过滤）
     * 3. 支持分页（减少数据传输）
     * 4. LEFT JOIN 编程题详情（用于排序）
     *
     * 参数说明：
     * - focusAreaIds: Focus Area ID列表（为空则不过滤）
     * - keyword: 关键字（搜索标题和描述，为空则不过滤）
     * - questionType: 试题类型（为空则不过滤）
     * - difficulty: 难度（为空则不过滤）
     * - isPriorityOnly: 是否只显示重点题（true=只显示重点，false/null=显示所有）
     * - userId: 用户ID（用于权限过滤和重点题过滤）
     * - pageable: 分页参数
     */
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN ProgrammingQuestionDetails pqd ON pqd.question.id = q.id " +
           "LEFT JOIN UserQuestionNote uqn ON uqn.question.id = q.id AND uqn.user.id = :userId " +
           "WHERE (q.isOfficial = true OR q.createdByUser.id = :userId) " +
           "AND (:#{#focusAreaIds == null} = true OR q.focusArea.id IN :focusAreaIds) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(q.questionDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:#{#questionTypes == null || #questionTypes.isEmpty()} = true OR q.questionType IN :questionTypes) " +
           "AND (:#{#difficulties == null || #difficulties.isEmpty()} = true OR q.difficulty IN :difficulties) " +
           "AND (:isPriorityOnly IS NULL OR :isPriorityOnly = false OR (uqn.isPriority = true)) " +
           "ORDER BY CASE WHEN pqd.leetcodeNumber IS NULL THEN 1 ELSE 0 END, " +
           "pqd.leetcodeNumber ASC, q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    Page<Question> searchQuestionsOptimized(
        @Param("focusAreaIds") List<Long> focusAreaIds,
        @Param("keyword") String keyword,
        @Param("questionTypes") List<Question.QuestionType> questionTypes,
        @Param("difficulties") List<Question.Difficulty> difficulties,
        @Param("isPriorityOnly") Boolean isPriorityOnly,
        @Param("userId") Long userId,
        Pageable pageable
    );
}
