# Phase 1 设计文档：用户管理与前端框架

## 1. 数据库设计

### 1.1 用户表 (users)

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '电子邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    full_name VARCHAR(100) COMMENT '全名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

**字段说明：**

- `id`: 主键，自增
- `username`: 用户名，唯一，用于登录
- `email`: 邮箱，唯一，用于登录和通知
- `password_hash`: 密码哈希（使用 BCrypt）
- `full_name`: 用户全名
- `avatar_url`: 头像图片地址
- `bio`: 个人简介
- `created_at`: 账户创建时间
- `updated_at`: 最后更新时间

### 1.2 职业路径表 (career_paths)

```sql
CREATE TABLE career_paths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职业路径ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '路径名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '路径代码（如: software_engineer, manager）',
    description TEXT COMMENT '路径描述',
    icon VARCHAR(50) COMMENT '图标名称',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_code (code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业路径表';
```

**初始数据：**
```sql
INSERT INTO career_paths (name, code, description, icon) VALUES
('软件工程师', 'software_engineer', '软件开发、架构设计、技术管理等技术类职业', 'Code'),
('职业经理', 'manager', '项目管理、人员管理、战略规划等管理类职业', 'Briefcase');
```

### 1.3 用户职业路径关联表 (user_career_paths)

```sql
CREATE TABLE user_career_paths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    career_path_id BIGINT NOT NULL COMMENT '职业路径ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_career (user_id, career_path_id),
    INDEX idx_user_id (user_id),
    INDEX idx_career_path_id (career_path_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户职业路径关联表';
```

**设计说明：**

这样设计的优势：
1. **灵活性**：用户可以选择任意多个职业路径，不受限制
2. **可扩展性**：未来添加新的职业路径（如：数据科学家、产品经理）只需在 `career_paths` 表插入数据
3. **简洁性**：users 表保持简洁，只包含用户基本信息
4. **规范化**：符合数据库设计范式，避免冗余

### 1.4 数据初始化脚本

**database/init_data.sql** - 初始化职业路径数据

```sql
-- 插入初始职业路径数据
INSERT INTO career_paths (name, code, description, icon) VALUES
('软件工程师', 'software_engineer', '软件开发、架构设计、技术管理等技术类职业', 'Code'),
('职业经理', 'manager', '项目管理、人员管理、战略规划等管理类职业', 'Briefcase');
```

**说明：**
- 职业路径数据在数据库初始化时插入
- Phase 1 不需要职业路径管理界面
- 后续如需添加新职业路径，直接在数据库中插入即可

## 2. 前端 UI 框架设计

### 2.1 整体布局结构

参照 finance 项目，采用左侧边栏 + 右侧主内容区域的经典布局：

```
┌─────────────────────────────────────────────────┐
│  Logo & Brand                                   │
├──────────────┬──────────────────────────────────┤
│              │                                  │
│  Top Level   │                                  │
│  Tabs        │                                  │
│  (3 buttons) │                                  │
├──────────────┤        Main Content Area        │
│              │                                  │
│  Navigation  │                                  │
│  Menu        │                                  │
│  (Dynamic    │                                  │
│   based on   │                                  │
│   active     │                                  │
│   tab)       │                                  │
│              │                                  │
│              │                                  │
├──────────────┤                                  │
│  Footer      │                                  │
└──────────────┴──────────────────────────────────┘
```

### 2.2 侧边栏设计

#### 2.2.1 顶部 Logo 区域

```
┌───────────────────────┐
│  📚  Growing          │
│      Personal Growth  │
└───────────────────────┘
```

- 图标：📚 或其他代表成长的 emoji
- 标题：Growing
- 副标题：Personal Growth

#### 2.2.2 顶级分类 Tabs（3个）

类似 finance 的设计，使用横向 Tab 切换主要模块：

1. **学习 (Learning)** 📖
   - 技能管理
   - 学习模块
   - 试题库

2. **求职 (Career)** 💼
   - 公司管理
   - 简历管理
   - 面试准备

3. **设置 (Settings)** ⚙️
   - 用户管理
   - 系统配置

