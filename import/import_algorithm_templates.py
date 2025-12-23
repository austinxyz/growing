#!/usr/bin/env python3
"""
算法模版库导入脚本
来源: labuladong算法小抄 - 第零章核心框架
URL: https://labuladong.online/algo/home/

运行方式:
    python3 import/import_algorithm_templates.py

依赖:
    pip install requests pymysql python-dotenv
"""

import os
import sys
import json
from datetime import datetime

# 添加父目录到路径（用于导入.env）
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

try:
    import pymysql
    from dotenv import load_dotenv
except ImportError:
    print("❌ 缺少依赖，请运行: pip install pymysql python-dotenv")
    sys.exit(1)

# 加载环境变量
load_dotenv(os.path.join(os.path.dirname(__file__), '../backend/.env'))

# 数据库配置
DB_CONFIG = {
    'host': os.getenv('DB_HOST', '10.0.0.7'),
    'port': int(os.getenv('DB_PORT', '37719')),
    'user': os.getenv('DB_USER', 'austinxu'),
    'password': os.getenv('DB_PASSWORD', 'helloworld'),
    'database': os.getenv('DB_NAME', 'growing'),
    'charset': 'utf8mb4'
}

# 算法模版数据
TEMPLATES = [
    {
        "title": "滑动窗口算法模版",
        "description": "双指针维护动态区间，解决子数组/子串问题的通用框架",
        "language": "java",
        "code": """void slidingWindow(String s) {
    Map<Character, Integer> window = new HashMap<>();

    int left = 0, right = 0;
    while (right < s.length()) {
        // c 是将移入窗口的字符
        char c = s.charAt(right);
        window.put(c, window.getOrDefault(c, 0) + 1);
        right++;
        // 进行窗口内数据的一系列更新
        ...

        // 判断左侧窗口是否要收缩
        while (left < right && window needs shrink) {
            // d 是将移出窗口的字符
            char d = s.charAt(left);
            window.put(d, window.get(d) - 1);
            left++;
            // 进行窗口内数据的一系列更新
            ...
        }
    }
}""",
        "complexity": {
            "time": "O(N)",
            "space": "O(K)"
        },
        "key_points": [
            "左闭右开区间 [left, right) 最便于处理",
            "扩大和缩小窗口的数据更新操作完全对称",
            "最小值在收缩时更新，最大值在扩大时更新",
            "定长窗口可将while改为if优化"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/sliding-window-framework/"
    },
    {
        "title": "二叉树遍历框架",
        "description": "前中后序遍历和层序遍历的核心框架，包含两种思维模式",
        "language": "java",
        "code": """// 1. 基础遍历框架
void traverse(TreeNode root) {
    if (root == null) return;

    // 前序位置 - 刚进入节点
    traverse(root.left);
    // 中序位置 - 左子树遍历完
    traverse(root.right);
    // 后序位置 - 即将离开节点
}

// 2. 层序遍历框架
void levelTraverse(TreeNode root) {
    if (root == null) return;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);

    while (!q.isEmpty()) {
        int sz = q.size();
        for (int i = 0; i < sz; i++) {
            TreeNode cur = q.poll();

            if (cur.left != null) q.offer(cur.left);
            if (cur.right != null) q.offer(cur.right);
        }
    }
}""",
        "complexity": {
            "time": "O(N)",
            "space": "O(H) 递归深度 / O(W) 层序宽度"
        },
        "key_points": [
            "前序位置只能获取父节点参数",
            "后序位置最强，可获取左右子树返回值",
            "需要子树信息时用后序位置",
            "动态规划≈分解问题，回溯≈遍历思路"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/binary-tree-summary/"
    },
    {
        "title": "回溯算法框架",
        "description": "遍历决策树的通用模版，适用于排列组合子集问题",
        "language": "java",
        "code": """void backtrack(int[] nums, LinkedList<Integer> track, boolean[] used) {
    // 触发结束条件
    if (track.size() == nums.length) {
        res.add(new LinkedList(track));
        return;
    }

    for (int i = 0; i < nums.length; i++) {
        // 排除不合法的选择
        if (used[i]) continue;

        // 做选择
        track.add(nums[i]);
        used[i] = true;
        // 进入下一层决策树
        backtrack(nums, track, used);
        // 撤销选择
        track.removeLast();
        used[i] = false;
    }
}""",
        "complexity": {
            "time": "O(N!) 全排列",
            "space": "O(N) 递归栈深度"
        },
        "key_points": [
            "本质是遍历决策树，纯暴力穷举",
            "前序位置做选择，后序位置撤销选择",
            "路径(track) + 选择列表 + 结束条件",
            "结果保存需拷贝避免引用问题"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/backtrack-framework/"
    },
    {
        "title": "动态规划框架",
        "description": "状态转移方程 + 备忘录/DP数组优化重叠子问题",
        "language": "python",
        "code": """# 自顶向下（递归 + 备忘录）
def dp(状态1, 状态2, ...):
    # base case
    if 满足结束条件:
        return 结果

    # 查备忘录
    if memo中已有结果:
        return memo中的结果

    # 穷举所有选择
    for 选择 in 所有可能的选择:
        result = 求最值(result, dp(新状态))

    # 存入备忘录
    memo[当前状态] = result
    return result

# 自底向上（迭代 + DP数组）
dp = [初始值] * (n + 1)
dp[0] = base_case

for 状态1 in 状态1的所有取值:
    for 状态2 in 状态2的所有取值:
        dp[状态] = 求最值(各种选择的结果)""",
        "complexity": {
            "time": "子问题个数 × 单个子问题时间",
            "space": "O(N) 备忘录/DP数组"
        },
        "key_points": [
            "动态规划三要素: 重叠子问题 + 最优子结构 + 状态转移方程",
            "本质是穷举 + 优化",
            "备忘录初始化用特殊值（如-666）而非0",
            "子问题必须互相独立才符合最优子结构"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/dynamic-programming-framework/"
    },
    {
        "title": "BFS算法框架",
        "description": "层序遍历求最短路径的通用模版",
        "language": "java",
        "code": """int bfs(int s, int target) {
    boolean[] visited = new boolean[graph.size()];
    Queue<Integer> q = new LinkedList<>();
    q.offer(s);
    visited[s] = true;
    int step = 0;

    while (!q.isEmpty()) {
        int sz = q.size();
        // 遍历当前层的所有节点
        for (int i = 0; i < sz; i++) {
            int cur = q.poll();
            // 判断是否到达终点
            if (cur == target) {
                return step;
            }
            // 将邻居节点加入队列
            for (int to : neighborsOf(cur)) {
                if (!visited[to]) {
                    q.offer(to);
                    visited[to] = true;
                }
            }
        }
        step++;
    }
    return -1;
}""",
        "complexity": {
            "time": "O(N) 遍历所有节点",
            "space": "O(N) 队列+visited集合"
        },
        "key_points": [
            "本质是二叉树的层序遍历",
            "适用于求解最短路径问题",
            "使用visited集合防止成环",
            "双向BFS从两端扩散可优化性能"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/bfs-framework/"
    },
    {
        "title": "快慢指针 - 原地修改数组",
        "description": "slow指针维护结果区间，fast指针探路",
        "language": "java",
        "code": """int slow = 0, fast = 0;
while (fast < nums.length) {
    if (nums[fast] != nums[slow]) { // 或其他条件
        slow++;
        nums[slow] = nums[fast];
    }
    fast++;
}
return slow + 1; // 新数组长度""",
        "complexity": {
            "time": "O(N)",
            "space": "O(1) 原地修改"
        },
        "key_points": [
            "slow维护结果区间[0..slow]",
            "fast探路找符合条件的元素",
            "返回长度通常是slow+1（索引转长度）",
            "适用于去重、删除元素等原地修改场景"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/array-two-pointers-summary/"
    },
    {
        "title": "左右指针 - 二分查找",
        "description": "有序数组中高效查找目标值",
        "language": "java",
        "code": """int left = 0, right = nums.length - 1;
while(left <= right) {
    int mid = (right + left) / 2;
    if(nums[mid] == target)
        return mid;
    else if (nums[mid] < target)
        left = mid + 1;
    else
        right = mid - 1;
}
return -1;""",
        "complexity": {
            "time": "O(log N)",
            "space": "O(1)"
        },
        "key_points": [
            "数组必须有序",
            "注意边界条件: left <= right",
            "mid计算防溢出: left + (right-left)/2",
            "可扩展为寻找左右边界问题"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/array-two-pointers-summary/"
    },
    {
        "title": "左右指针 - 回文串判断",
        "description": "从中心向两端扩散寻找最长回文子串",
        "language": "java",
        "code": """// 从两端向中心
int left = 0, right = s.length() - 1;
while (left < right) {
    if (s.charAt(left) != s.charAt(right))
        return false;
    left++;
    right--;
}
return true;

// 从中心向两端（最长回文子串）
String palindrome(String s, int l, int r) {
    while (l >= 0 && r < s.length()
           && s.charAt(l) == s.charAt(r)) {
        l--;
        r++;
    }
    return s.substring(l + 1, r); // 返回 s[l+1..r-1]
}""",
        "complexity": {
            "time": "O(N²) 遍历中心×扩散",
            "space": "O(1)"
        },
        "key_points": [
            "需同时检查奇数长度（单中心）和偶数长度（双中心）",
            "扩散时注意边界条件",
            "最长回文子串需遍历所有可能的中心点",
            "返回时注意substring的左闭右开特性"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/array-two-pointers-summary/"
    },
    {
        "title": "贪心算法框架",
        "description": "局部最优选择导致全局最优的算法范式",
        "language": "python",
        "code": """# 贪心算法通用框架
def greedy(problems):
    # 1. 排序/预处理
    problems.sort(key=lambda x: x.某个属性)

    result = []
    for problem in problems:
        # 2. 贪心选择
        if 当前选择是局部最优:
            result.append(problem)
            # 3. 更新状态
            更新相关状态

    return result

# 经典例子：区间调度问题
def intervalSchedule(intervals):
    # 按结束时间排序
    intervals.sort(key=lambda x: x[1])

    count = 0
    end = float('-inf')
    for interval in intervals:
        if interval[0] >= end:
            count += 1
            end = interval[1]

    return count""",
        "complexity": {
            "time": "O(N log N) 排序主导",
            "space": "O(1) 不计输出"
        },
        "key_points": [
            "贪心算法核心：证明局部最优能导致全局最优",
            "通常需要先排序",
            "区间调度问题按结束时间排序",
            "跳跃游戏关注可达位置的最远距离"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/greedy/"
    },
    {
        "title": "DFS深度优先遍历",
        "description": "递归遍历所有路径的通用框架（图/网格）",
        "language": "java",
        "code": """// 图的DFS遍历
boolean[] visited;
void dfs(Graph graph, int v) {
    if (visited[v]) return;

    visited[v] = true;
    // 访问节点v

    for (int neighbor : graph.neighbors(v)) {
        dfs(graph, neighbor);
    }
}

// 网格DFS遍历（岛屿问题）
void dfs(int[][] grid, int i, int j) {
    // 边界检查
    if (i < 0 || j < 0 || i >= grid.length
        || j >= grid[0].length) {
        return;
    }
    // 已访问或障碍物
    if (grid[i][j] != 1) return;

    grid[i][j] = 2; // 标记已访问

    // 上下左右四个方向
    dfs(grid, i + 1, j);
    dfs(grid, i - 1, j);
    dfs(grid, i, j + 1);
    dfs(grid, i, j - 1);
}""",
        "complexity": {
            "time": "O(V + E) 顶点+边",
            "space": "O(V) 递归栈"
        },
        "key_points": [
            "需要visited标记防止重复访问",
            "网格问题可用修改原数组代替visited",
            "四个方向遍历：上下左右",
            "回溯问题需要撤销标记，DFS通常不需要"
        ],
        "source_url": "https://labuladong.online/algo/essential-technique/binary-tree-summary/"
    }
]


def get_db_connection():
    """创建数据库连接"""
    try:
        conn = pymysql.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"❌ 数据库连接失败: {e}")
        sys.exit(1)


def get_admin_user_id(conn):
    """获取管理员用户ID"""
    with conn.cursor() as cursor:
        cursor.execute("SELECT id FROM users WHERE role = 'admin' LIMIT 1")
        result = cursor.fetchone()
        if not result:
            print("❌ 未找到管理员用户")
            sys.exit(1)
        return result[0]


def template_exists(conn, title):
    """检查模版是否已存在"""
    with conn.cursor() as cursor:
        cursor.execute(
            "SELECT id FROM learning_contents WHERE title = %s AND content_type = 'template'",
            (title,)
        )
        return cursor.fetchone() is not None


def insert_template(conn, template, admin_user_id):
    """插入算法模版"""
    # 构建contentData JSON
    content_data = {
        "language": template["language"],
        "code": template["code"],
        "complexity": template["complexity"],
        "keyPoints": template["key_points"],
        "sourceUrl": template["source_url"]
    }

    sql = """
        INSERT INTO learning_contents
        (focus_area_id, stage_id, content_type, title, description,
         url, author, content_data, sort_order, visibility, created_by)
        VALUES
        (NULL, NULL, 'template', %s, %s, %s, %s, %s, %s, 'public', %s)
    """

    with conn.cursor() as cursor:
        cursor.execute(sql, (
            template["title"],
            template["description"],
            template["source_url"],
            "labuladong",
            json.dumps(content_data, ensure_ascii=False),
            0,  # sort_order
            admin_user_id
        ))

    conn.commit()


def main():
    print("=" * 60)
    print("算法模版库导入脚本")
    print("来源: labuladong算法小抄 - 第零章核心框架")
    print("=" * 60)
    print()

    # 连接数据库
    print("📡 连接数据库...")
    conn = get_db_connection()
    print(f"✅ 已连接到 {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")
    print()

    # 获取管理员ID
    admin_user_id = get_admin_user_id(conn)
    print(f"👤 管理员用户ID: {admin_user_id}")
    print()

    # 导入模版
    print(f"📚 准备导入 {len(TEMPLATES)} 个算法模版...")
    print()

    imported_count = 0
    skipped_count = 0

    for idx, template in enumerate(TEMPLATES, 1):
        title = template["title"]

        if template_exists(conn, title):
            print(f"⏭️  [{idx}/{len(TEMPLATES)}] 跳过（已存在）: {title}")
            skipped_count += 1
        else:
            try:
                insert_template(conn, template, admin_user_id)
                print(f"✅ [{idx}/{len(TEMPLATES)}] 导入成功: {title}")
                imported_count += 1
            except Exception as e:
                print(f"❌ [{idx}/{len(TEMPLATES)}] 导入失败: {title}")
                print(f"   错误: {e}")

    print()
    print("=" * 60)
    print("导入完成!")
    print(f"✅ 成功导入: {imported_count} 个")
    print(f"⏭️  跳过已存在: {skipped_count} 个")
    print(f"📊 总计: {len(TEMPLATES)} 个模版")
    print("=" * 60)

    conn.close()


if __name__ == "__main__":
    main()
