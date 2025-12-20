package com.growing.app.repository;

import com.growing.app.model.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {
    List<CareerPath> findByIsActiveTrue();
    Optional<CareerPath> findByCode(String code);
}