#### 2.2.3 导航菜单（动态内容）

**Tab 1: 学习 (Learning)**

```
├─ 仪表盘
│
├─ 技能管理
│  ├─ 技能树
│  └─ 学习进度
│
├─ 学习模块
│  ├─ 编程与数据结构 ⭐
│  ├─ 系统设计 ⭐
│  ├─ 云计算 ⭐
│  ├─ AI/机器学习 ⭐
│  ├─ Behavioral ⭐
│  └─ 人员管理 ⭐
│
└─ 试题库
   ├─ 全部试题
   ├─ 按技能分类
   └─ 练习记录
```

**Tab 2: 求职 (Career)**

```
├─ 公司管理
│  ├─ 公司列表
│  └─ 职位管理
│
├─ 简历管理
│  ├─ 我的简历
│  └─ 定制简历
│
└─ 面试准备
   ├─ 面试经验
   └─ 专项试题库
```

**Tab 3: 设置 (Settings)**

```
├─ 用户管理
│  └─ 用户列表
│
├─ 个人资料
│  ├─ 基本信息
│  └─ 职业路径
│
└─ 系统设置
   └─ 通用设置
```

#### 2.2.4 底部 Footer

```
┌───────────────────────┐
│  v1.0.0               │
│  © 2025 Growing       │
└───────────────────────┘
```

### 2.3 技术实现方案

#### 2.3.1 组件结构

```
frontend/src/
├── components/
│   ├── Sidebar.vue              # 侧边栏主组件
│   ├── MainLayout.vue           # 主布局组件
│   └── ui/                      # UI 基础组件库
│       ├── Card.vue
│       ├── Button.vue
│       ├── Input.vue
│       └── ...
│
├── views/
│   ├── Dashboard.vue            # 仪表盘
│   │
│   ├── skills/                  # 技能管理模块
│   │   ├── SkillTree.vue
│   │   └── LearningProgress.vue
│   │
│   ├── modules/                 # 学习模块
│   │   ├── ProgrammingModule.vue
│   │   ├── SystemDesignModule.vue
│   │   ├── CloudModule.vue
│   │   ├── AIModule.vue
│   │   ├── BehavioralModule.vue
│   │   └── ManagementModule.vue
│   │
│   ├── questions/               # 试题库
│   │   ├── QuestionList.vue
│   │   ├── QuestionBySkill.vue
│   │   └── PracticeHistory.vue
│   │
│   ├── companies/               # 公司管理
│   │   ├── CompanyList.vue
│   │   └── PositionManagement.vue
│   │
│   ├── resumes/                 # 简历管理
│   │   ├── MyResume.vue
│   │   └── CustomizedResumes.vue
│   │
│   ├── interview/               # 面试准备
│   │   ├── InterviewExperience.vue
│   │   └── TargetedQuestions.vue
│   │
│   └── settings/                # 设置
│       ├── UserManagement.vue
│       ├── Profile.vue
│       └── SystemSettings.vue
│
└── router/
    └── index.js                 # 路由配置
```

#### 2.3.2 路由设计

```javascript
const routes = [
  // 仪表盘
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: Dashboard },

  // 学习模块 - 技能管理
  { path: '/skills/tree', component: SkillTree },
  { path: '/skills/progress', component: LearningProgress },

  // 学习模块 - 学习模块
  { path: '/modules/programming', component: ProgrammingModule },
  { path: '/modules/system-design', component: SystemDesignModule },
  { path: '/modules/cloud', component: CloudModule },
  { path: '/modules/ai', component: AIModule },
  { path: '/modules/behavioral', component: BehavioralModule },
  { path: '/modules/management', component: ManagementModule },

  // 学习模块 - 试题库
  { path: '/questions', component: QuestionList },
  { path: '/questions/by-skill', component: QuestionBySkill },
  { path: '/questions/practice', component: PracticeHistory },

  // 求职模块 - 公司管理
  { path: '/companies', component: CompanyList },
  { path: '/companies/:id/positions', component: PositionManagement },

  // 求职模块 - 简历管理
  { path: '/resumes/my', component: MyResume },
  { path: '/resumes/customized', component: CustomizedResumes },

  // 求职模块 - 面试准备
  { path: '/interview/experience', component: InterviewExperience },
  { path: '/interview/questions', component: TargetedQuestions },

  // 设置模块
  { path: '/settings/users', component: UserManagement },
  { path: '/settings/profile', component: Profile },
  { path: '/settings/system', component: SystemSettings }
];
```

