package com.growing.app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PreparationTodoDTO {
    private Long id;
    private Long interviewStageId;
    private Long userId;
    private String title;
    private String description;
    private String todoType; // General, StudyMaterial, Practice, ProjectReview, Checklist
    private String source; // AI or User
    private Integer priority; // 0-5
    private Integer orderIndex;
    private List<ResourceLink> resourceLinks; // 解析后的资源链接列表
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private String completionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Checklist merge fields
    private Boolean fromChecklist; // 是否来自checklist
    private Boolean hasDetails; // 是否有详细信息（是否已展开）
    private String checklistCategory; // checklist分类
    private Boolean checklistPriority; // checklist优先级标记

    // 内部类：资源（系统资源或外部链接）
    public static class ResourceLink {
        private Long id; // 资源关联ID
        private String resourceType; // Question/LearningResource/SystemDesignCase/Project/ManagementExperience/ExternalLink
        private Long resourceId; // 系统资源ID（外部链接时为null）
        private String title; // 资源标题（系统资源从数据库获取，外部链接手动输入）
        private String url; // 外部链接URL（仅ExternalLink类型）

        public ResourceLink() {}

        // 系统资源构造函数
        public ResourceLink(Long id, String resourceType, Long resourceId, String title) {
            this.id = id;
            this.resourceType = resourceType;
            this.resourceId = resourceId;
            this.title = title;
        }

        // 外部链接构造函数
        public ResourceLink(Long id, String title, String url) {
            this.id = id;
            this.resourceType = "ExternalLink";
            this.title = title;
            this.url = url;
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

        public Long getResourceId() {
            return resourceId;
        }

        public void setResourceId(Long resourceId) {
            this.resourceId = resourceId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInterviewStageId() {
        return interviewStageId;
    }

    public void setInterviewStageId(Long interviewStageId) {
        this.interviewStageId = interviewStageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTodoType() {
        return todoType;
    }

    public void setTodoType(String todoType) {
        this.todoType = todoType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<ResourceLink> getResourceLinks() {
        return resourceLinks;
    }

    public void setResourceLinks(List<ResourceLink> resourceLinks) {
        this.resourceLinks = resourceLinks;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getFromChecklist() {
        return fromChecklist;
    }

    public void setFromChecklist(Boolean fromChecklist) {
        this.fromChecklist = fromChecklist;
    }

    public Boolean getHasDetails() {
        return hasDetails;
    }

    public void setHasDetails(Boolean hasDetails) {
        this.hasDetails = hasDetails;
    }

    public String getChecklistCategory() {
        return checklistCategory;
    }

    public void setChecklistCategory(String checklistCategory) {
        this.checklistCategory = checklistCategory;
    }

    public Boolean getChecklistPriority() {
        return checklistPriority;
    }

    public void setChecklistPriority(Boolean checklistPriority) {
        this.checklistPriority = checklistPriority;
    }
}
