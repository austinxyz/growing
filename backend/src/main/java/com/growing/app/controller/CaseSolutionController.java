package com.growing.app.controller;

import com.growing.app.dto.CaseSolutionDTO;
import com.growing.app.dto.SolutionDiagramDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.CaseSolutionService;
import com.growing.app.service.SolutionDiagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 案例参考答案管理 - 管理员API
 */
@RestController
@RequestMapping("/api/admin/system-design-cases")
public class CaseSolutionController {

    @Autowired
    private CaseSolutionService solutionService;

    @Autowired
    private SolutionDiagramService diagramService;

    @Autowired
    private AuthService authService;

    // ==================== 参考答案管理 ====================

    /**
     * 获取某个案例的所有参考答案
     */
    @GetMapping("/{caseId}/solutions")
    public ResponseEntity<List<CaseSolutionDTO>> getSolutionsByCaseId(
            @PathVariable Long caseId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        List<CaseSolutionDTO> solutions = solutionService.getSolutionsByCaseId(caseId);
        return ResponseEntity.ok(solutions);
    }

    /**
     * 获取参考答案详情（包含配图）
     */
    @GetMapping("/solutions/{solutionId}")
    public ResponseEntity<CaseSolutionDTO> getSolutionById(
            @PathVariable Long solutionId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        CaseSolutionDTO solution = solutionService.getSolutionById(solutionId);
        return ResponseEntity.ok(solution);
    }

    /**
     * 创建参考答案
     */
    @PostMapping("/{caseId}/solutions")
    public ResponseEntity<CaseSolutionDTO> createSolution(
            @PathVariable Long caseId,
            @RequestBody CaseSolutionDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        CaseSolutionDTO created = solutionService.createSolution(caseId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新参考答案
     */
    @PutMapping("/solutions/{solutionId}")
    public ResponseEntity<CaseSolutionDTO> updateSolution(
            @PathVariable Long solutionId,
            @RequestBody CaseSolutionDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        CaseSolutionDTO updated = solutionService.updateSolution(solutionId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除参考答案
     */
    @DeleteMapping("/solutions/{solutionId}")
    public ResponseEntity<Void> deleteSolution(
            @PathVariable Long solutionId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        solutionService.deleteSolution(solutionId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 配图管理 ====================

    /**
     * 获取某个参考答案的所有配图
     */
    @GetMapping("/solutions/{solutionId}/diagrams")
    public ResponseEntity<List<SolutionDiagramDTO>> getDiagramsBySolutionId(
            @PathVariable Long solutionId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        List<SolutionDiagramDTO> diagrams = diagramService.getDiagramsBySolutionId(solutionId);
        return ResponseEntity.ok(diagrams);
    }

    /**
     * 创建配图
     */
    @PostMapping("/solutions/{solutionId}/diagrams")
    public ResponseEntity<SolutionDiagramDTO> createDiagram(
            @PathVariable Long solutionId,
            @RequestBody SolutionDiagramDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        SolutionDiagramDTO created = diagramService.createDiagram(solutionId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新配图
     */
    @PutMapping("/diagrams/{diagramId}")
    public ResponseEntity<SolutionDiagramDTO> updateDiagram(
            @PathVariable Long diagramId,
            @RequestBody SolutionDiagramDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        SolutionDiagramDTO updated = diagramService.updateDiagram(diagramId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除配图
     */
    @DeleteMapping("/diagrams/{diagramId}")
    public ResponseEntity<Void> deleteDiagram(
            @PathVariable Long diagramId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        diagramService.deleteDiagram(diagramId);
        return ResponseEntity.noContent().build();
    }
}
