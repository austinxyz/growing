# 算法题 → Labuladong Focus Area 映射分析

## 映射规则

根据题目的**核心思路**和**解题技巧**，将276道算法题映射到25个Focus Areas的**阶段3: 实战题目 (practice)**。

---

## 映射统计概览

### Category: Data Structures (185题)

#### Focus Area: Array (68题)

**关键词**: 双指针、滑动窗口、二分搜索、前缀和、差分数组、nSum

**映射题目**:

**双指针类 (20题)**:
- [1] Two Sum - HashMap/双指针
- [11] Container With Most Water - 双指针，比较低的往中间
- [15] 3Sum - 排序 + 双指针
- [26] Remove Duplicates from Sorted Array - 快慢指针
- [27] Remove Element - 快慢指针
- [42] Trapping Rain Water - 双指针
- [75] 颜色分类 - 双指针
- [80] 删除有序数组中的重复项 II - 快慢指针
- [88] 合并两个有序数组 - 双指针
- [167] Two Sum II - 左右指针
- [283] Move Zeroes - 快慢指针
- [344] Reverse String - 左右指针
- [977] 有序数组的平方 - left/right指针
- [986] Interval List Intersections - 双指针

**滑动窗口类 (12题)**:
- [3] Longest Substring Without Repeating Characters - 滑动窗口 + HashMap
- [30] Substring with Concatenation of All Words - 滑动窗口 + HashMap
- [76] Minimum Window Substring - 滑动窗口模板
- [209] 长度最小的子数组 - 滑动窗口
- [219] 存在重复元素 II - 滑动窗口 + Set
- [220] 存在重复元素 III - 滑动窗口 + TreeSet
- [395] 至少有 K 个重复字符的最长子串 - 滑动窗口
- [424] 替换后的最长重复字符 - 滑动窗口
- [438] Find All Anagrams - 滑动窗口模板
- [567] Permutation in String - 滑动窗口
- [713] 乘积小于 K 的子数组 - 滑动窗口
- [1004] 最大连续1的个数 III - 滑动窗口

**二分搜索类 (10题)**:
- [4] Median of Two Sorted Arrays - 二分搜索
- [34] Find First and Last Position - 左右边界
- [69] Sqrt(x) - 二分搜索
- [162] Find Peak Element - 二分搜索
- [410] Split Array Largest Sum - 二分 + 贪心
- [704] Binary Search - 基础二分
- [875] Koko Eating Bananas - 二分搜索
- [1011] Capacity To Ship Packages - 二分搜索
- [1201] Ugly Number III - 二分 + 容斥原理
- [1539] Kth Missing Positive Number - 二分搜索

**前缀和/差分数组类 (8题)**:
- [303] Range Sum Query - Immutable - 前缀和
- [304] Range Sum Query 2D - 前缀和矩阵
- [1094] Car Pooling - 差分数组
- [1109] Corporate Flight Bookings - 差分数组

**二维数组遍历类 (10题)**:
- [48] Rotate Image - 对角线互换
- [54] Spiral Matrix - 边界缩小
- [59] Spiral Matrix II - 边界缩小
- [73] Set Matrix Zeroes - 原地标记
- [289] Game of Life - 三数组记录
- [867] 转置矩阵 - 二维遍历
- [1260] 二维网格迁移 - 一维化处理
- [1329] 将矩阵按对角线排序 - i-j作为key

**其他数组技巧 (8题)**:
- [238] Product of Array Except Self - 前后缀积
- [528] Random Pick with Weight - 前缀和 + 二分
- [581] 最短无序连续子数组 - 排序比较

---

#### Focus Area: Linked List (28题)

**关键词**: 双指针、反转、环检测、回文

**映射题目**:

**双指针类 (8题)**:
- [19] Remove Nth Node From End - 快慢指针
- [21] Merge Two Sorted Lists - 双指针合并
- [82] 删除排序链表中的重复元素 II - 快慢指针
- [83] Remove Duplicates from Sorted List - 快慢指针
- [86] Partition List - 双指针
- [141] Linked List Cycle - 快慢指针
- [142] Linked List Cycle II - 快慢指针 + 相遇点
- [160] Intersection of Two Linked Lists - 双指针
- [876] Middle of the Linked List - 快慢指针

