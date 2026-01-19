#!/bin/bash

# 测试备份服务的脚本

set -e

BACKUP_URL="http://localhost:5001"

echo "========================================="
echo "Growing Backup Service - 测试脚本"
echo "========================================="
echo ""

# 1. 健康检查
echo "1. 健康检查..."
if curl -s -f "$BACKUP_URL/health" > /dev/null; then
    echo "✅ 服务运行正常"
    curl -s "$BACKUP_URL/health" | jq .
else
    echo "❌ 服务不可用"
    exit 1
fi
echo ""

# 2. 获取状态
echo "2. 获取备份状态..."
curl -s "$BACKUP_URL/backup/status" | jq .
echo ""

# 3. 列出现有备份
echo "3. 列出现有备份..."
curl -s "$BACKUP_URL/backup/list" | jq '.backups[] | {filename, type, size, timestamp}'
echo ""

# 4. 触发手动备份
echo "4. 触发手动备份..."
read -p "是否执行手动备份？(y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "开始备份..."
    RESULT=$(curl -s -X POST "$BACKUP_URL/backup/trigger" \
        -H "Content-Type: application/json" \
        -d '{"type":"manual"}')

    echo "$RESULT" | jq .

    if echo "$RESULT" | jq -e '.success' > /dev/null; then
        echo "✅ 备份成功"
    else
        echo "❌ 备份失败"
    fi
else
    echo "跳过手动备份"
fi
echo ""

# 5. 查看日志
echo "5. 查看备份日志 (最后10行)..."
curl -s "$BACKUP_URL/backup/logs?type=backup&lines=10" | jq -r '.logs[]'
echo ""

echo "========================================="
echo "测试完成"
echo "========================================="
