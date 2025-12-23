package com.growing.app.service;

import com.growing.app.dto.UserTemplateNoteDTO;
import com.growing.app.model.UserTemplateNote;
import com.growing.app.repository.UserTemplateNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserTemplateNoteService {

    @Autowired
    private UserTemplateNoteRepository userTemplateNoteRepository;

    /**
     * 保存或更新用户模版笔记（UPSERT逻辑）
     * 一个用户对一个模版只能有一条笔记，如果已存在则更新
     */
    @Transactional
    public UserTemplateNoteDTO saveOrUpdateNote(Long templateId, Long userId, String noteContent) {
        if (noteContent == null || noteContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Note content cannot be empty");
        }

        Optional<UserTemplateNote> existingNoteOpt = userTemplateNoteRepository
                .findByTemplateIdAndUserId(templateId, userId);

        UserTemplateNote note;
        if (existingNoteOpt.isPresent()) {
            // 更新已有笔记
            note = existingNoteOpt.get();
            note.setNoteContent(noteContent);
        } else {
            // 创建新笔记
            note = new UserTemplateNote(templateId, userId, noteContent);
        }

        UserTemplateNote savedNote = userTemplateNoteRepository.save(note);
        return convertToDTO(savedNote);
    }

    /**
     * 获取用户对指定模版的笔记
     */
    public Optional<UserTemplateNoteDTO> getNoteByTemplateAndUser(Long templateId, Long userId) {
        return userTemplateNoteRepository.findByTemplateIdAndUserId(templateId, userId)
                .map(this::convertToDTO);
    }

    /**
     * 删除用户笔记
     */
    @Transactional
    public void deleteNote(Long templateId, Long userId) {
        userTemplateNoteRepository.findByTemplateIdAndUserId(templateId, userId)
                .ifPresent(note -> userTemplateNoteRepository.delete(note));
    }

    /**
     * 转换为DTO
     */
    private UserTemplateNoteDTO convertToDTO(UserTemplateNote note) {
        return new UserTemplateNoteDTO(
                note.getId(),
                note.getTemplateId(),
                note.getUserId(),
                note.getNoteContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
