package com.growing.app.service;

import com.growing.app.dto.CaseSolutionDTO;
import com.growing.app.dto.SolutionDiagramDTO;
import com.growing.app.model.CaseSolution;
import com.growing.app.model.SolutionDiagram;
import com.growing.app.model.SystemDesignCase;
import com.growing.app.repository.CaseSolutionRepository;
import com.growing.app.repository.SolutionDiagramRepository;
import com.growing.app.repository.SystemDesignCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaseSolutionService {

    @Autowired
    private CaseSolutionRepository solutionRepository;

    @Autowired
    private SystemDesignCaseRepository caseRepository;

    @Autowired
    private SolutionDiagramRepository diagramRepository;

    /**
     * 获取某个案例的所有参考答案（支持多方案）
     */
    public List<CaseSolutionDTO> getSolutionsByCaseId(Long caseId) {
        List<CaseSolution> solutions = solutionRepository.findBySystemDesignCaseIdOrderByDisplayOrderAsc(caseId);
        return solutions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取参考答案详情（包含配图）
     */
    public CaseSolutionDTO getSolutionById(Long id) {
        CaseSolution solution = solutionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "参考答案不存在"));
        return convertToDTOWithDiagrams(solution);
    }

    /**
     * 创建参考答案
     */
    @Transactional
    public CaseSolutionDTO createSolution(Long caseId, CaseSolutionDTO dto) {
        SystemDesignCase designCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "案例不存在"));

        CaseSolution solution = new CaseSolution();
        solution.setSystemDesignCase(designCase);
        updateEntityFromDTO(solution, dto);

        CaseSolution saved = solutionRepository.save(solution);
        return convertToDTO(saved);
    }

    /**
     * 更新参考答案
     */
    @Transactional
    public CaseSolutionDTO updateSolution(Long id, CaseSolutionDTO dto) {
        CaseSolution solution = solutionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "参考答案不存在"));

        updateEntityFromDTO(solution, dto);
        CaseSolution updated = solutionRepository.save(solution);
        return convertToDTO(updated);
    }

    /**
     * 删除参考答案（级联删除diagrams）
     */
    @Transactional
    public void deleteSolution(Long id) {
        if (!solutionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "参考答案不存在");
        }
        solutionRepository.deleteById(id);
    }

    // ==================== DTO转换方法 ====================

    private CaseSolutionDTO convertToDTO(CaseSolution entity) {
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

    private CaseSolutionDTO convertToDTOWithDiagrams(CaseSolution entity) {
        CaseSolutionDTO dto = convertToDTO(entity);

        // 加载配图
        List<SolutionDiagram> diagrams = diagramRepository.findByCaseSolutionIdOrderByDisplayOrderAsc(entity.getId());
        dto.setDiagrams(diagrams.stream()
                .map(this::convertDiagramToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private SolutionDiagramDTO convertDiagramToDTO(SolutionDiagram entity) {
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

    private void updateEntityFromDTO(CaseSolution entity, CaseSolutionDTO dto) {
        entity.setSolutionName(dto.getSolutionName());
        entity.setAuthor(dto.getAuthor());
        entity.setStep1Requirements(dto.getStep1Requirements());
        entity.setStep2Entities(dto.getStep2Entities());
        entity.setStep3Api(dto.getStep3Api());
        entity.setStep4DataFlow(dto.getStep4DataFlow());
        entity.setStep5Architecture(dto.getStep5Architecture());
        entity.setStep6DeepDive(dto.getStep6DeepDive());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
    }
}
