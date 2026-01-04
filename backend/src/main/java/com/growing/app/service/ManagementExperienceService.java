package com.growing.app.service;

import com.growing.app.dto.ManagementExperienceDTO;
import com.growing.app.entity.ManagementExperience;
import com.growing.app.repository.ManagementExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagementExperienceService {

    @Autowired
    private ManagementExperienceRepository managementExperienceRepository;

    public List<ManagementExperienceDTO> getAllExperiencesByUserId(Long userId) {
        return managementExperienceRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ManagementExperienceDTO getExperienceById(Long id, Long userId) {
        ManagementExperience experience = managementExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "管理经验不存在"));

        if (!experience.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此管理经验");
        }

        return convertToDTO(experience);
    }

    @Transactional
    public ManagementExperienceDTO createExperience(Long userId, ManagementExperienceDTO dto) {
        ManagementExperience experience = new ManagementExperience();
        experience.setUserId(userId);
        experience.setExperienceName(dto.getExperienceName());
        experience.setExperienceType(dto.getExperienceType());
        experience.setTeamGrowthSubtype(dto.getTeamGrowthSubtype());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setBackground(dto.getBackground());
        experience.setActionsTaken(dto.getActionsTaken());
        experience.setResults(dto.getResults());
        experience.setLessonsLearned(dto.getLessonsLearned());
        experience.setHiringCount(dto.getHiringCount());
        experience.setImprovementResult(dto.getImprovementResult());

        experience = managementExperienceRepository.save(experience);
        return convertToDTO(experience);
    }

    @Transactional
    public ManagementExperienceDTO updateExperience(Long id, Long userId, ManagementExperienceDTO dto) {
        ManagementExperience experience = managementExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "管理经验不存在"));

        if (!experience.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此管理经验");
        }

        experience.setExperienceName(dto.getExperienceName());
        experience.setExperienceType(dto.getExperienceType());
        experience.setTeamGrowthSubtype(dto.getTeamGrowthSubtype());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setBackground(dto.getBackground());
        experience.setActionsTaken(dto.getActionsTaken());
        experience.setResults(dto.getResults());
        experience.setLessonsLearned(dto.getLessonsLearned());
        experience.setHiringCount(dto.getHiringCount());
        experience.setImprovementResult(dto.getImprovementResult());

        experience = managementExperienceRepository.save(experience);
        return convertToDTO(experience);
    }

    @Transactional
    public void deleteExperience(Long id, Long userId) {
        ManagementExperience experience = managementExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "管理经验不存在"));

        if (!experience.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此管理经验");
        }

        managementExperienceRepository.delete(experience);
    }

    private ManagementExperienceDTO convertToDTO(ManagementExperience experience) {
        ManagementExperienceDTO dto = new ManagementExperienceDTO();
        dto.setId(experience.getId());
        dto.setUserId(experience.getUserId());
        dto.setExperienceName(experience.getExperienceName());
        dto.setExperienceType(experience.getExperienceType());
        dto.setTeamGrowthSubtype(experience.getTeamGrowthSubtype());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());
        dto.setBackground(experience.getBackground());
        dto.setActionsTaken(experience.getActionsTaken());
        dto.setResults(experience.getResults());
        dto.setLessonsLearned(experience.getLessonsLearned());
        dto.setHiringCount(experience.getHiringCount());
        dto.setImprovementResult(experience.getImprovementResult());
        dto.setCreatedAt(experience.getCreatedAt());
        dto.setUpdatedAt(experience.getUpdatedAt());
        return dto;
    }
}
