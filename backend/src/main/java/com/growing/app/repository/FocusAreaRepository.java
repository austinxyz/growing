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

    /**
     * 查询所有Focus Area并预加载Skill关联（避免N+1查询）
     * 用于缓存预热，一次性加载所有数据
     */
    @Query("SELECT fa FROM FocusArea fa JOIN FETCH fa.skill")
    List<FocusArea> findAllWithSkill();

    Long countBySkillId(Long skillId);

    void deleteBySkillId(Long skillId);

    @Query(value = "SELECT fa.* FROM focus_areas fa " +
                   "JOIN focus_area_categories fac ON fa.id = fac.focus_area_id " +
                   "WHERE fac.category_id = :categoryId", nativeQuery = true)
    List<FocusArea> findByCategoryId(@Param("categoryId") Long categoryId);

    // 查询未分类的 Focus Area（没有关联到任何大分类）
    @Query(value = "SELECT fa.* FROM focus_areas fa " +
                   "WHERE fa.skill_id = :skillId " +
                   "AND NOT EXISTS (SELECT 1 FROM focus_area_categories fac WHERE fac.focus_area_id = fa.id) " +
                   "ORDER BY fa.display_order ASC", nativeQuery = true)
    List<FocusArea> findUncategorizedBySkillId(@Param("skillId") Long skillId);

    // 根据大分类ID查询Focus Area
    @Query(value = "SELECT fa.* FROM focus_areas fa " +
                   "JOIN focus_area_categories fac ON fa.id = fac.focus_area_id " +
                   "WHERE fac.category_id = :majorCategoryId " +
                   "ORDER BY fa.display_order ASC", nativeQuery = true)
    List<FocusArea> findByMajorCategoryId(@Param("majorCategoryId") Long majorCategoryId);

    // 批量根据大分类ID查询Focus Area
    @Query(value = "SELECT DISTINCT fa.* FROM focus_areas fa " +
                   "JOIN focus_area_categories fac ON fa.id = fac.focus_area_id " +
                   "WHERE fac.category_id IN :categoryIds " +
                   "ORDER BY fa.display_order ASC", nativeQuery = true)
    List<FocusArea> findByMajorCategoryIdIn(@Param("categoryIds") List<Long> categoryIds);
}
