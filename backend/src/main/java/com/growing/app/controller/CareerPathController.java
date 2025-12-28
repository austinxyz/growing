package com.growing.app.controller;

import com.growing.app.dto.CareerPathWithSkillsDTO;
import com.growing.app.model.CareerPath;
import com.growing.app.model.User;
import com.growing.app.repository.UserRepository;
import com.growing.app.service.AuthService;
import com.growing.app.service.CareerPathService;
import com.growing.app.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/career-paths")
@RequiredArgsConstructor
@Tag(name = "Career Paths", description = "职业路径管理API")
@CrossOrigin(origins = "http://localhost:3000")
public class CareerPathController {

    private final CareerPathService careerPathService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "获取所有职业路径(包含技能)", description = "获取系统中所有可用的职业路径及其关联的技能")
    public ResponseEntity<Map<String, Object>> getAllCareerPaths() {
        List<CareerPathWithSkillsDTO> careerPaths = careerPathService.getActiveCareerPathsWithSkills();
        Map<String, Object> response = new HashMap<>();
        response.put("data", careerPaths);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取职业路径", description = "获取指定ID的职业路径详情")
    public ResponseEntity<CareerPath> getCareerPathById(@PathVariable Long id) {
        CareerPath careerPath = careerPathService.getCareerPathById(id);
        return ResponseEntity.ok(careerPath);
    }

    @GetMapping("/my")
    @Operation(summary = "获取当前用户的职业路径", description = "获取当前登录用户关联的所有职业路径")
    public ResponseEntity<Map<String, Object>> getMyCareerPaths(@RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.getUsernameFromToken(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<CareerPath> myCareerPaths = user.getCareerPaths().stream()
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("data", myCareerPaths);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "创建职业路径", description = "管理员创建新的职业路径")
    public ResponseEntity<CareerPath> createCareerPath(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String authHeader) {
        authService.isAdminByToken(authHeader.replace("Bearer ", ""));

        CareerPath careerPath = new CareerPath();
        careerPath.setName((String) requestBody.get("name"));
        careerPath.setCode((String) requestBody.get("code"));
        careerPath.setDescription((String) requestBody.get("description"));
        careerPath.setIcon((String) requestBody.get("icon"));
        careerPath.setIsActive(requestBody.get("isActive") != null ? (Boolean) requestBody.get("isActive") : true);

        @SuppressWarnings("unchecked")
        List<Integer> skillIdsInt = (List<Integer>) requestBody.get("skillIds");
        List<Long> skillIds = skillIdsInt != null ? skillIdsInt.stream().map(Integer::longValue).collect(Collectors.toList()) : null;

        CareerPath created = careerPathService.createCareerPath(careerPath, skillIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新职业路径", description = "管理员更新职业路径信息")
    public ResponseEntity<CareerPath> updateCareerPath(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String authHeader) {
        authService.isAdminByToken(authHeader.replace("Bearer ", ""));

        CareerPath careerPath = new CareerPath();
        careerPath.setName((String) requestBody.get("name"));
        careerPath.setCode((String) requestBody.get("code"));
        careerPath.setDescription((String) requestBody.get("description"));
        careerPath.setIcon((String) requestBody.get("icon"));
        careerPath.setIsActive(requestBody.get("isActive") != null ? (Boolean) requestBody.get("isActive") : true);

        @SuppressWarnings("unchecked")
        List<Integer> skillIdsInt = (List<Integer>) requestBody.get("skillIds");
        List<Long> skillIds = skillIdsInt != null ? skillIdsInt.stream().map(Integer::longValue).collect(Collectors.toList()) : null;

        CareerPath updated = careerPathService.updateCareerPath(id, careerPath, skillIds);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除职业路径", description = "管理员删除职业路径（不会删除关联的技能）")
    public ResponseEntity<Void> deleteCareerPath(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        authService.isAdminByToken(authHeader.replace("Bearer ", ""));
        careerPathService.deleteCareerPath(id);
        return ResponseEntity.noContent().build();
    }
}
