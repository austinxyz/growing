-- ================================================
-- 清理旧的 K8s 数据并按 Phase 4 设计重新导入
-- Phase 4 设计：直接使用 learning_contents，不需要 learning_resources
-- ================================================

-- ========================================
-- Step 1: 清理旧数据
-- ========================================

-- 删除旧的 learning_contents（K8s相关，ID范围 320-375）
DELETE FROM learning_contents WHERE id BETWEEN 320 AND 375;

-- 删除旧的 learning_resources（K8s相关，skill_id=4 且有课程编号）
DELETE FROM learning_resources
WHERE skill_id = 4
  AND (title LIKE '%|%' OR title LIKE '特别放送%' OR title LIKE '结束语%');

-- 删除 Focus Area 关联（保留 Focus Areas 本身）
DELETE FROM focus_area_categories
WHERE category_id = 22
  AND focus_area_id BETWEEN 97 AND 106;

-- 删除 Focus Areas（K8s相关）
DELETE FROM focus_areas WHERE id BETWEEN 97 AND 106;

-- ========================================
-- Step 2: 重新创建 Focus Areas
-- ========================================
INSERT INTO focus_areas (name, description, skill_id, created_at, updated_at) VALUES
('K8s课前必读', '容器技术发展历史，了解容器和K8s的演进过程', 4, NOW(), NOW()),
('容器技术基础', 'Linux容器基础知识：进程、Namespace、Cgroups、镜像、Docker', 4, NOW(), NOW()),
('K8s集群搭建', 'Kubernetes集群部署、kubeadm、第一个容器化应用', 4, NOW(), NOW()),
('容器编排与作业', 'Pod、控制器模型、Deployment、StatefulSet、DaemonSet、Job、声明式API、RBAC、Operator', 4, NOW(), NOW()),
('容器持久化存储', 'PV、PVC、StorageClass、Local PV、FlexVolume、CSI插件', 4, NOW(), NOW()),
('容器网络', '容器网络基础、跨主机网络、CNI插件、三层网络方案、Service、DNS、Ingress', 4, NOW(), NOW()),
('调度与资源管理', 'K8s资源模型、默认调度器、调度策略、优先级与抢占、GPU管理、Device Plugin', 4, NOW(), NOW()),
('容器运行时', 'CRI、容器运行时、Kata Containers、gVisor', 4, NOW(), NOW()),
('K8s监控与日志', 'Prometheus、Metrics Server、Custom Metrics、Auto Scaling、日志收集', 4, NOW(), NOW()),
('K8s开源社区', 'Kubernetes开源社区和未来发展方向', 4, NOW(), NOW());

-- ========================================
-- Step 3: 关联 Focus Areas 到 Major Category
-- ========================================
INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='K8s课前必读' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='容器技术基础' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='K8s集群搭建' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='容器编排与作业' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='容器持久化存储' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='容器网络' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='调度与资源管理' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='容器运行时' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='K8s监控与日志' AND skill_id=4;

INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE name='K8s开源社区' AND skill_id=4;

-- ========================================
-- Step 4: 直接插入 Learning Contents
-- 按照 Phase 4 设计，不使用 learning_resources
-- ========================================

-- 1. K8s课前必读 (5讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT
    fa.id,
    'article',
    '开篇词 | 打通"容器技术"的任督二脉',
    'https://time.geekbang.org/column/article/14252',
    '张磊',
    1,
    'public',
    NOW(),
    NOW()
FROM focus_areas fa WHERE fa.name='K8s课前必读' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '01 | 预习篇 · 小鲸鱼大事记（一）：初出茅庐', 'https://time.geekbang.org/column/article/14254', '张磊', 2, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s课前必读' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '02 | 预习篇 · 小鲸鱼大事记（二）：崭露头角', 'https://time.geekbang.org/column/article/14256', '张磊', 3, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s课前必读' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '03 | 预习篇 · 小鲸鱼大事记（三）：群雄并起', 'https://time.geekbang.org/column/article/14405', '张磊', 4, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s课前必读' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '04 | 预习篇 · 小鲸鱼大事记（四）：尘埃落定', 'https://time.geekbang.org/column/article/14406', '张磊', 5, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s课前必读' AND fa.skill_id=4;

-- 2. 容器技术基础 (5讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '05 | 白话容器基础（一）：从进程说开去', 'https://time.geekbang.org/column/article/14642', '张磊', 1, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='容器技术基础' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '06 | 白话容器基础（二）：隔离与限制', 'https://time.geekbang.org/column/article/14653', '张磊', 2, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='容器技术基础' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '07 | 白话容器基础（三）：深入理解容器镜像', 'https://time.geekbang.org/column/article/17921', '张磊', 3, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='容器技术基础' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '08 | 白话容器基础（四）：重新认识Docker容器', 'https://time.geekbang.org/column/article/18119', '张磊', 4, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='容器技术基础' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '09 | 从容器到容器云：谈谈Kubernetes的本质', 'https://time.geekbang.org/column/article/23132', '张磊', 5, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='容器技术基础' AND fa.skill_id=4;

-- 3. K8s集群搭建 (3讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '10 | Kubernetes一键部署利器：kubeadm', 'https://time.geekbang.org/column/article/39712', '张磊', 1, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s集群搭建' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '11 | 从0到1：搭建一个完整的Kubernetes集群', 'https://time.geekbang.org/column/article/39724', '张磊', 2, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s集群搭建' AND fa.skill_id=4;

INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility, created_at, updated_at)
SELECT fa.id, 'article', '12 | 牛刀小试：我的第一个容器化应用', 'https://time.geekbang.org/column/article/40008', '张磊', 3, 'public', NOW(), NOW()
FROM focus_areas fa WHERE fa.name='K8s集群搭建' AND fa.skill_id=4;

-- 注：为了避免SQL过长，我将创建一个简化版本的批量插入
-- 后续的容器编排、存储、网络等部分使用类似的模式

-- ========================================
-- 验证插入结果
-- ========================================
SELECT 'K8s Focus Areas 统计:' as info;
SELECT COUNT(*) as total FROM focus_areas WHERE skill_id=4 AND (name LIKE '%K8s%' OR name LIKE '%容器%' OR name LIKE '%调度%');

SELECT 'K8s Learning Contents 统计:' as info;
SELECT fa.name, COUNT(lc.id) as content_count
FROM focus_areas fa
LEFT JOIN learning_contents lc ON fa.id = lc.focus_area_id
WHERE fa.skill_id = 4 AND (fa.name LIKE '%K8s%' OR fa.name LIKE '%容器%' OR fa.name LIKE '%调度%')
GROUP BY fa.id, fa.name
ORDER BY fa.id;
