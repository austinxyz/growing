package com.growing.app.controller;

import com.growing.app.entity.*;
import com.growing.app.model.Question;
import com.growing.app.model.LearningResource;
import com.growing.app.model.SystemDesignCase;
import com.growing.app.repository.*;
import com.growing.app.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源搜索Controller
 * 为TODO功能提供系统资源搜索API
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceSearchController {

    private final QuestionRepository questionRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final SystemDesignCaseRepository systemDesignCaseRepository;
    private final ProjectExperienceRepository projectRepository;
    private final ManagementExperienceRepository managementExperienceRepository;
    private final AuthService authService;

    public ResourceSearchController(
            QuestionRepository questionRepository,
            LearningResourceRepository learningResourceRepository,
            SystemDesignCaseRepository systemDesignCaseRepository,
            ProjectExperienceRepository projectRepository,
            ManagementExperienceRepository managementExperienceRepository,
            AuthService authService) {
        this.questionRepository = questionRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.systemDesignCaseRepository = systemDesignCaseRepository;
        this.projectRepository = projectRepository;
        this.managementExperienceRepository = managementExperienceRepository;
        this.authService = authService;
    }

    /**
     * 搜索系统资源
     * @param resourceType 资源类型 (Question/LearningResource/SystemDesignCase/Project/ManagementExperience/ALL)
     * @param keyword 搜索关键词
     * @param token JWT token（用于获取当前用户ID）
     * @return 资源列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<ResourceSearchResult>> searchResources(
            @RequestParam(required = false, defaultValue = "ALL") String resourceType,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);

        List<ResourceSearchResult> results = new ArrayList<>();

        // 根据资源类型搜索
        if ("ALL".equals(resourceType) || "Question".equals(resourceType)) {
            results.addAll(searchQuestions(keyword, userId));
        }
        if ("ALL".equals(resourceType) || "LearningResource".equals(resourceType)) {
            results.addAll(searchLearningResources(keyword, userId));
        }
        if ("ALL".equals(resourceType) || "SystemDesignCase".equals(resourceType)) {
            results.addAll(searchSystemDesignCases(keyword));
        }
        if ("ALL".equals(resourceType) || "Project".equals(resourceType)) {
            results.addAll(searchProjects(keyword, userId));
        }
        if ("ALL".equals(resourceType) || "ManagementExperience".equals(resourceType)) {
            results.addAll(searchManagementExperiences(keyword, userId));
        }

        return ResponseEntity.ok(results);
    }

    /**
     * 搜索试题
     */
    private List<ResourceSearchResult> searchQuestions(String keyword, Long userId) {
        List<Question> questions;
        if (keyword.isEmpty()) {
            questions = questionRepository.findAll(); // 可以添加用户过滤
        } else {
            questions = questionRepository.findAll().stream()
                    .filter(q -> q.getQuestionDescription().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return questions.stream()
                .limit(20) // 限制返回数量
                .map(q -> new ResourceSearchResult(
                        q.getId(),
                        "Question",
                        q.getQuestionDescription(),
                        buildQuestionMeta(q)
                ))
                .collect(Collectors.toList());
    }

    /**
     * 搜索学习材料
     */
    private List<ResourceSearchResult> searchLearningResources(String keyword, Long userId) {
        List<LearningResource> resources;
        if (keyword.isEmpty()) {
            resources = learningResourceRepository.findAll();
        } else {
            resources = learningResourceRepository.findAll().stream()
                    .filter(r -> r.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return resources.stream()
                .limit(20)
                .map(r -> new ResourceSearchResult(
                        r.getId(),
                        "LearningResource",
                        r.getTitle(),
                        buildLearningResourceMeta(r)
                ))
                .collect(Collectors.toList());
    }

    /**
     * 搜索系统设计案例
     */
    private List<ResourceSearchResult> searchSystemDesignCases(String keyword) {
        List<SystemDesignCase> cases;
        if (keyword.isEmpty()) {
            cases = systemDesignCaseRepository.findAll();
        } else {
            cases = systemDesignCaseRepository.findAll().stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return cases.stream()
                .limit(20)
                .map(c -> new ResourceSearchResult(
                        c.getId(),
                        "SystemDesignCase",
                        c.getTitle(),
                        buildSystemDesignCaseMeta(c)
                ))
                .collect(Collectors.toList());
    }

    /**
     * 搜索项目经验
     */
    private List<ResourceSearchResult> searchProjects(String keyword, Long userId) {
        List<ProjectExperience> projects;
        if (keyword.isEmpty()) {
            projects = projectRepository.findByUserIdOrderByStartDateDesc(userId);
        } else {
            projects = projectRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                    .filter(p -> p.getProjectName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return projects.stream()
                .limit(20)
                .map(p -> new ResourceSearchResult(
                        p.getId(),
                        "Project",
                        p.getProjectName(),
                        buildProjectMeta(p)
                ))
                .collect(Collectors.toList());
    }

    /**
     * 搜索人员管理经验
     */
    private List<ResourceSearchResult> searchManagementExperiences(String keyword, Long userId) {
        List<ManagementExperience> experiences;
        if (keyword.isEmpty()) {
            experiences = managementExperienceRepository.findByUserIdOrderByStartDateDesc(userId);
        } else {
            experiences = managementExperienceRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                    .filter(e -> e.getExperienceName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return experiences.stream()
                .limit(20)
                .map(e -> new ResourceSearchResult(
                        e.getId(),
                        "ManagementExperience",
                        e.getExperienceName(),
                        buildManagementExperienceMeta(e)
                ))
                .collect(Collectors.toList());
    }

    /**
     * 构建试题元数据
     */
    private Map<String, String> buildQuestionMeta(Question q) {
        Map<String, String> meta = new HashMap<>();
        meta.put("difficulty", q.getDifficulty() != null ? q.getDifficulty().toString() : "N/A");
        return meta;
    }

    /**
     * 构建学习材料元数据
     */
    private Map<String, String> buildLearningResourceMeta(LearningResource r) {
        Map<String, String> meta = new HashMap<>();
        meta.put("type", r.getResourceType() != null ? r.getResourceType().toString() : "N/A");
        return meta;
    }

    /**
     * 构建系统设计案例元数据
     */
    private Map<String, String> buildSystemDesignCaseMeta(SystemDesignCase c) {
        Map<String, String> meta = new HashMap<>();
        meta.put("difficulty", c.getDifficulty() != null ? c.getDifficulty().toString() : "N/A");
        return meta;
    }

    /**
     * 构建项目经验元数据
     */
    private Map<String, String> buildProjectMeta(ProjectExperience p) {
        Map<String, String> meta = new HashMap<>();
        meta.put("type", p.getProjectType() != null ? p.getProjectType() : "N/A");
        return meta;
    }

    /**
     * 构建管理经验元数据
     */
    private Map<String, String> buildManagementExperienceMeta(ManagementExperience e) {
        Map<String, String> meta = new HashMap<>();
        meta.put("subtype", e.getTeamGrowthSubtype() != null ? e.getTeamGrowthSubtype() : "N/A");
        return meta;
    }

    /**
     * 资源搜索结果DTO
     */
    public static class ResourceSearchResult {
        private Long id;
        private String resourceType;
        private String title;
        private Map<String, String> meta; // 额外元数据（如难度、类型等）

        public ResourceSearchResult(Long id, String resourceType, String title, Map<String, String> meta) {
            this.id = id;
            this.resourceType = resourceType;
            this.title = title;
            this.meta = meta;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Map<String, String> getMeta() {
            return meta;
        }

        public void setMeta(Map<String, String> meta) {
            this.meta = meta;
        }
    }
}
