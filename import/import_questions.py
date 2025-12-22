#!/usr/bin/env python3
"""
Import Infrastructure Engineering Interview Questions to MySQL database
"""

import json
import requests
import sys

# Configuration
API_BASE_URL = "http://localhost:8080/api/admin/questions"
AUTH_TOKEN = ""  # Will be set via command line or environment

# Mapping categories to focus area IDs (from database)
FOCUS_AREA_MAPPING = {
    "Behavioral/Team Fit": 3,           # Customer Centricity
    "Cloud Knowledge - Kubernetes": 1,   # Containerization & Kubernetes
    "Linux Kernel & OS Internals": 8,   # Linux Kernel & OS Internals
    "System Design": 20,                # Systems Design & Architecture
    "Coding & Algorithms": 15           # Programming Proficiency
}

# Questions to import
QUESTIONS = [
    # 1. Behavioral/Team Fit Questions
    {
        "category": "Behavioral/Team Fit",
        "questionText": """## Customer Focus

Describe a time when you didn't have all the information in order to understand the exact nature of a user's issue or problem when developing a feature. How did that affect how you developed the feature? What steps (if any) did you take to understand the user's problem better?""",
        "difficulty": "MEDIUM",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Senior",
        "answerRequirement": """**Strong Answer Should Include:**
- Proactively engaged with users through surveys, interviews, or feedback sessions
- Created prototypes or MVPs to gather early feedback
- Collaborated with product managers, UX designers, or customer support teams
- Adjusted approach based on user feedback rather than assumptions
- Demonstrated empathy and user-centric thinking
- Measured success through user satisfaction metrics

**Example Elements:**
1. Set up regular calls with pilot teams to understand pain points
2. Created lightweight dashboards or tools to gather user feedback
3. Discovered that the real problem was different from initial requirements
4. Pivoted based on direct user engagement
5. Measured results with concrete metrics (e.g., 40% reduction in user-reported issues)

**Key Learning:** Direct user engagement reveals the real problem, not just surface-level symptoms""",
        "redFlags": [
            "Made assumptions without validation",
            "Didn't seek input from stakeholders",
            "Focused only on technical solution without understanding user needs",
            "No follow-up or iteration after initial implementation",
            "Blamed users for not being clear"
        ]
    },
    {
        "category": "Behavioral/Team Fit",
        "questionText": """## Action Oriented

Sometimes people delay taking action on something. Describe a time when you saw other people in the organization who were not acting and you took it upon yourself to lead the effort.""",
        "difficulty": "MEDIUM",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Senior",
        "answerRequirement": """**Strong Answer Should Include:**
- Identified a clear gap or opportunity
- Took initiative without being asked
- Rallied others around a shared goal
- Demonstrated ownership and accountability
- Showed results or measurable impact
- Balanced urgency with thoughtful planning

**Example Elements:**
1. Created a proof-of-concept to demonstrate value (e.g., 2-week POC)
2. Documented migration steps, rollback procedures, and metrics
3. Presented to team with a phased plan to reduce risk
4. Volunteered to be on-call for first migration to build confidence
5. Achieved measurable results (e.g., migrated 80% of services in 6 months with zero incidents)

**Key Learning:** Show concrete value quickly, reduce perceived risk, and lead by example""",
        "redFlags": [
            "Acted without considering team input",
            "Steamrolled others' concerns",
            "No clear problem definition",
            "Lack of follow-through",
            "Blame-focused narrative"
        ]
    },
    {
        "category": "Behavioral/Team Fit",
        "questionText": """## Organizational Savvy

Tell me about a team or work situation with diverse stakeholders (e.g., across orgs, different roles). How did you navigate through it? What steps did you take to ensure the team was able to act effectively?""",
        "difficulty": "HARD",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Staff",
        "answerRequirement": """**Strong Answer Should Include:**
- Recognized different stakeholder priorities and constraints
- Built alignment through active listening and compromise
- Established clear communication channels and decision-making processes
- Balanced technical and business considerations
- Demonstrated political awareness without being manipulative
- Created win-win outcomes

**Example Elements:**
1. **Listening tour**: 1-on-1s with each stakeholder to understand top concerns
2. **Shared documentation**: Decision matrix weighing all concerns transparently
3. **Pilot with skeptics**: Chose most concerned team for pilot to get early feedback
4. **Phased rollout**: Started with 10%, then 30%, then 100% - gave teams time to adapt
5. **Weekly updates**: Sent metrics to show value

**Example Scenario:** OS upgrade across 5000+ servers requiring alignment from app teams (downtime concerns), security (faster patching), finance (budget), and SRE (operational burden)

**Key Learning:** Make everyone feel heard and show incremental value""",
        "redFlags": [
            "Ignored stakeholder concerns",
            "Forced decisions without buy-in",
            "No understanding of organizational dynamics",
            "Failed to communicate effectively across teams",
            "Created adversarial relationships"
        ]
    },
    # 2. Cloud Knowledge - Kubernetes
    {
        "category": "Cloud Knowledge - Kubernetes",
        "questionText": """## Kubernetes Core Components

List Kubernetes key components in master node and worker node, and explain their responsibilities.""",
        "difficulty": "MEDIUM",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Mid",
        "answerRequirement": """**Master Node (Control Plane) Components:**

1. **kube-apiserver**
   - Central management entity and front-end to the control plane
   - Exposes the Kubernetes API (REST interface)
   - Validates and processes API requests
   - Acts as the communication hub between all components

2. **etcd**
   - Distributed key-value store for all cluster data
   - Stores desired state, configuration, metadata
   - Provides consensus and strong consistency
   - Critical for cluster state persistence

3. **kube-scheduler**
   - Watches for newly created Pods with no assigned node
   - Selects optimal node based on resource requirements, affinity/anti-affinity, taints/tolerations
   - Makes scheduling decisions but doesn't run Pods

4. **kube-controller-manager**
   - Runs controller processes to regulate cluster state
   - Includes: Node Controller, Replication Controller, Endpoints Controller, Service Account Controllers
   - Watches desired vs actual state and takes corrective actions

5. **cloud-controller-manager** (if using cloud provider)
   - Interacts with cloud provider APIs
   - Manages cloud-specific resources (load balancers, routes, volumes)

**Worker Node Components:**

1. **kubelet**
   - Primary node agent running on each worker
   - Receives PodSpecs from API server
   - Ensures containers are running and healthy
   - Reports node and Pod status back to API server
   - Manages container lifecycle through CRI

2. **kube-proxy**
   - Network proxy running on each node
   - Maintains network rules for Pod communication
   - Implements Kubernetes Service abstraction
   - Handles load balancing for Services
   - Modes: iptables, IPVS, or userspace

3. **Container Runtime**
   - Software responsible for running containers
   - Examples: containerd, CRI-O, Docker (deprecated)
   - Implements Container Runtime Interface (CRI)
   - Manages container images, execution, lifecycle

**Key Concepts:**
- All components communicate through apiserver, not directly to etcd
- Watch mechanism and reconciliation loops are central to Kubernetes
- High availability requires multiple master nodes and etcd quorum""",
        "redFlags": [
            "Confuses control plane and worker node components",
            "Can't explain how components interact",
            "Doesn't understand kubelet vs kube-proxy responsibilities",
            "No awareness of etcd's critical role",
            "Can't describe what happens when a Pod is created end-to-end"
        ]
    },
    {
        "category": "Cloud Knowledge - Kubernetes",
        "questionText": """## Kubernetes Networking

Describe the networking model in Kubernetes. How do Pods communicate with each other?""",
        "difficulty": "HARD",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Senior",
        "answerRequirement": """**Core Networking Requirements:**
1. All Pods can communicate with all other Pods without NAT
2. All Nodes can communicate with all Pods without NAT
3. The IP a Pod sees itself as is the same IP others see it as

**Pod-to-Pod Communication:**

**Within Same Node:**
- Pods connect to a virtual ethernet bridge (cbr0 or cni0)
- Each Pod gets a virtual ethernet pair (veth pair)
- One end in Pod network namespace, other on bridge
- Communication happens through Linux bridge

**Across Different Nodes:**
- Requires CNI (Container Network Interface) plugin
- Common plugins: Calico, Cilium, Flannel, Weave
- Implementation approaches:
  - **Overlay networks** (VXLAN): Encapsulate Pod traffic
  - **BGP routing**: Advertise Pod CIDR routes to network
  - **Cloud provider routes**: Use cloud's native routing

**Service Discovery & Load Balancing:**
1. **Services** provide stable IP/DNS for dynamic Pods
2. **kube-proxy** maintains iptables/IPVS rules for Services
3. **DNS** (CoreDNS) provides name resolution: `<service>.<namespace>.svc.cluster.local`
4. **Endpoints** objects track Pod IPs backing a Service

**Network Policies:**
- Define allowed ingress/egress traffic for Pods
- Implemented by CNI plugin (not all support them)
- Based on Pod selectors, namespaces, port/protocol

**Example Flow** (Pod A → Pod B across nodes):
```
Pod A (10.244.1.5)
  ↓ veth pair
Node 1 Bridge (cni0)
  ↓ routing rule
Node 1 eth0
  ↓ network (overlay/BGP/cloud route)
Node 2 eth0
  ↓ routing rule
Node 2 Bridge (cni0)
  ↓ veth pair
Pod B (10.244.2.8)
```

**Service Types:**
- ClusterIP: Internal cluster IP
- NodePort: Exposes on each node's IP at static port
- LoadBalancer: Provisions external load balancer
- ExternalName: Maps to DNS name""",
        "redFlags": [
            "Thinks Pods use Docker networking directly",
            "Doesn't understand how cross-node communication works",
            "No knowledge of CNI plugins",
            "Can't explain kube-proxy's role",
            "No awareness of Network Policies"
        ]
    },
    # 3. Linux Kernel & OS Internals
    {
        "category": "Linux Kernel & OS Internals",
        "questionText": """## Cgroups and Namespaces

What are cgroups and namespaces, and how do they contribute to containerization in Linux?""",
        "difficulty": "HARD",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Senior",
        "answerRequirement": """**Namespaces (Process Isolation):**

1. **PID namespace** - Process isolation
   - Container sees PID 1 as init process
   - Processes inside cannot see host processes
   - Prevents signal interference between containers

2. **Network namespace** - Network isolation
   - Separate network stack (interfaces, routing, iptables)
   - Containers can have overlapping IP ranges
   - Veth pairs connect container to host network

3. **Mount namespace** - Filesystem isolation
   - Separate filesystem mount points
   - Container has own root filesystem (/)
   - Can't see host mounts unless explicitly bound

4. **UTS namespace** - Hostname/domain isolation
   - Each container can have unique hostname

5. **IPC namespace** - Inter-process communication isolation
   - Separate System V IPC, POSIX message queues

6. **User namespace** - UID/GID isolation
   - Map container root (UID 0) to non-root user on host
   - Improved security: root in container ≠ root on host

**Cgroups (Resource Limitation & Accounting):**

1. **CPU cgroup**
   - cpu.shares: Relative CPU time (proportional)
   - cpu.cfs_quota_us: Hard CPU limits
   - Example: Limit container to 50% of 1 CPU core

2. **Memory cgroup**
   - memory.limit_in_bytes: Maximum memory (hard limit)
   - memory.soft_limit_in_bytes: Preferred limit (reclaimable)
   - memory.oom_control: Configure OOM behavior
   - Prevents memory leaks from affecting other containers

3. **blkio cgroup** - Block I/O
   - Throttle disk read/write speed
   - Prioritize I/O for certain containers

4. **net_cls/net_prio** - Network
   - Network packet classification and prioritization
   - Works with tc (traffic control) for QoS

5. **devices cgroup**
   - Whitelist/blacklist device access

**How They Enable Containers:**
- Namespaces provide isolation (separate view of system)
- Cgroups provide resource limits (prevent resource starvation)
- Container runtimes (Docker, containerd) use runc (OCI) to create namespaces/cgroups
- Kubernetes Pods share network/IPC namespaces, separate PID/mount/UTS

**Example:**
```bash
# Create namespaces
unshare --mount --uts --ipc --net --pid --fork bash

# Set cgroup limits
cgcreate -g cpu,memory:mycontainer
echo "50000" > /sys/fs/cgroup/cpu/mycontainer/cpu.cfs_quota_us
echo "536870912" > /sys/fs/cgroup/memory/mycontainer/memory.limit_in_bytes
cgexec -g cpu,memory:mycontainer my-application
```""",
        "redFlags": [
            "Confuses namespaces with cgroups (thinks they're the same)",
            "Can't name specific namespace types",
            "No understanding of resource limiting vs isolation",
            "Doesn't know how container runtimes use these features",
            "No awareness of security boundaries"
        ]
    },
    # 4. System Design
    {
        "category": "System Design",
        "questionText": """## Distributed Cache System Design

How would you design a distributed cache system?""",
        "difficulty": "HARD",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Staff",
        "answerRequirement": """**Requirements Gathering (Critical First Step):**
- **Functional**: Get/Put operations, TTL support, cache eviction
- **Non-functional**:
  - Latency: p99 < 5ms
  - Availability: 99.9% uptime
  - Scale: 1M QPS, 100GB data
  - Consistency: Eventual consistency acceptable?

**Key Design Components:**

**1. Consistent Hashing**
- Maps keys to nodes using hash ring
- Minimizes rehashing when nodes added/removed
- Virtual nodes (vnodes) for better load distribution
- Example: hash(key) % 360 → node with closest position

**2. Data Replication**
- Master-Slave: Write to master, replicate to N slaves
- Replication factor: Typically 2-3 for durability
- Quorum reads/writes (optional): R + W > N for strong consistency
- Tradeoff: Consistency vs Availability (CAP theorem)

**3. Cache Eviction Policies**
- LRU (Least Recently Used): Default for most use cases
- LFU (Least Frequently Used): Better for hot-key scenarios
- TTL-based: Expire keys after set time
- Hybrid: LRU + TTL

**4. High Availability**
- Sentinel (Redis): Monitors master, auto-failover to slave
- Health checks: Heartbeat every 5s, mark unhealthy after 3 failures
- Circuit breaker: Fail fast when node down
- Graceful degradation: Serve stale data or fallback to DB

**5. Scalability**
- Horizontal scaling: Add more nodes
- Sharding: Partition data across nodes
- Connection pooling: Reuse connections
- Pipelining: Batch multiple operations

**6. Cache Stampede Prevention**
- Thundering herd: Multiple clients cache-miss simultaneously
- Solution: Lock/Lease mechanism (first client fetches, others wait)
- Or: Probabilistic early expiration

**7. Monitoring**
- Metrics: Hit rate, miss rate, latency (p50, p99), memory usage, eviction rate
- Alerts: Hit rate < 80%, p99 latency > 10ms, node down
- Logging: Cache operations for debugging

**Tradeoffs:**
- In-memory only (faster) vs Persistent (durable)
- Strong consistency vs High availability
- Centralized vs Embedded cache (per-service)

**Technologies:**
- Redis: In-memory, supports persistence, rich data structures
- Memcached: Pure in-memory, simple key-value
- Hazelcast: Distributed, embedded cache""",
        "redFlags": [
            "Jumps straight to implementation without requirements",
            "Doesn't understand consistent hashing",
            "No mention of replication or availability",
            "Ignores cache eviction strategies",
            "No discussion of monitoring or failure handling",
            "Doesn't consider scalability limits"
        ]
    },
    # 5. Coding & Algorithms
    {
        "category": "Coding & Algorithms",
        "questionText": """## LRU Cache Implementation

Implement an LRU (Least Recently Used) Cache with O(1) time complexity for both get and put operations.""",
        "difficulty": "MEDIUM",
        "targetPosition": "Infrastructure Engineer",
        "targetLevel": "Senior",
        "answerRequirement": """**Requirements:**
- get(key): Return value if exists, else -1. Mark as recently used.
- put(key, value): Insert or update. If at capacity, remove LRU item.
- Time Complexity: O(1) for both operations
- Space Complexity: O(capacity)

**Data Structure:**
- HashMap: Fast O(1) key lookup
- Doubly Linked List: Maintain access order
  - Head: Most recently used
  - Tail: Least recently used

**Why Doubly Linked List?**
- Easy to move nodes to head (recent access) in O(1)
- Easy to remove tail (LRU eviction) in O(1)
- Need prev pointer for O(1) deletion

**Implementation (Python):**
```python
class Node:
    def __init__(self, key, value):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = {}  # key -> Node
        self.head = Node(0, 0)  # Dummy head
        self.tail = Node(0, 0)  # Dummy tail
        self.head.next = self.tail
        self.tail.prev = self.head

    def _remove(self, node):
        prev_node = node.prev
        next_node = node.next
        prev_node.next = next_node
        next_node.prev = prev_node

    def _add_to_head(self, node):
        node.prev = self.head
        node.next = self.head.next
        self.head.next.prev = node
        self.head.next = node

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        node = self.cache[key]
        self._remove(node)
        self._add_to_head(node)
        return node.value

    def put(self, key: int, value: int):
        if key in self.cache:
            node = self.cache[key]
            node.value = value
            self._remove(node)
            self._add_to_head(node)
        else:
            if len(self.cache) >= self.capacity:
                lru = self.tail.prev
                self._remove(lru)
                del self.cache[lru.key]
            new_node = Node(key, value)
            self.cache[key] = new_node
            self._add_to_head(new_node)
```

**Key Points:**
- Dummy head/tail simplify boundary cases
- HashMap provides O(1) lookup
- Doubly linked list provides O(1) insertion/deletion
- Both operations maintain the order invariant

**Production Considerations:**
- Thread safety: Add locks for concurrent access
- TTL support: Add expiration time per key
- Size-based eviction: Track memory size, not count
- Statistics: Track hit/miss rates, eviction counts""",
        "redFlags": [
            "Uses only HashMap or only LinkedList (can't achieve O(1) for both)",
            "Complexity higher than O(1) for operations",
            "Doesn't handle edge cases (capacity = 0, duplicate keys)",
            "Can't explain why doubly linked list is used",
            "No awareness of thread safety issues"
        ]
    }
]