#### 2.3.3 设计系统

使用与 finance 项目相同的设计系统：

- **UI 框架**: 无（使用自定义组件）
- **样式**: Tailwind CSS
- **图标**: lucide-vue-next
- **颜色主题**:
  - Primary: 蓝色系（代表学习和成长）
  - Secondary: 绿色系（代表进步）
  - Accent: 橙色系（代表重要技能）

#### 2.3.4 响应式设计

- **Desktop** (>= 1024px): 完整侧边栏 + 主内容区
- **Tablet** (768px - 1023px): 可折叠侧边栏
- **Mobile** (< 768px): 汉堡菜单 + 全屏侧边栏（覆盖模式）

### 2.4 状态管理

使用 Vue 3 Composition API + Pinia（如需要）：

```javascript
// stores/user.js
export const useUserStore = defineStore('user', {
  state: () => ({
    currentUser: null,
    isAuthenticated: false
  }),
  actions: {
    async login(credentials) { ... },
    async logout() { ... },
    async fetchProfile() { ... }
  }
});
```

### 2.5 API 集成

```javascript
// api/user.js
export default {
  // 用户认证
  login(credentials) {
    return api.post('/auth/login', credentials);
  },

  // 用户管理
  getUsers() {
    return api.get('/users');
  },
  createUser(userData) {
    return api.post('/users', userData);
  },
  updateUser(id, userData) {
    return api.put(`/users/${id}`, userData);
  },
  deleteUser(id) {
    return api.delete(`/users/${id}`);
  }
};
```

## 3. 后端 API 设计

### 3.1 用户管理 API

**Base URL**: `/api/users`

#### 3.1.1 获取用户列表
```
GET /api/users
Response: {
  data: [
    {
      id: 1,
      username: "austin",
      email: "austin@example.com",
      fullName: "Austin Xu",
      avatarUrl: "https://...",
      bio: "Full-stack developer",
      careerPaths: [
        {
          id: 1,
          name: "软件工程师",
          code: "software_engineer",
          icon: "Code"
        },
        {
          id: 2,
          name: "职业经理",
          code: "manager",
          icon: "Briefcase"
        }
      ],
      createdAt: "2025-01-01T00:00:00Z",
      updatedAt: "2025-01-15T10:30:00Z"
    }
  ],
  total: 1
}
```

#### 3.1.2 获取单个用户
```
GET /api/users/{id}
Response: {
  id: 1,
  username: "austin",
  email: "austin@example.com",
  fullName: "Austin Xu",
  avatarUrl: "https://...",
  bio: "Full-stack developer",
  careerPaths: [
    {
      id: 1,
      name: "软件工程师",
      code: "software_engineer",
      icon: "Code"
    },
    {
      id: 2,
      name: "职业经理",
      code: "manager",
      icon: "Briefcase"
    }
  ],
  createdAt: "2025-01-01T00:00:00Z",
  updatedAt: "2025-01-15T10:30:00Z"
}
```

#### 3.1.3 创建用户
```
POST /api/users
Body: {
  username: "newuser",
  email: "newuser@example.com",
  password: "securepassword",
  fullName: "New User",
  careerPathIds: [1]  // 选择职业路径ID数组
}
Response: {
  id: 2,
  username: "newuser",
  email: "newuser@example.com",
  fullName: "New User",
  careerPaths: [
    {
      id: 1,
      name: "软件工程师",
      code: "software_engineer",
      icon: "Code"
    }
  ],
  createdAt: "2025-01-20T12:00:00Z"
}
```

