# Infrastructure Engineering Interview Questions - Import Preview (Updated)

> **总数**: 9 道题目
> **来源**: Infrastructure Engineering Interview Q&A Guide.md
> **目标**: 作为公共试题导入到 Growing 系统
> **更新日期**: 2025-12-21

---

## 📊 Focus Area 映射关系

基于当前数据库中的 Focus Areas，以下是建议的映射关系：

| 问题类别 | 题目数量 | 映射到 Skill | 映射到 Focus Area |
|---------|---------|-------------|------------------|
| Behavioral/Team Fit | 3 | Behavioral技能 (ID: 3) | Customer Centricity (ID: 3) |
| Cloud Knowledge - Kubernetes | 2 | 云计算 (ID: 4) | Containerization & Kubernetes (ID: 1) |
| Linux Kernel & OS Internals | 1 | 云计算 (ID: 4) | Linux Kernel & OS Internals (ID: 8) |
| System Design | 1 | 系统设计 (ID: 2) | Systems Design & Architecture (ID: 20) |
| Coding & Algorithms | 1 | 编程和数据结构 (ID: 1) | Programming Proficiency (ID: 15) |

### 详细映射说明

#### 1. Behavioral/Team Fit → Customer Centricity (Focus Area ID: 3)
- **Skill**: Behavioral技能 (ID: 3)
- **可用 Focus Areas**:
  - [2] Continuous Learning
  - [3] Customer Centricity ✅ **推荐**
  - [19] Strategy
  - [22] Transparency/Stakeholder Management
  - [24] Values & Team Fit
  - [25] Values Alignment
  - [26] Vision
  - [27] Adaptability
  - [30] Clarity
  - [31] Collaboration
  - [32] Communication
  - [33] Conflict Resolution

**选择理由**: "Customer Centricity" 最符合这些 behavioral 问题的核心主题（Customer Focus, Action Oriented, Organizational Savvy）

#### 2. Cloud Knowledge - Kubernetes → Containerization & Kubernetes (Focus Area ID: 1)
- **Skill**: 云计算 (ID: 4)
- **可用 Focus Areas**:
  - [1] Containerization & Kubernetes ✅ **推荐**
  - [7] Kernel Engineering
  - [8] Linux Kernel & OS Internals
  - [9] Networking & Security
  - [21] Technical Depth
  - [23] Troubleshooting
  - [29] BPF & Networking
  - [34] Containerization & Runtimes

**选择理由**: 完全匹配 Kubernetes 主题

#### 3. Linux Kernel & OS Internals → Linux Kernel & OS Internals (Focus Area ID: 8)
- **Skill**: 云计算 (ID: 4)
- **Focus Area**: Linux Kernel & OS Internals (ID: 8) ✅ **完美匹配**

**选择理由**: 名称完全匹配

#### 4. System Design → Systems Design & Architecture (Focus Area ID: 20)
- **Skill**: 系统设计 (ID: 2)
- **可用 Focus Areas**:
  - [12] Open Source Contributions
  - [16] Problem-Solving
  - [17] Security & Compliance Awareness
  - [18] Security Engineering
  - [20] Systems Design & Architecture ✅ **推荐**

**选择理由**: 完全匹配系统设计主题

#### 5. Coding & Algorithms → Programming Proficiency (Focus Area ID: 15)
- **Skill**: 编程和数据结构 (ID: 1)
- **可用 Focus Areas**:
  - [5] Debugging & Troubleshooting
  - [14] Programming & Tooling
  - [15] Programming Proficiency ✅ **推荐**

**选择理由**: 涵盖编程能力和算法实现

---

## 📝 详细题目列表（带 Focus Area ID）

### 1️⃣ Customer Focus (Behavioral)

**Focus Area ID**: 3 (Customer Centricity)

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

**答案要求摘要**:
- Proactively engaged with users
- Created prototypes/MVPs for feedback
- Collaborated with stakeholders
- Adjusted based on feedback
- Measured success through metrics

**Red Flags** (5 条)

---

### 2️⃣ Action Oriented (Behavioral)

**Focus Area ID**: 3 (Customer Centricity)

**题目**:
```
Sometimes people delay taking action on something. Describe a time when you saw
other people in the organization who were not acting and you took it upon yourself
to lead the effort.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求摘要**:
- Identified gap/opportunity
- Took initiative
- Rallied others
- Demonstrated ownership
- Showed measurable impact

**Red Flags** (5 条)

---

### 3️⃣ Organizational Savvy (Behavioral)

**Focus Area ID**: 3 (Customer Centricity)

**题目**:
```
Tell me about a team or work situation with diverse stakeholders (e.g., across
orgs, different roles). How did you navigate through it? What steps did you take
to ensure the team was able to act effectively?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Staff

**答案要求摘要**:
- Recognized stakeholder priorities
- Built alignment through listening
- Established clear communication
- Balanced technical and business
- Created win-win outcomes

**Red Flags** (5 条)

---

### 4️⃣ Kubernetes Core Components

**Focus Area ID**: 1 (Containerization & Kubernetes)

