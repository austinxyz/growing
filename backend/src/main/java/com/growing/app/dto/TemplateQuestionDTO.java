package com.growing.app.dto;

import lombok.Data;

/**
 * 模板-题目关联DTO
 */
@Data
public class TemplateQuestionDTO {
    private Long id;
    private Long templateId;
    private String templateTitle;  // 模板标题
    private Long questionId;
    private String questionTitle;  // 题目标题
    private String difficulty;     // 题目难度
    private Integer leetcodeNumber; // LeetCode题号（如果是编程题）
}
