package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 简历主表实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "resumes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "version_name"}),
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_default", columnList = "user_id, is_default")
       })
@Data
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "version_name", nullable = false, length = 100)
    private String versionName;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Column(name = "career_objective", columnDefinition = "TEXT")
    private String careerObjective;

    // Contact & Links
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "other_links", columnDefinition = "JSON")
    private String otherLinks;

    // Languages & Hobbies
    @Column(name = "languages", columnDefinition = "JSON")
    private String languages;

    @Column(name = "hobbies", columnDefinition = "TEXT")
    private String hobbies;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