def get_auth_token():
    """Login and get JWT token"""
    login_url = "http://localhost:8080/api/auth/login"
    payload = {
        "username": "austinxu",
        "password": "helloworld"
    }

    response = requests.post(login_url, json=payload)
    if response.status_code == 200:
        token = response.json().get("token")
        print(f"✅ Successfully logged in as austinxu")
        return token
    else:
        print(f"❌ Failed to login: {response.status_code} - {response.text}")
        sys.exit(1)


def get_focus_area_id(category):
    """Get focus area ID for a question category"""
    return FOCUS_AREA_MAPPING.get(category)


def import_questions(auth_token):
    """Import all questions to the database"""
    headers = {
        "Authorization": f"Bearer {auth_token}",
        "Content-Type": "application/json"
    }

    success_count = 0
    fail_count = 0

    print("\n📋 Focus Area Mapping:")
    for category, fa_id in FOCUS_AREA_MAPPING.items():
        print(f"  {category} → Focus Area ID {fa_id}")
    print()

    for i, question in enumerate(QUESTIONS, 1):
        category = question["category"]
        focus_area_id = get_focus_area_id(category)

        if not focus_area_id:
            print(f"⚠️  [{i}/{len(QUESTIONS)}] Skipping: No focus area mapping for '{category}'")
            fail_count += 1
            continue

        payload = {
            "focusAreaId": focus_area_id,
            "questionText": question["questionText"],
            "difficulty": question["difficulty"],
            "targetPosition": question.get("targetPosition"),
            "targetLevel": question.get("targetLevel"),
            "answerRequirement": question["answerRequirement"],
            "redFlags": question.get("redFlags", [])
        }

        try:
            response = requests.post(API_BASE_URL, json=payload, headers=headers)
            if response.status_code in [200, 201]:
                success_count += 1
                title = question["questionText"].split("\n")[0][:50]
                print(f"✅ [{i}/{len(QUESTIONS)}] Imported (Focus Area {focus_area_id}): {title}...")
            else:
                fail_count += 1
                print(f"❌ [{i}/{len(QUESTIONS)}] Failed: {response.status_code} - {response.text[:100]}")
        except Exception as e:
            fail_count += 1
            print(f"❌ [{i}/{len(QUESTIONS)}] Error: {str(e)}")

    print(f"\n{'='*60}")
    print(f"Import Summary:")
    print(f"  ✅ Successfully imported: {success_count}")
    print(f"  ❌ Failed to import: {fail_count}")
    print(f"  📊 Total questions: {len(QUESTIONS)}")
    print(f"{'='*60}")


def main():
    print("Infrastructure Engineering Interview Questions Importer")
    print("="*60)

    # Get auth token
    auth_token = get_auth_token()

    # Import questions
    import_questions(auth_token)


if __name__ == "__main__":
    main()
