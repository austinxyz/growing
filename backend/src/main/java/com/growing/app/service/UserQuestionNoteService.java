package com.growing.app.service;

import com.growing.app.dto.UserQuestionNoteDTO;
import com.growing.app.model.Question;
import com.growing.app.model.User;
import com.growing.app.model.UserQuestionNote;
import com.growing.app.repository.QuestionRepository;
import com.growing.app.repository.UserQuestionNoteRepository;
import com.growing.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserQuestionNoteService {

    @Autowired
    private UserQuestionNoteRepository noteRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户对某试题的笔记
     */
    public UserQuestionNoteDTO getNoteByQuestionIdAndUserId(Long questionId, Long userId) {
        UserQuestionNote note = noteRepository.findByQuestionIdAndUserId(questionId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "笔记不存在"));
        return convertToDTO(note);
    }

    /**
     * 创建或更新笔记（UPSERT逻辑）
     * @deprecated 使用 saveOrUpdateNote(Long questionId, UserQuestionNoteDTO dto, Long userId) 替代
     */
    @Deprecated
    @Transactional
    public UserQuestionNoteDTO saveOrUpdateNote(Long questionId, String noteContent, Long userId) {
        UserQuestionNoteDTO dto = new UserQuestionNoteDTO();
        dto.setNoteContent(noteContent);
        return saveOrUpdateNote(questionId, dto, userId);
    }

    /**
     * 创建或更新笔记（UPSERT逻辑，支持noteContent和coreStrategy）
     */
    @Transactional
    public UserQuestionNoteDTO saveOrUpdateNote(Long questionId, UserQuestionNoteDTO dto, Long userId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 查找是否已存在笔记
        Optional<UserQuestionNote> existingNote =
            noteRepository.findByQuestionIdAndUserId(questionId, userId);

        UserQuestionNote note;
        if (existingNote.isPresent()) {
            // 更新现有笔记
            note = existingNote.get();
            note.setNoteContent(dto.getNoteContent());
            note.setCoreStrategy(dto.getCoreStrategy());
        } else {
            // 创建新笔记
            note = new UserQuestionNote();
            note.setQuestion(question);
            note.setUser(user);
            note.setNoteContent(dto.getNoteContent());
            note.setCoreStrategy(dto.getCoreStrategy());
        }

        return convertToDTO(noteRepository.save(note));
    }

    /**
     * 删除笔记
     */
    @Transactional
    public void deleteNote(Long questionId, Long userId) {
        UserQuestionNote note = noteRepository.findByQuestionIdAndUserId(questionId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "笔记不存在"));

        // 确保只能删除自己的笔记
        if (!note.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此笔记");
        }

        noteRepository.delete(note);
    }

    /**
     * 转换为DTO
     */
    private UserQuestionNoteDTO convertToDTO(UserQuestionNote note) {
        UserQuestionNoteDTO dto = new UserQuestionNoteDTO();
        dto.setId(note.getId());
        dto.setQuestionId(note.getQuestion().getId());
        dto.setUserId(note.getUser().getId());
        dto.setNoteContent(note.getNoteContent());
        dto.setCoreStrategy(note.getCoreStrategy());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
