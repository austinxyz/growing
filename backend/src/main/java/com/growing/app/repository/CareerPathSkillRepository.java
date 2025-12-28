package com.growing.app.repository;

import com.growing.app.model.CareerPathSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerPathSkillRepository extends JpaRepository<CareerPathSkill, Long> {

    List<CareerPathSkill> findByCareerPathId(Long careerPathId);

    List<CareerPathSkill> findBySkillId(Long skillId);

    void deleteByCareerPathIdAndSkillId(Long careerPathId, Long skillId);

    void deleteByCareerPathId(Long careerPathId);

    void deleteBySkillId(Long skillId);

    boolean existsByCareerPathIdAndSkillId(Long careerPathId, Long skillId);
}
