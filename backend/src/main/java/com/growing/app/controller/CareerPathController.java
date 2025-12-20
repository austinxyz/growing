package com.growing.app.controller;

import com.growing.app.model.CareerPath;
import com.growing.app.service.CareerPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/career-paths")
@RequiredArgsConstructor
@Tag(name = "Career Paths", description = "职业路径管理API")
@CrossOrigin(origins = "http://localhost:3000")
public class CareerPathController {

    private final CareerPathService careerPathService;

    @GetMapping
    @Operation(summary = "获取所有职业路径", description = "获取系统中所有可用的职业路径")
    public ResponseEntity<Map<String, Object>> getAllCareerPaths() {
        List<CareerPath> careerPaths = careerPathService.getActiveCareerPaths();
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
}
