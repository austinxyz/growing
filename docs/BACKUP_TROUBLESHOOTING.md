# Backup Service Troubleshooting Guide

## 问题: 数据库连接失败

### 症状
```
WARNING: Database connection failed
Please check your credentials and network connectivity
```

### 可能原因

#### 1. Docker网络无法访问外部数据库

**问题**: Docker容器使用bridge网络，无法访问主机或外部网络的MySQL

**解决方案A - 使用host网络模式**:
```yaml
# docker-compose.yml
backup:
  network_mode: "host"
  environment:
    - DB_HOST=localhost  # 使用localhost因为是host网络
    - DB_PORT=37719
```

**解决方案B - 检查网络连通性**:
```bash
# 在容器内测试连接
docker exec growing-backup ping -c 3 10.0.0.7
docker exec growing-backup nc -zv 10.0.0.7 37719
docker exec growing-backup mysql -h 10.0.0.7 -P 37719 -u austinxu -phelloworld -e "SELECT 1"
```

**解决方案C - 配置bridge网络路由**:
```bash
# 检查Docker网络配置
docker network inspect growing_growing-network

# 如果需要，添加路由规则允许容器访问外部网络
```

#### 2. 数据库凭据错误

**检查步骤**:
1. 验证DB_USER和DB_PASSWORD是否正确
2. 检查MySQL用户是否有远程连接权限：
   ```sql
   SELECT user, host FROM mysql.user WHERE user = 'austinxu';
   -- 应该看到 host = '%' 或 host = '172.%'（Docker网段）
   ```

#### 3. MySQL未配置远程访问

**检查MySQL配置**:
```bash
# 在MySQL服务器上检查bind-address
cat /etc/mysql/mysql.conf.d/mysqld.cnf | grep bind-address
# 应该是: bind-address = 0.0.0.0
```

**授予远程访问权限**:
```sql
CREATE USER 'austinxu'@'%' IDENTIFIED BY 'helloworld';
GRANT ALL PRIVILEGES ON growing.* TO 'austinxu'@'%';
FLUSH PRIVILEGES;
```

### 诊断命令

**1. 测试从Docker容器到数据库的连接**:
```bash
docker exec growing-backup sh -c 'mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD -e "SELECT VERSION()"'
```

**2. 检查mysqldump命令**:
```bash
docker exec growing-backup sh -c 'mysqldump -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD --version'
```

**3. 手动触发备份并查看日志**:
```bash
# 触发备份
curl -X POST http://localhost:5001/backup/trigger \
  -H "Content-Type: application/json" \
  -d '{"type":"manual"}'

# 查看详细日志
docker logs growing-backup

# 查看备份日志文件
docker exec growing-backup cat /backups/backup.log
```

### 快速修复

**临时解决方案 - 使用host网络**:

编辑 `import/docker-compose.remot.yml`:
```yaml
backup:
  network_mode: "host"  # 添加这行
  environment:
    - DB_HOST=127.0.0.1  # 改为localhost
    - DB_PORT=37719
    # ... 其他配置不变
  # 移除 networks 配置
  # 移除 ports 配置（host网络直接使用容器端口）
```

然后重启：
```bash
docker-compose down
docker-compose up -d
```

## 问题: 手动备份返回500错误

### 可能原因
1. 数据库连接失败（见上）
2. 权限问题（/backups目录）
3. mysqldump命令失败

### 检查步骤

**1. 检查/backups目录权限**:
```bash
docker exec growing-backup ls -la /backups
docker exec growing-backup touch /backups/test.txt
```

**2. 查看详细错误**:
```bash
# 查看webhook日志
docker logs growing-backup --tail 50

# 查看备份脚本日志
docker exec growing-backup cat /backups/backup.log
```

**3. 手动执行备份脚本**:
```bash
docker exec growing-backup sh -c 'export BACKUP_DIR=/backups && /scripts/backup.sh manual'
```

### 解决方案

**修复权限问题**:
```bash
# 在主机上修复卷权限
sudo chown -R 1000:1000 /volume1/docker/growing/backups
sudo chmod -R 755 /volume1/docker/growing/backups
```

**查看完整错误信息**:
```bash
# 获取最新的错误详情
curl http://localhost:5001/backup/logs?type=backup&lines=50
```

## 测试清单

执行以下命令验证备份服务：

```bash
# 1. 健康检查
curl http://localhost:5001/health

# 2. 获取状态
curl http://localhost:5001/backup/status

# 3. 列出备份
curl http://localhost:5001/backup/list

# 4. 手动触发备份
curl -X POST http://localhost:5001/backup/trigger \
  -H "Content-Type: application/json" \
  -d '{"type":"manual"}'

# 5. 查看日志
curl http://localhost:5001/backup/logs?type=backup&lines=20
```

## 相关文档

- [Backup System Documentation](./BACKUP_SYSTEM.md)
- [Docker Deployment Guide](./DOCKER_DEPLOYMENT.md)
