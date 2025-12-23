package com.growing.app.dto;

import lombok.Data;

/**
 * 创建/更新编程题（含详情）的请求DTO
 */
@Data
public class CreateQuestionWithDetailsRequest {

    private QuestionDTO question;
    private ProgrammingQuestionDetailsDTO programmingDetails;
}
