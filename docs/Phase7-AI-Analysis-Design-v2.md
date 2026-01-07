# Phase 7 Enhancement: AI-Driven JD-Resume Analysis (自动化版本)

## 📋 核心改进

**原流程**：用户手动复制粘贴AI结果 → 存在人为错误风险
**新流程**：Claude Code自动保存结果到数据库 → 完全自动化

---

## 🎯 **完全自动化的工作流程**

```
1. 用户在UI中选择Job Application + Resume
   ↓
2. 点击"生成AI分析Prompt"按钮
   ↓
3. Backend生成智能Prompt（包含API保存指令）
   - Prompt包含：Job ID, Resume ID, Analysis ID
   - Prompt包含：Backend API endpoint + JWT token
   - Prompt末尾包含：Claude Code保存结果的指令

<Austin> prompt保持简单，我们已经有skill for resume analysis，可能只需要job id，resume id和user id。然后调用skill
   ↓
4. 用户复制Prompt → 粘贴到Claude Code
   ↓
5. Claude Code执行AI分析
   - 分析JD和Resume的匹配度
   - 生成Markdown格式报告
   ↓
6. 🚀 Claude Code自动执行保存指令
   - 使用Bash工具调用Backend API
   - PUT /api/ai-job-analysis/{id}/save-result
   - 传递AI分析结果（Markdown）
   ↓
7. Backend接收并保存结果
   - 更新analysis_metadata (JSON)
   - 更新status为"completed"
   ↓
8. UI自动刷新或手动刷新查看结果
   - 展示Markdown渲染的完整报告
```

---

## 📝 **智能Prompt模板设计**

### 核心思路

在Prompt末尾添加**执行指令**，让Claude Code自动调用API保存结果：

```markdown
# JD-Resume匹配分析请求

**任务**: 分析以下简历与职位描述的匹配度，提供详细的评估报告。

---

## 📄 职位信息

**Job ID**: {job_id}
**公司**: {company_name}
**职位**: {position_name}
**级别**: {position_level}

### 技能要求
{qualifications}

### 岗位职责
{responsibilities}

---

## 📝 候选人简历

**Resume ID**: {resume_id}
**候选人**: {user_full_name}

### 工作经历
{experiences_markdown}

### 项目经验
{projects_markdown}

### 技能列表
{skills_markdown}

---

## 🎯 分析要求

请从以下维度进行分析，并以Markdown格式输出：

### 1. 技能匹配度（0-100分）
- 列出匹配的核心技能
- 列出缺失的关键技能
- 评估技能深度是否符合JD要求

### 2. 经验匹配度（0-100分）
- 项目经验是否与岗位职责匹配
- 工作年限是否符合要求
- 行业背景是否相关

### 3. 优势分析（列举3-5个要点）
- 候选人在哪些方面超出JD要求？
- 有哪些加分项（如领导力、跨团队协作）？

### 4. 不足之处（列举2-3个要点）
- 哪些技能需要提升？
- 经验上有哪些差距？

### 5. 简历优化建议（列举3-5个具体建议）
- 如何调整简历以更好匹配此JD？
- 哪些项目经验应该突出？
- 哪些关键词应该添加？

### 6. 综合评估
- 整体匹配分数：X/100
- 推荐等级：Strong Match / Good Match / Fair Match / Poor Match
- 一句话总结：是否推荐进入面试流程？

---

## 🤖 执行指令（Claude Code专用）

**分析完成后，请自动执行以下命令保存结果到数据库**：

```bash
# 将你的完整分析结果（Markdown格式）保存到变量
ANALYSIS_RESULT=$(cat <<'EOF'
# 你的分析结果（完整Markdown内容）
...
EOF
)

