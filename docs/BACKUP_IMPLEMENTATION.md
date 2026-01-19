# Database Backup System - Implementation Summary

参照 finance 项目实现的自动化数据库备份系统。

## 实现内容

### 1. 核心文件

#### Docker 相关
- `backup/Dockerfile` - 备份服务容器定义
- `backup/docker-compose.yml` - 本地开发配置
- `backup/entrypoint.sh` - 容器启动脚本
- `backup/build-multiarch.sh` - 多架构构建脚本 (amd64 + arm64)

#### 备份脚本
- `backup/scripts/backup.sh` - 备份执行脚本
  - 支持 daily, weekly, monthly, manual 四种类型
  - 自动清理过期备份
  - 记录备份元数据
- `backup/scripts/restore.sh` - 恢复执行脚本
  - 恢复前自动创建快照
  - 安全的数据库恢复流程

#### API 服务
- `backup/webhook.py` - Flask API 服务
  - `/health` - 健康检查
  - `/backup/status` - 获取系统状态
  - `/backup/list` - 列出备份文件
  - `/backup/trigger` - 触发手动备份
  - `/backup/restore` - 恢复备份
  - `/backup/logs` - 获取日志

#### 定时任务
- `backup/crontab` - Cron 定时任务配置
  - 每日备份: 02:00 UTC
  - 周备份: 03:00 UTC (周六)
  - 月备份: 04:00 UTC (每月1号)

#### 工具脚本
- `backup/test-backup.sh` - 测试脚本
- `backup/diagnose-backup.sh` - 诊断工具

### 2. 文档

- `docs/BACKUP_SYSTEM.md` - 完整的系统文档
  - 架构说明
  - 使用指南
  - API 文档
  - 故障排查
- `backup/README.md` - 快速开始指南
- `backup/.gitignore` - 忽略备份文件

### 3. CLAUDE.md 更新

在 "Database" 部分后添加了 "Database Backup" 部分，包含：
- Docker 容器启动命令
- 手动备份触发方法
- 查看备份列表方法
- 指向详细文档的链接

## 关键特性

### 自动化
- ✅ 定时自动备份 (每日/周/月)
- ✅ 自动清理过期备份
- ✅ 恢复前自动创建快照

### 安全性
- ✅ 数据库名称确认 (恢复前必须输入正确的数据库名)
- ✅ 容器隔离运行
- ✅ 环境变量存储凭据

### 可靠性
- ✅ 健康检查 (Docker healthcheck)
- ✅ 压缩备份 (gzip, 减少 70-80% 空间)
- ✅ 使用 --single-transaction (避免锁表)

### 监控
- ✅ 磁盘使用监控
- ✅ 备份历史查看
- ✅ 详细日志记录
- ✅ 诊断工具

## 端口配置

**重要**: 使用端口 **5001** 而非 5000，避免与 finance 项目冲突。

- 容器内部: 5000 (Flask 服务)
- 主机映射: 5001 (避免与 finance:5000 冲突)

访问 API:
```bash
curl http://localhost:5001/health
curl http://localhost:5001/backup/status
```

## 与 Finance 项目的差异

### 相同点
1. 架构设计完全一致
2. API 接口兼容
3. 备份流程相同
4. 安全机制相同

### 差异点
1. **端口**: Growing 使用 5001, Finance 使用 5000
2. **数据库名**: Growing 使用 "growing", Finance 使用 "finance"
3. **备份文件前缀**: Growing 使用 "growing_", Finance 使用 "finance_"

## 快速测试

### 1. 构建并启动

```bash
cd backup
docker-compose up -d
```

或手动启动:
```bash
docker build -t growing-backup:local .
docker run -d --name growing-backup \
  -e DB_HOST=10.0.0.7 \
  -e DB_PORT=37719 \
  -e DB_USER=austinxu \
  -e DB_PASSWORD=helloworld \
  -e DB_NAME=growing \
  -v $(pwd)/backups:/backups \
  -p 5001:5000 \
  growing-backup:local
```

### 2. 运行诊断

```bash
./diagnose-backup.sh
```

### 3. 测试备份

```bash
./test-backup.sh
```

### 4. 手动触发备份

```bash
curl -X POST http://localhost:5001/backup/trigger \
  -H "Content-Type: application/json" \
  -d '{"type":"manual"}'
```

### 5. 查看备份文件

```bash
curl http://localhost:5001/backup/list | jq .
```

## 生产部署

### 多架构构建

```bash
cd backup
./build-multiarch.sh
# 构建并推送到 xuaustin/growing-backup:latest
```

### 拉取并运行

```bash
docker run -d \
  --name growing-backup \
  -e DB_HOST=your_mysql_host \
  -e DB_PORT=3306 \
  -e DB_USER=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e DB_NAME=growing \
  -v /mnt/backups:/backups \
  -p 5001:5000 \
  xuaustin/growing-backup:latest
```

## 备份保留策略

- **每日备份**: 保留 7 天
- **周备份**: 保留 4 周
- **月备份**: 保留 12 个月
- **手动备份**: 不自动删除

## 下一步

如需集成到后端应用:

1. 在 `backend/.env` 添加:
   ```
   BACKUP_WEBHOOK_URL=http://localhost:5001
   ```

2. 创建 `BackupService.java` 和 `BackupController.java` (参考 finance 项目)

3. 创建前端管理页面 `BackupManagement.vue`

## 参考资料

- Finance 备份系统: `~/claude/finance/backup/`
- Finance 文档: `~/claude/finance/docs/backup-system.md`
- Growing 完整文档: `docs/BACKUP_SYSTEM.md`
