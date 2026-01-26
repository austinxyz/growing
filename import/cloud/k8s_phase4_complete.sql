-- ================================================
-- K8s 学习路径完整导入（按 Phase 4 设计）
-- 直接使用 learning_contents，不使用 learning_resources
-- ================================================

-- ========================================
-- Step 1: 清理旧数据
-- ========================================
DELETE FROM learning_contents WHERE focus_area_id BETWEEN 97 AND 106;
DELETE FROM learning_resources WHERE skill_id = 4 AND title LIKE '%|%';
DELETE FROM focus_area_categories WHERE category_id = 22 AND focus_area_id BETWEEN 97 AND 106;
DELETE FROM focus_areas WHERE id BETWEEN 97 AND 106;

-- ========================================
-- Step 2: 创建 Focus Areas
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
-- Step 3: 关联到 Kubernetes 大类别
-- ========================================
INSERT INTO focus_area_categories (focus_area_id, category_id, created_at)
SELECT id, 22, NOW() FROM focus_areas WHERE skill_id=4 AND name IN (
    'K8s课前必读', '容器技术基础', 'K8s集群搭建', '容器编排与作业', '容器持久化存储',
    '容器网络', '调度与资源管理', '容器运行时', 'K8s监控与日志', 'K8s开源社区'
);

-- ========================================
-- Step 4: 插入 Learning Contents（分批处理）
-- ========================================

-- 获取 Focus Area IDs（用于变量）
SET @fa_prereq = (SELECT id FROM focus_areas WHERE name='K8s课前必读' AND skill_id=4);
SET @fa_container = (SELECT id FROM focus_areas WHERE name='容器技术基础' AND skill_id=4);
SET @fa_setup = (SELECT id FROM focus_areas WHERE name='K8s集群搭建' AND skill_id=4);
SET @fa_orchestration = (SELECT id FROM focus_areas WHERE name='容器编排与作业' AND skill_id=4);
SET @fa_storage = (SELECT id FROM focus_areas WHERE name='容器持久化存储' AND skill_id=4);
SET @fa_network = (SELECT id FROM focus_areas WHERE name='容器网络' AND skill_id=4);
SET @fa_scheduling = (SELECT id FROM focus_areas WHERE name='调度与资源管理' AND skill_id=4);
SET @fa_runtime = (SELECT id FROM focus_areas WHERE name='容器运行时' AND skill_id=4);
SET @fa_monitoring = (SELECT id FROM focus_areas WHERE name='K8s监控与日志' AND skill_id=4);
SET @fa_community = (SELECT id FROM focus_areas WHERE name='K8s开源社区' AND skill_id=4);

-- 1. K8s课前必读 (5讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_prereq, 'article', '开篇词 | 打通"容器技术"的任督二脉', 'https://time.geekbang.org/column/article/14252', '张磊', 1, 'public'),
(@fa_prereq, 'article', '01 | 预习篇 · 小鲸鱼大事记（一）：初出茅庐', 'https://time.geekbang.org/column/article/14254', '张磊', 2, 'public'),
(@fa_prereq, 'article', '02 | 预习篇 · 小鲸鱼大事记（二）：崭露头角', 'https://time.geekbang.org/column/article/14256', '张磊', 3, 'public'),
(@fa_prereq, 'article', '03 | 预习篇 · 小鲸鱼大事记（三）：群雄并起', 'https://time.geekbang.org/column/article/14405', '张磊', 4, 'public'),
(@fa_prereq, 'article', '04 | 预习篇 · 小鲸鱼大事记（四）：尘埃落定', 'https://time.geekbang.org/column/article/14406', '张磊', 5, 'public');

-- 2. 容器技术基础 (5讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_container, 'article', '05 | 白话容器基础（一）：从进程说开去', 'https://time.geekbang.org/column/article/14642', '张磊', 1, 'public'),
(@fa_container, 'article', '06 | 白话容器基础（二）：隔离与限制', 'https://time.geekbang.org/column/article/14653', '张磊', 2, 'public'),
(@fa_container, 'article', '07 | 白话容器基础（三）：深入理解容器镜像', 'https://time.geekbang.org/column/article/17921', '张磊', 3, 'public'),
(@fa_container, 'article', '08 | 白话容器基础（四）：重新认识Docker容器', 'https://time.geekbang.org/column/article/18119', '张磊', 4, 'public'),
(@fa_container, 'article', '09 | 从容器到容器云：谈谈Kubernetes的本质', 'https://time.geekbang.org/column/article/23132', '张磊', 5, 'public');