# 调用Backend API保存结果
curl -X PUT \
  'http://localhost:8082/api/ai-job-analysis/{analysis_id}/save-result' \
  -H 'Authorization: Bearer {jwt_token}' \
  -H 'Content-Type: application/json' \
  -d "{
    \"aiAnalysisResult\": $(echo "$ANALYSIS_RESULT" | jq -Rs .),
    \"metadata\": {
      \"skillMatchScore\": 85,
      \"experienceMatchScore\": 90,
      \"overallScore\": 87,
      \"keyStrengths\": [\"Strong Spring Boot experience\", \"Led team of 5\"],
      \"keyWeaknesses\": [\"Limited cloud experience\"],
      \"recommendation\": \"Good Match\"
    }
  }"

echo "✅ 分析结果已自动保存到数据库！"
```

**说明**:
- `{analysis_id}`: 替换为实际的分析记录ID = **{actual_analysis_id}**
- `{jwt_token}`: 替换为实际的JWT Token = **{actual_jwt_token}**
- `metadata`字段中的分数请根据你的实际分析结果填写

---

**输出格式要求**:
- 使用Markdown格式
- 每个维度独立成段
- 使用bullet points列举要点
- 分数使用粗体标注
- **完成分析后，立即执行上方的保存指令**
```

---

## 🗄️ 数据库设计（无变化）

```sql
CREATE TABLE `ai_job_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `job_application_id` bigint NOT NULL,
  `resume_id` bigint NOT NULL,

  `generated_prompt` text NOT NULL COMMENT '生成的AI分析Prompt',
  `ai_analysis_result` text COMMENT 'AI分析结果（Markdown格式）',
  `analysis_metadata` json DEFAULT NULL,

  `status` varchar(50) DEFAULT 'prompt_generated' COMMENT 'prompt_generated | completed',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_job` (`job_application_id`),
  KEY `idx_resume` (`resume_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `ai_job_analysis_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_job_analysis_ibfk_2` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_job_analysis_ibfk_3` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 🔧 Backend API设计

### API Endpoints

#### 1. 生成AI分析Prompt（包含自动保存指令）

**Endpoint**: `POST /api/ai-job-analysis/generate-prompt`

**Request Body**:
```json
{
  "jobApplicationId": 123,
  "resumeId": 456
}
```

**Response**:
```json
{
  "id": 789,  // Analysis ID，用于后续保存
  "jobApplicationId": 123,
  "resumeId": 456,
  "generatedPrompt": "# JD-Resume匹配分析请求\n\n...\n\n## 🤖 执行指令\n...",
  "aiAnalysisResult": null,
  "metadata": null,
  "status": "prompt_generated",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:00:00"
}
```

**业务逻辑**:
1. 验证用户权限
2. 检查是否已存在此Job+Resume的分析记录：
   - **存在且status="completed"**: 创建新版本（或返回现有记录让用户选择）
   - **存在且status="prompt_generated"**: 返回现有记录（复用）
   - **不存在**: 创建新记录
3. 生成Prompt，**在末尾插入保存指令**：
   - 替换 `{analysis_id}` 为实际的 `record.getId()`
   - 替换 `{jwt_token}` 为当前用户的JWT Token（从请求头提取）
4. 返回完整的Prompt

---

#### 2. 保存AI分析结果（Claude Code自动调用）

**Endpoint**: `PUT /api/ai-job-analysis/{id}/save-result`

**Request Headers**:
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body**:
```json
{
  "aiAnalysisResult": "# 分析结果\n\n## 技能匹配度: **85/100**\n...",
  "metadata": {
    "skillMatchScore": 85,
    "experienceMatchScore": 90,
    "overallScore": 87,
    "keyStrengths": ["Strong Spring Boot experience", "Led team of 5"],
    "keyWeaknesses": ["Limited cloud experience"],
    "recommendation": "Good Match"
  }
}
```

**Response**:
```json
{
  "id": 789,
  "jobApplicationId": 123,
  "resumeId": 456,
  "generatedPrompt": "...",
  "aiAnalysisResult": "# 分析结果\n\n...",
  "metadata": { ... },
  "status": "completed",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:15:00"
}
```

**业务逻辑**:
1. 验证JWT Token
2. 验证用户权限（Analysis记录属于当前用户）
3. 验证状态（允许 `prompt_generated` → `completed` 或 `completed` → `completed` 更新）
4. 更新 `ai_analysis_result` 和 `analysis_metadata`
5. 更新状态为 `completed`
6. 返回完整DTO

---

#### 3. 查询Job Application的所有AI分析记录

**Endpoint**: `GET /api/ai-job-analysis/job-application/{jobApplicationId}`

**Response**:
```json
[
  {
    "id": 789,
    "jobApplicationId": 123,
    "resumeId": 456,
    "status": "completed",
    "metadata": {
      "overallScore": 87,
      "recommendation": "Good Match"
    },
    "createdAt": "2024-01-06T10:00:00",
    "updatedAt": "2024-01-06T10:15:00"
  }
]
```

---

#### 4. 获取单个分析详情

**Endpoint**: `GET /api/ai-job-analysis/{id}`

**Response**:
```json
{
  "id": 789,
  "jobApplicationId": 123,
  "resumeId": 456,
  "generatedPrompt": "...",
  "aiAnalysisResult": "# 分析结果\n\n...",
  "metadata": { ... },
  "status": "completed",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:15:00"
}
```

---

#### 5. 删除分析记录

**Endpoint**: `DELETE /api/ai-job-analysis/{id}`

**Response**: `204 No Content`

---

## 💡 Prompt生成逻辑（Java代码示例）

```java
@Service
public class AIJobAnalysisService {