**反转类 (6题)**:
- [25] Reverse Nodes in k-Group - 递归反转
- [61] Rotate List - reverse技巧
- [92] Reverse Linked List II - 递归
- [143] 重排链表 - Stack辅助
- [206] Reverse Linked List - 递归基础
- [445] 两数相加 II - 反转 + 相加

**其他链表技巧 (14题)**:
- [2] 两数相加 - 模拟加法
- [23] Merge k Sorted Lists - 优先队列
- [138] Copy List with Random Pointer - HashMap
- [234] Palindrome Linked List - 递归/反转
- [1019] 链表中的下一个更大节点 - 单调栈

---

#### Focus Area: Stack and Queue (22题)

**关键词**: 单调栈、单调队列、括号匹配

**映射题目**:

**栈基础 (5题)**:
- [20] 有效的括号 - 栈匹配
- [71] 简化路径 - 栈处理路径
- [150] 逆波兰表达式求值 - 栈计算
- [155] 最小栈 - 双栈设计
- [225] Implement Stack using Queues - 数据结构设计
- [232] Implement Queue using Stacks - 双栈实现队列

**单调栈类 (8题)**:
- [739] Daily Temperatures - 单调栈模板
- [496] Next Greater Element I - 单调栈
- [503] Next Greater Element II - 循环数组 + 单调栈
- [853] 车队 - 单调栈变体
- [895] 最大频率栈 - 栈 + HashMap
- [901] 股票价格跨度 - 单调栈
- [1249] Minimum Remove to Make Valid Parentheses - 栈 + 括号
- [1475] 商品折扣后的最终价格 - 单调栈
- [1944] 队列中可以看到的人数 - 单调栈

**队列/单调队列 (4题)**:
- [933] 最近的请求次数 - 队列

**括号问题 (5题)**:
- [20] 有效的括号
- [1249] Minimum Remove to Make Valid Parentheses
- （更多括号题在回溯算法部分）

---

#### Focus Area: Hash Table (15题)

**关键词**: HashMap、HashSet、设计题

**映射题目**:
- [1] Two Sum - HashMap
- [3] Longest Substring - HashMap + 滑动窗口
- [30] Substring with Concatenation - HashMap + 滑动窗口
- [76] Minimum Window Substring - HashMap
- [138] Copy List with Random Pointer - HashMap
- [219] 存在重复元素 II - HashMap/Set
- [220] 存在重复元素 III - TreeSet
- [380] Insert Delete GetRandom O(1) - HashMap + ArrayList
- [383] Ransom Note - 字符计数
- [438] Find All Anagrams - HashMap
- [567] Permutation in String - HashMap

---

#### Focus Area: Binary Tree (42题)

**关键词**: 遍历、BST、构造、序列化、LCA

**映射题目**:

**遍历类 (10题)**:
- [102] 二叉树的层序遍历 - BFS
- [103] 二叉树的锯齿形层序遍历 - BFS变体
- [104] Maximum Depth - 递归
- [107] 二叉树的层序遍历 II - BFS
- [144] Binary Tree Preorder Traversal - 前序
- [199] 二叉树的右视图 - BFS/DFS
- [257] 二叉树的所有路径 - DFS
- [637] 二叉树的层平均值 - BFS
- [1161] 最大层内元素和 - BFS
- [1302] 层数最深叶子节点的和 - BFS

**构造类 (6题)**:
- [96] Unique Binary Search Trees - 分解问题
- [105] Construct from Preorder and Inorder - 递归构造
- [106] Construct from Inorder and Postorder - 递归构造
- [654] Maximum Binary Tree - 递归构造
- [889] Construct from Preorder and Postorder - 递归构造
- [894] 所有可能的满二叉树 - 递归 + memo

**后序位置类 (8题)**:
- [114] Flatten Binary Tree - 后序
- [543] Diameter of Binary Tree - 后序
- [988] 从叶结点开始的最小字符串 - 后序

**BST类 (8题)**:
- [98] Validate BST - 递归
- [230] Kth Smallest in BST - 中序遍历
- [235] LCA of BST - BST特性
- [450] Delete Node in BST - BST删除
- [700] Search in BST - 中序
- [701] Insert into BST - 递归插入
- [1038] BST to Greater Sum Tree - 中序遍历

