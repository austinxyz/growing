# Phase 7 Enhancement: AI-Driven JD-Resume Matching Analysis

## 📋 概述

在现有Phase 7的关键词匹配分析基础上，增加**AI驱动的深度分析**功能。用户可以生成结构化的Prompt，在Claude Code中获取AI分析，然后保存结果到系统中。

---

## 🎯 核心工作流程

```
1. 用户在UI中选择Job Application + Resume
   ↓
2. 点击"生成AI分析Prompt"按钮
   ↓
3. Backend生成包含JD和Resume完整信息的Prompt
   ↓
4. 用户复制Prompt到Claude Code
   ↓
5. Claude Code执行AI分析，返回Markdown格式报告
   ↓
6. 用户复制AI分析结果，粘贴回UI
   ↓
7. 点击"保存分析结果"，存入数据库
   ↓
8. UI展示Markdown渲染的完整报告
```

---

## 🗄️ 数据库设计

### 新增表: `ai_job_analysis`

```sql
CREATE TABLE `ai_job_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `job_application_id` bigint NOT NULL COMMENT '职位申请ID',
  `resume_id` bigint NOT NULL COMMENT '简历ID',

  -- Prompt & Analysis
  `generated_prompt` text NOT NULL COMMENT '生成的AI分析Prompt',
  `ai_analysis_result` text COMMENT 'AI分析结果（Markdown格式）',

  -- Metadata (JSON)
  `analysis_metadata` json DEFAULT NULL COMMENT '分析元数据: {
    "skillMatchScore": 85,
    "experienceMatchScore": 90,
    "overallScore": 87,
    "keyStrengths": ["..."],
    "keyWeaknesses": ["..."],
    "recommendation": "Strong Match"
  }',

  -- Analysis Status
  `status` varchar(50) DEFAULT 'prompt_generated' COMMENT '分析状态: prompt_generated, completed',

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI驱动的JD-Resume匹配分析记录';
```

**字段说明**:
- `generated_prompt`: 自动生成的完整Prompt（包含JD和Resume所有信息）
- `ai_analysis_result`: 用户从Claude Code复制回来的AI分析结果（Markdown格式）
- `analysis_metadata`: 可选的结构化元数据（如果用户想手动输入分数）
- `status`:
  - `"prompt_generated"` - 只生成了Prompt，还未有AI结果
  - `"completed"` - 已保存AI分析结果

---

## 🔧 Backend API设计

### API Endpoints

#### 1. 生成AI分析Prompt

**Endpoint**: `POST /api/ai-job-analysis/generate-prompt`

**Request Body**:
```json
{
  "jobApplicationId": 123,
  "resumeId": 456
}
```

**Response** (`AIJobAnalysisDTO`):
```json
{
  "id": 1,
  "jobApplicationId": 123,
  "resumeId": 456,
  "generatedPrompt": "# JD-Resume匹配分析请求\n\n...(完整的Prompt文本)...",
  "aiAnalysisResult": null,
  "metadata": null,
  "status": "prompt_generated",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:00:00"
}
```

**业务逻辑**:
1. 验证用户权限（Job Application属于当前用户）
2. 验证Resume存在且属于当前用户
3. 从数据库加载Job Application、Company、Resume、Projects、Experiences
4. 使用模板生成结构化Prompt
5. 创建 `ai_job_analysis` 记录，状态为 `"prompt_generated"`
6. 返回DTO（前端可直接复制`generatedPrompt`）

---

#### 2. 保存AI分析结果

**Endpoint**: `PUT /api/ai-job-analysis/{id}/save-result`

**Request Body**:
```json
{
  "aiAnalysisResult": "# 分析结果\n\n## 技能匹配度: 85/100\n...(用户粘贴的AI返回的Markdown)...",
  "metadata": {  // Optional: 用户手动输入或解析AI结果得到
    "skillMatchScore": 85,
    "experienceMatchScore": 90,
    "overallScore": 87,
    "keyStrengths": ["Strong Spring Boot experience", "Led team of 5 engineers"],
    "keyWeaknesses": ["Limited cloud experience", "No Kubernetes"],
    "recommendation": "Good Match"
  }
}
```

**Response**:
```json
{
  "id": 1,
  "jobApplicationId": 123,
  "resumeId": 456,
  "generatedPrompt": "...",
  "aiAnalysisResult": "# 分析结果\n\n## 技能匹配度: 85/100\n...",
  "metadata": {
    "skillMatchScore": 85,
    "experienceMatchScore": 90,
    "overallScore": 87,
    "keyStrengths": ["..."],
    "keyWeaknesses": ["..."],
    "recommendation": "Good Match"
  },
  "status": "completed",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:15:00"
}
```

**业务逻辑**:
1. 验证用户权限（Analysis记录属于当前用户）
2. 验证状态（必须是`"prompt_generated"`才能更新）
3. 更新 `ai_analysis_result` 和 `analysis_metadata`
4. 更新状态为 `"completed"`
5. 返回完整DTO

---

#### 3. 查询Job Application的所有AI分析记录

**Endpoint**: `GET /api/ai-job-analysis/job-application/{jobApplicationId}`

**Response**:
```json
[
  {
    "id": 1,
    "jobApplicationId": 123,
    "resumeId": 456,
    "generatedPrompt": "...",
    "aiAnalysisResult": "...",
    "metadata": { ... },
    "status": "completed",
    "createdAt": "2024-01-06T10:00:00",
    "updatedAt": "2024-01-06T10:15:00"
  },
  {
    "id": 2,
    "jobApplicationId": 123,
    "resumeId": 789,  // 不同Resume的分析
    "generatedPrompt": "...",
    "aiAnalysisResult": null,
    "metadata": null,
    "status": "prompt_generated",
    "createdAt": "2024-01-07T14:00:00",
    "updatedAt": "2024-01-07T14:00:00"
  }
]
```

---

#### 4. 删除AI分析记录

**Endpoint**: `DELETE /api/ai-job-analysis/{id}`

**Response**: `204 No Content`

---

## 🎨 Prompt生成模板

### Java代码示例

```java
public String generatePrompt(JobApplication job, Resume resume,
                              List<ProjectExperience> projects,
                              List<ResumeExperience> experiences) {
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
    prompt.append("**候选人**: ").append(resume.getUser().getFullName()).append("\n");
    prompt.append("**邮箱**: ").append(resume.getEmail()).append("\n\n");

    // Work Experiences
    prompt.append("### 工作经历\n");
    for (ResumeExperience exp : experiences) {
        prompt.append("- **").append(exp.getCompanyName()).append("** | ")
              .append(exp.getPosition()).append("\n");
        prompt.append("  - 时间: ").append(formatDateRange(exp.getStartDate(), exp.getEndDate())).append("\n");
        if (exp.getResponsibilities() != null) {
            prompt.append("  - 职责: ").append(exp.getResponsibilities()).append("\n");
        }
        prompt.append("\n");
    }

    // Projects
    prompt.append("### 项目经验\n");
    for (ProjectExperience proj : projects) {
        prompt.append("- **").append(proj.getProjectName()).append("**\n");
        prompt.append("  - 技术栈: ").append(proj.getTechStack()).append("\n");
        if (proj.getMyContribution() != null) {
            prompt.append("  - 贡献: ").append(proj.getMyContribution()).append("\n");
        }
        prompt.append("\n");
    }

    // Skills
    prompt.append("### 技能列表\n");
    List<ResumeSkill> skills = resume.getResumeSkills();
    Map<String, List<String>> skillsByCategory = skills.stream()
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

    prompt.append("---\n\n");
    prompt.append("**输出格式要求**:\n");
    prompt.append("- 使用Markdown格式\n");
    prompt.append("- 每个维度独立成段\n");
    prompt.append("- 使用bullet points列举要点\n");
    prompt.append("- 分数使用粗体标注\n");

    return prompt.toString();
}
```

---

## 🖥️ Frontend UI设计

### 页面位置

**路径**: `JobApplicationList.vue` → Resume Tab

### UI布局

```vue
<template>
  <!-- Resume Tab Content -->
  <div class="ai-analysis-section">
    <h3>🤖 AI深度分析</h3>

    <!-- Step 1: Generate Prompt -->
    <div class="step-card">
      <h4>步骤1: 生成分析Prompt</h4>
      <button @click="generateAIPrompt" class="btn-primary">
        生成AI分析Prompt
      </button>

      <!-- Show generated prompt -->
      <div v-if="currentAnalysis && currentAnalysis.generatedPrompt" class="prompt-display">
        <div class="prompt-header">
          <span>已生成Prompt</span>
          <button @click="copyPrompt" class="btn-copy">
            📋 复制Prompt
          </button>
        </div>
        <pre class="prompt-content">{{ currentAnalysis.generatedPrompt }}</pre>
      </div>
    </div>

    <!-- Step 2: Paste AI Result -->
    <div v-if="currentAnalysis && currentAnalysis.status === 'prompt_generated'" class="step-card">
      <h4>步骤2: 粘贴AI分析结果</h4>
      <p class="instruction">
        1. 复制上方Prompt到Claude Code<br>
        2. 等待AI生成分析报告<br>
        3. 将AI返回的Markdown结果粘贴到下方
      </p>

      <textarea
        v-model="aiResultInput"
        class="ai-result-input"
        placeholder="粘贴AI分析结果（Markdown格式）..."
        rows="10"
      ></textarea>

      <button @click="saveAIResult" class="btn-success" :disabled="!aiResultInput">
        💾 保存分析结果
      </button>
    </div>

    <!-- Step 3: Display Analysis Result -->
    <div v-if="currentAnalysis && currentAnalysis.status === 'completed'" class="analysis-result">
      <h4>📊 AI分析结果</h4>

      <!-- Metadata Summary (if available) -->
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
        <button @click="deleteAnalysis(currentAnalysis.id)" class="btn-danger">
          🗑️ 删除此分析
        </button>
      </div>
    </div>

    <!-- Historical Analyses List -->
    <div v-if="historicalAnalyses.length > 0" class="historical-section">
      <h4>📜 历史分析记录</h4>
      <ul class="history-list">
        <li v-for="analysis in historicalAnalyses" :key="analysis.id"
            @click="loadAnalysis(analysis.id)"
            :class="{ active: currentAnalysis?.id === analysis.id }">
          <span class="date">{{ formatDate(analysis.createdAt) }}</span>
          <span class="status" :class="analysis.status">{{ getStatusLabel(analysis.status) }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'
import { marked } from 'marked'  // Markdown渲染库

const props = defineProps({
  jobApplicationId: { type: Number, required: true }
})

const currentAnalysis = ref(null)
const historicalAnalyses = ref([])
const aiResultInput = ref('')

// Generate AI Prompt
async function generateAIPrompt() {
  try {
    const resumeId = 1 // TODO: 从用户选择获取
    const data = await aiJobAnalysisApi.generatePrompt({
      jobApplicationId: props.jobApplicationId,
      resumeId
    })
    currentAnalysis.value = data
    loadHistoricalAnalyses()
  } catch (error) {
    console.error('Failed to generate prompt:', error)
  }
}

// Copy Prompt to Clipboard
function copyPrompt() {
  navigator.clipboard.writeText(currentAnalysis.value.generatedPrompt)
  alert('Prompt已复制到剪贴板！')
}

// Save AI Result
async function saveAIResult() {
  try {
    const data = await aiJobAnalysisApi.saveResult(currentAnalysis.value.id, {
      aiAnalysisResult: aiResultInput.value,
      metadata: null  // 可选：解析AI结果提取元数据
    })
    currentAnalysis.value = data
    aiResultInput.value = ''
    loadHistoricalAnalyses()
  } catch (error) {
    console.error('Failed to save AI result:', error)
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
async function deleteAnalysis(id) {
  if (!confirm('确定删除此分析记录？')) return

  try {
    await aiJobAnalysisApi.deleteAnalysis(id)
    currentAnalysis.value = null
    loadHistoricalAnalyses()
  } catch (error) {
    console.error('Failed to delete analysis:', error)
  }
}

// Render Markdown
function renderMarkdown(markdown) {
  return marked.parse(markdown || '')
}

function formatDate(date) {
  return new Date(date).toLocaleString('zh-CN')
}

function getStatusLabel(status) {
  return status === 'prompt_generated' ? '待完成' : '已完成'
}

onMounted(() => {
  loadHistoricalAnalyses()
})
</script>

<style scoped>
.ai-analysis-section {
  padding: 20px;
  background: #f9fafb;
}

.step-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.prompt-display {
  margin-top: 15px;
}

.prompt-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.prompt-content {
  background: #f4f4f5;
  padding: 15px;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
  font-size: 13px;
  line-height: 1.6;
}

.ai-result-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-family: monospace;
  font-size: 13px;
}

.metadata-summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.score-card {
  background: #e0f2fe;
  padding: 15px;
  border-radius: 6px;
  text-align: center;
}

.score-card.recommendation {
  background: #fef3c7;
}

.markdown-content {
  background: white;
  padding: 20px;
  border-radius: 6px;
  line-height: 1.8;
}

.markdown-content h2 {
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 8px;
  margin-top: 24px;
}

.markdown-content ul {
  padding-left: 24px;
}

.history-list {
  list-style: none;
  padding: 0;
}

.history-list li {
  padding: 12px;
  border-left: 3px solid #e5e7eb;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-list li.active {
  border-left-color: #3b82f6;
  background: #eff6ff;
}

.history-list li:hover {
  background: #f3f4f6;
}

.status {
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
}

.status.prompt_generated {
  background: #fef3c7;
  color: #92400e;
}

.status.completed {
  background: #d1fae5;
  color: #065f46;
}

.btn-primary, .btn-success, .btn-danger, .btn-copy {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-success {
  background: #10b981;
  color: white;
}

.btn-success:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-copy {
  background: #6b7280;
  color: white;
  font-size: 14px;
}
</style>
```

---

## 📁 文件清单

### Backend

1. **Entity**
   - `backend/src/main/java/com/growing/app/entity/AIJobAnalysis.java` ✅ (已创建)

2. **DTO**
   - `backend/src/main/java/com/growing/app/dto/AIJobAnalysisDTO.java` ✅ (已创建)

3. **Repository**
   - `backend/src/main/java/com/growing/app/repository/AIJobAnalysisRepository.java` (待创建)

4. **Service**
   - `backend/src/main/java/com/growing/app/service/AIJobAnalysisService.java` (待创建)

5. **Controller**
   - `backend/src/main/java/com/growing/app/controller/AIJobAnalysisController.java` (待创建)

### Frontend

6. **API Client**
   - `frontend/src/api/aiJobAnalysisApi.js` (待创建)

7. **UI Component**
   - 集成到现有的 `frontend/src/views/job-search/JobApplicationList.vue` (Resume Tab)

### Database

8. **Migration SQL**
   - `database/migrations/phase7_ai_analysis.sql` (待创建)

---

## 🚀 实施步骤

### Phase 1: 数据库 + Backend基础

1. ✅ 创建Entity (`AIJobAnalysis.java`)
2. ✅ 创建DTO (`AIJobAnalysisDTO.java`)
3. 创建Repository
4. 创建Service（Prompt生成逻辑）
5. 创建Controller（4个endpoints）
6. 执行数据库迁移SQL

### Phase 2: Frontend集成

7. 创建API Client
8. 修改 `JobApplicationList.vue`（添加AI分析Tab）
9. 添加Markdown渲染库（`npm install marked`）
10. 实现UI交互逻辑

### Phase 3: 测试

11. 单元测试（Service层）
12. 集成测试（API endpoints）
13. 手动测试完整工作流程

---

## 💡 使用示例

### 完整工作流程演示

**1. 用户登录 → 进入Job Application详情页**

**2. 点击"Resume" Tab → "生成AI分析Prompt"**
   - 系统生成包含完整JD和Resume的Prompt
   - 显示在UI中，用户点击"复制Prompt"

**3. 用户打开Claude Code命令行**
   ```bash
   $ claude
   ```
   粘贴Prompt，AI开始分析

**4. Claude Code返回分析结果（Markdown格式）**
   ```markdown
   # 分析结果

   ## 技能匹配度: **85/100**

   ### 匹配的核心技能
   - Java (8年经验，符合JD要求)
   - Spring Boot (项目经验丰富)
   - MySQL (数据库管理经验)

   ### 缺失的关键技能
   - Kubernetes (JD要求，但简历未提及)
   - AWS (仅有基础了解，需加强)

   ## 经验匹配度: **90/100**

   候选人有5年团队管理经验，完全符合Senior级别要求。

   ...
   ```

**5. 用户复制AI结果 → 粘贴回UI → 点击"保存分析结果"**

**6. UI渲染Markdown展示完整分析报告**

---

## 🔐 安全考虑

1. **权限验证**
   - 用户只能分析自己的Job Applications
   - 用户只能查看/修改自己的AI分析记录

2. **数据隐私**
   - Resume敏感信息（电话、地址）不包含在Prompt中
   - AI分析结果存储在用户私有空间

3. **输入验证**
   - Prompt长度限制（防止过大文本）
   - AI结果长度限制（TEXT字段有上限）

---

## 📊 性能优化

1. **Prompt生成**
   - 使用 `@Transactional(readOnly = true)` 优化查询
   - 使用JOIN FETCH减少N+1查询

2. **历史记录加载**
   - 分页加载（默认显示最近10条）
   - 使用索引优化查询（`idx_job`, `idx_user`）

3. **Markdown渲染**
   - 客户端渲染（不增加服务器负担）
   - 可选：缓存渲染结果（localStorage）

---

## 🧪 测试计划

### 单元测试

```java
@Test
public void testGeneratePrompt() {
    // Given
    JobApplication job = createTestJob();
    Resume resume = createTestResume();

    // When
    String prompt = aiJobAnalysisService.generatePrompt(job.getId(), resume.getId(), userId);

    // Then
    assertThat(prompt).contains("Job ID:");
    assertThat(prompt).contains("Resume ID:");
    assertThat(prompt).contains("技能匹配度");
}

@Test
public void testSaveAIResult() {
    // Given
    AIJobAnalysis analysis = createTestAnalysis();
    String aiResult = "# 分析结果\n\n## 技能匹配度: 85/100";

    // When
    AIJobAnalysisDTO result = aiJobAnalysisService.saveResult(analysis.getId(), aiResult, null, userId);

    // Then
    assertThat(result.getStatus()).isEqualTo("completed");
    assertThat(result.getAiAnalysisResult()).isEqualTo(aiResult);
}
```

### 集成测试

```bash
# Generate Prompt
curl -X POST http://localhost:8082/api/ai-job-analysis/generate-prompt \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"jobApplicationId": 1, "resumeId": 1}'

# Save AI Result
curl -X PUT http://localhost:8082/api/ai-job-analysis/1/save-result \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "aiAnalysisResult": "# 分析结果\n\n## 技能匹配度: 85/100",
    "metadata": {
      "skillMatchScore": 85,
      "experienceMatchScore": 90,
      "overallScore": 87
    }
  }'
```

---

## 📝 总结

这个设计完美结合了：
- ✅ **现有的自动化分析**（关键词匹配，快速预览）
- ✅ **新的AI深度分析**（Claude Code驱动，详细报告）
- ✅ **无缝的用户体验**（一键生成Prompt → 复制粘贴 → 保存结果）
- ✅ **灵活的扩展性**（支持多版本简历对比、历史记录、元数据提取）

**下一步**: 开始实施Backend代码（Repository → Service → Controller）
