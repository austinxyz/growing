# Growing Database Backup Service

自动化数据库备份服务，支持定时备份和手动触发。

## 快速开始

### 本地测试

```bash
# 1. 构建镜像
docker build -t growing-backup:local .

# 2. 运行容器
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

# 3. 验证服务
curl http://localhost:5001/health

# 4. 手动触发备份
curl -X POST http://localhost:5001/backup/trigger \
  -H "Content-Type: application/json" \
  -d '{"type":"manual"}'

# 5. 列出备份文件
curl http://localhost:5001/backup/list

# 6. 查看日志
docker logs growing-backup
```

### 生产部署

```bash
# 1. 构建并推送多架构镜像
./build-multiarch.sh

# 2. 拉取并运行
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

## 文件说明

```
backup/
├── Dockerfile              # Docker镜像定义
├── entrypoint.sh          # 容器启动脚本
├── webhook.py             # Flask API服务
├── crontab                # 定时任务配置
├── build-multiarch.sh     # 多架构构建脚本
├── scripts/
│   ├── backup.sh          # 备份执行脚本
│   └── restore.sh         # 恢复执行脚本
└── README.md              # 本文件
```

## 定时任务

- **每日备份**: 每天 02:00 UTC
- **周备份**: 每周六 03:00 UTC
- **月备份**: 每月1号 04:00 UTC

## API 端点

- `GET /health` - 健康检查
- `GET /backup/status` - 获取备份状态
- `GET /backup/list` - 列出备份文件
- `POST /backup/trigger` - 触发手动备份
- `POST /backup/restore` - 恢复备份
- `GET /backup/logs` - 获取日志

## 保留策略

- **每日备份**: 7天
- **周备份**: 4周
- **月备份**: 12个月

## 详细文档

查看 [docs/BACKUP_SYSTEM.md](../docs/BACKUP_SYSTEM.md) 获取完整文档。
