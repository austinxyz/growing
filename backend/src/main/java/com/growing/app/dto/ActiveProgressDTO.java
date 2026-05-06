package com.growing.app.dto;

import java.time.LocalDate;

/**
 * 面试进展看板的精简响应 DTO。
 * 派生字段（stalled、priority、microStageLabel、nextActionLabel）由后端 ProgressCalculator 计算好。
 */
public record ActiveProgressDTO(
        Long id,
        Long companyId,
        String companyName,
        String positionName,
        String applicationStatus,
        Integer macroStageStep,
        String microStageLabel,
        Integer daysSinceApplied,
        LocalDate appliedAt,
        Integer daysSinceLastUpdate,
        PriorityLevel priorityLevel,
        NextActionType nextActionType,
        String nextActionLabel,
        LocalDate nextActionDate,
        String submissionType
) {}
