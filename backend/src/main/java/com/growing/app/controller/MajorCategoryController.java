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
     * GET /api/major-categories?skillId=1  (获取指定skill的分类)
     */
    @GetMapping("/major-categories")
    public ResponseEntity<List<MajorCategoryDTO>> getAllMajorCategories(
            @RequestParam(required = false) Long skillId) {
        if (skillId != null) {
            return ResponseEntity.ok(majorCategoryService.getMajorCategoriesBySkillId(skillId));
        }
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

    /**
     * 创建大分类
     * POST /api/major-categories
     */
    @PostMapping("/major-categories")
    public ResponseEntity<MajorCategoryDTO> createMajorCategory(@RequestBody MajorCategoryDTO dto) {
        return ResponseEntity.ok(majorCategoryService.createMajorCategory(dto));
    }

    /**
     * 更新大分类
     * PUT /api/major-categories/{id}
     */
    @PutMapping("/major-categories/{id}")
    public ResponseEntity<MajorCategoryDTO> updateMajorCategory(
            @PathVariable Long id,
            @RequestBody MajorCategoryDTO dto) {
        return ResponseEntity.ok(majorCategoryService.updateMajorCategory(id, dto));
    }

    /**
     * 删除大分类
     * DELETE /api/major-categories/{id}
     */
    @DeleteMapping("/major-categories/{id}")
    public ResponseEntity<Void> deleteMajorCategory(@PathVariable Long id) {
        majorCategoryService.deleteMajorCategory(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 创建Focus Area
     * POST /api/focus-areas
     */
    @PostMapping("/focus-areas")
    public ResponseEntity<FocusAreaWithCategoryDTO> createFocusArea(@RequestBody FocusAreaWithCategoryDTO dto) {
        return ResponseEntity.ok(focusAreaService.createFocusArea(dto));
    }

    /**
     * 更新Focus Area
     * PUT /api/focus-areas/{id}
     */
    @PutMapping("/focus-areas/{id}")
    public ResponseEntity<FocusAreaWithCategoryDTO> updateFocusArea(
            @PathVariable Long id,
            @RequestBody FocusAreaWithCategoryDTO dto) {
        return ResponseEntity.ok(focusAreaService.updateFocusArea(id, dto));
    }

    /**
     * 删除Focus Area
     * DELETE /api/focus-areas/{id}
     */
    @DeleteMapping("/focus-areas/{id}")
    public ResponseEntity<Void> deleteFocusArea(@PathVariable Long id) {
        focusAreaService.deleteFocusArea(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量更新Focus Area的displayOrder
     * PUT /api/focus-areas/batch-update-order
     * Body: [{id: 1, displayOrder: 0}, {id: 2, displayOrder: 1}, ...]
     */
    @PutMapping("/focus-areas/batch-update-order")
    public ResponseEntity<Void> batchUpdateFocusAreaOrder(@RequestBody List<FocusAreaOrderUpdate> updates) {
        focusAreaService.batchUpdateDisplayOrder(updates);
        return ResponseEntity.ok().build();
    }

    /**
     * DTO for batch update
     */
    public static class FocusAreaOrderUpdate {
        private Long id;
        private Integer displayOrder;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }
    }
}
