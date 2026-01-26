-- ================================================
-- 添加极客时间网站并更新 K8s 学习资料关联
-- ================================================

-- ========================================
-- Step 1: 添加极客时间网站
-- ========================================
INSERT INTO learning_resource_websites (name, display_name, base_url, author, description, supports_iframe, created_at, updated_at)
VALUES (
    'geektime',
    '极客时间',
    'https://time.geekbang.org/',
    NULL,
    '极客邦科技旗下IT内容学习服务平台，提供专栏、视频课程、直播等多种形式的技术内容',
    0,  -- 极客时间通常不支持 iframe 嵌入（付费内容）
    NOW(),
    NOW()
);

-- 获取刚插入的 website_id
SET @geektime_id = LAST_INSERT_ID();

-- ========================================
-- Step 2: 更新 K8s Learning Contents 的 website_id
-- ========================================

-- 更新所有 K8s 相关的 learning_contents
-- 通过 focus_area_id 找到所有 K8s 的学习内容
UPDATE learning_contents lc
JOIN focus_areas fa ON lc.focus_area_id = fa.id
SET lc.website_id = @geektime_id,
    lc.updated_at = NOW()
WHERE fa.skill_id = 4  -- 云计算
  AND fa.name IN (
    'K8s课前必读',
    '容器技术基础',
    'K8s集群搭建',
    '容器编排与作业',
    '容器持久化存储',
    '容器网络',
    '调度与资源管理',
    '容器运行时',
    'K8s监控与日志',
    'K8s开源社区'
  );

-- ========================================
-- Step 3: 验证更新结果
-- ========================================
SELECT '新增的网站:' as info;
SELECT id, name, display_name, base_url, description
FROM learning_resource_websites
WHERE name = 'geektime';

SELECT 'K8s学习资料website关联统计:' as info;
SELECT
    w.display_name as website,
    fa.name as focus_area,
    COUNT(lc.id) as content_count
FROM learning_contents lc
JOIN focus_areas fa ON lc.focus_area_id = fa.id
JOIN learning_resource_websites w ON lc.website_id = w.id
WHERE fa.skill_id = 4
  AND fa.name IN (
    'K8s课前必读',
    '容器技术基础',
    'K8s集群搭建',
    '容器编排与作业',
    '容器持久化存储',
    '容器网络',
    '调度与资源管理',
    '容器运行时',
    'K8s监控与日志',
    'K8s开源社区'
  )
GROUP BY w.id, w.display_name, fa.id, fa.name
ORDER BY fa.id;

SELECT 'K8s学习资料示例（带website信息）:' as info;
SELECT
    lc.id,
    fa.name as focus_area,
    lc.title,
    w.display_name as website,
    lc.url
FROM learning_contents lc
JOIN focus_areas fa ON lc.focus_area_id = fa.id
JOIN learning_resource_websites w ON lc.website_id = w.id
WHERE fa.name = '容器技术基础' AND fa.skill_id = 4
LIMIT 3;
