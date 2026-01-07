package com.growing.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.AIJobAnalysisDTO;
import com.growing.app.dto.GeneratePromptResponse;
import com.growing.app.dto.SaveAnalysisRequest;
import com.growing.app.entity.AIJobAnalysis;
import com.growing.app.entity.JobApplication;
import com.growing.app.entity.Resume;
import com.growing.app.repository.AIJobAnalysisRepository;
import com.growing.app.repository.JobApplicationRepository;
import com.growing.app.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AI Job Analysis Service
 */
@Service
@RequiredArgsConstructor
public class AIJobAnalysisService {

    private final AIJobAnalysisRepository analysisRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    /**
     * 生成AI分析Prompt
     */
    public GeneratePromptResponse generatePrompt(Long jobApplicationId, Long userId) {
        // 1. 验证Job Application存在且属于当前用户
        JobApplication job = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job application not found"));

        if (!job.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your job application");
        }

        // 2. 生成Prompt
        String prompt = buildPrompt(jobApplicationId, userId);
        String setupInstructions = buildSetupInstructions();

        return new GeneratePromptResponse(prompt, setupInstructions, jobApplicationId, userId);
    }

    /**
     * 保存AI分析结果（支持upsert）
     */
    @Transactional
    public AIJobAnalysisDTO saveAnalysisResult(SaveAnalysisRequest request, Long currentUserId) {
        // 1. 验证权限
        if (!request.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User ID mismatch");
        }

        // 2. 验证Job Application存在且属于当前用户
        JobApplication job = jobApplicationRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job application not found"));

        if (!job.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your job application");
        }

        // 3. 获取用户的default resume
        Resume resume = resumeRepository.findByUserIdAndIsDefaultTrue(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No default resume found"));

        // 4. Upsert逻辑：查找已有记录
        Optional<AIJobAnalysis> existing = analysisRepository
                .findFirstByJobApplicationIdAndResumeIdAndUserIdOrderByCreatedAtDesc(
                        request.getJobId(), resume.getId(), currentUserId
                );

        AIJobAnalysis analysis;
        if (existing.isPresent()) {
            // 更新已有记录
            analysis = existing.get();
        } else {
            // 创建新记录
            analysis = new AIJobAnalysis();
            analysis.setUserId(currentUserId);
            analysis.setJobApplicationId(request.getJobId());
            analysis.setResumeId(resume.getId());
            analysis.setGeneratedPrompt("");  // 不需要保存Prompt
        }

        // 5. 保存分析结果
        analysis.setAiAnalysisResult(request.getAnalysisResult());

        // 6. 从JSON提取metadata
        try {
            AIJobAnalysisDTO.AnalysisMetadata metadata = extractMetadata(request.getAnalysisResult());
            analysis.setAnalysisMetadata(objectMapper.writeValueAsString(metadata));
        } catch (Exception e) {
            // 如果提取失败，记录日志但继续保存
            System.err.println("Failed to extract metadata: " + e.getMessage());
        }

        // 7. 更新状态
        analysis.setStatus("completed");
        analysis = analysisRepository.save(analysis);

        return convertToDTO(analysis);
    }

    /**
     * 查询Job Application的所有分析记录
     */
    public List<AIJobAnalysisDTO> getByJobApplication(Long jobApplicationId, Long userId) {
        // 验证Job Application属于当前用户
        JobApplication job = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job application not found"));

        if (!job.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your job application");
        }

        List<AIJobAnalysis> analyses = analysisRepository.findByJobApplicationIdOrderByCreatedAtDesc(jobApplicationId);
        return analyses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个分析详情
     */
    public AIJobAnalysisDTO getById(Long id, Long userId) {
        AIJobAnalysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found"));

        if (!analysis.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your analysis");
        }

        return convertToDTO(analysis);
    }

    /**
     * 删除分析记录
     */
    @Transactional
    public void deleteAnalysis(Long id, Long userId) {
        AIJobAnalysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found"));

        if (!analysis.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your analysis");
        }

        analysisRepository.delete(analysis);
    }

    // ============ Private Helper Methods ============

    /**
     * 构建Prompt内容
     */
    private String buildPrompt(Long jobId, Long userId) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("请帮我分析job ").append(jobId).append("和用户").append(userId).append("的简历匹配度，并保存到数据库\n\n");
        prompt.append("执行步骤：\n");
        prompt.append("1. 调用 /resume-analysis skill分析 job_id=").append(jobId).append(", user_id=").append(userId).append("\n");
        prompt.append("2. 获取JSON结果后，运行：\n\n");

        prompt.append("```bash\n");
        prompt.append("ANALYSIS_JSON='<粘贴上一步的JSON结果>'\n\n");

        prompt.append("curl -X POST http://localhost:8082/api/ai-job-analysis/save \\\n");
        prompt.append("  -H \"Authorization: Bearer $(cat ~/.growing-token)\" \\\n");
        prompt.append("  -H \"Content-Type: application/json\" \\\n");
        prompt.append("  -d \"{\\\"jobId\\\": ").append(jobId).append(", \\\"userId\\\": ").append(userId).append(", \\\"analysisResult\\\": $(echo \\\"$ANALYSIS_JSON\\\" | jq -c .)}\"\n\n");

        prompt.append("# 检查结果\n");
        prompt.append("if [ $? -eq 0 ]; then\n");
        prompt.append("  echo \"✅ 分析结果已保存到数据库！\"\n");
        prompt.append("else\n");
        prompt.append("  echo \"❌ 保存失败，请检查网络或Token是否过期\"\n");
        prompt.append("fi\n");
        prompt.append("```\n");

        return prompt.toString();
    }

    /**
     * 构建首次使用的配置说明
     */
    private String buildSetupInstructions() {
        return "首次使用前，请在终端运行以下命令（只需运行一次）：\n" +
               "echo \"your-jwt-token-here\" > ~/.growing-token\n" +
               "chmod 600 ~/.growing-token\n\n" +
               "注意：请将\"your-jwt-token-here\"替换为你登录后获取的实际Token";
    }

    /**
     * 从JSON中提取metadata
     */
    private AIJobAnalysisDTO.AnalysisMetadata extractMetadata(String jsonResult) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResult);

