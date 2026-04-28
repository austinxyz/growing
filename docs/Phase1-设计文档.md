# Phase 1 设计文档：用户管理与前端框架

> **说明**: 本文档为 Phase 1 的技术设计文档，描述系统架构、数据库设计、API 设计和前端架构。
>
> **需求文档**: 详细功能需求请参见 `requirement/Phase1-详细需求.md`

---

## 目录

1. [数据库设计](#1-数据库设计)
2. [后端架构设计](#2-后端架构设计)
3. [前端架构设计](#3-前端架构设计)
4. [API 接口设计](#4-api-接口设计)
5. [认证与安全](#5-认证与安全)
6. [部署配置](#6-部署配置)

---

## 1. 数据库设计

### 1.1 用户表 (users)

**实际 Schema**:
```sql
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱',
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希',
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '全名',
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `bio` text COLLATE utf8mb4_unicode_ci COMMENT '个人简介',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色: admin=管理员, user=普通用户',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

**设计要点**:

1. **角色系统** (role 字段)
   - 默认值: 'user'
   - 可选值: 'admin', 'user'
   - 用于权限控制（管理员可以访问用户管理页面）

2. **索引设计**
   - PRIMARY KEY (id): 主键索引
   - UNIQUE KEY (username): 用户名唯一索引
   - UNIQUE KEY (email): 邮箱唯一索引
   - INDEX (role): 角色索引，用于快速查询管理员

3. **密码安全**
   - password_hash 存储 BCrypt 加密后的密码（强度10）
   - VARCHAR(255) 足够存储 BCrypt hash（60字符）+ 未来算法升级

### 1.2 职业路径表 (career_paths)

**实际 Schema**:
```sql
CREATE TABLE `career_paths` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '职业路径ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路径名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路径代码',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '路径描述',
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标名称',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_code` (`code`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业路径表';
```

**初始数据**:
```sql
INSERT INTO career_paths (name, code, description, icon, is_active) VALUES
('软件工程师', 'software_engineer', '软件开发、架构设计、技术管理等技术类职业', 'Code', 1),
('职业经理', 'manager', '项目管理、人员管理、战略规划等管理类职业', 'Briefcase', 1);
```

### 1.3 用户职业路径关联表 (user_career_paths)

**实际 Schema**:
```sql
CREATE TABLE `user_career_paths` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `career_path_id` bigint NOT NULL COMMENT '职业路径ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_career` (`user_id`,`career_path_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_career_path_id` (`career_path_id`),
  CONSTRAINT `user_career_paths_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_career_paths_ibfk_2` FOREIGN KEY (`career_path_id`) REFERENCES `career_paths` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户职业路径关联表';
```

**设计说明**:
- 多对多关系：用户可以选择多个职业路径
- UNIQUE KEY (user_id, career_path_id): 防止重复关联
- ON DELETE CASCADE: 用户删除时自动清理关联记录

---

## 2. 后端架构设计

### 2.1 分层架构

```
┌─────────────────────────────────────────┐
│          Controller Layer               │
│  - AuthController                       │
│  - UserController                       │
│  - CareerPathController                 │
├─────────────────────────────────────────┤
│           Service Layer                 │
│  - AuthService (认证、权限)              │
│  - UserService (用户CRUD)               │
│  - CareerPathService (职业路径查询)      │
├─────────────────────────────────────────┤
│          Repository Layer               │
│  - UserRepository                       │
│  - CareerPathRepository                 │
│  - (Spring Data JPA)                    │
├─────────────────────────────────────────┤
│           Model Layer                   │
│  - User (JPA Entity)                    │
│  - CareerPath (JPA Entity)              │
└─────────────────────────────────────────┘
```

### 2.2 核心类设计

#### Entity 层

**User.java**:
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String role = "user";

    @ManyToMany
    @JoinTable(
        name = "user_career_paths",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "career_path_id")
    )
    private List<CareerPath> careerPaths = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Service 层

**AuthService.java**:
```java
@Service
public class AuthService {
    // 传统注册
    public AuthResponse register(CreateUserRequest request);

    // 传统登录
    public AuthResponse login(LoginRequest request);

    // Google OAuth 登录
    public AuthResponse googleLogin(GoogleLoginRequest request);

    // 权限检查
    public boolean isAdmin(String username);
    public boolean isAdminByToken(String token);
}
```

**UserService.java**:
```java
@Service
public class UserService {
    public List<UserDTO> getAllUsers();
    public UserDTO getUserById(Long id);
    public UserDTO createUser(CreateUserRequest request);
    public UserDTO updateUser(Long id, UpdateUserRequest request);
    public void deleteUser(Long id);
    public UserDTO convertToDTO(User user);
}
```

#### Controller 层

**UserController.java**:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // 所有方法都需要管理员权限
    private void requireAdmin(String authHeader);

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader);

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request,
                                       @RequestHeader("Authorization") String authHeader);

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                       @RequestBody UpdateUserRequest request,
                                       @RequestHeader("Authorization") String authHeader);

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader);
}
```

---

## 3. 前端架构设计

### 3.1 项目结构

```
frontend/src/
├── main.js                   # 应用入口
├── App.vue                   # 根组件
│
├── components/               # 可复用组件
│   └── Sidebar.vue           # 侧边栏组件
│
├── views/                    # 页面组件
│   ├── auth/
│   │   ├── Login.vue         # 登录页
│   │   └── Register.vue      # 注册页
│   ├── Dashboard.vue         # 仪表盘
│   ├── skills/
│   │   ├── SkillTree.vue
│   │   └── LearningProgress.vue
│   └── settings/
│       ├── UserManagement.vue
│       └── Profile.vue
│
├── router/                   # 路由配置
│   └── index.js
│
├── api/                      # API 调用
│   ├── axios.js              # Axios 配置
│   ├── auth.js               # 认证 API
│   └── user.js               # 用户 API
│
├── composables/              # Composition API
│   └── useAuth.js            # 认证状态管理
│
└── assets/
    └── main.css              # Tailwind CSS
```

### 3.2 核心组件设计

#### 认证状态管理 (useAuth.js)

```javascript
import { ref, computed } from 'vue'

const currentUser = ref(null)
const token = ref(localStorage.getItem('auth_token'))

export function useAuth() {
  // 计算属性
  const isAuthenticated = computed(() => !!currentUser.value && !!token.value)
  const displayName = computed(() => currentUser.value?.fullName || currentUser.value?.username || '')
  const role = computed(() => currentUser.value?.role || 'user')
  const isAdmin = computed(() => role.value === 'admin')

  // 方法
  const login = async (credentials) => { /* ... */ }
  const register = async (userData) => { /* ... */ }
  const googleLogin = async (credential) => { /* ... */ }
  const logout = () => { /* ... */ }

  return {
    currentUser, token, isAuthenticated, displayName, role, isAdmin,
    login, register, googleLogin, logout
  }
}
```

#### 路由配置 (router/index.js)

```javascript
const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/dashboard', component: Dashboard },
  { path: '/skills/tree', component: SkillTree },
  { path: '/skills/progress', component: LearningProgress },
  {
    path: '/settings/users',
    component: UserManagement,
    meta: { requiresAdmin: true }  // 需要管理员权限
  },
  { path: '/settings/profile', component: Profile }
]

// 路由守卫
router.beforeEach((to, from, next) => {
  const { isAuthenticated, isAdmin } = useAuth()

  if (to.meta.requiresAdmin) {
    if (!isAuthenticated.value) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
    } else if (!isAdmin.value) {
      alert('您没有访问此页面的权限')
      next(false)
    } else {
      next()
    }
  } else {
    next()
  }
})
```

### 3.3 UI 布局设计

**侧边栏布局**:
```
┌──────────────────────────────────┐
│  用户信息区                       │
│  ┌────┐ Austin Xu [管理员]        │
│  │ A  │ exampleuser@example.com     │
│  └────┘                [登出]    │
├──────────────────────────────────┤
│  📚 Growing                       │
│     Personal Growth              │
├──────────────────────────────────┤
│  [学习] [求职] [设置]             │
├──────────────────────────────────┤
│  📊 仪表盘                        │
│                                  │
│  技能管理                         │
│    🌳 技能树                      │
│    📈 学习进度                    │
│                                  │
│  学习模块                         │
│    即将推出...                    │
├──────────────────────────────────┤
│  v1.0.0                          │
│  © 2025 Growing                  │
└──────────────────────────────────┘
```

---

## 4. API 接口设计

### 4.1 认证 API

**Base URL**: `/api/auth`

#### 4.1.1 用户登录
```
POST /api/auth/login
Content-Type: application/json

Request Body:
{
  "username": "exampleuser",  // 或邮箱
  "password": "password123"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "user": {
    "id": 1,
    "username": "exampleuser",
    "email": "austin@example.com",
    "fullName": "Austin Xu",
    "role": "admin",
    "careerPaths": [...]
  }
}
```

#### 4.1.2 用户注册
```
POST /api/auth/register
Content-Type: application/json

Request Body:
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "New User",
  "careerPathIds": [1]
}

Response (200):
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "user": { ... }
}
```

#### 4.1.3 Google 登录
```
POST /api/auth/google
Content-Type: application/json

Request Body:
{
  "credential": "eyJhbGciOiJSUzI1NiIs..."  // Google ID Token
}

Response (200):
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "user": { ... }
}
```

#### 4.1.4 获取当前用户
```
GET /api/auth/me
Authorization: Bearer {token}

Response (200):
{
  "id": 1,
  "username": "exampleuser",
  "email": "austin@example.com",
  "fullName": "Austin Xu",
  "role": "admin",
  "careerPaths": [...]
}
```

### 4.2 用户管理 API

**Base URL**: `/api/users`

**权限要求**: 所有接口都需要管理员权限

#### 4.2.1 获取用户列表
```
GET /api/users
Authorization: Bearer {admin_token}

Response (200):
{
  "data": [
    {
      "id": 1,
      "username": "exampleuser",
      "email": "austin@example.com",
      "fullName": "Austin Xu",
      "role": "admin",
      "careerPaths": [
        {
          "id": 1,
          "name": "软件工程师",
          "code": "software_engineer",
          "icon": "Code"
        }
      ],
      "createdAt": "2025-12-20T14:00:00Z"
    }
  ]
}
```

#### 4.2.2 创建用户
```
POST /api/users
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User",
  "careerPathIds": [1, 2],
  "role": "user"  // 可选，默认 "user"
}

Response (200):
{
  "id": 5,
  "username": "testuser",
  ...
}
```

#### 4.2.3 更新用户
```
PUT /api/users/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

Request Body:
{
  "fullName": "Updated Name",
  "bio": "Updated bio",
  "careerPathIds": [1, 2],
  "role": "admin"
}

Response (200):
{
  "id": 5,
  "username": "testuser",
  "fullName": "Updated Name",
  ...
}
```

#### 4.2.4 删除用户
```
DELETE /api/users/{id}
Authorization: Bearer {admin_token}

Response (200):
{
  "message": "用户删除成功"
}
```

### 4.3 职业路径 API

**Base URL**: `/api/career-paths`

#### 4.3.1 获取所有职业路径
```
GET /api/career-paths

Response (200):
{
  "data": [
    {
      "id": 1,
      "name": "软件工程师",
      "code": "software_engineer",
      "description": "软件开发、架构设计...",
      "icon": "Code",
      "isActive": true
    },
    {
      "id": 2,
      "name": "职业经理",
      "code": "manager",
      "description": "项目管理、人员管理...",
      "icon": "Briefcase",
      "isActive": true
    }
  ]
}
```

---

## 5. 认证与安全

### 5.1 JWT Token 设计

**Token 结构**:
```
Header:
{
  "alg": "HS384",
  "typ": "JWT"
}

Payload:
{
  "sub": "exampleuser",           // 用户名
  "iat": 1734700000,           // 签发时间
  "exp": 1734786400,           // 过期时间（24小时后）
  "provider": "local"          // 登录方式（local/google）
}

Signature:
HMACSHA384(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

**实现**:
```java
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24小时
    private Long expiration;

    public String generateToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS384, secret)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

### 5.2 密码加密

**BCrypt 配置**:
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);  // 强度10
    }
}
```

**使用示例**:
```java
// 注册时加密
String hashedPassword = passwordEncoder.encode(plainPassword);
user.setPasswordHash(hashedPassword);

// 登录时验证
boolean matches = passwordEncoder.matches(plainPassword, user.getPasswordHash());
```

### 5.3 Google OAuth 集成

**前端集成**:
```javascript
// 加载 Google SDK
<script src="https://accounts.google.com/gsi/client" async defer></script>

// 初始化
window.google.accounts.id.initialize({
  client_id: '85048175163-25v00bc8dflnpfq04hgsc5brls1fv4h2.apps.googleusercontent.com',
  callback: handleGoogleCallback
})

// 渲染按钮
window.google.accounts.id.renderButton(
  document.getElementById('google-signin-button'),
  { theme: 'outline', size: 'large' }
)
```

**后端验证**:
```java
GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
    new NetHttpTransport(),
    new GsonFactory())
    .setAudience(Collections.singletonList(googleClientId))
    .build();

GoogleIdToken idToken = verifier.verify(credential);
GoogleIdToken.Payload payload = idToken.getPayload();
String email = payload.getEmail();
String name = (String) payload.get("name");
```

### 5.4 CORS 配置

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### 5.5 Axios 拦截器

**请求拦截器**（自动添加 token）:
```javascript
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)
```

**响应拦截器**（处理 401）:
```javascript
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('current_user')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

