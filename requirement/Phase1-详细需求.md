# Phase 1 详细需求文档

## 1. 概述

**目标**: 构建 Growing 应用的基础用户管理系统和前端框架，为后续功能模块奠定基础。

**范围**: Phase 1 聚焦于最小可用产品（MVP），实现用户注册、登录、管理功能，以及基础的前端布局框架。

**版本**: v1.0.0

**完成时间**: 2025-12-20（已完成）

---

## 2. 功能需求

### 2.1 用户管理模块

#### 2.1.1 用户注册

**需求描述**:
新用户可以通过两种方式注册账号：传统注册和 Google OAuth 登录。

**传统注册功能点**:
1. 用户填写注册表单
   - 用户名（必填，3-50字符，唯一）
   - 邮箱（必填，有效邮箱格式，唯一）
   - 密码（必填，至少8位，包含字母和数字）
   - 确认密码（必填，需与密码一致）
   - 全名（选填，最多100字符）
   - 选择职业路径（必选，至少一个）

2. 表单验证
   - 前端实时验证（用户输入时）
   - 后端验证（提交时）
   - 用户名和邮箱唯一性检查
   - 密码强度检查

3. 注册成功
   - 创建用户账号
   - 使用 BCrypt 加密密码（强度10）
   - 关联用户选择的职业路径
   - 自动登录并跳转到仪表盘
   - 生成 JWT token（有效期24小时）

**Google OAuth 注册功能点**:
1. Google 登录按钮
   - 显示 "Sign in with Google" 按钮
   - 使用 Google One Tap 登录
   - Google Client ID: 85048175163-25v00bc8dflnpfq04hgsc5brls1fv4h2.apps.googleusercontent.com

2. OAuth 流程
   - 用户点击 Google 登录
   - Google 认证窗口弹出
   - 用户授权后返回 ID Token
   - 后端验证 Google ID Token
   - 提取用户信息（email, name）

3. 用户创建
   - 如果邮箱已存在，直接登录
   - 如果邮箱不存在，创建新用户：
     - 从邮箱提取用户名（去掉 @domain）
     - 如果用户名冲突，添加数字后缀
     - 设置随机密码（不可登录，仅 Google 登录）
     - 默认关联"软件工程师"职业路径
     - 默认角色为 "user"
   - 生成 JWT token 并自动登录

**验收标准**:
- ✅ 用户可以使用用户名/邮箱+密码成功注册
- ✅ 用户可以使用 Google 账号成功注册
- ✅ 表单验证错误有清晰提示
- ✅ 注册成功后自动登录并显示用户信息
- ✅ 用户名和邮箱重复时阻止注册
- ✅ Google 用户名冲突时自动添加后缀
- ✅ 密码安全存储（不可逆加密）

---

#### 2.1.2 用户登录

**需求描述**:
已注册用户可以通过传统方式或 Google OAuth 登录系统。

**传统登录功能点**:
1. 登录表单
   - 用户名或邮箱（必填）
   - 密码（必填）
   - "记住我"选项（可选，暂不实现）

2. 登录验证
   - 支持用户名或邮箱登录
   - 密码验证（BCrypt compare）
   - 生成 JWT token（有效期24小时）

3. 登录成功
   - 返回 JWT token 和用户信息
   - 前端存储 token 到 localStorage
   - 跳转到仪表盘
   - 显示欢迎消息

4. 登录失败
   - 显示错误消息："用户名或密码错误"
   - 不泄露具体错误原因（安全考虑）

**Google OAuth 登录功能点**:
1. Google 登录按钮
   - 同注册页面，显示 "Sign in with Google"
   - 使用相同的 Google Client ID

2. OAuth 流程
   - 用户点击 Google 登录
   - Google 认证并返回 ID Token
   - 后端验证 Google ID Token
   - 查找或创建用户（逻辑同注册）

3. 登录成功
   - 生成 JWT token
   - 返回用户信息（包括角色）
   - 自动跳转到仪表盘

