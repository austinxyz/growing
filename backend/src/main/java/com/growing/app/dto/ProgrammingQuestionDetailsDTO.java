package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProgrammingQuestionDetailsDTO {

    private Long id;
    private Long questionId;

    // LeetCode题号
    private Integer leetcodeNumber;

    // 外部资源链接
    private String leetcodeUrl;
    private String labuladongUrl;
    private String hellointerviewUrl;

    // 重要性标记
    private Boolean isImportant;

    // 算法技巧标签
    private List<String> tags;

    // 复杂度和相关题目
    private String complexity;
    private List<SimilarQuestion> similarQuestions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class SimilarQuestion {
        private Long id;
        private String title;
        private String titleSlug;   // LeetCode URL路径
        private String difficulty;  // Easy/Medium/Hard
    }
}
