package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 公司档案实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "companies",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "company_name"}),
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_industry", columnList = "industry")
       })
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_description", columnDefinition = "TEXT")
    private String companyDescription;

    @Column(name = "company_culture", columnDefinition = "TEXT")
    private String companyCulture;

    @Column(name = "location")
    private String location;

    @Column(name = "company_size", length = 50)
    private String companySize;

    @Column(name = "industry", length = 100)
    private String industry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
