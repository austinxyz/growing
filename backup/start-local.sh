#!/bin/bash

# Growing Backup Service - Local Startup Script
# 本地开发环境启动脚本

set -e

echo "🚀 Starting Growing Backup Service (Local)"
echo ""

# 添加mysql-client到PATH
export PATH="/opt/homebrew/opt/mysql-client/bin:$PATH"

# 设置环境变量 — fail fast if any required value is unset (no hardcoded defaults)
: "${DB_HOST:?DB_HOST not set; copy .env.example to .env and fill in values}"
: "${DB_USER:?DB_USER not set}"
: "${DB_PASSWORD:?DB_PASSWORD not set}"
export DB_HOST DB_USER DB_PASSWORD
export DB_PORT=${DB_PORT:-3306}
export DB_NAME=${DB_NAME:-growing}
export BACKUP_RETENTION_DAYS=${BACKUP_RETENTION_DAYS:-7}
export BACKUP_RETENTION_WEEKS=${BACKUP_RETENTION_WEEKS:-4}
export BACKUP_RETENTION_MONTHS=${BACKUP_RETENTION_MONTHS:-12}

# 创建本地备份目录（使用当前目录下的backups）
export BACKUP_DIR="$(pwd)/backups"
export SCRIPTS_DIR="$(pwd)/scripts"
mkdir -p "$BACKUP_DIR/daily"
mkdir -p "$BACKUP_DIR/weekly"
mkdir -p "$BACKUP_DIR/monthly"

echo "Database Configuration:"
echo "  Host: $DB_HOST:$DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""
echo "Backup Directory: $BACKUP_DIR"
echo ""
echo "Service will run on http://localhost:5001"
echo ""

# 修改webhook.py中的BACKUP_DIR路径为本地路径
sed -i.bak "s|BACKUP_DIR = \"/backups\"|BACKUP_DIR = \"$BACKUP_DIR\"|g" webhook.py

# 启动Flask服务
python3 webhook.py