**会话管理**:
- JWT token 存储在 localStorage
- 每次 API 请求自动携带 token（Authorization: Bearer {token}）
- token 过期后自动跳转登录页
- 前端路由守卫保护需要认证的页面

**验收标准**:
- ✅ 用户可以使用用户名/邮箱+密码成功登录
- ✅ 用户可以使用 Google 账号成功登录
- ✅ 登录失败显示友好错误消息
- ✅ 登录成功后可以访问受保护页面
- ✅ token 过期后自动退出
- ✅ Google 登录流程流畅，无需多次授权

---

#### 2.1.3 用户登出

**需求描述**:
用户可以安全退出系统，清除登录状态。

**功能点**:
1. 登出入口
   - 侧边栏用户信息区域的登出按钮
   - 点击显示登出图标（LogOut）

2. 登出流程
   - 清除 localStorage 中的 token
   - 清除 Pinia store 中的用户信息
   - 调用后端 /api/auth/logout（可选，记录日志）
   - 跳转到登录页

3. 安全考虑
   - 即使后端调用失败，前端仍清除本地状态
   - 防止 CSRF 攻击

**验收标准**:
- ✅ 用户点击登出后无法访问受保护页面
- ✅ 用户信息被完全清除
- ✅ 跳转到登录页

---

#### 2.1.4 用户管理（仅管理员）

**需求描述**:
管理员可以查看、创建、编辑和删除用户账号。

**角色权限**:
- **admin**: 可以访问用户管理页面，执行所有操作
- **user**: 无法访问用户管理页面，尝试访问时显示"权限不足"

**用户列表功能**:
1. 列表展示
   - 表格形式展示所有用户
   - 显示字段：
     - 用户名（username）
     - 邮箱（email）
     - 全名（fullName）
     - 角色（role）- 显示徽章（admin/user）
     - 职业路径（careerPaths）- 显示标签
     - 创建时间（createdAt）
   - 操作按钮：编辑、删除

2. 搜索和过滤（暂不实现）
   - 按用户名搜索
   - 按邮箱搜索
   - 按角色过滤

3. 排序（暂不实现）
   - 按创建时间排序
   - 按用户名排序

**创建用户功能**:
1. 创建表单
   - 用户名（必填）
   - 邮箱（必填）
   - 密码（必填）
   - 全名（选填）
   - 职业路径（必选，多选）
   - 角色（必选，admin/user，默认 user）

2. 表单验证
   - 同注册表单验证
   - 额外验证：角色必须是有效值

3. 创建成功
   - 用户列表自动刷新
   - 显示成功消息

**编辑用户功能**:
1. 编辑表单
   - 用户名（只读，不可修改）
   - 邮箱（只读，不可修改）
   - 全名（可修改）
   - 个人简介（bio）（可修改）
   - 职业路径（可修改）
   - 角色（可修改）

2. 更新成功
   - 用户列表自动刷新
   - 显示成功消息

**删除用户功能**:
1. 删除确认
   - 点击删除按钮
   - 弹出确认对话框："确定删除用户 {username} 吗？"
   - 用户确认后执行删除

2. 级联删除
   - 删除用户记录
   - 自动删除 user_career_paths 关联记录（数据库外键 ON DELETE CASCADE）

3. 删除成功
   - 用户列表自动刷新
   - 显示成功消息

**权限保护**:
1. 前端保护
   - 路由守卫：非管理员访问 /settings/users 时拦截
   - UI 隐藏：普通用户不显示"用户管理"菜单项
   - 用户信息区：管理员显示"管理员"徽章

2. 后端保护
   - 所有用户管理 API 需要管理员权限
   - 检查 JWT token 中的用户角色
   - 非管理员调用返回 403 错误

**验收标准**:
- ✅ 管理员可以查看所有用户列表
- ✅ 管理员可以创建新用户（包括设置角色）
- ✅ 管理员可以编辑用户信息
- ✅ 管理员可以删除用户
- ✅ 普通用户无法访问用户管理页面
- ✅ 普通用户看不到用户管理菜单
- ✅ 管理员在侧边栏显示"管理员"徽章
- ✅ 后端 API 正确验证管理员权限