**序列化类 (2题)**:
- [297] Serialize and Deserialize Binary Tree - 前序/后序/level
- [331] 验证二叉树的前序序列化 - 边的入度出度

**LCA类 (4题)**:
- [236] Lowest Common Ancestor - 后序
- [1644] LCA II - 后序 + 标记
- [1650] LCA III - 链表相交
- [1676] LCA IV - 后序

**其他 (4题)**:
- [116] Populating Next Right Pointers - 遍历
- [117] 填充每个节点的下一个右侧节点指针 II - BFS
- [222] Count Complete Tree Nodes - 完全二叉树
- [226] Invert Binary Tree - 递归
- [662] 二叉树最大宽度 - BFS + id编号
- [919] 完全二叉树插入器 - BFS
- [958] 二叉树的完全性检验 - BFS
- [998] 最大二叉树 II - 递归
- [1022] 从根到叶的二叉数之和 - DFS
- [1110] 删点成林 - 后序

---

#### Focus Area: Graph (18题)

**关键词**: DFS、BFS、拓扑排序、并查集、最短路径

**映射题目**:

**DFS/BFS类 (10题)**:
- [127] 单词接龙 - BFS
- [130] Surrounded Regions - DFS/并查集
- [133] Clone Graph - BFS
- [200] Number of Islands - DFS
- [433] 最小基因变化 - BFS
- [695] Max Area of Island - DFS
- [694] Number of Distinct Islands - DFS + 方向记录
- [841] 钥匙和房间 - BFS
- [863] 二叉树中所有距离为 K 的结点 - BFS
- [994] 腐烂的橘子 - BFS
- [1020] Number of Enclaves - DFS边界填充
- [1091] 二进制矩阵中的最短路径 - BFS
- [1254] Number of Closed Islands - DFS
- [1905] Count Sub Islands - DFS
- [1926] 迷宫中离入口最近的出口 - BFS

**拓扑排序类 (3题)**:
- [207] Course Schedule - DFS/BFS拓扑
- [210] Course Schedule II - 拓扑排序
- [1361] 验证二叉树 - 入度检测

**并查集类 (5题)**:
- [547] 省份数量 - 并查集
- [684] Redundant Connection - 并查集环检测
- [947] 移除最多的同行或同列石头 - 并查集
- [990] Satisfiability of Equality Equations - 并查集
- [1584] Min Cost to Connect All Points - Kruskal + 并查集

**最短路径类 (3题)**:
- [1514] 概率最大的路径 - Dijkstra变体
- [1631] 最小体力消耗路径 - Dijkstra
- [909] Snakes and Ladders - BFS

**二分图类 (2题)**:
- [785] Is Graph Bipartite - DFS染色
- [886] Possible Bipartition - DFS染色

**其他图问题 (2题)**:
- [310] 最小高度树 - 拓扑排序变体
- [399] 除法求值 - 带权图BFS
- [2101] 引爆最多的炸弹 - BFS

---

#### Focus Area: Sorting Algorithms (3题)

**映射题目**:
- 排序算法的习题通常融入到其他数据结构中

---

#### Focus Area: Data Structure Design (12题)

**关键词**: LRU、LFU、随机、中位数

**映射题目**:
- [380] Insert Delete GetRandom O(1) - HashMap + ArrayList
- [382] Linked List Random Node - 蓄水池采样
- [384] Shuffle an Array - 洗牌算法
- [398] Random Pick Index - 蓄水池采样
- [528] Random Pick with Weight - 前缀和 + 二分
- [895] 最大频率栈 - Stack + HashMap设计

---

### Category: Search (42题)

#### Focus Area: Backtracking (35题)

**关键词**: 排列、组合、子集、剪枝、N皇后、数独

**映射题目**:

**排列组合子集 (10题)**:
- [39] Combination Sum - 回溯
- [40] Combination Sum II - 回溯 + 剪枝
- [46] Permutations - 回溯
- [47] Permutations II - 回溯 + 剪枝
- [77] Combinations - 回溯
- [78] Subsets - 回溯
- [90] Subsets II - 回溯 + 剪枝
- [216] Combination Sum III - 回溯

