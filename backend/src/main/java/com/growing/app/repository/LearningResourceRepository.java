package com.growing.app.repository;

import com.growing.app.model.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    @Query("SELECT lr FROM LearningResource lr WHERE lr.skill.id = :skillId " +
           "AND (lr.isOfficial = true OR lr.createdByUser.id = :userId) " +
           "ORDER BY lr.displayOrder ASC, lr.createdAt ASC")
    List<LearningResource> findBySkillIdForUser(Long skillId, Long userId);

    @Query("SELECT lr FROM LearningResource lr WHERE lr.skill.id = :skillId " +
           "ORDER BY lr.displayOrder ASC, lr.createdAt ASC")
    List<LearningResource> findBySkillIdForAdmin(Long skillId);

    List<LearningResource> findByIsOfficialTrueAndSkillIdOrderByDisplayOrderAsc(Long skillId);

    List<LearningResource> findByCreatedByUserIdOrderByCreatedAtDesc(Long userId);

    Long countBySkillId(Long skillId);

    void deleteBySkillId(Long skillId);
}
