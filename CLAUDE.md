# Growing App - Claude Code Guide

> **Context Recovery**: When resuming sessions, read this file first.
> **Project Root**: `/Users/yanzxu/claude/growing/`

## Stack

Java 17 + Spring Boot 3.2 + Vue 3 + MySQL 8.0

## Project Overview

Growing 是一个个人成长知识管理系统，用于管理自我提升计划、知识库、prompt 等资源。

## Critical Guardrails

### Environment Setup

**NEVER commit `backend/.env`** - Contains DB credentials. Already in `.gitignore`.

**ALWAYS use `./backend/start.sh` to start backend** - Loads environment variables from `backend/.env` and starts Spring Boot.

**Database credentials are in `backend/.env`** - Never hardcode credentials in code or CLAUDE.md.

## Architecture Quick Reference

```
backend/
  src/main/java/com/growing/app/
    controller/     # REST endpoints
    service/        # Business logic
    repository/     # Data access (Spring Data JPA)
    model/          # JPA entities
    dto/            # Data transfer objects
    config/         # Spring configuration

frontend/
  src/
    components/     # Reusable UI components
    views/          # Page components (lazy-loaded routes)
    router/         # Vue Router config
    api/            # Axios client + API calls

prompt/           # Prompt templates storage

database/         # Database migration scripts

requirement/      # Requirements and specifications
```

## Development Status

**Phase 1.1 进行中**

✅ 完成：
- 数据库初始化（users, career_paths, user_career_paths 表）
- 后端用户管理 API（CRUD）
- 后端职业路径查询 API

🔄 进行中：
- 前端项目结构和基础布局

## Quick Start

### 后端启动

```bash
cd backend
./start.sh  # 或者 mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`

API 文档：`http://localhost:8080/api/swagger-ui.html`

### 前端启动（待实现）

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:3000`
