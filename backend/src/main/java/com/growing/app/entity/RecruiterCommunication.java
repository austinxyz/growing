package com.growing.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 招聘沟通记录实体类
 * Phase 7: 求职管理模块
 */
@Entity
@Table(name = "recruiter_communications",
       indexes = {
           @Index(name = "idx_recruiter", columnList = "recruiter_id"),
           @Index(name = "idx_date", columnList = "communication_date")
       })
@Data
public class RecruiterCommunication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recruiter_id", nullable = false)
    private Long recruiterId;

    @Column(name = "job_application_id")
    private Long jobApplicationId;

    @Column(name = "communication_date", nullable = false)
    private LocalDateTime communicationDate;

    @Column(name = "communication_method", nullable = false, length = 50)
    private String communicationMethod;

    @Column(name = "communication_content", columnDefinition = "TEXT")
    private String communicationContent;

    @Column(name = "next_action", columnDefinition = "TEXT")
    private String nextAction;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
