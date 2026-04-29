## Context

growing 是 Spring Boot 3.2 + Vue 3 的求职管理应用，已在 NAS 上 Docker Compose 部署。求职数据现仅能从 Web UI 查看；用户希望在笔记本上的 Claude Desktop 直接通过 MCP 查询数据，让 LLM 帮起草跟进邮件 / 复盘 / 提示该跟进哪个面试。

约束：
- 不引入新 Docker container（部署复杂度低优先）
- 不引入新 tech stack（已有 Java + Spring Boot 足够）
- 复用现有 JWT 认证（don't re-invent auth）
- 复用现有 `JobApplicationService`（user-scoping、submissionType、active-progress 派生字段都在 service 层完成）

参考：完整 brainstorming 设计稿在 `docs/superpowers/specs/2026-04-29-mcp-job-application-tools-design.md`，包含架构图和决策表 Q1-Q4。

## Goals / Non-Goals

**Goals:**
- 笔记本 Claude Desktop 配一个 streamable-http MCP server entry，调到 NAS:8082/mcp 后能用 3 个 tool 查询自己的求职数据
- Auth 复用 growing JWT —— 一个 token 通用 Web UI + MCP
- 用户隔离：austinxu 的 token 只能看 austinxu 的数据
- 嵌入现有 Spring Boot 进程，单镜像部署
- Tool 直接复用 `JobApplicationService`，不走 HTTP loopback

**Non-Goals:**
- Mutation tools（V1 read-only）
- Streaming / 分块响应
- 替代 JWT 的长期 API key
- 重构现有 Spring Security `permitAll()` 全局策略
- 多 MCP transport 支持（仅 streamable-http；不做 stdio）

## Decisions

### D1: Spring AI MCP starter，不是 raw java-sdk
**选项**：
- A. `spring-ai-starter-mcp-server-webmvc`（**采纳**）
- B. `io.modelcontextprotocol.sdk:mcp-bom` raw SDK + 自己接 Servlet

**理由**：A 是 Spring 官方集成，注解风格（`@Tool` 之类），跟 Tomcat 自动绑定；B 要自己写 Servlet/Filter 接管 `/mcp` 路径。A 在已是 Spring Boot 的项目里几乎是 zero-config。代价：版本绑定 Spring AI 1.0+。

### D2: 嵌入 backend 同进程，不开新 container
**选项**：
- A. 嵌入 Spring Boot（**采纳**）
- B. 独立 Node MCP container

**理由**：A 复用现有 deploy 流程（`./deploy.sh` 一条命令）、单 JVM、tool 直接 inject service。B 增加一个 Docker 镜像 + 服务间 JWT。Java MCP SDK 已 GA，成熟度足够。

### D3: 同端口 8082 + 路径 `/mcp`
**选项**：
- A. 复用 8082，新路径 `/mcp`（**采纳**）
- B. 新端口 8083 专给 MCP

**理由**：A 不改 docker-compose（不用加 published port）、不改 NAS 防火墙、不改 Tailscale 路由。Spring AI starter 默认就把 endpoint 挂在主端口。代价：必须确保 `/mcp` 路径不跟现有 controller 冲突——已确认无冲突。

### D4: Auth 复用 growing JWT，手工验证
**选项**：
- A. 复用 JWT，MCP layer 手工调 `JwtUtil` + `AuthService`（**采纳**）
- B. 加 Spring Security filter chain 给 `/mcp/**`
- C. 单独 MCP API key 表

**理由**：A 跟现有 controller 用的是完全相同的 auth 路径，无新代码；B 会触发整个 Spring Security 重构（现状 `.anyRequest().permitAll()` 是 pre-existing concern，不在本 change 范围）；C 多一张表多一套生命周期管理。代价：24h 过期需手工重新粘 token —— V1 可接受。

### D5: Tool 直接调 Service，不绕 HTTP 自己的 controller
**理由**：避免 JWT 重新签发 + JSON serdes + HTTP roundtrip。`JobApplicationService` 的 user-scoping 已经按 userId 过滤，跟从 controller 调一样的 ownership 保证。

### D6: 3 个 tool（list / detail / active_progress），不是 2 也不是 5+
**理由**：用户原本只要 2 个，但 `get_active_progress` 暴露了 dashboard 的派生字段（priority / stalled / microStage / nextAction），是 LLM "怎么推理"的关键 affordance —— 加这一个工具显著提升 AI 用法。再多反而让 Claude 选择困难（MCP 最佳实践：tool 数量精炼，每个用途明确）。

### D7: Output 用 text/plain JSON，不用 application/json structured content
**理由**：MCP spec 主流约定就是 text content + JSON 字符串，Claude 自动 parse。`application/json` 内容块 client 兼容性差。

### D8: Read-only V1，不暴露 mutation tool
**理由**：LLM 误调用 mutation 修改 production 数据风险高（"删掉 Pinterest 那个" 一时口误就完了）。要 mutation 等用户明确反馈需求 + 加 user confirmation 设计后再加（V2 change）。

## Risks / Trade-offs

- **风险**：Spring AI 1.0 是 2025 年末发布，artifact ID 和 property key 仍在快速演化
  → **Mitigation**：实施时第一步对照官方文档（`docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html`）确认；如果有 breaking change，更新依赖即可，业务逻辑独立

- **风险**：从 Web UI localStorage 复制 JWT 到 Claude Desktop config 体验不优雅，且 24h 过期
  → **Trade-off**：V1 可接受。V2 加一个 "Generate MCP token" 按钮在 Profile 页面即可。

- **风险**：Tool 直接调 Service 绕过了 controller 层的请求日志和 GlobalExceptionHandler
  → **Mitigation**：MCP layer 自己 log（标记 `[mcp-tool=...]`）+ 自己 catch ResponseStatusException 转成 `{"error":...}` 文本

- **风险**：MCP server 暴露了 backend 的 8082 端口外部访问 —— 但 growing 现有 `/api/*` 也在这个端口；MCP 不增加暴露面
  → **Trade-off**：跟现有 attack surface 一致。

- **风险**：Spring Security `permitAll()` 全开，理论上谁能连到 8082 就能调 MCP —— 但 MCP 自己手工验 JWT，无 token 直接 401
  → **Trade-off**：分层防御（network + token）。pre-existing Spring Security 重构留给单独 change。

## Migration Plan

无数据迁移。代码部署：
1. 加 `spring-ai-starter-mcp-server-webmvc` 依赖到 `pom.xml`
2. 添加 `application.properties` 的 MCP 配置项
3. 实现 `McpAuthService` + `McpJobTools`
4. 写测试 + 通过
5. `./deploy.sh` 部署
6. 手工配 Claude Desktop 测试 → smoke 三个 tool

回滚：单条 `git revert` 即可（无 schema 变更，无破坏性改动）。

## Open Questions

- 是否在 growing UI 加一个"Copy MCP token" 按钮方便复制？V1 用户从 DevTools 直接拿；用户反馈后再做。
- Tool descriptions 要不要支持中英文？`@Tool(description="...")` 是给 LLM 看的，写中文也行，但英文 LLM 处理更稳定。V1 用英文。
- 多账号场景下，Claude Desktop 如何切换 token？目前要在 config 里配两条 entry（不同 token）。Claude Desktop 暂不支持运行时切换；这是 client 限制，不是我们的问题。
