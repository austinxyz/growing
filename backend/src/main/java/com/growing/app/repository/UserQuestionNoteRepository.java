package com.growing.app.repository;

import com.growing.app.model.UserQuestionNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuestionNoteRepository extends JpaRepository<UserQuestionNote, Long> {

    /**
     * 查找用户对某试题的笔记
     */
    @Query("SELECT n FROM UserQuestionNote n " +
           "WHERE n.question.id = :questionId AND n.user.id = :userId")
    Optional<UserQuestionNote> findByQuestionIdAndUserId(
        @Param("questionId") Long questionId,
        @Param("userId") Long userId);

    /**
     * 查找用户的所有笔记（带JOIN FETCH避免N+1问题）
     */
    @Query("SELECT n FROM UserQuestionNote n " +
           "JOIN FETCH n.question q " +
           "JOIN FETCH q.focusArea fa " +
           "WHERE n.user.id = :userId")
    List<UserQuestionNote> findAllByUserIdWithQuestionAndFocusArea(@Param("userId") Long userId);

    /**
     * 删除用户对某试题的笔记
     */
    void deleteByQuestionIdAndUserId(Long questionId, Long userId);

    /**
     * 统计用户笔记数量
     */
    long countByUserId(Long userId);
}
