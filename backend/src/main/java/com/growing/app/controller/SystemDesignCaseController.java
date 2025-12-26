package com.growing.app.controller;

import com.growing.app.dto.SystemDesignCaseDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.SystemDesignCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 系统设计案例管理 - 管理员API
 */
@RestController
@RequestMapping("/api/admin/system-design-cases")
public class SystemDesignCaseController {

    @Autowired
    private SystemDesignCaseService caseService;

    @Autowired
    private AuthService authService;

    /**
     * 获取所有案例
     */
    @GetMapping
    public ResponseEntity<List<SystemDesignCaseDTO>> getAllCases(
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        List<SystemDesignCaseDTO> cases = caseService.getAllCases();
        return ResponseEntity.ok(cases);
    }

    /**
     * 获取案例详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemDesignCaseDTO> getCaseById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        SystemDesignCaseDTO caseDTO = caseService.getCaseById(id);
        return ResponseEntity.ok(caseDTO);
    }

    /**
     * 创建案例
     */
    @PostMapping
    public ResponseEntity<SystemDesignCaseDTO> createCase(
            @RequestBody SystemDesignCaseDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        Long userId = authService.getUserIdFromToken(token);
        SystemDesignCaseDTO created = caseService.createCase(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新案例
     */
    @PutMapping("/{id}")
    public ResponseEntity<SystemDesignCaseDTO> updateCase(
            @PathVariable Long id,
            @RequestBody SystemDesignCaseDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        Long userId = authService.getUserIdFromToken(token);
        SystemDesignCaseDTO updated = caseService.updateCase(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除案例
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}
