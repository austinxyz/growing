package com.growing.app.controller;

import com.growing.app.dto.ReferralDTO;
import com.growing.app.service.AuthService;
import com.growing.app.service.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内推记录Controller
 * Phase 7: 求职管理模块
 */
@RestController
@RequestMapping("/api/referrals")
public class ReferralController {

    @Autowired
    private ReferralService referralService;

    @Autowired
    private AuthService authService;

    // 获取用户所有内推记录
    @GetMapping
    public ResponseEntity<List<ReferralDTO>> getAllReferrals(
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(referralService.getAllReferralsByUserId(userId));
    }

    // 按公司获取
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ReferralDTO>> getReferralsByCompany(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(referralService.getReferralsByCompany(userId, companyId));
    }

    // 获取内推记录详情
    @GetMapping("/{id}")
    public ResponseEntity<ReferralDTO> getReferralById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(referralService.getReferralById(id, userId));
    }

    // 创建内推记录
    @PostMapping
    public ResponseEntity<ReferralDTO> createReferral(
            @RequestBody ReferralDTO referralDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        ReferralDTO created = referralService.createReferral(userId, referralDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 更新内推记录
    @PutMapping("/{id}")
    public ResponseEntity<ReferralDTO> updateReferral(
            @PathVariable Long id,
            @RequestBody ReferralDTO referralDTO,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        return ResponseEntity.ok(referralService.updateReferral(id, userId, referralDTO));
    }

    // 删除内推记录
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferral(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = authService.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        Long userId = authService.getUserIdByUsername(username);

        referralService.deleteReferral(id, userId);
        return ResponseEntity.noContent().build();
    }
}