    @Autowired
    private AIJobAnalysisRepository analysisRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 生成AI分析Prompt（包含自动保存指令）
     */
    public AIJobAnalysisDTO generatePrompt(Long jobApplicationId, Long resumeId, Long userId, String jwtToken) {
        // 1. 验证权限
        JobApplication job = jobApplicationRepository.findById(jobApplicationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job application not found"));

        if (!job.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your job application");
        }

        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your resume");
        }

        // 2. 检查是否已存在分析记录
        Optional<AIJobAnalysis> existing = analysisRepository
            .findByJobApplicationIdAndResumeIdAndUserId(jobApplicationId, resumeId, userId);

        AIJobAnalysis analysis;
        if (existing.isPresent() && "prompt_generated".equals(existing.get().getStatus())) {
            // 复用未完成的记录
            analysis = existing.get();
        } else {
            // 创建新记录
            analysis = new AIJobAnalysis();
            analysis.setUserId(userId);
            analysis.setJobApplicationId(jobApplicationId);
            analysis.setResumeId(resumeId);
            analysis.setStatus("prompt_generated");
        }

        // 3. 生成Prompt内容
        String promptContent = buildPromptContent(job, resume);

        // 4. 添加自动保存指令
        String fullPrompt = promptContent + buildAutoSaveInstructions(analysis.getId(), jwtToken);

        analysis.setGeneratedPrompt(fullPrompt);
        analysis = analysisRepository.save(analysis);

        return convertToDTO(analysis);
    }

    /**
     * 构建Prompt主体内容
     */
    private String buildPromptContent(JobApplication job, Resume resume) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("# JD-Resume匹配分析请求\n\n");
        prompt.append("**任务**: 分析以下简历与职位描述的匹配度，提供详细的评估报告。\n\n");
        prompt.append("---\n\n");

        // === Job Information ===
        prompt.append("## 📄 职位信息\n\n");
        prompt.append("**Job ID**: ").append(job.getId()).append("\n");
        prompt.append("**公司**: ").append(job.getCompany().getCompanyName()).append("\n");
        prompt.append("**职位**: ").append(job.getPositionName()).append("\n");
        prompt.append("**级别**: ").append(job.getPositionLevel()).append("\n\n");

        prompt.append("### 技能要求\n");
        prompt.append(job.getQualifications() != null ? job.getQualifications() : "无").append("\n\n");

        prompt.append("### 岗位职责\n");
        prompt.append(job.getResponsibilities() != null ? job.getResponsibilities() : "无").append("\n\n");

        prompt.append("---\n\n");

