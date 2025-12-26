package com.growing.app.service;

import com.growing.app.dto.*;
import com.growing.app.model.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemDesignCaseService {

    @Autowired
    private SystemDesignCaseRepository caseRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaseResourceRepository resourceRepository;

    @Autowired
    private CaseSolutionRepository solutionRepository;

    @Autowired
    private UserCaseNoteRepository userNoteRepository;

    private static final Long SYSTEM_DESIGN_SKILL_ID = 2L;

    // ==================== 管理员API ====================

    /**
     * 获取所有案例（系统设计skill）
     */
    public List<SystemDesignCaseDTO> getAllCases() {
        List<SystemDesignCase> cases = caseRepository.findBySkillIdOrderByDisplayOrder(SYSTEM_DESIGN_SKILL_ID);
        return cases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取案例详情（管理员视角）
     */
    public SystemDesignCaseDTO getCaseById(Long id) {
        SystemDesignCase designCase = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在"));
        return convertToDTOWithDetails(designCase);
    }

    /**
     * 创建案例
     */
    @Transactional
    public SystemDesignCaseDTO createCase(SystemDesignCaseDTO dto, Long userId) {
        SystemDesignCase designCase = new SystemDesignCase();
        updateEntityFromDTO(designCase, dto, userId);

        SystemDesignCase saved = caseRepository.save(designCase);
        return convertToDTO(saved);
    }

    /**
     * 更新案例
     */
    @Transactional
    public SystemDesignCaseDTO updateCase(Long id, SystemDesignCaseDTO dto, Long userId) {
        SystemDesignCase designCase = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在"));

        updateEntityFromDTO(designCase, dto, userId);
        SystemDesignCase updated = caseRepository.save(designCase);
        return convertToDTO(updated);
    }

    /**
     * 删除案例（级联删除resources, solutions, diagrams, user_notes）
     */
    @Transactional
    public void deleteCase(Long id) {
        if (!caseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在");
        }
        caseRepository.deleteById(id);
    }

    // ==================== 用户端API ====================

    /**
     * 获取所有官方案例（用户视角）
     */
    public List<SystemDesignCaseDTO> getOfficialCases() {
        List<SystemDesignCase> cases = caseRepository.findByIsOfficialTrueOrderByDisplayOrderAscDifficultyRatingAsc();
        return cases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取案例详情（用户视角，包含用户答题记录）
     */
    public SystemDesignCaseDTO getCaseByIdForUser(Long id, Long userId) {
        SystemDesignCase designCase = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在"));

        SystemDesignCaseDTO dto = convertToDTOWithDetails(designCase);

        // 加载用户答题记录
        userNoteRepository.findBySystemDesignCaseIdAndUserId(id, userId)
                .ifPresent(note -> dto.setUserNote(convertUserNoteToDTO(note)));

        return dto;
    }

    // ==================== DTO转换方法 ====================

    private SystemDesignCaseDTO convertToDTO(SystemDesignCase entity) {
        SystemDesignCaseDTO dto = new SystemDesignCaseDTO();
        dto.setId(entity.getId());
        dto.setSkillId(entity.getSkill().getId());
        dto.setSkillName(entity.getSkill().getName());
        dto.setTitle(entity.getTitle());
        dto.setCaseDescription(entity.getCaseDescription());
        dto.setDifficulty(entity.getDifficulty());
        dto.setDifficultyRating(entity.getDifficultyRating());
        dto.setCompanyTags(entity.getCompanyTagsList());
        dto.setRelatedFocusAreas(entity.getRelatedFocusAreasList());
        dto.setIsOfficial(entity.getIsOfficial());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getCreatedBy() != null) {
            dto.setCreatedByUserId(entity.getCreatedBy().getId());
            dto.setCreatedByUsername(entity.getCreatedBy().getUsername());
        }

        return dto;
    }

    private SystemDesignCaseDTO convertToDTOWithDetails(SystemDesignCase entity) {
        SystemDesignCaseDTO dto = convertToDTO(entity);

        // 加载学习资源
        List<CaseResource> resources = resourceRepository.findBySystemDesignCaseIdOrderByDisplayOrderAsc(entity.getId());
        dto.setResources(resources.stream()
                .map(this::convertResourceToDTO)
                .collect(Collectors.toList()));

        // 加载参考答案（支持多方案）
        List<CaseSolution> solutions = solutionRepository.findBySystemDesignCaseIdOrderByDisplayOrderAsc(entity.getId());
        dto.setSolutions(solutions.stream()
                .map(this::convertSolutionToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private CaseResourceDTO convertResourceToDTO(CaseResource entity) {
        CaseResourceDTO dto = new CaseResourceDTO();
        dto.setId(entity.getId());
        dto.setCaseId(entity.getSystemDesignCase().getId());
        dto.setResourceType(entity.getResourceType());
        dto.setTitle(entity.getTitle());
        dto.setUrl(entity.getUrl());
        dto.setDescription(entity.getDescription());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private CaseSolutionDTO convertSolutionToDTO(CaseSolution entity) {
        CaseSolutionDTO dto = new CaseSolutionDTO();
        dto.setId(entity.getId());
        dto.setCaseId(entity.getSystemDesignCase().getId());
        dto.setSolutionName(entity.getSolutionName());
        dto.setAuthor(entity.getAuthor());
        dto.setStep1Requirements(entity.getStep1Requirements());
        dto.setStep2Entities(entity.getStep2Entities());
        dto.setStep3Api(entity.getStep3Api());
        dto.setStep4DataFlow(entity.getStep4DataFlow());
        dto.setStep5Architecture(entity.getStep5Architecture());
        dto.setStep6DeepDive(entity.getStep6DeepDive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private UserCaseNoteDTO convertUserNoteToDTO(UserCaseNote entity) {
        UserCaseNoteDTO dto = new UserCaseNoteDTO();
        dto.setId(entity.getId());
        dto.setCaseId(entity.getSystemDesignCase().getId());
        dto.setUserId(entity.getUser().getId());
        dto.setStep1Requirements(entity.getStep1Requirements());
        dto.setStep2Entities(entity.getStep2Entities());
        dto.setStep3Api(entity.getStep3Api());
        dto.setStep4DataFlow(entity.getStep4DataFlow());
        dto.setStep5Architecture(entity.getStep5Architecture());
        dto.setStep6DeepDive(entity.getStep6DeepDive());
        dto.setArchitectureDiagramUrl(entity.getArchitectureDiagramUrl());
        dto.setKeyPoints(entity.getKeyPoints());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private void updateEntityFromDTO(SystemDesignCase entity, SystemDesignCaseDTO dto, Long userId) {
        // 设置Skill（系统设计固定为ID=2）
        if (entity.getSkill() == null) {
            Skill skill = skillRepository.findById(SYSTEM_DESIGN_SKILL_ID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "系统设计Skill不存在"));
            entity.setSkill(skill);
        }

        entity.setTitle(dto.getTitle());
        entity.setCaseDescription(dto.getCaseDescription());
        entity.setDifficulty(dto.getDifficulty());
        entity.setDifficultyRating(dto.getDifficultyRating());
        entity.setCompanyTagsList(dto.getCompanyTags());
        entity.setRelatedFocusAreasList(dto.getRelatedFocusAreas());
        entity.setIsOfficial(dto.getIsOfficial() != null ? dto.getIsOfficial() : true);
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        // 设置创建人
        if (entity.getCreatedBy() == null && userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
            entity.setCreatedBy(user);
        }
    }
}