**题目**:
```
List Kubernetes key components in master node and worker node, and explain their
responsibilities.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Mid

**答案要求摘要**:
- Master: apiserver, etcd, scheduler, controller-manager
- Worker: kubelet, kube-proxy, container runtime
- Communication patterns
- Reconciliation loops
- High availability

**Red Flags** (5 条)

---

### 5️⃣ Kubernetes Networking

**Focus Area ID**: 1 (Containerization & Kubernetes)

**题目**:
```
Describe the networking model in Kubernetes. How do Pods communicate with each other?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求摘要**:
- Flat network model (no NAT)
- Pod-to-Pod: same node (bridge) vs cross-node (CNI)
- CNI plugins: Calico, Cilium, Flannel
- Service Discovery: Services, kube-proxy, DNS
- Network Policies

**Red Flags** (5 条)

---

### 6️⃣ Cgroups and Namespaces

**Focus Area ID**: 8 (Linux Kernel & OS Internals)

**题目**:
```
What are cgroups and namespaces, and how do they contribute to containerization
in Linux?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求摘要**:
- Namespaces: PID, Network, Mount, UTS, IPC, User
- Cgroups: CPU, Memory, blkio, devices
- How they enable containers
- Container runtime integration
- Security implications

**Red Flags** (5 条)

---

### 7️⃣ Distributed Cache System Design

**Focus Area ID**: 20 (Systems Design & Architecture)

**题目**:
```
How would you design a distributed cache system?
```

**难度**: HARD
**目标职位**: Infrastructure Engineer
**目标级别**: Staff

**答案要求摘要**:
- Requirements gathering (functional + non-functional)
- Consistent hashing
- Data replication (CAP theorem)
- Cache eviction (LRU/LFU/TTL)
- High availability (Sentinel, health checks)
- Monitoring (hit rate, latency)

**Red Flags** (6 条)

---

### 8️⃣ LRU Cache Implementation

**Focus Area ID**: 15 (Programming Proficiency)

**题目**:
```
Implement an LRU (Least Recently Used) Cache with O(1) time complexity for both
get and put operations.
```

**难度**: MEDIUM
**目标职位**: Infrastructure Engineer
**目标级别**: Senior

**答案要求摘要**:
- Data structure: HashMap + Doubly Linked List
- Why doubly linked list (O(1) operations)
- Implementation with dummy nodes
- O(1) time complexity
- Production considerations (thread safety, TTL)

**Red Flags** (5 条)

---

## ✅ 映射总结

| Focus Area ID | Focus Area Name | 题目数量 |
|--------------|----------------|---------|
| 3 | Customer Centricity | 3 题 (Behavioral) |
| 1 | Containerization & Kubernetes | 2 题 (K8s) |
| 8 | Linux Kernel & OS Internals | 1 题 (Cgroups/Namespaces) |
| 20 | Systems Design & Architecture | 1 题 (Distributed Cache) |
| 15 | Programming Proficiency | 1 题 (LRU Cache) |

---

## 🚀 导入步骤

### 1. 确认映射关系
请检查上述映射是否合理。如果需要调整，可以：
- 将某些题目映射到其他 Focus Area
- 或创建新的 Focus Area

### 2. 运行导入脚本

导入脚本已更新 Focus Area 映射：

```python
FOCUS_AREA_MAPPING = {
    "Behavioral/Team Fit": 3,           # Customer Centricity
    "Cloud Knowledge - Kubernetes": 1,   # Containerization & Kubernetes
    "Linux Kernel & OS Internals": 8,   # Linux Kernel & OS Internals
    "System Design": 20,                # Systems Design & Architecture
    "Coding & Algorithms": 15           # Programming Proficiency
}
```

运行命令：
```bash
cd /Users/yanzxu/claude/growing/import
python3 import_questions.py
```

### 3. 验证导入

登录系统检查：
1. 学习 → 我的试题库
2. 选择对应的 Skill 和 Focus Area
3. 确认试题已正确导入
4. 检查 Red Flags 和答案要求是否完整

---

## 🤔 其他建议

### 可选映射调整

如果你觉得某些映射不合适，以下是备选方案：

1. **Behavioral 问题** 也可以映射到：
   - Transparency/Stakeholder Management (ID: 22) - 适合 Organizational Savvy
   - Collaboration (ID: 31) - 适合 Action Oriented
   - Communication (ID: 32) - 适合所有 Behavioral 问题

2. **System Design** 也可以映射到：
   - Problem-Solving (ID: 16) - 如果更侧重解决问题的思路

3. **LRU Cache** 也可以映射到：
   - Debugging & Troubleshooting (ID: 5) - 如果更侧重调试技能
   - Programming & Tooling (ID: 14) - 如果更侧重编程工具

---

**准备好导入了吗？**

✅ 已检查 Focus Area 映射
✅ 后端 (http://localhost:8080) 和前端 (http://localhost:3001) 正在运行
✅ 已登录管理员账号 (austinxu)
✅ 确认将这些问题作为公共试题导入 (`is_official=true`)

确认后运行导入脚本！
