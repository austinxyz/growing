# Infrastructure Engineering Interview Questions - Import Preview

> **总数**: 9 道题目
> **来源**: Infrastructure Engineering Interview Q&A Guide.md
> **目标**: 作为公共试题导入到 Growing 系统

---

## 导入计划

### 按类别分布

| 类别 | 题目数量 | 对应 Focus Area | 难度分布 |
|------|---------|----------------|---------|
| Behavioral/Team Fit | 3 | Behavior | MEDIUM (2), HARD (1) |
| Cloud Knowledge - Kubernetes | 2 | Kubernetes | MEDIUM (1), HARD (1) |
| Linux Kernel & OS Internals | 1 | Operating Systems | HARD (1) |
| System Design | 1 | System Design | HARD (1) |
| Coding & Algorithms | 1 | Data Structures & Algorithms | MEDIUM (1) |

### 按难度分布

- **EASY**: 0 题
- **MEDIUM**: 4 题
- **HARD**: 5 题

---

## 详细题目列表

### 1️⃣ Behavioral/Team Fit - Customer Focus

**题目**:
```
Describe a time when you didn't have all the information in order to understand
the exact nature of a user's issue or problem when developing a feature. How did
that affect how you developed the feature? What steps (if any) did you take to
understand the user's problem better?
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求要点**:
- Proactively engaged with users through surveys, interviews, or feedback sessions
- Created prototypes or MVPs to gather early feedback
- Collaborated with product managers, UX designers, or customer support teams
- Adjusted approach based on user feedback rather than assumptions
- Demonstrated empathy and user-centric thinking
- Measured success through user satisfaction metrics

**Red Flags** (5 条):
- Made assumptions without validation
- Didn't seek input from stakeholders
- Focused only on technical solution without understanding user needs
- No follow-up or iteration after initial implementation
- Blamed users for not being clear

---

### 2️⃣ Behavioral/Team Fit - Action Oriented

**题目**:
```
Sometimes people delay taking action on something. Describe a time when you saw
other people in the organization who were not acting and you took it upon yourself
to lead the effort.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求要点**:
- Identified a clear gap or opportunity
- Took initiative without being asked
- Rallied others around a shared goal
- Demonstrated ownership and accountability
- Showed results or measurable impact
- Balanced urgency with thoughtful planning

**Red Flags** (5 条):
- Acted without considering team input
- Steamrolled others' concerns
- No clear problem definition
- Lack of follow-through
- Blame-focused narrative

---

### 3️⃣ Behavioral/Team Fit - Organizational Savvy

**题目**:
```
Tell me about a team or work situation with diverse stakeholders (e.g., across
orgs, different roles). How did you navigate through it? What steps did you take
to ensure the team was able to act effectively?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Staff

**答案要求要点**:
- Recognized different stakeholder priorities and constraints
- Built alignment through active listening and compromise
- Established clear communication channels and decision-making processes
- Balanced technical and business considerations
- Demonstrated political awareness without being manipulative
- Created win-win outcomes

**Red Flags** (5 条):
- Ignored stakeholder concerns
- Forced decisions without buy-in
- No understanding of organizational dynamics
- Failed to communicate effectively across teams
- Created adversarial relationships

---

### 4️⃣ Cloud Knowledge - Kubernetes - Core Components

**题目**:
```
List Kubernetes key components in master node and worker node, and explain their
responsibilities.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Mid

**答案要求要点**:
- Master Node: kube-apiserver, etcd, kube-scheduler, kube-controller-manager, cloud-controller-manager
- Worker Node: kubelet, kube-proxy, Container Runtime
- Explain communication patterns (all components talk through apiserver)
- Understand watch mechanism and reconciliation loops
- Know high availability considerations

**Red Flags** (5 条):
- Confuses control plane and worker node components
- Can't explain how components interact
- Doesn't understand kubelet vs kube-proxy responsibilities
- No awareness of etcd's critical role
- Can't describe what happens when a Pod is created end-to-end

---

### 5️⃣ Cloud Knowledge - Kubernetes - Networking

**题目**:
```
Describe the networking model in Kubernetes. How do Pods communicate with each other?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求要点**:
- Core Networking Requirements: All Pods can communicate without NAT
- Pod-to-Pod Communication: Within same node (bridge) and across nodes (CNI)
- CNI plugins: Calico, Cilium, Flannel, Weave
- Service Discovery & Load Balancing: Services, kube-proxy, DNS, Endpoints
- Network Policies for security
- Service types: ClusterIP, NodePort, LoadBalancer, ExternalName

**Red Flags** (5 条):
- Thinks Pods use Docker networking directly
- Doesn't understand how cross-node communication works
- No knowledge of CNI plugins
- Can't explain kube-proxy's role
- No awareness of Network Policies

---

### 6️⃣ Linux Kernel & OS Internals - Cgroups and Namespaces

**题目**:
```
What are cgroups and namespaces, and how do they contribute to containerization
in Linux?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求要点**:
- **Namespaces** (Isolation): PID, Network, Mount, UTS, IPC, User
- **Cgroups** (Resource Limits): CPU, Memory, blkio, net_cls/net_prio, devices
- How they enable containers: Namespaces for isolation, Cgroups for resource limits
- Container Runtime Integration: Docker/containerd uses runc (OCI runtime)
- Security implications: user namespaces, device cgroups

