-- =====================================================
-- Phase 2: 更新 learning_resources 资源类型枚举
-- 版本: v4.0
-- 创建时间: 2025-12-20
-- =====================================================

-- 更新 learning_resources 表的 resource_type 枚举类型
-- 添加新的资源类型: VIDEO, DOCUMENT, BLOG, PRACTICE, TOOL
-- 从原来的 5 种类型扩展到 10 种类型

ALTER TABLE learning_resources
MODIFY COLUMN resource_type ENUM(
    'DOCUMENT',     -- 文档
    'VIDEO',        -- 视频
    'COURSE',       -- 课程
    'BOOK',         -- 书籍
    'BLOG',         -- 博客
    'PRACTICE',     -- 练习
    'TOOL',         -- 工具
    'ARTICLE',      -- 文章
    'WEBSITE',      -- 网站
    'OTHER'         -- 其他
) NOT NULL COMMENT '资源类型';

-- =====================================================
-- 说明
-- =====================================================
-- 原枚举值: BOOK, COURSE, WEBSITE, ARTICLE, OTHER
-- 新增枚举值: DOCUMENT, VIDEO, BLOG, PRACTICE, TOOL
-- 总共 10 种资源类型,与后端 Java enum 保持一致
