package com.growing.app.controller;

import com.growing.app.dto.CompanyDTO;
import com.growing.app.dto.CompanyLinkDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公司Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AuthService authService;

    // 获取用户所有公司
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(companyService.getAllCompaniesByUserId(userId));
    }

    // 按行业获取
    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<CompanyDTO>> getCompaniesByIndustry(
            @PathVariable String industry,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(companyService.getCompaniesByIndustry(userId, industry));
    }

    // 获取公司详情（包含links）
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(companyService.getCompanyById(id, userId));
    }

    // 创建公司
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(
            @RequestBody CompanyDTO companyDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        CompanyDTO created = companyService.createCompany(userId, companyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新公司
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyDTO companyDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(companyService.updateCompany(id, userId, companyDTO));
    }

    // 删除公司（cascade delete所有关联资源）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        companyService.deleteCompany(id, userId);
        return ResponseEntity.noContent().build();
    }

    // --- Company Links ---

    // 获取公司所有链接
    @GetMapping("/{companyId}/links")
    public ResponseEntity<List<CompanyLinkDTO>> getCompanyLinks(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(companyService.getCompanyLinks(companyId, userId));
    }

    // 创建公司链接
    @PostMapping("/{companyId}/links")
    public ResponseEntity<CompanyLinkDTO> createCompanyLink(
            @PathVariable Long companyId,
            @RequestBody CompanyLinkDTO linkDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        CompanyLinkDTO created = companyService.createCompanyLink(companyId, userId, linkDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新公司链接
    @PutMapping("/{companyId}/links/{linkId}")
    public ResponseEntity<CompanyLinkDTO> updateCompanyLink(
            @PathVariable Long companyId,
            @PathVariable Long linkId,
            @RequestBody CompanyLinkDTO linkDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(companyService.updateCompanyLink(companyId, linkId, userId, linkDTO));
    }

    // 删除公司链接
    @DeleteMapping("/{companyId}/links/{linkId}")
    public ResponseEntity<Void> deleteCompanyLink(
            @PathVariable Long companyId,
            @PathVariable Long linkId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        companyService.deleteCompanyLink(companyId, linkId, userId);
        return ResponseEntity.noContent().build();
    }
}