-- 3. K8s集群搭建 (3讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_setup, 'article', '10 | Kubernetes一键部署利器：kubeadm', 'https://time.geekbang.org/column/article/39712', '张磊', 1, 'public'),
(@fa_setup, 'article', '11 | 从0到1：搭建一个完整的Kubernetes集群', 'https://time.geekbang.org/column/article/39724', '张磊', 2, 'public'),
(@fa_setup, 'article', '12 | 牛刀小试：我的第一个容器化应用', 'https://time.geekbang.org/column/article/40008', '张磊', 3, 'public');

-- 4. 容器编排与作业 (15讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_orchestration, 'article', '13 | 为什么我们需要Pod？', 'https://time.geekbang.org/column/article/40092', '张磊', 1, 'public'),
(@fa_orchestration, 'article', '14 | 深入解析Pod对象（一）：基本概念', 'https://time.geekbang.org/column/article/40366', '张磊', 2, 'public'),
(@fa_orchestration, 'article', '15 | 深入解析Pod对象（二）：使用进阶', 'https://time.geekbang.org/column/article/40466', '张磊', 3, 'public'),
(@fa_orchestration, 'article', '16 | 编排其实很简单：谈谈"控制器"模型', 'https://time.geekbang.org/column/article/40583', '张磊', 4, 'public'),
(@fa_orchestration, 'article', '17 | 经典PaaS的记忆：作业副本与水平扩展', 'https://time.geekbang.org/column/article/40906', '张磊', 5, 'public'),
(@fa_orchestration, 'article', '18 | 深入理解StatefulSet（一）：拓扑状态', 'https://time.geekbang.org/column/article/41017', '张磊', 6, 'public'),
(@fa_orchestration, 'article', '19 | 深入理解StatefulSet（二）：存储状态', 'https://time.geekbang.org/column/article/41154', '张磊', 7, 'public'),
(@fa_orchestration, 'article', '20 | 深入理解StatefulSet（三）：有状态应用实践', 'https://time.geekbang.org/column/article/41217', '张磊', 8, 'public'),
(@fa_orchestration, 'article', '21 | 容器化守护进程的意义：DaemonSet', 'https://time.geekbang.org/column/article/41366', '张磊', 9, 'public'),
(@fa_orchestration, 'article', '22 | 撬动离线业务：Job与CronJob', 'https://time.geekbang.org/column/article/41607', '张磊', 10, 'public'),
(@fa_orchestration, 'article', '23 | 声明式API与Kubernetes编程范式', 'https://time.geekbang.org/column/article/41769', '张磊', 11, 'public'),
(@fa_orchestration, 'article', '24 | 深入解析声明式API（一）：API对象的奥秘', 'https://time.geekbang.org/column/article/41876', '张磊', 12, 'public'),
(@fa_orchestration, 'article', '25 | 深入解析声明式API（二）：编写自定义控制器', 'https://time.geekbang.org/column/article/42076', '张磊', 13, 'public'),
(@fa_orchestration, 'article', '26 | 基于角色的权限控制：RBAC', 'https://time.geekbang.org/column/article/42154', '张磊', 14, 'public'),
(@fa_orchestration, 'article', '27 | 聪明的微创新：Operator工作原理解读', 'https://time.geekbang.org/column/article/42493', '张磊', 15, 'public');

-- 5. 容器持久化存储 (4讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_storage, 'article', '28 | PV、PVC、StorageClass，这些到底在说啥？', 'https://time.geekbang.org/column/article/42698', '张磊', 1, 'public'),
(@fa_storage, 'article', '29 | PV、PVC体系是不是多此一举？从本地持久化卷谈起', 'https://time.geekbang.org/column/article/42819', '张磊', 2, 'public'),
(@fa_storage, 'article', '30 | 编写自己的存储插件：FlexVolume与CSI', 'https://time.geekbang.org/column/article/44245', '张磊', 3, 'public'),
(@fa_storage, 'article', '31 | 容器存储实践：CSI插件编写指南', 'https://time.geekbang.org/column/article/64392', '张磊', 4, 'public');

