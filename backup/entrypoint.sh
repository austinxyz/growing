#!/bin/bash

set -e

echo "Starting Growing Backup Service..."

# 导出环境变量到文件供cron使用
echo "Exporting environment variables for cron..."
cat > /etc/environment << EOF
DB_HOST=$DB_HOST
DB_PORT=$DB_PORT
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD
DB_NAME=$DB_NAME
BACKUP_DIR=$BACKUP_DIR
SCRIPTS_DIR=$SCRIPTS_DIR
BACKUP_RETENTION_DAYS=${BACKUP_RETENTION_DAYS:-7}
BACKUP_RETENTION_WEEKS=${BACKUP_RETENTION_WEEKS:-4}
BACKUP_RETENTION_MONTHS=${BACKUP_RETENTION_MONTHS:-12}
EOF

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
echo "  Port: ${DB_PORT:-37719}"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"

# 测试数据库连接
echo "Testing database connection..."
if mysql -h "$DB_HOST" -P "${DB_PORT:-37719}" -u "$DB_USER" -p"$DB_PASSWORD" --skip-ssl -e "SELECT 1" > /dev/null 2>&1; then
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
PORT=${PORT:-5000}
echo "Starting webhook service on port $PORT..."
exec python3 /app/webhook.py
