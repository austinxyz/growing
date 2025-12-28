package com.growing.app.repository;

import com.growing.app.model.LearningResourceWebsite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningResourceWebsiteRepository extends JpaRepository<LearningResourceWebsite, Long> {
    Optional<LearningResourceWebsite> findByName(String name);
}
