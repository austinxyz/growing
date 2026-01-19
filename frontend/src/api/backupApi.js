// ⚠️ baseURL is '/api', do NOT add '/api' prefix!
// Note: interceptor returns response.data already

import apiClient from './index'

export default {
  /**
   * 手动触发备份
   */
  triggerBackup() {
    return apiClient.post('/backup/trigger', {}, {
      timeout: 600000  // 10 minutes timeout for backup operations
    })
  },

  /**
   * 列出所有备份文件
   * @param {string} type - 备份类型 (all, daily, weekly, monthly)
   */
  listBackups(type = null) {
    const params = type ? { type } : {}
    return apiClient.get('/backup/list', { params })
  },

  /**
   * 恢复备份
   * @param {string} filename - 备份文件名
   * @param {string} confirmDbName - 确认的数据库名称
   */
  restoreBackup(filename, confirmDbName) {
    return apiClient.post('/backup/restore', {
      filename,
      confirmDbName
    }, {
      timeout: 600000  // 10 minutes timeout for restore operations
    })
  },

  /**
   * 获取备份日志
   * @param {string} type - 日志类型 (backup, restore)
   * @param {number} lines - 行数
   */
  getLogs(type = 'backup', lines = 100) {
    return apiClient.get('/backup/logs', {
      params: { type, lines }
    })
  },

  /**
   * 获取备份服务状态
   */
  getStatus() {
    return apiClient.get('/backup/status')
  },

  /**
   * 健康检查
   */
  healthCheck() {
    return apiClient.get('/backup/health')
  }
}
