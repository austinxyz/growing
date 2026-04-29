# MCP Job Application Tools — Design Spec

> **Date**: 2026-04-29
> **Author**: brainstormed with Claude Code (superpowers:brainstorming)
> **Status**: Approved — OpenSpec artifacts generated; ready for `/opsx:apply`
> **OpenSpec change**: [`openspec/changes/mcp-job-application-tools/`](../../../openspec/changes/mcp-job-application-tools/)

---

## 1. 问题与目标

求职管理数据现在只能在 growing Web UI 里看。期望：在笔记本上的 Claude Desktop / Claude Code 里能直接问"我现在最该跟进哪个面试？" / "AppZen 那个职位的详情？"，让 LLM 帮我做跟进邮件起草、复盘分析、节奏判断 —— 不用切到浏览器。

部署形态：growing 已经在 NAS 上跑（Docker Compose）。MCP server 应该跟 backend 一起部署，从笔记本通过 NAS 网络访问。

## 2. Brainstorming 决策表

| Q | 选项 | 决定 |
|---|---|---|
| Q1 | 使用场景 | **远程 MCP**（笔记本 Claude Desktop → NAS 上 growing） |
| Q2 | 实现方式 | **A 嵌入 Spring Boot**（Spring AI MCP starter，不新增 container） |
| Q3 | Auth | **A 复用现有 JWT**（用户登录 growing UI 拿 token，粘到 Claude Desktop config） |
| Q4 | Tool 数量 | **B 3 个 tool**：`list_applications` / `get_application_detail` / `get_active_progress` |

## 3. 架构

```
┌─ Claude Desktop / Claude Code (笔记本) ──────────────┐
│  MCP client config:                                  │
│    transport: streamable-http                        │
│    url: http://nas:8082/mcp                          │
│    headers:                                          │
│      Authorization: Bearer <growing-jwt-from-login>  │
└────────────────────┬─────────────────────────────────┘
                     │ Streamable HTTP (POST /mcp)
                     ▼
┌─ NAS · Docker ── growing-backend container ──────────┐
│  Spring Boot 3.2 (existing)                          │
│  + spring-ai-starter-mcp-server-webmvc (NEW)         │
│                                                       │
│  ┌─ /mcp endpoint (Spring AI MCP) ─────────────┐     │
│  │  ① McpJobTools (@Component) registers 3      │     │
│  │     tools                                    │     │
│  │  ② McpAuthService validates Bearer JWT       │     │
│  │  ③ tools call existing JobApplicationService │     │
│  └──────────────────────────────────────────────┘     │
│  既有 /api/* endpoint 不变                            │
└───────────────────────────────────────────────────────┘
                     │
                     ▼
                MySQL (现有)
```

**关键架构决策**：
- **同端口 8082，新路径 `/mcp`**：避免改 docker-compose、Tailscale 路由、防火墙规则
- **Spring AI MCP starter**（不是 raw Java SDK）：`spring-ai-starter-mcp-server-webmvc` 跟 Tomcat 深度集成，注解风格，少写 boilerplate
- **直接调 Service 层**：MCP tool 实现不绕一圈 HTTP 请求自己的 controller — 直接调 `jobApplicationService.getActiveProgress(userId)`，省掉 JWT 重新生成 + HTTP serdes

## 4. Tool 契约

### Tool 1: `list_applications`
```json
// Input
{
  "type": "object",
  "properties": {
    "status": {
      "type": "string",
      "description": "Filter by applicationStatus (e.g., 'Applied', 'Interviewing', 'Offer', 'Rejected'). Accepts English canonical or Chinese variants. Omit for all."
    }
  }
}
```
**Output**: JSON array of summaries（裁剪掉 qualifications/responsibilities 大文本，省 token）：
```json
[{
  "id": 17, "companyName": "AppZen", "positionName": "EM, DevOps",
  "applicationStatus": "Interviewing", "submissionType": "Direct",
  "statusUpdatedAt": "2026-04-25T10:00:00", "daysSinceApplied": 18,
  "interviewStageCount": 2, "interviewRecordCount": 1
}]
```

### Tool 2: `get_application_detail`
```json
// Input
{
  "type": "object",
  "properties": { "id": { "type": "integer", "description": "Application id" } },
  "required": ["id"]
}
```
**Output**: 完整 `JobApplicationDTO` —— 含 stages / records / recruiterInsights / qualifications / responsibilities / notes / offer 字段 / status history。"重"的 tool，让 AI 起草跟进邮件、复盘面试时用。

### Tool 3: `get_active_progress`
```json
// Input
{
  "type": "object",
  "properties": {
    "include_closed": {
      "type": "boolean",
      "description": "When true, also include Rejected/Withdrawn applications. Default false.",
      "default": false
    }
  }
}
```
**Output**: `ActiveProgressDTO[]` —— 跟 dashboard 一样：含派生字段 (`priorityLevel`, `microStageLabel`, `daysSinceLastUpdate`, `nextActionType`, `nextActionLabel`)，已按 priority + days 排序。AI 最用得上的 tool（"接下来该关注什么"）。

### 通用约定
- 所有 tool 返回 **`text/plain` 内容块**包 JSON 字符串（MCP 标准做法，Claude 自动 parse）
- 时间字段统一 ISO-8601
- 错误：`{"error": {"code": 401|403|404|500, "message": "..."}}` 也作为 text content 返回，由 Claude 解读

## 5. Auth & 用户隔离

