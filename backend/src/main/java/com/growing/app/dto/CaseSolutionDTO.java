package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CaseSolutionDTO {

    private Long id;
    private Long caseId;
    private String solutionName;  // 方案名称(如:方案A-基础版本,方案B-高级版本)
    private String author;

    // 6个步骤内容（Markdown格式）
    private String step1Requirements;  // 步骤1:需求澄清与功能列表
    private String step2Entities;  // 步骤2:核心实体定义
    private String step3Api;  // 步骤3:API设计
    private String step4DataFlow;  // 步骤4:数据流设计
    private String step5Architecture;  // 步骤5:高层架构设计
    private String step6DeepDive;  // 步骤6:深入讨论

    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联数据（按需包含）
    private List<SolutionDiagramDTO> diagrams;  // 配图列表
}
