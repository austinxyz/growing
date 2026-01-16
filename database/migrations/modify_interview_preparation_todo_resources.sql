-- =====================================================
-- Phase 7: 面试准备TODO功能 - 资源关联表修改
-- =====================================================
-- 说明: 将外部URL链接改为系统内部资源关联
-- 支持的资源类型:
--   - Question (试题)
--   - LearningResource (学习材料)
--   - SystemDesignCase (系统设计案例)
--   - Project (项目经验)
--   - ManagementExperience (人员管理经验)
--   - ExternalLink (外部链接，兼容保留)
-- =====================================================

-- Step 1: 创建资源关联表
CREATE TABLE IF NOT EXISTS interview_preparation_todo_resources (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    todo_id BIGINT NOT NULL COMMENT 'TODO ID',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型: Question/LearningResource/SystemDesignCase/Project/ManagementExperience/ExternalLink',
    resource_id BIGINT NULL COMMENT '资源ID（外部链接时为NULL）',

    -- 外部链接字段（仅当resource_type=ExternalLink时使用）
    title VARCHAR(500) NULL COMMENT '外部链接标题',
    url VARCHAR(1000) NULL COMMENT '外部链接URL',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (todo_id) REFERENCES interview_preparation_todos(id) ON DELETE CASCADE,
    INDEX idx_todo_id (todo_id),
    INDEX idx_resource (resource_type, resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试准备TODO资源关联表';

-- Step 2: 修改 interview_preparation_todos 表
-- 移除 resource_links JSON字段（如果存在数据，需要先迁移）
-- 注意: 如果列不存在会报错，可以忽略或手动检查
-- ALTER TABLE interview_preparation_todos DROP COLUMN resource_links;

-- =====================================================
-- 执行说明:
-- 1. 运行前确保已备份数据
-- 2. 如果 resource_links 中有数据，需要先迁移到新表
-- 3. 迁移完成后再执行 DROP COLUMN
-- =====================================================
