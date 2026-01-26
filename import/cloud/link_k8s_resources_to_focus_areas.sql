-- ================================================
-- 将 K8s Learning Resources 关联到对应的 Focus Areas
-- 通过 learning_contents 表建立关联
-- ================================================

-- 1. K8s课前必读 (5讲) → Focus Area ID=97
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    97 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND (lr.title LIKE '开篇词%'
       OR lr.title LIKE '01 | 预习篇%'
       OR lr.title LIKE '02 | 预习篇%'
       OR lr.title LIKE '03 | 预习篇%'
       OR lr.title LIKE '04 | 预习篇%')
ORDER BY lr.id;

-- 2. 容器技术基础 (5讲) → Focus Area ID=98
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    98 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND (lr.title LIKE '05 | 白话容器基础%'
       OR lr.title LIKE '06 | 白话容器基础%'
       OR lr.title LIKE '07 | 白话容器基础%'
       OR lr.title LIKE '08 | 白话容器基础%'
       OR lr.title LIKE '09 | 从容器到容器云%')
ORDER BY lr.id;

-- 3. K8s集群搭建 (3讲) → Focus Area ID=99
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    99 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND (lr.title LIKE '10 | Kubernetes一键部署%'
       OR lr.title LIKE '11 | 从0到1%'
       OR lr.title LIKE '12 | 牛刀小试%')
ORDER BY lr.id;

-- 4. 容器编排与作业 (15讲) → Focus Area ID=100
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    100 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND lr.title REGEXP '^(13|14|15|16|17|18|19|20|21|22|23|24|25|26|27) \\|'
ORDER BY lr.id;

-- 5. 容器持久化存储 (4讲) → Focus Area ID=101
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    101 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND lr.title REGEXP '^(28|29|30|31) \\|'
ORDER BY lr.id;

-- 6. 容器网络 (8讲) → Focus Area ID=102
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    102 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND lr.title REGEXP '^(32|33|34|35|36|37|38|39) \\|'
ORDER BY lr.id;

-- 7. 调度与资源管理 (5讲) → Focus Area ID=103
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    103 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND lr.title REGEXP '^(40|41|42|43|44) \\|'
ORDER BY lr.id;

-- 8. 容器运行时 (3讲) → Focus Area ID=104
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    104 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND lr.title REGEXP '^(45|46|47) \\|'
ORDER BY lr.id;

-- 9. K8s监控与日志 (3讲) → Focus Area ID=105
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    105 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND lr.title REGEXP '^(48|49|50) \\|'
ORDER BY lr.id;

-- 10. K8s开源社区 (5讲：51-52 + 特别放送 + 结束语) → Focus Area ID=106
INSERT INTO learning_contents (focus_area_id, content_type, title, url, sort_order, visibility, created_at, updated_at)
SELECT
    106 as focus_area_id,
    'article' as content_type,
    lr.title,
    lr.url,
    ROW_NUMBER() OVER (ORDER BY lr.id) as sort_order,
    'public' as visibility,
    NOW() as created_at,
    NOW() as updated_at
FROM learning_resources lr
WHERE lr.skill_id = 4
  AND (lr.title REGEXP '^(51|52) \\|'
       OR lr.title LIKE '特别放送%'
       OR lr.title LIKE '结束语%')
ORDER BY lr.id;

-- ========================================
-- 完成！验证插入结果
-- ========================================
SELECT '关联统计:' as info;
SELECT
    fa.name as focus_area,
    COUNT(lc.id) as content_count
FROM focus_areas fa
LEFT JOIN learning_contents lc ON fa.id = lc.focus_area_id
WHERE fa.id BETWEEN 97 AND 106
GROUP BY fa.id, fa.name
ORDER BY fa.id;
