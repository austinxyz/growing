# API 测试脚本

这个目录包含了用于测试 Growing App API 的脚本和工具。

## 前置条件

1. 后端服务运行在 `http://localhost:8080`
2. 前端服务运行在 `http://localhost:3000`
3. 测试账号：`austinxu` / `helloworld`

## 脚本说明

### test_skills.sh

测试 Career Paths 和 Skills API。

**测试内容**：
- GET `/api/career-paths` - 获取所有职业路径
- GET `/api/skills/career-path/1` - 获取软件工程师的技能
- GET `/api/skills/career-path/2` - 获取职业经理的技能

**运行**：
```bash
cd test
./test_skills.sh
```

**预期输出**：
- Career paths JSON（包含职业路径列表）
- Skills JSON（包含技能树，带 focusAreas）

### test_api.sh

测试用户个人 Career Paths API。

**测试内容**：
- POST `/api/auth/login` - 登录获取 token
- GET `/api/career-paths/my` - 获取当前用户的职业路径

**运行**：
```bash
cd test
./test_api.sh
```

**预期输出**：
- Token (前20个字符)
- 当前用户关联的职业路径 JSON

### test_api.html

浏览器端 API 测试工具（支持 CORS）。

**测试内容**：
- 登录并获取 token
- 获取所有 career paths
- 为每个 career path 获取对应的 skills 和 focus areas

**运行**：
1. 在浏览器中打开 `test/test_api.html`
2. 查看页面输出

**预期输出**：
- Token received 消息
- Career Paths 列表
- 每个 Career Path 的 Skills 列表（包括 Focus Areas）

## 故障排除

### 401 Unauthorized
- 检查 JWT_SECRET 是否匹配
- Token 可能已过期（24小时有效期）
- 重新登录获取新 token

### 404 Not Found
- 确认后端服务已启动
- 检查 API endpoint 路径是否正确

### CORS 错误
- 确认前端在 `http://localhost:3000`
- 检查 `SecurityConfig.java` 的 CORS 配置

### Connection Refused
- 后端服务未启动 → `cd backend && ./start.sh`
- 数据库未运行 → 检查 MySQL 连接

## 相关文档

- [CLAUDE.md](../CLAUDE.md) - 项目快速指南
- [ARCHITECTURE.md](../docs/ARCHITECTURE.md) - 架构文档
- [Phase 1 设计文档](../docs/Phase1-设计文档.md) - 用户认证和管理
- [Phase 2 设计文档](../docs/Phase2-设计文档.md) - 技能和学习资源管理
- [Phase 3 设计文档](../docs/Phase3-设计文档.md) - 试题库功能
- [Phase 4 设计文档](../docs/Phase4-设计文档.md) - 算法与数据结构学习模块
