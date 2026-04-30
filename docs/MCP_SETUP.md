# Connecting Claude Desktop to growing's MCP Server

growing exposes a Model Context Protocol (MCP) server alongside its REST API.
Claude Desktop / Claude Code can call into your job-application data via three
read-only tools: `list_applications`, `get_application_detail`,
`get_active_progress`.

## Quick Start

### 1. Get a JWT from growing

1. Open growing in your browser (e.g. `http://localhost:3001` for local dev,
   `http://your-nas:3001` for NAS deployment) and log in.
2. Open DevTools → **Application** → **Local Storage** → your origin →
   copy the value of the `token` key. It looks like `eyJhbGciOiJIUzUxMiJ9...`.

The JWT is valid for **24 hours** — when it expires, MCP tools start
returning `401`. Just log into growing again and copy a fresh token.

### 2. Configure Claude Desktop

Edit `claude_desktop_config.json` (find its path via Settings → Developer
→ Edit Config in Claude Desktop):

```jsonc
{
  "mcpServers": {
    "growing": {
      "url": "http://localhost:8082/mcp/sse",
      "headers": {
        "Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9...your-jwt-here..."
      }
    }
  }
}
```

For NAS deployment, replace `localhost:8082` with your NAS host:port.

### 3. Restart Claude Desktop

After saving the config, fully quit and reopen Claude Desktop. The growing
MCP server should appear in the tools picker.

## Multi-Account Setup

If you have multiple growing accounts (e.g. `austinxu` and `austin.xyz`),
each gets its own JWT and its own MCP server entry — Claude can query both
in the same conversation:

```jsonc
{
  "mcpServers": {
    "growing-austinxu":  {
      "url": "http://localhost:8082/mcp/sse",
      "headers": { "Authorization": "Bearer <token-for-austinxu>" }
    },
    "growing-austinxyz": {
      "url": "http://localhost:8082/mcp/sse",
      "headers": { "Authorization": "Bearer <token-for-austinxyz>" }
    }
  }
}
```

## Available Tools

### `list_applications`
List your job applications, optionally filtered by status.
Returns trimmed summaries (no qualifications/responsibilities/notes — fetch
those via `get_application_detail`).

**Parameter** (optional): `status` — accepts canonical English (`Applied`,
`Screening`, `Interviewing`, `Offer`, `Rejected`, `Withdrawn`) or Chinese
variants (`已投递`, `筛选中`, `面试中`, `已拒绝`, `已撤回`). Omit for all.

**Example prompt**:
> "What jobs am I currently interviewing for?"

### `get_application_detail`
Get the full detail of a single application: stages, interview records,
recruiter insights, qualifications, responsibilities, notes.

**Parameter** (required): `id` — the application id.

**Example prompt**:
> "Help me draft a follow-up email for the AppZen Engineering Manager role."

### `get_active_progress`
Return active applications with derived dashboard fields (priority, stalled
detection, micro-stage label, next action, days elapsed). Sorted by priority
then days. This is the most useful tool for "what should I focus on" reasoning.

**Parameter** (optional): `include_closed` — when `true`, also include
Rejected/Withdrawn applications. Default `false`.

**Example prompts**:
> "What interview should I prep for next?"
> "Are any of my applications stalling?"
> "Summarize where I stand across all my open applications."

## Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| All tools return 401 | JWT expired (24h) | Log into growing UI, copy fresh token, update config, restart Claude Desktop |
| Tools don't appear in Claude | Config syntax error | Validate JSON; check Claude Desktop logs (`~/Library/Logs/Claude/` on macOS) |
| 401 immediately on connect | Wrong NAS host or port | Confirm growing is reachable: `curl http://nas:8082/api/career-paths` |
| Tools appear but say "no applications" | Wrong account | The JWT scopes queries to its user; check you copied the right account's token |

## How It Works (for the curious)

- Transport: **SSE** (Server-Sent Events) — Spring AI 1.0 ships SSE only;
  Streamable HTTP is expected in 1.1+
- Endpoints: `GET /mcp/sse` (event stream) + `POST /mcp/message?sessionId=…`
  (per-session JSON-RPC messages)
- Auth: Bearer JWT on every request, validated by `McpAuthFilter` against
  the same `JwtUtil` + `AuthService` used by the REST controllers
- User scoping: tool methods read `userId` from a thread-local populated
  by the auth filter; every service call is scoped to that user, so
  account A's token cannot see account B's data
- Read-only: V1 exposes no mutation tools to avoid LLM accidentally
  modifying production data ("delete the Pinterest one" — no thanks)

See [`openspec/changes/archive/.../mcp-job-application-tools/`](../openspec/)
once archived for the full design + spec + tasks history.
