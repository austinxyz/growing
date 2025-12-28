package com.growing.app.repository;

import com.growing.app.entity.SkillTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 技能-模版关联Repository
 * Phase 6: 通用技能学习模块
 */
@Repository
public interface SkillTemplateRepository extends JpaRepository<SkillTemplate, SkillTemplate.SkillTemplateId> {

    /**
     * 查找技能的所有模版
     */
    List<SkillTemplate> findBySkillId(Long skillId);

    /**
     * 查找模版的所有技能（反向查询）
     */
    List<SkillTemplate> findByTemplateId(Long templateId);

    /**
     * 查找技能的默认模版
     */
    Optional<SkillTemplate> findBySkillIdAndIsDefaultTrue(Long skillId);

    /**
     * 删除技能的所有模版关联
     */
    void deleteBySkillId(Long skillId);

    /**
     * 删除模版的所有技能关联
     */
    void deleteByTemplateId(Long templateId);

    /**
     * 检查技能和模版是否已关联
     */
    boolean existsBySkillIdAndTemplateId(Long skillId, Long templateId);

    /**
     * 清除技能的所有默认模版标记
     */
    @Modifying
    @Query("UPDATE SkillTemplate st SET st.isDefault = false WHERE st.skillId = :skillId")
    void clearDefaultFlagBySkillId(@Param("skillId") Long skillId);
}
