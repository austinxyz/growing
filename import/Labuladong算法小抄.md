# Labuladong 算法内容 - Phase4 v2.0 三段式结构

**Skill**: 编程与数据结构
**来源**: https://labuladong.online
**总览**: 4大分类 | 25个Focus Area | 225篇文章
**学习路径**: 基础原理 → 实现代码 → 实战题目

---

## 学习阶段定义 (Learning Stages for Programming Skill)

**阶段1: 基础原理** (theory)
- 数据结构的基本概念、特点、适用场景
- 算法思维框架和核心原理
- 时间空间复杂度分析

**阶段2: 实现代码** (implementation)
- 数据结构的代码实现、API设计
- 算法技巧和模板代码
- 优化策略和实战技巧

**阶段3: 实战题目** (practice)
- LeetCode题目练习
- 算法应用和拓展
- 综合实战项目

---

## 大分类 1: 数据结构

### Focus Area 1.1: 数组

#### 阶段1: 基础原理 (theory)
- [数组（顺序存储）基本原理](https://labuladong.online/algo/data-structure-basic/array-basic/)
- [动态数组代码实现](https://labuladong.online/algo/data-structure-basic/array-implement/)
- [环形数组技巧及实现](https://labuladong.online/algo/data-structure-basic/cycle-array/)
- [位图原理及实现](https://labuladong.online/algo/data-structure-basic/bitmap/)

#### 阶段2: 实现代码 (implementation)
- [双指针技巧秒杀七道数组题目](https://labuladong.online/algo/essential-technique/array-two-pointers-summary/)
- [滑动窗口算法核心代码模板](https://labuladong.online/algo/essential-technique/sliding-window-framework/)
- [二分搜索算法核心代码模板](https://labuladong.online/algo/essential-technique/binary-search-framework/)
- [实际运用二分搜索时的思维框架](https://labuladong.online/algo/frequency-interview/binary-search-in-action/)
- [小而美的算法技巧：前缀和数组](https://labuladong.online/algo/data-structure/prefix-sum/)
- [小而美的算法技巧：差分数组](https://labuladong.online/algo/data-structure/diff-array/)
- [滑动窗口延伸：Rabin Karp 字符匹配算法](https://labuladong.online/algo/practice-in-action/rabinkarp/)
- [带权重的随机选择算法](https://labuladong.online/algo/frequency-interview/random-pick-with-weight/)
- [田忌赛马背后的算法决策](https://labuladong.online/algo/practice-in-action/advantage-shuffle/)

#### 阶段3: 实战题目 (practice)
- [1] 1. Two Sum
  - **核心思路**: 比较长度，记录最长的那个。
  - **链接**: https://leetcode.com/problems/two-sum/

- [11] 11. Container With Most Water
  - **核心思路**: 双指针，那边比较低，往中间，不断比较max。
  - **链接**: https://leetcode.com/problems/container-with-most-water/

- [15] 15. 3Sum ⭐
  - **核心思路**: 排序，去除重复的，转化为2sum的问题。
  - **链接**: https://leetcode.com/problems/3sum/

- [26] 26. Remove Duplicates from Sorted Array
  - **核心思路**: 27，283快慢指针
  - **链接**: https://leetcode.com/problems/remove-duplicates-from-sorted-array/

- [27] 27. Remove Element
  - **核心思路**: 快慢指针
  - **链接**: https://leetcode.com/problems/remove-element/

- [42] 42. Trapping Rain Water
  - **核心思路**: 双指针，左侧最高，右侧最高，那边比较低，将指针往前，并且记录最大和当前的差值，累加
  - **链接**: https://leetcode.com/problems/trapping-rain-water/

- [75] 75. 颜色分类
  - **核心思路**: 记录下p0和p2，互换，如果p<p0,设回p0
  - **链接**: https://leetcode.com/problems/sort-colors/

- [80] 80. 删除有序数组中的重复项 II
  - **核心思路**: 快慢指针，加一个count
  - **链接**: https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/

- [88] 88. 合并两个有序数组
  - **核心思路**: 从结尾开始，两个指针
  - **链接**: https://leetcode.com/problems/merge-sorted-array/

- [167] 167. Two Sum II - Input Array Is Sorted
  - **核心思路**: 左右指针
  - **链接**: https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/

- [283] 283. Move Zeroes
  - **核心思路**: 快慢指针
  - **链接**: https://leetcode.com/problems/move-zeroes/

- [344] 344. Reverse String
  - **核心思路**: 左右指针
  - **链接**: https://leetcode.com/problems/reverse-string/

- [977] 977. 有序数组的平方
  - **核心思路**: left，right
  - **链接**: https://leetcode.com/problems/squares-of-a-sorted-array/

- [986] 986. Interval List Intersections
  - **核心思路**: 记录firstIndex，secondIndex，如果first end < another start, first ++, 反之 second end < first start, second++，取max first，min end，append到result中，那个end，对应的index++，直到某个index hit到最后。
  - **链接**: https://leetcode.com/problems/interval-list-intersections/description/

- [3] 3. Longest Substring Without Repeating Characters
  - **核心思路**: 用window map来记录每个字符的个数，发现right的个数大于1了，移动left直到为1，比较长度，记录最长的那个。
  - **链接**: https://leetcode.com/problems/longest-substring-without-repeating-characters/

- [30] 30. Substring with Concatenation of All Words ⭐
  - **核心思路**: 记录单词长度，总长度（单词长度*word length），从0-word 长度都可以开始，用一个hashmap记录target word count，slide window map，如果发现当前的word count大于target word count，缩小window （left+word count），如果count == target count，记录left，否则 增加right长度，如果当前word没在target中，那么 left = right，清除slide window map，count为0.
  - **链接**: https://leetcode.com/problems/substring-with-concatenation-of-all-words/description/

- [76] 76. Minimum Window Substring
  - **核心思路**: 发现valid=need的情况下，缩小left，记录下最小的right-left的长度，记录当时的start。
  - **链接**: https://leetcode.com/problems/minimum-window-substring/

- [209] 209. 长度最小的子数组
  - **核心思路**: 如果》target，就看看移动left，同时计算right-left的最小值
  - **链接**: https://leetcode.com/problems/minimum-size-subarray-sum/

- [219] 219. 存在重复元素 II ⭐
  - **核心思路**: 用一个set来记录数字，如果在set中，如果right-left>k,就需要移动left
  - **链接**: https://leetcode.com/problems/contains-duplicate-ii/

- [220] 220. 存在重复元素 III
  - **核心思路**: 用treeset ceiling和floor来发现abs，如果符合要求就返回return，不然right-left>k就要移动left
  - **链接**: https://leetcode.com/problems/contains-duplicate-iii/

- [395] 395. 至少有 K 个重复字符的最长子串 ⭐
  - **核心思路**: 计算包含x个不同字符的k个重复字符的最长字串，然后1-》26找到最长的
  - **链接**: https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters/

- [424] 424. 替换后的最长重复字符
  - **核心思路**: 记录每个字符的长度，找到最长的那个，right-left-最长的>k就需要移动left，记录max
  - **链接**: https://leetcode.com/problems/longest-repeating-character-replacement/

- [438] 438. Find All Anagrams in a String
  - **核心思路**: need map记录target 字符和个数，window map记录当前的字符和个数，valid记录符合条件的字符个数，window某个字符个数=need某个字符个数，那么valid++，如果valid=need的size，记录left，再缩小left，字符个数- -，直到right-left的长度小于target的字符串。
  - **链接**: https://leetcode.com/problems/find-all-anagrams-in-a-string/

- [567] 567. Permutation in String
  - **核心思路**: 和438类似，就是发现valid=need size后return true；
  - **链接**: https://leetcode.com/problems/permutation-in-string/

- [713] 713. 乘积小于 K 的子数组
  - **核心思路**: 符合right-left的window，就有right-left中组合，相加
  - **链接**: https://leetcode.com/problems/subarray-product-less-than-k/

- [1004] 1004. 最大连续1的个数 III
  - **核心思路**: 如果right-left-1个数>k就需要移动left，记录max
  - **链接**: https://leetcode.com/problems/max-consecutive-ones-iii/

- [4] 4. Median of Two Sorted Arrays
  - **核心思路**: k=(n1+n2+1)/2, 取较小的nums 数组，二分查询，得到m1，m2=k-m1-1;  nums1[m1] > nums2[m2], m1要缩小，right=m1，否则增加 left=m1+1；找到m1，m2=k-m1，算m1-1，m2-1的最大值和m1 和m2的最小值，如果n1+n2整除，那么最大值+最小值/2，否则，最大值。
  - **链接**: https://leetcode.com/problems/median-of-two-sorted-arrays/description/

- [34] 34. Find First and Last Position of Element in Sorted Array
  - **核心思路**: 找到左边界和右边界，左边界，=target的时候收紧右，右边界，=target的时候收紧左。left=0，right=length，左闭右开，闭的+1，开的不用加。
  - **链接**: https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/

- [69] 69. Sqrt(x)
  - **核心思路**: 二分查询 x≤1 return x, left = 1; right = x/2+1, 二分查询，mid，和x/mid，如果mid > x/mid，那么要减小，right=mid，否则是增加left = mid+1，最后返回left-1
  - **链接**: https://leetcode.com/problems/sqrtx/description/

- [162] 162. Find Peak Element
  - **核心思路**: 二分查找，如果只有一个数，就是当前数，只有两个数，大的那个，三个或以上，left=1，right=n-2，mid，mid-1，mid+1，比较，如果最大，返回当前数，不然看mid-1大，还是mid+1大，修改left，right。
  - **链接**: https://leetcode.com/problems/find-peak-element/description/

- [410] 410. Split Array Largest Sum
  - **核心思路**: 函数，某个最大值下，可以分为几个数组，left=max，right=sum，二分查询找left 边界，和1011是一样的。
  - **链接**: https://leetcode.com/problems/split-array-largest-sum/

- [704] 704. Binary Search
  - **核心思路**: 两边都是闭，左右都+1，-1
  - **链接**: https://leetcode.com/problems/binary-search/

- [875] 875. Koko Eating Bananas
  - **核心思路**: 函数，某个速度下，花几小时吃掉香蕉，left =1，right=piles的最大值 10^9, 二分查询找left边界410，函数，某个最大值下，可以分为几个数组，left=max，right=sum，二分查询找left 边界，
  - **链接**: https://leetcode.com/problems/koko-eating-bananas/

- [1011] 1011. Capacity To Ship Packages Within D Days
  - **核心思路**: 函数，某个载重下，花几天送达所有包袱，left = max weights，right =sum weights，二分查询找left 边界
  - **链接**: https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/

- [1201] 1201. Ugly Number III ⭐
  - **核心思路**: 通过最小公倍数，来找到个数，a+b+c-ab-ac-bc+abc，然后用二分查找来逼近
  - **链接**: https://leetcode.com/problems/ugly-number-iii/

- [1539] 1539. Kth Missing Positive Number ⭐
  - **核心思路**: 从p1=1开始计数，index为0，如果p1 == nums[index], index++, 不然count++，p1总是++，直到 count==k, 或者index到底，如果count <k, 那么再加 k-count，返回p1-1
  - **链接**: https://leetcode.com/problems/kth-missing-positive-number/description/

- [303] 303. Range Sum Query - Immutable
  - **核心思路**: 用一个preSum，1-n+1，通过preSum（j+1）- preSum（i）
  - **链接**: https://leetcode.com/problems/range-sum-query-immutable/

- [304] 304. Range Sum Query 2D - Immutable
  - **核心思路**: 用一个preSumMatrix，[n+1][m+1],preSum(row2+1)(col2+1)-(row1)(col2+1)-(row2+1)(col1)+(row1)(col1)查分数组，init-》diff数组，increase，i+val，j+1 - val，result
  - **链接**: https://leetcode.com/problems/range-sum-query-2d-immutable/

- [1094] 1094. Car Pooling ⭐
  - **核心思路**: i=first-1，j=end-1, increase val,
  - **链接**: https://leetcode.com/problems/car-pooling/

- [1109] 1109. Corporate Flight Bookings
  - **核心思路**: 最多1001个车站，每个车站的capacity要小于capacity，i=first，j=end-1
  - **链接**: https://leetcode.com/problems/corporate-flight-bookings/

- [48] 48. Rotate Image
  - **核心思路**: 先对角线互换，然后每一行reverse
  - **链接**: https://leetcode.com/problems/rotate-image/

- [54] 54. Spiral Matrix
  - **核心思路**: 59，设定left，right，top，bottom，不断缩小边界
  - **链接**: https://leetcode.com/problems/spiral-matrix/

- [59] 59. Spiral Matrix II
  - **核心思路**: 设定left，right，top，bottom，不断缩小边界
  - **链接**: https://leetcode.com/problems/spiral-matrix-ii/

- [73] 73. Set Matrix Zeroes
  - **核心思路**: 先记录下第一行，第一列有没有0，有的话设firstrowzero，firstcolzero为true，然后遍历剩下的，将0移到第一行，第一列对应的位置，1-n，1-m，如果对应的第一行，第一列为0，那么设为0，然后如果第一行需要为0，将其都变为0，第一列也一样
  - **链接**: https://leetcode.com/problems/set-matrix-zeroes/submissions/1622281905/

- [289] 289. Game of Life ⭐
  - **核心思路**: 用三个数组，记录前一行，当前行，下一行的[-1,0,1]的1的个数。for 0-n，根据prev，curr，next计算值
  - **链接**: https://leetcode.com/problems/game-of-life/description/

- [867] 867. 转置矩阵
  - **核心思路**: 构造一个新的m*n 数组
  - **链接**: https://leetcode.com/problems/transpose-matrix/

- [1260] 1260. 二维网格迁移
  - **核心思路**: 将二维转成一维，先reverse，再分别reverse
  - **链接**: https://leetcode.com/problems/shift-2d-grid/

- [1329] 1329. 将矩阵按对角线排序 ⭐
  - **核心思路**: 用i-j做为key，构造list，倒排序，取最后一个元素构造新的数组
  - **链接**: https://leetcode.com/problems/sort-the-matrix-diagonally/

- [238] 238. Product of Array Except Self
  - **核心思路**: **从左到右，记录下preProd，然后从右到左，记录postProd，相乘
  - **链接**: https://leetcode.com/problems/product-of-array-except-self/description/

- [528] 528. Random Pick with Weight ⭐
  - **核心思路**: 前缀和，然后[1, presum[n-1]]中找随机数，二分查找找最右侧。
  - **链接**: https://leetcode.com/problems/random-pick-with-weight/

- [581] 581. 最短无序连续子数组
  - **核心思路**: 将数组排序，找到左边不等于排序后的第一个元素，右边不等于排序后的第一个元素，right-left+1
  - **链接**: https://leetcode.com/problems/shortest-unsorted-continuous-subarray/


---

### Focus Area 1.2: 链表

#### 阶段1: 基础原理 (theory)
- [链表（链式存储）基本原理](https://labuladong.online/algo/data-structure-basic/linkedlist-basic/)
- [链表代码实现](https://labuladong.online/algo/data-structure-basic/linkedlist-implement/)
- [跳表核心原理](https://labuladong.online/algo/data-structure-basic/skip-list-basic/)

#### 阶段2: 实现代码 (implementation)
- [双指针技巧秒杀七道链表题目](https://labuladong.online/algo/essential-technique/linked-list-skills-summary/)
- [单链表的花式反转方法汇总](https://labuladong.online/algo/data-structure/reverse-linked-list-recursion/)
- [如何判断回文链表](https://labuladong.online/algo/data-structure/palindrome-linked-list/)

#### 阶段3: 实战题目 (practice)
- [19] 19. Remove Nth Node From End of List
  - **核心思路**: 因为要记住前一个节点，从dummy开始
  - **链接**: https://leetcode.com/problems/remove-nth-node-from-end-of-list/

- [21] 21. Merge Two Sorted Lists
  - **核心思路**: 将result的指针从dummy开始
  - **链接**: https://leetcode.com/problems/merge-two-sorted-lists/

- [82] 82. 删除排序链表中的重复元素 II ⭐
  - **核心思路**: 快慢指针，记得要记下prev，或者用两个list，一个dup，一个noDup
  - **链接**: https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/

- [83] 83. Remove Duplicates from Sorted List
  - **核心思路**: 快慢指针
  - **链接**: https://leetcode.com/problems/remove-duplicates-from-sorted-list/

- [86] 86. Partition List
  - **核心思路**: 两个dummy list，然后最后连起来，记得要把h的最后节点next设为null
  - **链接**: https://leetcode.com/problems/partition-list/

- [141] 141. Linked List Cycle
  - **核心思路**: 快慢指针，快的追上慢的
  - **链接**: https://leetcode.com/problems/linked-list-cycle/

- [142] 142. Linked List Cycle II
  - **核心思路**: 快慢指针，等到相遇的时候讲一个重置为head，然后步进1，再次相遇就是重合点141， 快慢指针，快的追上慢的
  - **链接**: https://leetcode.com/problems/linked-list-cycle-ii/

- [160] 160. Intersection of Two Linked Lists
  - **核心思路**: 将两个list连起来，先a后b，或者先b后a，重复node就是答案
  - **链接**: https://leetcode.com/problems/intersection-of-two-linked-lists/

- [876] 876. Middle of the Linked List
  - **核心思路**: 快慢指针
  - **链接**: https://leetcode.com/problems/middle-of-the-linked-list/

- [25] 25. Reverse Nodes in k-Group
  - **核心思路**: 用递归，拿到succesor head.next = successor
  - **链接**: https://leetcode.com/problems/reverse-nodes-in-k-group/

- [61] 61. Rotate List ⭐
  - **核心思路**: 先reverse整个list，然后找到分别reverse 前K个，和后K个，链接起来
  - **链接**: https://leetcode.com/problems/rotate-list/

- [92] 92. Reverse Linked List II ⭐
  - **核心思路**: 用递归，先前进到m，触发25
  - **链接**: https://leetcode.com/problems/reverse-linked-list-ii/

- [143] 143. 重排链表 ⭐
  - **核心思路**: 将链表存在一个stack中，遍历，拿到p，last，next，让p.next=last, last.next=next, p=next; last =next, or last.next=next, 结束。
  - **链接**: https://leetcode.com/problems/reorder-list/

- [206] 206. Reverse Linked List
  - **核心思路**: 用递归，[head.next](http://head.next/).next = head, head.next = null;
  - **链接**: https://leetcode.com/problems/reverse-linked-list/

- [445] 445. 两数相加 II
  - **核心思路**: 先reverse list，就是2
  - **链接**: https://leetcode.com/problems/add-two-numbers-ii/

- [2] 2. 两数相加
  - **核心思路**: 待补充
  - **链接**: https://leetcode.com/problems/add-two-numbers/

- [23] 23. Merge k Sorted Lists
  - **核心思路**: 利用heap/PriorityQueue，比较函数(a,b)→(a.val-b.val)
  - **链接**: https://leetcode.com/problems/merge-k-sorted-lists/

- [138] 138. Copy List with Random Pointer ⭐
  - **核心思路**: **先遍历list，create new node，用一个map记录old，new，再重新遍历一次list，找到next，random，非null，通过map找到new，赋值
  - **链接**: https://leetcode.com/problems/copy-list-with-random-pointer/

- [234] 234. Palindrome Linked List
  - **核心思路**: 用递归，left，right，res都是全局变量
  - **链接**: https://leetcode.com/problems/palindrome-linked-list/

- [1019] 1019. 链表中的下一个更大节点
  - **核心思路**: 遍历列表放在array中，然后用单调栈解决
  - **链接**: https://leetcode.com/problems/next-greater-node-in-linked-list/


---

### Focus Area 1.3: 栈与队列

#### 阶段1: 基础原理 (theory)
- [队列/栈基本原理](https://labuladong.online/algo/data-structure-basic/queue-stack-basic/)
- [用链表实现队列/栈](https://labuladong.online/algo/data-structure-basic/linked-queue-stack/)
- [用数组实现队列/栈](https://labuladong.online/algo/data-structure-basic/array-queue-stack/)
- [双端队列（Deque）原理及实现](https://labuladong.online/algo/data-structure-basic/deque-implement/)

#### 阶段2: 实现代码 (implementation)
- [队列实现栈以及栈实现队列](https://labuladong.online/algo/data-structure/stack-queue/)
- [单调栈算法模板解决三道例题](https://labuladong.online/algo/data-structure/monotonic-stack/)
- [单调队列结构解决滑动窗口问题](https://labuladong.online/algo/data-structure/monotonic-queue/)

#### 阶段3: 实战题目 (practice)
- [20] 20. 有效的括号
  - **核心思路**: 遇到{[(压入}]),遇到}])pop。如果不同，那么false，最后stack为空，为true
  - **链接**: https://leetcode.com/problems/valid-parentheses/

- [71] 71. 简化路径
  - **核心思路**: 遇到. 直接忽略，遇到..pop，遇到其他push，最后再输出stack
  - **链接**: https://leetcode.com/problems/simplify-path/

- [150] 150. 逆波兰表达式求值
  - **核心思路**: 遇到表达式，pop 两个值计算，将结果压回，遇到数字，压栈
  - **链接**: https://leetcode.com/problems/evaluate-reverse-polish-notation/

- [155] 155. 最小栈
  - **核心思路**: 用两个栈，一个栈记录最小值，push的时候看栈顶和当前元素，如果小，压入当前元素，否则压入栈顶元素。pop的时候都pop，getmin拿最小栈的peek
  - **链接**: https://leetcode.com/problems/min-stack/

- [225] 225. Implement Stack using Queues
  - **核心思路**: 记录top，push的时候还是push，记录top，pop的话遍历queue，修改top为最后第二个元素，poll
  - **链接**: https://leetcode.com/problems/implement-stack-using-queues/

- [232] 232. Implement Queue using Stacks ⭐
  - **核心思路**: 两个stack，top，bottom，push的时候push到top，pop的时候，先从top压到bottom，拿bottom的头，empty两个stack都是空
  - **链接**: https://leetcode.com/problems/implement-queue-using-stacks/

- [739] 739. Daily Temperatures ⭐
  - **核心思路**: 在stack中放index
  - **链接**: https://leetcode.com/problems/daily-temperatures/

- [496] 496. Next Greater Element I
  - **核心思路**: 记录下 nums2的下一个greater map，然后将nums1的值在map中查询
  - **链接**: https://leetcode.com/problems/next-greater-element-i/

- [503] 503. Next Greater Element II
  - **核心思路**: 将数组加倍，计算greater map
  - **链接**: https://leetcode.com/problems/next-greater-element-ii/

- [853] 853. 车队
  - **核心思路**: 将车队按照位置排序，然后算出时间，如果位置在前，时间短的，会追上，最后从尾找递增数组个数
  - **链接**: https://leetcode.com/problems/car-fleet/

- [895] 895. 最大频率栈 ⭐
  - **核心思路**: 记录最大freq，记录val to freq map，记录freq对应的stack。push的时候改频率，改map，压栈，如果最大freq变了，改最大freq，pop的时候用最大freq来找stack，找val，该map，如果最大freq变了，改freq。
  - **链接**: https://leetcode.com/problems/maximum-frequency-stack/

- [901] 901. 股票价格跨度
  - **核心思路**: 单调栈，记录value和天数，当挤掉比你小的，需要将对应天数加上
  - **链接**: https://leetcode.com/problems/online-stock-span/

- [1249] 1249. Minimum Remove to Make Valid Parentheses ⭐
  - **核心思路**: 用stack，遇到（，将当前位置压入stack，如果遇到），如果stack为空，那么将当前位置加入delete list，否则pop，遍历完后，将stack中的元素加入delete list，去除deletelist元素 返回数组。
  - **链接**: https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/description/

- [1475] 1475. 商品折扣后的最终价格
  - **核心思路**: 先单调栈找到下一个更小数，然后相减
  - **链接**: https://leetcode.com/problems/final-prices-with-a-special-discount-in-a-shop/

- [1944] 1944. 队列中可以看到的人数 ⭐
  - **核心思路**: 就是栈里面元素的个数，单调栈放比你矮的人
  - **链接**: https://leetcode.com/problems/number-of-visible-people-in-a-queue/

- [933] 933. 最近的请求次数
  - **核心思路**: 用一个队列，将超时的都poll了
  - **链接**: https://leetcode.com/problems/number-of-recent-calls/

- [20] 20. 有效的括号
  - **核心思路**: 遇到{[(压入}]),遇到}])pop。如果不同，那么false，最后stack为空，为true
  - **链接**: https://leetcode.com/problems/valid-parentheses/

- [1249] 1249. Minimum Remove to Make Valid Parentheses ⭐
  - **核心思路**: 用stack，遇到（，将当前位置压入stack，如果遇到），如果stack为空，那么将当前位置加入delete list，否则pop，遍历完后，将stack中的元素加入delete list，去除deletelist元素 返回数组。
  - **链接**: https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/description/


---

### Focus Area 1.4: 哈希表

#### 阶段1: 基础原理 (theory)
- [哈希表核心原理](https://labuladong.online/algo/data-structure-basic/hashmap-basic/)
- [用拉链法实现哈希表](https://labuladong.online/algo/data-structure-basic/hashtable-chaining/)
- [线性探查法的两个难点](https://labuladong.online/algo/data-structure-basic/linear-probing-key-point/)
- [线性探查法的两种代码实现](https://labuladong.online/algo/data-structure-basic/linear-probing-code/)
- [哈希集合的原理及代码实现](https://labuladong.online/algo/data-structure-basic/hash-set/)

#### 阶段2: 实现代码 (implementation)
- [用链表加强哈希表（LinkedHashMap）](https://labuladong.online/algo/data-structure-basic/hashtable-with-linked-list/)
- [用数组加强哈希表（ArrayHashMap）](https://labuladong.online/algo/data-structure-basic/hashtable-with-array/)
- [布隆过滤器原理及实现](https://labuladong.online/algo/data-structure-basic/bloom-filter/)

#### 阶段3: 实战题目 (practice)
- [1] 1. Two Sum
  - **核心思路**: 比较长度，记录最长的那个。
  - **链接**: https://leetcode.com/problems/two-sum/

- [3] 3. Longest Substring Without Repeating Characters
  - **核心思路**: 用window map来记录每个字符的个数，发现right的个数大于1了，移动left直到为1，比较长度，记录最长的那个。
  - **链接**: https://leetcode.com/problems/longest-substring-without-repeating-characters/

- [30] 30. Substring with Concatenation of All Words ⭐
  - **核心思路**: 记录单词长度，总长度（单词长度*word length），从0-word 长度都可以开始，用一个hashmap记录target word count，slide window map，如果发现当前的word count大于target word count，缩小window （left+word count），如果count == target count，记录left，否则 增加right长度，如果当前word没在target中，那么 left = right，清除slide window map，count为0.
  - **链接**: https://leetcode.com/problems/substring-with-concatenation-of-all-words/description/

- [76] 76. Minimum Window Substring
  - **核心思路**: 发现valid=need的情况下，缩小left，记录下最小的right-left的长度，记录当时的start。
  - **链接**: https://leetcode.com/problems/minimum-window-substring/

- [138] 138. Copy List with Random Pointer ⭐
  - **核心思路**: **先遍历list，create new node，用一个map记录old，new，再重新遍历一次list，找到next，random，非null，通过map找到new，赋值
  - **链接**: https://leetcode.com/problems/copy-list-with-random-pointer/

- [219] 219. 存在重复元素 II ⭐
  - **核心思路**: 用一个set来记录数字，如果在set中，如果right-left>k,就需要移动left
  - **链接**: https://leetcode.com/problems/contains-duplicate-ii/

- [220] 220. 存在重复元素 III
  - **核心思路**: 用treeset ceiling和floor来发现abs，如果符合要求就返回return，不然right-left>k就要移动left
  - **链接**: https://leetcode.com/problems/contains-duplicate-iii/

- [380] 380. Insert Delete GetRandom O(1) ⭐
  - **核心思路**: 用一个map记录value/index，用一个list记录values，insert，如果map中不存在，将values加入list，value，list.size-1加入map，remove，如果map中存在，取出index，将最后一个元素放到list的index位置，remove value/index from map，getRandom，拿一个随机数（1-list，size），从list中取数。
  - **链接**: https://leetcode.com/problems/insert-delete-getrandom-o1/description/

- [383] 383. Ransom Note
  - **核心思路**: 用两个26的数组记录每个字符值，保证ransomNode的每个值都《magazine的。
  - **链接**: https://leetcode.com/problems/ransom-note/description/

- [438] 438. Find All Anagrams in a String
  - **核心思路**: need map记录target 字符和个数，window map记录当前的字符和个数，valid记录符合条件的字符个数，window某个字符个数=need某个字符个数，那么valid++，如果valid=need的size，记录left，再缩小left，字符个数- -，直到right-left的长度小于target的字符串。
  - **链接**: https://leetcode.com/problems/find-all-anagrams-in-a-string/

- [567] 567. Permutation in String
  - **核心思路**: 和438类似，就是发现valid=need size后return true；
  - **链接**: https://leetcode.com/problems/permutation-in-string/


---

### Focus Area 1.5: 二叉树

#### 阶段1: 基础原理 (theory)
- [二叉树基础及常见类型](https://labuladong.online/algo/data-structure-basic/binary-tree-basic/)
- [二叉树的递归/层序遍历](https://labuladong.online/algo/data-structure-basic/binary-tree-traverse-basic/)
- [DFS 和 BFS 的适用场景](https://labuladong.online/algo/data-structure-basic/use-case-of-dfs-bfs/)
- [多叉树的递归/层序遍历](https://labuladong.online/algo/data-structure-basic/n-ary-tree-traverse-basic/)
- [二叉搜索树的应用及可视化](https://labuladong.online/algo/data-structure-basic/tree-map-basic/)
- [红黑树的完美平衡及可视化](https://labuladong.online/algo/data-structure-basic/rbtree-basic/)
- [Trie/字典树/前缀树原理及可视化](https://labuladong.online/algo/data-structure-basic/trie-map-basic/)
- [二叉堆核心原理及可视化](https://labuladong.online/algo/data-structure-basic/binary-heap-basic/)
- [二叉堆/优先级队列代码实现](https://labuladong.online/algo/data-structure-basic/binary-heap-implement/)
- [线段树核心原理及可视化](https://labuladong.online/algo/data-structure-basic/segment-tree-basic/)
- [数据压缩和霍夫曼树](https://labuladong.online/algo/data-structure-basic/huffman-tree/)

#### 阶段2: 实现代码 (implementation)
- [二叉树系列算法核心纲领](https://labuladong.online/algo/essential-technique/binary-tree-summary/)
- [二叉树心法（思路篇）](https://labuladong.online/algo/data-structure/binary-tree-part1/)
- [二叉树心法（构造篇）](https://labuladong.online/algo/data-structure/binary-tree-part2/)
- [二叉树心法（后序篇）](https://labuladong.online/algo/data-structure/binary-tree-part3/)
- [二叉树心法（序列化篇）](https://labuladong.online/algo/data-structure/serialize-and-deserialize-binary-tree/)
- [二叉搜索树心法（特性篇）](https://labuladong.online/algo/data-structure/bst-part1/)
- [二叉搜索树心法（基操篇）](https://labuladong.online/algo/data-structure/bst-part2/)
- [二叉搜索树心法（构造篇）](https://labuladong.online/algo/data-structure/bst-part3/)
- [二叉搜索树心法（后序篇）](https://labuladong.online/algo/data-structure/bst-part4/)
- [TreeMap/TreeSet 代码实现](https://labuladong.online/algo/data-structure-basic/tree-map-implement/)
- [基本线段树的代码实现](https://labuladong.online/algo/data-structure/segment-tree-implement/)
- [优化：实现动态线段树](https://labuladong.online/algo/data-structure/segment-tree-dynamic/)
- [优化：实现懒更新线段树](https://labuladong.online/algo/data-structure/segment-tree-lazy-update/)
- [Trie 树代码实现](https://labuladong.online/algo/data-structure/trie-implement/)
- [实现霍夫曼编码压缩算法](https://labuladong.online/algo/data-structure/huffman-tree-implementation/)

#### 阶段3: 实战题目 (practice)
- [102] 102. 二叉树的层序遍历
  - **核心思路**: 层序遍历
  - **链接**: https://leetcode.com/problems/binary-tree-level-order-traversal/

- [103] 103. 二叉树的锯齿形层序遍历 🟠
  - **核心思路**: 层序遍历，reverse，addFirst/addLast
  - **链接**: https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal

- [104] 104. Maximum Depth of Binary Tree
  - **核心思路**: 深度是左右子树深度的max +
  - **链接**: https://leetcode.com/problems/maximum-depth-of-binary-tree/

- [107] 107. 二叉树的层序遍历 II 🟠
  - **核心思路**: 层序遍历 addFirst
  - **链接**: https://leetcode.com/problems/binary-tree-level-order-traversal-ii

- [144] 144. Binary Tree Preorder Traversal ⭐
  - **核心思路**: 前序遍历
  - **链接**: https://leetcode.com/problems/binary-tree-preorder-traversal/

- [199] 199. 二叉树的右视图
  - **核心思路**: 层级遍历，先放right，再放left，那么第一个元素加到res中，最后返回res。或者前序遍历，如果当前depth（>res.size），那就是第一个，加到res中，还是先right，后left。
  - **链接**: https://leetcode.com/problems/binary-tree-right-side-view/

- [257] 257. 二叉树的所有路径
  - **核心思路**: 遍历（node和前缀），当left=right=null是一条路径，放全局list中，然后在left，right遍历，最后返回结果
  - **链接**: https://leetcode.com/problems/binary-tree-paths/

- [637] 637. 二叉树的层平均值 🟢
  - **核心思路**: 层序遍历
  - **链接**: https://leetcode.com/problems/average-of-levels-in-binary-tree

- [1161] 1161. 最大层内元素和
  - **核心思路**: 层序遍历
  - **链接**: https://leetcode.com/problems/maximum-level-sum-of-a-binary-tree/

- [1302] 1302. 层数最深叶子节点的和 🟠
  - **核心思路**: 层序遍历
  - **链接**: https://leetcode.com/problems/deepest-leaves-sum

- [96] 96. Unique Binary Search Trees ⭐
  - **核心思路**: 构造[left, right]的树list，然后lefts * rights
  - **链接**: https://leetcode.com/problems/unique-binary-search-trees/

- [105] 105. Construct Binary Tree from Preorder and Inorder Traversal
  - **核心思路**: preoder，prestart是root，找到inorder的index，找到左边的个数，构造左右子树
  - **链接**: https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/

- [106] 106. Construct Binary Tree from Inorder and Postorder Traversal
  - **核心思路**: postorder，postend是root，找到inorder的index，找到左边的个数，构造左右子树
  - **链接**: https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/

- [654] 654. Maximum Binary Tree ⭐
  - **核心思路**: 找到最大值的index，create root，然后构造左右子树
  - **链接**: https://leetcode.com/problems/maximum-binary-tree/

- [889] 889. Construct Binary Tree from Preorder and Postorder Traversal
  - **核心思路**: preorder，prestart是root，prestart+1去找postorder的index，找到左边个数，构造左右子树114，分解问题，将左右子树都flatten，然后root.left = null, root.right = 左叶子，找到左子树最后一个节点，将右子树拼上去。
  - **链接**: https://leetcode.com/problems/construct-binary-tree-from-preorder-and-postorder-traversal/

- [894] 894. 所有可能的满二叉树
  - **核心思路**: 先计算n=1，用memo记录每层的结果，然后第n层，可以分解为i，j，为左右子树，可以不同组合相乘，new root，返回结果。
  - **链接**: https://leetcode.com/problems/all-possible-full-binary-trees/

- [114] 114. Flatten Binary Tree to Linked List
  - **核心思路**: 待补充
  - **链接**: https://leetcode.com/problems/flatten-binary-tree-to-linked-list/

- [543] 543. Diameter of Binary Tree ⭐
  - **核心思路**: 直径是左右子树最大深度相加，遍历树，返回深度。获得每个node的直径，全局变量比较拿到最大值
  - **链接**: https://leetcode.com/problems/diameter-of-binary-tree/

- [988] 988. 从叶结点开始的最小字符串
  - **核心思路**: 遍历（node和前缀），记录最小string，当left=right=null，判断当前的string是不是比记录的小，然后left，right遍历，最后返回结果
  - **链接**: https://leetcode.com/problems/smallest-string-starting-from-leaf/

- [98] 98. Validate Binary Search Tree
  - **核心思路**: 给一个最大和最小，比最大的大，或者比最小的小，false，不然node做为左边的最大，右边的最小，遍历左右子树
  - **链接**: https://leetcode.com/problems/validate-binary-search-tree/

- [230] 230. Kth Smallest Element in a BST
  - **核心思路**: 中序遍历，全局index记录顺序
  - **链接**: https://leetcode.com/problems/kth-smallest-element-in-a-bst/

- [235] 235. Lowest Common Ancestor of a Binary Search Tree
  - **核心思路**: val1 为小的，val2为大的，如果node比val2都大，找left，比val1都小，找right，中间，返回node。
  - **链接**: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/

- [450] 450. Delete Node in a BST ⭐
  - **核心思路**: 如果比当前大，在右边删，比当前小，在左边删，要删当前节点，如果只有一个叶子，叶子提上来，不然找左边的最小作为root。
  - **链接**: https://leetcode.com/problems/delete-node-in-a-bst/

- [700] 700. Search in a Binary Search Tree
  - **核心思路**: 中序
  - **链接**: https://leetcode.com/problems/search-in-a-binary-search-tree/

- [701] 701. Insert into a Binary Search Tree
  - **核心思路**: 如果比当前大，插入左边，当前小，插入右边，当前为null，构造节点。
  - **链接**: https://leetcode.com/problems/insert-into-a-binary-search-tree/

- [1038] 1038. Binary Search Tree to Greater Sum Tree ⭐
  - **核心思路**: 中序遍历，从right到left，sum全局
  - **链接**: https://leetcode.com/problems/binary-search-tree-to-greater-sum-tree/

- [297] 297. Serialize and Deserialize Binary Tree ⭐
  - **核心思路**: 记录下null节点，前序或者后序的序列化，可以反序列化，但是中序不行，还可以通过level的方法序列化和反序列化。前序，第一个元素为root，然后再递归构造left和right。后序，最后一个元素为root，然后在递归构造left和right。level方法，记录每一层，null也要放在queue中，然后序列化，反序列化，用一个queue，找到非null的left，right放在queue中。
  - **链接**: https://leetcode.com/problems/serialize-and-deserialize-binary-tree/

- [331] 331. 验证二叉树的前序序列化 ⭐
  - **核心思路**: 非空节点，入-1，出+2，空节点，入-1，edge初始为1，遍历节点，如果edge<0返回错误，最后出入应该相等，也就是edge=0。或者用deserize的方法，确保nodes非空，如果遇到#，返回true，再判断左右是不是都是true。
  - **链接**: https://leetcode.com/problems/verify-preorder-serialization-of-a-binary-tree/

- [236] 236. Lowest Common Ancestor of a Binary Tree ⭐
  - **核心思路**: node的值等于val1或者val2，返回node，否则找left，right，两者都非空，返回node，否则返回非空的。
  - **链接**: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/

- [1644] 1644. Lowest Common Ancestor of a Binary Tree II
  - **核心思路**: 通过findQ，findP记录两个都找到了，后序遍历，如果left，right都非空，返回node，如果node的值为p，返回node，findQ为true，返回left，right中非空的一个。
  - **链接**: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-ii/

- [1650] 1650. Lowest Common Ancestor of a Binary Tree III
  - **核心思路**: 通过parent形成链表，找到链表的交叉点，可以将两个链表相连。
  - **链接**: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iii/

- [1676] 1676. Lowest Common Ancestor of a Binary Tree IV
  - **核心思路**: node.val包含在nodes中，返回node，否则find right，find left，都非空，返回node，否则返回非空的
  - **链接**: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iv/

- [116] 116. Populating Next Right Pointers in Each Node
  - **核心思路**: 遍历（node1，node2），三种情况，node1.left, node1.right, node1.right, node2.left, node2.left, node2.right, node1.next = node2;
  - **链接**: https://leetcode.com/problems/populating-next-right-pointers-in-each-node/

- [117] 117. 填充每个节点的下一个右侧节点指针 II
  - **核心思路**: 层序遍历，加next
  - **链接**: https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii/

- [222] 222. Count Complete Tree Nodes
  - **核心思路**: 计算left的层数，right的层数，如果相同那就是完全二叉树，2^level-1, 否则就是count(left)+count(right)+1
  - **链接**: https://leetcode.com/problems/count-complete-tree-nodes/

- [226] 226. Invert Binary Tree
  - **核心思路**: 反转左子树，反转右子树，root.left = right, root.right = left
  - **链接**: https://leetcode.com/problems/invert-binary-tree/

- [662] 662. 二叉树最大宽度 ⭐
  - **核心思路**: 子节点的id = parent id*2，parent id*2+1，找到first，end，end-first+1
  - **链接**: https://leetcode.com/problems/maximum-width-of-binary-tree/

- [919] 919. 完全二叉树插入器 ⭐
  - **核心思路**: 通过层序遍历（广度优先），如果当前节点左右子节点为null，加入插入队列。插入操作，从插入队列中取出值，加入当前node，如果左右都不是null，将这个节点移除插入队列。将当前node加入插入队列。
  - **链接**: https://leetcode.com/problems/complete-binary-tree-inserter/

- [958] 958. 二叉树的完全性检验 🟠
  - **核心思路**: 如果出现null，那么一直是null
  - **链接**: https://leetcode.com/problems/check-completeness-of-a-binary-tree

- [998] 998. 最大二叉树 II
  - **核心思路**: root为null，new root返回，如果root.val 小于val，new root，right为旧root，返回，如果大于，root.right = 往下递归
  - **链接**: https://leetcode.com/problems/maximum-binary-tree-ii/

- [1022] 1022. 从根到叶的二进制数之和
  - **核心思路**: 遍历，（node和前缀值），当left=right=null，加到res中，然后left，right 遍历，最后返回结果1457，用int[10]记录每个数字出现个数，回文序列不能出现2个以上的奇数的数字。遍历，当left=right=null，判断是不是回文序列，再left，right遍历，最后将当前数字 count—
  - **链接**: https://leetcode.com/problems/sum-of-root-to-leaf-binary-numbers/

- [1110] 1110. 删点成林
  - **核心思路**: 将delete node变成一个set，traverse，是不是有parent，node，如果node在deleteset中，needdelete=true，!needdelete && !hasparent,加入result，node.left = 遍历left，node.right =遍历right，最后根据是不是要delete，返回null或者node
  - **链接**: https://leetcode.com/problems/delete-nodes-and-return-forest/


---

### Focus Area 1.6: 图

#### 阶段1: 基础原理 (theory)
- [图论中的基本术语](https://labuladong.online/algo/data-structure-basic/graph-terminology/)
- [图结构的通用代码实现](https://labuladong.online/algo/data-structure-basic/graph-basic/)
- [图结构的 DFS/BFS 遍历](https://labuladong.online/algo/data-structure-basic/graph-traverse-basic/)
- [欧拉图和一笔画游戏](https://labuladong.online/algo/data-structure-basic/eulerian-graph/)
- [图结构最短路径算法概览](https://labuladong.online/algo/data-structure-basic/graph-shortest-path/)
- [最小生成树算法概览](https://labuladong.online/algo/data-structure-basic/graph-minimum-spanning-tree/)
- [Union Find 并查集原理](https://labuladong.online/algo/data-structure-basic/union-find-basic/)

#### 阶段2: 实现代码 (implementation)
- [二分图判定算法](https://labuladong.online/algo/data-structure/bipartite-graph/)
- [Hierholzer 算法寻找欧拉路径](https://labuladong.online/algo/data-structure/eulerian-graph-hierholzer/)
- [环检测及拓扑排序算法](https://labuladong.online/algo/data-structure/topological-sort/)
- [Union-Find 并查集算法](https://labuladong.online/algo/data-structure/union-find/)
- [Dijkstra 算法核心原理及实现](https://labuladong.online/algo/data-structure/dijkstra/)
- [Dijkstra 拓展：带限制的最短路问题](https://labuladong.online/algo/data-structure/dijkstra-follow-up/)
- [Kruskal 最小生成树算法](https://labuladong.online/algo/data-structure/kruskal/)
- [Prim 最小生成树算法](https://labuladong.online/algo/data-structure/prim/)

#### 阶段3: 实战题目 (practice)
- [127] 127. 单词接龙
  - **核心思路**: 找到beginword加入队列，通过字典找到next candidates，visited过滤，如果是最后一个了，返回，不然继续。
  - **链接**: https://leetcode.com/problems/word-ladder/

- [130] 130. Surrounded Regions
  - **核心思路**: 将四条边的O和dummy连起来，中间的O和上下左右四个方向的O连起来，最后将不和dummy连起来的O改成X
  - **链接**: https://leetcode.com/problems/surrounded-regions/

- [133] 133. Clone Graph ⭐
  - **核心思路**: New Node = create Old Node, bfs, add node into queue, poll old node, get new node from map, go through old node.neighbors, if neighbors has no new node in map, create new neighbor, add new neighbor into new node’s neighbors list.
  - **链接**: https://leetcode.com/problems/clone-graph/description/

- [200] 200. Number of Islands ⭐
  - **核心思路**: dfs，填平，计数695，记录面积，填平，比较
  - **链接**: https://leetcode.com/problems/number-of-islands/

- [433] 433. 最小基因变化
  - **核心思路**: 将start加入队列，step=0，取出队列curr，如果等于end，返回step，从bank中取出candidate next，记录visited set，过滤visited，将剩下的加入队列，step++，直到队列为空，返回-1
  - **链接**: https://leetcode.com/problems/minimum-genetic-mutation/

- [695] 695. Max Area of Island
  - **核心思路**: 记录面积，填平，比较
  - **链接**: https://leetcode.com/problems/max-area-of-island/

- [694] 694. Number of Distinct Islands
  - **核心思路**: 记录遍历方向，1，2，3，4，-1，-2，-3，4，做为一个string放在集合中
  - **链接**: https://leetcode.com/problems/number-of-distinct-islands/

- [841] 841. 钥匙和房间
  - **核心思路**: 将零加入队列，取出队列元素，room[i]做为next rooms，用visited记录访问过的房间，未访问过的加入队列，直到队列为0，遍历，看看还有没有未访问过的room，return
  - **链接**: https://leetcode.com/problems/keys-and-rooms/

- [863] 863. 二叉树中所有距离为 K 的结点
  - **核心思路**: 将子节点，父节点看作是一个图，先traverse，拿到node，parent的map，然后从开始节点开始便利，step=0，加入队列，遍历当前队列之，如果level达到k，将结果放入list，否则，将子节点，父节点发放入队列（过滤掉visited）。
  - **链接**: https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree/

- [994] 994. 腐烂的橘子
  - **核心思路**: bfs，将所有2放入队列，step=1，从1变成2，四个方向前进，step++，直到没有路，遍历发现还有没有1. 最后应该是step-1，因为一开始2的不算step
  - **链接**: https://leetcode.com/problems/rotting-oranges/

- [1020] 1020. Number of Enclaves
  - **核心思路**: 先将边上的岛屿填平，然后计算1的个数
  - **链接**: https://leetcode.com/problems/number-of-enclaves/

- [1091] 1091. 二进制矩阵中的最短路径
  - **核心思路**: 如果0，0不是0，将0，0加入队列，取出队列curr，判断是不是n-1，n-1，如果不是，找8个方向candidate，去除越界的，去除非0的，去除visited，将其加入队列。直到队列为空，返回-1
  - **链接**: https://leetcode.com/problems/shortest-path-in-binary-matrix/

- [1254] 1254. Number of Closed Islands
  - **核心思路**: 还是将边上岛屿填平，然后遍历，找到0，便利上下左右填平，++。
  - **链接**: https://leetcode.com/problems/number-of-closed-islands/

- [1905] 1905. Count Sub Islands
  - **核心思路**: 那些第一个grid为0，第一个为1的岛屿填平，然后计算数量
  - **链接**: https://leetcode.com/problems/count-sub-islands/

- [1926] 1926. 迷宫中离入口最近的出口
  - **核心思路**: 将当前node加入队列，step=0，取出队列curr，判断是不是出口（到边界并且不是start），如果不是，找到四个方向candidate，过滤掉越界的，去掉墙，去掉visited，加入队列，step++，直到队列为空，返回-1
  - **链接**: https://leetcode.com/problems/nearest-exit-from-entrance-in-maze/

- [207] 207. Course Schedule
  - **核心思路**: 用DFS，深度优先，先构造临接表，然后遍历i，记录onPath，和visited，如果当前i在onPath，返回false，否则加入onPath，visited为true，然后遍历子节点，遍历完后onPath为false。用BFS，广度优先，构造临接表，然后算入度，入度为0加入queue的先遍历，将对应的节点入度-1，继续加入入度为0的到queue，直到q为空，如果所有node都遍历的，返回true，否则返回false（有环）
  - **链接**: https://leetcode.com/problems/course-schedule/

- [210] 210. Course Schedule II ⭐
  - **核心思路**: 用BFS，将入度0的以此加入res数组，用DFS，遍历子节点后，在后序遍历位置加入节点，最后将queue反转（也可以用stack）
  - **链接**: https://leetcode.com/problems/course-schedule-ii/

- [1361] 1361. 验证二叉树
  - **核心思路**: 有一个入度为0，root，其余入度为1，从root出发，遍历left，right，用visited数组记录，判断是不是有环。
  - **链接**: https://leetcode.com/problems/validate-binary-tree-nodes/

- [547] 547. 省份数量
  - **核心思路**: 遍历，将对应的union起来，最后返回count
  - **链接**: https://leetcode.com/problems/number-of-provinces/

- [684] 684. Redundant Connection
  - **核心思路**: 如果start，end是connected，就返回，否则就union
  - **链接**: https://leetcode.com/problems/redundant-connection/

- [947] 947. 移除最多的同行或同列石头
  - **核心思路**: 二维遍历，将同行或者同列的union起来，最后石头个数-count
  - **链接**: https://leetcode.com/problems/most-stones-removed-with-same-row-or-column/

- [990] 990. Satisfiability of Equality Equations ⭐
  - **核心思路**: 如果是==，那么union，再遍历！=，如果是conneted，那返回false，最后返回true
  - **链接**: https://leetcode.com/problems/satisfiability-of-equality-equations/

- [1584] 1584. Min Cost to Connect All Points
  - **核心思路**: 二维遍历，记录所有距离，从小到大排序，如果当前两个点connected，那么skip，不然union起来，加在总距离中。最后返回总距离。
  - **链接**: https://leetcode.com/problems/min-cost-to-connect-all-points/

- [1514] 1514. 概率最大的路径
  - **核心思路**: 将当前最大概率*到下一个的概率，如果大于dijikstra数组，那替换，加入PriorityQueue（从大到小）。到end点，取出值。
  - **链接**: https://leetcode.com/problems/path-with-maximum-probability/

- [1631] 1631. 最小体力消耗路径 ⭐
  - **核心思路**: 当前的最小体力消耗，和下一个的消耗比较，取max，如果小于dijikstra数组，那替换，加入PriorityQueue（从小到大），遍历到最下面，取出值
  - **链接**: https://leetcode.com/problems/path-with-minimum-effort/

- [909] 909. Snakes and Ladders
  - **核心思路**: ****i = n-1 - （index-1）/n，j=（index-1）/n %2 ==0？ （index-1）%n：n-1-（index-1）%n， bfs，index=1，index+1 -》index+6，找到对应值，如果是-1，放index 到queue，否则放对应的值到queue，如果index》=n*n，返回step，不然step++，用一个visited记录已经便利过的。
  - **链接**: https://leetcode.com/problems/snakes-and-ladders/description/

- [785] 785. Is Graph Bipartite?
  - **核心思路**: 都是二分图，着色。visited，判断两个节点是不是同颜色，没visited，那么着反色。
  - **链接**: https://leetcode.com/problems/is-graph-bipartite/

- [886] 886. Possible Bipartition
  - **核心思路**: 都是二分图，着色。visited，判断两个节点是不是同颜色，没visited，那么着反色。
  - **链接**: https://leetcode.com/problems/possible-bipartition/

- [310] 310. 最小高度树 ⭐
  - **核心思路**: 先用graph拿到临接表，然后拿到叶子结点（边为1），将叶子结点拿掉，将相邻节点的临接表中去除边，如果边为1，那么加入叶子结点队列，继续去除叶子结点直到只有两个节点。返回list。
  - **链接**: https://leetcode.com/problems/minimum-height-trees/

- [399] 399. 除法求值
  - **核心思路**: 构造带权值的图，然后做bfs，将边的权值相乘。
  - **链接**: https://leetcode.com/problems/evaluate-division/

- [2101] 2101. 引爆最多的炸弹
  - **核心思路**: 根据点和点距离和爆炸距离判断联通关系，构造有向图。然后bfs遍历所有节点，找到最多连接的那个。
  - **链接**: https://leetcode.com/problems/detonate-the-maximum-bombs/


---

### Focus Area 1.7: 排序算法

#### 阶段1: 基础原理 (theory)
- [本章导读](https://labuladong.online/algo/intro/sorting/)
- [排序算法的关键指标](https://labuladong.online/algo/data-structure-basic/sort-basic/)
- [选择排序所面临的问题](https://labuladong.online/algo/data-structure-basic/select-sort/)
- [拥有稳定性：冒泡排序](https://labuladong.online/algo/data-structure-basic/bubble-sort/)
- [运用逆向思维：插入排序](https://labuladong.online/algo/data-structure-basic/insertion-sort/)
- [突破 O(N^2)：希尔排序](https://labuladong.online/algo/data-structure-basic/shell-sort/)
- [妙用二叉树前序位置：快速排序](https://labuladong.online/algo/data-structure-basic/quick-sort/)
- [妙用二叉树后序位置：归并排序](https://labuladong.online/algo/data-structure-basic/merge-sort/)
- [二叉堆结构的运用：堆排序](https://labuladong.online/algo/data-structure-basic/heap-sort/)
- [全新的排序原理：计数排序](https://labuladong.online/algo/data-structure-basic/counting-sort/)
- [博采众长：桶排序](https://labuladong.online/algo/data-structure-basic/bucket-sort/)
- [基数排序（Radix Sort）](https://labuladong.online/algo/data-structure-basic/radix-sort/)

#### 阶段2: 实现代码 (implementation)
- [拓展：归并排序详解及应用](https://labuladong.online/algo/practice-in-action/merge-sort/)
- [拓展：快速排序详解及应用](https://labuladong.online/algo/practice-in-action/quick-sort/)

#### 阶段3: 实战题目 (practice)
- （排序算法习题通常融入到其他数据结构习题中）

---

### Focus Area 1.8: 数据结构设计

#### 阶段1: 基础原理 (theory)
- （数据结构设计更侧重实现和应用）

#### 阶段2: 实现代码 (implementation)
- [算法就像搭乐高：手撸 LRU 算法](https://labuladong.online/algo/data-structure/lru-cache/)
- [算法就像搭乐高：手撸 LFU 算法](https://labuladong.online/algo/frequency-interview/lfu/)
- [常数时间删除/查找数组中的任意元素](https://labuladong.online/algo/data-structure/random-set/)
- [设计朋友圈时间线功能](https://labuladong.online/algo/data-structure/design-twitter/)
- [设计考场座位分配算法](https://labuladong.online/algo/frequency-interview/exam-room/)
- [拓展：如何实现一个计算器](https://labuladong.online/algo/data-structure/implement-calculator/)
- [拓展：两个二叉堆实现中位数算法](https://labuladong.online/algo/practice-in-action/find-median-from-data-stream/)
- [拓展：数组去重问题（困难版）](https://labuladong.online/algo/frequency-interview/remove-duplicate-letters/)

#### 阶段3: 实战题目 (practice)
- [380] 380. Insert Delete GetRandom O(1) ⭐
  - **核心思路**: 用一个map记录value/index，用一个list记录values，insert，如果map中不存在，将values加入list，value，list.size-1加入map，remove，如果map中存在，取出index，将最后一个元素放到list的index位置，remove value/index from map，getRandom，拿一个随机数（1-list，size），从list中取数。
  - **链接**: https://leetcode.com/problems/insert-delete-getrandom-o1/description/

- [382] 382. Linked List Random Node ⭐
  - **核心思路**: index=0,random (index)==0 替换新值,
  - **链接**: https://leetcode.com/problems/linked-list-random-node/

- [384] 384. Shuffle an Array
  - **核心思路**: 用p=i+random（length-i），来确定要不要swap，i和p
  - **链接**: https://leetcode.com/problems/shuffle-an-array/

- [398] 398. Random Pick Index
  - **核心思路**: 等于target的来记录index，按照ramdom（index）来选择是不是选当前i。
  - **链接**: https://leetcode.com/problems/random-pick-index/

- [528] 528. Random Pick with Weight ⭐
  - **核心思路**: 前缀和，然后[1, presum[n-1]]中找随机数，二分查找找最右侧。
  - **链接**: https://leetcode.com/problems/random-pick-with-weight/

- [895] 895. 最大频率栈 ⭐
  - **核心思路**: 记录最大freq，记录val to freq map，记录freq对应的stack。push的时候改频率，改map，压栈，如果最大freq变了，改最大freq，pop的时候用最大freq来找stack，找val，该map，如果最大freq变了，改freq。
  - **链接**: https://leetcode.com/problems/maximum-frequency-stack/


---

## 大分类 2: 搜索

### Focus Area 2.1: 回溯算法

#### 阶段1: 基础原理 (theory)
- [回溯算法解题套路框架](https://labuladong.online/algo/essential-technique/backtrack-framework/)
- [回溯算法秒杀所有排列/组合/子集问题](https://labuladong.online/algo/essential-technique/permutation-combination-subset-all-in-one/)
- [解答回溯算法/DFS算法的若干疑问](https://labuladong.online/algo/essential-technique/backtrack-vs-dfs/)

#### 阶段2: 实现代码 (implementation)
- [回溯算法实践：数独和 N 皇后问题](https://labuladong.online/algo/practice-in-action/sudoku-nqueue/)
- [一文秒杀所有岛屿题目](https://labuladong.online/algo/frequency-interview/island-dfs-summary/)
- [球盒模型：回溯算法穷举的两种视角](https://labuladong.online/algo/practice-in-action/two-views-of-backtrack/)
- [回溯算法实践：括号生成](https://labuladong.online/algo/practice-in-action/generate-parentheses/)
- [回溯算法实践：集合划分](https://labuladong.online/algo/practice-in-action/partition-to-k-equal-sum-subsets/)

#### 阶段3: 实战题目 (practice)
- [39] 39. Combination Sum
  - **核心思路**: start，sum，list visit（start）- 可以有重复
  - **链接**: https://leetcode.com/problems/combination-sum/

- [40] 40. Combination Sum II
  - **核心思路**: 排序，通过nums[i]==nums[i-1]来剪枝，start，sum，list visit （start+1）
  - **链接**: https://leetcode.com/problems/combination-sum-ii/

- [46] 46. Permutations
  - **核心思路**: 回溯，used记录visited，用一个list来visit，add，visit next，remove
  - **链接**: https://leetcode.com/problems/permutations/

- [47] 47. Permutations II ⭐
  - **核心思路**: 1，n，通过i>0 && nums[i-1]==nums[i] && !used[i-1]来剪枝，用used来剪枝，visit
  - **链接**: https://leetcode.com/problems/permutations-ii/

- [77] 77. Combinations
  - **核心思路**: start visit（start+1）=保证不重复
  - **链接**: https://leetcode.com/problems/combinations/

- [78] 78. Subsets
  - **核心思路**: start visit（start+1）保证不重复
  - **链接**: https://leetcode.com/problems/subsets/

- [90] 90. Subsets II
  - **核心思路**: start visit （start+1），通过nums[i]== nums[i-1]来剪枝
  - **链接**: https://leetcode.com/problems/subsets-ii/

- [216] 216. Combination Sum III
  - **核心思路**: start，sum, list, visit (start+1) - 保证不重复，
  - **链接**: https://leetcode.com/problems/combination-sum-iii/

- [37] 37. Sudoku Solver
  - **核心思路**: 看成一位数组，从0开始，i=index/9，j=index%9，判断是不是valid，行，列，当前3*3，是的话，赋值，index+1，visit，在复位
  - **链接**: https://leetcode.com/problems/sudoku-solver/

- [51] 51. N-Queens
  - **核心思路**: 用int[]来记录一组解，row == 9结束，不然判断是不是valid（col不能有值，对角不能有值），row++，继续。
  - **链接**: https://leetcode.com/problems/n-queens/

- [52] 52. N-Queens II ⭐
  - **核心思路**: 只是res+1，同51
  - **链接**: https://leetcode.com/problems/n-queens-ii/

- [93] 93. 复原 IP 地址
  - **核心思路**: 取1-3个字符，判断是不是合法，组成当前集合，知道字符串最后，如果正好有四个合法串，返回。
  - **链接**: https://leetcode.com/problems/restore-ip-addresses/

- [131] 131. 分割回文串
  - **核心思路**: backtrack，当前list，然后从start往后，拿到子字符串，判断是不是回文，是的话加在当前集合中，继续往下，直到到字符串最后。
  - **链接**: https://leetcode.com/problems/palindrome-partitioning/

- [301] 301. 删除无效的括号 ⭐
  - **核心思路**: pre string，当前括号要么用，要么不用，prestring到最后，判断是不是合法，比较长度，如果leftCount<0 剪枝。
  - **链接**: https://leetcode.com/problems/remove-invalid-parentheses/

- [784] 784. 字母大小写全排列
  - **核心思路**: 全排列，preString，preString到最后，加入res list，index，当前字符，如果是字符，大写，小写，index++。否则直接往下。
  - **链接**: https://leetcode.com/problems/letter-case-permutation/

- [1593] 1593. 拆分字符串使唯一子字符串的数目最大 ⭐
  - **核心思路**: 记录分好的字符串，从当前index到最大，判断是不是包含，未包含往下递归。到最后比较size。取最大的。
  - **链接**: https://leetcode.com/problems/split-a-string-into-the-max-number-of-unique-substrings/

- [1849] 1849. 将字符串拆分为递减的连续值
  - **核心思路**: 记录拆好的list，从当前index到最后，剪枝，不是递减为1，长度大于一半，
  - **链接**: https://leetcode.com/problems/splitting-a-string-into-descending-consecutive-values/

- [79] 79. 单词搜索
  - **核心思路**: 从第一个匹配开始，前后左右，考虑边界，考虑used。
  - **链接**: https://leetcode.com/problems/word-search/

- [473] 473. 火柴拼正方形 ⭐
  - **核心思路**: 分成4个相同子集，需要用memo来剪枝，将used数组变成二进制保存。
  - **链接**: https://leetcode.com/problems/matchsticks-to-square/

- [526] 526. 优美的排列 ⭐
  - **核心思路**: backtrack，used记录使用过的，for 1-n，start，start%i or i%start。
  - **链接**: https://leetcode.com/problems/beautiful-arrangement/

- [638] 638. 大礼包
  - **核心思路**: 获取真的便宜的special，start to last special，看当前special能不能用，backtrack继续start开始，直到都不能用，按正常价格买剩余的，比价最小值
  - **链接**: https://leetcode.com/problems/shopping-offers/

- [967] 967. 连续差相同的数字
  - **核心思路**: backtrack，从第一位开始，
  - **链接**: https://leetcode.com/problems/numbers-with-same-consecutive-differences/

- [980] 980. 不同路径 III
  - **核心思路**: 找到start，统计一共要走多少步，dfs，用used记录走过的，记录当前步数，当当前步数和总步数 相同，切为2时+1/
  - **链接**: https://leetcode.com/problems/unique-paths-iii/

- [996] 996. 平方数组的数目
  - **核心思路**: 全排列，将数字排序，用used来记录使用的，list，将list.size==nums.length做为计数,for 0-length，用used来剪枝，用i-1=i 以及i-1 not used来剪枝，用平方数来剪枝。
  - **链接**: https://leetcode.com/problems/number-of-squareful-arrays/

- [1219] 1219. 黄金矿工
  - **核心思路**: dfs 遍历，加visited
  - **链接**: https://leetcode.com/problems/path-with-maximum-gold/

- [1306] 1306. 跳跃游戏 III
  - **核心思路**: step=0，将当前下标加入队列，取出队列元素curr，如果等于target，返回step，+-arr[curr]，如果不超过边界，加入队列，step++
  - **链接**: https://leetcode.com/problems/jump-game-iii/

- [1457] 1457. 二叉树中的伪回文路径
  - **核心思路**: 用int[10]记录每个数字出现个数，回文序列不能出现2个以上的奇数的数字。遍历，当left=right=null，判断是不是回文序列，再left，right遍历，最后将当前数字 count—
  - **链接**: https://leetcode.com/problems/pseudo-palindromic-paths-in-a-binary-tree/

- [1723] 1723. 完成所有工作的最短时间 ⭐
  - **核心思路**: 和2305是一样的， 考虑对工作进行遍历，回溯哦，for 1-n （worker），剪枝，如果工作时间超过了之前的一个解，如果当前的工作量已经有之前的工人一样的了（job只要完成时不care具体工人的），给这个工作给当前工人，往下，回溯，不给这个工作。
  - **链接**: https://leetcode.com/problems/find-minimum-time-to-finish-all-jobs/

- [2305] 2305. 公平分发饼干
  - **核心思路**: 考虑对工作进行遍历，回溯，for 1-n （worker），剪枝，如果工作时间超过了之前的一个解，如果当前的工作量已经有之前的工人一样的了（job只要完成时不care具体工人的），给这个工作给当前工人，往下，回溯，不给这个工作。
  - **链接**: https://leetcode.com/problems/fair-distribution-of-cookies/


---

### Focus Area 2.2: BFS算法

#### 阶段1: 基础原理 (theory)
- [BFS 算法解题套路框架](https://labuladong.online/algo/essential-technique/bfs-framework/)

#### 阶段2: 实现代码 (implementation)
- （BFS实现通常在游戏中体现）

#### 阶段3: 实战题目 (practice)
- [752] 752. Open the Lock
  - **核心思路**: 做最少路径的，一般用广度优先。将start放入队列，step =0，遍历队列，如果和目的相同，返回，否则，找到下一个（过滤掉dead的），过滤掉已经viist到的，然后加入队列，step++，
  - **链接**: https://leetcode.com/problems/open-the-lock/

- [773] 773. Sliding Puzzle
  - **核心思路**: 还是广度优先，将start变成一个字符串，step = 0，放入队列，遍历当前队列中的值，如果是end状态，结束，否则找到下一个状态，过滤掉visited，然后加入队列，step++
  - **链接**: https://leetcode.com/problems/sliding-puzzle/

- [542] 542. 01 矩阵
  - **核心思路**: 将所有零都加入队列，ret数组也都为0，bfs，找到所有next，如果遇到非0，还没遍历到（没赋值），ret 数组对应位置 curr +1，最后遍历整个数组后返回。
  - **链接**: https://leetcode.com/problems/01-matrix/


---

## 大分类 3: 动规

### Focus Area 3.1: 动态规划基础

#### 阶段1: 基础原理 (theory)
- [动态规划解题套路框架](https://labuladong.online/algo/essential-technique/dynamic-programming-framework/)
- [动态规划设计：最长递增子序列](https://labuladong.online/algo/dynamic-programming/longest-increasing-subsequence/)
- [base case 和备忘录的初始值怎么定？](https://labuladong.online/algo/dynamic-programming/memo-fundamental/)
- [动态规划穷举的两种视角](https://labuladong.online/algo/dynamic-programming/two-views-of-dp/)
- [动态规划和回溯算法的思维转换](https://labuladong.online/algo/dynamic-programming/word-break/)
- [对动态规划进行空间压缩](https://labuladong.online/algo/dynamic-programming/space-optimization/)
- [最优子结构原理和 dp 数组遍历方向](https://labuladong.online/algo/dynamic-programming/faq-summary/)

#### 阶段2: 实现代码 (implementation)
- （动态规划基础的实现通常融入到具体问题中）

#### 阶段3: 实战题目 (practice)
- [53] 53. Maximum Subarray
  - **核心思路**: dp[nums.length+1], dp[0]=0, dp[i] = max(dp[i-1]+nums[i-1], nums[i-1]), get max dp
  - **链接**: https://leetcode.com/problems/maximum-subarray/

- [63] 63. Unique Paths II
  - **核心思路**: dp[0][0] =1; if (grid[i][j]==1), dp[i][j]=0; else grid[i][j] = grid[i-1][j] + grid[i][j-1]
  - **链接**: https://leetcode.com/problems/unique-paths-ii/description/

- [64] 64. Minimum Path Sum ⭐
  - **核心思路**: dp[i][j] = max(dp[i-1][j], dp[i][j-1])+grid[i][j]
  - **链接**: https://leetcode.com/problems/minimum-path-sum/

- [120] 120. Triangle
  - **核心思路**: **dp[0][0] = triangle[0][0], dp[i][j] = max(dp[i-1][j-1], dp[i-1][j]) + triangle[i][j] 可以通过从后往前来压缩到一维
  - **链接**: https://leetcode.com/problems/triangle/description/

- [174] 174. Dungeon Game
  - **核心思路**: dp[i][j]=Min(dp[i+1][j], dp[i][j+1]) - grid[i][j], d[i][j]≤0 d[i][j]=1
  - **链接**: https://leetcode.com/problems/dungeon-game/

- [509] 509. Fibonacci Number
  - **核心思路**: 从0，1开始循环
  - **链接**: https://leetcode.com/problems/fibonacci-number/

- [931] 931. Minimum Falling Path Sum
  - **核心思路**: 从第一行开始算，比较上一行三个方向的值，往下计算。
  - **链接**: https://leetcode.com/problems/minimum-falling-path-sum/

- [1262] 1262. Greatest Sum Divisible by Three
  - **核心思路**: dp0=0, dp1=MIN_VALUE, dp2=MIN_VALUE, 分别是被3除，余0，1，2的最大值，然后newdpi=（dpj+nums），dpi = max（newdpi，dpi）
  - **链接**: https://leetcode.com/problems/greatest-sum-divisible-by-three/description/

- [2320] 2320. 统计放置房子的方式数
  - **核心思路**: dp[i] = dp[i-1] + dp[i-2], dp[i]*dp[i] % base
  - **链接**: https://leetcode.com/problems/count-number-of-ways-to-place-houses/


---

### Focus Area 3.2: 子序列问题

#### 阶段1: 基础原理 (theory)
- [经典动态规划：编辑距离](https://labuladong.online/algo/dynamic-programming/edit-distance/)
- [动态规划设计：最大子数组](https://labuladong.online/algo/dynamic-programming/maximum-subarray/)
- [经典动态规划：最长公共子序列](https://labuladong.online/algo/dynamic-programming/longest-common-subsequence/)
- [动态规划之子序列问题解题模板](https://labuladong.online/algo/dynamic-programming/subsequence-problem/)

#### 阶段2: 实现代码 (implementation)
- （子序列问题的实现融入到原理讲解中）

#### 阶段3: 实战题目 (practice)
- [72] 72. Edit Distance
  - **核心思路**: dp[n+1][m+1], if char(i-1) == char(j-1), memo[0][j]=j, memo[i][0]=i, dp[i][j] = dp[i-1][j-1] else min(dp[i-1][j],dp[i][j-1], dp[i-1[j-1])+1)
  - **链接**: https://leetcode.com/problems/edit-distance/

- [300] 300. Longest Increasing Subsequence ⭐
  - **核心思路**: 两层循环，0-n，然后看0-i，nums[i]>nums[j]可以+1，比较最大值。最后找最大的dp
  - **链接**: https://leetcode.com/problems/longest-increasing-subsequence/

- [354] 354. Russian Doll Envelopes
  - **核心思路**: 先将信封按照宽度从大到小排序，同长度从小到大排序，然后求长度的最大自增子序列
  - **链接**: https://leetcode.com/problems/russian-doll-envelopes/

- [368] 368. Largest Divisible Subset
  - **核心思路**: ****dp[0] = {nums[0]}, dp[i], for (0-j) if nums[i] % last ele in dp[j] and size>current, {dp[j], nums[i]}, get max list.
  - **链接**: https://leetcode.com/problems/largest-divisible-subset/description/

- [583] 583. Delete Operation for Two Strings ⭐
  - **核心思路**: m+n-2*lcs
  - **链接**: https://leetcode.com/problems/delete-operation-for-two-strings/

- [712] 712. Minimum ASCII Delete Sum for Two Strings
  - **核心思路**: dp[n+1][m+1], dp[i][0] = dp[i-1][0]+char(i-1), dp[0][j] = dp[0][j-1]+char(j-1), if (char(i-1)==char(j-1)), dp[i][j] = d[i-1][j-1], else min (dp[i-1)[j] + char(i-1), dp[i][j-1] + char(j-1))
  - **链接**: https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/

- [718] 718. Maximum Length of Repeated Subarray
  - **核心思路**: dp[i][j] - if (i==0 or j==0) → nums1[i]==nums0[j], 1:0 else → if nums1[i]==nums2[j], dp[i-1][j-1]+1; find max dp[i][j]
  - **链接**: https://leetcode.com/problems/maximum-length-of-repeated-subarray/description/

- [1143] 1143. Longest Common Subsequence
  - **核心思路**: dp[n+1][m+1], if char(i-1)==char(j-1),  dp[i][j] = dp[i-1][j-1]+1 else max(dp[i-1][j], dp[i][j-1]), return dp[n][m]
  - **链接**: https://leetcode.com/problems/longest-common-subsequence/


---

### Focus Area 3.3: 背包问题

#### 阶段1: 基础原理 (theory)
- [经典动态规划：0-1 背包问题](https://labuladong.online/algo/dynamic-programming/knapsack1/)
- [经典动态规划：子集背包问题](https://labuladong.online/algo/dynamic-programming/knapsack2/)
- [经典动态规划：完全背包问题](https://labuladong.online/algo/dynamic-programming/knapsack3/)
- [背包问题的变体：目标和](https://labuladong.online/algo/dynamic-programming/target-sum/)

#### 阶段2: 实现代码 (implementation)
- （背包问题的实现融入到原理讲解中）

#### 阶段3: 实战题目 (practice)
- [322] 322. Coin Change
  - **核心思路**: 两层循环，外面是数额，内部是coins。从1开始算。(amount-coin[i]) +1, 做最小值比较
  - **链接**: https://leetcode.com/problems/coin-change/

- [416] 416. Partition Equal Subset Sum
  - **核心思路**: target = sum/2, for i = 1-nums.length, for j = target to 1, if (j-nums[i]≥0), dp[j] = dp[j] or dp[j-nums[i]]
  - **链接**: https://leetcode.com/problems/partition-equal-subset-sum/description/

- [494] 494. Target Sum
  - **核心思路**: goal = (target+sum)/2, 转化为416**
  - **链接**: https://leetcode.com/problems/target-sum/

- [518] 518. Coin Change II ⭐
  - **核心思路**: dp[0] = 1, for i= 1-nums.length, for j 1 to amount, if (j-nums[i]≥0), dp[j]=dp[j]+dp[j-nums[i]];
  - **链接**: https://leetcode.com/problems/coin-change-ii/

- [1049] 1049. Last Stone Weight II ⭐
  - **核心思路**: 背包问题，找到一半重量，然后看能不能不知道一个set离一般重量最接近，最后的结果是总重量-set重量*2， 0-n石头，一半重量到0，第i个石头可以选择用还是不用。
  - **链接**: https://leetcode.com/problems/last-stone-weight-ii/description/


---

### Focus Area 3.4: 博弈与游戏

#### 阶段1: 基础原理 (theory)
- [动态规划之最小路径和](https://labuladong.online/algo/dynamic-programming/minimum-path-sum/)
- [旅游省钱大法：加权最短路径](https://labuladong.online/algo/dynamic-programming/cheap-travel/)
- [多源最短路径：Floyd 算法](https://labuladong.online/algo/data-structure/floyd/)
- [经典动态规划：正则表达式](https://labuladong.online/algo/dynamic-programming/regular-expression-matching/)
- [经典动态规划：高楼扔鸡蛋](https://labuladong.online/algo/dynamic-programming/egg-drop/)
- [经典动态规划：戳气球](https://labuladong.online/algo/dynamic-programming/burst-balloons/)
- [经典动态规划：博弈问题](https://labuladong.online/algo/dynamic-programming/game-theory/)

#### 阶段2: 实现代码 (implementation)
- [动态规划帮我通关了《魔塔》](https://labuladong.online/algo/dynamic-programming/magic-tower/)
- [动态规划帮我通关了《辐射4》](https://labuladong.online/algo/dynamic-programming/freedom-trail/)

#### 阶段3: 实战题目 (practice)
- [877] 877. Stone Game
  - **核心思路**: 永远是true，看准奇数或者偶数堆。
  - **链接**: https://leetcode.com/problems/stone-game/

- [2140] 2140. 解决智力问题 ⭐
  - **核心思路**: dp[i] = max(dp[i+step+1]+power, dp[i+1])
  - **链接**: https://leetcode.com/problems/solving-questions-with-brainpower/


---

### Focus Area 3.5: 经典DP问题

#### 阶段1: 基础原理 (theory)
- [一个方法团灭 LeetCode 打家劫舍问题](https://labuladong.online/algo/dynamic-programming/house-robber/)
- [一个方法团灭 LeetCode 股票买卖问题](https://labuladong.online/algo/dynamic-programming/stock-problem-summary/)

#### 阶段2: 实现代码 (implementation)
- （经典DP问题的实现融入到原理讲解中）

#### 阶段3: 实战题目 (practice)
- [198] 198. House Robber
  - **核心思路**: dp[i] = max(dp[i-2]+nums[i], dp[i-1])
  - **链接**: https://leetcode.com/problems/house-robber/

- [213] 213. House Robber II
  - **核心思路**: dp[i] = max (dp(nums, 0, n-1), dp(nusm, 1, n-2))
  - **链接**: https://leetcode.com/problems/house-robber-ii/

- [337] 337. House Robber III ⭐
  - **核心思路**: dp[i] = max(do, notdo), do = val + left.left, left.right, right.left, right.rightnotdo = left, right.
  - **链接**: https://leetcode.com/problems/house-robber-iii/

- [740] 740. 删除并获得点数
  - **核心思路**: dp[10000], sum same number, dp[i] = max(nums[i]+dp[i-2], dp[i-1])
  - **链接**: https://leetcode.com/problems/delete-and-earn/

- [121] 121. Best Time to Buy and Sell Stock ⭐
  - **核心思路**: dp[i][k][0] = dp[i-1][k][0] , dp[i-1][k][1]+nums[k]dp[i][k][1]=dp[i-1][k][1], dp[i-1][k-1][0]-nums[k]
  - **链接**: https://leetcode.com/problems/best-time-to-buy-and-sell-stock/

- [343] 343. Integer Break ⭐
  - **核心思路**: dp[0]=0; dp[1]=1, for i - [2-n], for j - [1,i], dp[i] = max(j*max(dp[i-j], i-j))
  - **链接**: https://leetcode.com/problems/integer-break/description/

- [2611] 2611. 老鼠和奶酪
  - **核心思路**: order reward1[i]-reward2[i], find k max, use reward1, else reward2, sum.
  - **链接**: https://leetcode.com/problems/mice-and-cheese/


---

### Focus Area 3.6: 贪心算法

#### 阶段1: 基础原理 (theory)
- [贪心算法解题套路框架](https://labuladong.online/algo/essential-technique/greedy/)

#### 阶段2: 实现代码 (implementation)
- [老司机加油算法](https://labuladong.online/algo/frequency-interview/gas-station-greedy/)
- [贪心算法之区间调度问题](https://labuladong.online/algo/frequency-interview/interval-scheduling/)
- [扫描线技巧：安排会议室](https://labuladong.online/algo/frequency-interview/scan-line-technique/)
- [剪视频剪出一个贪心算法](https://labuladong.online/algo/frequency-interview/cut-video/)

#### 阶段3: 实战题目 (practice)
- [45] 45. Jump Game II
  - **核心思路**: 计算下一个next，当前nums[i]+i的最大值，然后step++
  - **链接**: https://leetcode.com/problems/jump-game-ii/

- [55] 55. Jump Game ⭐
  - **核心思路**: 确保i < max(nums[i]+i)
  - **链接**: https://leetcode.com/problems/jump-game/

- [134] 134. Gas Station ⭐
  - **核心思路**: 计算preSum，最小的presum要大于0，最小的presum的那个index就是target
  - **链接**: https://leetcode.com/problems/gas-station/

- [135] 135. Candy
  - **核心思路**: 先都给一粒糖，从左到右，如果右边的大，右边的糖=当前+1，从右到左，如果左边大，左边的糖等于max（当前+1，左边），再遍历拿到total
  - **链接**: https://leetcode.com/problems/candy/description/

- [402] 402. 移掉 K 位数字 ⭐
  - **核心思路**: 让数字单调增，如果不是就是删除的位，直到k==0，如果0，那么继续，首位不能为0，如果k还大于零，pop栈，如果空，返回0，最后反向排序。
  - **链接**: https://leetcode.com/problems/remove-k-digits/

- [435] 435. Non-overlapping Intervals
  - **核心思路**: 按照end排序，找到start ≥end count+1，然后新的end，length-count就是可以移除的
  - **链接**: https://leetcode.com/problems/non-overlapping-intervals/

- [452] 452. Minimum Number of Arrows to Burst Balloons ⭐
  - **核心思路**: 有多少非重复区域，就需要多少箭，注意=也是可以的。
  - **链接**: https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/

- [502] 502. IPO
  - **核心思路**: 将profits，capital转为project数组，按照capital排序，按照profits放入一个Priority queue，最大堆，记录当前资金，将项目capital 《当前资金的放入queue，取出最大profits，count++，直到取出k的project。返回资金值
  - **链接**: https://leetcode.com/problems/ipo/description/

- [1024] 1024. Video Stitching ⭐
  - **核心思路**: 按照begin从小到大排序，end从大到小排序，begin需要等于0，然后是最大的end，再看begin《end的那些中的最大
  - **链接**: https://leetcode.com/problems/video-stitching/

- [2073] 2073. 买票需要的时间
  - **核心思路**: 排在前面的人，最多买到tickets[k]张票，之后的人最多买到tickets[k]-1的票
  - **链接**: https://leetcode.com/problems/time-needed-to-buy-tickets/


---

## 大分类 4: 其他

### Focus Area 4.1: 数学技巧

#### 阶段1: 基础原理 (theory)
- [一行代码就能解决的算法题](https://labuladong.online/algo/frequency-interview/one-line-solutions/)
- [常用的位操作](https://labuladong.online/algo/frequency-interview/bitwise-operation/)
- [必知必会数学技巧](https://labuladong.online/algo/essential-technique/math-techniques-summary/)
- [谈谈游戏中的随机算法](https://labuladong.online/algo/frequency-interview/random-algorithm/)
- [讲两道常考的阶乘算法题](https://labuladong.online/algo/frequency-interview/factorial-problems/)
- [如何高效寻找素数](https://labuladong.online/algo/frequency-interview/print-prime-number/)
- [如何同时寻找缺失和重复的元素](https://labuladong.online/algo/frequency-interview/mismatch-set/)
- [几个反直觉的概率问题](https://labuladong.online/algo/frequency-interview/probability-problem/)

#### 阶段2: 实现代码 (implementation)
- [【游戏】扫雷游戏地图生成器](https://labuladong.online/algo/game/minesweeper/)

#### 阶段3: 实战题目 (practice)
- [9] 9. Palindrome Number
  - **核心思路**: **转为字符串，用left，right指针判断
  - **链接**: https://leetcode.com/problems/palindrome-number/

- [50] 50. Pow(x, n)
  - **核心思路**: n → long, n<0, 1/pow(x,-n), n%2==1, x&pow(x, n-1) else if (n> 1), pow(x,n/2)*pow(x, n/2) else 1.
  - **链接**: https://leetcode.com/problems/powx-n/description/

- [67] 67. Add Binary
  - **核心思路**: ****从末尾开始，posA，posB，通过inc来计算要不要进位，如果一个结束了，那么就算另一个。
  - **链接**: https://leetcode.com/problems/add-binary/description/

- [137] 137. Single Number II
  - **核心思路**: 建立两个数组都是10000，那num/10000作为index，一个放次数，一个放value，然后遍历这个数组，去除count为1的index，返回对应值
  - **链接**: https://leetcode.com/problems/single-number-ii/description/

- [149] 149. Max Points on a Line
  - **核心思路**: 两重循环，i - 0-n-1，j - i+1-n，i循环的时候设置count map，计算x1-x2，y1-y2的最大公约数，将i/gcd + “ ” + j/gcd存入count map，如果有相同的，count++，计算max count
  - **链接**: https://leetcode.com/problems/max-points-on-a-line/description/

- [201] 201. Bitwise AND of Numbers Range
  - **核心思路**: 先将right和right-1取并，变成新的right，然后将right和left取并。
  - **链接**: https://leetcode.com/problems/bitwise-and-of-numbers-range/description/

- [204] 204. Count Primes
  - **核心思路**: 开始所有都是prime=true，从2开始到i*i<n, 对于i的话i*i 之后step i的都不是。计算prime=true的个数。
  - **链接**: https://leetcode.com/problems/count-primes/

- [263] 263. Ugly Number
  - **核心思路**: 是不是除于2/3/5之后最后变成1.
  - **链接**: https://leetcode.com/problems/ugly-number/

- [264] 264. Ugly Number II
  - **核心思路**: 记录下2，3，5分别用到了第几个数，比较2*ugly[count2],3*ugly[count3],5*ugly[count5], 最小的就是后续的数字，increase count，index计数
  - **链接**: https://leetcode.com/problems/ugly-number-ii/

- [313] 313. Super Ugly Number ⭐
  - **核心思路**: 用一个PriorityQueue来记录候选值，当前index，当前prime，不断的合并队列，过滤重复的。
  - **链接**: https://leetcode.com/problems/super-ugly-number/

- [372] 372. Super Pow
  - **核心思路**: 先计算a % 1337，然后计算first，first = a^b[length-1], 再计算second（递归）^10,first*second%1337
  - **链接**: https://leetcode.com/problems/super-pow/

- [791] 791. Custom Sort String
  - **核心思路**: 将每个字符的数量纪录下来，用order的顺序append字符，并且将count降为0，然后再将技术不为0的char append到最后
  - **链接**: https://leetcode.com/problems/custom-sort-string/description/


---

### Focus Area 4.2: 经典面试题

#### 阶段1: 基础原理 (theory)
- [如何高效解决接雨水问题](https://labuladong.online/algo/frequency-interview/trapping-rain-water/)
- [一文秒杀所有丑数系列问题](https://labuladong.online/algo/frequency-interview/ugly-number-summary/)
- [一个方法解决三道区间问题](https://labuladong.online/algo/practice-in-action/interval-problem-summary/)

#### 阶段2: 实现代码 (implementation)
- [谁能想到，斗地主也能玩出算法](https://labuladong.online/algo/practice-in-action/split-array-into-consecutive-subsequences/)
- [烧饼排序算法](https://labuladong.online/algo/frequency-interview/pancake-sorting/)
- [字符串乘法计算](https://labuladong.online/algo/practice-in-action/multiply-strings/)
- [如何判定完美矩形](https://labuladong.online/algo/frequency-interview/perfect-rectangle/)

#### 阶段3: 实战题目 (practice)
- [5] 5. Longest Palindromic Substring ⭐
  - **核心思路**: 找到i，j往外扩展的palindrom string，然后遍历数组
  - **链接**: https://leetcode.com/problems/longest-palindromic-substring/

- [6] 6. Zigzag Conversion
  - **核心思路**: 记录每行字符串，记录一个方向，+，i++，-，i—，如果当前为0或者numRows-1，改变方向
  - **链接**: https://leetcode.com/problems/zigzag-conversion/

- [28] 28. Find the Index of the First Occurrence in a String
  - **核心思路**: ** index=0 - haystack-needle，如果当前字符是needle第一个字符，substring 看看是不是符合needle，否则继续。
  - **链接**: https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/description/

- [58] 58. Length of Last Word
  - **核心思路**: **先trim，从最后一个字符开始，找到第一个空格，length++
  - **链接**: https://leetcode.com/problems/length-of-last-word/description/

- [68] 68. Text Justification
  - **核心思路**: 先分组，在对于每组单词进行format，分为一个单词和最后一行一种情况，以及多个单词一种情况。
  - **链接**: https://leetcode.com/problems/text-justification/description/

- [125] 125. Valid Palindrome
  - **核心思路**: 字符串处理后，left，right 指针判断
  - **链接**: https://leetcode.com/problems/valid-palindrome/description/

- [151] 151. Reverse Words in a String
  - **核心思路**: 先reverse，然后再将word reverse回来，去除空格
  - **链接**: https://leetcode.com/problems/reverse-words-in-a-string/

- [392] 392. Is Subsequence
  - **核心思路**: 找到两个postion，取出s的char，在t中找到对应的char，t++，s++，如果最后s position等于s长度，返回true。
  - **链接**: https://leetcode.com/problems/is-subsequence/

- [427] 427. Construct Quad Tree
  - **核心思路**: 分为topLeft（0，0，n/2，n/2）topright（0，n/2，0，n），（n/2，0，n，n/2）bottomLeft，（n/2，n/2，n，n）bottomRight，四个区域，拿到Node，如果四个node都是isLeaf，而且val相同，那么返回leaf node with val。如果n==1，那么返回leaf node with gird[top][left], 否则leaf=false，赋值四个child。
  - **链接**: https://leetcode.com/problems/construct-quad-tree/description/

- [973] 973. K Closest Points to Origin
  - **核心思路**: 将x*x+y*y进行排序，取出前K个，可以用Priority Queue，好处是可以streaming
  - **链接**: https://leetcode.com/problems/k-closest-points-to-origin/description/

- [30] 30. Substring with Concatenation of All Words ⭐
  - **核心思路**: 记录单词长度，总长度（单词长度*word length），从0-word 长度都可以开始，用一个hashmap记录target word count，slide window map，如果发现当前的word count大于target word count，缩小window （left+word count），如果count == target count，记录left，否则 增加right长度，如果当前word没在target中，那么 left = right，清除slide window map，count为0.
  - **链接**: https://leetcode.com/problems/substring-with-concatenation-of-all-words/description/

- [1] 1. Two Sum
  - **核心思路**: 比较长度，记录最长的那个。
  - **链接**: https://leetcode.com/problems/two-sum/

- [9] 9. Palindrome Number
  - **核心思路**: **转为字符串，用left，right指针判断
  - **链接**: https://leetcode.com/problems/palindrome-number/


---

## 核心框架 (算法模版 - 独立板块)

> 这部分不属于Focus Area，而是快速参考的算法模版库

### 学习方法论

- [本章导读](https://labuladong.online/algo/intro/core-intro/)
- [学习数据结构和算法的框架思维](https://labuladong.online/algo/essential-technique/algorithm-summary/)
- [一个视角 + 两种思维模式搞定递归](https://labuladong.online/algo/essential-technique/understand-recursion/)

### 复杂度分析

- [本章导读](https://labuladong.online/algo/intro/data-structure-basic/)
- [时间空间复杂度入门](https://labuladong.online/algo/intro/complexity-basic/)
- [算法时空复杂度分析实用指南](https://labuladong.online/algo/essential-technique/complexity-analysis/)

### 双指针模版

- [双指针技巧秒杀七道链表题目](https://labuladong.online/algo/essential-technique/linked-list-skills-summary/)
- [双指针技巧秒杀七道数组题目](https://labuladong.online/algo/essential-technique/array-two-pointers-summary/)

### 滑动窗口模版

- [滑动窗口算法核心代码模板](https://labuladong.online/algo/essential-technique/sliding-window-framework/)

### 二分搜索模版

- [二分搜索算法核心代码模板](https://labuladong.online/algo/essential-technique/binary-search-framework/)

### 回溯算法模版

- [回溯算法解题套路框架](https://labuladong.online/algo/essential-technique/backtrack-framework/)
- [回溯算法秒杀所有排列/组合/子集问题](https://labuladong.online/algo/essential-technique/permutation-combination-subset-all-in-one/)

### BFS算法模版

- [BFS 算法解题套路框架](https://labuladong.online/algo/essential-technique/bfs-framework/)

### 动态规划模版

- [动态规划解题套路框架](https://labuladong.online/algo/essential-technique/dynamic-programming-framework/)

### 贪心算法模版

- [贪心算法解题套路框架](https://labuladong.online/algo/essential-technique/greedy/)

### 分治算法模版

- [分治算法解题套路框架](https://labuladong.online/algo/essential-technique/divide-and-conquer/)

---

## 统计信息

### 架构对比

| 版本 | 大分类 | Focus Area | 学习阶段 | 文章数 |
|------|--------|-----------|---------|--------|
| v1.0 (合并前) | 6 | 33 | 固定3阶段 | 225 |
| **v2.0 (当前)** | **4 + 核心框架** | **25 + 模版库** | **Skill级别自定义** | **225** |

### 三段式学习结构

每个Focus Area包含：

1. **阶段1: 基础原理** (theory)
   - 数据结构基本概念
   - 算法思维框架
   - 时间空间复杂度

2. **阶段2: 实现代码** (implementation)
   - 代码实现和API设计
   - 算法技巧和优化
   - 模板代码

3. **阶段3: 实战题目** (practice)
   - LeetCode题目练习
   - 游戏实战项目
   - 综合习题

### 核心改进

**v1.0 → v2.0主要变化**:
- ✅ 4个主分类（删除"核心刷题框架"和"基础篇"）
- ✅ 核心框架作为独立"算法模版"板块
- ✅ 每个Focus Area采用三段式学习路径
- ✅ 支持Skill级别自定义学习阶段（未来扩展：系统设计 = 案例→模版→权衡）
- ✅ 统一管理所有学习内容（文章、视频、代码、题目）

### 数据库导入准备

**learning_stages 初始数据** (编程与数据结构):
```sql
INSERT INTO learning_stages (skill_id, stage_name, stage_type, description, sort_order) VALUES
(1, '基础原理', 'theory', '数据结构的基本概念、特点、适用场景', 1),
(1, '实现代码', 'implementation', '数据结构的代码实现、API设计、算法技巧', 2),
(1, '实战题目', 'practice', 'LeetCode题目练习、算法应用', 3);
```

**learning_contents 示例**:
```sql
-- 阶段1: 基础原理
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author) VALUES
(1, 1, 'article', '数组（顺序存储）基本原理', 'https://labuladong.online/algo/data-structure-basic/array-basic/', 'labuladong');

-- 阶段2: 实现代码
INSERT INTO learning_contents (focus_area_id, stage_id, content_type, title, url, author, content_data) VALUES
(1, 2, 'code_example', '双指针技巧秒杀七道数组题目', 'https://labuladong.online/algo/essential-technique/array-two-pointers-summary/', 'labuladong',
'{"language": "java", "notes": "双指针技巧适用于数组、链表等场景"}');

-- 阶段3: 实战题目 (通过questions表的stage_id关联)
UPDATE questions SET stage_id = 3 WHERE title LIKE '%数组%';
```

---

**文档版本**: v2.0
**创建时间**: 2025-12-22
**最后更新**: 2025-12-22
**状态**: 三段式结构完成 ✅
**下一步**: 创建数据库设计文档 + Migration V7脚本