---

#### 2.1.5 个人资料管理

**需求描述**:
用户可以查看和编辑自己的个人资料（暂不实现，Phase 2）。

**功能点**（待实现）:
- 查看个人资料
- 编辑基本信息（全名、bio）
- 修改职业路径
- 修改密码
- 上传头像

---

### 2.2 职业路径管理

#### 2.2.1 职业路径查询

**需求描述**:
系统提供预定义的职业路径供用户选择。

**功能点**:
1. 职业路径数据
   - 软件工程师（code: software_engineer, icon: Code）
   - 职业经理（code: manager, icon: Briefcase）

2. API 接口
   - GET /api/career-paths
   - 返回所有启用的职业路径
   - 包含字段：id, name, code, description, icon, isActive

3. 使用场景
   - 注册时选择职业路径
   - 创建用户时选择职业路径
   - 编辑用户时修改职业路径

**数据管理**:
- Phase 1 职业路径通过数据库初始化脚本管理
- 不提供前端管理界面
- 后续如需添加，直接在数据库插入数据

**验收标准**:
- ✅ API 正确返回职业路径列表
- ✅ 职业路径在注册/创建用户时可选择
- ✅ 用户可以选择多个职业路径

---

### 2.3 前端框架

#### 2.3.1 整体布局

**需求描述**:
构建统一的页面布局框架，包含侧边栏和主内容区。

**布局结构**:
```
┌─────────────────────────────────────────────────┐
│  用户信息区                                      │
│  - 用户头像字母                                  │
│  - 用户名 + 管理员徽章（如果是admin）              │
│  - 邮箱                                         │
│  - 登出按钮                                      │
├──────────────┬──────────────────────────────────┤
│  Logo        │                                  │
│  📚 Growing  │                                  │
├──────────────┤                                  │
│  顶级Tab     │                                  │
│  [学习]      │        Main Content Area        │
│  [求职]      │                                  │
│  [设置]      │                                  │
├──────────────┤                                  │
│  导航菜单     │                                  │
│  (动态显示)   │                                  │
│              │                                  │
├──────────────┤                                  │
│  Footer      │                                  │
│  v1.0.0      │                                  │
│  © 2025      │                                  │
└──────────────┴──────────────────────────────────┘
```

**功能点**:
1. 用户信息区
   - 显示用户头像（首字母圆形徽章）
   - 显示 displayName（优先 fullName，其次 username）
   - 显示 username（小字灰色）
   - 管理员用户显示"管理员"徽章（蓝色背景）
   - 登出按钮（悬停时变红色）

2. 侧边栏组件
   - 固定宽度 256px（desktop）
   - 响应式设计（tablet/mobile 可折叠）
   - 三个顶级 Tab：学习、求职、设置
   - Tab 切换时动态显示对应导航菜单

3. 主内容区
   - 占据剩余宽度
   - 白色背景
   - 内容区域内边距 32px

**响应式设计**:
- Desktop (>= 1024px): 完整侧边栏
- Tablet (768-1023px): 侧边栏宽度缩小到 224px
- Mobile (< 768px): 汉堡菜单（暂不实现）

**验收标准**:
- ✅ 侧边栏正确显示用户信息
- ✅ 管理员用户显示徽章
- ✅ 登出按钮功能正常
- ✅ 顶级 Tab 切换正确显示对应菜单
- ✅ 布局在不同屏幕尺寸下显示正常

---

#### 2.3.2 导航菜单

**需求描述**:
根据当前选中的顶级 Tab 显示对应的导航菜单项。

**学习 Tab 菜单**:
```
├─ 仪表盘
│
├─ 技能管理
│  ├─ 技能树
│  └─ 学习进度
│
├─ 学习模块（即将推出）
│
└─ 试题库（即将推出）
```

**求职 Tab 菜单**:
```
└─ 求职模块（即将推出）
```

**设置 Tab 菜单**:
```
├─ 用户管理（仅管理员）
│  └─ 用户管理
│
└─ 个人设置
   └─ 个人资料
```

