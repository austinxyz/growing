# Google OAuth 配置指南

本指南将帮助您为 Growing 应用配置 Google Sign-In 功能。

## 📋 前提条件

- Google 账号
- 能够访问 Google Cloud Console

## 🚀 配置步骤

### 1. 创建 Google Cloud 项目

1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 点击顶部的项目选择器，然后点击 **"新建项目"**
3. 输入项目名称，例如 `Growing-App`
4. 点击 **"创建"**
5. 等待项目创建完成（通常需要几秒钟）

### 2. 启用 Google Sign-In API

1. 在 Google Cloud Console 中，确保您已选择刚创建的项目
2. 在左侧导航菜单中，选择 **"API 和服务"** > **"库"**
3. 搜索 `Google+ API` 或 `Google Identity Services`
4. 点击搜索结果中的 API
5. 点击 **"启用"** 按钮

### 3. 配置 OAuth 同意屏幕

1. 在左侧菜单选择 **"API 和服务"** > **"OAuth 同意屏幕"**
2. 选择用户类型：
   - **外部**：如果要让任何 Google 用户都能登录
   - **内部**：如果只允许您组织内的用户（需要 Google Workspace）
3. 点击 **"创建"**
4. 填写应用信息：
   - **应用名称**：`Growing`
   - **用户支持电子邮件**：您的邮箱地址
   - **应用首页**：`http://localhost:3000`（开发环境）
   - **应用隐私权政策链接**：暂时可以填 `http://localhost:3000`
   - **应用服务条款链接**：暂时可以填 `http://localhost:3000`
   - **已获授权的网域**：
     - `localhost`（开发环境）
     - 您的生产域名（如果有）
   - **开发者联系信息**：您的邮箱地址
5. 点击 **"保存并继续"**
6. **作用域**页面：
   - 点击 **"添加或移除作用域"**
   - 选择以下作用域：
     - `email`
     - `profile`
     - `openid`
   - 点击 **"更新"**
   - 点击 **"保存并继续"**
7. **测试用户**（如果选择了"外部"）：
   - 在开发阶段，添加您自己的 Google 邮箱作为测试用户
   - 点击 **"添加用户"**
   - 输入邮箱地址
   - 点击 **"保存并继续"**
8. 查看摘要，点击 **"返回信息中心"**

### 4. 创建 OAuth 2.0 凭据

1. 在左侧菜单选择 **"API 和服务"** > **"凭据"**
2. 点击顶部的 **"+ 创建凭据"**
3. 选择 **"OAuth 客户端 ID"**
4. 选择应用类型：**"Web 应用"**
5. 填写信息：
   - **名称**：`Growing Web Client`
   - **已获授权的 JavaScript 来源**：
     ```
     http://localhost:3000
     http://127.0.0.1:3000
     ```
     （生产环境添加您的实际域名，例如 `https://yourdomain.com`）
   - **已获授权的重定向 URI**：
     ```
     http://localhost:3000
     http://localhost:3000/login
     http://localhost:3000/register
     ```
6. 点击 **"创建"**
7. 弹出窗口会显示您的 **Client ID**
   - **重要**：复制这个 Client ID，稍后需要使用
   - Client ID 格式类似：`123456789-abcdefghijk.apps.googleusercontent.com`

### 5. 配置前端应用127.0.01

1. 打开文件 `frontend/src/views/auth/Register.vue`
2. 找到这一行：
   ```javascript
   const GOOGLE_CLIENT_ID = 'YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com'
   ```
3. 将 `YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com` 替换为您刚才复制的 Client ID
4. 同样在 `frontend/src/views/auth/Login.vue` 中也要替换（如果有的话）

示例：
```javascript
const GOOGLE_CLIENT_ID = '123456789-abcdefghijk.apps.googleusercontent.com'
```

### 6. 测试配置

1. 启动前端开发服务器：
   ```bash
   cd frontend
   npm run dev
   ```

2. 访问 http://localhost:3000/register

3. 您应该能看到一个 **"使用 Google 账号注册"** 按钮

4. 点击按钮，应该会弹出 Google 登录窗口

5. 登录后，表单会自动填充您的 Gmail 邮箱和姓名

## 🔧 常见问题

### Q1: 点击 Google 按钮没有反应？

**解决方法：**
1. 检查浏览器控制台是否有错误
2. 确认 Client ID 已正确配置
3. 检查是否正确添加了授权的 JavaScript 来源

### Q2: 出现 "redirect_uri_mismatch" 错误？

**解决方法：**
1. 确保在 Google Cloud Console 的 OAuth 客户端设置中添加了正确的重定向 URI
2. URI 必须完全匹配（包括协议、域名、端口）
3. 保存后可能需要等待几分钟才能生效

### Q3: 弹出窗口显示 "This app isn't verified"？

**解决方法：**
- 开发阶段这是正常的
- 点击 **"Advanced"** > **"Go to Growing (unsafe)"**
- 这只会在测试用户中出现
- 生产环境需要通过 Google 的验证流程

### Q4: 只能我自己测试，其他人无法使用？

**解决方法：**
- 在 OAuth 同意屏幕配置中，应用状态为 "Testing"
- 测试模式下只有添加的测试用户可以登录
- 添加更多测试用户：
  1. 进入 **"OAuth 同意屏幕"**
  2. 在 **"测试用户"** 部分点击 **"添加用户"**
  3. 输入 Google 邮箱地址
- 或者发布应用（需要通过 Google 验证）

## 📝 生产环境部署

当您准备将应用部署到生产环境时：

1. **更新授权来源**：
   - 在 Google Cloud Console 的 OAuth 客户端设置中
   - 添加生产环境的域名到 "已获授权的 JavaScript 来源"
   - 例如：`https://yourdomain.com`

2. **更新重定向 URI**：
   - 添加生产环境的回调 URL
   - 例如：`https://yourdomain.com/login`

3. **发布应用**（可选，如果希望所有 Google 用户都能使用）：
   - 进入 **"OAuth 同意屏幕"**
   - 点击 **"发布应用"**
   - 提交验证申请
   - 验证过程可能需要几天到几周

4. **使用环境变量**：
   - 不要在代码中硬编码 Client ID
   - 使用环境变量：
   ```javascript
   const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID
   ```
   - 在 `.env` 文件中配置：
   ```
   VITE_GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
   ```

## 🔒 安全建议

1. **不要公开 Client Secret**
   - Google Sign-In JavaScript SDK 不需要 Client Secret
   - 如果后端需要验证，Secret 应该只存储在服务器端

2. **验证 ID Token**
   - 在后端验证 Google 返回的 ID Token
   - 不要只信任前端传来的用户信息

3. **使用 HTTPS**
   - 生产环境必须使用 HTTPS
   - Google OAuth 不支持 HTTP（localhost 除外）

## 📚 参考资源

- [Google Identity Services 文档](https://developers.google.com/identity/gsi/web)
- [Google OAuth 2.0 文档](https://developers.google.com/identity/protocols/oauth2)
- [Google Cloud Console](https://console.cloud.google.com/)

## ✅ 完成清单

- [ ] 创建 Google Cloud 项目
- [ ] 启用 Google Identity Services API
- [ ] 配置 OAuth 同意屏幕
- [ ] 创建 OAuth 2.0 凭据
- [ ] 获取 Client ID
- [ ] 在前端代码中配置 Client ID
- [ ] 测试 Google Sign-In 功能
- [ ] 添加测试用户（如需要）

---

配置完成后，用户就可以使用 Google 账号快速注册和登录 Growing 应用了！
