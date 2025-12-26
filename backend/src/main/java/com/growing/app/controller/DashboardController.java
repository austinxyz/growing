package com.growing.app.controller;

import com.growing.app.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘整体统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = dashboardService.getOverallStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取算法与数据结构分类统计
     */
    @GetMapping("/algorithm-categories")
    public ResponseEntity<Map<String, Object>> getAlgorithmCategories() {
        Map<String, Object> categories = dashboardService.getAlgorithmCategoriesStatistics();
        return ResponseEntity.ok(categories);
    }

    /**
     * 获取系统设计模块统计
     */
    @GetMapping("/system-design-summary")
    public ResponseEntity<Map<String, Object>> getSystemDesignSummary() {
        Map<String, Object> summary = dashboardService.getSystemDesignStatistics();
        return ResponseEntity.ok(summary);
    }
}
