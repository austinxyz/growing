package com.growing.app.service;

import com.growing.app.dto.LearningResourceDTO;
import com.growing.app.model.LearningResource;
import com.growing.app.model.Skill;
import com.growing.app.model.User;
import com.growing.app.repository.LearningResourceRepository;
import com.growing.app.repository.SkillRepository;
import com.growing.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningResourceService {

    @Autowired
    private LearningResourceRepository learningResourceRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取技能下的Learning Resources（用户视角 - 应用可见性过滤）
    public List<LearningResourceDTO> getResourcesBySkillIdForUser(Long skillId, Long userId) {
        return learningResourceRepository.findBySkillIdForUser(skillId, userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取技能下的Learning Resources（管理员视角 - 无过滤）
    public List<LearningResourceDTO> getResourcesBySkillIdForAdmin(Long skillId) {
        return learningResourceRepository.findBySkillIdForAdmin(skillId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取用户创建的资源
    public List<LearningResourceDTO> getUserCreatedResources(Long userId) {
        return learningResourceRepository.findByCreatedByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 创建官方资源（管理员）
    @Transactional
    public LearningResourceDTO createOfficialResource(LearningResourceDTO dto) {
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        LearningResource resource = new LearningResource();
        resource.setSkill(skill);
        resource.setResourceType(dto.getResourceType());
        resource.setTitle(dto.getTitle());
        resource.setUrl(dto.getUrl());
        resource.setAuthor(dto.getAuthor());
        resource.setDescription(dto.getDescription());
        resource.setIsOfficial(true);
        resource.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        resource = learningResourceRepository.save(resource);
        return convertToDTO(resource);
    }

    // 创建用户资源
    @Transactional
    public LearningResourceDTO createUserResource(LearningResourceDTO dto, Long userId) {
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));

        LearningResource resource = new LearningResource();
        resource.setSkill(skill);
        resource.setResourceType(dto.getResourceType());
        resource.setTitle(dto.getTitle());
        resource.setUrl(dto.getUrl());
        resource.setAuthor(dto.getAuthor());
        resource.setDescription(dto.getDescription());
        resource.setIsOfficial(false);
        resource.setCreatedByUser(user);
        resource.setDisplayOrder(0);

        resource = learningResourceRepository.save(resource);
        return convertToDTO(resource);
    }

    // 更新资源
    @Transactional
    public LearningResourceDTO updateResource(Long id, LearningResourceDTO dto, Long userId, boolean isAdmin) {
        LearningResource resource = learningResourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习资源不存在"));

        // 权限检查：管理员可以更新所有资源，普通用户只能更新自己创建的资源
        if (!isAdmin && (resource.getCreatedByUser() == null || !resource.getCreatedByUser().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限修改此资源");
        }

        resource.setResourceType(dto.getResourceType());
        resource.setTitle(dto.getTitle());
        resource.setUrl(dto.getUrl());
        resource.setAuthor(dto.getAuthor());
        resource.setDescription(dto.getDescription());

        if (isAdmin) {
            resource.setDisplayOrder(dto.getDisplayOrder());
        }

        resource = learningResourceRepository.save(resource);
        return convertToDTO(resource);
    }

    // 删除资源
    @Transactional
    public void deleteResource(Long id, Long userId, boolean isAdmin) {
        LearningResource resource = learningResourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习资源不存在"));

        // 权限检查：管理员可以删除所有资源，普通用户只能删除自己创建的资源
        if (!isAdmin && (resource.getCreatedByUser() == null || !resource.getCreatedByUser().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限删除此资源");
        }

        learningResourceRepository.deleteById(id);
    }

    // DTO转换
    private LearningResourceDTO convertToDTO(LearningResource resource) {
        LearningResourceDTO dto = new LearningResourceDTO();
        dto.setId(resource.getId());
        dto.setSkillId(resource.getSkill().getId());
        dto.setResourceType(resource.getResourceType());
        dto.setTitle(resource.getTitle());
        dto.setUrl(resource.getUrl());
        dto.setAuthor(resource.getAuthor());
        dto.setDescription(resource.getDescription());
        dto.setIsOfficial(resource.getIsOfficial());
        dto.setDisplayOrder(resource.getDisplayOrder());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());

        if (resource.getCreatedByUser() != null) {
            dto.setCreatedByUserId(resource.getCreatedByUser().getId());
            dto.setCreatedByUsername(resource.getCreatedByUser().getUsername());
        }

        return dto;
    }
}
