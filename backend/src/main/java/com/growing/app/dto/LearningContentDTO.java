package com.growing.app.dto;

import com.growing.app.model.LearningContent.ContentType;
import com.growing.app.model.LearningContent.Visibility;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LearningContentDTO {

    private Long id;
    private Long focusAreaId;
    private String focusAreaName;
    private Long stageId;
    private String stageName;
    private ContentType contentType;
    private String title;
    private String description;
    private String url;
    private String author;
    private String contentData; // JSON字符串
    private Integer sortOrder;
    private Visibility visibility;
    private Long createdByUserId;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 当content_type为question时，关联的题目信息
    private Long questionId;
    private String difficulty;
}
