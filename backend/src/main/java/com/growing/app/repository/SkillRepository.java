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
}
