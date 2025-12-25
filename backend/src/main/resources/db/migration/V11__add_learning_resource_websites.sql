-- V11: 添加学习资源网站表和关联字段
-- 创建日期: 2025-12-24
-- 目的: 支持多个学习资源网站，标识是否支持iframe嵌入

-- ============================================================
-- 1. 创建学习资源网站表
-- ============================================================
CREATE TABLE learning_resource_websites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '网站名称（如 labuladong, hellointerview）',
    display_name VARCHAR(200) NOT NULL COMMENT '显示名称',
    base_url VARCHAR(500) NOT NULL COMMENT '网站起始地址',
    author VARCHAR(100) COMMENT '作者/团队',
    description TEXT COMMENT '网站介绍/目的',
    supports_iframe BOOLEAN DEFAULT false COMMENT '是否支持iframe嵌入',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '学习资源网站配置表';

-- ============================================================
-- 2. 在 learning_contents 表添加 website_id 字段
-- ============================================================
ALTER TABLE learning_contents
ADD COLUMN website_id BIGINT COMMENT '关联的学习资源网站ID',
ADD CONSTRAINT fk_learning_contents_website
    FOREIGN KEY (website_id) REFERENCES learning_resource_websites(id)
    ON DELETE SET NULL;

-- ============================================================
-- 3. 插入两个学习资源网站
-- ============================================================

-- labuladong: 支持iframe嵌入
INSERT INTO learning_resource_websites (name, display_name, base_url, author, description, supports_iframe)
VALUES (
    'labuladong',
    'labuladong的算法小抄',
    'https://labuladong.online/algo/',
    'labuladong',
    '提供算法可视化和详细讲解的中文算法学习网站',
    true
);

-- hellointerview: 不支持iframe嵌入（X-Frame-Options限制）
INSERT INTO learning_resource_websites (name, display_name, base_url, author, description, supports_iframe)
VALUES (
    'hellointerview',
    'Hello Interview',
    'https://www.hellointerview.com/learn/code',
    'Hello Interview',
    '面试导向的算法学习网站，提供14个Problem Patterns',
    false
);

-- ============================================================
-- 4. 更新现有 learning_contents 记录的 website_id
-- ============================================================

-- 更新 labuladong 资源（假设 URL 包含 labuladong.online）
UPDATE learning_contents
SET website_id = (SELECT id FROM learning_resource_websites WHERE name = 'labuladong')
WHERE url LIKE '%labuladong.online%' OR url LIKE '%labuladong.online%';

-- 更新 hellointerview 资源（假设 URL 包含 hellointerview.com）
UPDATE learning_contents
SET website_id = (SELECT id FROM learning_resource_websites WHERE name = 'hellointerview')
WHERE url LIKE '%hellointerview.com%';

-- ============================================================
-- 验证SQL（可选，用于手动检查）
-- ============================================================
-- SELECT * FROM learning_resource_websites;
-- SELECT id, title, url, website_id FROM learning_contents WHERE website_id IS NOT NULL;
