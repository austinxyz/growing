package com.growing.app.controller;

import com.growing.app.dto.CaseResourceDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.CaseResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 案例学习资源管理 - 管理员API
 */
@RestController
@RequestMapping("/api/admin/system-design-cases/{caseId}/resources")
public class CaseResourceController {

    @Autowired
    private CaseResourceService resourceService;

    @Autowired
    private AuthService authService;

    /**
     * 获取某个案例的所有学习资源
     */
    @GetMapping
    public ResponseEntity<List<CaseResourceDTO>> getResourcesByCaseId(
            @PathVariable Long caseId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        List<CaseResourceDTO> resources = resourceService.getResourcesByCaseId(caseId);
        return ResponseEntity.ok(resources);
    }

    /**
     * 创建学习资源
     */
    @PostMapping
    public ResponseEntity<CaseResourceDTO> createResource(
            @PathVariable Long caseId,
            @RequestBody CaseResourceDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        CaseResourceDTO created = resourceService.createResource(caseId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 更新学习资源
     */
    @PutMapping("/{resourceId}")
    public ResponseEntity<CaseResourceDTO> updateResource(
            @PathVariable Long caseId,
            @PathVariable Long resourceId,
            @RequestBody CaseResourceDTO dto,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        CaseResourceDTO updated = resourceService.updateResource(resourceId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除学习资源
     */
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long caseId,
            @PathVariable Long resourceId,
            @RequestHeader("Authorization") String token) {
        if (!authService.isAdminByToken(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无管理员权限");
        }

        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }
}