-- 6. 容器网络 (8讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_network, 'article', '32 | 浅谈容器网络', 'https://time.geekbang.org/column/article/64948', '张磊', 1, 'public'),
(@fa_network, 'article', '33 | 深入解析容器跨主机网络', 'https://time.geekbang.org/column/article/65287', '张磊', 2, 'public'),
(@fa_network, 'article', '34 | Kubernetes网络模型与CNI网络插件', 'https://time.geekbang.org/column/article/67351', '张磊', 3, 'public'),
(@fa_network, 'article', '35 | 解读Kubernetes三层网络方案', 'https://time.geekbang.org/column/article/67775', '张磊', 4, 'public'),
(@fa_network, 'article', '36 | 为什么说Kubernetes只有soft multi-tenancy？', 'https://time.geekbang.org/column/article/68316', '张磊', 5, 'public'),
(@fa_network, 'article', '37 | 找到容器不容易：Service、DNS与服务发现', 'https://time.geekbang.org/column/article/68636', '张磊', 6, 'public'),
(@fa_network, 'article', '38 | 从外界连通Service与Service调试"三板斧"', 'https://time.geekbang.org/column/article/68964', '张磊', 7, 'public'),
(@fa_network, 'article', '39 | 谈谈Service与Ingress', 'https://time.geekbang.org/column/article/69214', '张磊', 8, 'public');

-- 7. 调度与资源管理 (5讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_scheduling, 'article', '40 | Kubernetes的资源模型与资源管理', 'https://time.geekbang.org/column/article/69678', '张磊', 1, 'public'),
(@fa_scheduling, 'article', '41 | 十字路口上的Kubernetes默认调度器', 'https://time.geekbang.org/column/article/69890', '张磊', 2, 'public'),
(@fa_scheduling, 'article', '42 | Kubernetes默认调度器调度策略解析', 'https://time.geekbang.org/column/article/70211', '张磊', 3, 'public'),
(@fa_scheduling, 'article', '43 | Kubernetes默认调度器的优先级与抢占机制', 'https://time.geekbang.org/column/article/70519', '张磊', 4, 'public'),
(@fa_scheduling, 'article', '44 | Kubernetes GPU管理与Device Plugin机制', 'https://time.geekbang.org/column/article/70876', '张磊', 5, 'public');

-- 8. 容器运行时 (3讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_runtime, 'article', '45 | 幕后英雄：SIG-Node与CRI', 'https://time.geekbang.org/column/article/71056', '张磊', 1, 'public'),
(@fa_runtime, 'article', '46 | 解读 CRI 与 容器运行时', 'https://time.geekbang.org/column/article/71499', '张磊', 2, 'public'),
(@fa_runtime, 'article', '47 | 绝不仅仅是安全：Kata Containers 与 gVisor', 'https://time.geekbang.org/column/article/71606', '张磊', 3, 'public');

-- 9. K8s监控与日志 (3讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_monitoring, 'article', '48 | Prometheus、Metrics Server与Kubernetes监控体系', 'https://time.geekbang.org/column/article/72281', '张磊', 1, 'public'),
(@fa_monitoring, 'article', '49 | Custom Metrics: 让Auto Scaling不再"食之无味"', 'https://time.geekbang.org/column/article/72693', '张磊', 2, 'public'),
(@fa_monitoring, 'article', '50 | 让日志无处可逃：容器日志收集与管理', 'https://time.geekbang.org/column/article/73156', '张磊', 3, 'public');

-- 10. K8s开源社区 (5讲)
INSERT INTO learning_contents (focus_area_id, content_type, title, url, author, sort_order, visibility) VALUES
(@fa_community, 'article', '51 | 谈谈Kubernetes开源社区和未来走向', 'https://time.geekbang.org/column/article/73477', '张磊', 1, 'public'),
(@fa_community, 'article', '52 | 答疑：在问题中解决问题，在思考中产生思考', 'https://time.geekbang.org/column/article/73790', '张磊', 2, 'public'),
(@fa_community, 'article', '特别放送 | 2019 年，容器技术生态会发生些什么？', 'https://time.geekbang.org/column/article/83596', '张磊', 3, 'public'),
(@fa_community, 'article', '特别放送 | 基于 Kubernetes 的云原生应用管理，到底应该怎么做？', 'https://time.geekbang.org/column/article/114197', '张磊', 4, 'public'),
(@fa_community, 'article', '结束语 | Kubernetes：赢开发者赢天下', 'https://time.geekbang.org/column/article/74278', '张磊', 5, 'public');

-- ========================================
-- 验证结果
-- ========================================
SELECT '导入完成统计:' as info;
SELECT
    fa.name as focus_area,
    COUNT(lc.id) as content_count
FROM focus_areas fa
LEFT JOIN learning_contents lc ON fa.id = lc.focus_area_id
WHERE fa.skill_id = 4
  AND fa.name IN ('K8s课前必读', '容器技术基础', 'K8s集群搭建', '容器编排与作业', '容器持久化存储',
                  '容器网络', '调度与资源管理', '容器运行时', 'K8s监控与日志', 'K8s开源社区')
GROUP BY fa.id, fa.name
ORDER BY fa.id;
