package com.growing.app.repository;

import com.growing.app.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT s FROM Skill s JOIN s.careerPathSkills cps WHERE cps.careerPath.id = :careerPathId ORDER BY s.displayOrder ASC")
    List<Skill> findByCareerPathId(Long careerPathId);

    List<Skill> findByIsImportantTrueOrderByDisplayOrderAsc();

    /**
     * 获取未关联到任何职业路径的技能
     */
    @Query("SELECT s FROM Skill s WHERE s.id NOT IN (SELECT DISTINCT cps.skill.id FROM CareerPathSkill cps) ORDER BY s.displayOrder ASC")
    List<Skill> findUnassociatedSkills();
}
