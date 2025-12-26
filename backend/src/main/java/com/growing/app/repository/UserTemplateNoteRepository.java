package com.growing.app.repository;

import com.growing.app.model.UserTemplateNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTemplateNoteRepository extends JpaRepository<UserTemplateNote, Long> {

    /**
     * 查找指定用户对指定模版的笔记
     */
    Optional<UserTemplateNote> findByTemplateIdAndUserId(Long templateId, Long userId);

    /**
     * 检查指定用户是否对指定模版有笔记
     */
    boolean existsByTemplateIdAndUserId(Long templateId, Long userId);

    /**
     * 统计用户笔记数量
     */
    long countByUserId(Long userId);
}
