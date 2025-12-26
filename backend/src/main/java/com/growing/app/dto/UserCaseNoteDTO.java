package com.growing.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCaseNoteDTO {

    private Long id;
    private Long caseId;
    private Long userId;

    // 结构化答案字段（Key Points）
    private String kpRequirement;  // 需求分析
    private String kpNfr;  // 非功能性需求
    private String kpEntity;  // 核心实体定义
    private String kpComponents;  // 关键组件
    private String kpApi;  // API设计
    private String kpObject1;  // 核心对象1设计
    private String kpObject2;  // 核心对象2设计

    // 用户答案（6个步骤，Markdown格式）
    private String step1Requirements;  // 步骤1:需求澄清与功能列表
    private String step2Entities;  // 步骤2:核心实体定义
    private String step3Api;  // 步骤3:API设计
    private String step4DataFlow;  // 步骤4:数据流设计
    private String step5Architecture;  // 步骤5:高层架构设计
    private String step6DeepDive;  // 步骤6:深入讨论

    // 用户架构图
    private String architectureDiagramUrl;  // 用户上传的架构图URL

    // 要点总结
    private String keyPoints;  // 要点总结(Markdown格式)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
