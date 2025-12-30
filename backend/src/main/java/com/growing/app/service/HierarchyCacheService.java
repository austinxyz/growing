package com.growing.app.service;

import com.growing.app.model.CareerPath;
import com.growing.app.model.Skill;
import com.growing.app.model.MajorCategory;
import com.growing.app.model.FocusArea;
import com.growing.app.repository.CareerPathRepository;
import com.growing.app.repository.SkillRepository;
import com.growing.app.repository.MajorCategoryRepository;
import com.growing.app.repository.FocusAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 层级数据缓存服务
 * 用于缓存职业路径、技能、大分类、Focus Area的映射关系，避免多表JOIN
 *
 * 性能优化原理：
 * 1. 这些数据量小（几百条），更新不频繁，适合全量缓存
 * 2. 在内存中计算focus_area_id列表，避免JOIN
 * 3. 直接用IN查询questions表，结合分页，查询极快
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HierarchyCacheService {

    private final CareerPathRepository careerPathRepository;
    private final SkillRepository skillRepository;
    private final MajorCategoryRepository majorCategoryRepository;
    private final FocusAreaRepository focusAreaRepository;

    /**
     * 应用启动时预热缓存
     */
    @PostConstruct
    public void warmUpCache() {
        log.info("预热层级缓存...");
        long start = System.currentTimeMillis();

        getAllCareerPaths();
        getAllSkills();
        getAllMajorCategories();
        getAllFocusAreas();

        log.info("层级缓存预热完成，耗时: {}ms", System.currentTimeMillis() - start);
    }

    // ==================== 缓存数据源 ====================

    @Cacheable("careerPaths")
    public List<CareerPath> getAllCareerPaths() {
        return careerPathRepository.findAll();
    }

    @Cacheable("skills")
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Cacheable("majorCategories")
    public List<MajorCategory> getAllMajorCategories() {
        return majorCategoryRepository.findAll();
    }

    @Cacheable("focusAreas")
    public List<FocusArea> getAllFocusAreas() {
        // 使用JOIN FETCH预加载Skill，避免懒加载导致的N+1查询
        return focusAreaRepository.findAllWithSkill();
    }

    // ==================== 层级查询方法 ====================

    /**
     * 根据职业路径ID获取所有关联的Focus Area IDs
     * 用于试题库查询：职业路径 → 技能 → Focus Area
     *
     * 注：暂时简化实现，直接从Skill获取（Skill通过CareerPathSkill关联CareerPath）
     * 如需支持，需要查询CareerPathSkill中间表
     */
    public Set<Long> getFocusAreaIdsByCareerPath(Long careerPathId) {
        // TODO: 需要查询CareerPathSkill中间表
        // 当前简化实现：返回空集合，要求用户选择技能
        return Collections.emptySet();
    }

    /**
     * 根据技能ID获取所有关联的Focus Area IDs
     * 用于试题库查询：技能 → Focus Area（直接关联）
     */
    public Set<Long> getFocusAreaIdsBySkill(Long skillId) {
        List<FocusArea> allFocusAreas = getAllFocusAreas();

        // FocusArea直接关联Skill
        return allFocusAreas.stream()
                .filter(fa -> fa.getSkill().getId().equals(skillId))
                .map(FocusArea::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 根据大分类ID获取所有关联的Focus Area IDs
     * 用于试题库查询：大分类 → Focus Area
     *
     * 注：MajorCategory与FocusArea通过focus_area_categories中间表关联
     * 需要查询FocusAreaCategory表
     */
    public Set<Long> getFocusAreaIdsByMajorCategory(Long majorCategoryId) {
        // 暂时返回空集合，需要查询FocusAreaCategory中间表
        // TODO: 集成FocusAreaCategoryRepository
        return Collections.emptySet();
    }

    /**
     * 根据多个条件组合计算Focus Area IDs（AND逻辑）
     *
     * @param careerPathId 职业路径ID（可选）
     * @param skillId 技能ID（可选）
     * @param majorCategoryId 大分类ID（可选）
     * @param focusAreaId Focus Area ID（可选，如果指定则直接返回）
     * @return Focus Area IDs集合，如果没有任何过滤条件则返回空集合（由Service层判断返回null）
     */
    public Set<Long> getFocusAreaIdsByFilters(Long careerPathId, Long skillId,
                                               Long majorCategoryId, Long focusAreaId) {
        // 如果直接指定了Focus Area，优先级最高
        if (focusAreaId != null) {
            return Set.of(focusAreaId);
        }

        Set<Long> result = null;

        // 按层级从上到下计算，取交集（AND逻辑）
        // 注意：如果某个方法返回空集合（TODO未实现），则跳过该条件
        if (careerPathId != null) {
            Set<Long> careerPathFocusAreas = getFocusAreaIdsByCareerPath(careerPathId);
            if (!careerPathFocusAreas.isEmpty()) {  // 只有非空才参与筛选
                result = careerPathFocusAreas;
            }
        }

        if (skillId != null) {
            Set<Long> skillFocusAreas = getFocusAreaIdsBySkill(skillId);
            if (!skillFocusAreas.isEmpty()) {  // 只有非空才参与筛选
                result = result == null ? skillFocusAreas :
                        result.stream().filter(skillFocusAreas::contains).collect(Collectors.toSet());
            }
        }

        if (majorCategoryId != null) {
            Set<Long> categoryFocusAreas = getFocusAreaIdsByMajorCategory(majorCategoryId);
            if (!categoryFocusAreas.isEmpty()) {  // 只有非空才参与筛选
                result = result == null ? categoryFocusAreas :
                        result.stream().filter(categoryFocusAreas::contains).collect(Collectors.toSet());
            }
        }

        // 返回结果集（可能为空）
        // Service层会判断：如果为空且没有任何过滤条件，则返回null给Repository
        return result == null ? Collections.emptySet() : result;
    }

    // ==================== 缓存失效方法 ====================

    @CacheEvict(value = "careerPaths", allEntries = true)
    public void evictCareerPathsCache() {
        log.info("清除职业路径缓存");
    }

    @CacheEvict(value = "skills", allEntries = true)
    public void evictSkillsCache() {
        log.info("清除技能缓存");
    }

    @CacheEvict(value = "majorCategories", allEntries = true)
    public void evictMajorCategoriesCache() {
        log.info("清除大分类缓存");
    }

    @CacheEvict(value = "focusAreas", allEntries = true)
    public void evictFocusAreasCache() {
        log.info("清除Focus Area缓存");
    }

    /**
     * 清除所有层级缓存（用于批量更新后）
     */
    @CacheEvict(value = {"careerPaths", "skills", "majorCategories", "focusAreas"}, allEntries = true)
    public void evictAllHierarchyCache() {
        log.info("清除所有层级缓存");
    }
}
