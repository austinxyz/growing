# Docker Production Deployment Guide

## 🚀 快速部署

### 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- 访问数据库服务器（10.0.0.7:37719）

### 部署步骤

1. **创建环境变量文件**

```bash
# 从模板创建.env文件
cp .env.production.example .env

# 编辑.env文件，填入实际值
vim .env
```

2. **运行部署脚本**

```bash
./deploy.sh
```

脚本会自动：
- ✅ 验证环境变量
- ✅ 拉取最新镜像
- ✅ 停止旧容器
- ✅ 启动新容器（生产模式）
- ✅ 显示状态和日志

### 手动部署（不使用脚本）

```bash
# 1. 创建.env文件（见上）

# 2. 拉取镜像
docker-compose pull

# 3. 启动服务
docker-compose up -d

# 4. 查看状态
docker-compose ps

# 5. 查看日志
docker-compose logs -f
```

## 🔧 配置说明

### Docker Compose环境变量

在`.env`文件中配置：

```bash
# 数据库配置
DB_HOST=10.0.0.7          # 数据库主机
DB_PORT=37719             # 数据库端口
DB_NAME=growing           # 数据库名称
DB_USER=your_username     # 数据库用户名
DB_PASSWORD=your_password # 数据库密码

# JWT密钥（重要！）
JWT_SECRET=your_super_secret_jwt_key_change_this_in_production
```

### Spring Profile配置

**自动设置为生产模式**：

- **Dockerfile**：默认 `ENV SPRING_PROFILES_ACTIVE=prod`
- **docker-compose.yml**：显式设置 `SPRING_PROFILES_ACTIVE: prod`

这确保容器启动时**始终使用生产环境配置**：
- ✅ 关闭SQL日志
- ✅ 优化性能
- ✅ 禁用API文档（Swagger）
- ✅ 隐藏错误详情

### 验证生产模式

启动后检查日志：

```bash
docker-compose logs backend | grep "profiles are active"
```

应该看到：
```
The following profiles are active: prod
```

如果看到`dev`，说明配置有误！

## 📊 服务说明

### Backend服务

- **镜像**: `xuaustin/growing-backend:latest`
- **端口**: 8082
- **健康检查**: `http://localhost:8082/api/career-paths`
- **内存配置**: 512MB - 1GB
- **日志等级**: INFO（生产模式）

### Frontend服务

- **镜像**: `xuaustin/growing-frontend:latest`
- **端口**: 3001 (nginx监听80，映射到宿主机3001)
- **健康检查**: `http://localhost/`
- **依赖**: backend服务

## 🔍 常用命令

### 查看状态

```bash
# 所有服务状态
docker-compose ps

# 详细信息
docker-compose ps -a
```

### 查看日志

```bash
# 所有服务日志（实时）
docker-compose logs -f

# 只看Backend日志
docker-compose logs -f backend

# 只看Frontend日志
docker-compose logs -f frontend

# 最近100行日志
docker-compose logs --tail=100

# 查看特定时间范围
docker-compose logs --since 1h
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启单个服务
docker-compose restart backend
docker-compose restart frontend
```

### 停止服务

```bash
# 停止服务（保留容器）
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器、网络、卷
docker-compose down -v
```

### 更新服务

```bash
# 拉取最新镜像
docker-compose pull

# 重新创建并启动
docker-compose up -d --force-recreate

# 或使用部署脚本
./deploy.sh
```

## 🐛 故障排查

### 问题1: Backend启动失败

**检查日志**：
```bash
docker-compose logs backend
```

**常见原因**：
- 数据库连接失败（检查DB_HOST, DB_PORT, DB_USER, DB_PASSWORD）
- JWT_SECRET未设置
- 端口8082被占用

**解决方法**：
```bash
# 检查.env文件
cat .env

# 检查端口占用
lsof -i :8082

# 重新启动
docker-compose restart backend
```

### 问题2: 仍然看到SQL日志

**原因**: 可能使用了开发模式

**检查**：
```bash
docker-compose logs backend | grep "profiles are active"
```

**修复**：
确保`docker-compose.yml`中有：
```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod
```

然后重新启动：
```bash
docker-compose down
docker-compose up -d
```

### 问题3: Swagger UI仍然可访问

**原因**: 未使用生产模式

**验证**：
```bash
# 应该返回404
curl http://localhost:8082/api/swagger-ui.html
```

**修复**: 确认生产模式（见问题2）

### 问题4: 健康检查失败

**检查**：
```bash
docker-compose ps
# 如果显示 unhealthy，查看日志
docker-compose logs backend
```

**常见原因**：
- 应用启动慢（等待更长时间）
- 数据库连接失败
- 内存不足

**调整健康检查**：
编辑`docker-compose.yml`，增加`start_period`:
```yaml
healthcheck:
  start_period: 120s  # 从60s增加到120s
```

## 🔒 安全建议

### 1. JWT密钥

**生成强密钥**：
```bash
openssl rand -base64 64
```

**保护密钥**：
- ❌ 不要提交到Git
- ✅ 使用环境变量
- ✅ 定期轮换（每3-6个月）

### 2. 数据库凭据

- ❌ 不要使用默认密码
- ✅ 使用强密码（16+字符）
- ✅ 限制数据库访问IP

### 3. 网络暴露

**生产环境建议**：
```yaml
# 不暴露Backend端口到公网
ports:
  - "127.0.0.1:8082:8082"  # 只允许本地访问

# 或使用Nginx反向代理，不暴露Backend
```

### 4. 日志安全

生产模式已经：
- ✅ 隐藏SQL参数（可能包含敏感数据）
- ✅ 隐藏错误堆栈
- ✅ 禁用API文档

## 📈 性能优化

### JVM内存调优

编辑`.env`或`docker-compose.yml`:

```bash
# 小型部署（<100用户）
JAVA_OPTS=-Xms256m -Xmx512m

# 中型部署（100-500用户）
JAVA_OPTS=-Xms512m -Xmx1024m

# 大型部署（500+用户）
JAVA_OPTS=-Xms1024m -Xmx2048m
```

### 数据库连接池

默认配置在`application-prod.properties`中。

如需调整，在`docker-compose.yml`添加：
```yaml
environment:
  SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE: 20
  SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE: 5
```

## 🔄 CI/CD集成

### GitHub Actions示例

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /path/to/growing
            git pull
            ./deploy.sh
```

## 📋 部署检查清单

部署前确认：

- [ ] `.env` 文件已创建并配置正确
- [ ] JWT_SECRET 已设置且足够强（32+字符）
- [ ] 数据库凭据正确
- [ ] Docker和Docker Compose已安装
- [ ] 端口3001和8082未被占用
- [ ] 网络可访问数据库服务器

部署后验证：

- [ ] 服务状态正常：`docker-compose ps`
- [ ] 生产模式激活：`docker-compose logs backend | grep "profiles are active: prod"`
- [ ] 无SQL日志：`docker-compose logs backend` 不显示Hibernate SQL
- [ ] Swagger UI禁用：`curl http://localhost:8082/api/swagger-ui.html` 返回404
- [ ] 前端可访问：`curl http://localhost:3001`
- [ ] 后端健康检查通过：`curl http://localhost:8082/api/career-paths`

## 📚 相关文档

- **本地开发**: `/backend/start.sh` - 开发模式启动
- **部署指南**: `/backend/DEPLOYMENT.md` - 环境配置详解
- **快速参考**: `/CLAUDE.md` - 守则和快速启动
