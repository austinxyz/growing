package com.growing.app.service;

import com.growing.app.model.CareerPath;
import com.growing.app.repository.CareerPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerPathService {

    private final CareerPathRepository careerPathRepository;

    public List<CareerPath> getAllCareerPaths() {
        return careerPathRepository.findAll();
    }

    public List<CareerPath> getActiveCareerPaths() {
        return careerPathRepository.findByIsActiveTrue();
    }

    public CareerPath getCareerPathById(Long id) {
        return careerPathRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Career path not found with id: " + id));
    }
}
