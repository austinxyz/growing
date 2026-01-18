package com.growing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.FocusAreaBriefDTO;
import com.growing.app.dto.InterviewStageDTO;
import com.growing.app.entity.InterviewStage;
import com.growing.app.entity.JobApplication;
import com.growing.app.model.FocusArea;
import com.growing.app.model.Skill;
import com.growing.app.repository.FocusAreaRepository;
import com.growing.app.repository.InterviewStageRepository;
import com.growing.app.repository.JobApplicationRepository;
import com.growing.app.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewStageService {

    @Autowired
    private InterviewStageRepository interviewStageRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private FocusAreaRepository focusAreaRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private com.growing.app.repository.InterviewPreparationTodoRepository preparationTodoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<InterviewStageDTO> getStagesByApplicationId(Long applicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此求职申请的面试阶段");
        }

        List<InterviewStage> stages = interviewStageRepository.findByJobApplicationIdOrderByStageOrder(applicationId);

        // Collect all focusAreaIds from all stages for batch query
        List<Long> allFocusAreaIds = stages.stream()
                .flatMap(stage -> {
                    try {
                        if (stage.getFocusAreaIds() != null) {
                            List<Long> ids = objectMapper.readValue(stage.getFocusAreaIds(),
                                    new TypeReference<List<Long>>() {});
                            return ids.stream();
                        }
                    } catch (Exception e) {
                        // Ignore parse errors
                    }
                    return java.util.stream.Stream.empty();
                })
                .distinct()
                .collect(Collectors.toList());

        // Batch query all focus areas
        java.util.Map<Long, FocusArea> focusAreaMap = allFocusAreaIds.isEmpty()
                ? java.util.Collections.emptyMap()
                : focusAreaRepository.findAllById(allFocusAreaIds).stream()
                        .collect(Collectors.toMap(FocusArea::getId, fa -> fa));

        // Convert to DTOs with cached focus areas
        return stages.stream()
                .map(stage -> convertToDTO(stage, focusAreaMap))
                .collect(Collectors.toList());
    }

    @Transactional
    public InterviewStageDTO createStage(Long userId, InterviewStageDTO dto) {
        JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此求职申请添加面试阶段");
        }

        InterviewStage stage = new InterviewStage();
        stage.setJobApplicationId(dto.getJobApplicationId());
        stage.setStageName(dto.getStageName());
        stage.setStageOrder(dto.getStageOrder() != null ? dto.getStageOrder() : 0);
        stage.setPreparationNotes(dto.getPreparationNotes());

        // Convert JSON fields
        try {
            if (dto.getSkillIds() != null) {
                stage.setSkillIds(objectMapper.writeValueAsString(dto.getSkillIds()));
            }
            if (dto.getFocusAreaIds() != null) {
                stage.setFocusAreaIds(objectMapper.writeValueAsString(dto.getFocusAreaIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        stage = interviewStageRepository.save(stage);
        return convertToDTO(stage);
    }

    @Transactional
    public InterviewStageDTO updateStage(Long id, Long userId, InterviewStageDTO dto) {
        InterviewStage stage = interviewStageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此面试阶段");
        }

        stage.setStageName(dto.getStageName());
        stage.setStageOrder(dto.getStageOrder());
        stage.setPreparationNotes(dto.getPreparationNotes());

        // Update JSON fields
        try {
            if (dto.getSkillIds() != null) {
                stage.setSkillIds(objectMapper.writeValueAsString(dto.getSkillIds()));
            }
            if (dto.getFocusAreaIds() != null) {
                stage.setFocusAreaIds(objectMapper.writeValueAsString(dto.getFocusAreaIds()));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON字段格式错误: " + e.getMessage());
        }

        stage = interviewStageRepository.save(stage);
        return convertToDTO(stage);
    }

    @Transactional
    public void deleteStage(Long id, Long userId) {
        InterviewStage stage = interviewStageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此面试阶段");
        }

        interviewStageRepository.delete(stage);
    }

    /**
     * 生成面试准备AI Prompt
     * 包含JD、面试阶段信息、关联的Skills和Focus Areas
     */
    public String generatePreparationPrompt(Long stageId, Long userId) {
        InterviewStage stage = interviewStageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试阶段不存在"));

        JobApplication application = jobApplicationRepository.findById(stage.getJobApplicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此面试阶段");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("# 面试准备分析任务\n\n");
        prompt.append("请根据以下信息为面试阶段生成准备清单和建议。\n\n");

        // Job Description
        prompt.append("## 职位信息\n\n");
        prompt.append("**职位名称**: ").append(application.getPositionName()).append("\n");
        if (application.getPositionLevel() != null) {
            prompt.append("**职位级别**: ").append(application.getPositionLevel()).append("\n");
        }
        prompt.append("\n");

        if (application.getQualifications() != null && !application.getQualifications().isEmpty()) {
            prompt.append("### Qualifications (技能要求)\n\n");
            prompt.append(application.getQualifications()).append("\n\n");
        }

        if (application.getResponsibilities() != null && !application.getResponsibilities().isEmpty()) {
            prompt.append("### Responsibilities (岗位职责)\n\n");
            prompt.append(application.getResponsibilities()).append("\n\n");
        }

        // Interview Stage Info
        prompt.append("## 面试阶段信息\n\n");
        prompt.append("**阶段名称**: ").append(stage.getStageName()).append("\n");
        prompt.append("**阶段顺序**: 第 ").append(stage.getStageOrder()).append(" 轮\n\n");

        // Skills
        if (stage.getSkillIds() != null) {
            try {
                List<Long> skillIds = objectMapper.readValue(stage.getSkillIds(),
                        new TypeReference<List<Long>>() {});
                if (!skillIds.isEmpty()) {
                    prompt.append("### 考察的技能 (Skills)\n\n");
                    List<Skill> skills = skillRepository.findAllById(skillIds);
                    for (Skill skill : skills) {
                        prompt.append("- **").append(skill.getName()).append("**");
                        if (skill.getDescription() != null) {
                            prompt.append(": ").append(skill.getDescription());
                        }
                        prompt.append("\n");
                    }
                    prompt.append("\n");
                }
            } catch (Exception e) {
                // Ignore parsing error
            }
        }

        // Focus Areas
        if (stage.getFocusAreaIds() != null) {
            try {
                List<Long> focusAreaIds = objectMapper.readValue(stage.getFocusAreaIds(),
                        new TypeReference<List<Long>>() {});
                if (!focusAreaIds.isEmpty()) {
                    prompt.append("### 重点关注领域 (Focus Areas)\n\n");
                    List<FocusArea> focusAreas = focusAreaRepository.findAllById(focusAreaIds);
                    for (FocusArea fa : focusAreas) {
                        prompt.append("- **").append(fa.getName()).append("**");
                        if (fa.getDescription() != null) {
                            prompt.append(": ").append(fa.getDescription());
                        }
                        prompt.append("\n");
                    }
                    prompt.append("\n");
                }
            } catch (Exception e) {
                // Ignore parsing error
            }
        }

        // Existing Preparation Notes
        if (stage.getPreparationNotes() != null && !stage.getPreparationNotes().isEmpty()) {
            prompt.append("### 现有准备重点\n\n");
            prompt.append(stage.getPreparationNotes()).append("\n\n");
        }

        // Instructions
        prompt.append("## 任务要求\n\n");
        prompt.append("请基于以上信息生成：\n\n");
        prompt.append("1. **面试准备清单** (Markdown格式，带checkbox)\n");
        prompt.append("   - 针对该阶段的具体准备事项\n");
        prompt.append("   - 优先级标注（⭐高优先级）\n");
        prompt.append("   - 每项清单要具体、可执行\n\n");
        prompt.append("2. **建议增强的Focus Areas** (如果Skills关联了Focus Areas)\n");
        prompt.append("   - 列出建议深入准备的Focus Area名称和ID\n");
        prompt.append("   - 说明为什么这些领域在此阶段重要\n\n");
        prompt.append("3. **注意事项和技巧**\n");
        prompt.append("   - 面试中的常见陷阱\n");
        prompt.append("   - 回答问题的策略\n");
        prompt.append("   - 如何展示相关经验\n\n");
        prompt.append("请以Markdown格式输出，结构清晰，便于复制到preparation_notes字段。\n");

        return prompt.toString();
    }

    /**
     * 生成整个职位的面试准备AI Prompt
     * 包含JD + 所有面试阶段的信息
     */
    public String generateJobPreparationPrompt(Long jobApplicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "求职申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此求职申请");
        }

        // Get all interview stages for this job
        List<InterviewStage> stages = interviewStageRepository
                .findByJobApplicationIdOrderByStageOrder(jobApplicationId);

        StringBuilder prompt = new StringBuilder();
        prompt.append("# 面试准备综合分析任务\n\n");
        prompt.append("请根据以下职位和面试流程信息，为每个面试阶段生成详细的准备清单。\n\n");

        // Job Description
        prompt.append("## 职位信息\n\n");
        prompt.append("**职位名称**: ").append(application.getPositionName()).append("\n");
        if (application.getPositionLevel() != null) {
            prompt.append("**职位级别**: ").append(application.getPositionLevel()).append("\n");
        }
        prompt.append("\n");

        if (application.getQualifications() != null && !application.getQualifications().isEmpty()) {
            prompt.append("### Qualifications (技能要求)\n\n");
            prompt.append(application.getQualifications()).append("\n\n");
        }

        if (application.getResponsibilities() != null && !application.getResponsibilities().isEmpty()) {
            prompt.append("### Responsibilities (岗位职责)\n\n");
            prompt.append(application.getResponsibilities()).append("\n\n");
        }

        // All Interview Stages
        if (!stages.isEmpty()) {
            prompt.append("## 面试流程\n\n");
            prompt.append("本职位面试共").append(stages.size()).append("个阶段：\n\n");

            for (InterviewStage stage : stages) {
                prompt.append("### 阶段 ").append(stage.getStageOrder())
                        .append(": ").append(stage.getStageName()).append("\n\n");

                // Skills
                if (stage.getSkillIds() != null) {
                    try {
                        List<Long> skillIds = objectMapper.readValue(stage.getSkillIds(),
                                new TypeReference<List<Long>>() {});
                        if (!skillIds.isEmpty()) {
                            prompt.append("**考察技能**: ");
                            List<Skill> skills = skillRepository.findAllById(skillIds);
                            prompt.append(skills.stream()
                                    .map(Skill::getName)
                                    .collect(Collectors.joining(", ")));
                            prompt.append("\n\n");
                        }
                    } catch (Exception e) {
                        // Ignore parsing error
                    }
                }

                // Focus Areas
                if (stage.getFocusAreaIds() != null) {
                    try {
                        List<Long> focusAreaIds = objectMapper.readValue(stage.getFocusAreaIds(),
                                new TypeReference<List<Long>>() {});
                        if (!focusAreaIds.isEmpty()) {
                            prompt.append("**重点领域**: ");
                            List<FocusArea> focusAreas = focusAreaRepository.findAllById(focusAreaIds);
                            prompt.append(focusAreas.stream()
                                    .map(FocusArea::getName)
                                    .collect(Collectors.joining(", ")));
                            prompt.append("\n\n");
                        }
                    } catch (Exception e) {
                        // Ignore parsing error
                    }
                }

                // Existing Preparation Notes
                if (stage.getPreparationNotes() != null && !stage.getPreparationNotes().isEmpty()) {
                    prompt.append("**已有准备重点**:\n\n");
                    prompt.append(stage.getPreparationNotes()).append("\n\n");
                }

                prompt.append("---\n\n");
            }
        }

        // Instructions
        prompt.append("## 任务要求\n\n");
        prompt.append("请为每个面试阶段生成：\n\n");
        prompt.append("1. **准备清单** (Markdown checkbox格式，`- [ ]`)\n");
        prompt.append("   - 具体、可执行的准备事项\n");
        prompt.append("   - 用⭐标注高优先级项\n");
        prompt.append("   - 包含复习的知识点、练习的题目类型等\n\n");
        prompt.append("2. **建议的Focus Areas**\n");
        prompt.append("   - 列出该阶段应重点准备的Focus Area（基于技能要求）\n");
        prompt.append("   - 说明为什么这些领域重要\n\n");
        prompt.append("3. **面试技巧**\n");
        prompt.append("   - 该阶段的常见问题类型\n");
        prompt.append("   - 回答策略和注意事项\n");
        prompt.append("   - 如何展示相关项目经验\n\n");
        prompt.append("## 输出格式\n\n");
        prompt.append("请按照以下格式输出（便于后续更新到各阶段的preparation_notes字段）：\n\n");
        prompt.append("```markdown\n");
        prompt.append("## 阶段1: [阶段名称]\n\n");
        prompt.append("### 准备清单\n");
        prompt.append("- [ ] 准备事项1\n");
        prompt.append("- [ ] ⭐ 重要事项2\n\n");
        prompt.append("### 建议Focus Areas\n");
        prompt.append("- **[Focus Area名称]**: 原因说明\n\n");
        prompt.append("### 面试技巧\n");
        prompt.append("- 技巧1\n");
        prompt.append("- 技巧2\n");
        prompt.append("```\n");

        return prompt.toString();
    }

    private InterviewStageDTO convertToDTO(InterviewStage stage) {
        // Call overloaded method with empty map (for single-stage conversion)
        return convertToDTO(stage, Collections.emptyMap());
    }

    private InterviewStageDTO convertToDTO(InterviewStage stage, java.util.Map<Long, FocusArea> focusAreaMap) {
        InterviewStageDTO dto = new InterviewStageDTO();
        dto.setId(stage.getId());
        dto.setJobApplicationId(stage.getJobApplicationId());
        dto.setStageName(stage.getStageName());
        dto.setStageOrder(stage.getStageOrder());
        dto.setPreparationNotes(stage.getPreparationNotes());
        dto.setCreatedAt(stage.getCreatedAt());
        dto.setUpdatedAt(stage.getUpdatedAt());

        // Parse JSON fields
        try {
            if (stage.getSkillIds() != null) {
                dto.setSkillIds(objectMapper.readValue(stage.getSkillIds(),
                        new TypeReference<List<Long>>() {}));
            }
            if (stage.getFocusAreaIds() != null) {
                dto.setFocusAreaIds(objectMapper.readValue(stage.getFocusAreaIds(),
                        new TypeReference<List<Long>>() {}));
            }
        } catch (Exception e) {
            dto.setSkillIds(Collections.emptyList());
            dto.setFocusAreaIds(Collections.emptyList());
        }

        // DTO Completeness Checklist: Populate skillNames
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            List<String> skillNames = skillRepository.findAllById(dto.getSkillIds()).stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
            dto.setSkillNames(skillNames);
        } else {
            dto.setSkillNames(Collections.emptyList());
        }

        // DTO Completeness Checklist: Populate focusAreas with names
        if (dto.getFocusAreaIds() != null && !dto.getFocusAreaIds().isEmpty()) {
            List<FocusAreaBriefDTO> focusAreas = dto.getFocusAreaIds().stream()
                    .map(focusAreaId -> {
                        // Use cached focusAreaMap if available, otherwise query database
                        FocusArea focusArea = focusAreaMap.isEmpty()
                                ? focusAreaRepository.findById(focusAreaId).orElse(null)
                                : focusAreaMap.get(focusAreaId);
                        return focusArea != null ? convertFocusAreaToBriefDTO(focusArea) : null;
                    })
                    .filter(fa -> fa != null)
                    .collect(Collectors.toList());
            dto.setFocusAreas(focusAreas);
        } else {
            dto.setFocusAreas(Collections.emptyList());
        }

        // Checklist items are now in interview_preparation_todos table
        // They are loaded separately via PreparationTodoService if needed
        dto.setChecklistItems(Collections.emptyList());

        // DTO Completeness Checklist: Populate checklistCount from interview_preparation_todos
        int checklistCount = preparationTodoRepository.countByInterviewStageId(stage.getId());
        dto.setChecklistCount(checklistCount);

        return dto;
    }

    private FocusAreaBriefDTO convertFocusAreaToBriefDTO(FocusArea focusArea) {
        FocusAreaBriefDTO dto = new FocusAreaBriefDTO();
        dto.setId(focusArea.getId());
        dto.setName(focusArea.getName());
        dto.setDescription(focusArea.getDescription());

        // FocusArea uses @ManyToOne Skill relationship
        if (focusArea.getSkill() != null) {
            dto.setSkillId(focusArea.getSkill().getId());
            dto.setSkillName(focusArea.getSkill().getName());
        }

        return dto;
    }

    // convertChecklistToDTO removed - checklist is now merged into interview_preparation_todos table
}