---

## 6. 部署配置

### 6.1 后端配置

**application.properties**:
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# JPA 配置
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# JWT 配置
jwt.secret=${JWT_SECRET:your-secret-key}
jwt.expiration=86400000

# Google OAuth
google.client.id=85048175163-25v00bc8dflnpfq04hgsc5brls1fv4h2.apps.googleusercontent.com
```

**backend/.env** (本地开发):
```bash
DB_HOST=your-db-host
DB_PORT=3306
DB_NAME=growing
DB_USER=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your-jwt-secret-key-change-in-production
```

**启动脚本** (backend/start.sh):
```bash
#!/bin/bash
source .env
mvn spring-boot:run
```

### 6.2 前端配置

**vite.config.js**:
```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 6.3 开发环境启动

**后端**:
```bash
cd backend
./start.sh
# 或 mvn spring-boot:run
```

**前端**:
```bash
cd frontend
npm install
npm run dev
```

**访问地址**:
- 前端: http://localhost:3000
- 后端: http://localhost:8080

---

## 7. 技术栈总结

### 7.1 后端技术栈
- **框架**: Spring Boot 3.2.0
- **安全**: Spring Security + JWT (jjwt 0.12.3)
- **数据库**: MySQL 8.0 + Spring Data JPA
- **密码加密**: BCrypt (强度10)
- **OAuth**: Google API Client 2.0.0
- **构建工具**: Maven

### 7.2 前端技术栈
- **框架**: Vue 3 (Composition API)
- **路由**: Vue Router 4
- **HTTP**: Axios
- **样式**: Tailwind CSS
- **图标**: lucide-vue-next
- **OAuth**: Google Identity Services
- **构建工具**: Vite

---

**文档版本**: v1.0.0
**最后更新**: 2025-12-20
**状态**: Phase 1 已完成
