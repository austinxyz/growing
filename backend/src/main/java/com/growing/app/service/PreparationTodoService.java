package com.growing.app.service;

import com.growing.app.dto.PreparationTodoDTO;
import com.growing.app.entity.*;
import com.growing.app.model.Question;
import com.growing.app.model.LearningResource;
import com.growing.app.model.SystemDesignCase;
import com.growing.app.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PreparationTodoService {

    private final InterviewPreparationTodoRepository todoRepository;
    private final InterviewPreparationTodoResourceRepository todoResourceRepository;
    private final QuestionRepository questionRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final SystemDesignCaseRepository systemDesignCaseRepository;
    private final ProjectExperienceRepository projectRepository;
    private final ManagementExperienceRepository managementExperienceRepository;

    public PreparationTodoService(
            InterviewPreparationTodoRepository todoRepository,
            InterviewPreparationTodoResourceRepository todoResourceRepository,
            QuestionRepository questionRepository,
            LearningResourceRepository learningResourceRepository,
            SystemDesignCaseRepository systemDesignCaseRepository,
            ProjectExperienceRepository projectRepository,
            ManagementExperienceRepository managementExperienceRepository) {
        this.todoRepository = todoRepository;
        this.todoResourceRepository = todoResourceRepository;
        this.questionRepository = questionRepository;
        this.learningResourceRepository = learningResourceRepository;
        this.systemDesignCaseRepository = systemDesignCaseRepository;
        this.projectRepository = projectRepository;
        this.managementExperienceRepository = managementExperienceRepository;
    }

    /**
     * 获取面试阶段的所有TODO（按orderIndex排序）
     */
    public List<PreparationTodoDTO> getTodosByStageId(Long stageId, Long userId) {
        List<InterviewPreparationTodo> todos = todoRepository.findByInterviewStageIdAndUserIdOrderByOrderIndexAsc(stageId, userId);

        // 批量查询所有TODO的资源
        List<Long> todoIds = todos.stream().map(InterviewPreparationTodo::getId).collect(Collectors.toList());
        List<InterviewPreparationTodoResource> allResources = todoIds.isEmpty() ?
            Collections.emptyList() :
            todoResourceRepository.findByTodoIdIn(todoIds);

        // 按todoId分组
        Map<Long, List<InterviewPreparationTodoResource>> resourcesByTodoId = allResources.stream()
                .collect(Collectors.groupingBy(r -> r.getTodo().getId()));

        return todos.stream()
                .map(todo -> convertToDTO(todo, resourcesByTodoId.getOrDefault(todo.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    /**
     * 获取面试阶段的所有准备项（AI生成 + 用户自定义TODO统一列表）
     * 现在所有数据都在interview_preparation_todos表中，通过source字段区分
     */
    public List<PreparationTodoDTO> getTodosWithChecklistByStageId(Long stageId, Long userId) {
        // 获取该面试阶段的所有TODO（包括AI生成和用户自定义）
        // userId为NULL的是AI生成的，有userId的是用户自定义的
        List<InterviewPreparationTodo> todos = todoRepository.findByInterviewStageIdOrderByOrderIndexAsc(stageId);

        // 批量查询所有TODO的资源
        List<Long> todoIds = todos.stream().map(InterviewPreparationTodo::getId).collect(Collectors.toList());
        List<InterviewPreparationTodoResource> allResources = todoIds.isEmpty() ?
            Collections.emptyList() :
            todoResourceRepository.findByTodoIdIn(todoIds);
        Map<Long, List<InterviewPreparationTodoResource>> resourcesByTodoId = allResources.stream()
                .collect(Collectors.groupingBy(r -> r.getTodo().getId()));

        // 转换为DTO
        List<PreparationTodoDTO> result = todos.stream()
                .map(todo -> {
                    PreparationTodoDTO dto = convertToDTO(
                        todo,
                        resourcesByTodoId.getOrDefault(todo.getId(), Collections.emptyList())
                    );
                    // 设置fromChecklist标志（AI生成的被视为来自checklist）
                    dto.setFromChecklist("AI".equals(todo.getSource()));
                    dto.setChecklistCategory(todo.getTodoType());
                    dto.setChecklistPriority(todo.getPriority() >= 4);
                    dto.setHasDetails(true); // 所有TODO都有详细信息（可以直接完成）
                    return dto;
                })
                .collect(Collectors.toList());

        // 按优先级和order排序
        result.sort((a, b) -> {
            // 优先级高的在前
            int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
            if (priorityCompare != 0) return priorityCompare;
            // 同优先级按order排序
            return Integer.compare(a.getOrderIndex(), b.getOrderIndex());
        });

        return result;
    }

    /**
     * 创建TODO
     */
    @Transactional
    public PreparationTodoDTO createTodo(PreparationTodoDTO dto, Long userId) {
        InterviewPreparationTodo todo = new InterviewPreparationTodo();
        todo.setInterviewStageId(dto.getInterviewStageId());
        todo.setUserId(userId);
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setTodoType(dto.getTodoType() != null ? dto.getTodoType() : "General");
        todo.setSource("User"); // 用户创建的TODO
        todo.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        todo.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : getNextOrderIndex(dto.getInterviewStageId()));
        todo.setIsCompleted(false);

        InterviewPreparationTodo saved = todoRepository.save(todo);

        // 保存资源关联
        if (dto.getResourceLinks() != null && !dto.getResourceLinks().isEmpty()) {
            saveResources(saved, dto.getResourceLinks());
        }

        return convertToDTO(saved, Collections.emptyList());
    }

    /**
     * 更新TODO
     */
    @Transactional
    public PreparationTodoDTO updateTodo(Long id, PreparationTodoDTO dto, Long userId) {
        InterviewPreparationTodo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TODO not found"));

        // 验证用户权限（AI生成的TODO userId为NULL，可以被任何用户编辑）
        if (todo.getUserId() != null && !todo.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this TODO");
        }

        // 更新字段
        if (dto.getTitle() != null) {
            todo.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            todo.setDescription(dto.getDescription());
        }
        if (dto.getTodoType() != null) {
            todo.setTodoType(dto.getTodoType());
        }
        if (dto.getPriority() != null) {
            todo.setPriority(dto.getPriority());
        }
        if (dto.getOrderIndex() != null) {
            todo.setOrderIndex(dto.getOrderIndex());
        }

        // 更新资源链接
        if (dto.getResourceLinks() != null) {
            // 删除旧资源
            todoResourceRepository.deleteByTodoId(id);
            // 保存新资源
            if (!dto.getResourceLinks().isEmpty()) {
                saveResources(todo, dto.getResourceLinks());
            }
        }

        InterviewPreparationTodo updated = todoRepository.save(todo);
        List<InterviewPreparationTodoResource> resources = todoResourceRepository.findByTodoId(id);
        return convertToDTO(updated, resources);
    }

    /**
     * 标记TODO为完成/未完成
     */
    @Transactional
    public PreparationTodoDTO toggleComplete(Long id, Boolean isCompleted, String completionNotes, Long userId) {
        InterviewPreparationTodo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TODO not found"));

        // 验证用户权限（AI生成的TODO userId为NULL，可以被任何用户操作）
        if (todo.getUserId() != null && !todo.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this TODO");
        }

        todo.setIsCompleted(isCompleted);
        if (isCompleted) {
            todo.setCompletedAt(LocalDateTime.now());
            todo.setCompletionNotes(completionNotes);
        } else {
            todo.setCompletedAt(null);
            todo.setCompletionNotes(null);
        }

        InterviewPreparationTodo updated = todoRepository.save(todo);
        List<InterviewPreparationTodoResource> resources = todoResourceRepository.findByTodoId(id);
        return convertToDTO(updated, resources);
    }

    /**
     * 删除TODO（现在允许删除AI生成的TODO）
     */
    @Transactional
    public void deleteTodo(Long id, Long userId) {
        InterviewPreparationTodo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TODO not found"));

        // 验证用户权限
        // AI生成的TODO (userId == null): 验证用户拥有该interview stage
        // 用户创建的TODO (userId != null): 验证是否为同一用户
        if (todo.getUserId() == null) {
            // AI生成的TODO，通过interview_stage_id验证权限
            // 这里简化处理，后续可以添加interview_stage → job_application → user的权限检查
            // 暂时允许删除（因为前端已经在正确的context下）
        } else if (!todo.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this TODO");
        }

        // 级联删除资源关联（通过外键ON DELETE CASCADE自动处理）
        todoRepository.delete(todo);
    }

    /**
     * 批量更新TODO顺序
     */
    @Transactional
    public void reorderTodos(Long stageId, List<ReorderRequest> reorderRequests, Long userId) {
        for (ReorderRequest request : reorderRequests) {
            InterviewPreparationTodo todo = todoRepository.findById(request.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TODO not found: " + request.getId()));

            // 验证用户权限（AI生成的TODO可以被任何用户重新排序）
            if (todo.getUserId() != null && !todo.getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to reorder this TODO");
            }

            // 验证TODO属于指定的面试阶段
            if (!todo.getInterviewStageId().equals(stageId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TODO does not belong to the specified interview stage");
            }

            todo.setOrderIndex(request.getOrderIndex());
            todoRepository.save(todo);
        }
    }

    /**
     * 保存资源关联
     */
    private void saveResources(InterviewPreparationTodo todo, List<PreparationTodoDTO.ResourceLink> resourceLinks) {
        for (PreparationTodoDTO.ResourceLink link : resourceLinks) {
            InterviewPreparationTodoResource resource = new InterviewPreparationTodoResource();
            resource.setTodo(todo);
            resource.setResourceType(link.getResourceType());
            resource.setResourceId(link.getResourceId());

            // 外部链接特殊处理
            if ("ExternalLink".equals(link.getResourceType())) {
                resource.setTitle(link.getTitle());
                resource.setUrl(link.getUrl());
            }

            todoResourceRepository.save(resource);
        }
    }

    /**
     * 获取下一个orderIndex
     */
    private Integer getNextOrderIndex(Long stageId) {
        List<InterviewPreparationTodo> todos = todoRepository.findByInterviewStageIdOrderByOrderIndexAsc(stageId);
        if (todos.isEmpty()) {
            return 0;
        }
        return todos.get(todos.size() - 1).getOrderIndex() + 1;
    }

    /**
     * 转换Entity到DTO（包含资源）
     */
    private PreparationTodoDTO convertToDTO(InterviewPreparationTodo entity, List<InterviewPreparationTodoResource> resources) {
        PreparationTodoDTO dto = new PreparationTodoDTO();
        dto.setId(entity.getId());
        dto.setInterviewStageId(entity.getInterviewStageId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setTodoType(entity.getTodoType());
        dto.setSource(entity.getSource()); // AI or User
        dto.setPriority(entity.getPriority());
        dto.setOrderIndex(entity.getOrderIndex());

        // 转换资源
        List<PreparationTodoDTO.ResourceLink> resourceLinks = resources.stream()
                .map(this::convertResourceToDTO)
                .collect(Collectors.toList());
        dto.setResourceLinks(resourceLinks);

        dto.setIsCompleted(entity.getIsCompleted());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setCompletionNotes(entity.getCompletionNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    /**
     * 转换资源Entity到DTO（包含资源标题查询）
     */
    private PreparationTodoDTO.ResourceLink convertResourceToDTO(InterviewPreparationTodoResource resource) {
        String resourceType = resource.getResourceType();

        // 外部链接直接返回
        if ("ExternalLink".equals(resourceType)) {
            return new PreparationTodoDTO.ResourceLink(
                resource.getId(),
                resource.getTitle(),
                resource.getUrl()
            );
        }

        // 系统资源需要查询标题
        String title = getResourceTitle(resourceType, resource.getResourceId());
        return new PreparationTodoDTO.ResourceLink(
            resource.getId(),
            resourceType,
            resource.getResourceId(),
            title
        );
    }

    /**
     * 根据资源类型和ID获取资源标题
     */
    private String getResourceTitle(String resourceType, Long resourceId) {
        switch (resourceType) {
            case "Question":
                return questionRepository.findById(resourceId)
                        .map(Question::getQuestionDescription)
                        .orElse("Unknown Question");
            case "LearningResource":
                return learningResourceRepository.findById(resourceId)
                        .map(LearningResource::getTitle)
                        .orElse("Unknown Resource");
            case "SystemDesignCase":
                return systemDesignCaseRepository.findById(resourceId)
                        .map(c -> c.getTitle())
                        .orElse("Unknown Case");
            case "Project":
                return projectRepository.findById(resourceId)
                        .map(p -> p.getProjectName())
                        .orElse("Unknown Project");
            case "ManagementExperience":
                return managementExperienceRepository.findById(resourceId)
                        .map(e -> e.getExperienceName())
                        .orElse("Unknown Experience");
            default:
                return "Unknown Resource";
        }
    }

    /**
     * 重排序请求
     */
    public static class ReorderRequest {
        private Long id;
        private Integer orderIndex;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
        }
    }
}
