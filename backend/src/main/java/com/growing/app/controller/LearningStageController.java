package com.growing.app.controller;

import com.growing.app.dto.LearningStageDTO;
import com.growing.app.model.User;
import com.growing.app.repository.UserRepository;
import com.growing.app.service.LearningStageService;
import com.growing.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 用户端学习阶段Controller
 *
 * 用户端学习阶段API：
 * - 获取Skill的所有学习阶段
 * - 获取单个学习阶段详情（含学习内容列表）
 */
@RestController
@RequestMapping("/api/learning-stages")
@CrossOrigin(origins = "http://localhost:3000")
public class LearningStageController {

    @Autowired
    private LearningStageService learningStageService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取Skill的所有学习阶段
     *
     * GET /api/learning-stages/skills/{skillId}
     *
     * 返回该Skill下的所有学习阶段（不含contents列表）
     */
    @GetMapping("/skills/{skillId}")
    public ResponseEntity<List<LearningStageDTO>> getStagesBySkill(
            @PathVariable Long skillId,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 验证用户身份
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);
            userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

            List<LearningStageDTO> stages = learningStageService.getStagesBySkillId(skillId);
            return ResponseEntity.ok(stages);
        } catch (Exception e) {
            System.err.println("Error in getStagesBySkill: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取单个学习阶段详情（含学习内容列表）
     *
     * GET /api/learning-stages/{id}
     *
     * 返回结构:
     * {
     *   "id": 1,
     *   "skillId": 10,
     *   "stageName": "基础原理",
     *   "stageDescription": "...",
     *   "displayOrder": 1,
     *   "contents": [...]  // 该阶段下的所有学习内容
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningStageDTO> getStageById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        // 验证用户身份
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录"));

        LearningStageDTO stage = learningStageService.getStageById(id);
        return ResponseEntity.ok(stage);
    }
}
