-- ================================================
-- Kubernetes Complete Learning Path Data
-- Based on: 极客时间《深入剖析 Kubernetes》- 张磊
-- Course URL: https://time.geekbang.org/column/intro/100015201
--
-- Skill: 云计算 (ID=4)
-- Major Category: Kubernetes (ID=22)
-- ================================================

-- ========================================
-- Step 1: 插入 Focus Areas（知识领域）
-- Focus Areas 属于 Skill，通过 focus_area_categories 关联到 Major Category
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
-- Step 2: 关联 Focus Areas 到 Major Category (Kubernetes)
-- 使用 focus_area_categories 多对多关联表
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
-- Step 3: 插入 Learning Resources（学习资料）
-- Learning Resources 属于 Skill，不直接关联 Focus Area
-- 通过 learning_contents 关联到 Focus Area
-- ========================================

-- 1. K8s课前必读 (5讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '开篇词 | 打通"容器技术"的任督二脉', 'https://time.geekbang.org/column/article/14252', 1, NOW(), NOW()),
(4, 'ARTICLE', '01 | 预习篇 · 小鲸鱼大事记（一）：初出茅庐', 'https://time.geekbang.org/column/article/14254', 1, NOW(), NOW()),
(4, 'ARTICLE', '02 | 预习篇 · 小鲸鱼大事记（二）：崭露头角', 'https://time.geekbang.org/column/article/14256', 1, NOW(), NOW()),
(4, 'ARTICLE', '03 | 预习篇 · 小鲸鱼大事记（三）：群雄并起', 'https://time.geekbang.org/column/article/14405', 1, NOW(), NOW()),
(4, 'ARTICLE', '04 | 预习篇 · 小鲸鱼大事记（四）：尘埃落定', 'https://time.geekbang.org/column/article/14406', 1, NOW(), NOW());

-- 2. 容器技术基础 (5讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '05 | 白话容器基础（一）：从进程说开去', 'https://time.geekbang.org/column/article/14642', 1, NOW(), NOW()),
(4, 'ARTICLE', '06 | 白话容器基础（二）：隔离与限制', 'https://time.geekbang.org/column/article/14653', 1, NOW(), NOW()),
(4, 'ARTICLE', '07 | 白话容器基础（三）：深入理解容器镜像', 'https://time.geekbang.org/column/article/17921', 1, NOW(), NOW()),
(4, 'ARTICLE', '08 | 白话容器基础（四）：重新认识Docker容器', 'https://time.geekbang.org/column/article/18119', 1, NOW(), NOW()),
(4, 'ARTICLE', '09 | 从容器到容器云：谈谈Kubernetes的本质', 'https://time.geekbang.org/column/article/23132', 1, NOW(), NOW());

-- 3. K8s集群搭建 (3讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '10 | Kubernetes一键部署利器：kubeadm', 'https://time.geekbang.org/column/article/39712', 1, NOW(), NOW()),
(4, 'ARTICLE', '11 | 从0到1：搭建一个完整的Kubernetes集群', 'https://time.geekbang.org/column/article/39724', 1, NOW(), NOW()),
(4, 'ARTICLE', '12 | 牛刀小试：我的第一个容器化应用', 'https://time.geekbang.org/column/article/40008', 1, NOW(), NOW());

-- 4. 容器编排与作业 (15讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '13 | 为什么我们需要Pod？', 'https://time.geekbang.org/column/article/40092', 1, NOW(), NOW()),
(4, 'ARTICLE', '14 | 深入解析Pod对象（一）：基本概念', 'https://time.geekbang.org/column/article/40366', 1, NOW(), NOW()),
(4, 'ARTICLE', '15 | 深入解析Pod对象（二）：使用进阶', 'https://time.geekbang.org/column/article/40466', 1, NOW(), NOW()),
(4, 'ARTICLE', '16 | 编排其实很简单：谈谈"控制器"模型', 'https://time.geekbang.org/column/article/40583', 1, NOW(), NOW()),
(4, 'ARTICLE', '17 | 经典PaaS的记忆：作业副本与水平扩展', 'https://time.geekbang.org/column/article/40906', 1, NOW(), NOW()),
(4, 'ARTICLE', '18 | 深入理解StatefulSet（一）：拓扑状态', 'https://time.geekbang.org/column/article/41017', 1, NOW(), NOW()),
(4, 'ARTICLE', '19 | 深入理解StatefulSet（二）：存储状态', 'https://time.geekbang.org/column/article/41154', 1, NOW(), NOW()),
(4, 'ARTICLE', '20 | 深入理解StatefulSet（三）：有状态应用实践', 'https://time.geekbang.org/column/article/41217', 1, NOW(), NOW()),
(4, 'ARTICLE', '21 | 容器化守护进程的意义：DaemonSet', 'https://time.geekbang.org/column/article/41366', 1, NOW(), NOW()),
(4, 'ARTICLE', '22 | 撬动离线业务：Job与CronJob', 'https://time.geekbang.org/column/article/41607', 1, NOW(), NOW()),
(4, 'ARTICLE', '23 | 声明式API与Kubernetes编程范式', 'https://time.geekbang.org/column/article/41769', 1, NOW(), NOW()),
(4, 'ARTICLE', '24 | 深入解析声明式API（一）：API对象的奥秘', 'https://time.geekbang.org/column/article/41876', 1, NOW(), NOW()),
(4, 'ARTICLE', '25 | 深入解析声明式API（二）：编写自定义控制器', 'https://time.geekbang.org/column/article/42076', 1, NOW(), NOW()),
(4, 'ARTICLE', '26 | 基于角色的权限控制：RBAC', 'https://time.geekbang.org/column/article/42154', 1, NOW(), NOW()),
(4, 'ARTICLE', '27 | 聪明的微创新：Operator工作原理解读', 'https://time.geekbang.org/column/article/42493', 1, NOW(), NOW());

