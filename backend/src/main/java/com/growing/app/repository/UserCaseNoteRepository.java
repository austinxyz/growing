package com.growing.app.repository;

import com.growing.app.model.UserCaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCaseNoteRepository extends JpaRepository<UserCaseNote, Long> {

    /**
     * 查询用户对某个案例的答题记录
     * UNIQUE约束保证一个用户对一个案例只有一条记录
     */
    Optional<UserCaseNote> findBySystemDesignCaseIdAndUserId(Long caseId, Long userId);

    /**
     * 查询用户的所有答题记录（按更新时间倒序）
     */
    List<UserCaseNote> findByUserIdOrderByUpdatedAtDesc(Long userId);

    /**
     * 查询用户的所有答题记录（用于学习总结）
     */
    List<UserCaseNote> findByUserId(Long userId);

    /**
     * 删除用户对某个案例的答题记录
     */
    void deleteBySystemDesignCaseIdAndUserId(Long caseId, Long userId);
}
