package com.growing.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.AnswerTemplateDTO;
import com.growing.app.dto.ProgrammingQuestionDetailsDTO;
import com.growing.app.dto.QuestionDTO;
import com.growing.app.dto.QuestionListDTO;
import com.growing.app.dto.UserQuestionNoteDTO;
import com.growing.app.model.FocusArea;
import com.growing.app.model.Question;
import com.growing.app.model.User;
import com.growing.app.model.UserQuestionNote;
import com.growing.app.repository.*;
import com.growing.app.model.MajorCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

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
    private FocusAreaCategoryRepository focusAreaCategoryRepository;

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillTemplateService skillTemplateService;

    @Autowired
    private AnswerTemplateService answerTemplateService;

    @Autowired
    private HierarchyCacheService hierarchyCacheService;

    /**
     * 获取Focus Area下的试题列表（轻量级，支持分页）
     * ✅ 优化：只返回QuestionListDTO，不包含description、笔记、编程题详情
     */
    public Map<String, Object> getQuestionListByFocusArea(Long focusAreaId, Integer page, Integer size, Long userId) {
        // 1. 创建分页参数（默认第0页，每页20条）
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            size != null && size > 0 && size <= 100 ? size : 20
        );

        // 2. 查询试题列表（不加载详情）
        Page<Question> questionPage = questionRepository.findByFocusAreaIdAndVisibleToUserPaged(focusAreaId, userId, pageable);

        // 3. 批量加载用户笔记状态（只需要isPriority和hasUserNote标志）
        List<Long> questionIds = questionPage.getContent().stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        Map<Long, UserQuestionNote> userNotesMap = new HashMap<>();
        if (!questionIds.isEmpty() && userId != null) {
            List<UserQuestionNote> userNotes = noteRepository.findByQuestionIdsAndUserId(questionIds, userId);
            userNotesMap = userNotes.stream()
                    .collect(Collectors.toMap(n -> n.getQuestion().getId(), n -> n));
        }

        // 4. 转换为轻量级ListDTO
        final Map<Long, UserQuestionNote> finalUserNotesMap = userNotesMap;
        List<QuestionListDTO> questionListDTOs = questionPage.getContent().stream()
                .map(question -> convertToListDTO(question, finalUserNotesMap.get(question.getId())))
                .collect(Collectors.toList());

        // 5. 构造分页响应
        Map<String, Object> response = new HashMap<>();
        response.put("content", questionListDTOs);
        response.put("totalElements", questionPage.getTotalElements());
        response.put("totalPages", questionPage.getTotalPages());
        response.put("currentPage", questionPage.getNumber());
        response.put("pageSize", questionPage.getSize());
        response.put("hasNext", questionPage.hasNext());
        response.put("hasPrevious", questionPage.hasPrevious());

        return response;
    }

    /**
     * 获取Focus Area下的试题（用户可见，含编程题详情、用户笔记和AI笔记）
     * @deprecated 使用getQuestionListByFocusArea替代（支持分页，性能更好）
     */
    @Deprecated
    public List<QuestionDTO> getQuestionsByFocusAreaId(Long focusAreaId, Long userId) {
        // 1. 获取试题列表
        List<Question> questions = questionRepository.findByFocusAreaIdAndVisibleToUser(focusAreaId, userId);

        if (questions.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量加载笔记（避免N+1查询）
        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // 批量查询用户笔记
        Map<Long, UserQuestionNote> userNotesMap = new HashMap<>();
        if (userId != null) {
            List<UserQuestionNote> userNotes = noteRepository.findByQuestionIdsAndUserId(questionIds, userId);
            userNotesMap = userNotes.stream()
                    .collect(Collectors.toMap(n -> n.getQuestion().getId(), n -> n));
        }

        // 批量查询AI笔记（user_id = -1）
        List<UserQuestionNote> aiNotes = noteRepository.findByQuestionIdsAndUserId(questionIds, -1L);
        Map<Long, UserQuestionNote> aiNotesMap = aiNotes.stream()
                .collect(Collectors.toMap(n -> n.getQuestion().getId(), n -> n));

        // 3. 转换为DTO（使用已加载的笔记数据）
        final Map<Long, UserQuestionNote> finalUserNotesMap = userNotesMap;
        final Map<Long, UserQuestionNote> finalAiNotesMap = aiNotesMap;

        return questions.stream()
                .map(question -> {
                    // 转换为DTO并加载编程题详情
                    QuestionDTO dto = convertToDTOWithDetails(question);

                    // 从Map中获取笔记（避免额外查询）
                    UserQuestionNote userNote = finalUserNotesMap.get(question.getId());
                    if (userNote != null) {
                        dto.setUserNote(convertNoteToDTO(userNote));
                    }

                    UserQuestionNote aiNote = finalAiNotesMap.get(question.getId());
                    if (aiNote != null) {
                        dto.setAiNote(convertNoteToDTO(aiNote));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 搜索试题（支持多条件过滤 + 分页）- 性能优化版本
     *
     * 性能优化：
     * 1. 统一转换为focusAreaIds，减少数据库查询次数
     * 2. 使用单次数据库查询，在SQL层面进行所有过滤
     * 3. 批量加载笔记，解决N+1问题
     * 4. 支持分页，减少数据传输量
     *
     * 支持的查询条件:
     * - keyword: 关键字搜索（标题、描述）
     * - careerPathId, skillId, majorCategoryId, focusAreaId: 级联筛选
     * - questionType: 试题类型
     * - difficulty: 难度
     * - isPriorityOnly: 是否只显示重点题
     * - page, size: 分页参数
     * - userId: 用户ID（用于过滤可见性和加载笔记）
     */
    public Map<String, Object> searchQuestions(
            String keyword,
            Long careerPathId,
            Long skillId,
            Long majorCategoryId,
            Long focusAreaId,
            List<String> questionTypes,
            List<String> difficulties,
            Boolean isPriorityOnly,
            Integer page,
            Integer size,
            Long userId) {

        long startTime = System.currentTimeMillis();
        logger.info("🔍 开始查询试题 - skillId: {}, majorCategoryId: {}, focusAreaId: {}", skillId, majorCategoryId, focusAreaId);

        // 1. 统一转换为 focusAreaIds（减少数据库查询）
        long step1Start = System.currentTimeMillis();
        List<Long> focusAreaIds = resolveFocusAreaIds(careerPathId, skillId, majorCategoryId, focusAreaId);
        long step1Time = System.currentTimeMillis() - step1Start;
        logger.info("  ⏱️ Step1 解析focusAreaIds: {}ms, 结果: {} 个Focus Areas", step1Time, focusAreaIds != null ? focusAreaIds.size() : 0);

        // 2. 转换枚举类型（多选）
        List<Question.QuestionType> typeEnums = null;
        if (questionTypes != null && !questionTypes.isEmpty()) {
            typeEnums = questionTypes.stream()
                    .map(type -> {
                        try {
                            return Question.QuestionType.valueOf(type);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(type -> type != null)
                    .collect(Collectors.toList());
        }

        List<Question.Difficulty> difficultyEnums = null;
        if (difficulties != null && !difficulties.isEmpty()) {
            difficultyEnums = difficulties.stream()
                    .map(diff -> {
                        try {
                            return Question.Difficulty.valueOf(diff);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(diff -> diff != null)
                    .collect(Collectors.toList());
        }

        // 3. 创建分页参数（默认第0页，每页20条）
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            size != null && size > 0 && size <= 100 ? size : 20
        );

        // 4. 单次数据库查询（所有过滤条件在SQL层面完成）
        long step4Start = System.currentTimeMillis();
        Page<Question> questionPage = questionRepository.searchQuestionsOptimized(
            focusAreaIds,
            keyword,
            typeEnums,       // 传递List
            difficultyEnums, // 传递List
            isPriorityOnly,  // 是否只显示重点题
            userId,
            pageable
        );
        long step4Time = System.currentTimeMillis() - step4Start;
        logger.info("  ⏱️ Step4 数据库查询: {}ms, 结果: {} 条试题", step4Time, questionPage.getTotalElements());

        // 5. 批量加载用户笔记状态（只需要isPriority和hasUserNote标志）
        List<Long> questionIds = questionPage.getContent().stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // 只加载用户笔记（不加载AI笔记，节省查询时间）
        Map<Long, UserQuestionNote> userNotesMap = new HashMap<>();

        if (!questionIds.isEmpty() && userId != null) {
            // 只查询用户笔记的isPriority状态
            List<UserQuestionNote> userNotes = noteRepository.findByQuestionIdsAndUserId(questionIds, userId);
            userNotesMap = userNotes.stream()
                    .collect(Collectors.toMap(n -> n.getQuestion().getId(), n -> n));
        }

        // 6. 转换为轻量级ListDTO（不包含description、AI笔记、编程题详情）
        final Map<Long, UserQuestionNote> finalUserNotesMap = userNotesMap;

        List<QuestionListDTO> questionListDTOs = questionPage.getContent().stream()
                .map(question -> convertToListDTO(question, finalUserNotesMap.get(question.getId())))
                .collect(Collectors.toList());

        // 7. 构造分页响应
        Map<String, Object> response = new HashMap<>();
        response.put("content", questionListDTOs);
        response.put("totalElements", questionPage.getTotalElements());
        response.put("totalPages", questionPage.getTotalPages());
        response.put("currentPage", questionPage.getNumber());
        response.put("pageSize", questionPage.getSize());
        response.put("hasNext", questionPage.hasNext());
        response.put("hasPrevious", questionPage.hasPrevious());

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("✅ 查询完成 - 总耗时: {}ms (缓存: {}ms, SQL: {}ms, DTO转换: {}ms)",
            totalTime, step1Time, step4Time, totalTime - step1Time - step4Time);

        return response;
    }

    /**
     * 统一解析focusAreaIds（性能优化：使用缓存，避免数据库查询和多表JOIN）
     *
     * 性能优化原理：
     * 1. 使用HierarchyCacheService在内存中计算focus_area_id列表
     * 2. 避免多表JOIN（career_paths → skills → major_categories → focus_areas）
     * 3. 缓存数据量小（几百条），查询极快（<1ms）
     * 4. 对比旧实现：减少3-4次数据库查询
     */
    private List<Long> resolveFocusAreaIds(Long careerPathId, Long skillId, Long majorCategoryId, Long focusAreaId) {
        // 使用缓存服务统一计算Focus Area IDs
        Set<Long> focusAreaIds = hierarchyCacheService.getFocusAreaIdsByFilters(
                careerPathId, skillId, majorCategoryId, focusAreaId
        );

        if (focusAreaIds.isEmpty()) {
            // 如果没有任何过滤条件，返回null（表示不过滤focusArea）
            if (careerPathId == null && skillId == null && majorCategoryId == null && focusAreaId == null) {
                return null;
            }
            // 有过滤条件但没有结果，返回空列表（会导致查询返回0条）
            return new ArrayList<>();
        }

        return new ArrayList<>(focusAreaIds);
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
     * 获取试题详情（含用户笔记和AI笔记）
     */
    public QuestionDTO getQuestionByIdForUser(Long id, Long userId) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        QuestionDTO dto = convertToDTO(question);

        // 加载用户笔记
        Optional<UserQuestionNote> note = noteRepository.findByQuestionIdAndUserId(id, userId);
        note.ifPresent(n -> dto.setUserNote(convertNoteToDTO(n)));

        // 加载AI笔记（user_id = -1）
        Optional<UserQuestionNote> aiNote = noteRepository.findByQuestionIdAndUserId(id, -1L);
        aiNote.ifPresent(n -> dto.setAiNote(convertNoteToDTO(n)));

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
        question.setQuestionDescription(dto.getQuestionDescription());
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

        // 设置questionType（如果提供了programmingDetails，则设置为programming）
        if (dto.getQuestionType() != null) {
            question.setQuestionType(Question.QuestionType.valueOf(dto.getQuestionType()));
        } else if (dto.getProgrammingDetails() != null) {
            question.setQuestionType(Question.QuestionType.programming);
        }
        // 如果未指定且无programmingDetails，保持默认值technical

        Question savedQuestion = questionRepository.save(question);
        QuestionDTO result = convertToDTO(savedQuestion);

        // 如果提供了编程题详情，创建ProgrammingQuestionDetails
        if (dto.getProgrammingDetails() != null) {
            ProgrammingQuestionDetailsDTO createdDetails =
                    programmingDetailsService.createDetails(savedQuestion.getId(), dto.getProgrammingDetails());
            result.setProgrammingDetails(createdDetails);
        }

        return result;
    }

    /**
     * 更新试题
     */
    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto, Long userId, boolean isAdmin) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        // 权限检查：
        // 1. 管理员可以修改所有试题
        // 2. 普通用户不能修改公共试题（isOfficial = true）
        // 3. 普通用户只能修改自己创建的私有试题
        if (!isAdmin) {
            if (question.getIsOfficial()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改公共试题");
            }
            if (question.getCreatedByUser() == null || !question.getCreatedByUser().getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此试题");
            }
        }

        question.setTitle(dto.getTitle());
        question.setQuestionDescription(dto.getQuestionDescription());
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

        // 更新questionType（如果提供了programmingDetails，则设置为programming）
        if (dto.getQuestionType() != null) {
            question.setQuestionType(Question.QuestionType.valueOf(dto.getQuestionType()));
        } else if (dto.getProgrammingDetails() != null) {
            question.setQuestionType(Question.QuestionType.programming);
        }

        Question savedQuestion = questionRepository.save(question);
        QuestionDTO result = convertToDTO(savedQuestion);

        // 如果提供了编程题详情，更新或创建ProgrammingQuestionDetails
        if (dto.getProgrammingDetails() != null) {
            ProgrammingQuestionDetailsDTO updatedDetails =
                    programmingDetailsService.updateDetails(savedQuestion.getId(), dto.getProgrammingDetails());
            result.setProgrammingDetails(updatedDetails);
        }

        return result;
    }

    /**
     * 删除试题
     */
    @Transactional
    public void deleteQuestion(Long id, Long userId, boolean isAdmin) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "试题不存在"));

        // 权限检查：
        // 1. 管理员可以删除所有试题
        // 2. 普通用户不能删除公共试题（isOfficial = true）
        // 3. 普通用户只能删除自己创建的私有试题
        if (!isAdmin) {
            if (question.getIsOfficial()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除公共试题");
            }
            if (question.getCreatedByUser() == null || !question.getCreatedByUser().getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此试题");
            }
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
        dto.setDifficulty(question.getDifficulty());
        dto.setAnswerRequirement(question.getAnswerRequirement());
        dto.setTargetPosition(question.getTargetPosition());
        dto.setTargetLevel(question.getTargetLevel());
        dto.setRedFlags(question.getRedFlagsList());
        dto.setIsOfficial(question.getIsOfficial());
        dto.setCreatedByUserId(question.getCreatedByUser() != null ? question.getCreatedByUser().getId() : null);
        dto.setDisplayOrder(question.getDisplayOrder());
        dto.setQuestionType(question.getQuestionType() != null ? question.getQuestionType().name() : null);
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

        // 加载Skill的默认答题模版（如果存在）
        if (question.getFocusArea() != null && question.getFocusArea().getSkill() != null) {
            Long skillId = question.getFocusArea().getSkill().getId();
            var skillTemplateDTO = skillTemplateService.getDefaultTemplate(skillId);

            if (skillTemplateDTO != null && skillTemplateDTO.getTemplateId() != null) {
                try {
                    AnswerTemplateDTO template = answerTemplateService.getTemplate(skillTemplateDTO.getTemplateId());
                    dto.setAnswerTemplate(template);
                } catch (Exception e) {
                    // 模版不存在或加载失败，忽略
                }
            }
        }

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

    /**
     * 转换为轻量级列表DTO（性能优化）
     *
     * 不包含:
     * - questionDescription (节省带宽)
     * - 用户笔记内容 (只包含isPriority和hasUserNote标志)
     * - AI笔记
     * - 编程题详情
     */
    private QuestionListDTO convertToListDTO(Question question, UserQuestionNote userNote) {
        QuestionListDTO dto = new QuestionListDTO();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setQuestionType(question.getQuestionType() != null ? question.getQuestionType().name() : null);
        dto.setDifficulty(question.getDifficulty() != null ? question.getDifficulty().name() : null);

        // 只设置笔记状态标志
        if (userNote != null) {
            dto.setHasUserNote(true);
            dto.setIsPriority(userNote.getIsPriority() != null && userNote.getIsPriority().equals(1));
        } else {
            dto.setHasUserNote(false);
            dto.setIsPriority(false);
        }

        // 关联信息
        if (question.getFocusArea() != null) {
            dto.setFocusAreaId(question.getFocusArea().getId());
            dto.setFocusAreaName(question.getFocusArea().getName());
        }

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

        QuestionDTO dto = convertToDTOWithDetails(question);

        // 加载AI笔记（user_id = -1）
        Optional<UserQuestionNote> aiNote = noteRepository.findByQuestionIdAndUserId(questionId, -1L);
        aiNote.ifPresent(n -> dto.setAiNote(convertNoteToDTO(n)));

        return dto;
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

        // 加载AI笔记（user_id = -1）
        Optional<UserQuestionNote> aiNote = noteRepository.findByQuestionIdAndUserId(questionId, -1L);
        aiNote.ifPresent(n -> dto.setAiNote(convertNoteToDTO(n)));

        return dto;
    }

    /**
     * 获取用户的学习总结（按大分类和Focus Area分组）
     * 显示"算法与数据结构"skill下的所有编程题，有笔记的显示核心思路
     */
    public Map<String, Object> getLearningReview(Long userId) {
        // 常量：算法与数据结构skill ID
        final Long ALGORITHM_SKILL_ID = 1L;

        // 1. 一次性批量查询所有编程题（带Focus Area和Programming Details）
        List<Question> allProgrammingQuestions = questionRepository.findAllProgrammingQuestionsBySkillId(ALGORITHM_SKILL_ID);

        // 2. 一次性获取用户的所有笔记，建立 questionId -> note 的映射
        List<UserQuestionNote> userNotes = noteRepository.findAllByUserIdWithQuestionAndFocusArea(userId);
        Map<Long, UserQuestionNote> noteMap = userNotes.stream()
                .collect(Collectors.toMap(
                        note -> note.getQuestion().getId(),
                        note -> note
                ));

        // 3. 批量加载所有编程题详情，建立 questionId -> details 的映射
        List<Long> questionIds = allProgrammingQuestions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());
        Map<Long, ProgrammingQuestionDetailsDTO> detailsMap =
                programmingDetailsService.getDetailsByQuestionIds(questionIds);

        // 4. 收集所有 Focus Area IDs，批量查询它们对应的大分类
        Set<Long> focusAreaIds = allProgrammingQuestions.stream()
                .map(q -> q.getFocusArea().getId())
                .collect(Collectors.toSet());

        // 4. 批量查询所有 Focus Area 对应的大分类关系
        Map<Long, List<Long>> focusAreaToCategoryMap = new HashMap<>();
        Map<Long, MajorCategory> majorCategoryMap = new HashMap<>();

        for (Long focusAreaId : focusAreaIds) {
            var facList = focusAreaCategoryRepository.findByFocusAreaId(focusAreaId);
            List<Long> categoryIds = new ArrayList<>();
            for (var fac : facList) {
                categoryIds.add(fac.getCategoryId());
                if (!majorCategoryMap.containsKey(fac.getCategoryId())) {
                    majorCategoryRepository.findById(fac.getCategoryId())
                            .ifPresent(mc -> majorCategoryMap.put(mc.getId(), mc));
                }
            }
            focusAreaToCategoryMap.put(focusAreaId, categoryIds);
        }

        // 5. 结构: Map<大分类名, Map<FocusAreaId, List<QuestionSummary>>>
        Map<String, Map<Long, List<Map<String, Object>>>> categoryGroups = new LinkedHashMap<>();
        Map<String, Long> categoryIdMap = new HashMap<>();

        // 6. 遍历所有编程题，组装数据
        for (Question question : allProgrammingQuestions) {
            FocusArea focusArea = question.getFocusArea();
            UserQuestionNote note = noteMap.get(question.getId());

            // 获取编程题详情（从批量加载的map中获取）
            ProgrammingQuestionDetailsDTO programmingDetails = detailsMap.get(question.getId());
            if (programmingDetails == null) {
                continue;
            }

            // 获取该 Focus Area 对应的所有大分类
            List<Long> categoryIds = focusAreaToCategoryMap.get(focusArea.getId());

            if (categoryIds == null || categoryIds.isEmpty()) {
                // 没有关联的大分类，放入"未分类"
                processQuestionForReview(categoryGroups, categoryIdMap, "未分类", null,
                        focusArea, question, note, programmingDetails);
            } else {
                // 一个Focus Area可能属于多个大分类，将试题添加到所有相关分类中
                for (Long categoryId : categoryIds) {
                    MajorCategory mc = majorCategoryMap.get(categoryId);
                    if (mc != null) {
                        processQuestionForReview(categoryGroups, categoryIdMap,
                                mc.getName(), mc.getId(), focusArea, question, note, programmingDetails);
                    }
                }
            }
        }

        // 对每个分类内的Focus Area按Focus Area name排序，对每个Focus Area内的试题按leetcode_number排序
        Map<String, Object> categoriesResult = new LinkedHashMap<>();
        categoryGroups.forEach((categoryName, focusAreaGroups) -> {
            List<Map<String, Object>> focusAreaList = new ArrayList<>();

            focusAreaGroups.forEach((focusAreaId, questions) -> {
                // 排序逻辑：先按isImportant降序（重要的在前），再按leetcodeNumber升序
                questions.sort(Comparator
                        .comparing((Map<String, Object> q) -> {
                            Boolean isImportant = (Boolean) q.get("isImportant");
                            return isImportant != null && isImportant ? 0 : 1; // 0在前（重要的）
                        })
                        .thenComparing(q ->
                                q.get("leetcodeNumber") != null ? (Integer) q.get("leetcodeNumber") : Integer.MAX_VALUE
                        ));

                Map<String, Object> focusAreaGroup = new LinkedHashMap<>();
                focusAreaGroup.put("focusAreaId", focusAreaId);
                focusAreaGroup.put("focusAreaName", questions.get(0).get("focusAreaName"));
                focusAreaGroup.put("questions", questions);
                focusAreaList.add(focusAreaGroup);
            });

            // 按Focus Area名称排序
            focusAreaList.sort(Comparator.comparing(fa -> (String) fa.get("focusAreaName")));

            Map<String, Object> categoryData = new LinkedHashMap<>();
            categoryData.put("categoryId", categoryIdMap.get(categoryName));
            categoryData.put("focusAreas", focusAreaList);
            categoriesResult.put(categoryName, categoryData);
        });

        // 统计所有编程题的数量
        int totalQuestions = categoryGroups.values().stream()
                .mapToInt(focusAreaGroups -> focusAreaGroups.values().stream()
                        .mapToInt(List::size)
                        .sum())
                .sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("categories", categoriesResult);
        result.put("totalQuestions", totalQuestions);
        return result;
    }

    /**
     * 处理单个试题，添加到对应的分类和Focus Area分组
     * @param note 可能为null（用户没有笔记）
     */
    private void processQuestionForReview(
            Map<String, Map<Long, List<Map<String, Object>>>> categoryGroups,
            Map<String, Long> categoryIdMap,
            String categoryName,
            Long categoryId,
            FocusArea focusArea,
            Question question,
            UserQuestionNote note,
            ProgrammingQuestionDetailsDTO programmingDetails) {

        // 初始化分类
        if (!categoryGroups.containsKey(categoryName)) {
            categoryGroups.put(categoryName, new LinkedHashMap<>());
            categoryIdMap.put(categoryName, categoryId);
        }

        Map<Long, List<Map<String, Object>>> focusAreaGroups = categoryGroups.get(categoryName);

        // 初始化Focus Area
        if (!focusAreaGroups.containsKey(focusArea.getId())) {
            focusAreaGroups.put(focusArea.getId(), new ArrayList<>());
        }

        Map<String, Object> questionSummary = new LinkedHashMap<>();
        questionSummary.put("questionId", question.getId());
        questionSummary.put("title", question.getTitle());
        questionSummary.put("focusAreaId", focusArea.getId());
        questionSummary.put("focusAreaName", focusArea.getName());
        questionSummary.put("difficulty", question.getDifficulty());

        // 核心思路：如果用户有笔记就显示，否则为null
        questionSummary.put("coreStrategy", note != null ? note.getCoreStrategy() : null);

        // 添加编程题专属信息
        questionSummary.put("leetcodeNumber", programmingDetails.getLeetcodeNumber());
        questionSummary.put("leetcodeUrl", programmingDetails.getLeetcodeUrl());
        questionSummary.put("isImportant", programmingDetails.getIsImportant());

        focusAreaGroups.get(focusArea.getId()).add(questionSummary);
    }
}