        // === Resume Information ===
        prompt.append("## 📝 候选人简历\n\n");
        prompt.append("**Resume ID**: ").append(resume.getId()).append("\n");
        prompt.append("**候选人**: ").append(resume.getUser().getFullName()).append("\n\n");

        // Work Experiences
        prompt.append("### 工作经历\n");
        for (ResumeExperience exp : resume.getResumeExperiences()) {
            prompt.append("- **").append(exp.getCompanyName()).append("** | ")
                  .append(exp.getPosition()).append("\n");
            prompt.append("  - 时间: ").append(formatDateRange(exp.getStartDate(), exp.getEndDate())).append("\n");
            if (exp.getResponsibilities() != null) {
                prompt.append("  - 职责: ").append(exp.getResponsibilities()).append("\n");
            }
            prompt.append("\n");
        }

        // Projects (from project_experiences table)
        List<ProjectExperience> projects = projectExperienceRepository.findByUserId(resume.getUserId());
        prompt.append("### 项目经验\n");
        for (ProjectExperience proj : projects) {
            prompt.append("- **").append(proj.getProjectName()).append("**\n");
            if (proj.getTechStack() != null) {
                prompt.append("  - 技术栈: ").append(proj.getTechStack()).append("\n");
            }
            if (proj.getMyContribution() != null) {
                prompt.append("  - 贡献: ").append(proj.getMyContribution()).append("\n");
            }
            prompt.append("\n");
        }

        // Skills
        prompt.append("### 技能列表\n");
        Map<String, List<String>> skillsByCategory = resume.getResumeSkills().stream()
            .collect(Collectors.groupingBy(
                ResumeSkill::getSkillCategory,
                Collectors.mapping(ResumeSkill::getSkillName, Collectors.toList())
            ));

        for (Map.Entry<String, List<String>> entry : skillsByCategory.entrySet()) {
            prompt.append("- **").append(entry.getKey()).append("**: ")
                  .append(String.join(", ", entry.getValue())).append("\n");
        }
        prompt.append("\n");

        // Education
        prompt.append("### 教育背景\n");
        for (ResumeEducation edu : resume.getResumeEducation()) {
            prompt.append("- **").append(edu.getSchoolName()).append("** | ")
                  .append(edu.getDegree()).append(" in ").append(edu.getMajor()).append("\n");
        }
        prompt.append("\n");

        prompt.append("---\n\n");

        // === Analysis Requirements ===
        prompt.append("## 🎯 分析要求\n\n");
        prompt.append("请从以下维度进行分析，并以Markdown格式输出：\n\n");

        prompt.append("### 1. 技能匹配度（0-100分）\n");
        prompt.append("- 列出匹配的核心技能\n");
        prompt.append("- 列出缺失的关键技能\n");
        prompt.append("- 评估技能深度是否符合JD要求\n\n");

        prompt.append("### 2. 经验匹配度（0-100分）\n");
        prompt.append("- 项目经验是否与岗位职责匹配\n");
        prompt.append("- 工作年限是否符合要求\n");
        prompt.append("- 行业背景是否相关\n\n");

        prompt.append("### 3. 优势分析（列举3-5个要点）\n");
        prompt.append("- 候选人在哪些方面超出JD要求？\n");
        prompt.append("- 有哪些加分项（如领导力、跨团队协作）？\n\n");

        prompt.append("### 4. 不足之处（列举2-3个要点）\n");
        prompt.append("- 哪些技能需要提升？\n");
        prompt.append("- 经验上有哪些差距？\n\n");

        prompt.append("### 5. 简历优化建议（列举3-5个具体建议）\n");
        prompt.append("- 如何调整简历以更好匹配此JD？\n");
        prompt.append("- 哪些项目经验应该突出？\n");
        prompt.append("- 哪些关键词应该添加？\n\n");

        prompt.append("### 6. 综合评估\n");
        prompt.append("- 整体匹配分数：X/100\n");
        prompt.append("- 推荐等级：Strong Match / Good Match / Fair Match / Poor Match\n");
        prompt.append("- 一句话总结：是否推荐进入面试流程？\n\n");

