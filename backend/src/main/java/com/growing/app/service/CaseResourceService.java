package com.growing.app.service;

import com.growing.app.dto.CaseResourceDTO;
import com.growing.app.model.CaseResource;
import com.growing.app.model.SystemDesignCase;
import com.growing.app.repository.CaseResourceRepository;
import com.growing.app.repository.SystemDesignCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaseResourceService {

    @Autowired
    private CaseResourceRepository resourceRepository;

    @Autowired
    private SystemDesignCaseRepository caseRepository;

    /**
     * 获取某个案例的所有学习资源
     */
    public List<CaseResourceDTO> getResourcesByCaseId(Long caseId) {
        List<CaseResource> resources = resourceRepository.findBySystemDesignCaseIdOrderByDisplayOrderAsc(caseId);
        return resources.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建学习资源
     */
    @Transactional
    public CaseResourceDTO createResource(Long caseId, CaseResourceDTO dto) {
        SystemDesignCase designCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在"));

        CaseResource resource = new CaseResource();
        resource.setSystemDesignCase(designCase);
        updateEntityFromDTO(resource, dto);

        CaseResource saved = resourceRepository.save(resource);
        return convertToDTO(saved);
    }

    /**
     * 更新学习资源
     */
    @Transactional
    public CaseResourceDTO updateResource(Long id, CaseResourceDTO dto) {
        CaseResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学习资源不存在"));

        updateEntityFromDTO(resource, dto);
        CaseResource updated = resourceRepository.save(resource);
        return convertToDTO(updated);
    }

    /**
     * 删除学习资源
     */
    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "学习资源不存在");
        }
        resourceRepository.deleteById(id);
    }

    // ==================== DTO转换方法 ====================

    private CaseResourceDTO convertToDTO(CaseResource entity) {
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

    private void updateEntityFromDTO(CaseResource entity, CaseResourceDTO dto) {
        entity.setResourceType(dto.getResourceType());
        entity.setTitle(dto.getTitle());
        entity.setUrl(dto.getUrl());
        entity.setDescription(dto.getDescription());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
    }
}
