#!/bin/bash

# MySQL恢复脚本
# 从备份文件恢复数据库

set -e

BACKUP_DIR="/backups"
LOG_FILE="$BACKUP_DIR/restore.log"

# 日志函数
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# 主逻辑
main() {
    local backup_file=$1

    if [ -z "$backup_file" ]; then
        log "ERROR: 未指定备份文件"
        echo "用法: $0 <backup_file>"
        exit 1
    fi

    if [ ! -f "$backup_file" ]; then
        log "ERROR: 备份文件不存在: $backup_file"
        exit 1
    fi

    log "========== 开始恢复流程 =========="
    log "备份文件: $backup_file"

    # 检查必需的环境变量
    if [ -z "$DB_HOST" ] || [ -z "$DB_USER" ] || [ -z "$DB_PASSWORD" ] || [ -z "$DB_NAME" ]; then
        log "ERROR: 缺少必需的环境变量 (DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)"
        exit 1
    fi

    # 创建恢复前快照
    local snapshot_timestamp=$(date '+%Y%m%d_%H%M%S')
    local snapshot_file="$BACKUP_DIR/pre_restore_snapshot_${snapshot_timestamp}.sql.gz"
    log "创建恢复前快照: $snapshot_file"

    if mysqldump \
        -h "$DB_HOST" \
        -P "$DB_PORT" \
        -u "$DB_USER" \
        -p"$DB_PASSWORD" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        "$DB_NAME" | gzip > "$snapshot_file" 2>> "$LOG_FILE"; then
        log "快照创建成功"
    else
        log "WARNING: 快照创建失败，继续恢复流程"
    fi

    # 恢复数据库
    log "开始恢复数据库..."
    if zcat "$backup_file" | mysql \
        -h "$DB_HOST" \
        -P "$DB_PORT" \
        -u "$DB_USER" \
        -p"$DB_PASSWORD" \
        "$DB_NAME" 2>> "$LOG_FILE"; then
        log "数据库恢复成功"
        log "========== 恢复流程完成 =========="
        exit 0
    else
        log "ERROR: 数据库恢复失败"
        log "可以从快照恢复: $snapshot_file"
        exit 1
    fi
}

# 执行主函数
main "$@"
