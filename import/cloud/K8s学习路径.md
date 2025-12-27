# Kubernetes 学习路径

> 来源: 极客时间《深入剖析 Kubernetes》- 张磊
>
> Kubernetes 社区资深成员与项目维护者
>
> 课程链接: https://time.geekbang.org/column/intro/100015201

共 57 讲（已完结）| 122553 人已学习

---

## 课前必读 (5讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 开篇词 \| 打通"容器技术"的任督二脉 | 08:46 | [查看](https://time.geekbang.org/column/article/14252) |
| 2 | 01 \| 预习篇 · 小鲸鱼大事记（一）：初出茅庐 | 12:12 | [查看](https://time.geekbang.org/column/article/14254) |
| 3 | 02 \| 预习篇 · 小鲸鱼大事记（二）：崭露头角 | 06:51 | [查看](https://time.geekbang.org/column/article/14256) |
| 4 | 03 \| 预习篇 · 小鲸鱼大事记（三）：群雄并起 | 10:42 | [查看](https://time.geekbang.org/column/article/14405) |
| 5 | 04 \| 预习篇 · 小鲸鱼大事记（四）：尘埃落定 | 15:03 | [查看](https://time.geekbang.org/column/article/14406) |

## 容器技术概念入门篇 (5讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 05 \| 白话容器基础（一）：从进程说开去 | 11:33 | [查看](https://time.geekbang.org/column/article/14642) |
| 2 | 06 \| 白话容器基础（二）：隔离与限制 | 16:36 | [查看](https://time.geekbang.org/column/article/14653) |
| 3 | 07 \| 白话容器基础（三）：深入理解容器镜像 | 19:35 | [查看](https://time.geekbang.org/column/article/17921) |
| 4 | 08 \| 白话容器基础（四）：重新认识Docker容器 | 23:01 | [查看](https://time.geekbang.org/column/article/18119) |
| 5 | 09 \| 从容器到容器云：谈谈Kubernetes的本质 | 21:17 | [查看](https://time.geekbang.org/column/article/23132) |

## Kubernetes集群搭建与实践 (3讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 10 \| Kubernetes一键部署利器：kubeadm | 17:21 | [查看](https://time.geekbang.org/column/article/39712) |
| 2 | 11 \| 从0到1：搭建一个完整的Kubernetes集群 | 17:53 | [查看](https://time.geekbang.org/column/article/39724) |
| 3 | 12 \| 牛刀小试：我的第一个容器化应用 | 13:16 | [查看](https://time.geekbang.org/column/article/40008) |

## 容器编排与Kubernetes作业管理 (15讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 13 \| 为什么我们需要Pod？ | 18:45 | [查看](https://time.geekbang.org/column/article/40092) |
| 2 | 14 \| 深入解析Pod对象（一）：基本概念 | 11:48 | [查看](https://time.geekbang.org/column/article/40366) |
| 3 | 15 \| 深入解析Pod对象（二）：使用进阶 | 21:48 | [查看](https://time.geekbang.org/column/article/40466) |
| 4 | 16 \| 编排其实很简单：谈谈"控制器"模型 | 07:51 | [查看](https://time.geekbang.org/column/article/40583) |
| 5 | 17 \| 经典PaaS的记忆：作业副本与水平扩展 | 17:54 | [查看](https://time.geekbang.org/column/article/40906) |
| 6 | 18 \| 深入理解StatefulSet（一）：拓扑状态 | 11:53 | [查看](https://time.geekbang.org/column/article/41017) |
| 7 | 19 \| 深入理解StatefulSet（二）：存储状态 | 12:51 | [查看](https://time.geekbang.org/column/article/41154) |
| 8 | 20 \| 深入理解StatefulSet（三）：有状态应用实践 | 21:51 | [查看](https://time.geekbang.org/column/article/41217) |
| 9 | 21 \| 容器化守护进程的意义：DaemonSet | 17:28 | [查看](https://time.geekbang.org/column/article/41366) |
| 10 | 22 \| 撬动离线业务：Job与CronJob | 19:32 | [查看](https://time.geekbang.org/column/article/41607) |
| 11 | 23 \| 声明式API与Kubernetes编程范式 | 16:43 | [查看](https://time.geekbang.org/column/article/41769) |
| 12 | 24 \| 深入解析声明式API（一）：API对象的奥秘 | 18:44 | [查看](https://time.geekbang.org/column/article/41876) |
| 13 | 25 \| 深入解析声明式API（二）：编写自定义控制器 | 17:50 | [查看](https://time.geekbang.org/column/article/42076) |
| 14 | 26 \| 基于角色的权限控制：RBAC | 14:03 | [查看](https://time.geekbang.org/column/article/42154) |
| 15 | 27 \| 聪明的微创新：Operator工作原理解读 | 22:38 | [查看](https://time.geekbang.org/column/article/42493) |

## Kubernetes容器持久化存储 (4讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 28 \| PV、PVC、StorageClass，这些到底在说啥？ | 18:54 | [查看](https://time.geekbang.org/column/article/42698) |
| 2 | 29 \| PV、PVC体系是不是多此一举？从本地持久化卷谈起 | 15:45 | [查看](https://time.geekbang.org/column/article/42819) |
| 3 | 30 \| 编写自己的存储插件：FlexVolume与CSI | 15:07 | [查看](https://time.geekbang.org/column/article/44245) |
| 4 | 31 \| 容器存储实践：CSI插件编写指南 | 14:15 | [查看](https://time.geekbang.org/column/article/64392) |

## Kubernetes容器网络 (8讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 32 \| 浅谈容器网络 | 11:31 | [查看](https://time.geekbang.org/column/article/64948) |
| 2 | 33 \| 深入解析容器跨主机网络 | 18:59 | [查看](https://time.geekbang.org/column/article/65287) |
| 3 | 34 \| Kubernetes网络模型与CNI网络插件 | 17:24 | [查看](https://time.geekbang.org/column/article/67351) |
| 4 | 35 \| 解读Kubernetes三层网络方案 | 19:30 | [查看](https://time.geekbang.org/column/article/67775) |
| 5 | 36 \| 为什么说Kubernetes只有soft multi-tenancy？ | 14:49 | [查看](https://time.geekbang.org/column/article/68316) |
| 6 | 37 \| 找到容器不容易：Service、DNS与服务发现 | 10:57 | [查看](https://time.geekbang.org/column/article/68636) |
| 7 | 38 \| 从外界连通Service与Service调试"三板斧" | 11:04 | [查看](https://time.geekbang.org/column/article/68964) |
| 8 | 39 \| 谈谈Service与Ingress | 10:09 | [查看](https://time.geekbang.org/column/article/69214) |

## Kubernetes作业调度与资源管理 (5讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 40 \| Kubernetes的资源模型与资源管理 | 10:26 | [查看](https://time.geekbang.org/column/article/69678) |
| 2 | 41 \| 十字路口上的Kubernetes默认调度器 | 09:52 | [查看](https://time.geekbang.org/column/article/69890) |
| 3 | 42 \| Kubernetes默认调度器调度策略解析 | 11:04 | [查看](https://time.geekbang.org/column/article/70211) |
| 4 | 43 \| Kubernetes默认调度器的优先级与抢占机制 | 11:18 | [查看](https://time.geekbang.org/column/article/70519) |
| 5 | 44 \| Kubernetes GPU管理与Device Plugin机制 | 10:53 | [查看](https://time.geekbang.org/column/article/70876) |

## Kubernetes容器运行时 (3讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 45 \| 幕后英雄：SIG-Node与CRI | 09:25 | [查看](https://time.geekbang.org/column/article/71056) |
| 2 | 46 \| 解读 CRI 与 容器运行时 | 08:36 | [查看](https://time.geekbang.org/column/article/71499) |
| 3 | 47 \| 绝不仅仅是安全：Kata Containers 与 gVisor | 10:39 | [查看](https://time.geekbang.org/column/article/71606) |

## Kubernetes容器监控与日志 (3讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 48 \| Prometheus、Metrics Server与Kubernetes监控体系 | 09:25 | [查看](https://time.geekbang.org/column/article/72281) |
| 2 | 49 \| Custom Metrics: 让Auto Scaling不再"食之无味" | 07:51 | [查看](https://time.geekbang.org/column/article/72693) |
| 3 | 50 \| 让日志无处可逃：容器日志收集与管理 | 07:15 | [查看](https://time.geekbang.org/column/article/73156) |

## 再谈开源与社区 (1讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 51 \| 谈谈Kubernetes开源社区和未来走向 | 09:30 | [查看](https://time.geekbang.org/column/article/73477) |

## 答疑文章 (1讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 52 \| 答疑：在问题中解决问题，在思考中产生思考 | 13:14 | [查看](https://time.geekbang.org/column/article/73790) |

## 特别放送 (2讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 特别放送 \| 2019 年，容器技术生态会发生些什么？ | 07:54 | [查看](https://time.geekbang.org/column/article/83596) |
| 2 | 特别放送 \| 基于 Kubernetes 的云原生应用管理，到底应该怎么做？ | 10:28 | [查看](https://time.geekbang.org/column/article/114197) |

## 结束语 (1讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 结束语 \| Kubernetes：赢开发者赢天下 | 05:09 | [查看](https://time.geekbang.org/column/article/74278) |

## 结课测试 (1讲)

| # | 课程标题 | 时长 | 链接 |
|---|---------|------|------|
| 1 | 结课测试 \| 这些Kubernetes的相关知识，你都掌握了吗？ | - | [查看](https://time.geekbang.org/column/article/224358) |

---

## 学习建议

1. **基础阶段**：课前必读 + 容器技术概念入门篇（了解容器和K8s的发展历程，掌握容器基础知识）
2. **实践阶段**：Kubernetes集群搭建与实践（动手搭建集群，运行第一个应用）
3. **核心概念**：容器编排与Kubernetes作业管理（理解Pod、控制器、声明式API等核心概念）
4. **进阶主题**：容器持久化存储 + 容器网络（深入理解K8s的存储和网络机制）
5. **调度与运行时**：作业调度与资源管理 + 容器运行时（掌握调度机制和运行时原理）
6. **运维监控**：容器监控与日志（了解监控和日志收集方案）

总学习时长约 **12小时**
