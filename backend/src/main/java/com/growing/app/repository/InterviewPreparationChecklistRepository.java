package com.growing.app.repository;

import com.growing.app.entity.InterviewPreparationChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试准备清单Repository
 * Phase 7: 求职管理模块 - Enhancements
 */
@Repository
public interface InterviewPreparationChecklistRepository extends JpaRepository<InterviewPreparationChecklist, Long> {

    /**
     * 根据面试阶段ID查找所有准备清单项
     * @param interviewStageId 面试阶段ID
     * @return 准备清单项列表，按sort_order排序
     */
    List<InterviewPreparationChecklist> findByInterviewStageIdOrderBySortOrderAsc(Long interviewStageId);

    /**
     * 根据面试阶段ID和优先级查找准备清单项
     * @param interviewStageId 面试阶段ID
     * @param isPriority 是否为重点项
     * @return 准备清单项列表，按sort_order排序
     */
    List<InterviewPreparationChecklist> findByInterviewStageIdAndIsPriorityOrderBySortOrderAsc(Long interviewStageId, Boolean isPriority);

    /**
     * 根据面试阶段ID删除所有准备清单项
     * @param interviewStageId 面试阶段ID
     */
    void deleteByInterviewStageId(Long interviewStageId);
}
