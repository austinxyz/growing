package com.growing.app.repository;

import com.growing.app.model.FocusArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FocusAreaRepository extends JpaRepository<FocusArea, Long> {

    List<FocusArea> findBySkillIdOrderByDisplayOrderAsc(Long skillId);

    Long countBySkillId(Long skillId);

    void deleteBySkillId(Long skillId);

    @Query(value = "SELECT fa.* FROM focus_areas fa " +
                   "JOIN focus_area_categories fac ON fa.id = fac.focus_area_id " +
                   "WHERE fac.category_id = :categoryId", nativeQuery = true)
    List<FocusArea> findByCategoryId(@Param("categoryId") Long categoryId);
}
