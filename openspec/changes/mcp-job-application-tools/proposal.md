## Why

求职管理数据现在只能在 growing Web UI 里看，但用户希望在笔记本的 Claude Desktop / Claude Code 里能直接问"我现在最该跟进哪个面试？" / "AppZen 那个职位的详情？"，让 LLM 帮忙起草跟进邮件、复盘分析、节奏判断 —— 不用切到浏览器手动复制粘贴。growing 已部署在 NAS 上，MCP server 应跟 backend 一起部署在同一 container，从笔记本通过 NAS 网络访问。

## What Changes

- 新增 **MCP server** 嵌入到现有 Spring Boot backend（同 JVM、同 container），暴露 streamable-http transport 在 `POST /mcp`
- 暴露 **3 个只读 tool**:
  - `list_applications(status?)` — 列出申请，可选状态过滤（接受英文/中文 status）
  - `get_application_detail(id)` — 单个申请完整详情（含 stages / records / recruiterInsights）
  - `get_active_progress(include_closed?)` — 复用现有 `/active-progress` 派生数据（priority、stalled、microStage、nextAction），AI 推理"接下来该关注什么"的核心 tool
- **Auth 复用现有 JWT**：用户登录 growing UI 拿 token，粘到 Claude Desktop 配置 `Authorization: Bearer <token>`；MCP 验证 JWT 提取 userId，所有查询自动 scope 到该用户
- **Tool 直接调 Service 层**（不绕 HTTP 层），复用 `JobApplicationService` 的 user-scoping
- 不引入新数据库变更；不开新 controller；现有 `/api/*` 端点不动

## Capabilities

### New Capabilities

- `mcp-server`: growing 暴露的 Model Context Protocol 服务器能力——3 个只读 tool 让 MCP client（Claude Desktop / Claude Code）查询当前用户的求职管理数据。包含 transport（streamable-http）、auth（Bearer JWT）、tool 契约（input/output schema）的标准化定义。

### Modified Capabilities

无（MCP 是个独立 capability，跟 `interview-progress` 共用 `JobApplicationService` 但不修改其 requirements）。

## Impact

**新依赖**:
- `pom.xml`：加 `spring-ai-starter-mcp-server-webmvc` (Spring AI 1.0)

**后端**:
- `backend/src/main/java/com/growing/app/mcp/McpJobTools.java` (新)：注册 3 个 tool，方法实现直接调 service
- `backend/src/main/java/com/growing/app/mcp/McpAuthService.java` (新)：从 HTTP request 提取 + 验证 JWT，返回 userId
- `application.properties`：`spring.ai.mcp.server.name=growing` + `spring.ai.mcp.server.transport=streamable-http`

**配置/部署**:
- `docker-compose.yml`：不改（端口 8082 复用，MCP 走 `/mcp` 路径）
- `README.md` 加一段「Claude Desktop 配置说明」

**数据库**: 无 schema 变更。

**测试**:
- `McpJobToolsTest` (Mockito)：3 个 tool 的输入校验 / output 序列化 / 错误路径
- `McpAuthServiceTest`：JWT 验证四种状态（valid / missing / invalid / expired）
- `McpEndpointIT` (Spring Boot Test + MockMvc)：端到端集成

**风险 / 依赖**:
- Spring AI MCP starter 是 2025 年新发布的（1.0 GA 末），artifact ID 和 property key 实施时需对照 Spring AI 文档确认
- Bearer token 从 growing UI 复制粘贴，24h 过期需要 refresh —— V1 可接受，V2 考虑 API key
- 现有 SecurityConfig 是 `permitAll()` 全开（pre-existing concern），MCP 自己手工验 JWT 不依赖 Spring Security filter 链；不在本 change 里重构整个 Spring Security

**Out of Scope (V2)**:
- Mutation tools（创建/更新申请、写面试记录）—— V1 read-only 避免 LLM 误操作
- Streaming responses（大 list 分块）—— 应用规模小（<100 个）一次返回够用
- API key / OAuth flow 替代 JWT
- 把 SecurityConfig 全局 `permitAll()` 重构成 authenticated 模式

## References

- Brainstorming spec: [`docs/superpowers/specs/2026-04-29-mcp-job-application-tools-design.md`](../../../docs/superpowers/specs/2026-04-29-mcp-job-application-tools-design.md)
- Existing service to reuse: `backend/src/main/java/com/growing/app/service/JobApplicationService.java`
- MCP spec: <https://modelcontextprotocol.io/>
