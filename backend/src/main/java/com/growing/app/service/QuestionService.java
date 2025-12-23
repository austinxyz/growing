package com.growing.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ProgrammingQuestionDetailsDTO;
import com.growing.app.dto.QuestionDTO;
import com.growing.app.dto.UserQuestionNoteDTO;
import com.growing.app.model.FocusArea;
import com.growing.app.model.Question;
import com.growing.app.model.User;
import com.growing.app.model.UserQuestionNote;
import com.growing.app.repository.FocusAreaRepository;
import com.growing.app.repository.QuestionRepository;
import com.growing.app.repository.UserQuestionNoteRepository;
import com.growing.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQuestionNoteRepository noteRepository;

    @Autowired
    private ProgrammingQuestionDetailsService programmingDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取Focus Area下的试题（用户可见，含编程题详情和用户笔记）
     */
    public List<QuestionDTO> getQuestionsByFocusAreaId(Long focusAreaId, Long userId) {
        return questionRepository.findByFocusAreaIdAndVisibleToUser(focusAreaId, userId)
                .stream()
                .map(question -> {
                    // 转换为DTO并加载编程题详情
                    QuestionDTO dto = convertToDTOWithDetails(question);

                    // 加载用户笔记
                    Optional<UserQuestionNote> note = noteRepository.findByQuestionIdAndUserId(question.getId(), userId);
                    note.ifPresent(n -> dto.setNote(convertNoteToDTO(n)));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取Focus Area下的所有试题（管理员，含编程题详情）
     */
    public List<QuestionDTO> getAllQuestionsByFocusAreaId(Long focusAreaId) {
        return questionRepository.findByFocusAreaId(focusAreaId)
                .stream()
                .map(this::convertToDTOWithDetails)
                .collect(Collectors.toList());
    }

    /**
     * 获取试题详情（含用户笔记）
     */
    public QuestionDTO getQuestionByIdForUser(Long id, Long userId) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        QuestionDTO dto = convertToDTO(question);

        // 加载用户笔记
        Optional<UserQuestionNote> note = noteRepository.findByQuestionIdAndUserId(id, userId);
        note.ifPresent(n -> dto.setUserNote(convertNoteToDTO(n)));

        return dto;
    }

    /**
     * 获取试题详情（不含用户笔记）
     */
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));
        return convertToDTO(question);
    }

    /**
     * 创建试题
     */
    @Transactional
    public QuestionDTO createQuestion(QuestionDTO dto, Long userId, boolean isOfficial) {
        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setQuestionText(dto.getQuestionText());
        question.setDifficulty(dto.getDifficulty());
        question.setAnswerRequirement(dto.getAnswerRequirement());
        question.setTargetPosition(dto.getTargetPosition());
        question.setTargetLevel(dto.getTargetLevel());

        // Red Flags存储为JSON数组字符串
        if (dto.getRedFlags() != null && !dto.getRedFlags().isEmpty()) {
            question.setRedFlagsList(dto.getRedFlags());
        }

        // 关联Focus Area
        FocusArea focusArea = focusAreaRepository.findById(dto.getFocusAreaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "专注领域不存在"));
        question.setFocusArea(focusArea);

        // 设置创建者（用户创建）或is_official（管理员创建）
        if (isOfficial) {
            question.setIsOfficial(true);
            question.setCreatedByUser(null);
        } else {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
            question.setCreatedByUser(user);
            question.setIsOfficial(false);
        }

        if (dto.getDisplayOrder() != null) {
            question.setDisplayOrder(dto.getDisplayOrder());
        }

        return convertToDTO(questionRepository.save(question));
    }

    /**
     * 更新试题
     */
    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto, Long userId, boolean isAdmin) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        // 权限检查：管理员可修改所有，用户只能修改自己的
        if (!isAdmin && !question.getCreatedByUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此试题");
        }

        question.setTitle(dto.getTitle());
        question.setQuestionText(dto.getQuestionText());
        question.setDifficulty(dto.getDifficulty());
        question.setAnswerRequirement(dto.getAnswerRequirement());
        question.setTargetPosition(dto.getTargetPosition());
        question.setTargetLevel(dto.getTargetLevel());

        // 更新Red Flags
        if (dto.getRedFlags() != null) {
            question.setRedFlagsList(dto.getRedFlags());
        }

        // 更新Focus Area
        if (dto.getFocusAreaId() != null && !question.getFocusArea().getId().equals(dto.getFocusAreaId())) {
            FocusArea focusArea = focusAreaRepository.findById(dto.getFocusAreaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "专注领域不存在"));
            question.setFocusArea(focusArea);
        }

        if (dto.getDisplayOrder() != null) {
            question.setDisplayOrder(dto.getDisplayOrder());
        }

        return convertToDTO(questionRepository.save(question));
    }

    /**
     * 删除试题
     */
    @Transactional
    public void deleteQuestion(Long id, Long userId, boolean isAdmin) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        // 权限检查：管理员可删除所有，用户只能删除自己的
        if (!isAdmin && (question.getCreatedByUser() == null || !question.getCreatedByUser().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此试题");
        }

        questionRepository.delete(question);
    }

    /**
     * 转换为DTO
     */
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setFocusAreaId(question.getFocusArea().getId());
        dto.setFocusAreaName(question.getFocusArea().getName());
        dto.setTitle(question.getTitle());
        dto.setQuestionDescription(question.getQuestionDescription());
        dto.setQuestionText(question.getQuestionText());
        dto.setDifficulty(question.getDifficulty());
        dto.setAnswerRequirement(question.getAnswerRequirement());
        dto.setTargetPosition(question.getTargetPosition());
        dto.setTargetLevel(question.getTargetLevel());
        dto.setRedFlags(question.getRedFlagsList());
        dto.setIsOfficial(question.getIsOfficial());
        dto.setCreatedByUserId(question.getCreatedByUser() != null ? question.getCreatedByUser().getId() : null);
        dto.setDisplayOrder(question.getDisplayOrder());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getUpdatedAt());
        return dto;
    }

    /**
     * 转换为DTO（含编程题详情）
     */
    private QuestionDTO convertToDTOWithDetails(Question question) {
        QuestionDTO dto = convertToDTO(question);

        // 加载编程题详情（如果存在）
        programmingDetailsService.getDetailsByQuestionId(question.getId())
                .ifPresent(dto::setProgrammingDetails);

        return dto;
    }

    /**
     * 转换笔记为DTO
     */
    private UserQuestionNoteDTO convertNoteToDTO(UserQuestionNote note) {
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

    // ==================== 编程题详情管理 ====================

    /**
     * 创建编程题（含详情）
     * 用于管理员创建编程题时一次性创建Question + ProgrammingQuestionDetails
     */
    @Transactional
    public QuestionDTO createQuestionWithDetails(QuestionDTO questionDTO,
                                                  ProgrammingQuestionDetailsDTO detailsDTO,
                                                  Long userId,
                                                  boolean isOfficial) {
        // 先创建Question
        QuestionDTO createdQuestion = createQuestion(questionDTO, userId, isOfficial);

        // 如果提供了详情，创建ProgrammingQuestionDetails
        if (detailsDTO != null) {
            ProgrammingQuestionDetailsDTO createdDetails =
                    programmingDetailsService.createDetails(createdQuestion.getId(), detailsDTO);
            createdQuestion.setProgrammingDetails(createdDetails);
        }

        return createdQuestion;
    }

    /**
     * 更新编程题（含详情）
     * 用于管理员更新编程题时同时更新Question + ProgrammingQuestionDetails
     */
    @Transactional
    public QuestionDTO updateQuestionWithDetails(Long questionId,
                                                  QuestionDTO questionDTO,
                                                  ProgrammingQuestionDetailsDTO detailsDTO,
                                                  Long userId,
                                                  boolean isAdmin) {
        // 更新Question
        QuestionDTO updatedQuestion = updateQuestion(questionId, questionDTO, userId, isAdmin);

        // 更新或创建ProgrammingQuestionDetails
        if (detailsDTO != null) {
            Optional<ProgrammingQuestionDetailsDTO> existingDetails =
                    programmingDetailsService.getDetailsByQuestionId(questionId);

            if (existingDetails.isPresent()) {
                // 更新现有详情
                ProgrammingQuestionDetailsDTO updated =
                        programmingDetailsService.updateDetailsByQuestionId(questionId, detailsDTO);
                updatedQuestion.setProgrammingDetails(updated);
            } else {
                // 创建新详情
                ProgrammingQuestionDetailsDTO created =
                        programmingDetailsService.createDetails(questionId, detailsDTO);
                updatedQuestion.setProgrammingDetails(created);
            }
        }

        return updatedQuestion;
    }

    /**
     * 获取编程题详情（用于管理端展示）
     * 包含Question + ProgrammingQuestionDetails
     */
    public QuestionDTO getQuestionWithDetailsForAdmin(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        return convertToDTOWithDetails(question);
    }

    /**
     * 获取编程题详情（用于用户端展示）
     * 包含Question + ProgrammingQuestionDetails + UserQuestionNote
     */
    public QuestionDTO getQuestionWithDetailsForUser(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        QuestionDTO dto = convertToDTOWithDetails(question);

        // 加载用户笔记
        Optional<UserQuestionNote> note = noteRepository.findByQuestionIdAndUserId(questionId, userId);
        note.ifPresent(n -> dto.setUserNote(convertNoteToDTO(n)));

        return dto;
    }
}
