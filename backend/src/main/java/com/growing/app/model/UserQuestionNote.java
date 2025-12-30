package com.growing.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_question_notes",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_question_user",
               columnNames = {"question_id", "user_id"}
       ))
@Data
public class UserQuestionNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "note_content", nullable = false, columnDefinition = "TEXT")
    private String noteContent;

    /**
     * 关联的知识点ID列表
     * JSON格式: [1, 2, 3]
     */
    @Column(name = "related_knowledge_point_ids", columnDefinition = "JSON")
    private String relatedKnowledgePointIds;

    @Column(name = "core_strategy", columnDefinition = "TEXT")
    private String coreStrategy;

    @Column(name = "is_priority", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isPriority = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
