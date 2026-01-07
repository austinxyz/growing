# Phase 7 Enhancement: AI Resume Analysis (Skill-Based - 最简化方案)

## 📋 核心思路

**利用现有的 `resume-analysis` skill**，用户只需复制一个**极简Prompt**到Claude Code：

```
分析job 13和用户3的简历匹配度，并保存结果到数据库
```

Claude Code会：
1. 自动调用 `resume-analysis` skill
2. 获取AI分析结果（JSON格式）
3. 自动调用Backend API保存结果
4. 用户在UI刷新查看

---

## 🎯 **完全自动化的工作流程**

```
1. 用户在UI中查看Job Application详情
   ↓
2. 点击"生成AI分析Prompt"按钮
   ↓
3. Backend生成极简Prompt（只包含job_id, user_id）
   - Prompt: "分析job {job_id}和用户{user_id}的简历匹配度，并保存结果到数据库 (analysis_id={analysis_id}, token={short_token})"
   ↓
<Austin> 需要analysis id吗？是不是可以根据job id，user id算出来，还有token，是不是可以通过user id得到？

4. 用户复制Prompt → 粘贴到Claude Code
   ↓
5. Claude Code自动执行：
   - 调用 /resume-analysis skill（job_id={job_id}, user_id={user_id}）
   - skill返回完整的JSON分析结果
   - 使用Bash工具调用Backend API保存结果
   ↓
6. Backend接收并保存
   - 更新ai_analysis_result字段（JSON字符串）
   - 更新analysis_metadata字段（提取核心指标）
   - 更新status为"completed"
   ↓
7. UI手动刷新查看
   - 解析JSON展示各维度分数
   - 展示strengths、improvements、customization建议
```

---

## 📝 **极简Prompt设计**

### Backend生成的Prompt模板

```markdown
请执行以下任务：

1. 使用 /resume-analysis skill 分析job {job_id}和用户{user_id}的简历匹配度
2. 获取分析结果后，执行以下命令保存到数据库：

```bash
# 将skill返回的JSON结果保存到变量
ANALYSIS_JSON='<将上一步的JSON结果粘贴到这里>'

# 调用Backend API保存
curl -X PUT \
  'http://localhost:8082/api/ai-job-analysis/{analysis_id}/save-result' \
  -H 'Authorization: Bearer {short_lived_token}' \
  -H 'Content-Type: application/json' \
  -d "{\"aiAnalysisResult\": $(echo \"$ANALYSIS_JSON\" | jq -c .)}"

# 检查结果
if [ $? -eq 0 ]; then
  echo "✅ 分析结果已保存到数据库！"
else
  echo "❌ 保存失败，请检查网络或Token是否过期"
fi
```

---

**参数说明**：
- job_id: {job_id}
- user_id: {user_id}
- analysis_id: {analysis_id}
- Token有效期: 5分钟（请在生成后立即执行）
```

**实际示例**：
```markdown
请执行以下任务：

1. 使用 /resume-analysis skill 分析job 13和用户3的简历匹配度
2. 获取分析结果后，执行以下命令保存到数据库：

```bash
ANALYSIS_JSON='<将上一步的JSON结果粘贴到这里>'

curl -X PUT \
  'http://localhost:8082/api/ai-job-analysis/789/save-result' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...' \
  -H 'Content-Type: application/json' \
  -d "{\"aiAnalysisResult\": $(echo \"$ANALYSIS_JSON\" | jq -c .)}"
```

---

**参数说明**：
- job_id: 13
- user_id: 3
- analysis_id: 789
- Token有效期: 5分钟
```

---

## 🗄️ 数据库设计（无变化）

```sql
CREATE TABLE `ai_job_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `job_application_id` bigint NOT NULL,
  `resume_id` bigint NOT NULL COMMENT '使用用户的default resume',

  `generated_prompt` text NOT NULL COMMENT '生成的极简Prompt',
  `ai_analysis_result` text COMMENT 'Skill返回的JSON分析结果（完整保存）',

  `analysis_metadata` json DEFAULT NULL COMMENT '从JSON中提取的核心指标: {
    "matchScore": 90,
    "educationScore": 85,
    "experienceScore": 100,
    "skillScore": 90,
    "responsibilityScore": 66,
    "softSkillScore": 100
  }',

  `status` varchar(50) DEFAULT 'prompt_generated' COMMENT 'prompt_generated | completed',

  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_job` (`job_application_id`),
  KEY `idx_resume` (`resume_id`),
  KEY `idx_status` (`status`),
  KEY `idx_job_resume_user` (`job_application_id`, `resume_id`, `user_id`),
  CONSTRAINT `ai_job_analysis_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_job_analysis_ibfk_2` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_job_analysis_ibfk_3` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 🔧 Backend API设计

