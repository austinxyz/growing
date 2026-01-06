package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.FocusAreaBriefDTO;
import com.growing.app.dto.ProjectExperienceDTO;
import com.growing.app.entity.ProjectExperience;
import com.growing.app.model.FocusArea;
import com.growing.app.repository.FocusAreaRepository;
import com.growing.app.repository.ProjectExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectExperienceService {

    @Autowired
    private ProjectExperienceRepository projectExperienceRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 获取用户所有项目经验
    public List<ProjectExperienceDTO> getAllProjectsByUserId(Long userId) {
        return projectExperienceRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按项目类型获取
    public List<ProjectExperienceDTO> getProjectsByType(Long userId, String projectType) {
        return projectExperienceRepository.findByUserIdAndProjectTypeOrderByStartDateDesc(userId, projectType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取项目详情
    public ProjectExperienceDTO getProjectById(Long id, Long userId) {
        ProjectExperience project = projectExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在"));

        // 验证所有权
        if (!project.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此项目");
        }

        return convertToDTO(project);
    }

    // 创建项目经验
    @Transactional
    public ProjectExperienceDTO createProject(Long userId, ProjectExperienceDTO dto) {
        ProjectExperience project = new ProjectExperience();
        project.setUserId(userId);
        project.setProjectName(dto.getProjectName());
        project.setProjectType(dto.getProjectType());
        project.setWhatDescription(dto.getWhatDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setTeamSize(dto.getTeamSize());
        project.setMyRole(dto.getMyRole());
        project.setBackground(dto.getBackground());
        project.setProblemStatement(dto.getProblemStatement());
        project.setChallenges(dto.getChallenges());
        project.setConstraints(dto.getConstraints());
        project.setTechStack(dto.getTechStack());
        project.setArchitecture(dto.getArchitecture());
        project.setInnovation(dto.getInnovation());
        project.setMyContribution(dto.getMyContribution());
        project.setQuantitativeResults(dto.getQuantitativeResults());
        project.setBusinessImpact(dto.getBusinessImpact());
        project.setPersonalGrowth(dto.getPersonalGrowth());
        project.setLessonsLearned(dto.getLessonsLearned());
        project.setDifficulty(dto.getDifficulty());

        // Convert JSON fields
        try {
            if (dto.getTechTags() != null) {
                project.setTechTags(objectMapper.writeValueAsString(dto.getTechTags()));
            }
            if (dto.getFocusAreaIds() != null) {
                project.setFocusAreaIds(objectMapper.writeValueAsString(dto.getFocusAreaIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        project = projectExperienceRepository.save(project);
        return convertToDTO(project);
    }

    // 更新项目经验
    @Transactional
    public ProjectExperienceDTO updateProject(Long id, Long userId, ProjectExperienceDTO dto) {
        ProjectExperience project = projectExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在"));

        // 验证所有权
        if (!project.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此项目");
        }

        project.setProjectName(dto.getProjectName());
        project.setProjectType(dto.getProjectType());
        project.setWhatDescription(dto.getWhatDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setTeamSize(dto.getTeamSize());
        project.setMyRole(dto.getMyRole());
        project.setBackground(dto.getBackground());
        project.setProblemStatement(dto.getProblemStatement());
        project.setChallenges(dto.getChallenges());
        project.setConstraints(dto.getConstraints());
        project.setTechStack(dto.getTechStack());
        project.setArchitecture(dto.getArchitecture());
        project.setInnovation(dto.getInnovation());
        project.setMyContribution(dto.getMyContribution());
        project.setQuantitativeResults(dto.getQuantitativeResults());
        project.setBusinessImpact(dto.getBusinessImpact());
        project.setPersonalGrowth(dto.getPersonalGrowth());
        project.setLessonsLearned(dto.getLessonsLearned());
        project.setDifficulty(dto.getDifficulty());

        // Update JSON fields
        try {
            if (dto.getTechTags() != null) {
                project.setTechTags(objectMapper.writeValueAsString(dto.getTechTags()));
            }
            if (dto.getFocusAreaIds() != null) {
                project.setFocusAreaIds(objectMapper.writeValueAsString(dto.getFocusAreaIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        project = projectExperienceRepository.save(project);
        return convertToDTO(project);
    }

    // 删除项目经验
    @Transactional
    public void deleteProject(Long id, Long userId) {
        ProjectExperience project = projectExperienceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "项目不存在"));

        // 验证所有权
        if (!project.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此项目");
        }

        projectExperienceRepository.delete(project);
    }

    // DTO Conversion
    private ProjectExperienceDTO convertToDTO(ProjectExperience project) {
        ProjectExperienceDTO dto = new ProjectExperienceDTO();
        dto.setId(project.getId());
        dto.setUserId(project.getUserId());
        dto.setProjectName(project.getProjectName());
        dto.setProjectType(project.getProjectType());
        dto.setWhatDescription(project.getWhatDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setTeamSize(project.getTeamSize());
        dto.setMyRole(project.getMyRole());
        dto.setBackground(project.getBackground());
        dto.setProblemStatement(project.getProblemStatement());
        dto.setChallenges(project.getChallenges());
        dto.setConstraints(project.getConstraints());
        dto.setTechStack(project.getTechStack());
        dto.setArchitecture(project.getArchitecture());
        dto.setInnovation(project.getInnovation());
        dto.setMyContribution(project.getMyContribution());
        dto.setQuantitativeResults(project.getQuantitativeResults());
        dto.setBusinessImpact(project.getBusinessImpact());
        dto.setPersonalGrowth(project.getPersonalGrowth());
        dto.setLessonsLearned(project.getLessonsLearned());
        dto.setDifficulty(project.getDifficulty());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        // Parse JSON fields
        try {
            if (project.getTechTags() != null) {
                dto.setTechTags(objectMapper.readValue(project.getTechTags(),
                        new TypeReference<List<String>>() {}));
            } else {
                dto.setTechTags(Collections.emptyList());
            }

            // Parse focusAreaIds and populate complete Focus Area details
            if (project.getFocusAreaIds() != null) {
                List<Long> focusAreaIds = objectMapper.readValue(project.getFocusAreaIds(),
                        new TypeReference<List<Long>>() {});
                dto.setFocusAreaIds(focusAreaIds);

                // Fetch complete Focus Area details
                if (!focusAreaIds.isEmpty()) {
                    List<FocusArea> focusAreas = focusAreaRepository.findAllById(focusAreaIds);
                    List<FocusAreaBriefDTO> focusAreaDTOs = focusAreas.stream()
                            .map(this::convertToFocusAreaBriefDTO)
                            .collect(Collectors.toList());
                    dto.setFocusAreas(focusAreaDTOs);

                    // Dynamically calculate skillIds from focusAreas (de-duplicated)
                    Set<Long> skillIdsSet = focusAreas.stream()
                            .filter(fa -> fa.getSkill() != null)
                            .map(fa -> fa.getSkill().getId())
                            .collect(Collectors.toSet());
                    dto.setSkillIds(new ArrayList<>(skillIdsSet));
                } else {
                    dto.setFocusAreas(Collections.emptyList());
                    dto.setSkillIds(Collections.emptyList());
                }
            } else {
                dto.setFocusAreaIds(Collections.emptyList());
                dto.setFocusAreas(Collections.emptyList());
                dto.setSkillIds(Collections.emptyList());
            }
        } catch (Exception e) {
            // If JSON parsing fails, set empty lists
            dto.setTechTags(Collections.emptyList());
            dto.setFocusAreaIds(Collections.emptyList());
            dto.setFocusAreas(Collections.emptyList());
            dto.setSkillIds(Collections.emptyList());
        }

        return dto;
    }

    // Convert FocusArea to FocusAreaBriefDTO
    private FocusAreaBriefDTO convertToFocusAreaBriefDTO(FocusArea focusArea) {
        FocusAreaBriefDTO dto = new FocusAreaBriefDTO();
        dto.setId(focusArea.getId());
        dto.setName(focusArea.getName());
        dto.setDescription(focusArea.getDescription());
        // Note: icon field removed - not in database schema

        // Get skill ID and name from associated skill
        if (focusArea.getSkill() != null) {
            dto.setSkillId(focusArea.getSkill().getId());
            dto.setSkillName(focusArea.getSkill().getName());
        }

        return dto;
    }
}
