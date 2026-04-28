# Growing App - Docker Deployment Guide

## 📦 服务架构

Growing App使用Docker Compose部署，包含3个服务：

1. **Backend** - Spring Boot API (端口8082)
2. **Frontend** - Vue.js + Nginx (端口3001)
3. **Backup** - 数据库备份服务 (端口5001)

所有服务在同一个Docker网络中运行，可以通过服务名互相访问。

## 🚀 快速开始

### 1. 环境准备

```bash
# 克隆项目
git clone https://github.com/austinxyz/growing.git
cd growing

# 创建环境变量文件
cp .env.production.example .env
```

### 2. 配置环境变量

编辑`.env`文件，填入实际的数据库凭据：

```bash
# 数据库配置
DB_HOST=your-db-host
DB_PORT=3306
DB_NAME=growing
DB_USER=your_db_username
DB_PASSWORD=your_db_password

# JWT密钥（必须修改为强随机字符串）
JWT_SECRET=your_super_secret_jwt_key_change_this_in_production

# 备份保留策略（可选，使用默认值）
# BACKUP_RETENTION_DAYS=7
# BACKUP_RETENTION_WEEKS=4
# BACKUP_RETENTION_MONTHS=12
```

### 3. 部署启动

```bash
# 使用部署脚本（推荐，自动使用生产模式）
./deploy.sh

# 或手动启动
docker-compose up -d
```

### 4. 验证部署

```bash
# 检查所有服务状态
docker-compose ps

# 应该看到3个服务都是Up状态：
# - growing-backend
# - growing-frontend
# - growing-backup

# 检查后端生产模式
docker-compose logs backend | grep "profiles are active"
# 应显示: The following profiles are active: prod

# 检查备份服务
curl http://localhost:5001/health
# 应返回: {"status": "healthy", "timestamp": "..."}
```

### 5. 访问应用

- **前端页面**: http://your-server:3001
- **后端API**: http://your-server:8082
- **备份管理**: 登录后访问 设置 → 系统管理 → 数据库备份（仅管理员）

## 🔧 服务详情

### Backend服务

- **镜像**: `xuaustin/growing-backend:latest`
- **端口**: 8082
- **环境变量**:
  - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - 数据库连接
  - `JWT_SECRET` - JWT令牌密钥
  - `SPRING_PROFILES_ACTIVE=prod` - 生产模式（固定）
  - `BACKUP_WEBHOOK_URL=http://backup:5000` - 备份服务URL（自动配置）

### Frontend服务

- **镜像**: `xuaustin/growing-frontend:latest`
- **端口**: 3001（映射到容器的80端口）
- **功能**: Vue.js应用 + Nginx静态文件服务器

### Backup服务

- **镜像**: `xuaustin/growing-backup:latest`
- **端口**: 5001（映射到容器的5000端口）
- **功能**:
  - 定时备份（每日02:00 UTC，每周六03:00，每月1日04:00）
  - 手动备份触发
  - 备份文件管理
  - 数据库恢复
- **数据卷**: `backup-data` - 存储所有备份文件

## 📊 备份功能

### 自动备份计划

备份服务自动执行以下备份：

- **每日备份**: 每天02:00 UTC，保留7天
- **每周备份**: 每周六03:00 UTC，保留4周
- **每月备份**: 每月1日04:00 UTC，保留12个月

### 手动备份

通过前端界面（仅管理员）：

1. 登录管理员账号
2. 导航至：设置 → 系统管理 → 数据库备份
3. 点击"手动备份"按钮

### 恢复备份

⚠️ **危险操作** - 会覆盖当前数据库！

1. 在备份管理页面选择要恢复的备份文件
2. 点击"恢复"按钮
3. 输入数据库名称（`growing`）确认
4. 等待恢复完成（自动刷新页面）

### 查看备份文件

所有备份文件存储在Docker数据卷`backup-data`中：

```bash
# 列出备份文件
docker exec growing-backup ls -lh /backups/daily
docker exec growing-backup ls -lh /backups/weekly
docker exec growing-backup ls -lh /backups/monthly

# 复制备份文件到本地
docker cp growing-backup:/backups/daily/growing_daily_20260118_020000.sql.gz ./
```