**N皇后/数独 (2题)**:
- [37] Sudoku Solver - 回溯
- [51] N-Queens - 回溯
- [52] N-Queens II - 回溯

**字符串分割 (5题)**:
- [93] 复原 IP 地址 - 回溯
- [131] 分割回文串 - 回溯
- [301] 删除无效的括号 - 回溯 + 剪枝
- [784] 字母大小写全排列 - 回溯
- [1593] 拆分字符串使唯一子字符串的数目最大 - 回溯
- [1849] 将字符串拆分为递减的连续值 - 回溯

**岛屿问题 (见1.6图)**

**其他回溯 (18题)**:
- [79] 单词搜索 - 回溯 + visited
- [473] 火柴拼正方形 - 回溯 + memo
- [526] 优美的排列 - 回溯
- [638] 大礼包 - 回溯
- [967] 连续差相同的数字 - 回溯
- [980] 不同路径 III - 回溯 + visited
- [996] 平方数组的数目 - 回溯 + 剪枝
- [1219] 黄金矿工 - 回溯
- [1306] 跳跃游戏 III - BFS/回溯
- [1457] 二叉树中的伪回文路径 - 回溯 + 计数
- [1723] 完成所有工作的最短时间 - 回溯 + 剪枝
- [2305] 公平分发饼干 - 回溯 + 剪枝

---

#### Focus Area: BFS Algorithm (7题)

**映射题目**:
- [752] Open the Lock - BFS
- [773] Sliding Puzzle - BFS
- [542] 01 矩阵 - BFS
- （其他BFS题在图部分）

---

### Category: Dynamic Programming (68题)

#### Focus Area: Dynamic Programming Fundamentals (15题)

**关键词**: 状态定义、状态转移、初始化

**映射题目**:
- [53] Maximum Subarray - 基础DP
- [62] Unique Paths - 二维DP
- [63] Unique Paths II - 二维DP
- [64] Minimum Path Sum - 二维DP
- [70] Climbing Stairs - 基础DP
- [120] Triangle - 二维DP
- [174] Dungeon Game - 逆向DP
- [509] Fibonacci Number - 基础DP
- [931] Minimum Falling Path Sum - 二维DP
- [1262] Greatest Sum Divisible by Three - 状态DP
- [2320] 统计放置房子的方式数 - DP

---

#### Focus Area: Subsequence Problems (12题)

**关键词**: LCS、LIS、编辑距离

**映射题目**:
- [72] Edit Distance - 编辑距离
- [300] Longest Increasing Subsequence - LIS
- [354] Russian Doll Envelopes - LIS变体
- [368] Largest Divisible Subset - LIS变体
- [583] Delete Operation for Two Strings - LCS变体
- [712] Minimum ASCII Delete Sum - LCS变体
- [718] Maximum Length of Repeated Subarray - DP
- [1143] Longest Common Subsequence - LCS

---

#### Focus Area: Knapsack Problems (8题)

**关键词**: 0-1背包、完全背包、子集背包

**映射题目**:
- [322] Coin Change - 完全背包
- [416] Partition Equal Subset Sum - 0-1背包
- [494] Target Sum - 0-1背包变体
- [518] Coin Change II - 完全背包
- [1049] Last Stone Weight II - 0-1背包

---

#### Focus Area: Game Theory (8题)

**映射题目**:
- [877] Stone Game - 博弈DP
- [2140] 解决智力问题 - DP

---

#### Focus Area: Classic DP Problems (15题)

**关键词**: 打家劫舍、股票买卖

**映射题目**:

**打家劫舍系列 (4题)**:
- [198] House Robber - 基础打家劫舍
- [213] House Robber II - 环形
- [337] House Robber III - 树形DP
- [740] 删除并获得点数 - 打家劫舍变体

**股票买卖系列 (1题)**:
- [121] Best Time to Buy and Sell Stock - 状态机DP

**其他经典DP (10题)**:
- [343] Integer Break - DP
- [2611] 老鼠和奶酪 - 贪心/DP

---

#### Focus Area: Greedy Algorithm (10题)

**关键词**: 区间调度、跳跃游戏

