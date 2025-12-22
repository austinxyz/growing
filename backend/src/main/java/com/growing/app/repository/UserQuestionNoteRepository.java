package com.growing.app.repository;

import com.growing.app.model.UserQuestionNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * 删除用户对某试题的笔记
     */
    void deleteByQuestionIdAndUserId(Long questionId, Long userId);
}
