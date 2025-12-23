# 数据导入脚本

## 算法模版库导入

### 数据来源

- **来源**: [labuladong 的算法小抄](https://labuladong.online/algo/home/)
- **章节**: 第零章、核心刷题框架汇总
- **内容**: 10个核心算法模版
  1. 滑动窗口算法模版
  2. 二叉树遍历框架
  3. 回溯算法框架
  4. 动态规划框架
  5. BFS算法框架
  6. 快慢指针 - 原地修改数组
  7. 左右指针 - 二分查找
  8. 左右指针 - 回文串判断
  9. 贪心算法框架
  10. DFS深度优先遍历

### 使用方法

#### 1. 安装依赖

```bash
pip install pymysql python-dotenv
```

#### 2. 确认数据库配置

脚本会自动从 `backend/.env` 读取数据库配置：
- `DB_HOST`: 数据库地址（默认: 10.0.0.7）
- `DB_PORT`: 端口（默认: 37719）
- `DB_USER`: 用户名（默认: austinxu）
- `DB_PASSWORD`: 密码（默认: helloworld）
- `DB_NAME`: 数据库名（默认: growing）

#### 3. 运行导入脚本

```bash
# 从项目根目录运行
python3 import/import_algorithm_templates.py
```

#### 4. 预期输出

```
============================================================
算法模版库导入脚本
来源: labuladong算法小抄 - 第零章核心框架
============================================================

📡 连接数据库...
✅ 已连接到 10.0.0.7:37719/growing

👤 管理员用户ID: 1

📚 准备导入 10 个算法模版...

✅ [1/10] 导入成功: 滑动窗口算法模版
✅ [2/10] 导入成功: 二叉树遍历框架
✅ [3/10] 导入成功: 回溯算法框架
...

============================================================
导入完成!
✅ 成功导入: 10 个
⏭️  跳过已存在: 0 个
📊 总计: 10 个模版
============================================================
```

### 导入后验证

1. 登录管理员账号（austinxu / helloworld）
2. 访问 **设置 → 内容管理 → 算法模版库**
3. 应该能看到 10 个新导入的算法模版

### 数据结构

每个模版包含以下字段：

```json
{
  "title": "算法名称",
  "description": "简短描述",
  "content_type": "template",
  "author": "labuladong",
  "url": "原文链接",
  "contentData": {
    "language": "java/python",
    "code": "模版代码",
    "complexity": {
      "time": "时间复杂度",
      "space": "空间复杂度"
    },
    "keyPoints": [
      "关键点1",
      "关键点2"
    ],
    "sourceUrl": "原文链接"
  },
  "focus_area_id": null,
  "stage_id": null,
  "visibility": "public",
  "sort_order": 0
}
```

### 注意事项

1. **去重**: 脚本会检查模版是否已存在（根据 title），已存在的会跳过
2. **管理员**: 需要数据库中至少有一个 `role='admin'` 的用户
3. **数据库**: 确保数据库已运行 Flyway 迁移，`learning_contents` 表已创建
4. **stage_id**: 算法模版的 `stage_id` 和 `focus_area_id` 都为 NULL（已修复模型约束）

### 故障排查

**错误: 数据库连接失败**
- 检查 `backend/.env` 配置
- 确认 MySQL 服务运行在 10.0.0.7:37719

**错误: 未找到管理员用户**
- 运行 `SELECT * FROM users WHERE role='admin'` 检查管理员账号
- 如无管理员，需先通过注册页面创建并在数据库中手动设置 `role='admin'`

**错误: learning_contents 表不存在**
- 后端未运行 Flyway 迁移
- 启动后端服务（`./backend/start.sh`）触发自动迁移

### 扩展脚本

如需导入更多模版，编辑 `import_algorithm_templates.py` 中的 `TEMPLATES` 列表，添加新的模版数据。

### 版权声明

- 算法模版内容来自 [labuladong 的算法小抄](https://labuladong.online)
- 仅用于学习和个人项目，请遵守原作者的版权声明
- 商业使用前请联系原作者获取授权
