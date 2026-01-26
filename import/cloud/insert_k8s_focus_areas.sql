-- ================================================
-- Kubernetes Focus Areas and Learning Resources
-- Based on: 极客时间《深入剖析 Kubernetes》
-- Skill: 云计算 (ID=4)
-- Major Category: Kubernetes (ID=22)
-- ================================================

-- 插入 Focus Areas（知识领域）
INSERT INTO focus_areas (name, description, major_category_id, created_at, updated_at) VALUES
('课前必读', '容器技术发展历史，了解容器和K8s的演进过程', 22, NOW(), NOW()),
('容器技术基础', 'Linux容器基础知识：进程、Namespace、Cgroups、镜像、Docker', 22, NOW(), NOW()),
('集群搭建与实践', 'Kubernetes集群部署、kubeadm、第一个容器化应用', 22, NOW(), NOW()),
('容器编排与作业管理', 'Pod、控制器模型、Deployment、StatefulSet、DaemonSet、Job、声明式API、RBAC、Operator', 22, NOW(), NOW()),
('容器持久化存储', 'PV、PVC、StorageClass、Local PV、FlexVolume、CSI插件', 22, NOW(), NOW()),
('容器网络', '容器网络基础、跨主机网络、CNI插件、三层网络方案、Service、DNS、Ingress', 22, NOW(), NOW()),
('作业调度与资源管理', 'K8s资源模型、默认调度器、调度策略、优先级与抢占、GPU管理、Device Plugin', 22, NOW(), NOW()),
('容器运行时', 'CRI、容器运行时、Kata Containers、gVisor', 22, NOW(), NOW()),
('容器监控与日志', 'Prometheus、Metrics Server、Custom Metrics、Auto Scaling、日志收集', 22, NOW(), NOW()),
('开源社区', 'Kubernetes开源社区和未来发展方向', 22, NOW(), NOW());

-- 获取刚插入的 Focus Area IDs（用于后续插入学习资料）
-- 这里需要手动查询后更新，或者使用变量（MySQL 8.0+）

-- ================================================
-- Learning Resources（学习资料）
-- 需要先获取 focus_area_id，然后插入
-- ================================================

-- 1. 课前必读 (假设 focus_area_id = 最新插入的ID)
-- 需要先查询: SELECT id FROM focus_areas WHERE name='课前必读' AND major_category_id=22;

-- 示例插入（需要替换 focus_area_id）：
-- INSERT INTO learning_resources (title, type, url, focus_area_id, created_at, updated_at) VALUES
-- ('开篇词 | 打通"容器技术"的任督二脉', 'ARTICLE', 'https://time.geekbang.org/column/article/14252', <FOCUS_AREA_ID>, NOW(), NOW()),
-- ('01 | 预习篇 · 小鲸鱼大事记（一）：初出茅庐', 'ARTICLE', 'https://time.geekbang.org/column/article/14254', <FOCUS_AREA_ID>, NOW(), NOW()),
-- ...

-- 为了简化，我将创建一个包含完整插入语句的脚本
-- 分两步执行：
-- Step 1: 插入 Focus Areas
-- Step 2: 查询 IDs 后插入 Learning Resources
