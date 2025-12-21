package com.growing.app.repository;

import com.growing.app.model.FocusArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FocusAreaRepository extends JpaRepository<FocusArea, Long> {

    List<FocusArea> findBySkillIdOrderByDisplayOrderAsc(Long skillId);

    Long countBySkillId(Long skillId);

    void deleteBySkillId(Long skillId);
}
