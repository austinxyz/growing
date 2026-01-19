#!/bin/bash

set -e

echo "Starting Growing Backup Service..."

# 启动cron服务
echo "Starting cron service..."
service cron start

# 检查环境变量
if [ -z "$DB_HOST" ] || [ -z "$DB_USER" ] || [ -z "$DB_PASSWORD" ] || [ -z "$DB_NAME" ]; then
    echo "ERROR: Missing required environment variables"
    echo "Required: DB_HOST, DB_USER, DB_PASSWORD, DB_NAME"
    exit 1
fi

echo "Database connection configured:"
echo "  Host: $DB_HOST"
echo "  Port: ${DB_PORT:-3306}"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"

# 测试数据库连接
echo "Testing database connection..."
if mysql -h "$DB_HOST" -P "${DB_PORT:-3306}" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1" > /dev/null 2>&1; then
    echo "Database connection successful!"
else
    echo "WARNING: Database connection failed"
    echo "Please check your credentials and network connectivity"
    echo "Service will start anyway - backups will fail until connection is restored"
fi

echo "Backup service configuration:"
echo "  Daily backups: Retained for ${BACKUP_RETENTION_DAYS:-7} days"
echo "  Weekly backups: Retained for ${BACKUP_RETENTION_WEEKS:-4} weeks"
echo "  Monthly backups: Retained for ${BACKUP_RETENTION_MONTHS:-12} months"

echo "Cron schedule (UTC):"
echo "  Daily: 02:00"
echo "  Weekly: 03:00 (Saturday)"
echo "  Monthly: 04:00 (1st of month)"

# 启动webhook服务
echo "Starting webhook service on port 5000..."
exec python3 /app/webhook.py
