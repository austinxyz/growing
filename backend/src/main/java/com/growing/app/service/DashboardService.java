package com.growing.app.service;

import com.growing.app.model.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MajorCategoryRepository majorCategoryRepository;

    @Autowired
    private LearningResourceRepository learningResourceRepository;

    @Autowired
    private SystemDesignCaseRepository systemDesignCaseRepository;

    /**
     * 获取整体统计信息
     */
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 职业技能数量
        long skillCount = skillRepository.count();
        stats.put("totalSkills", skillCount);

        // Focus Area总数
        long focusAreaCount = focusAreaRepository.count();
        stats.put("totalFocusAreas", focusAreaCount);

        // 试题总数
        long questionCount = questionRepository.count();
        stats.put("totalQuestions", questionCount);

        // 算法与数据结构：算法技能ID = 1
        long algorithmCategoryCount = majorCategoryRepository.countBySkillId(1L);
        stats.put("algorithmCategories", algorithmCategoryCount);

        // 系统设计基础知识数量（系统设计技能的Focus Area数量，系统设计技能ID = 2）
        long systemDesignBasicsCount = focusAreaRepository.countBySkillId(2L);
        stats.put("systemDesignBasics", systemDesignBasicsCount);

        // 系统设计案例数量
        long systemDesignCasesCount = systemDesignCaseRepository.count();
        stats.put("systemDesignCases", systemDesignCasesCount);

        return stats;
    }

    /**
     * 获取算法与数据结构分类统计
     */
    public Map<String, Object> getAlgorithmCategoriesStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 算法技能ID = 1
        Long algorithmSkillId = 1L;

        // 获取所有大分类
        List<MajorCategory> majorCategories = majorCategoryRepository.findBySkillId(algorithmSkillId);

        List<Map<String, Object>> categoryStats = new ArrayList<>();

        for (MajorCategory category : majorCategories) {
            Map<String, Object> categoryStat = new HashMap<>();
            categoryStat.put("id", category.getId());
            categoryStat.put("name", category.getName());

            // 获取该分类下的所有Focus Area
            List<FocusArea> focusAreas = focusAreaRepository.findByCategoryId(category.getId());
            categoryStat.put("focusAreaCount", focusAreas.size());

            // 获取Focus Area IDs用于统计试题
            List<Long> focusAreaIds = focusAreas.stream()
                    .map(FocusArea::getId)
                    .collect(Collectors.toList());

            // 统计试题数量
            long questionCount = 0;
            if (!focusAreaIds.isEmpty()) {
                questionCount = questionRepository.countByFocusAreaIdIn(focusAreaIds);
            }
            categoryStat.put("questionCount", questionCount);

            categoryStats.add(categoryStat);
        }

        result.put("categories", categoryStats);
        return result;
    }

    /**
     * 获取系统设计模块统计
     */
    public Map<String, Object> getSystemDesignStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 系统设计技能ID = 2
        Long systemDesignSkillId = 2L;

        // 获取基础知识Focus Areas
        List<FocusArea> basicsFocusAreas = focusAreaRepository.findBySkillIdOrderByDisplayOrderAsc(systemDesignSkillId);

        List<Map<String, String>> focusAreaList = basicsFocusAreas.stream()
                .limit(10)  // 只显示前10个
                .map(fa -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("id", fa.getId().toString());
                    item.put("name", fa.getName());
                    return item;
                })
                .collect(Collectors.toList());

        result.put("focusAreas", focusAreaList);
        result.put("totalFocusAreas", basicsFocusAreas.size());

        // 系统设计案例统计
        long casesCount = systemDesignCaseRepository.count();
        result.put("totalCases", casesCount);

        // 获取案例列表（前5个）
        List<SystemDesignCase> cases = systemDesignCaseRepository.findAll();
        List<Map<String, String>> caseList = cases.stream()
                .limit(5)
                .map(c -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("id", c.getId().toString());
                    item.put("title", c.getTitle());
                    return item;
                })
                .collect(Collectors.toList());

        result.put("cases", caseList);

        return result;
    }
}
