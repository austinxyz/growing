package com.growing.app.repository;

import com.growing.app.model.SystemDesignCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemDesignCaseRepository extends JpaRepository<SystemDesignCase, Long> {

    /**
     * 按skill_id查询案例（系统设计skill固定为2）
     * 按难度评分和display_order排序
     */
    @Query("SELECT c FROM SystemDesignCase c " +
           "WHERE c.skill.id = :skillId " +
           "ORDER BY c.displayOrder ASC, c.difficultyRating ASC, c.createdAt DESC")
    List<SystemDesignCase> findBySkillIdOrderByDisplayOrder(@Param("skillId") Long skillId);

    /**
     * 查询所有官方案例
     */
    List<SystemDesignCase> findByIsOfficialTrueOrderByDisplayOrderAscDifficultyRatingAsc();

    /**
     * 查询用户创建的案例
     */
    List<SystemDesignCase> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
}