        AIJobAnalysisDTO.AnalysisMetadata metadata = new AIJobAnalysisDTO.AnalysisMetadata();
        metadata.setSkillMatchScore(root.path("matchScore").asInt(0));
        metadata.setExperienceMatchScore(root.path("experienceMatch").path("score").asInt(0));
        metadata.setOverallScore(root.path("matchScore").asInt(0));

        // 提取strengths和weaknesses
        List<String> strengths = objectMapper.convertValue(
                root.path("strengths"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        metadata.setKeyStrengths(strengths);

        List<String> improvements = objectMapper.convertValue(
                root.path("improvements"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        metadata.setKeyWeaknesses(improvements);

        // 根据分数设置推荐等级
        int score = metadata.getOverallScore();
        if (score >= 90) {
            metadata.setRecommendation("Strong Match");
        } else if (score >= 70) {
            metadata.setRecommendation("Good Match");
        } else if (score >= 50) {
            metadata.setRecommendation("Fair Match");
        } else {
            metadata.setRecommendation("Poor Match");
        }

        return metadata;
    }

    /**
     * 转换为DTO
     */
    private AIJobAnalysisDTO convertToDTO(AIJobAnalysis entity) {
        AIJobAnalysisDTO dto = new AIJobAnalysisDTO();
        dto.setId(entity.getId());
        dto.setJobApplicationId(entity.getJobApplicationId());
        dto.setResumeId(entity.getResumeId());
        dto.setGeneratedPrompt(entity.getGeneratedPrompt());
        dto.setAiAnalysisResult(entity.getAiAnalysisResult());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // 解析metadata JSON
        if (entity.getAnalysisMetadata() != null) {
            try {
                AIJobAnalysisDTO.AnalysisMetadata metadata = objectMapper.readValue(
                        entity.getAnalysisMetadata(),
                        AIJobAnalysisDTO.AnalysisMetadata.class
                );
                dto.setMetadata(metadata);
            } catch (Exception e) {
                System.err.println("Failed to parse metadata: " + e.getMessage());
            }
        }

        return dto;
    }
}
