# HelloInterview 系统设计学习路径

> 来源：hellointerview.com
> 整理日期：2025-12-25

## 一、核心概念 (Core Concepts)

**对应Major Category**: 核心概念 (category_id=13)

### Focus Area 列表

#### 1. Networking Essentials (网络基础)
- **URL**: `/learn/system-design/core-concepts/networking-essentials`
- **关键内容**: 网络协议、HTTP/HTTPS、TCP/IP基础
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐⭐ (系统设计基础)

#### 2. API Design (API设计)
- **URL**: `/learn/system-design/core-concepts/api-design`
- **关键内容**: RESTful API、GraphQL、API最佳实践
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐⭐

#### 3. Data Modeling (数据建模)
- **URL**: `/learn/system-design/core-concepts/data-modeling`
- **关键内容**: 关系型vs非关系型、schema设计、数据范式
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐⭐

#### 4. Database Indexing (数据库索引)
- **URL**: `/learn/system-design/core-concepts/db-indexing`
- **关键内容**: B-tree索引、复合索引、查询优化
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐⭐

#### 5. Caching (缓存)
- **URL**: `/learn/system-design/core-concepts/caching`
- **关键内容**: 缓存策略(LRU/LFU)、缓存失效、缓存层次
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐⭐

#### 6. Sharding (分片)
- **URL**: `/learn/system-design/core-concepts/sharding`
- **关键内容**: 水平分片、垂直分片、分片键选择
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐

#### 7. Consistent Hashing (一致性哈希)
- **URL**: `/learn/system-design/core-concepts/consistent-hashing`
- **关键内容**: 虚拟节点、负载均衡、动态扩缩容
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐

#### 8. CAP Theorem (CAP定理)
- **URL**: `/learn/system-design/core-concepts/cap-theorem`
- **关键内容**: 一致性、可用性、分区容错性权衡
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐⭐

#### 9. Numbers to Know (必知数据)
- **URL**: `/learn/system-design/core-concepts/numbers-to-know`
- **关键内容**: 延迟数字、吞吐量、容量规划
- **学习资源类型**: 文章
- **重要性**: ⭐⭐⭐⭐

---

## 二、关键技术 (Key Technologies)

**对应Major Category**: 关键技术 (category_id=14)

### Focus Area 列表

#### 1. Redis
- **URL**: `/learn/system-design/deep-dives/redis`
- **关键内容**: 内存数据库、持久化、主从复制、集群
- **学习资源类型**: 深度文章
- **应用场景**: 缓存、会话存储、排行榜、消息队列
- **重要性**: ⭐⭐⭐⭐⭐

#### 2. Elasticsearch
- **URL**: `/learn/system-design/deep-dives/elasticsearch`
- **关键内容**: 全文搜索、倒排索引、分布式搜索
- **学习资源类型**: 深度文章
- **应用场景**: 搜索引擎、日志分析、实时分析
- **重要性**: ⭐⭐⭐⭐

#### 3. Kafka
- **URL**: `/learn/system-design/deep-dives/kafka`
- **关键内容**: 消息队列、发布订阅、流处理
- **学习资源类型**: 深度文章
- **应用场景**: 事件驱动、日志收集、数据管道
- **重要性**: ⭐⭐⭐⭐⭐

#### 4. API Gateway (API网关)
- **URL**: `/learn/system-design/deep-dives/api-gateway`
- **关键内容**: 路由、限流、鉴权、负载均衡
- **学习资源类型**: 深度文章
- **应用场景**: 微服务架构、统一入口
- **重要性**: ⭐⭐⭐⭐

#### 5. Cassandra
- **URL**: `/learn/system-design/deep-dives/cassandra`
- **关键内容**: 分布式NoSQL、最终一致性、宽列存储
- **学习资源类型**: 深度文章
- **应用场景**: 时间序列数据、海量写入
- **重要性**: ⭐⭐⭐⭐

#### 6. DynamoDB
- **URL**: `/learn/system-design/deep-dives/dynamodb`
- **关键内容**: AWS NoSQL、自动扩展、分区键设计
- **学习资源类型**: 深度文章
- **应用场景**: 无服务器应用、高并发读写
- **重要性**: ⭐⭐⭐⭐

#### 7. PostgreSQL
- **URL**: `/learn/system-design/deep-dives/postgres`
- **关键内容**: 关系型数据库、ACID、事务、扩展性
- **学习资源类型**: 深度文章
- **应用场景**: 事务型应用、复杂查询
- **重要性**: ⭐⭐⭐⭐⭐

#### 8. Flink
- **URL**: `/learn/system-design/deep-dives/flink`
- **关键内容**: 流处理、状态管理、窗口操作
- **学习资源类型**: 深度文章
- **应用场景**: 实时计算、事件处理
- **重要性**: ⭐⭐⭐

#### 9. ZooKeeper
- **URL**: `/learn/system-design/deep-dives/zookeeper`
- **关键内容**: 分布式协调、配置管理、服务发现
- **学习资源类型**: 深度文章
- **应用场景**: 分布式锁、主节点选举
- **重要性**: ⭐⭐⭐⭐

---

## 三、设计模式 (Patterns)

**对应Major Category**: 设计模式 (category_id=15)

### Focus Area 列表

#### 1. Real-time Updates (实时更新)
- **URL**: `/learn/system-design/patterns/realtime-updates`
- **关键内容**: WebSocket、Server-Sent Events、长轮询
- **学习资源类型**: 文章
- **应用场景**: 聊天应用、实时通知、协作编辑
- **重要性**: ⭐⭐⭐⭐

