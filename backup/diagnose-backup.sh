#!/bin/bash

# 备份系统诊断脚本
# 检查备份服务状态、配置和常见问题

set -e

echo "========================================="
echo "Growing Backup System - 诊断工具"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. 检查Docker容器
echo "1. 检查Docker容器状态..."
if docker ps | grep -q growing-backup; then
    echo -e "${GREEN}✅ 容器正在运行${NC}"
    docker ps | grep growing-backup
else
    echo -e "${RED}❌ 容器未运行${NC}"
    echo "启动容器:"
    echo "  docker start growing-backup"
    echo "或查看日志:"
    echo "  docker logs growing-backup"
fi
echo ""

# 2. 检查容器健康状态
echo "2. 检查容器健康状态..."
HEALTH_STATUS=$(docker inspect growing-backup --format='{{.State.Health.Status}}' 2>/dev/null || echo "unknown")
if [ "$HEALTH_STATUS" = "healthy" ]; then
    echo -e "${GREEN}✅ 容器健康状态: $HEALTH_STATUS${NC}"
elif [ "$HEALTH_STATUS" = "unknown" ]; then
    echo -e "${YELLOW}⚠️  容器不存在或未配置健康检查${NC}"
else
    echo -e "${RED}❌ 容器健康状态: $HEALTH_STATUS${NC}"
    echo "查看健康检查日志:"
    echo "  docker inspect growing-backup | jq '.[0].State.Health'"
fi
echo ""

# 3. 检查环境变量
echo "3. 检查环境变量..."
REQUIRED_VARS=("DB_HOST" "DB_PORT" "DB_NAME" "DB_USER" "DB_PASSWORD")
ALL_SET=true

for var in "${REQUIRED_VARS[@]}"; do
    VALUE=$(docker exec growing-backup env 2>/dev/null | grep "^$var=" | cut -d= -f2 || echo "")
    if [ -n "$VALUE" ]; then
        if [ "$var" = "DB_PASSWORD" ]; then
            echo -e "${GREEN}✅ $var: ********${NC}"
        else
            echo -e "${GREEN}✅ $var: $VALUE${NC}"
        fi
    else
        echo -e "${RED}❌ $var: 未设置${NC}"
        ALL_SET=false
    fi
done

if [ "$ALL_SET" = false ]; then
    echo -e "${YELLOW}⚠️  缺少必需的环境变量${NC}"
    echo "重新运行容器并设置环境变量"
fi
echo ""

# 4. 检查数据库连接
echo "4. 检查数据库连接..."
DB_HOST=$(docker exec growing-backup env | grep "^DB_HOST=" | cut -d= -f2)
DB_PORT=$(docker exec growing-backup env | grep "^DB_PORT=" | cut -d= -f2)
DB_USER=$(docker exec growing-backup env | grep "^DB_USER=" | cut -d= -f2)
DB_NAME=$(docker exec growing-backup env | grep "^DB_NAME=" | cut -d= -f2)
DB_PASSWORD=$(docker exec growing-backup env | grep "^DB_PASSWORD=" | cut -d= -f2)

if [ -n "$DB_HOST" ] && [ -n "$DB_USER" ] && [ -n "$DB_PASSWORD" ]; then
    if docker exec growing-backup mysql -h "$DB_HOST" -P "${DB_PORT:-3306}" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 数据库连接成功${NC}"
        echo "  数据库: $DB_NAME"
    else
        echo -e "${RED}❌ 数据库连接失败${NC}"
        echo "检查:"
        echo "  - MySQL服务是否运行"
        echo "  - 凭据是否正确"
        echo "  - 网络连接是否正常"
    fi
else
    echo -e "${YELLOW}⚠️  数据库配置不完整，跳过连接测试${NC}"
fi
echo ""

# 5. 检查备份服务API
echo "5. 检查备份服务API..."
if curl -s -f http://localhost:5001/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ API服务正常${NC}"
    curl -s http://localhost:5001/health | jq .
else
    echo -e "${RED}❌ API服务不可用${NC}"
    echo "检查:"
    echo "  - 容器是否运行"
    echo "  - 端口5000是否映射正确"
    echo "  docker logs growing-backup"
fi
echo ""

# 6. 检查备份文件
echo "6. 检查备份文件..."
BACKUP_COUNT=$(docker exec growing-backup find /backups -name "*.sql.gz" -type f 2>/dev/null | wc -l || echo 0)
if [ "$BACKUP_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✅ 找到 $BACKUP_COUNT 个备份文件${NC}"
    echo "最近的备份:"
    docker exec growing-backup find /backups -name "*.sql.gz" -type f -exec ls -lh {} \; 2>/dev/null | sort -k6,7 | tail -5 || true
else
    echo -e "${YELLOW}⚠️  未找到备份文件${NC}"
    echo "触发手动备份:"
    echo "  curl -X POST http://localhost:5001/backup/trigger -H 'Content-Type: application/json' -d '{\"type\":\"manual\"}'"
fi
echo ""

# 7. 检查Cron任务
echo "7. 检查Cron任务..."
if docker exec growing-backup service cron status 2>/dev/null | grep -q "running"; then
    echo -e "${GREEN}✅ Cron服务运行中${NC}"
    echo "定时任务:"
    docker exec growing-backup crontab -l 2>/dev/null || echo "  未找到crontab配置"
else
    echo -e "${RED}❌ Cron服务未运行${NC}"
    echo "启动cron:"
    echo "  docker exec growing-backup service cron start"
fi
echo ""

# 8. 检查磁盘空间
echo "8. 检查备份目录磁盘空间..."
DISK_INFO=$(docker exec growing-backup df -h /backups 2>/dev/null | tail -1)
if [ -n "$DISK_INFO" ]; then
    echo "$DISK_INFO"
    USAGE=$(echo "$DISK_INFO" | awk '{print $5}' | sed 's/%//')
    if [ "$USAGE" -lt 80 ]; then
        echo -e "${GREEN}✅ 磁盘空间充足${NC}"
    elif [ "$USAGE" -lt 90 ]; then
        echo -e "${YELLOW}⚠️  磁盘使用率 ${USAGE}% - 建议清理旧备份${NC}"
    else
        echo -e "${RED}❌ 磁盘使用率 ${USAGE}% - 空间不足！${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  无法获取磁盘信息${NC}"
fi
echo ""

# 9. 检查日志
echo "9. 查看最近的日志..."
echo "容器日志 (最后10行):"
docker logs --tail 10 growing-backup 2>&1 || echo "  无法获取日志"
echo ""

# 总结
echo "========================================="
echo "诊断完成"
echo "========================================="
echo ""
echo "常用命令:"
echo "  查看日志: docker logs -f growing-backup"
echo "  重启服务: docker restart growing-backup"
echo "  进入容器: docker exec -it growing-backup /bin/bash"
echo "  手动备份: curl -X POST http://localhost:5001/backup/trigger -H 'Content-Type: application/json' -d '{\"type\":\"manual\"}'"
echo "  查看备份: curl http://localhost:5001/backup/list"
echo ""
