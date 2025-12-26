package com.growing.app.service;

import com.growing.app.dto.*;
import com.growing.app.model.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningContentService {

    @Autowired
    private LearningContentRepository learningContentRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private LearningStageRepository learningStageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    @Autowired
    private FocusAreaCategoryRepository focusAreaCategoryRepository;

    @Autowired
    private UserLearningContentNoteRepository userLearningContentNoteRepository;

    @Autowired
    private UserLearningContentKnowledgePointRepository knowledgePointRepository;

    // ==================== 用户端API ====================

    /**
     * 获取Focus Area的完整学习内容（按阶段分组）
     * API: GET /api/learning-contents?focusAreaId=X
     */
    public FocusAreaLearningDTO getContentsByFocusArea(Long focusAreaId) {
        FocusArea focusArea = focusAreaRepository.findById(focusAreaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Focus Area不存在"));

        FocusAreaLearningDTO result = new FocusAreaLearningDTO();

        // 设置Focus Area信息
        FocusAreaLearningDTO.FocusAreaInfo focusAreaInfo = new FocusAreaLearningDTO.FocusAreaInfo();
        focusAreaInfo.setId(focusArea.getId());
        focusAreaInfo.setName(focusArea.getName());

        // 获取大分类名称
        List<FocusAreaCategory> categories = focusAreaCategoryRepository.findByFocusAreaId(focusAreaId);
        if (!categories.isEmpty() && categories.get(0).getCategory() != null) {
            MajorCategory majorCategory = categories.get(0).getCategory();
            focusAreaInfo.setCategoryName(majorCategory.getName());
        }

        result.setFocusArea(focusAreaInfo);

        // 获取所有学习阶段
        Long skillId = focusArea.getSkill().getId();
        List<LearningStage> stages = learningStageRepository.findBySkillIdOrderBySortOrderAsc(skillId);

        // 获取所有学习内容
        List<LearningContent> allContents = learningContentRepository.findByFocusAreaIdOrderByStageIdAscSortOrderAsc(focusAreaId);

        // 按阶段分组
        Map<Long, List<LearningContent>> contentsByStage = allContents.stream()
                .collect(Collectors.groupingBy(c -> c.getStage().getId(), LinkedHashMap::new, Collectors.toList()));

        // 构建StageContentDTO列表
        List<StageContentDTO> stageContents = stages.stream()
                .map(stage -> {
                    StageContentDTO dto = new StageContentDTO();
                    dto.setId(stage.getId());
                    dto.setStageName(stage.getStageName());
                    dto.setStageType(stage.getStageType());
                    dto.setDescription(stage.getDescription());
                    dto.setSortOrder(stage.getSortOrder());

                    // 填充该阶段的学习内容
                    List<LearningContent> stageContentList = contentsByStage.getOrDefault(stage.getId(), new ArrayList<>());
                    dto.setContents(stageContentList.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));

                    return dto;
                })
                .collect(Collectors.toList());

        result.setStages(stageContents);
        return result;
    }

    /**
     * 获取单个学习内容详情
     * API: GET /api/learning-contents/{id}
     */
    public LearningContentDTO getContentById(Long contentId) {
        LearningContent content = learningContentRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习内容不存在"));

        return convertToDTO(content);
    }

    /**
     * 获取算法模版列表（focus_area_id = NULL）
     * API: GET /api/algorithm-templates
     */
    public List<LearningContentDTO> getAlgorithmTemplates(String search) {
        List<LearningContent> templates;

        if (search != null && !search.trim().isEmpty()) {
            templates = learningContentRepository.findByContentTypeAndFocusAreaIdIsNullAndTitleContainingOrderBySortOrder(
                    LearningContent.ContentType.template, search.trim());
        } else {
            templates = learningContentRepository.findByContentTypeAndFocusAreaIdIsNullOrderBySortOrder(
                    LearningContent.ContentType.template);
        }

        return templates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取算法模版列表（分页版本）
     * API: GET /api/learning-contents/algorithm-templates
     */
    public Page<LearningContentDTO> getAlgorithmTemplates(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LearningContent> templates;

        if (search != null && !search.trim().isEmpty()) {
            templates = learningContentRepository.findByContentTypeAndFocusAreaIdIsNullAndTitleContaining(
                    LearningContent.ContentType.template, search.trim(), pageable);
        } else {
            templates = learningContentRepository.findByContentTypeAndFocusAreaIdIsNull(
                    LearningContent.ContentType.template, pageable);
        }

        return templates.map(this::convertToDTO);
    }

    /**
     * 获取单个算法模版详情
     * API: GET /api/algorithm-templates/{id}
     */
    public LearningContentDTO getAlgorithmTemplateById(Long templateId) {
        LearningContent template = learningContentRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "算法模版不存在"));

        if (template.getContentType() != LearningContent.ContentType.template || template.getFocusArea() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该内容不是算法模版");
        }

        return convertToDTO(template);
    }

    // ==================== 管理员API ====================

    /**
     * 分页查询学习内容（支持多条件过滤）
     * API: GET /api/admin/learning-contents
     */
    public Page<LearningContentDTO> getContentsForAdmin(Long focusAreaId, Long stageId,
                                                         LearningContent.ContentType contentType,
                                                         int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LearningContent> contentPage;

        // 根据条件组合查询
        if (focusAreaId != null && stageId != null && contentType != null) {
            contentPage = learningContentRepository.findByFocusAreaIdAndStageIdAndContentTypeOrderBySortOrderAsc(
                    focusAreaId, stageId, contentType, pageable);
        } else if (focusAreaId != null && contentType != null) {
            contentPage = learningContentRepository.findByFocusAreaIdAndContentTypeOrderBySortOrderAsc(
                    focusAreaId, contentType, pageable);
        } else if (stageId != null && contentType != null) {
            contentPage = learningContentRepository.findByStageIdAndContentTypeOrderBySortOrderAsc(
                    stageId, contentType, pageable);
        } else if (focusAreaId != null) {
            contentPage = learningContentRepository.findByFocusAreaIdOrderBySortOrderAsc(focusAreaId, pageable);
        } else if (stageId != null) {
            contentPage = learningContentRepository.findByStageIdOrderBySortOrderAsc(stageId, pageable);
        } else if (contentType != null) {
            contentPage = learningContentRepository.findByContentTypeOrderBySortOrderAsc(contentType, pageable);
        } else {
            contentPage = learningContentRepository.findAll(pageable);
        }

        return contentPage.map(this::convertToDTO);
    }

    /**
     * 创建学习内容（管理员）
     * API: POST /api/admin/learning-contents
     */
    @Transactional
    public LearningContentDTO createContent(LearningContentDTO dto, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        LearningContent content = new LearningContent();

        // 设置Stage（算法模版时为null）
        if (dto.getStageId() != null) {
            LearningStage stage = learningStageRepository.findById(dto.getStageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习阶段不存在"));
            content.setStage(stage);
        }

        content.setContentType(dto.getContentType());
        content.setTitle(dto.getTitle());
        content.setDescription(dto.getDescription());
        content.setUrl(dto.getUrl());
        content.setAuthor(dto.getAuthor());
        content.setContentData(dto.getContentData());
        content.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        content.setVisibility(dto.getVisibility() != null ? dto.getVisibility() : LearningContent.Visibility.PUBLIC);
        content.setCreatedBy(admin);

        // 设置Focus Area（算法模版时为null）
        if (dto.getFocusAreaId() != null) {
            FocusArea focusArea = focusAreaRepository.findById(dto.getFocusAreaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Focus Area不存在"));
            content.setFocusArea(focusArea);
        }

        content = learningContentRepository.save(content);
        return convertToDTO(content);
    }

    /**
     * 更新学习内容（管理员）
     * API: PUT /api/admin/learning-contents/{id}
     */
    @Transactional
    public LearningContentDTO updateContent(Long contentId, LearningContentDTO dto) {
        LearningContent content = learningContentRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习内容不存在"));

        // 更新Stage（如果指定）
        if (dto.getStageId() != null && !dto.getStageId().equals(content.getStage().getId())) {
            LearningStage stage = learningStageRepository.findById(dto.getStageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习阶段不存在"));
            content.setStage(stage);
        }

        // 更新Focus Area（如果指定）
        if (dto.getFocusAreaId() != null) {
            if (content.getFocusArea() == null || !dto.getFocusAreaId().equals(content.getFocusArea().getId())) {
                FocusArea focusArea = focusAreaRepository.findById(dto.getFocusAreaId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Focus Area不存在"));
                content.setFocusArea(focusArea);
            }
        } else {
            content.setFocusArea(null); // 设置为算法模版
        }

        content.setContentType(dto.getContentType());
        content.setTitle(dto.getTitle());
        content.setDescription(dto.getDescription());
        content.setUrl(dto.getUrl());
        content.setAuthor(dto.getAuthor());
        content.setContentData(dto.getContentData());
        content.setSortOrder(dto.getSortOrder());
        content.setVisibility(dto.getVisibility());

        content = learningContentRepository.save(content);
        return convertToDTO(content);
    }

    /**
     * 删除学习内容（管理员）
     * API: DELETE /api/admin/learning-contents/{id}
     */
    @Transactional
    public void deleteContent(Long contentId) {
        if (!learningContentRepository.existsById(contentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "学习内容不存在");
        }

        learningContentRepository.deleteById(contentId);
    }

    /**
     * 批量调整排序（管理员）
     * API: PUT /api/admin/learning-contents/reorder
     */
    @Transactional
    public void reorderContents(List<Long> contentIds) {
        for (int i = 0; i < contentIds.size(); i++) {
            Long contentId = contentIds.get(i);
            LearningContent content = learningContentRepository.findById(contentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习内容不存在: " + contentId));
            content.setSortOrder(i);
            learningContentRepository.save(content);
        }
    }

    // ==================== 算法模版管理 ====================

    /**
     * 获取所有算法模版（管理员）
     * API: GET /api/admin/learning-contents/templates
     */
    public List<LearningContentDTO> getTemplates() {
        // 查询 content_type='template' 且 focus_area_id IS NULL 的记录
        List<LearningContent> templates = learningContentRepository
                .findByContentTypeAndFocusAreaIsNullOrderBySortOrderAsc(LearningContent.ContentType.template);

        return templates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==================== DTO转换 ====================

    private LearningContentDTO convertToDTO(LearningContent content) {
        LearningContentDTO dto = new LearningContentDTO();
        dto.setId(content.getId());

        // 设置Stage信息（算法模版可能为null）
        if (content.getStage() != null) {
            dto.setStageId(content.getStage().getId());
            dto.setStageName(content.getStage().getStageName());
        }

        dto.setContentType(content.getContentType());
        dto.setTitle(content.getTitle());
        dto.setDescription(content.getDescription());
        dto.setUrl(content.getUrl());
        dto.setAuthor(content.getAuthor());
        dto.setContentData(content.getContentData());
        dto.setSortOrder(content.getSortOrder());
        dto.setVisibility(content.getVisibility());
        dto.setCreatedAt(content.getCreatedAt());
        dto.setUpdatedAt(content.getUpdatedAt());

        // 设置Focus Area信息（如果存在）
        if (content.getFocusArea() != null) {
            dto.setFocusAreaId(content.getFocusArea().getId());
            dto.setFocusAreaName(content.getFocusArea().getName());
        }

        // 设置创建者信息
        if (content.getCreatedBy() != null) {
            dto.setCreatedByUserId(content.getCreatedBy().getId());
            dto.setCreatedByUsername(content.getCreatedBy().getUsername());
        }

        // 设置网站信息（如果存在）
        if (content.getWebsite() != null) {
            dto.setWebsiteId(content.getWebsite().getId());
            dto.setWebsiteName(content.getWebsite().getName());
            dto.setWebsiteDisplayName(content.getWebsite().getDisplayName());
            dto.setSupportsIframe(content.getWebsite().getSupportsIframe());
        }

        return dto;
    }

    // ==================== 学习笔记相关方法 ====================

    /**
     * 获取用户对某个学习资料的笔记
     */
    public UserLearningContentNoteDTO getNote(Long contentId, Long userId) {
        return userLearningContentNoteRepository.findByLearningContentIdAndUserId(contentId, userId)
                .map(this::convertToNoteDTO)
                .orElse(null);
    }

    /**
     * 保存或更新笔记（UPSERT逻辑）
     */
    @Transactional
    public UserLearningContentNoteDTO saveOrUpdateNote(Long contentId, Long userId, String noteContent) {
        // 验证学习资料存在
        LearningContent content = learningContentRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习资料不存在"));

        // 验证用户存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 查找是否已有笔记
        UserLearningContentNote note = userLearningContentNoteRepository
                .findByLearningContentIdAndUserId(contentId, userId)
                .orElse(new UserLearningContentNote());

        // 更新笔记内容
        note.setLearningContent(content);
        note.setUser(user);
        note.setNoteContent(noteContent);

        UserLearningContentNote saved = userLearningContentNoteRepository.save(note);
        return convertToNoteDTO(saved);
    }

    /**
     * 删除笔记
     */
    @Transactional
    public void deleteNote(Long contentId, Long userId) {
        userLearningContentNoteRepository.deleteByLearningContentIdAndUserId(contentId, userId);
    }

    /**
     * 转换为DTO
     */
    private UserLearningContentNoteDTO convertToNoteDTO(UserLearningContentNote note) {
        UserLearningContentNoteDTO dto = new UserLearningContentNoteDTO();
        dto.setId(note.getId());
        dto.setLearningContentId(note.getLearningContent().getId());
        dto.setUserId(note.getUser().getId());
        dto.setNoteContent(note.getNoteContent());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }

    // ==================== 知识点相关方法 ====================

    /**
     * 获取用户对某个学习资料的所有知识点
     */
    public List<UserLearningContentKnowledgePointDTO> getKnowledgePoints(Long contentId, Long userId) {
        List<UserLearningContentKnowledgePoint> points = knowledgePointRepository
                .findByLearningContentIdAndUserIdOrderByDisplayOrderAsc(contentId, userId);
        return points.stream()
                .map(this::convertToKnowledgePointDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建知识点
     */
    @Transactional
    public UserLearningContentKnowledgePointDTO createKnowledgePoint(
            Long contentId, Long userId, String title, String summary) {

        // 验证学习资料存在
        LearningContent content = learningContentRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习资料不存在"));

        // 验证用户存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        // 获取当前最大的displayOrder
        List<UserLearningContentKnowledgePoint> existingPoints =
                knowledgePointRepository.findByLearningContentIdAndUserIdOrderByDisplayOrderAsc(contentId, userId);
        int maxOrder = existingPoints.isEmpty() ? -1 :
                existingPoints.stream()
                        .mapToInt(UserLearningContentKnowledgePoint::getDisplayOrder)
                        .max()
                        .orElse(-1);

        UserLearningContentKnowledgePoint point = new UserLearningContentKnowledgePoint();
        point.setLearningContent(content);
        point.setUser(user);
        point.setTitle(title);
        point.setSummary(summary);
        point.setDisplayOrder(maxOrder + 1);

        UserLearningContentKnowledgePoint saved = knowledgePointRepository.save(point);
        return convertToKnowledgePointDTO(saved);
    }

    /**
     * 更新知识点
     */
    @Transactional
    public UserLearningContentKnowledgePointDTO updateKnowledgePoint(
            Long pointId, Long userId, String title, String summary) {

        UserLearningContentKnowledgePoint point = knowledgePointRepository.findById(pointId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "知识点不存在"));

        // 验证所有权
        if (!point.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此知识点");
        }

        point.setTitle(title);
        point.setSummary(summary);

        UserLearningContentKnowledgePoint saved = knowledgePointRepository.save(point);
        return convertToKnowledgePointDTO(saved);
    }

    /**
     * 删除知识点
     */
    @Transactional
    public void deleteKnowledgePoint(Long pointId, Long userId) {
        UserLearningContentKnowledgePoint point = knowledgePointRepository.findById(pointId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "知识点不存在"));

        // 验证所有权
        if (!point.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此知识点");
        }

        knowledgePointRepository.delete(point);
    }

    /**
     * 重新排序知识点
     */
    @Transactional
    public List<UserLearningContentKnowledgePointDTO> reorderKnowledgePoints(
            Long contentId, Long userId, List<Long> pointIds) {

        // 验证所有知识点都属于该用户和该学习资料
        List<UserLearningContentKnowledgePoint> points = knowledgePointRepository
                .findByLearningContentIdAndUserIdOrderByDisplayOrderAsc(contentId, userId);

        Map<Long, UserLearningContentKnowledgePoint> pointMap = points.stream()
                .collect(Collectors.toMap(UserLearningContentKnowledgePoint::getId, p -> p));

        // 更新displayOrder
        for (int i = 0; i < pointIds.size(); i++) {
            Long pointId = pointIds.get(i);
            UserLearningContentKnowledgePoint point = pointMap.get(pointId);
            if (point != null) {
                point.setDisplayOrder(i);
                knowledgePointRepository.save(point);
            }
        }

        // 返回更新后的列表
        return getKnowledgePoints(contentId, userId);
    }

    /**
     * 转换为DTO
     */
    private UserLearningContentKnowledgePointDTO convertToKnowledgePointDTO(UserLearningContentKnowledgePoint point) {
        UserLearningContentKnowledgePointDTO dto = new UserLearningContentKnowledgePointDTO();
        dto.setId(point.getId());
        dto.setLearningContentId(point.getLearningContent().getId());
        dto.setUserId(point.getUser().getId());
        dto.setTitle(point.getTitle());
        dto.setSummary(point.getSummary());
        dto.setDisplayOrder(point.getDisplayOrder());
        dto.setCreatedAt(point.getCreatedAt());
        dto.setUpdatedAt(point.getUpdatedAt());
        return dto;
    }
}