**映射题目**:
- [45] Jump Game II - 贪心
- [55] Jump Game - 贪心
- [134] Gas Station - 贪心/前缀和
- [135] Candy - 贪心
- [402] 移掉 K 位数字 - 贪心 + 单调栈
- [435] Non-overlapping Intervals - 区间调度
- [452] Minimum Number of Arrows - 区间调度
- [502] IPO - 贪心 + 优先队列
- [1024] Video Stitching - 贪心
- [2073] 买票需要的时间 - 贪心

---

### Category: Others (21题)

#### Focus Area: Mathematical Tricks (10题)

**映射题目**:
- [9] Palindrome Number - 数学
- [50] Pow(x, n) - 快速幂
- [67] Add Binary - 模拟
- [137] Single Number II - 位运算
- [149] Max Points on a Line - 数学
- [201] Bitwise AND of Numbers Range - 位运算
- [204] Count Primes - 筛法
- [263] Ugly Number - 数学
- [264] Ugly Number II - 多指针
- [313] Super Ugly Number - 优先队列
- [372] Super Pow - 快速幂
- [791] Custom Sort String - 计数排序

---

#### Focus Area: Classic Interview Problems (11题)

**映射题目**:
- [5] Longest Palindromic Substring - 中心扩展
- [6] Zigzag Conversion - 模拟
- [28] Find First Occurrence - 字符串匹配
- [58] Length of Last Word - 字符串
- [68] Text Justification - 模拟
- [125] Valid Palindrome - 双指针
- [151] Reverse Words - 字符串处理
- [392] Is Subsequence - 双指针
- [427] Construct Quad Tree - 分治
- [973] K Closest Points - 堆/快速选择

---

## 映射方法论

### 1. 基于核心思路的关键词匹配

**示例**:
- 核心思路包含"双指针" → Focus Area: 数组 或 链表
- 核心思路包含"滑动窗口" → Focus Area: 数组
- 核心思路包含"回溯" → Focus Area: 回溯算法
- 核心思路包含"dp" → Focus Area: 动态规划系列
- 核心思路包含"BFS" → Focus Area: BFS算法
- 核心思路包含"并查集" → Focus Area: 图

### 2. 基于题目类型的分类

**数据结构类**:
- 数组/链表题 → Focus Area: 数组 或 链表
- 栈/队列题 → Focus Area: 栈与队列
- 树题 → Focus Area: 二叉树
- 图题 → Focus Area: 图

**算法类**:
- 搜索题 → Focus Area: 回溯算法 或 BFS算法
- DP题 → Focus Area: 动态规划系列
- 数学题 → Focus Area: 数学技巧

### 3. 多标签支持

**一题多标签**:
- [30] Substring with Concatenation - 滑动窗口 + 哈希表
  - Focus Area: 数组 (阶段3)
  - Focus Area: 哈希表 (阶段3)

### 4. 难度分级

- 🟢 简单 → 适合阶段1-2
- 🟠 中等 → 适合阶段2-3
- 🔴 困难 → 适合阶段3

---

## 下一步：数据库导入

### questions 表字段映射

```sql
-- 为每道题添加 focus_area_id 和 stage_id
UPDATE questions
SET focus_area_id = ?,
    stage_id = 3,  -- 所有题目都在阶段3: 实战题目
    difficulty = ?
WHERE leetcode_id = ?;
```

### 示例导入脚本

```python
# 题目 → Focus Area 映射规则
mapping_rules = {
    "双指针": [1, 2],  # Focus Area 1.1 数组, 1.2 链表
    "滑动窗口": [1],   # Focus Area 1.1 数组
    "回溯": [9],       # Focus Area 2.1 回溯算法
    "BFS": [10],       # Focus Area 2.2 BFS算法
    "dp": [11, 12, 13, 14, 15, 16],  # 动态规划系列
    # ...
}

# 基于核心思路自动分类
def classify_question(core_idea):
    for keyword, focus_areas in mapping_rules.items():
        if keyword in core_idea:
            return focus_areas
    return []
```

---

**文档版本**: v1.0
**创建时间**: 2025-12-22
**状态**: 映射规则定义完成 ✅
**下一步**: 创建自动化映射脚本
