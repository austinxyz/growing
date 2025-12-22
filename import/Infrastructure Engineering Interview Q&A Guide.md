# Infrastructure Engineering Interview Q&A Guide

**Authors**: Kire Filipovski, Sharad Murthy, Austin Xu
**Last Updated**: Nov 16, 2025
**Purpose**: Comprehensive Q&A guide for Infrastructure Engineering interviews with detailed answers, evaluation criteria, and red flags.

---

## Table of Contents

1. [Behavioral/Team Fit Questions](#behavioral-team-fit)
2. [Cloud Knowledge - Kubernetes](#cloud-kubernetes)
3. [Linux Kernel & OS Internals](#linux-kernel-os)
4. [System Design](#system-design)
5. [Coding & Algorithms](#coding-algorithms)
6. [Security Engineering](#security)
7. [Networking](#networking)
8. [Observability & Troubleshooting](#observability)

---

## 1. Behavioral/Team Fit Questions {#behavioral-team-fit}

### 1.1 Customer Focus

**Question**: Describe a time when you didn't have all the information in order to understand the exact nature of a user's issue or problem when developing a feature. How did that affect how you developed the feature? What steps (if any) did you take to understand the user's problem better?

**Strong Answer Indicators**:
- Proactively engaged with users through surveys, interviews, or feedback sessions
- Created prototypes or MVPs to gather early feedback
- Collaborated with product managers, UX designers, or customer support teams
- Adjusted approach based on user feedback rather than assumptions
- Demonstrated empathy and user-centric thinking
- Measured success through user satisfaction metrics

**Red Flags**:
- Made assumptions without validation
- Didn't seek input from stakeholders
- Focused only on technical solution without understanding user needs
- No follow-up or iteration after initial implementation
- Blamed users for not being clear

**Sample Answer**:
> "When building a Kubernetes cluster auto-scaling feature, initial requirements were vague about what 'slow application' meant. Instead of assuming, I:
> 1. Set up weekly calls with 3 pilot teams to understand their pain points
> 2. Created a lightweight dashboard showing cluster metrics and asked them to mark when performance felt poor
> 3. Discovered that latency spikes during deployment were the real issue, not just resource shortage
> 4. Pivoted from pure resource-based scaling to include deployment-aware scheduling
> 5. Result: 40% reduction in user-reported performance issues
> The key lesson was that direct user engagement revealed the real problem was timing, not capacity."

**Further Resources**:
- [The Lean Startup](https://www.amazon.com/Lean-Startup-Entrepreneurs-Continuous-Innovation/dp/0307887898) - Build-Measure-Learn cycle
- [User Story Mapping](https://www.jpattonassociates.com/user-story-mapping/) - Understanding user needs

---

### 1.2 Action Oriented

**Question**: Sometimes people delay taking action on something. Describe a time when you saw other people in the organization who were not acting and you took it upon yourself to lead the effort.

**Strong Answer Indicators**:
- Identified a clear gap or opportunity
- Took initiative without being asked
- Rallied others around a shared goal
- Demonstrated ownership and accountability
- Showed results or measurable impact
- Balanced urgency with thoughtful planning

**Red Flags**:
- Acted without considering team input
- Steamrolled others' concerns
- No clear problem definition
- Lack of follow-through
- Blame-focused narrative

**Sample Answer**:
> "Our team had months of discussion about migrating from monolithic deployments to containerized workloads, but no one took the first step due to perceived complexity. I:
> 1. Created a 2-week proof-of-concept containerizing our authentication service
> 2. Documented migration steps, rollback procedures, and metrics showing 30% faster deployments
> 3. Presented to the team with a phased migration plan for non-critical services first
> 4. Volunteered to be on-call for the first migration to build confidence
> 5. Within 6 months, we migrated 80% of services with zero production incidents
> The key was showing concrete value quickly, reducing perceived risk, and leading by example."

**Further Resources**:
- [Start with Why](https://www.amazon.com/Start-Why-Leaders-Inspire-Everyone/dp/1591846447) - Simon Sinek
- [High Output Management](https://www.amazon.com/High-Output-Management-Andrew-Grove/dp/0679762884) - Andy Grove

---

### 1.3 Organizational Savvy

**Question**: Tell me about a team or work situation with diverse stakeholders (EX: Across Orgs, Different Roles). How did you navigate through it? What steps did you take to ensure the team was able to act effectively?

**Strong Answer Indicators**:
- Recognized different stakeholder priorities and constraints
- Built alignment through active listening and compromise
- Established clear communication channels and decision-making processes
- Balanced technical and business considerations
- Demonstrated political awareness without being manipulative
- Created win-win outcomes

**Red Flags**:
- Ignored stakeholder concerns
- Forced decisions without buy-in
- No understanding of organizational dynamics
- Failed to communicate effectively across teams
- Created adversarial relationships

**Sample Answer**:
> "When leading an OS upgrade across 5000+ servers, I needed alignment from:
> - App teams (worried about downtime)
> - Security (needed faster patching)
> - Finance (budget constraints)
> - SRE (operational burden)
>
> My approach:
> 1. **Listening tour**: 1-on-1s with each stakeholder to understand their top 3 concerns
> 2. **Shared document**: Created a decision matrix weighing all concerns transparently
> 3. **Pilot with skeptics**: Chose the most concerned app team for the pilot to get early feedback
> 4. **Phased rollout**: Started with 10% for 2 weeks, then 30%, then 100% - gave teams time to adapt
> 5. **Weekly updates**: Sent metrics on patching speed, downtime, and incidents to show value
>
> Result: Completed in 6 months with 99.9% uptime, and we established a quarterly upgrade cadence. The key was making everyone feel heard and showing incremental value."

**Further Resources**:
- [The Five Dysfunctions of a Team](https://www.tablegroup.com/product/dysfunctions/) - Patrick Lencioni
- [Crucial Conversations](https://www.amazon.com/Crucial-Conversations-Talking-Stakes-Second/dp/0071771328)

---

## 2. Cloud Knowledge - Kubernetes {#cloud-kubernetes}

### 2.1 Kubernetes Core Components

**Question**: List K8s key components in master node and worker node, what's the responsibility.

**Strong Answer**:

**Master Node (Control Plane) Components**:

1. **kube-apiserver**
   - Central management entity and front-end to the control plane
   - Exposes the Kubernetes API (REST interface)
   - Validates and processes API requests
   - Acts as the communication hub between all components

2. **etcd**
   - Distributed key-value store
   - Stores all cluster data (desired state, configuration, metadata)
   - Provides consensus and strong consistency
   - Critical for cluster state persistence

3. **kube-scheduler**
   - Watches for newly created Pods with no assigned node
   - Selects optimal node based on resource requirements, affinity/anti-affinity rules, taints/tolerations
   - Makes scheduling decisions but doesn't actually run Pods

4. **kube-controller-manager**
   - Runs controller processes to regulate cluster state
   - Includes: Node Controller, Replication Controller, Endpoints Controller, Service Account & Token Controllers
   - Continuously watches desired vs actual state and takes corrective actions

5. **cloud-controller-manager** (if using cloud provider)
   - Interacts with cloud provider APIs
   - Manages cloud-specific resources (load balancers, routes, volumes)

**Worker Node Components**:

1. **kubelet**
   - Primary node agent running on each worker
   - Receives PodSpecs from the API server
   - Ensures containers described in PodSpecs are running and healthy
   - Reports node and Pod status back to the API server
   - Manages container lifecycle through container runtime (CRI)

2. **kube-proxy**
   - Network proxy running on each node
   - Maintains network rules for Pod communication
   - Implements Kubernetes Service abstraction (ClusterIP, NodePort, LoadBalancer)
   - Handles load balancing for Services
   - Modes: iptables, IPVS, or userspace

3. **Container Runtime**
   - Software responsible for running containers
   - Examples: containerd, CRI-O, Docker (deprecated)
   - Implements Container Runtime Interface (CRI)
   - Manages container images, execution, and lifecycle

**Strong Answer Indicators**:
- Explains not just "what" but "why" each component exists
- Mentions communication patterns (e.g., all components talk through apiserver, not directly to etcd)
- Understands the watch mechanism and reconciliation loops
- Knows about high availability considerations (multiple master nodes, etcd quorum)
- Can explain failure scenarios (e.g., what happens if scheduler fails?)

**Red Flags**:
- Confuses control plane and worker node components
- Can't explain how components interact
- Doesn't understand kubelet vs kube-proxy responsibilities
- No awareness of etcd's critical role
- Can't describe what happens when a Pod is created end-to-end

**Further Resources**:
- [Kubernetes Components](https://kubernetes.io/docs/concepts/overview/components/)
- [Kubernetes Internals](https://github.com/shubheksha/kubernetes-internals)
- [kubernetes-the-hard-way](https://github.com/kelseyhightower/kubernetes-the-hard-way) - Deep dive setup

---

### 2.2 Kubernetes Networking

**Question**: Describe the networking model in Kubernetes. How do Pods communicate with each other?

**Strong Answer**:

Kubernetes follows a flat networking model with these key principles:

**Core Networking Requirements**:
1. All Pods can communicate with all other Pods without NAT
2. All Nodes can communicate with all Pods without NAT
3. The IP a Pod sees itself as is the same IP others see it as

**Pod-to-Pod Communication**:

**Within Same Node**:
- Pods connect to a virtual ethernet bridge (typically `cbr0` or `cni0`)
- Each Pod gets a virtual ethernet pair (veth pair)
- One end in Pod network namespace, other end on bridge
- Communication happens through Linux bridge

**Across Different Nodes**:
- Requires a CNI (Container Network Interface) plugin
- Common plugins: Calico, Cilium, Flannel, Weave
- Each plugin implements routing differently:
  - **Overlay networks** (VXLAN): Encapsulate Pod traffic (Flannel, Weave)
  - **BGP** routing: Advertise Pod CIDR routes to network (Calico)
  - **Cloud provider routes**: Use cloud's native routing (AWS VPC CNI)

**Service Discovery & Load Balancing**:
1. **Services** provide stable IP/DNS for dynamic Pods
2. **kube-proxy** maintains iptables/IPVS rules for Services
3. **DNS** (CoreDNS) provides name resolution
   - Format: `<service-name>.<namespace>.svc.cluster.local`
4. **Endpoints** objects track Pod IPs backing a Service

**Network Policies**:
- Define allowed ingress/egress traffic for Pods
- Implemented by CNI plugin (not all support them)
- Based on Pod selectors, namespaces, and port/protocol combinations

**Example Flow** (Pod A in Node 1 → Pod B in Node 2):
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

**Strong Answer Indicators**:
- Explains the flat network model and why it's important
- Understands CNI plugin role and can name specific implementations
- Knows the difference between overlay vs BGP-based networking
- Can describe the full packet flow across nodes
- Aware of NetworkPolicy for security
- Understands Service types (ClusterIP, NodePort, LoadBalancer, ExternalName)

**Red Flags**:
- Thinks Pods use Docker networking directly
- Doesn't understand how cross-node communication works
- No knowledge of CNI plugins
- Can't explain kube-proxy's role
- No awareness of Network Policies

**Further Resources**:
- [Kubernetes Networking Guide](https://kubernetes.io/docs/concepts/cluster-administration/networking/)
- [Life of a Packet in Kubernetes](https://dramasamy.medium.com/life-of-a-packet-in-kubernetes-part-1-f9bc0909e051)
- [CNI Specification](https://github.com/containernetworking/cni/blob/master/SPEC.md)

---

## 3. Linux Kernel & OS Internals {#linux-kernel-os}

### 3.1 Cgroups and Namespaces

**Question**: What are cgroups and namespaces, and how do they contribute to containerization in Linux?

**Strong Answer**:

**Namespaces** (Process Isolation):
Namespaces provide process isolation by creating separate views of system resources. Each container gets its own namespace instance:

1. **PID namespace** - Process isolation
   - Container sees PID 1 as its init process
   - Processes inside cannot see host processes
   - Prevents signal interference between containers

2. **Network namespace** - Network isolation
   - Separate network stack (interfaces, routing tables, iptables)
   - Containers can have overlapping IP ranges
   - Veth pairs connect container to host network

3. **Mount namespace** - Filesystem isolation
   - Separate filesystem mount points
   - Container has its own root filesystem (/)
   - Can't see host mounts unless explicitly bound

4. **UTS namespace** - Hostname/domain name isolation
   - Each container can have unique hostname
   - Useful for distributed systems needing unique identities

5. **IPC namespace** - Inter-process communication isolation
   - Separate System V IPC, POSIX message queues
   - Prevents containers from interfering with each other's IPC

6. **User namespace** - UID/GID isolation
   - Map container root (UID 0) to non-root user on host
   - Improved security - root in container ≠ root on host
   - Less commonly used due to complexity

**Cgroups** (Resource Limitation & Accounting):
Control groups limit and monitor resource usage:

1. **CPU cgroup**
   - `cpu.shares`: Relative CPU time (proportional share)
   - `cpu.cfs_period_us`, `cpu.cfs_quota_us`: Hard CPU limits
   - Example: Limit container to 50% of 1 CPU core

2. **Memory cgroup**
   - `memory.limit_in_bytes`: Maximum memory (hard limit)
   - `memory.soft_limit_in_bytes`: Preferred limit (soft, reclaimable under pressure)
   - `memory.oom_control`: Configure OOM behavior
   - Prevents memory leaks from affecting other containers

3. **blkio cgroup** - Block I/O
   - Throttle disk read/write speed
   - Prioritize I/O for certain containers
   - Example: Limit background jobs to prevent starving critical services

4. **net_cls/net_prio cgroup** - Network
   - Network packet classification and prioritization
   - Works with tc (traffic control) for QoS

5. **devices cgroup**
   - Whitelist/blacklist device access
   - Prevent containers from accessing specific devices

**How They Enable Containers**:

```bash
# Example: Creating a simple container with namespaces and cgroups

# Create new namespaces
unshare --mount --uts --ipc --net --pid --fork --user --map-root-user bash

# Set up cgroup limits
cgcreate -g cpu,memory:mycontainer
echo "50000" > /sys/fs/cgroup/cpu/mycontainer/cpu.cfs_quota_us  # 50% CPU
echo "536870912" > /sys/fs/cgroup/memory/mycontainer/memory.limit_in_bytes  # 512MB

# Run process in cgroup
cgexec -g cpu,memory:mycontainer my-application
```

**Container Runtime Integration**:
- **Docker/containerd**: Uses `runc` (OCI runtime) which creates namespaces/cgroups
- **Kubernetes**: Pods share network/IPC namespaces, separate PID/mount/UTS
- **systemd**: Manages cgroups hierarchy (cgroup v2)

**Strong Answer Indicators**:
- Explains both isolation (namespaces) and resource limits (cgroups)
- Knows the difference between namespace types and their use cases
- Understands security implications (user namespaces, device cgroups)
- Can describe how container runtimes use these primitives
- Aware of cgroup v1 vs v2 differences
- Mentions security considerations (CAP_SYS_ADMIN for creating namespaces)

**Red Flags**:
- Confuses namespaces with cgroups (or thinks they're the same)
- Can't name specific namespace types
- No understanding of resource limiting vs isolation
- Doesn't know how container runtimes use these features
- No awareness of security boundaries

**Further Resources**:
- [Man pages: namespaces(7), cgroups(7)](https://man7.org/linux/man-pages/)
- [Understanding Linux Containers](https://www.redhat.com/en/topics/containers/whats-a-linux-container)
- [Cgroups, namespaces, and beyond](https://www.youtube.com/watch?v=sK5i-N34im8) - Docker talk
- [LXC - Linux Containers](https://linuxcontainers.org/) - Reference implementation

---

## 4. System Design {#system-design}

### 4.1 Distributed Cache System

**Question**: How to design a distributed cache system

**Strong Answer**:

**Requirements Gathering** (Critical First Step):
- **Functional**: Get/Put operations, TTL support, cache eviction
- **Non-functional**:
  - Latency: p99 < 5ms
  - Availability: 99.9% uptime
  - Scale: 1M QPS, 100GB data
  - Consistency: Eventual consistency acceptable?

**High-Level Architecture**:

```
┌─────────┐
│ Client  │
└────┬────┘
     │
     ↓
┌─────────────────┐
│ Consistent Hash │  ← Route to correct cache node
│   Load Balancer │
└────┬────────────┘
     │
     ↓
┌────────────────────────────────┐
│  Cache Cluster (Redis/Memcached)
│  ┌──────┐  ┌──────┐  ┌──────┐│
│  │Node 1│  │Node 2│  │Node 3││
│  │Master│  │Master│  │Master││
│  └──┬───┘  └──┬───┘  └──┬───┘│
│     │Replica  │Replica  │Replica
│  ┌──┴───┐  ┌──┴───┐  ┌──┴───┐│
│  │Slave │  │Slave │  │Slave ││
│  └──────┘  └──────┘  └──────┘│
└────────────────────────────────┘
```

**Key Design Components**:

**1. Consistent Hashing**:
- Maps keys to nodes using hash ring
- Minimizes rehashing when nodes added/removed
- Virtual nodes (vnodes) for better load distribution
- Example: `hash(key) % 360 → node with closest position`

**2. Data Replication**:
- **Master-Slave**: Write to master, replicate to N slaves
- **Replication factor**: Typically 2-3 for durability
- **Quorum reads/writes** (optional): R + W > N for strong consistency
- Tradeoff: Consistency vs Availability (CAP theorem)

**3. Cache Eviction Policies**:
- **LRU** (Least Recently Used): Default choice for most use cases
- **LFU** (Least Frequently Used): Better for hot-key scenarios
- **FIFO**: Simple but often inefficient
- **TTL-based**: Expire keys after set time
- Hybrid: LRU + TTL

**4. High Availability**:
- **Sentinel** (Redis): Monitors master, auto-failover to slave
- **Health checks**: Heartbeat every 5s, mark unhealthy after 3 failures
- **Circuit breaker**: Fail fast when node down
- **Graceful degradation**: Serve stale data or fallback to DB

**5. Scalability Considerations**:
- **Horizontal scaling**: Add more nodes to cluster
- **Sharding**: Partition data across nodes
- **Connection pooling**: Reuse connections to reduce overhead
- **Pipelining**: Batch multiple operations in one network roundtrip

**6. Cache Warming & Stampede Prevention**:
- **Cache warming**: Pre-populate on deployment
- **Thundering herd**: Multiple clients cache-miss simultaneously
  - Solution: **Lock/Lease** mechanism (first client fetches, others wait)
  - Or: **Probabilistic early expiration** (expire before TTL based on probability)

**7. Monitoring & Observability**:
- **Metrics**: Hit rate, miss rate, latency (p50, p99), memory usage, eviction rate
- **Alerts**: Hit rate < 80%, p99 latency > 10ms, node down
- **Logging**: Cache operations for debugging

**Example Implementation** (Python pseudocode):
```python
import hashlib
import bisect

class ConsistentHash:
    def __init__(self, nodes, vnodes=150):
        self.vnodes = vnodes
        self.ring = []
        self.node_map = {}
        for node in nodes:
            self.add_node(node)

    def _hash(self, key):
        return int(hashlib.md5(key.encode()).hexdigest(), 16)

    def add_node(self, node):
        for i in range(self.vnodes):
            vnode_key = f"{node}:{i}"
            hash_val = self._hash(vnode_key)
            bisect.insort(self.ring, hash_val)
            self.node_map[hash_val] = node

    def get_node(self, key):
        hash_val = self._hash(key)
        idx = bisect.bisect_right(self.ring, hash_val)
        if idx == len(self.ring):
            idx = 0
        return self.node_map[self.ring[idx]]

class DistributedCache:
    def __init__(self, nodes):
        self.hash_ring = ConsistentHash(nodes)
        self.clients = {node: RedisClient(node) for node in nodes}

    def get(self, key):
        node = self.hash_ring.get_node(key)
        try:
            return self.clients[node].get(key)
        except Exception:
            # Fallback to replica or return None
            return None

    def put(self, key, value, ttl=3600):
        node = self.hash_ring.get_node(key)
        self.clients[node].set(key, value, ex=ttl)
```

**Tradeoffs Discussion**:
- **In-memory only** (Redis, Memcached) vs **Persistent** (Redis with AOF/RDB)
  - Faster but lose data on restart vs Slower but durable
- **Strong consistency** vs **High availability**
  - Quorum-based vs Master-slave replication
- **Centralized** vs **Embedded cache**
  - Shared across services vs Per-service (reduces network, but more memory)

**Strong Answer Indicators**:
- Starts with requirements and scale estimation
- Discusses consistent hashing and why it's important
- Mentions CAP theorem tradeoffs
- Includes monitoring and failure scenarios
- Talks about cache stampede prevention
- Considers data persistence and replication
- Provides concrete metrics (latency targets, hit rates)

**Red Flags**:
- Jumps straight to implementation without requirements
- Doesn't understand consistent hashing
- No mention of replication or availability
- Ignores cache eviction strategies
- No discussion of monitoring or failure handling
- Doesn't consider scalability limits

**Further Resources**:
- [Designing Data-Intensive Applications](https://www.amazon.com/Designing-Data-Intensive-Applications-Reliable-Maintainable/dp/1449373321) - Martin Kleppmann
- [System Design Interview](https://www.amazon.com/System-Design-Interview-insiders-Second/dp/B08CMF2CQF) - Alex Xu
- [Redis Documentation](https://redis.io/documentation)
- [Memcached Architecture](https://github.com/memcached/memcached/wiki/Architecture)

---

## 5. Coding & Algorithms {#coding-algorithms}

### 5.1 LRU Cache Implementation

**Question**: How to implement LRU algorithm

**Strong Answer**:

**LRU (Least Recently Used) Cache** evicts the least recently accessed item when capacity is reached.

**Requirements**:
- `get(key)`: Return value if exists, else -1. Mark as recently used.
- `put(key, value)`: Insert or update. If at capacity, remove LRU item.
- **Time Complexity**: O(1) for both operations
- **Space Complexity**: O(capacity)

**Data Structure**:
- **HashMap**: Fast O(1) key lookup
- **Doubly Linked List**: Maintain access order
  - Head: Most recently used
  - Tail: Least recently used

**Why Doubly Linked List?**
- Easy to move nodes to head (recent access) in O(1)
- Easy to remove tail (LRU eviction) in O(1)
- Need prev pointer for O(1) deletion

**Implementation** (Python):

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
        # Dummy head and tail for easy boundary handling
        self.head = Node(0, 0)
        self.tail = Node(0, 0)
        self.head.next = self.tail
        self.tail.prev = self.head

    def _remove(self, node):
        """Remove node from linked list"""
        prev_node = node.prev
        next_node = node.next
        prev_node.next = next_node
        next_node.prev = prev_node

    def _add_to_head(self, node):
        """Add node right after head (most recent)"""
        node.prev = self.head
        node.next = self.head.next
        self.head.next.prev = node
        self.head.next = node

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1

        node = self.cache[key]
        # Move to head (mark as recently used)
        self._remove(node)
        self._add_to_head(node)
        return node.value

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            # Update existing key
            node = self.cache[key]
            node.value = value
            self._remove(node)
            self._add_to_head(node)
        else:
            # Insert new key
            if len(self.cache) >= self.capacity:
                # Evict LRU (tail.prev)
                lru_node = self.tail.prev
                self._remove(lru_node)
                del self.cache[lru_node.key]

            new_node = Node(key, value)
            self.cache[key] = new_node
            self._add_to_head(new_node)

# Test
cache = LRUCache(2)
cache.put(1, 1)
cache.put(2, 2)
print(cache.get(1))  # Returns 1
cache.put(3, 3)      # Evicts key 2
print(cache.get(2))  # Returns -1 (not found)
```

**Time & Space Complexity**:
- `get()`: O(1) - HashMap lookup + linked list update
- `put()`: O(1) - HashMap insert/update + linked list update
- Space: O(capacity) - HashMap + linked list nodes

**Alternative: Python OrderedDict**:
```python
from collections import OrderedDict

class LRUCache:
    def __init__(self, capacity: int):
        self.cache = OrderedDict()
        self.capacity = capacity

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        self.cache.move_to_end(key)  # Mark as recently used
        return self.cache[key]

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            self.cache.move_to_end(key)
        self.cache[key] = value
        if len(self.cache) > self.capacity:
            self.cache.popitem(last=False)  # Remove LRU (first item)
```

**Production Considerations**:
- **Thread safety**: Use locks for concurrent access
- **TTL support**: Add expiration time per key
- **Size-based eviction**: Evict based on memory size, not count
- **Statistics**: Track hit/miss rates, eviction counts

**Follow-up Questions to Expect**:
1. How would you make it thread-safe?
   - Add `threading.Lock()` around get/put operations
2. How would you implement LFU (Least Frequently Used)?
   - Add frequency counter, use min-heap for eviction
3. What if keys have different sizes?
   - Track total memory size, not just count
4. How would you persist the cache to disk?
   - Serialize to JSON/pickle, save periodically or on shutdown

**Strong Answer Indicators**:
- Explains why both HashMap and Doubly Linked List are needed
- Implements both get() and put() correctly with O(1) time
- Uses dummy head/tail to simplify boundary cases
- Discusses trade-offs and production considerations
- Can implement without looking up syntax

**Red Flags**:
- Uses only HashMap or only LinkedList (can't achieve O(1) for both operations)
- Complexity higher than O(1) for operations
- Doesn't handle edge cases (capacity = 0, duplicate keys)
- Can't explain why doubly linked list is used
- No awareness of thread safety issues

**Further Resources**:
- [LeetCode 146. LRU Cache](https://leetcode.com/problems/lru-cache/)
- [Wikipedia - Cache Replacement Policies](https://en.wikipedia.org/wiki/Cache_replacement_policies)

---

## Template for Remaining Questions

*Due to the large number of questions (100+), the remaining questions follow this structure. You can expand any category as needed:*

### [Category Name]

**Question**: [Question text]

**Strong Answer Indicators**:
- [Key points that indicate deep understanding]
- [Technical accuracy markers]
- [Practical experience indicators]

**Red Flags**:
- [Warning signs of shallow understanding]
- [Common misconceptions]
- [Gaps in knowledge]

**Sample Answer**:
> [Concrete example answer with STAR format where applicable]

**Further Resources**:
- [Relevant documentation]
- [Books, articles, courses]

---

## Evaluation Framework

### Strong Candidate Signals
1. **Technical Depth**: Can go multiple levels deep on any topic
2. **Problem-Solving**: Asks clarifying questions, considers tradeoffs
3. **Communication**: Explains complex concepts clearly
4. **Production Mindset**: Considers monitoring, failure scenarios, scalability
5. **Continuous Learning**: References recent technologies, shows curiosity
6. **Collaboration**: Team-first mentality, builds consensus

### Red Flag Signals
1. **Shallow Knowledge**: Can't explain "why" behind solutions
2. **No Tradeoff Discussion**: Presents one solution without alternatives
3. **Poor Communication**: Can't explain technical concepts simply
4. **No Production Experience**: Doesn't consider failure scenarios
5. **Blame Culture**: Points fingers at others in behavioral questions
6. **Outdated Knowledge**: Not aware of industry trends

---

## Quick Reference: Question-to-Category Mapping

| Category | Number of Questions | Key Topics |
|----------|-------------------|------------|
| Behavioral/Team Fit | 15 | Customer focus, Action oriented, Org savvy |
| Cloud/Kubernetes | 25 | K8s components, Networking, Scaling |
| Linux/Kernel | 18 | Namespaces, Cgroups, Syscalls, Boot process |
| System Design | 12 | Distributed cache, PKI, Rate limiting |
| Coding/Algorithms | 8 | LRU, Sorting, Trees, Hashing |
| Security | 22 | TLS, SSRF, Secrets, mTLS |
| Networking | 10 | Layer 3/7, DNS, Packet flow |
| Observability | 8 | Metrics, Logging, Troubleshooting |

---

**Next Steps**:
1. Review the questions in your focus area
2. Practice explaining concepts to a peer
3. Build hands-on projects demonstrating key skills
4. Refer to "Further Resources" for deep dives

**Note**: This guide provides frameworks and examples. The best answers come from real experience. Use this as a starting point, then personalize with your own stories and insights.
