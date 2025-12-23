package com.growing.app.repository;

import com.growing.app.model.LearningStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学习阶段Repository
 */
@Repository
public interface LearningStageRepository extends JpaRepository<LearningStage, Long> {

    /**
     * 查询Skill的所有学习阶段（按sort_order排序）
     */
    List<LearningStage> findBySkillIdOrderBySortOrderAsc(Long skillId);

    /**
     * 查询特定类型的学习阶段
     */
    Optional<LearningStage> findBySkillIdAndStageType(Long skillId, String stageType);

    /**
     * 检查Skill下是否存在同名阶段
     */
    boolean existsBySkillIdAndStageName(Long skillId, String stageName);

    /**
     * 检查Skill下是否存在同名阶段（排除指定ID）
     */
    boolean existsBySkillIdAndStageNameAndIdNot(Long skillId, String stageName, Long id);
}