        return prompt.toString();
    }

    /**
     * 构建自动保存指令（Claude Code专用）
     */
    private String buildAutoSaveInstructions(Long analysisId, String jwtToken) {
        StringBuilder instructions = new StringBuilder();

        instructions.append("---\n\n");
        instructions.append("## 🤖 执行指令（Claude Code专用）\n\n");
        instructions.append("**分析完成后，请自动执行以下命令保存结果到数据库**：\n\n");

        instructions.append("```bash\n");
        instructions.append("# 将你的完整分析结果（Markdown格式）保存到变量\n");
        instructions.append("ANALYSIS_RESULT=$(cat <<'EOF'\n");
        instructions.append("# 在这里粘贴你的完整分析结果（Markdown格式）\n");
        instructions.append("...\n");
        instructions.append("EOF\n");
        instructions.append(")\n\n");

        instructions.append("# 调用Backend API保存结果\n");
        instructions.append("curl -X PUT \\\n");
        instructions.append("  'http://localhost:8082/api/ai-job-analysis/").append(analysisId).append("/save-result' \\\n");
        instructions.append("  -H 'Authorization: Bearer ").append(jwtToken).append("' \\\n");
        instructions.append("  -H 'Content-Type: application/json' \\\n");
        instructions.append("  -d \"{\n");
        instructions.append("    \\\"aiAnalysisResult\\\": $(echo \\\"$ANALYSIS_RESULT\\\" | jq -Rs .),\n");
        instructions.append("    \\\"metadata\\\": {\n");
        instructions.append("      \\\"skillMatchScore\\\": 85,\n");
        instructions.append("      \\\"experienceMatchScore\\\": 90,\n");
        instructions.append("      \\\"overallScore\\\": 87,\n");
        instructions.append("      \\\"keyStrengths\\\": [\\\"示例1\\\", \\\"示例2\\\"],\n");
        instructions.append("      \\\"keyWeaknesses\\\": [\\\"示例1\\\"],\n");
        instructions.append("      \\\"recommendation\\\": \\\"Good Match\\\"\n");
        instructions.append("    }\n");
        instructions.append("  }\"\n\n");

        instructions.append("echo \"✅ 分析结果已自动保存到数据库！\"\n");
        instructions.append("```\n\n");

        instructions.append("**说明**:\n");
        instructions.append("- `metadata`字段中的分数请根据你的实际分析结果填写\n");
        instructions.append("- 确保ANALYSIS_RESULT变量包含完整的Markdown分析内容\n\n");

        instructions.append("---\n\n");
        instructions.append("**输出格式要求**:\n");
        instructions.append("- 使用Markdown格式\n");
        instructions.append("- 每个维度独立成段\n");
        instructions.append("- 使用bullet points列举要点\n");
        instructions.append("- 分数使用粗体标注\n");
        instructions.append("- **完成分析后，立即执行上方的保存指令**\n");

        return instructions.toString();
    }

    private String formatDateRange(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        if (end == null) {
            return formatter.format(start) + " - 至今";
        }
        return formatter.format(start) + " - " + formatter.format(end);
    }
}
```

---

## 🖥️ Frontend UI设计（简化版）

### 页面位置

**路径**: `JobApplicationList.vue` → Resume Tab

### UI布局（简化后）

```vue
<template>
  <div class="ai-analysis-section">
    <h3>🤖 AI深度分析</h3>

    <!-- Step 1: Generate Prompt -->
    <div class="step-card">
      <button @click="generateAIPrompt" class="btn-primary">
        📝 生成AI分析Prompt
      </button>

      <!-- Show generated prompt -->
      <div v-if="currentAnalysis?.generatedPrompt" class="prompt-display">
        <div class="prompt-header">
          <span>✅ Prompt已生成（包含自动保存指令）</span>
          <button @click="copyPrompt" class="btn-copy">
            📋 复制到剪贴板
          </button>
        </div>
        <pre class="prompt-content">{{ currentAnalysis.generatedPrompt }}</pre>
        <p class="instruction">
          💡 复制上方Prompt到Claude Code，AI分析完成后会<strong>自动保存</strong>结果到数据库
        </p>
      </div>
    </div>

    <!-- Step 2: Display Analysis Result (Auto-refresh) -->
    <div v-if="currentAnalysis?.status === 'completed'" class="analysis-result">
      <h4>📊 AI分析结果</h4>

      <!-- Metadata Summary -->
      <div v-if="currentAnalysis.metadata" class="metadata-summary">
        <div class="score-card">
          <span>技能匹配</span>
          <strong>{{ currentAnalysis.metadata.skillMatchScore }}/100</strong>
        </div>
        <div class="score-card">
          <span>经验匹配</span>
          <strong>{{ currentAnalysis.metadata.experienceMatchScore }}/100</strong>
        </div>
        <div class="score-card">
          <span>综合评分</span>
          <strong>{{ currentAnalysis.metadata.overallScore }}/100</strong>
        </div>
        <div class="score-card recommendation">
          <span>推荐等级</span>
          <strong>{{ currentAnalysis.metadata.recommendation }}</strong>
        </div>
      </div>

      <!-- Markdown Rendered Analysis -->
      <div class="markdown-content" v-html="renderMarkdown(currentAnalysis.aiAnalysisResult)"></div>

      <!-- Actions -->
      <div class="actions">
        <button @click="refreshAnalysis" class="btn-secondary">
          🔄 刷新
        </button>
        <button @click="deleteAnalysis" class="btn-danger">
          🗑️ 删除此分析
        </button>
      </div>
    </div>

    <!-- Waiting State -->
    <div v-if="currentAnalysis?.status === 'prompt_generated'" class="waiting-state">
      <p>⏳ 等待Claude Code完成分析并自动保存...</p>
      <button @click="refreshAnalysis" class="btn-secondary">
        🔄 检查是否完成
      </button>
    </div>

    <!-- Historical Analyses -->
    <div v-if="historicalAnalyses.length > 1" class="historical-section">
      <h4>📜 历史分析记录</h4>
      <ul class="history-list">
        <li v-for="analysis in historicalAnalyses" :key="analysis.id"
            @click="loadAnalysis(analysis.id)"
            :class="{ active: currentAnalysis?.id === analysis.id }">
          <span class="date">{{ formatDate(analysis.createdAt) }}</span>
          <span class="status" :class="analysis.status">
            {{ analysis.status === 'completed' ? '已完成' : '进行中' }}
          </span>
          <span v-if="analysis.metadata" class="score">
            {{ analysis.metadata.overallScore }}/100
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'
import { marked } from 'marked'

