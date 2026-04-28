# Database Backup & Restore System

Growing app 的自动化数据库备份系统，提供定时备份、手动触发和安全恢复功能。

## 架构

备份系统运行在独立的 Docker 容器中，包含：
- **Python Flask** - 提供 HTTP API 接口
- **Cron** - 定时任务调度
- **MySQL Client** - 数据库操作
- **健康检查** - 服务监控

## 功能特性

### 自动备份
- **每日备份** - 每天凌晨 2:00 (UTC)
- **周备份** - 每周六凌晨 3:00 (UTC)
- **月备份** - 每月 1 号凌晨 4:00 (UTC)

### 保留策略
- **每日备份**: 保留最近 7 天
- **周备份**: 保留最近 4 周
- **月备份**: 保留最近 12 个月

### 手动操作
- **手动备份** - 通过管理后台或 API 触发
- **安全恢复** - 恢复前需要确认数据库名称
- **备份监控** - 磁盘使用、备份历史和日志查看

### 安全性
- **数据库名称验证** - 恢复前必须输入正确的数据库名称
- **仅限管理员** - 只有管理员用户可以触发备份或恢复
- **容器隔离** - 备份服务运行在独立容器中
- **恢复前快照** - 恢复操作会自动创建当前数据库快照

## 快速开始

### 1. 构建 Docker 镜像

**本地构建**:
```bash
cd backup
docker build -t growing-backup:local .
```

**多架构构建** (amd64 + arm64):
```bash
cd backup
./build-multiarch.sh
# 构建并推送到 xuaustin/growing-backup:latest
```

### 2. 运行备份容器

**本地开发**:
```bash
docker run -d \
  --name growing-backup \
  -e DB_HOST=your-db-host \
  -e DB_PORT=3306 \
  -e DB_USER=your_db_username \
  -e DB_PASSWORD=your_db_password \
  -e DB_NAME=growing \
  -v $(pwd)/backups:/backups \
  -p 5001:5000 \
  growing-backup:local
```

**生产环境** (从 Docker Hub):
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

### 3. 配置后端

在 `backend/.env` 中添加：
```bash
# 备份服务 URL (后端通过此 URL 与备份服务通信)
BACKUP_WEBHOOK_URL=http://localhost:5001
```

生产环境使用实际主机名：
```bash
BACKUP_WEBHOOK_URL=http://backup-service:5000
```

### 4. 验证安装

```bash
# 健康检查
curl http://localhost:5001/health

# 查看状态
curl http://localhost:5001/backup/status

# 列出备份文件
curl http://localhost:5001/backup/list
```

## API 接口

### 健康检查
```bash
GET http://localhost:5001/health
```

响应:
```json
{
  "status": "healthy",
  "timestamp": "2026-01-18T10:00:00Z"
}
```

### 获取状态
```bash
GET http://localhost:5001/backup/status
```

响应:
```json
{
  "success": true,
  "status": {
    "healthy": true,
    "disk_usage": {
      "total": "500G",
      "used": "120G",
      "available": "380G",
      "use_percent": "24%"
    },
    "latest_backups": {
      "daily": {
        "filename": "growing_daily_20260118.sql.gz",
        "size": 12457600,
        "timestamp": "2026-01-18T02:00:00Z"
      },
      "weekly": null,
      "monthly": null
    },
    "retention_policy": {
      "daily": "7 days",
      "weekly": "4 weeks",
      "monthly": "12 months"
    }
  }
}
```

### 列出备份文件
```bash
# 列出所有备份
GET http://localhost:5001/backup/list

# 按类型筛选
GET http://localhost:5001/backup/list?type=daily
GET http://localhost:5001/backup/list?type=weekly
GET http://localhost:5001/backup/list?type=monthly
```

响应:
```json
{
  "success": true,
  "total": 5,
  "backups": [
    {
      "filename": "growing_daily_20260118_020000.sql.gz",
      "type": "daily",
      "size": 12457600,
      "timestamp": "2026-01-18T02:00:00Z"
    }
  ]
}
```