### 1. 生成AI分析Prompt（极简版）

**Endpoint**: `POST /api/ai-job-analysis/generate-prompt`

**Request Body**:
```json
{
  "jobApplicationId": 13
}
```

**Response**:
```json
{
  "id": 789,
  "jobApplicationId": 13,
  "resumeId": 2,  // 自动使用用户的default resume
  "generatedPrompt": "请执行以下任务：\n\n1. 使用 /resume-analysis skill 分析job 13和用户3的简历匹配度\n2. 获取分析结果后，执行以下命令保存到数据库：\n\n```bash\nANALYSIS_JSON='<将上一步的JSON结果粘贴到这里>'\n\ncurl -X PUT \\\n  'http://localhost:8082/api/ai-job-analysis/789/save-result' \\\n  -H 'Authorization: Bearer eyJ...' \\\n  -H 'Content-Type: application/json' \\\n  -d \"{\\\"aiAnalysisResult\\\": $(echo \\\"$ANALYSIS_JSON\\\" | jq -c .)}\"\n```\n\n---\n\n**参数说明**：\n- job_id: 13\n- user_id: 3\n- analysis_id: 789\n- Token有效期: 5分钟",
  "status": "prompt_generated",
  "createdAt": "2024-01-06T10:00:00"
}
```

**业务逻辑**:
1. 验证Job Application属于当前用户
2. 获取用户的default resume（`resumes.is_default = 1`）
3. 检查是否已有此Job的分析记录：
   - **存在**: 更新现有记录，重置status为`"prompt_generated"`
   - **不存在**: 创建新记录
4. 生成5分钟短期Token（使用`JwtUtil.generateShortLivedToken()`）
5. 生成极简Prompt，嵌入：
   - job_id = `job.getId()`
   - user_id = `user.getId()`
   - analysis_id = `analysis.getId()`
   - short_lived_token = 生成的5分钟Token
6. 保存并返回

---

### 2. 保存AI分析结果（Claude Code自动调用）

**Endpoint**: `PUT /api/ai-job-analysis/{id}/save-result`

**Request Body**:
```json
{
  "aiAnalysisResult": "{\"jobApplicationId\":13,\"resumeId\":2,\"matchScore\":90,...}"
}
```

**Response**:
```json
{
  "id": 789,
  "jobApplicationId": 13,
  "resumeId": 2,
  "generatedPrompt": "...",
  "aiAnalysisResult": "{\"matchScore\":90,...}",
  "metadata": {
    "matchScore": 90,
    "educationScore": 85,
    "experienceScore": 100,
    "skillScore": 90,
    "responsibilityScore": 66,
    "softSkillScore": 100
  },
  "status": "completed",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:05:00"
}
```

**业务逻辑**:
1. 验证短期Token（5分钟TTL）
2. 验证用户权限
3. 验证JSON格式（确保是valid JSON）
4. 从JSON中提取metadata：
   ```java
   ObjectMapper mapper = new ObjectMapper();
   JsonNode root = mapper.readTree(aiAnalysisResult);

   AnalysisMetadata metadata = new AnalysisMetadata();
   metadata.setMatchScore(root.get("matchScore").asInt());
   metadata.setEducationScore(root.path("educationMatch").path("score").asInt());
   metadata.setExperienceScore(root.path("experienceMatch").path("score").asInt());
   metadata.setSkillScore(root.path("skillMatch").path("score").asInt());
   metadata.setResponsibilityScore(root.path("responsibilityMatch").path("score").asInt());
   metadata.setSoftSkillScore(root.path("softSkillMatch").path("score").asInt());
   ```
5. 更新记录：
   - `ai_analysis_result` = JSON字符串（原封不动保存）
   - `analysis_metadata` = 提取的metadata（JSON）
   - `status` = `"completed"`
6. 返回完整DTO

---

### 3. 查询分析结果

**Endpoint**: `GET /api/ai-job-analysis/job-application/{jobApplicationId}`

**Response**:
```json
[
  {
    "id": 789,
    "jobApplicationId": 13,
    "resumeId": 2,
    "metadata": {
      "matchScore": 90,
      "educationScore": 85,
      ...
    },
    "status": "completed",
    "createdAt": "2024-01-06T10:00:00"
  }
]
```

---

### 4. 获取分析详情（含完整JSON）

**Endpoint**: `GET /api/ai-job-analysis/{id}`

**Response**:
```json
{
  "id": 789,
  "jobApplicationId": 13,
  "resumeId": 2,
  "generatedPrompt": "...",
  "aiAnalysisResult": "{\"matchScore\":90,\"educationMatch\":{...},\"strengths\":[...],\"customization\":{...}}",
  "metadata": {
    "matchScore": 90,
    ...
  },
  "status": "completed",
  "createdAt": "2024-01-06T10:00:00",
  "updatedAt": "2024-01-06T10:05:00"
}
```

---

## 🖥️ Frontend UI设计

### 页面位置

**路径**: `JobApplicationList.vue` → Resume Tab

### UI布局

```vue
<template>
  <div class="ai-analysis-section">
    <h3>🤖 AI智能分析</h3>

    <!-- Step 1: Generate Prompt -->
    <div v-if="!currentAnalysis || currentAnalysis.status === 'prompt_generated'" class="prompt-section">
      <button @click="generatePrompt" class="btn-primary" :disabled="loading">
        {{ loading ? '生成中...' : '📝 生成AI分析Prompt' }}
      </button>

      <div v-if="currentAnalysis?.generatedPrompt" class="prompt-display">
        <div class="prompt-header">
          <span>✅ Prompt已生成（包含自动保存指令）</span>
          <button @click="copyPrompt" class="btn-copy">
            📋 复制到剪贴板
          </button>
        </div>
        <pre class="prompt-content">{{ currentAnalysis.generatedPrompt }}</pre>

        <div class="prompt-instructions">
          <p>💡 <strong>使用步骤</strong>：</p>
          <ol>
            <li>点击上方"复制到剪贴板"</li>
            <li>打开Claude Code命令行</li>
            <li>粘贴Prompt并执行</li>
            <li>等待AI分析完成（约30秒）</li>
            <li>Claude Code会自动保存结果到数据库</li>
            <li>返回此页面点击"刷新"查看结果</li>
          </ol>
          <p class="warning">⚠️ Token有效期5分钟，请立即执行</p>
        </div>
      </div>

      <div v-if="currentAnalysis?.status === 'prompt_generated'" class="waiting-section">
        <p>⏳ 等待Claude Code完成分析...</p>
        <button @click="refreshAnalysis" class="btn-secondary">
          🔄 检查是否完成
        </button>
      </div>
    </div>

    <!-- Step 2: Display Analysis Result -->
    <div v-if="currentAnalysis?.status === 'completed'" class="analysis-result">
      <div class="result-header">
        <h4>📊 AI分析结果</h4>
        <div class="actions">
          <button @click="refreshAnalysis" class="btn-secondary">
            🔄 刷新
          </button>
          <button @click="regenerateAnalysis" class="btn-warning">
            🔁 重新分析
          </button>
        </div>
      </div>

      <!-- Overall Match Score -->
      <div class="overall-score">
        <div class="score-circle" :class="getScoreClass(analysisData.matchScore)">
          <div class="score-value">{{ analysisData.matchScore }}</div>
          <div class="score-label">综合匹配</div>
        </div>
      </div>

      <!-- Dimension Scores -->
      <div class="dimension-scores">
        <div class="score-card">
          <span class="label">教育背景</span>
          <span class="score">{{ analysisData.educationMatch.score }}</span>
          <div class="progress-bar">
            <div class="progress" :style="{width: analysisData.educationMatch.score + '%'}"></div>
          </div>
        </div>

        <div class="score-card">
          <span class="label">工作经验</span>
          <span class="score">{{ analysisData.experienceMatch.score }}</span>
          <div class="progress-bar">
            <div class="progress" :style="{width: analysisData.experienceMatch.score + '%'}"></div>
          </div>
        </div>

        <div class="score-card">
          <span class="label">技术技能</span>
          <span class="score">{{ analysisData.skillMatch.score }}</span>
          <div class="progress-bar">
            <div class="progress" :style="{width: analysisData.skillMatch.score + '%'}"></div>
          </div>
        </div>

        <div class="score-card">
          <span class="label">岗位职责</span>
          <span class="score">{{ analysisData.responsibilityMatch.score }}</span>
          <div class="progress-bar">
            <div class="progress" :style="{width: analysisData.responsibilityMatch.score + '%'}"></div>
          </div>
        </div>

        <div class="score-card">
          <span class="label">软技能</span>
          <span class="score">{{ analysisData.softSkillMatch.score }}</span>
          <div class="progress-bar">
            <div class="progress" :style="{width: analysisData.softSkillMatch.score + '%'}"></div>
          </div>
        </div>
      </div>

      <!-- Matched Skills -->
      <div class="skills-section">
        <h5>✅ 匹配的技能</h5>
        <div class="skill-tags">
          <span v-for="skill in analysisData.skillMatch.matchedSkills" :key="skill" class="tag tag-success">
            {{ skill }}
          </span>
        </div>
      </div>

      <!-- Missing Skills -->
      <div v-if="analysisData.skillMatch.missingSkills.length > 0" class="skills-section">
        <h5>❌ 缺失的技能</h5>
        <div class="skill-tags">
          <span v-for="skill in analysisData.skillMatch.missingSkills" :key="skill" class="tag tag-danger">
            {{ skill }}
          </span>
        </div>
      </div>

      <!-- Strengths -->
      <div class="insights-section">
        <h5>💪 优势</h5>
        <ul>
          <li v-for="(strength, idx) in analysisData.strengths" :key="idx">
            {{ strength }}
          </li>
        </ul>
      </div>

      <!-- Improvements -->
      <div class="insights-section">
        <h5>📈 改进建议</h5>
        <ul>
          <li v-for="(improvement, idx) in analysisData.improvements" :key="idx">
            {{ improvement }}
          </li>
        </ul>
      </div>

      <!-- Customization Suggestions (if match score > 70) -->
      <div v-if="analysisData.customization" class="customization-section">
        <h5>✨ 简历定制建议</h5>

        <!-- Keyword Suggestions -->
        <div v-if="analysisData.customization.keywordSuggestions" class="subsection">
          <h6>🔑 关键词优化</h6>
          <div v-for="(suggestion, idx) in analysisData.customization.keywordSuggestions" :key="idx" class="suggestion-card">
            <strong>{{ suggestion.keyword }}</strong>
            <p>位置：{{ suggestion.section }}</p>
            <p>原因：{{ suggestion.reason }}</p>
          </div>
        </div>

        <!-- Project Optimizations -->
        <div v-if="analysisData.customization.projectOptimizations" class="subsection">
          <h6>🚀 项目经验优化</h6>
          <div v-for="(opt, idx) in analysisData.customization.projectOptimizations" :key="idx" class="optimization-card">
            <strong>{{ opt.projectName }}</strong>
            <ul>
              <li v-for="(sug, sidx) in opt.suggestions" :key="sidx">{{ sug }}</li>
            </ul>
          </div>
        </div>

        <!-- Structural Suggestions -->
        <div v-if="analysisData.customization.structuralSuggestions" class="subsection">
          <h6>📋 结构调整</h6>
          <ul>
            <li v-for="(sug, idx) in analysisData.customization.structuralSuggestions" :key="idx">
              {{ sug }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { aiJobAnalysisApi } from '@/api/aiJobAnalysisApi'

const props = defineProps({
  jobApplicationId: { type: Number, required: true }
})

const currentAnalysis = ref(null)
const loading = ref(false)

const analysisData = computed(() => {
  if (!currentAnalysis.value?.aiAnalysisResult) return null
  try {
    return JSON.parse(currentAnalysis.value.aiAnalysisResult)
  } catch (error) {
    console.error('Failed to parse analysis result:', error)
    return null
  }
})

async function generatePrompt() {
  loading.value = true
  try {
    const data = await aiJobAnalysisApi.generatePrompt({
      jobApplicationId: props.jobApplicationId
    })
    currentAnalysis.value = data
  } catch (error) {
    console.error('Failed to generate prompt:', error)
    alert('生成Prompt失败，请重试')
  } finally {
    loading.value = false
  }
}

function copyPrompt() {
  navigator.clipboard.writeText(currentAnalysis.value.generatedPrompt)
  alert('✅ Prompt已复制！请粘贴到Claude Code执行。')
}

async function refreshAnalysis() {
  if (!currentAnalysis.value?.id) return
  try {
    const data = await aiJobAnalysisApi.getById(currentAnalysis.value.id)
    currentAnalysis.value = data
  } catch (error) {
    console.error('Failed to refresh:', error)
  }
}

async function regenerateAnalysis() {
  if (!confirm('确定重新生成分析？现有结果将被覆盖。')) return
  await generatePrompt()
}

function getScoreClass(score) {
  if (score >= 80) return 'excellent'
  if (score >= 60) return 'good'
  return 'poor'
}

async function loadLatestAnalysis() {
  try {
    const analyses = await aiJobAnalysisApi.getByJobApplication(props.jobApplicationId)
    if (analyses.length > 0) {
      currentAnalysis.value = analyses[0]  // 最新的一条
    }
  } catch (error) {
    console.error('Failed to load analysis:', error)
  }
}

onMounted(() => {
  loadLatestAnalysis()
})
</script>

<style scoped>
/* 样式保持简洁 */
.overall-score {
  text-align: center;
  margin: 30px 0;
}

.score-circle {
  display: inline-block;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.score-circle.excellent {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.score-circle.good {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
}

.score-circle.poor {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.score-value {
  font-size: 48px;
  font-weight: bold;
}

.score-label {
  font-size: 14px;
  margin-top: 5px;
}

.dimension-scores {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin: 30px 0;
}

.score-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  margin-top: 10px;
}

.progress {
  height: 100%;
  background: #3b82f6;
  transition: width 0.3s ease;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.tag-success {
  background: #d1fae5;
  color: #065f46;
}

.tag-danger {
  background: #fee2e2;
  color: #991b1b;
}
</style>
```

---

## 📁 文件清单

### Backend

1. ✅ **Entity**: `AIJobAnalysis.java`
2. ✅ **DTO**: `AIJobAnalysisDTO.java`
3. **Repository**: `AIJobAnalysisRepository.java` (待创建)
4. **Service**: `AIJobAnalysisService.java` (待创建 - 极简实现)
5. **Controller**: `AIJobAnalysisController.java` (待创建)
6. **JwtUtil Enhancement**: 添加`generateShortLivedToken()`方法

### Frontend

7. **API Client**: `aiJobAnalysisApi.js` (待创建)
8. **UI Component**: 修改 `JobApplicationList.vue`（Resume Tab）
9. **Markdown Renderer**: `npm install marked`（用于渲染分析结果）

### Database

10. **Migration SQL**: `phase7_ai_analysis.sql` (待执行)

---

## 🚀 优势对比

| 特性 | v1方案（完整Prompt） | v2方案（手动粘贴） | v3方案（Skill-Based）✅ |
|------|---------------------|-------------------|------------------------|
| Prompt长度 | 500+ 行 | 300+ 行 | **20行** |
| 用户操作 | 复制→粘贴→执行 | 复制→粘贴→保存 | **复制→粘贴** |
| Token风险 | 24小时Token暴露 | 24小时Token暴露 | **5分钟短期Token** |
| 依赖工具 | jq, curl | 无 | **jq, curl** |
| 分析质量 | 基础关键词匹配 | 基础关键词匹配 | **AI语义分析（最强）** |
| 维护成本 | 高（Prompt很长） | 中等 | **低（复用skill）** |

---

## 🔒 安全改进

### 短期Token实现

```java
// JwtUtil.java 新增方法
public String generateShortLivedToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "ai_analysis");
    claims.put("expires_in", "5m");

    return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 300000))  // 5分钟
            .signWith(getSigningKey())
            .compact();
}
```

**安全优势**：
- Token只能用5分钟
- 明确标记为AI分析专用
- 降低泄露风险

---

## 🧪 测试流程

### 完整测试步骤

1. **Backend启动**
   ```bash
   cd backend && ./start.sh
   ```

2. **Frontend启动**
   ```bash
   cd frontend && npm run dev
   ```

3. **登录用户**
   - Username: austinxu
   - Password: helloworld

4. **进入Job Application详情**
   - 选择一个Job Application
   - 点击"Resume"标签页

5. **生成Prompt**
   - 点击"生成AI分析Prompt"
   - 查看生成的Prompt内容

6. **复制到Claude Code**
   - 点击"复制到剪贴板"
   - 打开终端执行`claude`
   - 粘贴Prompt

7. **验证Skill执行**
   - Claude Code自动调用`/resume-analysis`
   - 查看返回的JSON分析结果

8. **验证自动保存**
   - Claude Code自动执行curl命令
   - 查看是否输出"✅ 分析结果已保存到数据库！"

9. **UI刷新查看**
   - 返回UI点击"刷新"
   - 验证分析结果正确展示

---

## 📝 总结

**v3方案（Skill-Based）的核心优势**：
1. ✅ **极简Prompt** - 只需20行，无需维护庞大的模板
2. ✅ **复用现有skill** - 充分利用已有的AI分析能力
3. ✅ **5分钟短期Token** - 降低安全风险
4. ✅ **语义级分析** - 比关键词匹配更智能
5. ✅ **自动化保存** - Claude Code直接调用API
6. ✅ **低维护成本** - skill更新时Prompt无需改动

**下一步**：
- 执行数据库迁移
- 实现Backend API（极简版）
- 实现Frontend UI
- 端到端测试
