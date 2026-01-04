package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ResumeExperienceDTO;
import com.growing.app.entity.ResumeExperience;
import com.growing.app.entity.Resume;
import com.growing.app.repository.ResumeExperienceRepository;
import com.growing.app.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeExperienceService {

    @Autowired
    private ResumeExperienceRepository resumeExperienceRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ResumeExperienceDTO> getExperiencesByResumeId(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此简历的工作经验");
        }

        return resumeExperienceRepository.findByResumeIdOrderBySortOrder(resumeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ResumeExperienceDTO getExperienceById(Long id, Long userId) {
        ResumeExperience experience = resumeExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "工作经验不存在"));

        Resume resume = resumeRepository.findById(experience.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此工作经验");
        }

        return convertToDTO(experience);
    }

    @Transactional
    public ResumeExperienceDTO createExperience(Long userId, ResumeExperienceDTO dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此简历添加工作经验");
        }

        ResumeExperience experience = new ResumeExperience();
        experience.setResumeId(dto.getResumeId());
        experience.setCompanyName(dto.getCompanyName());
        experience.setPosition(dto.getPosition());
        experience.setLocation(dto.getLocation());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setIsCurrent(dto.getIsCurrent());
        experience.setResponsibilities(dto.getResponsibilities());
        experience.setAchievements(dto.getAchievements());
        experience.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        // Convert JSON field
        try {
            if (dto.getProjectIds() != null) {
                experience.setProjectIds(objectMapper.writeValueAsString(dto.getProjectIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        experience = resumeExperienceRepository.save(experience);
        return convertToDTO(experience);
    }

    @Transactional
    public ResumeExperienceDTO updateExperience(Long id, Long userId, ResumeExperienceDTO dto) {
        ResumeExperience experience = resumeExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "工作经验不存在"));

        Resume resume = resumeRepository.findById(experience.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此工作经验");
        }

        experience.setCompanyName(dto.getCompanyName());
        experience.setPosition(dto.getPosition());
        experience.setLocation(dto.getLocation());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setIsCurrent(dto.getIsCurrent());
        experience.setResponsibilities(dto.getResponsibilities());
        experience.setAchievements(dto.getAchievements());
        experience.setSortOrder(dto.getSortOrder());

        // Update JSON field
        try {
            if (dto.getProjectIds() != null) {
                experience.setProjectIds(objectMapper.writeValueAsString(dto.getProjectIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        experience = resumeExperienceRepository.save(experience);
        return convertToDTO(experience);
    }

    @Transactional
    public void deleteExperience(Long id, Long userId) {
        ResumeExperience experience = resumeExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "工作经验不存在"));

        Resume resume = resumeRepository.findById(experience.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此工作经验");
        }

        resumeExperienceRepository.delete(experience);
    }

    private ResumeExperienceDTO convertToDTO(ResumeExperience experience) {
        ResumeExperienceDTO dto = new ResumeExperienceDTO();
        dto.setId(experience.getId());
        dto.setResumeId(experience.getResumeId());
        dto.setCompanyName(experience.getCompanyName());
        dto.setPosition(experience.getPosition());
        dto.setLocation(experience.getLocation());
        dto.setStartDate(experience.getStartDate());
        dto.setEndDate(experience.getEndDate());
        dto.setIsCurrent(experience.getIsCurrent());
        dto.setResponsibilities(experience.getResponsibilities());
        dto.setAchievements(experience.getAchievements());
        dto.setSortOrder(experience.getSortOrder());
        dto.setCreatedAt(experience.getCreatedAt());
        dto.setUpdatedAt(experience.getUpdatedAt());

        // Parse JSON field
        try {
            if (experience.getProjectIds() != null) {
                dto.setProjectIds(objectMapper.readValue(experience.getProjectIds(),
                        new TypeReference<List<Long>>() {}));
            }
        } catch (Exception e) {
            dto.setProjectIds(Collections.emptyList());
        }

        return dto;
    }
}
