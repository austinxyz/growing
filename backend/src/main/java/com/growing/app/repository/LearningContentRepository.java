package com.growing.app.repository;

import com.growing.app.model.LearningContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学习内容Repository
 */
@Repository
public interface LearningContentRepository extends JpaRepository<LearningContent, Long> {

    /**
     * 查询Focus Area + Stage的内容
     */
    List<LearningContent> findByFocusAreaIdAndStageIdOrderBySortOrder(Long focusAreaId, Long stageId);

    /**
     * 查询算法模版（focus_area_id = NULL）
     */
    List<LearningContent> findByContentTypeAndFocusAreaIdIsNullOrderBySortOrder(LearningContent.ContentType contentType);

    /**
     * 搜索模版
     */
    List<LearningContent> findByContentTypeAndFocusAreaIdIsNullAndTitleContainingOrderBySortOrder(
            LearningContent.ContentType contentType, String keyword);

    /**
     * 按阶段查询
     */
    List<LearningContent> findByStageIdOrderBySortOrder(Long stageId);

    /**
     * 按阶段分页查询
     */
    Page<LearningContent> findByStageIdOrderBySortOrder(Long stageId, Pageable pageable);

    /**
     * 按Focus Area + Stage分页查询
     */
    Page<LearningContent> findByFocusAreaIdAndStageIdOrderBySortOrder(
            Long focusAreaId, Long stageId, Pageable pageable);

    /**
     * 统计
     */
    long countByFocusAreaIdAndStageId(Long focusAreaId, Long stageId);

    /**
     * 查询Focus Area的所有学习内容（所有阶段）
     */
    List<LearningContent> findByFocusAreaIdOrderByStageIdAscSortOrderAsc(Long focusAreaId);

    /**
     * 管理员分页查询（支持多条件过滤）
     */
    Page<LearningContent> findByFocusAreaIdOrderBySortOrderAsc(Long focusAreaId, Pageable pageable);
    Page<LearningContent> findByStageIdOrderBySortOrderAsc(Long stageId, Pageable pageable);
    Page<LearningContent> findByContentTypeOrderBySortOrderAsc(LearningContent.ContentType contentType, Pageable pageable);
    Page<LearningContent> findByFocusAreaIdAndStageIdAndContentTypeOrderBySortOrderAsc(
            Long focusAreaId, Long stageId, LearningContent.ContentType contentType, Pageable pageable);
    Page<LearningContent> findByFocusAreaIdAndContentTypeOrderBySortOrderAsc(
            Long focusAreaId, LearningContent.ContentType contentType, Pageable pageable);
    Page<LearningContent> findByStageIdAndContentTypeOrderBySortOrderAsc(
            Long stageId, LearningContent.ContentType contentType, Pageable pageable);

    /**
     * 查询所有算法模版（focus_area IS NULL）
     */
    List<LearningContent> findByContentTypeAndFocusAreaIsNullOrderBySortOrderAsc(LearningContent.ContentType contentType);
}
