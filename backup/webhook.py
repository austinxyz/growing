#!/usr/bin/env python3
"""
Backup Webhook Service
提供HTTP API接口用于触发备份、恢复、查看日志等操作
"""

from flask import Flask, jsonify, request
import subprocess
import os
import json
import glob
from datetime import datetime

app = Flask(__name__)

BACKUP_DIR = os.environ.get('BACKUP_DIR', '/backups')
SCRIPTS_DIR = os.environ.get('SCRIPTS_DIR', '/scripts')
LOG_FILE = f"{BACKUP_DIR}/backup.log"
RESTORE_LOG_FILE = f"{BACKUP_DIR}/restore.log"
METADATA_FILE = f"{BACKUP_DIR}/metadata.json"


def run_command(command, timeout=300):
    """执行shell命令并返回结果"""
    try:
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            timeout=timeout
        )
        return {
            "success": result.returncode == 0,
            "stdout": result.stdout,
            "stderr": result.stderr,
            "returncode": result.returncode
        }
    except subprocess.TimeoutExpired:
        return {
            "success": False,
            "error": f"Command timeout after {timeout} seconds"
        }
    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }


@app.route('/health', methods=['GET'])
def health_check():
    """健康检查"""
    return jsonify({
        "status": "healthy",
        "timestamp": datetime.utcnow().isoformat()
    })


@app.route('/backup/status', methods=['GET'])
def get_status():
    """获取备份系统状态"""
    try:
        # 获取磁盘使用情况
        df_result = run_command(f"df -h {BACKUP_DIR}")
        disk_usage = {}
        if df_result['success'] and df_result['stdout']:
            lines = df_result['stdout'].strip().split('\n')
            if len(lines) > 1:
                parts = lines[1].split()
                if len(parts) >= 5:
                    disk_usage = {
                        "total": parts[1],
                        "used": parts[2],
                        "available": parts[3],
                        "use_percent": parts[4]
                    }

        # 获取最新备份信息
        latest_backups = {
            "daily": get_latest_backup('daily'),
            "weekly": get_latest_backup('weekly'),
            "monthly": get_latest_backup('monthly')
        }

        return jsonify({
            "success": True,
            "status": {
                "healthy": True,
                "disk_usage": disk_usage,
                "latest_backups": latest_backups,
                "retention_policy": {
                    "daily": "7 days",
                    "weekly": "4 weeks",
                    "monthly": "12 months"
                }
            }
        })
    except Exception as e:
        return jsonify({
            "success": False,
            "error": str(e)
        }), 500


def get_latest_backup(backup_type):
    """获取指定类型的最新备份"""
    backups = get_backup_files(backup_type)
    if backups:
        latest = max(backups, key=lambda x: x['mtime'])
        return {
            "filename": latest['filename'],
            "size": latest['size'],
            "timestamp": latest['timestamp']
        }
    return None


@app.route('/backup/trigger', methods=['POST'])
def trigger_backup():
    """手动触发备份"""
    backup_type = request.json.get('type', 'manual') if request.json else 'manual'

    result = run_command(f'{SCRIPTS_DIR}/backup.sh {backup_type}', timeout=600)

    if result['success']:
        # 获取最新生成的备份文件信息
        latest_backup = get_latest_backup('daily')  # manual备份保存在daily目录

        return jsonify({
            "success": True,
            "message": "Backup completed successfully",
            "type": backup_type,
            "backup": latest_backup,
            "timestamp": datetime.utcnow().isoformat()
        })
    else:
        return jsonify({
            "success": False,
            "message": "Backup failed",
            "error": result.get('stderr', result.get('error', 'Unknown error'))
        }), 500


@app.route('/backup/list', methods=['GET'])
def list_backups():
    """列出所有备份文件"""
    backup_type = request.args.get('type', 'all')  # all, daily, weekly, monthly

    backups = []

    if backup_type in ['all', 'daily']:
        backups.extend(get_backup_files('daily'))
    if backup_type in ['all', 'weekly']:
        backups.extend(get_backup_files('weekly'))
    if backup_type in ['all', 'monthly']:
        backups.extend(get_backup_files('monthly'))

    # 按时间倒序排序
    backups.sort(key=lambda x: x['timestamp'], reverse=True)

    return jsonify({
        "success": True,
        "backups": backups,
        "total": len(backups)
    })


