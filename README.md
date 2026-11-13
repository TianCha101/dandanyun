# 点单云 · 后端最小框架

《点单云》的可扩展 Spring Boot 后端底座。它不包含完整点餐业务，但已经解决新模块开发前必须统一的工程问题：配置、数据库、数据访问、身份认证、接口返回、异常处理、日志和测试。

仓库：<https://github.com/TianCha101/dandanyun>

## 技术基线

| 项 | 版本 |
|---|---|
| JDK | 25 |
| Maven | 3.9+ |
| Spring Boot | 4.0.8 |
| MySQL | 26.7 |
| MyBatis | MyBatis Spring Boot Starter 4.0.1 |
| 端口 | 8080 |

## 已包含

- 统一返回结构 `Result<T>`
- 业务异常、参数异常、鉴权异常、权限异常和数据库异常处理
- `X-Request-Id` 请求链路标识和结构化控制台日志
- MySQL、HikariCP、MyBatis Mapper 与下划线转驼峰配置
- 管理员 `ADMIN` 与普通用户 `USER` 两套 JWT 鉴权
- 基于 PBKDF2 的密码摘要
- 登录参考实现、`/admin/**` 与 `/user/**` 权限隔离
- 健康检查与数据库就绪检查
- 系统用户初始化脚本
- JWT、密码摘要和应用启动测试

## 当前边界

以下内容属于业务模块，不在本框架内：

- 员工、分类、菜品、套餐
- 地址、购物车、订单、支付和统计
- Vue 管理端与 H5 用户端

框架已经提供写这些模块所需的公共能力和一个完整纵向样例，可以在此基础上直接添加业务表、Mapper、Service、Controller 和前端页面。

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE dandanyun
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
```

### 2. 配置运行环境

PowerShell 示例：

```powershell
$env:DB_URL = "jdbc:mysql://127.0.0.1:3306/dandanyun?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的数据库密码"
$env:DB_INIT_MODE = "always"
$env:DANDANYUN_JWT_SECRET = "至少32字节、部署时必须替换的随机密钥"
```

首次启动用 `DB_INIT_MODE=always` 执行建表和演示账号初始化；之后建议改为 `never`，避免每次启动重复执行初始化脚本。

### 3. 构建并启动

```powershell
.\build.bat
.\start.bat
```

也可以使用 Maven：

```powershell
mvn clean package
java -jar target\dandanyun-server-0.0.1-SNAPSHOT.jar
```

## 环境变量

| 变量 | 默认值 | 说明 |
|---|---|---|
| `DB_URL` | `jdbc:mysql://127.0.0.1:3306/dandanyun...` | MySQL 连接地址 |
| `DB_USERNAME` | `root` | MySQL 用户名 |
| `DB_PASSWORD` | 空 | MySQL 密码 |
| `DB_POOL_SIZE` | `10` | HikariCP 最大连接数 |
| `DB_INIT_MODE` | `never` | 是否执行 `schema.sql` 和 `data.sql` |
| `DANDANYUN_JWT_SECRET` | 开发用默认密钥 | JWT HMAC 密钥，至少 32 字节 |
| `DANDANYUN_JWT_EXPIRATION_SECONDS` | `7200` | token 有效期 |
| `DANDANYUN_PASSWORD_ITERATIONS` | `120000` | PBKDF2 迭代次数 |

## 演示账号

仅在启用初始化脚本时创建：

| 端 | 用户名 | 密码 | 角色 |
|---|---|---|---|
| 管理端 | `admin` | `Admin@123` | `ADMIN` |
| 用户端 | `customer` | `User@123` | `USER` |

演示密码只用于本地联调，正式使用前必须替换。

## 接口

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| GET | `/health` | 否 | 应用存活检查 |
| GET | `/health/ready` | 否 | 应用与数据库就绪检查 |
| POST | `/auth/admin/login` | 否 | 管理员登录 |
| POST | `/auth/user/login` | 否 | 用户登录 |
| GET | `/admin/me` | Bearer | 当前管理员信息 |
| GET | `/admin/ping` | Bearer | 管理端鉴权样例 |
| GET | `/admin/boom` | Bearer | 业务异常样例 |
| GET | `/user/me` | Bearer | 当前用户信息 |
| GET | `/user/ping` | Bearer | 用户端鉴权样例 |

### 登录示例

```powershell
$body = @{ username = "admin"; password = "Admin@123" } | ConvertTo-Json
$login = Invoke-RestMethod `
  -Method Post `
  -ContentType "application/json" `
  -Body $body `
  http://localhost:8080/auth/admin/login

$headers = @{ Authorization = "Bearer $($login.data.accessToken)" }
Invoke-RestMethod -Headers $headers http://localhost:8080/admin/me
```

## 目录

```text
src/main/
├─ java/com/dandanyun/
│  ├─ auth/            JWT、登录、身份上下文与鉴权拦截
│  ├─ common/          Result、异常、请求 ID
│  ├─ config/          CORS、JWT 与密码配置
│  ├─ controller/      health、admin、user 示例接口
│  └─ system/          sys_user 实体与 Mapper
└─ resources/
   ├─ db/init/         schema.sql 与 data.sql
   ├─ mapper/          MyBatis XML
   └─ application.yml  基础配置
```

## 测试

```powershell
mvn test
```

当前测试覆盖应用启动、公开健康接口、未登录拦截、JWT 签发与篡改校验、密码摘要校验。

## 下一步

业务开发直接沿用现有分层：

1. 在 `db/init/schema.sql` 中增加业务表。
2. 建立实体、Mapper、Service 和 Controller。
3. 管理端接口放在 `/admin/**`，用户端接口放在 `/user/**`。
4. 新接口复用 `Result<T>`、业务异常和现有鉴权上下文。
5. 先完成“员工 CRUD”，再按分类、菜品、套餐、购物车和订单逐步推进。
