package com.growing.app.dto;

import java.time.LocalDateTime;

public class UserTemplateNoteDTO {

    private Long id;
    private Long templateId;
    private Long userId;
    private String noteContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public UserTemplateNoteDTO() {
    }

    public UserTemplateNoteDTO(Long id, Long templateId, Long userId, String noteContent,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.templateId = templateId;
        this.userId = userId;
        this.noteContent = noteContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
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
}