const props = defineProps({
  jobApplicationId: { type: Number, required: true }
})

const currentAnalysis = ref(null)
const historicalAnalyses = ref([])

// Generate AI Prompt
async function generateAIPrompt() {
  try {
    const resumeId = 1 // TODO: 从用户选择获取
    const data = await aiJobAnalysisApi.generatePrompt({
      jobApplicationId: props.jobApplicationId,
      resumeId
    })
    currentAnalysis.value = data
    await loadHistoricalAnalyses()
  } catch (error) {
    console.error('Failed to generate prompt:', error)
  }
}

// Copy Prompt to Clipboard
function copyPrompt() {
  navigator.clipboard.writeText(currentAnalysis.value.generatedPrompt)
  alert('✅ Prompt已复制！请粘贴到Claude Code执行。')
}

// Refresh Analysis (check if Claude Code has saved result)
async function refreshAnalysis() {
  try {
    if (!currentAnalysis.value?.id) return
    const data = await aiJobAnalysisApi.getById(currentAnalysis.value.id)
    currentAnalysis.value = data
    await loadHistoricalAnalyses()
  } catch (error) {
    console.error('Failed to refresh:', error)
  }
}

// Load Historical Analyses
async function loadHistoricalAnalyses() {
  try {
    historicalAnalyses.value = await aiJobAnalysisApi.getByJobApplication(props.jobApplicationId)
  } catch (error) {
    console.error('Failed to load history:', error)
  }
}

