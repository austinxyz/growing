package com.growing.app.repository;

import com.growing.app.model.MajorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 大分类Repository
 */
@Repository
public interface MajorCategoryRepository extends JpaRepository<MajorCategory, Long> {

    /**
     * 按sort_order排序查询所有大分类
     */
    List<MajorCategory> findAllByOrderBySortOrderAsc();

    /**
     * 按skill_id和sort_order排序查询大分类
     */
    List<MajorCategory> findBySkillIdOrderBySortOrderAsc(Long skillId);

    /**
     * 按名称查询
     */
    Optional<MajorCategory> findByName(String name);
}
