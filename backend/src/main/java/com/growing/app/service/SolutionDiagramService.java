package com.growing.app.service;

import com.growing.app.dto.SolutionDiagramDTO;
import com.growing.app.model.CaseSolution;
import com.growing.app.model.SolutionDiagram;
import com.growing.app.repository.CaseSolutionRepository;
import com.growing.app.repository.SolutionDiagramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolutionDiagramService {

    @Autowired
    private SolutionDiagramRepository diagramRepository;

    @Autowired
    private CaseSolutionRepository solutionRepository;

    /**
     * 获取某个参考答案的所有配图
     */
    public List<SolutionDiagramDTO> getDiagramsBySolutionId(Long solutionId) {
        List<SolutionDiagram> diagrams = diagramRepository.findByCaseSolutionIdOrderByDisplayOrderAsc(solutionId);
        return diagrams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建配图
     */
    @Transactional
    public SolutionDiagramDTO createDiagram(Long solutionId, SolutionDiagramDTO dto) {
        CaseSolution solution = solutionRepository.findById(solutionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "参考答案不存在"));

        SolutionDiagram diagram = new SolutionDiagram();
        diagram.setCaseSolution(solution);
        updateEntityFromDTO(diagram, dto);

        SolutionDiagram saved = diagramRepository.save(diagram);
        return convertToDTO(saved);
    }

    /**
     * 更新配图
     */
    @Transactional
    public SolutionDiagramDTO updateDiagram(Long id, SolutionDiagramDTO dto) {
        SolutionDiagram diagram = diagramRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "配图不存在"));

        updateEntityFromDTO(diagram, dto);
        SolutionDiagram updated = diagramRepository.save(diagram);
        return convertToDTO(updated);
    }

    /**
     * 删除配图
     */
    @Transactional
    public void deleteDiagram(Long id) {
        if (!diagramRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "配图不存在");
        }
        diagramRepository.deleteById(id);
    }

    // ==================== DTO转换方法 ====================

    private SolutionDiagramDTO convertToDTO(SolutionDiagram entity) {
        SolutionDiagramDTO dto = new SolutionDiagramDTO();
        dto.setId(entity.getId());
        dto.setSolutionId(entity.getCaseSolution().getId());
        dto.setDiagramType(entity.getDiagramType());
        dto.setTitle(entity.getTitle());
        dto.setImageUrl(entity.getImageUrl());
        dto.setDescription(entity.getDescription());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private void updateEntityFromDTO(SolutionDiagram entity, SolutionDiagramDTO dto) {
        entity.setDiagramType(dto.getDiagramType());
        entity.setTitle(dto.getTitle());
        entity.setImageUrl(dto.getImageUrl());
        entity.setDescription(dto.getDescription());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
    }
}