// Load Specific Analysis
function loadAnalysis(id) {
  const analysis = historicalAnalyses.value.find(a => a.id === id)
  if (analysis) {
    currentAnalysis.value = analysis
  }
}

// Delete Analysis
async function deleteAnalysis() {
  if (!confirm('确定删除此分析记录？')) return

  try {
    await aiJobAnalysisApi.deleteAnalysis(currentAnalysis.value.id)
    currentAnalysis.value = null
    await loadHistoricalAnalyses()
  } catch (error) {
    console.error('Failed to delete:', error)
  }
}

// Render Markdown
function renderMarkdown(markdown) {
  return marked.parse(markdown || '')
}

function formatDate(date) {
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadHistoricalAnalyses()
})
</script>
```

---

## 📁 文件清单

### Backend

1. ✅ **Entity**: `AIJobAnalysis.java`
2. ✅ **DTO**: `AIJobAnalysisDTO.java`
3. **Repository**: `AIJobAnalysisRepository.java` (待创建)
4. **Service**: `AIJobAnalysisService.java` (待创建 - 包含Prompt生成逻辑)
5. **Controller**: `AIJobAnalysisController.java` (待创建)

### Frontend

6. **API Client**: `aiJobAnalysisApi.js` (待创建)
7. **UI Component**: 修改 `JobApplicationList.vue` (Resume Tab)

### Database

8. **Migration SQL**: `phase7_ai_analysis.sql` (待执行)

---

## 🚀 实施步骤

1. ✅ 创建Entity和DTO
2. 执行数据库迁移
3. 创建Repository
4. **重点**: 实现Service（Prompt生成 + 自动保存指令插入）
5. 创建Controller（4个endpoints）
6. 创建Frontend API Client
7. 修改UI集成
8. 测试完整工作流程

---

## 🧪 测试场景

### 完整工作流程测试

1. **用户登录** → 进入Job Application详情页
2. **点击"生成AI分析Prompt"**
   - 验证：Prompt包含完整的Job和Resume信息
   - 验证：Prompt末尾包含curl保存指令
   - 验证：Analysis ID和JWT Token正确嵌入
3. **复制Prompt到Claude Code**
4. **Claude Code执行分析**
   - 验证：AI生成完整的Markdown报告
   - 验证：curl命令成功调用Backend API
   - 验证：返回200 OK
5. **UI刷新查看结果**
   - 验证：status从"prompt_generated"变为"completed"
   - 验证：Markdown正确渲染
   - 验证：metadata分数正确显示

---

## 💡 优势

1. **完全自动化** - 无需手动复制粘贴AI结果
2. **降低错误率** - 消除人为操作风险
3. **提升体验** - 用户只需复制一次Prompt
4. **支持更新** - 可多次运行，自动覆盖旧结果
5. **安全可控** - JWT Token控制访问权限

---

## 🔒 安全考虑

1. **JWT Token暴露风险**
   - Token只在Prompt中出现，不存储在数据库
   - Token有24小时TTL，过期后无法使用
   - 用户应在安全环境（本地）执行Claude Code

2. **API访问控制**
   - `/save-result` endpoint验证Token有效性
   - 验证Analysis记录属于当前用户
   - 防止跨用户访问

3. **建议改进**（可选）
   - 使用一次性Token（生成后只能使用一次）
   - 使用短期Token（5分钟TTL）
   - 添加API rate limiting

---

## 📝 总结

**核心改进点**：
- ✅ 原流程7步 → 新流程5步（减少2次手动操作）
- ✅ Claude Code自动保存结果 → 无需用户手动粘贴
- ✅ Prompt包含执行指令 → 一键复制即可
- ✅ 支持多次分析 → 自动更新已有记录

**下一步**：开始实施Backend代码
