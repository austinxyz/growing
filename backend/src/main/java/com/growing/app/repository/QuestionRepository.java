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
     */
    @Query("SELECT q FROM Question q " +
           "WHERE q.focusArea.id = :focusAreaId " +
           "AND (q.isOfficial = true OR q.createdByUser.id = :userId) " +
           "ORDER BY q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    List<Question> findByFocusAreaIdAndVisibleToUser(
        @Param("focusAreaId") Long focusAreaId,
        @Param("userId") Long userId);

    /**
     * 获取Focus Area下的所有试题（管理员视角）
     */
    @Query("SELECT q FROM Question q " +
           "WHERE q.focusArea.id = :focusAreaId " +
           "ORDER BY q.difficulty ASC, q.displayOrder ASC, q.createdAt DESC")
    List<Question> findByFocusAreaId(@Param("focusAreaId") Long focusAreaId);

    /**
     * 获取用户创建的所有试题
     */
    List<Question> findByCreatedByUserId(Long userId);

    /**
     * 获取所有公共试题
     */
    List<Question> findByIsOfficial(Boolean isOfficial);
}