#### 2. Dealing with Contention (处理竞争)
- **URL**: `/learn/system-design/patterns/dealing-with-contention`
- **关键内容**: 乐观锁、悲观锁、分布式锁
- **学习资源类型**: 文章
- **应用场景**: 高并发写入、库存扣减
- **重要性**: ⭐⭐⭐⭐⭐

#### 3. Multi-step Processes (多步骤流程)
- **URL**: `/learn/system-design/patterns/multi-step-processes`
- **关键内容**: Saga模式、事务补偿、工作流引擎
- **学习资源类型**: 文章
- **应用场景**: 订单处理、支付流程
- **重要性**: ⭐⭐⭐⭐

#### 4. Scaling Reads (读扩展)
- **URL**: `/learn/system-design/patterns/scaling-reads`
- **关键内容**: 读写分离、副本、缓存策略
- **学习资源类型**: 文章
- **应用场景**: 读多写少场景、内容分发
- **重要性**: ⭐⭐⭐⭐⭐

#### 5. Scaling Writes (写扩展)
- **URL**: `/learn/system-design/patterns/scaling-writes`
- **关键内容**: 分片、队列、批量处理
- **学习资源类型**: 文章
- **应用场景**: 高并发写入、日志系统
- **重要性**: ⭐⭐⭐⭐

#### 6. Handling Large Blobs (处理大文件)
- **URL**: `/learn/system-design/patterns/large-blobs`
- **关键内容**: 对象存储、分块上传、CDN
- **学习资源类型**: 文章
- **应用场景**: 文件上传、视频存储
- **重要性**: ⭐⭐⭐⭐

#### 7. Managing Long Running Tasks (管理长任务)
- **URL**: `/learn/system-design/patterns/long-running-tasks`
- **关键内容**: 异步处理、任务队列、状态机
- **学习资源类型**: 文章
- **应用场景**: 报表生成、数据导入
- **重要性**: ⭐⭐⭐⭐

---

## 四、实战案例 (Question Breakdowns)

> 以下是HelloInterview提供的系统设计面试真题解析，可作为实践学习资源

### 经典题目列表

1. **Design Bit.ly** - 短链接服务
2. **Design Dropbox** - 文件同步存储
3. **Design Ticketmaster** - 票务系统
4. **Design FB News Feed** - 社交动态流
5. **Design Tinder** - 匹配推荐系统
6. **Design LeetCode** - 在线编程平台
7. **Design WhatsApp** - 即时通讯
8. **Design Yelp** - 位置服务
9. **Design Uber** - 实时定位调度
10. **Design Instagram** - 图片社交平台
11. **Design YouTube** - 视频流媒体
12. **Design Google Docs** - 协作文档编辑
13. **Design a Rate Limiter** - 限流系统
14. **Design a Distributed Cache** - 分布式缓存
15. **Design a Payment System** - 支付系统

---

## 五、导入建议

### 5.1 Focus Area 创建优先级

**高优先级** (建议先创建):
1. 核心概念分类下的9个Focus Area
2. 关键技术分类下的Redis、Kafka、PostgreSQL
3. 设计模式分类下的Real-time Updates、Dealing with Contention、Scaling Reads/Writes

**中优先级**:
1. 其他关键技术Focus Area
2. 其他设计模式Focus Area

### 5.2 学习内容提取策略

每个Focus Area对应的学习内容可以使用以下格式：

```
内容标题: [Focus Area名称]
内容类型: article (文章) 或 video (视频)
URL: https://hellointerview.com[原始URL]
作者: Hello Interview Team
描述: [从页面标题提取]
可见性: public (公开)
```

### 5.3 数据库导入SQL示例

```sql
-- 示例：为"核心概念"分类创建Focus Area
INSERT INTO focus_areas (name, description, category_id, skill_id, sort_order) VALUES
('Networking Essentials', '网络基础知识，包括HTTP/HTTPS、TCP/IP等协议', 13, 2, 1),
('API Design', 'RESTful API、GraphQL设计最佳实践', 13, 2, 2),
('Caching', '缓存策略、缓存失效和缓存层次', 13, 2, 3);

-- 为每个Focus Area添加学习内容
INSERT INTO learning_contents (focus_area_id, stage_id, title, content_type, url, author, description, visibility, sort_order) VALUES
(1, 1, 'Networking Essentials', 'article', 'https://hellointerview.com/learn/system-design/core-concepts/networking-essentials', 'Hello Interview Team', '系统设计必备：网络基础知识', 'public', 1);
```

---

## 六、补充说明

### 6.1 快速入门部分 (In a Hurry)

HelloInterview还提供了"快速入门"系列，适合时间紧张的学习者：

1. Introduction - 系统设计介绍
2. How to Prepare - 如何准备面试
3. Delivery Framework - 面试框架
4. Core Concepts - 核心概念速览
5. Key Technologies - 关键技术速览
6. Common Patterns - 常见模式速览

**建议**: 可作为"系统设计入门"单独的Focus Area

### 6.2 高级主题 (Advanced Topics)

1. Data Structures for Big Data - 大数据数据结构
2. Time Series Databases - 时序数据库

**建议**: 可作为进阶学习内容

---

## 七、总结

**总计**:
- 核心概念: 9个Focus Area
- 关键技术: 9个Focus Area
- 设计模式: 7个Focus Area
- **总计: 25个Focus Area + 15个实战案例**

**建议学习路径**:
1. 先学习核心概念（打基础）
2. 再学习关键技术（工具箱）
3. 最后学习设计模式（解决方案）
4. 结合实战案例进行练习

**预计学习时间**: 8-12周（按每周5-10小时计算）