def get_backup_files(backup_type):
    """获取指定类型的备份文件"""
    backup_dir = f"{BACKUP_DIR}/{backup_type}"
    if not os.path.exists(backup_dir):
        return []

    files = glob.glob(f"{backup_dir}/*.sql.gz")

    result = []
    for filepath in files:
        try:
            stat = os.stat(filepath)
            filename = os.path.basename(filepath)

            result.append({
                "filename": filename,
                "filepath": filepath,
                "type": backup_type,
                "size": stat.st_size,
                "timestamp": datetime.fromtimestamp(stat.st_mtime).isoformat(),
                "mtime": stat.st_mtime
            })
        except Exception:
            # Silently skip unreadable files
            pass

    return result


@app.route('/backup/restore', methods=['POST'])
def restore_backup():
    """恢复备份"""
    data = request.json
    if not data:
        return jsonify({"success": False, "error": "Missing request body"}), 400

    filename = data.get('filename')
    confirm_db_name = data.get('confirmDbName')

    # 验证必需参数
    if not filename:
        return jsonify({"success": False, "error": "Missing filename"}), 400

    if not confirm_db_name:
        return jsonify({"success": False, "error": "Missing confirmation"}), 400

    # 验证数据库名称
    expected_db_name = os.environ.get('DB_NAME', '')
    if confirm_db_name != expected_db_name:
        return jsonify({
            "success": False,
            "error": "Database name confirmation failed"
        }), 400

    # 查找备份文件
    backup_file = find_backup_file(filename)
    if not backup_file:
        return jsonify({
            "success": False,
            "error": f"Backup file not found: {filename}"
        }), 404

    # 执行恢复
    result = run_command(f'{SCRIPTS_DIR}/restore.sh "{backup_file}"', timeout=1800)

    if result['success']:
        return jsonify({
            "success": True,
            "message": "Database restored successfully",
            "filename": filename,
            "timestamp": datetime.utcnow().isoformat()
        })
    else:
        return jsonify({
            "success": False,
            "message": "Restore failed",
            "error": result.get('stderr', result.get('error', 'Unknown error'))
        }), 500


def find_backup_file(filename):
    """查找备份文件（在所有目录中搜索）"""
    for backup_type in ['daily', 'weekly', 'monthly']:
        filepath = f"{BACKUP_DIR}/{backup_type}/{filename}"
        if os.path.exists(filepath):
            return filepath
    return None


@app.route('/backup/logs', methods=['GET'])
def get_logs():
    """获取备份日志"""
    lines = request.args.get('lines', 100, type=int)
    log_type = request.args.get('type', 'backup')  # backup or restore

    log_file = RESTORE_LOG_FILE if log_type == 'restore' else LOG_FILE

    if not os.path.exists(log_file):
        return jsonify({
            "success": True,
            "logs": [],
            "total": 0
        })

    try:
        # 读取最后N行
        result = run_command(f"tail -n {lines} {log_file}")

        if result['success']:
            log_lines = result['stdout'].strip().split('\n') if result['stdout'] else []
            return jsonify({
                "success": True,
                "logs": log_lines,
                "total": len(log_lines)
            })
        else:
            return jsonify({
                "success": False,
                "error": "Failed to read log file"
            }), 500
    except Exception as e:
        return jsonify({
            "success": False,
            "error": str(e)
        }), 500


if __name__ == '__main__':
    # 创建备份目录
    os.makedirs(BACKUP_DIR, exist_ok=True)
    os.makedirs(f"{BACKUP_DIR}/daily", exist_ok=True)
    os.makedirs(f"{BACKUP_DIR}/weekly", exist_ok=True)
    os.makedirs(f"{BACKUP_DIR}/monthly", exist_ok=True)

    # 启动Flask服务
    port = int(os.environ.get('PORT', 5001))
    app.run(host='0.0.0.0', port=port, debug=False)
