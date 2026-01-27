package com.growing.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 求职管理统计数据 DTO
 * Phase 7 - Dashboard statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchStatsDTO {
    private Long resumeCount;              // 简历数量
    private Long projectCount;             // 项目经验数量
    private Long managementCount;          // 人员管理案例数量
    private Long companyCount;             // 公司数量
    private Long jobApplicationCount;      // 职位申请数量
}
