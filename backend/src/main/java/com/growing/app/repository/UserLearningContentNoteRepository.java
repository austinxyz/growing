package com.growing.app.repository;

import com.growing.app.model.UserLearningContentNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearningContentNoteRepository extends JpaRepository<UserLearningContentNote, Long> {

    /**
     * 根据学习资料ID和用户ID查找笔记
     */
    Optional<UserLearningContentNote> findByLearningContentIdAndUserId(Long learningContentId, Long userId);

    /**
     * 删除指定学习资料和用户的笔记
     */
    void deleteByLearningContentIdAndUserId(Long learningContentId, Long userId);
}