```
Claude Desktop                MCP /mcp endpoint                JobApplicationService
─────────────                  ────────────────                 ─────────────────────
  Authorization:               McpAuthService.getUserIdFromRequest():
  Bearer <jwt>      ──────►      ① extract "Bearer <token>" from header
                                  ② JwtUtil.getUsernameFromToken(token)
                                  ③ AuthService.getUserIdByUsername(username)
                                  ④ return userId  OR  throw 401
                                                                    ▲
                                tool method (receives userId) ──────┘
                                  ↓
                                jobApplicationService.getActiveProgress(userId)
                                  (existing service method, already user-scoped)
```

**关键点**：

1. **复用现有 JWT 体系**：`JwtUtil` + `AuthService` 已实现完整链路；MCP 不写自己的 JWT 验证。Tool 方法只多一行 `Long userId = mcpAuthService.getUserIdFromRequest(request);`

2. **每个 tool 强制 user-scope**：`JobApplicationService` 所有方法都按 userId 过滤。两个账号（austinxu / austin.xyz）各自的 token 只能看自己的数据。

3. **不动 SecurityConfig 全局**：现有 `permitAll()` 是项目范围 pre-existing concern；本次只为 `/mcp/**` 加单独的 auth 检查（在 McpAuthService 里手工做）。一致性优先于"正确性洁癖" — 后续单独 change 处理 Spring Security 重构。

4. **错误响应**：
    - JWT 缺失/格式错 → `{"error":{"code":401,"message":"Missing or invalid Authorization header"}}`
    - JWT 过期（24h） → 同上 401，message 提示 "Token expired, please re-login to growing and copy a fresh token"
    - 应用 ID 不属于当前用户 → service 层抛 403 → MCP 包 `{"error":{"code":403}}`
    - DB 错误 / 未知异常 → 500 + 通用错误信息（不泄露 stack trace）

5. **多账号场景**：Claude Desktop 配两个 MCP server entry，URL 同一个、token 不同：
```jsonc
"mcpServers": {
  "growing-austinxu":  { "url": "http://nas:8082/mcp", "headers": {"Authorization": "Bearer <T1>"} },
  "growing-austinxyz": { "url": "http://nas:8082/mcp", "headers": {"Authorization": "Bearer <T2>"} }
}
```

## 6. 测试策略

| 层 | 框架 | 覆盖 |
|---|---|---|
| **单元** `McpJobToolsTest` (Mockito) | JUnit 5 + Mockito | 每个 tool 的输入参数校验 / mock service 返回 / output 序列化结构 / 错误路径 |
| **单元** `McpAuthServiceTest` | JUnit 5 + Mockito | JWT 有效 → 返回 userId；无 header → 401；invalid JWT → 401；过期 JWT → 401 with proper message |
| **集成** `McpEndpointIT` | Spring Boot Test + MockMvc | POST `/mcp` with Authorization header → 200 + tool list 响应；无 header → 401；调一个真实 tool 验证完整流程 |

**E2E（手工 smoke）**：
1. NAS 部署后，从笔记本登录 growing UI 拿 JWT
2. 配 Claude Desktop `mcpServers` 指到 `http://nas:8082/mcp`
3. 在 Claude 问 "我现在最该跟进哪个面试？" → 应该自动调 `get_active_progress`
4. 在 Claude 问 "AppZen 那个详情" → 应该自动调 `get_application_detail`

## 7. 部署 + 边界

**新增工件**：
- `pom.xml`：加 `spring-ai-starter-mcp-server-webmvc` (Spring AI 1.0)
- `application.properties`：`spring.ai.mcp.server.name=growing` + `spring.ai.mcp.server.transport=streamable-http`
- 不动 `docker-compose.yml`（端口 8082 复用）
- 不动 `database/migrations/`（无 schema 变更）
- README 更新一段「Claude Desktop 配置说明」

**回滚**：单条 git revert 即可（无 schema 变更，无破坏性改动）。

**边界 / 错误处理**：

| 场景 | 处理 |
|---|---|
| Token 过期（24h+） | 401 + message 引导用户重新登录 growing UI 拿新 token |
| 找不存在的 application id | service 抛 404 → MCP 包 `{"error":{"code":404}}` |
| 查另一个用户的 application id | service 抛 403（既有 ownership check）→ MCP 包 `{"error":{"code":403}}` |
| status filter 传中文（如 "面试中"） | service 的 `normalizeStatus` 已支持 → 正常工作 |
| 空结果 | 返回 `[]` 不报错 |
| MCP server 启动时 DB 不可达 | Spring Boot 启动失败（同现有行为，docker compose 会重试） |

## 8. Non-Goals (V2)

- **Mutation tools**（创建/更新申请、写面试记录）— V1 read-only，避免 LLM 误操作 production 数据
- **Streaming responses**（大 list 分块）— V1 一次性返回，应用规模小（<100 个）够用
- **API key 替代 JWT** — 等 24h 过期成为痛点再说
- **OAuth flow** — 单用户/双用户场景下 bearer token 已够；OAuth 是过度设计

## 9. 实施衔接

接下来 OpenSpec 流程会创建：
- `openspec/changes/mcp-job-application-tools/proposal.md`
- `openspec/changes/mcp-job-application-tools/design.md`
- `openspec/changes/mcp-job-application-tools/specs/<capability>/spec.md`
- `openspec/changes/mcp-job-application-tools/tasks.md` (TDD pairs + 每组 review checkpoint + 最终 verification)

运行 `/opsx:apply` 进入 TDD 实施。
