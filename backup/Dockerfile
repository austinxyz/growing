FROM python:3.11-slim

# 安装系统依赖
RUN apt-get update && apt-get install -y \
    default-mysql-client \
    cron \
    curl \
    jq \
    && rm -rf /var/lib/apt/lists/*

# 安装Python依赖
RUN pip install --no-cache-dir flask

# 创建工作目录
WORKDIR /app

# 复制脚本文件
COPY webhook.py /app/webhook.py
COPY scripts/backup.sh /scripts/backup.sh
COPY scripts/restore.sh /scripts/restore.sh
COPY crontab /etc/cron.d/backup-cron

# 设置脚本权限
RUN chmod +x /scripts/backup.sh /scripts/restore.sh /app/webhook.py

# 设置cron权限和配置
RUN chmod 0644 /etc/cron.d/backup-cron && \
    crontab /etc/cron.d/backup-cron && \
    touch /var/log/cron.log

# 创建备份目录
RUN mkdir -p /backups/daily /backups/weekly /backups/monthly

# 暴露webhook端口
EXPOSE 5000

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:5000/health || exit 1

# 启动脚本
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