-- 5. 容器持久化存储 (4讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '28 | PV、PVC、StorageClass，这些到底在说啥？', 'https://time.geekbang.org/column/article/42698', 1, NOW(), NOW()),
(4, 'ARTICLE', '29 | PV、PVC体系是不是多此一举？从本地持久化卷谈起', 'https://time.geekbang.org/column/article/42819', 1, NOW(), NOW()),
(4, 'ARTICLE', '30 | 编写自己的存储插件：FlexVolume与CSI', 'https://time.geekbang.org/column/article/44245', 1, NOW(), NOW()),
(4, 'ARTICLE', '31 | 容器存储实践：CSI插件编写指南', 'https://time.geekbang.org/column/article/64392', 1, NOW(), NOW());

-- 6. 容器网络 (8讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '32 | 浅谈容器网络', 'https://time.geekbang.org/column/article/64948', 1, NOW(), NOW()),
(4, 'ARTICLE', '33 | 深入解析容器跨主机网络', 'https://time.geekbang.org/column/article/65287', 1, NOW(), NOW()),
(4, 'ARTICLE', '34 | Kubernetes网络模型与CNI网络插件', 'https://time.geekbang.org/column/article/67351', 1, NOW(), NOW()),
(4, 'ARTICLE', '35 | 解读Kubernetes三层网络方案', 'https://time.geekbang.org/column/article/67775', 1, NOW(), NOW()),
(4, 'ARTICLE', '36 | 为什么说Kubernetes只有soft multi-tenancy？', 'https://time.geekbang.org/column/article/68316', 1, NOW(), NOW()),
(4, 'ARTICLE', '37 | 找到容器不容易：Service、DNS与服务发现', 'https://time.geekbang.org/column/article/68636', 1, NOW(), NOW()),
(4, 'ARTICLE', '38 | 从外界连通Service与Service调试"三板斧"', 'https://time.geekbang.org/column/article/68964', 1, NOW(), NOW()),
(4, 'ARTICLE', '39 | 谈谈Service与Ingress', 'https://time.geekbang.org/column/article/69214', 1, NOW(), NOW());

-- 7. 调度与资源管理 (5讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '40 | Kubernetes的资源模型与资源管理', 'https://time.geekbang.org/column/article/69678', 1, NOW(), NOW()),
(4, 'ARTICLE', '41 | 十字路口上的Kubernetes默认调度器', 'https://time.geekbang.org/column/article/69890', 1, NOW(), NOW()),
(4, 'ARTICLE', '42 | Kubernetes默认调度器调度策略解析', 'https://time.geekbang.org/column/article/70211', 1, NOW(), NOW()),
(4, 'ARTICLE', '43 | Kubernetes默认调度器的优先级与抢占机制', 'https://time.geekbang.org/column/article/70519', 1, NOW(), NOW()),
(4, 'ARTICLE', '44 | Kubernetes GPU管理与Device Plugin机制', 'https://time.geekbang.org/column/article/70876', 1, NOW(), NOW());

-- 8. 容器运行时 (3讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '45 | 幕后英雄：SIG-Node与CRI', 'https://time.geekbang.org/column/article/71056', 1, NOW(), NOW()),
(4, 'ARTICLE', '46 | 解读 CRI 与 容器运行时', 'https://time.geekbang.org/column/article/71499', 1, NOW(), NOW()),
(4, 'ARTICLE', '47 | 绝不仅仅是安全：Kata Containers 与 gVisor', 'https://time.geekbang.org/column/article/71606', 1, NOW(), NOW());

-- 9. K8s监控与日志 (3讲)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '48 | Prometheus、Metrics Server与Kubernetes监控体系', 'https://time.geekbang.org/column/article/72281', 1, NOW(), NOW()),
(4, 'ARTICLE', '49 | Custom Metrics: 让Auto Scaling不再"食之无味"', 'https://time.geekbang.org/column/article/72693', 1, NOW(), NOW()),
(4, 'ARTICLE', '50 | 让日志无处可逃：容器日志收集与管理', 'https://time.geekbang.org/column/article/73156', 1, NOW(), NOW());

-- 10. K8s开源社区 (5讲：社区+答疑+特别放送+结束语)
INSERT INTO learning_resources (skill_id, resource_type, title, url, is_official, created_at, updated_at) VALUES
(4, 'ARTICLE', '51 | 谈谈Kubernetes开源社区和未来走向', 'https://time.geekbang.org/column/article/73477', 1, NOW(), NOW()),
(4, 'ARTICLE', '52 | 答疑：在问题中解决问题，在思考中产生思考', 'https://time.geekbang.org/column/article/73790', 1, NOW(), NOW()),
(4, 'ARTICLE', '特别放送 | 2019 年，容器技术生态会发生些什么？', 'https://time.geekbang.org/column/article/83596', 1, NOW(), NOW()),
(4, 'ARTICLE', '特别放送 | 基于 Kubernetes 的云原生应用管理，到底应该怎么做？', 'https://time.geekbang.org/column/article/114197', 1, NOW(), NOW()),
(4, 'ARTICLE', '结束语 | Kubernetes：赢开发者赢天下', 'https://time.geekbang.org/column/article/74278', 1, NOW(), NOW());

-- ========================================
-- 完成！
-- ========================================
-- 总计：
-- - 10 个 Focus Areas（关联到 Kubernetes 大类别）
-- - 57 篇学习资料（官方课程）
-- ========================================