## 🔄 更新部署

### 方法1：拉取最新镜像（推荐）

```bash
# 拉取最新镜像
docker-compose pull

# 重新创建并启动容器
docker-compose up -d

# 清理旧镜像
docker image prune -f
```

### 方法2：完全重新部署

```bash
# 停止并删除所有容器
docker-compose down

# 拉取最新镜像
docker-compose pull

# 启动服务
docker-compose up -d
```

⚠️ **注意**: 备份数据卷`backup-data`会保留，不会被删除。

## 🛠️ 维护操作

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs backup

# 实时跟踪日志
docker-compose logs -f backend
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart backend
docker-compose restart backup
```

### 停止服务

```bash
# 停止但不删除容器
docker-compose stop

# 停止并删除容器（保留数据卷）
docker-compose down

# 停止并删除容器和数据卷（危险！）
docker-compose down -v
```

### 进入容器调试

```bash
# 进入backend容器
docker exec -it growing-backend bash

# 进入backup容器
docker exec -it growing-backup bash

# 执行数据库操作
docker exec growing-backup mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME -e "SHOW TABLES;"
```

## 🏗️ 构建自定义镜像

如果需要修改代码并构建自己的镜像：

```bash
# 构建并推送多平台镜像（amd64 + arm64）
./build-multiarch.sh v1.0.1

# 或单独构建某个服务
cd backend
docker build -t your-username/growing-backend:latest .

cd ../frontend
docker build -t your-username/growing-frontend:latest .

cd ../backup
docker build -t your-username/growing-backup:latest .
```

然后修改`docker-compose.yml`中的镜像名称为你的镜像。

## 🔐 安全建议

1. **修改JWT密钥**: `.env`中的`JWT_SECRET`必须是强随机字符串
   ```bash
   # 生成新密钥
   openssl rand -base64 64
   ```

2. **数据库密码**: 使用强密码，不要使用默认值

3. **防火墙配置**:
   - 端口3001（前端）需要对外开放
   - 端口8082（后端）和5001（备份）可以只在内网开放

4. **HTTPS配置**: 生产环境建议使用Nginx反向代理配置HTTPS

5. **备份数据保护**: 备份文件包含敏感数据，需要：
   - 定期复制到安全的异地存储
   - 限制`backup-data`数据卷的访问权限
   - 加密备份文件（如需要）

## 📈 监控和健康检查

所有服务都配置了健康检查：

```bash
# 查看健康状态
docker inspect growing-backend --format='{{json .State.Health}}' | jq .
docker inspect growing-frontend --format='{{json .State.Health}}' | jq .
docker inspect growing-backup --format='{{json .State.Health}}' | jq .
```

健康检查配置：
- **Backend**: 每30秒检查`/api/career-paths`端点
- **Frontend**: 每30秒检查首页
- **Backup**: 每30秒检查`/health`端点

## ❓ 常见问题

### Q: Backend启动失败，提示数据库连接错误

检查`.env`文件中的数据库配置是否正确：
```bash
docker-compose logs backend | grep -i "error\|exception"
```

### Q: Backup服务提示数据库连接失败

这是正常的！Backup服务在容器启动时会测试数据库连接，但即使失败也会继续启动。实际备份会在定时任务触发时执行。

如果数据库在远程主机且Docker网络无法访问，备份功能将无法正常工作。

### Q: 前端页面空白或404

检查frontend容器是否正常运行：
```bash
docker-compose ps frontend
docker-compose logs frontend
```

### Q: 备份文件占用太多空间

调整保留策略（`.env`文件）：
```bash
BACKUP_RETENTION_DAYS=3    # 减少到3天
BACKUP_RETENTION_WEEKS=2   # 减少到2周
BACKUP_RETENTION_MONTHS=6  # 减少到6个月
```

然后重启backup服务：
```bash
docker-compose restart backup
```

## 📞 技术支持

- **GitHub**: https://github.com/austinxyz/growing
- **文档**: 查看`docs/`目录下的详细文档
- **备份系统**: 查看`docs/BACKUP_SYSTEM.md`
