package com.growing.app.controller;

import com.growing.app.dto.AIJobAnalysisDTO;
import com.growing.app.dto.GeneratePromptRequest;
import com.growing.app.dto.GeneratePromptResponse;
import com.growing.app.dto.SaveAnalysisRequest;
import com.growing.app.service.AIJobAnalysisService;
import com.growing.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI Job Analysis Controller
 */
@RestController
@RequestMapping("/api/ai-job-analysis")
@RequiredArgsConstructor
public class AIJobAnalysisController {

    private final AIJobAnalysisService aiJobAnalysisService;
    private final AuthService authService;

    /**
     * 生成AI分析Prompt
     */
    @PostMapping("/generate-prompt")
    public ResponseEntity<GeneratePromptResponse> generatePrompt(
            @Valid @RequestBody GeneratePromptRequest request,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token);
        GeneratePromptResponse response = aiJobAnalysisService.generatePrompt(
                request.getJobApplicationId(),
                userId
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 保存AI分析结果（支持upsert）
     */
    @PostMapping("/save")
    public ResponseEntity<AIJobAnalysisDTO> saveAnalysis(
            @Valid @RequestBody SaveAnalysisRequest request,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token);
        AIJobAnalysisDTO result = aiJobAnalysisService.saveAnalysisResult(request, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询Job Application的所有分析记录
     */
    @GetMapping("/job-application/{jobApplicationId}")
    public ResponseEntity<List<AIJobAnalysisDTO>> getByJobApplication(
            @PathVariable Long jobApplicationId,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token);
        List<AIJobAnalysisDTO> analyses = aiJobAnalysisService.getByJobApplication(jobApplicationId, userId);
        return ResponseEntity.ok(analyses);
    }

    /**
     * 获取单个分析详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AIJobAnalysisDTO> getById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token);
        AIJobAnalysisDTO analysis = aiJobAnalysisService.getById(id, userId);
        return ResponseEntity.ok(analysis);
    }

    /**
     * 删除分析记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token);
        aiJobAnalysisService.deleteAnalysis(id, userId);
        return ResponseEntity.noContent().build();
    }
}
