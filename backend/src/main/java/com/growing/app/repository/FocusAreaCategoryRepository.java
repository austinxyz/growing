package com.growing.app.repository;

import com.growing.app.model.FocusAreaCategory;
import com.growing.app.model.FocusAreaCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Focus Area与大分类关联Repository
 */
@Repository
public interface FocusAreaCategoryRepository extends JpaRepository<FocusAreaCategory, FocusAreaCategoryId> {

    /**
     * 查询大分类下的所有Focus Area
     */
    List<FocusAreaCategory> findByCategoryId(Long categoryId);

    /**
     * 查询Focus Area所属的所有大分类
     */
    List<FocusAreaCategory> findByFocusAreaId(Long focusAreaId);

    /**
     * 删除Focus Area的所有分类关联
     */
    void deleteByFocusAreaId(Long focusAreaId);

    /**
     * 删除大分类的所有Focus Area关联
     */
    void deleteByCategoryId(Long categoryId);

    /**
     * 查询大分类下的Focus Area ID列表
     */
    @Query("SELECT fac.focusAreaId FROM FocusAreaCategory fac WHERE fac.categoryId = :categoryId")
    List<Long> findFocusAreaIdsByCategoryId(Long categoryId);
}
