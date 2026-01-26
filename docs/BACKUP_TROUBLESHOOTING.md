# Backup Service Troubleshooting Guide

## Cron Backup Jobs Failing

**Symptom**:
- Manual backups work fine
- Daily/weekly/monthly cron backups fail with "ERROR: 缺少必需的环境变量"

**Root Cause**: Cron runs with minimal environment, doesn't inherit Docker env vars

**Solution**: Environment variables are now exported to `/etc/environment` at container startup, then sourced in crontab

**Files Modified**:
- `backup/entrypoint.sh` - Exports DB credentials to `/etc/environment`
- `backup/crontab` - Sources environment before running: `. /etc/environment; /scripts/backup.sh`

**Verification**:
```bash
# Check environment file in container
docker exec growing-backup-1 cat /etc/environment

# Monitor next scheduled backup
docker exec growing-backup-1 tail -f /var/log/cron.log
```

## Other Common Issues

See full troubleshooting guide with solutions for:
- Database SSL certificate errors
- Missing nginx proxy configuration  
- Hardcoded file paths
- Port mismatches
- Cron scheduling

Run `cat docs/BACKUP_SYSTEM.md` for detailed backup system documentation.
