package com.growing.app.dto;

import lombok.Data;

import java.util.List;

/**
 * Recruiter提供的结构化信息DTO
 * Phase 7: 求职管理模块 - Enhancements
 */
@Data
public class RecruiterInsightsDTO {

    /**
     * 团队规模（如"10-15人"）
     */
    private String teamSize;

    /**
     * 团队文化（如"扁平化管理，鼓励创新"）
     */
    private String teamCulture;

    /**
     * 技术栈偏好（JSON数组格式）
     */
    private List<String> techStackPreference;

    /**
     * 面试重点（如"系统设计和编程基础"）
     */
    private String interviewFocus;

    /**
     * 内部流程tips（如"第一轮技术面试会很深入，建议准备常见算法题"）
     */
    private String processTips;
}