### 触发手动备份
```bash
POST http://localhost:5001/backup/trigger
Content-Type: application/json

{
  "type": "manual"
}
```

响应:
```json
{
  "success": true,
  "message": "Backup completed successfully",
  "type": "manual",
  "backup": {
    "filename": "growing_manual_20260118_143022.sql.gz",
    "size": 12457600,
    "timestamp": "2026-01-18T14:30:22Z"
  },
  "timestamp": "2026-01-18T14:30:22Z"
}
```

### 恢复备份
```bash
POST http://localhost:5001/backup/restore
Content-Type: application/json

{
  "filename": "growing_daily_20260118.sql.gz",
  "confirmDbName": "growing"
}
```

成功响应:
```json
{
  "success": true,
  "message": "Database restored successfully",
  "filename": "growing_daily_20260118.sql.gz",
  "timestamp": "2026-01-18T14:35:00Z"
}
```

错误响应 (数据库名称不匹配):
```json
{
  "success": false,
  "error": "Database name confirmation failed"
}
```

### 获取日志
```bash
# 获取备份日志 (最后100行)
GET http://localhost:5001/backup/logs?type=backup&lines=100

# 获取恢复日志
GET http://localhost:5001/backup/logs?type=restore&lines=100
```

响应:
```json
{
  "success": true,
  "logs": [
    "[2026-01-18 02:00:00] 开始 daily 备份: growing_daily_20260118_020000.sql",
    "[2026-01-18 02:00:05] 备份完成: /backups/daily/growing_daily_20260118_020000.sql.gz (大小: 12457600 bytes, 表数量: 28)"
  ],
  "total": 2
}
```

## 环境变量

| 变量 | 必需 | 默认值 | 说明 |
|------|------|--------|------|
| `DB_HOST` | 是 | - | MySQL 主机地址 |
| `DB_PORT` | 是 | - | MySQL 端口 (通常 3306) |
| `DB_NAME` | 是 | - | 数据库名称 |
| `DB_USER` | 是 | - | MySQL 用户名 |
| `DB_PASSWORD` | 是 | - | MySQL 密码 |
| `BACKUP_RETENTION_DAYS` | 否 | 7 | 每日备份保留天数 |
| `BACKUP_RETENTION_WEEKS` | 否 | 4 | 周备份保留周数 |
| `BACKUP_RETENTION_MONTHS` | 否 | 12 | 月备份保留月数 |
| `TZ` | 否 | UTC | 时区 |

## 文件结构

### 备份目录

```
/backups/
├── daily/
│   ├── growing_daily_20260118_020000.sql.gz
│   ├── growing_daily_20260117_020000.sql.gz
│   └── ... (最近7天)
├── weekly/
│   ├── growing_weekly_20260113_030000.sql.gz
│   └── ... (最近4周)
├── monthly/
│   ├── growing_monthly_20260101_040000.sql.gz
│   └── ... (最近12个月)
├── pre_restore_snapshot_YYYYMMDD_HHMMSS.sql.gz  # 恢复前自动创建
├── backup.log      # 备份日志
├── restore.log     # 恢复日志
└── metadata.json   # 备份元数据
```

## 备份命名规则

格式: `growing_{type}_{timestamp}.sql.gz`

- **类型**: `daily`, `weekly`, `monthly`, `manual`
- **时间戳**: `YYYYMMDD_HHMMSS` (UTC)

示例:
- `growing_daily_20260118_020000.sql.gz` - 2026年1月18日凌晨2点的每日备份
- `growing_weekly_20260113_030000.sql.gz` - 2026年1月13日凌晨3点的周备份
- `growing_manual_20260118_143022.sql.gz` - 手动触发的备份

## Cron 定时任务

备份容器运行以下 cron 任务 (UTC 时区):

