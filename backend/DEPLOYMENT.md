# Growing Backend Deployment Guide

## 环境配置说明

项目支持两种环境配置：

### 📘 开发环境（Development）
- **配置文件**: `application-dev.properties`
- **日志等级**: DEBUG/TRACE（详细）
- **特性**:
  - ✅ 显示所有SQL语句
  - ✅ SQL参数绑定日志（TRACE级别）
  - ✅ Hibernate性能统计（DEBUG级别）
  - ✅ API文档（Swagger UI）启用
  - ✅ 详细错误信息

### 🚀 生产环境（Production）
- **配置文件**: `application-prod.properties`
- **日志等级**: INFO/WARN（精简）
- **特性**:
  - ❌ 关闭SQL日志（性能优化）
  - ❌ 关闭Hibernate统计
  - ❌ 禁用API文档（安全）
  - ❌ 隐藏错误详情（安全）
  - ✅ 只记录重要信息和错误

## 启动方式

### 方式1：使用启动脚本（推荐）

```bash
# 开发模式（默认）
cd backend
./start.sh

# 或显式指定dev
./start.sh dev

# 生产模式
./start.sh prod
```

### 方式2：使用Maven命令

```bash
# 开发模式
cd backend
export $(grep -v '^#' .env | xargs)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产模式
cd backend
export $(grep -v '^#' .env | xargs)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 方式3：使用JAR包部署

```bash
# 1. 构建JAR包
cd backend
mvn clean package -DskipTests

# 2. 运行（开发模式）
export $(grep -v '^#' .env | xargs)
java -jar target/growing-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 3. 运行（生产模式）
export $(grep -v '^#' .env | xargs)
java -jar target/growing-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 日志等级对比

| 组件 | 开发环境 | 生产环境 |
|------|---------|---------|
| 应用程序 (com.growing.app) | DEBUG | INFO |
| SQL语句显示 | ✅ 显示 | ❌ 关闭 |
| SQL参数绑定 | TRACE | WARN |
| Hibernate统计 | DEBUG | WARN |
| Hibernate其他 | - | WARN |
| Spring Framework | - | WARN |
| 数据库连接池 | - | WARN |
| API文档（Swagger） | ✅ 启用 | ❌ 禁用 |
| 错误堆栈信息 | ✅ 显示 | ❌ 隐藏 |

## 生产环境额外配置（可选）

### 启用文件日志

编辑 `application-prod.properties`，取消注释以下行：

```properties
# Log file configuration
logging.file.name=logs/growing-app.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.console=%d{yyyy-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### 修改CORS配置

生产环境需要修改允许的源地址：

```properties
# 将 localhost 改为实际域名
spring.web.cors.allowed-origins=https://your-production-domain.com
```

## 验证环境配置

启动后检查日志输出：

### 开发环境特征
```
The following profiles are active: dev
...
Hibernate: select ... (会看到SQL语句)
Hibernate: binding parameter [1] as [VARCHAR] - [value] (会看到参数绑定)
```

### 生产环境特征
```
The following profiles are active: prod
...
(不会看到SQL语句和参数绑定日志)
```

## 环境切换检查清单

切换到生产环境前，确认：

- [ ] `.env` 文件包含正确的数据库凭据
- [ ] CORS配置指向正确的前端域名
- [ ] JWT_SECRET 已设置且足够强
- [ ] 数据库连接信息正确
- [ ] 使用 `./start.sh prod` 或 `--spring.profiles.active=prod` 启动
- [ ] 日志中显示 "profiles are active: prod"
- [ ] 控制台无SQL语句输出
- [ ] Swagger UI无法访问（http://localhost:8082/api/swagger-ui.html 返回404）

## 故障排查

### 问题1: 启动后仍然看到SQL日志
**原因**: 可能使用了错误的profile
**解决**: 检查启动日志中的 "The following profiles are active: xxx"，确保是 "prod"

### 问题2: 生产环境需要调试SQL
**临时解决**: 在 `application-prod.properties` 中临时启用：
```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```
**注意**: 调试完毕后务必改回去！

### 问题3: 日志文件过大
**解决**: 调整 `logging.file.max-size` 和 `logging.file.max-history` 参数

## 性能影响对比

| 配置项 | 开发环境性能影响 | 生产环境性能影响 |
|--------|----------------|----------------|
| SQL日志 | -5% ~ -10% | 0%（关闭） |
| 参数绑定日志 | -3% ~ -5% | 0%（关闭） |
| Hibernate统计 | -2% ~ -5% | 0%（关闭） |
| **总体影响** | **-10% ~ -20%** | **优化后基线** |

## 相关文件

- `application.properties` - 默认配置（指向dev profile）
- `application-dev.properties` - 开发环境配置
- `application-prod.properties` - 生产环境配置
- `start.sh` - 启动脚本
- `.env` - 环境变量（数据库凭据，不提交到Git）
