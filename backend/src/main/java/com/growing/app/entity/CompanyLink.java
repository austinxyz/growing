package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 公司有用链接实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "company_links",
       indexes = @Index(name = "idx_company", columnList = "company_id"))
@Data
public class CompanyLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "link_title", nullable = false)
    private String linkTitle;

    @Column(name = "link_url", nullable = false, length = 500)
    private String linkUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