#### 3.1.4 更新用户
```
PUT /api/users/{id}
Body: {
  fullName: "Updated Name",
  bio: "Updated bio",
  careerPathIds: [1, 2]  // 更新职业路径
}
Response: {
  id: 1,
  username: "austin",
  email: "austin@example.com",
  fullName: "Updated Name",
  bio: "Updated bio",
  careerPaths: [
    {
      id: 1,
      name: "软件工程师",
      code: "software_engineer",
      icon: "Code"
    },
    {
      id: 2,
      name: "职业经理",
      code: "manager",
      icon: "Briefcase"
    }
  ],
  updatedAt: "2025-01-20T12:30:00Z"
}
```

#### 3.1.5 删除用户
```
DELETE /api/users/{id}
Response: {
  message: "User deleted successfully"
}
```

### 3.2 职业路径查询 API

**Base URL**: `/api/career-paths`

#### 3.2.1 获取所有职业路径
```
GET /api/career-paths
Response: {
  data: [
    {
      id: 1,
      name: "软件工程师",
      code: "software_engineer",
      description: "软件开发、架构设计、技术管理等技术类职业",
      icon: "Code",
      isActive: true
    },
    {
      id: 2,
      name: "职业经理",
      code: "manager",
      description: "项目管理、人员管理、战略规划等管理类职业",
      icon: "Briefcase",
      isActive: true
    }
  ]
}
```

**说明：**
- Phase 1 只实现查询功能，用于用户选择职业路径
- 不实现创建/更新/删除职业路径的 API
- 职业路径数据通过数据库初始化脚本管理

### 3.3 认证 API

**Base URL**: `/api/auth`

#### 3.3.1 登录
```
POST /api/auth/login
Body: {
  username: "austin",
  password: "password"
}
Response: {
  token: "eyJhbGciOiJIUzI1NiIs...",
  user: {
    id: 1,
    username: "austin",
    email: "austin@example.com",
    fullName: "Austin Xu"
  }
}
```

#### 3.3.2 登出
```
POST /api/auth/logout
Response: {
  message: "Logged out successfully"
}
```

#### 3.3.3 获取当前用户
```
GET /api/auth/me
Response: {
  id: 1,
  username: "austin",
  email: "austin@example.com",
  fullName: "Austin Xu",
  careerPaths: [
    {
      id: 1,
      name: "软件工程师",
      code: "software_engineer",
      icon: "Code"
    },
    {
      id: 2,
      name: "职业经理",
      code: "manager",
      icon: "Briefcase"
    }
  ]
}
```

## 4. 实现优先级

### Phase 1.1 - 基础框架
1. 数据库初始化
   - 创建 users, career_paths, user_career_paths 表
   - 执行数据初始化脚本（插入职业路径数据）
2. 后端职业路径查询 API（GET /api/career-paths）
3. 后端用户 CRUD API
4. 前端基础布局（MainLayout + Sidebar）
5. 路由配置

### Phase 1.2 - 用户管理
1. 用户列表页面
2. 创建/编辑用户表单（包含职业路径选择）
3. 用户详情页面
4. 前后端集成测试

### Phase 1.3 - 认证系统（简化版）
1. 登录/注册页面（基础UI）
   - 登录表单
   - 注册表单
   - 简单的前端验证

**暂不实现：**
- JWT 认证机制（待定）
- 路由守卫
- 会话管理

## 5. 待完成功能（Phase 2+）

- 技能体系管理
- 学习模块开发
- 试题库功能
- 公司和简历管理
- 数据可视化
- 权限管理
- 邮件验证

## 6. 技术债务和注意事项

1. **密码安全**: 使用 BCrypt 进行密码哈希，强度至少为 10
2. **XSS 防护**: 前端输入必须进行转义
3. **CSRF 防护**: 使用 CSRF Token 或 SameSite Cookie
4. **输入验证**: 前后端双重验证所有用户输入
5. **错误处理**: 统一错误响应格式
6. **日志记录**: 记录所有用户操作（特别是敏感操作）
7. **数据备份**: 定期备份用户数据

## 7. 后续优化方向

1. **多因素认证 (MFA)**
2. **社交登录集成**
3. **用户头像上传**
4. **邮箱验证流程**
5. **密码重置功能**
6. **账户安全日志**
7. **用户行为分析**
