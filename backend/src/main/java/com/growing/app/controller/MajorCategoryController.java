package com.growing.app.controller;

import com.growing.app.dto.FocusAreaWithCategoryDTO;
import com.growing.app.dto.MajorCategoryDTO;
import com.growing.app.service.FocusAreaService;
import com.growing.app.service.MajorCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 大分类和Focus Area Controller
 * 用于算法内容管理页面获取分类和Focus Area数据
 */
@RestController
@RequestMapping("/api")
public class MajorCategoryController {

    @Autowired
    private MajorCategoryService majorCategoryService;

    @Autowired
    private FocusAreaService focusAreaService;

    /**
     * 获取所有大分类
     * GET /api/major-categories
     */
    @GetMapping("/major-categories")
    public ResponseEntity<List<MajorCategoryDTO>> getAllMajorCategories() {
        return ResponseEntity.ok(majorCategoryService.getAllMajorCategories());
    }

    /**
     * 获取指定技能下的所有Focus Areas（包含分类信息）
     * GET /api/focus-areas-with-categories?skillId=1
     */
    @GetMapping("/focus-areas-with-categories")
    public ResponseEntity<List<FocusAreaWithCategoryDTO>> getFocusAreasWithCategories(
            @RequestParam Long skillId) {
        return ResponseEntity.ok(focusAreaService.getFocusAreasWithCategoriesBySkillId(skillId));
    }
}