**Red Flags** (5 条):
- Confuses namespaces with cgroups (thinks they're the same)
- Can't name specific namespace types
- No understanding of resource limiting vs isolation
- Doesn't know how container runtimes use these features
- No awareness of security boundaries

---

### 7️⃣ System Design - Distributed Cache System

**题目**:
```
How would you design a distributed cache system?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Staff

**答案要求要点**:
- **Requirements Gathering**: Functional (Get/Put, TTL, eviction) and Non-functional (latency, availability, scale)
- **Consistent Hashing**: Minimize rehashing when nodes change
- **Data Replication**: Master-Slave, replication factor, CAP theorem
- **Cache Eviction**: LRU, LFU, TTL-based
- **High Availability**: Sentinel, health checks, circuit breaker, graceful degradation
- **Scalability**: Horizontal scaling, sharding, connection pooling, pipelining
- **Cache Stampede Prevention**: Lock/Lease mechanism
- **Monitoring**: Hit rate, miss rate, latency, memory usage

**Red Flags** (6 条):
- Jumps straight to implementation without requirements
- Doesn't understand consistent hashing
- No mention of replication or availability
- Ignores cache eviction strategies
- No discussion of monitoring or failure handling
- Doesn't consider scalability limits

---

### 8️⃣ Coding & Algorithms - LRU Cache Implementation

**题目**:
```
Implement an LRU (Least Recently Used) Cache with O(1) time complexity for both
get and put operations.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求要点**:
- **Data Structure**: HashMap + Doubly Linked List
- **Why Doubly Linked List**: O(1) move to head, O(1) remove tail, need prev pointer
- **Implementation**: Dummy head/tail, _remove(), _add_to_head(), get(), put()
- **Time Complexity**: O(1) for both get and put
- **Space Complexity**: O(capacity)
- **Production Considerations**: Thread safety, TTL support, size-based eviction

**Red Flags** (5 条):
- Uses only HashMap or only LinkedList (can't achieve O(1) for both)
- Complexity higher than O(1) for operations
- Doesn't handle edge cases (capacity = 0, duplicate keys)
- Can't explain why doubly linked list is used
- No awareness of thread safety issues

---

### 9️⃣ Coding & Algorithms - LRU Cache Implementation

**题目**:
```
Implement an LRU (Least Recently Used) Cache with O(1) time complexity for both
get and put operations.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求要点**:
- Same as above (duplicate entry in script)

**Red Flags** (5 条):
- Same as above

---

## 导入前需要确认的事项

### ⚠️ Focus Area 映射

导入脚本中使用的 Focus Area 映射为占位符，需要根据你的实际数据库调整：

```python
CATEGORY_TO_FOCUS_AREA = {
    "Behavioral/Team Fit": "Behavior",
    "Cloud Knowledge - Kubernetes": "Kubernetes",
    "Linux Kernel & OS Internals": "Operating Systems",
    "System Design": "System Design",
    "Coding & Algorithms": "Data Structures & Algorithms",
    "Security Engineering": "Security",
    "Networking": "Computer Networks",
    "Observability & Troubleshooting": "Monitoring & Observability"
}
```

**建议操作**:
1. 先登录系统查看现有的 Skill 和 Focus Area
2. 如果没有对应的 Focus Area，可以先创建
3. 或者，将问题导入到一个通用的 Focus Area（如"面试准备"）

### ✅ 导入配置

- **API Endpoint**: `http://localhost:8080/api/admin/questions`
- **认证方式**: JWT Token (通过 austinxu/helloworld 登录)
- **试题属性**: `is_official=true` (作为公共试题)
- **创建者**: `created_by_user_id=null` (管理员创建)

### 📊 数据完整性

所有问题都包含:
- ✅ 问题描述 (questionText)
- ✅ 难度级别 (difficulty)
- ✅ 答案要求 (answerRequirement)
- ✅ Red Flags 列表
- ✅ 目标职位 (targetPosition)
- ✅ 目标级别 (targetLevel)

---

## 导入步骤

1. **确认 Focus Area 存在**
   - 登录系统 → 设置 → 技能管理
   - 检查是否有对应的 Focus Area
   - 如果没有，先创建所需的 Skill 和 Focus Area

2. **更新导入脚本**（可选）
   - 如果 Focus Area 名称或映射关系不同，更新 `import_questions.py`
   - 或者先导入到一个通用的 Focus Area ID

3. **运行导入脚本**
   ```bash
   cd /Users/yanzxu/claude/growing/import
   python3 import_questions.py
   ```

4. **验证导入结果**
   - 登录系统 → 设置 → 试题管理
   - 检查导入的试题是否正确
   - 验证 Focus Area 关联是否正确

---

## 后续扩展

从原始文件中还有更多问题可以导入：

- **Security Engineering** (22 questions)
- **Networking** (10 questions)
- **Observability & Troubleshooting** (8 questions)
- **More Behavioral questions** (12 questions)
- **More Kubernetes questions** (23 questions)
- **More Linux/Kernel questions** (17 questions)
- **More System Design questions** (11 questions)
- **More Coding questions** (7 questions)

**总计可导入**: 100+ questions

---

**准备好导入了吗？请确认：**

1. ✅ 已经检查了 Focus Area 映射
2. ✅ 后端和前端服务都在运行
3. ✅ 已经登录并有管理员权限
4. ✅ 确认要将这些问题作为公共试题导入

确认后，运行导入脚本即可开始！
