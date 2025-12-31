# Docker 部署检查清单

## 部署到 10.0.0.7 前的准备

### 1. 环境变量配置

在 10.0.0.7 服务器上创建 `.env` 文件：

```bash
# 复制模板
cp .env.example .env

# 编辑配置
vim .env
```

必须配置的变量：
- `DB_USER`: 数据库用户名（austinxu）
- `DB_PASSWORD`: 数据库密码（helloworld）
- `JWT_SECRET`: JWT密钥（至少256位，生产环境必须修改！）

### 2. 后端CORS配置检查

✅ **已配置**：`backend/src/main/java/com/growing/app/config/SecurityConfig.java` 已允许：
- `http://10.0.0.7:3001` - Docker部署地址
- `http://10.0.0.13:3001` - 开发环境地址

如果部署到其他地址，需要添加到 `allowedOrigins` 列表。

### 3. 前端Nginx配置检查

✅ **已配置**：`frontend/nginx.conf`
- 已配置 `/api/` 代理到 `http://backend:8082/api/`
- 已添加 `proxy_set_header Origin $http_origin` 支持CORS
- SPA路由支持（所有路径fallback到index.html）

### 4. Docker网络架构

```
用户浏览器 (http://10.0.0.7:3001)
    ↓
Frontend Container (nginx:80) → 映射到宿主机 3001端口
    ↓ /api/* 请求
Backend Container (Spring Boot:8082) → 映射到宿主机 8082端口
    ↓
MySQL (10.0.0.7:37719) - 宿主机数据库
```

## 部署步骤

### 方式1：使用已构建的Docker镜像（推荐）

```bash
# 1. 在10.0.0.7上创建.env文件
cd /path/to/growing
cp .env.example .env
vim .env  # 填入DB_USER, DB_PASSWORD, JWT_SECRET

# 2. 启动服务
docker-compose pull  # 拉取最新镜像
docker-compose up -d

# 3. 检查状态
docker-compose ps
docker-compose logs -f

# 4. 测试
curl http://10.0.0.7:3001
curl -X POST http://10.0.0.7:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"austinxu","password":"helloworld"}'
```

### 方式2：本地构建并推送镜像

```bash
# 1. 构建并推送镜像
./build-and-push.sh

# 2. 在10.0.0.7上执行方式1的步骤
```

## 常见问题排查

### 问题1：登录返回403 Forbidden

**原因**：CORS配置问题

**检查**：
```bash
# 检查后端日志
docker-compose logs backend | grep -i cors

# 测试CORS预检
curl -X OPTIONS http://10.0.0.7:8082/api/auth/login \
  -H "Origin: http://10.0.0.7:3001" \
  -H "Access-Control-Request-Method: POST" \
  -v
```

**解决**：
1. 确认 `SecurityConfig.java` 中 `allowedOrigins` 包含访问地址
2. 确认后端已重启：`docker-compose restart backend`

### 问题2：前端显示API请求404

**原因**：Nginx代理配置问题

**检查**：
```bash
# 进入前端容器检查nginx配置
docker exec -it growing-frontend cat /etc/nginx/conf.d/default.conf

# 检查nginx日志
docker-compose logs frontend
```

**解决**：
1. 确认 `nginx.conf` 中 `proxy_pass http://backend:8082/api/`
2. 重启前端：`docker-compose restart frontend`

### 问题3：数据库连接失败

**原因**：环境变量或网络问题

**检查**：
```bash
# 检查后端容器能否访问数据库
docker exec -it growing-backend sh
wget --spider mysql://10.0.0.7:37719

# 检查环境变量
docker-compose config | grep DB_
```

**解决**：
1. 确认 `.env` 文件中数据库配置正确
2. 确认宿主机防火墙允许容器访问37719端口
3. 重启后端：`docker-compose restart backend`

### 问题4：JWT token无效

**原因**：JWT_SECRET不一致

**检查**：
```bash
# 确认.env文件中JWT_SECRET已设置
cat .env | grep JWT_SECRET
```

**解决**：
1. 确保 `.env` 中 `JWT_SECRET` 已设置（至少256位）
2. 重启后端：`docker-compose restart backend`
3. 清除浏览器localStorage，重新登录

## 端口使用

- **3001**: 前端Nginx（映射容器80端口）
- **8082**: 后端Spring Boot（映射容器8082端口）
- **37719**: MySQL数据库（宿主机）

## 健康检查

所有服务都配置了健康检查：

```bash
# 查看健康状态
docker-compose ps

# 前端健康检查
curl http://10.0.0.7:3001/

# 后端健康检查
curl http://10.0.0.7:8082/api/career-paths
```

## 更新部署

```bash
# 1. 拉取最新镜像
docker-compose pull

# 2. 重启服务（保留数据）
docker-compose up -d

# 3. 查看日志确认启动成功
docker-compose logs -f
```

## 回滚

```bash
# 使用特定版本标签
docker-compose down
docker run -d --name growing-backend xuaustin/growing-backend:v1.0.0
docker run -d --name growing-frontend xuaustin/growing-frontend:v1.0.0
```

## 监控

```bash
# 实时查看所有日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 查看容器资源使用
docker stats growing-backend growing-frontend
```

## 安全建议

1. **生产环境必须修改JWT_SECRET**：使用至少256位随机字符串
2. **定期更新Docker镜像**：包含安全补丁
3. **限制端口访问**：使用防火墙限制8082端口仅内网访问
4. **启用HTTPS**：配置nginx SSL证书（生产环境）
5. **备份数据库**：定期备份MySQL数据

## Google OAuth配置（可选）

如需使用Google登录，需在Google Cloud Console配置：

1. 添加授权的JavaScript来源：`http://10.0.0.7:3001`
2. 添加授权的重定向URI：`http://10.0.0.7:3001`
3. 更新 `Login.vue` 中的 `GOOGLE_CLIENT_ID`