```cron
# 每日备份: 凌晨2点
0 2 * * * /scripts/backup.sh daily

# 周备份: 每周六凌晨3点
0 3 * * 6 /scripts/backup.sh weekly

# 月备份: 每月1号凌晨4点
0 4 1 * * /scripts/backup.sh monthly
```

要自定义时间，修改 `backup/crontab` 并重新构建镜像。

## 恢复流程

恢复备份时的流程：

1. **恢复前快照**: 自动创建当前数据库快照
   - 保存为 `pre_restore_snapshot_YYYYMMDD_HHMMSS.sql.gz`
   - 如果恢复失败，可以用快照回滚

2. **数据库名称验证**: 用户必须输入正确的数据库名称确认

3. **恢复执行**:
   - 从备份文件恢复数据
   - 所有操作记录到 `restore.log`

4. **恢复后**: 应用自动重启以重新加载数据库连接

## 故障排查

### 备份容器无法启动

**查看日志**:
```bash
docker logs growing-backup
```

**常见问题**:
- 缺少环境变量 (DB_HOST, DB_USER, etc.)
- MySQL 凭据无效
- 网络连接问题

### 定时备份未执行

**查看 cron 日志**:
```bash
docker exec growing-backup cat /var/log/cron.log
```

**验证时区**:
```bash
docker exec growing-backup date
```

### 恢复失败 "Database name confirmation failed"

**原因**: 输入的数据库名称与 `DB_NAME` 环境变量不匹配

**解决方法**:
1. 检查容器的 `DB_NAME`: `docker exec growing-backup env | grep DB_NAME`
2. 在确认对话框中输入准确的数据库名称

### 磁盘空间不足

**查看磁盘使用**:
```bash
curl http://localhost:5001/backup/status | jq '.status.disk_usage'
```

**解决方法**:
1. 删除旧的手动备份
2. 调整保留策略 (修改环境变量后重启容器)
3. 增加备份卷大小

## 性能考虑

### 备份耗时

典型 Growing 数据库：
- **小型** (< 100 MB): ~5-10 秒
- **中型** (100-500 MB): ~15-30 秒
- **大型** (> 500 MB): ~1-2 分钟

压缩可减少约 70-80% 文件大小。

### 数据库锁

`mysqldump` 使用 `--single-transaction` 标志，避免备份时锁表。适用于 InnoDB 表（Growing app 使用的表类型）。

## 安全最佳实践

1. **限制备份目录权限**:
   ```bash
   chmod 700 /path/to/backups
   ```

2. **使用强密码**:
   - 专用备份用户，最小权限
   - 强密码（不提交到版本控制）

3. **加密备份** (可选):
   ```bash
   # 修改 backup.sh 添加加密
   mysqldump ... | gzip | openssl enc -aes-256-cbc -salt -out backup.sql.gz.enc
   ```

4. **异地备份**:
   - 复制备份到远程存储 (S3, NAS 等)
   - 使用卷挂载或备份同步工具

## Docker Health Check

备份容器包含健康检查：

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:5001/health || exit 1
```

检查健康状态:
```bash
docker inspect growing-backup --format='{{.State.Health.Status}}'
```

## 常见问题

**Q: 可以备份多个数据库吗？**
A: 当前每个容器支持一个数据库。要备份多个数据库，运行多个容器。

**Q: 如何修改备份时间？**
A: 修改 `backup/crontab` 并重新构建 Docker 镜像。

**Q: 可以直接备份到云存储吗？**
A: 未内置。使用卷挂载和同步工具 (rclone, aws s3 sync 等)。

**Q: 如果恢复中途失败怎么办？**
A: 自动创建了恢复前快照。可以手动恢复快照。

**Q: 可以恢复到不同的数据库吗？**
A: 可以，但需要先更新 `DB_NAME` 环境变量并重启容器。

## License

与 Growing app 相同 - MIT License
