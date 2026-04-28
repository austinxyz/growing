package com.growing.app.dto;

/**
 * 面试进展卡片的优先级。Ordinal 顺序决定默认排序权重（OFFER_DEADLINE 最优先）。
 */
public enum PriorityLevel {
    OFFER_DEADLINE,
    STALLED,
    SCHEDULED,
    WAITING
}