**导航功能**:
1. 菜单项高亮
   - 当前路由匹配的菜单项高亮显示
   - 使用 router.path.startsWith() 匹配

2. 自动切换 Tab
   - 根据当前路由自动切换顶级 Tab
   - /settings/* → 设置 Tab
   - /career/* → 求职 Tab
   - 其他 → 学习 Tab

3. 权限控制
   - "用户管理"菜单项仅管理员可见（v-if="isAdmin"）

**验收标准**:
- ✅ 菜单项正确高亮当前页面
- ✅ Tab 自动切换到正确分类
- ✅ 普通用户不显示"用户管理"菜单
- ✅ 点击菜单项正确跳转

---

#### 2.3.3 路由配置

**需求描述**:
配置 Vue Router，实现页面导航和权限控制。

**路由列表**:
```javascript
/                    → 重定向到 /login
/login              → 登录页
/register           → 注册页
/dashboard          → 仪表盘
/skills/tree        → 技能树
/skills/progress    → 学习进度
/settings/users     → 用户管理（需要 admin 权限）
/settings/profile   → 个人资料
```

**路由守卫**:
1. 管理员权限检查
   - beforeEach 钩子
   - 检查 to.meta.requiresAdmin
   - 验证 isAuthenticated 和 isAdmin
   - 未登录 → 跳转登录页（带 redirect 参数）
   - 非管理员 → alert 提示 + 阻止导航

2. 认证检查（暂时宽松）
   - 当前实现：仅检查管理员权限
   - 未来实现：所有受保护路由都需登录

**验收标准**:
- ✅ 所有路由可以正确跳转
- ✅ 管理员权限路由受保护
- ✅ 未登录访问 admin 路由跳转登录页
- ✅ 普通用户访问 admin 路由被阻止

---

#### 2.3.4 仪表盘页面

**需求描述**:
提供应用首页，展示用户成长数据概览。

**页面内容**:
1. 欢迎信息
   - "欢迎回来，{displayName}！"

2. 数据卡片（3个）
   - 学习进度卡片（0% - 即将推出）
   - 试题练习卡片（0题 - 即将推出）
   - 目标公司卡片（0家 - 即将推出）

3. 快速访问（2个链接）
   - 技能树 → /skills/tree
   - 用户管理（仅管理员）→ /settings/users

**验收标准**:
- ✅ 正确显示用户名
- ✅ 数据卡片正确展示
- ✅ 快速访问链接可以跳转
- ✅ 普通用户不显示"用户管理"快速访问

---

### 2.4 技术实现细节

#### 2.4.1 认证机制

**JWT Token 设计**:
- 算法：HS384
- 有效期：24小时（86400秒）
- Claims：
  - sub: username
  - iat: 签发时间
  - exp: 过期时间
  - provider: "local" 或 "google"（可选）

**密码加密**:
- 算法：BCrypt
- 强度：10
- 存储：password_hash 字段（255字符）

**前端认证状态管理**:
- 使用 Composition API
- useAuth composable 提供：
  - isAuthenticated (computed)
  - currentUser (ref)
  - displayName (computed)
  - username (computed)
  - role (computed)
  - isAdmin (computed)
  - login(credentials)
  - register(userData)
  - googleLogin(credential)
  - logout()

**API 请求拦截**:
- Axios 请求拦截器自动添加 Authorization header
- Axios 响应拦截器处理 401 错误（token 过期）

---

#### 2.4.2 数据库设计

**users 表**:
```sql
id            BIGINT         主键，自增
username      VARCHAR(50)    唯一，用户名
email         VARCHAR(100)   唯一，邮箱
password_hash VARCHAR(255)   密码哈希
full_name     VARCHAR(100)   全名
avatar_url    VARCHAR(500)   头像URL
bio           TEXT           个人简介
role          VARCHAR(20)    角色（admin/user）
created_at    TIMESTAMP      创建时间
updated_at    TIMESTAMP      更新时间

索引:
- PRIMARY KEY (id)
- UNIQUE KEY (username)
- UNIQUE KEY (email)
- INDEX (username)
- INDEX (email)
- INDEX (role)
```

**career_paths 表**:
```sql
id          BIGINT         主键，自增
name        VARCHAR(100)   唯一，路径名称
code        VARCHAR(50)    唯一，路径代码
description TEXT           路径描述
icon        VARCHAR(50)    图标名称
is_active   TINYINT(1)     是否启用
created_at  TIMESTAMP      创建时间
updated_at  TIMESTAMP      更新时间

索引:
- PRIMARY KEY (id)
- UNIQUE KEY (name)
- UNIQUE KEY (code)
- INDEX (code)
- INDEX (is_active)
```

**user_career_paths 表**:
```sql
id              BIGINT      主键，自增
user_id         BIGINT      用户ID
career_path_id  BIGINT      职业路径ID
created_at      TIMESTAMP   创建时间

索引:
- PRIMARY KEY (id)
- UNIQUE KEY (user_id, career_path_id)
- INDEX (user_id)
- INDEX (career_path_id)
- FOREIGN KEY (user_id) → users(id) ON DELETE CASCADE
- FOREIGN KEY (career_path_id) → career_paths(id) ON DELETE CASCADE
```

---

#### 2.4.3 后端 API 规范

**统一响应格式**:
```json
// 成功
{
  "data": {...},
  "message": "Success"
}

// 失败
{
  "message": "Error description"
}
```

**HTTP 状态码**:
- 200: 成功
- 400: 客户端错误（验证失败、业务逻辑错误）
- 401: 未认证
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器错误

**安全机制**:
1. CORS 配置
   - 允许来源：http://localhost:3000
   - 允许方法：GET, POST, PUT, DELETE
   - 允许凭据：true

2. 输入验证
   - 使用 @Valid 注解验证请求体
   - DTO 字段验证（@NotBlank, @Email, @Size 等）
   - 密码强度验证

3. 错误处理
   - 统一异常处理
   - 不泄露敏感信息
   - 记录错误日志

---

## 3. 非功能性需求

### 3.1 性能要求

- 页面首次加载时间 < 2秒
- API 响应时间 < 500ms
- 前端路由切换 < 100ms
- 数据库查询优化（使用索引）

### 3.2 安全要求

- 密码使用 BCrypt 加密，不可逆
- JWT token 有效期 24小时
- Google OAuth 使用官方 SDK
- 所有用户输入进行验证和转义
- 管理员权限严格验证（前后端双重检查）
- HTTPS 部署（生产环境）

### 3.3 可用性要求

- 响应式设计，支持 Desktop/Tablet
- 表单验证错误提示友好
- 操作成功/失败有明确反馈
- 加载状态显示 loading 指示器

### 3.4 可维护性要求

- 代码结构清晰，符合最佳实践
- 组件复用性高
- API 文档完整
- 数据库字段有注释

---

## 4. 测试验收

### 4.1 功能测试

#### 用户注册
- [x] 传统注册成功
- [x] Google 注册成功
- [x] 用户名重复时阻止注册
- [x] 邮箱重复时阻止注册
- [x] Google 用户名冲突时自动添加后缀
- [x] 密码验证（长度、复杂度）
- [x] 注册成功后自动登录

#### 用户登录
- [x] 传统登录成功（用户名）
- [x] 传统登录成功（邮箱）
- [x] Google 登录成功
- [x] 密码错误时显示错误
- [x] 用户不存在时显示错误
- [x] 登录成功后跳转仪表盘

#### 用户管理（管理员）
- [x] 管理员可以查看用户列表
- [x] 管理员可以创建用户
- [x] 管理员可以编辑用户
- [x] 管理员可以删除用户
- [x] 普通用户无法访问用户管理
- [x] 前端隐藏用户管理菜单（普通用户）
- [x] 后端 API 拒绝非管理员请求

#### 前端框架
- [x] 侧边栏正确显示用户信息
- [x] 管理员徽章正确显示
- [x] Tab 切换正确
- [x] 菜单高亮正确
- [x] 路由跳转正确
- [x] 仪表盘数据显示正确

### 4.2 安全测试

- [x] 密码正确加密
- [x] JWT token 正确生成和验证
- [x] Google ID Token 正确验证
- [x] 管理员权限正确验证（前端）
- [x] 管理员权限正确验证（后端）
- [x] 非管理员无法调用管理 API

### 4.3 兼容性测试

- [x] Chrome 浏览器
- [ ] Firefox 浏览器（待测试）
- [ ] Safari 浏览器（待测试）
- [x] 响应式布局（Desktop 1920x1080）
- [ ] 响应式布局（Tablet 1024x768）（待测试）

---

## 5. 已完成功能清单

### 5.1 数据库
- [x] users 表创建（包含 role 字段）
- [x] career_paths 表创建
- [x] user_career_paths 表创建
- [x] 初始化职业路径数据
- [x] 数据库迁移脚本（003_add_user_role.sql）

### 5.2 后端 API
- [x] POST /api/auth/register（传统注册）
- [x] POST /api/auth/login（传统登录）
- [x] POST /api/auth/google（Google OAuth）
- [x] POST /api/auth/logout
- [x] GET /api/auth/me
- [x] GET /api/career-paths
- [x] GET /api/users（需要 admin）
- [x] GET /api/users/{id}（需要 admin）
- [x] POST /api/users（需要 admin）
- [x] PUT /api/users/{id}（需要 admin）
- [x] DELETE /api/users/{id}（需要 admin）
- [x] 管理员权限验证（AuthService.isAdminByToken）

### 5.3 前端页面
- [x] 登录页面（Login.vue）
- [x] 注册页面（Register.vue）
- [x] 仪表盘（Dashboard.vue）
- [x] 侧边栏（Sidebar.vue）
- [x] 用户管理（UserManagement.vue）
- [x] 个人资料（Profile.vue）- 基础框架
- [x] 技能树（SkillTree.vue）- 基础框架
- [x] 学习进度（LearningProgress.vue）- 基础框架

### 5.4 前端功能
- [x] useAuth composable（认证状态管理）
- [x] 路由配置和守卫
- [x] Axios 拦截器（自动添加 token）
- [x] Google OAuth 集成
- [x] 角色检查（isAdmin）
- [x] UI 权限控制（v-if="isAdmin"）

---

## 6. 技术栈总结

### 6.1 后端
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT（jjwt 0.12.3）
- Google API Client（google-api-client 2.0.0）
- BCrypt 密码加密

### 6.2 前端
- Vue 3（Composition API）
- Vue Router
- Vite
- Axios
- Tailwind CSS
- lucide-vue-next（图标）
- Google Identity Services（OAuth）

### 6.3 开发工具
- Maven
- npm
- MySQL Client
- Git

---

## 7. 后续优化方向（Phase 2+）

### 7.1 用户体验
- [ ] 邮箱验证
- [ ] 密码重置功能
- [ ] 头像上传
- [ ] 用户列表分页
- [ ] 用户搜索和过滤
- [ ] 表单自动保存草稿

### 7.2 安全增强
- [ ] 多因素认证（MFA）
- [ ] 登录日志
- [ ] 账户锁定（多次失败）
- [ ] CSRF Token
- [ ] Rate Limiting

### 7.3 功能扩展
- [ ] 技能体系管理
- [ ] 学习模块开发
- [ ] 试题库功能
- [ ] 公司管理
- [ ] 简历管理

---

## 8. 已知问题和技术债务

### 8.1 待解决问题
- 前端没有统一的错误处理机制
- 缺少加载状态指示器
- 缺少操作成功的 toast 提示

### 8.2 待优化项
- 用户列表需要分页（当前加载全部）
- 密码强度可视化指示器
- Google 登录错误处理可以更友好

### 8.3 文档待完善
- API 接口文档（Swagger）
- 前端组件文档
- 数据库 ER 图

---

**文档版本**: v1.0.0
**最后更新**: 2025-12-20
**状态**: Phase 1 已完成
