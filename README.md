# 点单云 · 最小运行框架

一套能跑起来的 Spring Boot 后端骨架：统一返回、全局异常、跨域、两套接口前缀。
**不连数据库、不连 Redis、不做登录**——只保证「能启动、能发请求、返回结构统一」，业务模块在这个骨架上往后加。

仓库：<https://github.com/TianCha101/dandanyun>

```bash
git clone https://github.com/TianCha101/dandanyun.git
cd dandanyun
```

## 环境要求

| 项 | 版本 |
|---|---|
| JDK | 21 及以上（本机以 25 实测） |
| Maven | 3.9+（或直接用 IDEA 内置的） |
| Spring Boot | 4.0.8 |
| 端口 | 8080 |

> 项目最初按 Spring Boot 3.x 规划（课程口径）。实际开发机是 JDK 25，本地依赖缓存也都是 4.x 体系，所以基线定在 4.0.8。
> 若需要 3.x，把 `pom.xml` 里的版本号改成 3.5.x 即可，代码不用动。

## 快速开始

```bash
# 打包
mvn -DskipTests package

# 运行
java -jar target/dandanyun-server-0.0.1-SNAPSHOT.jar
```

Windows 下也可直接双击：

- `build.bat` —— 编译打包（自动从 PATH 里的 java 推导 JAVA_HOME）
- `start.bat` —— 启动并打开健康检查页

用 IDEA：打开项目根目录（认 `pom.xml`），运行 `DandanyunApplication`。

启动后访问 <http://localhost:8080/health>。

## 接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/health` | 健康检查，返回应用名、Java 版本、服务器时间 |
| GET | `/admin/ping` | 管理端（管理员 + 商家共用）示例 |
| GET | `/user/ping` | 用户端（顾客 H5）示例 |
| GET | `/admin/boom` | 故意抛业务异常，验证全局异常处理器 |

返回统一结构：

```json
{ "code": 200, "message": "ok", "data": { } }
```

## 目录

```
.
├─ pom.xml
├─ build.bat / start.bat
└─ src/main/
   ├─ java/com/dandanyun/
   │  ├─ DandanyunApplication.java          启动类
   │  ├─ common/Result.java                 统一返回
   │  ├─ common/BusinessException.java      业务异常
   │  ├─ common/GlobalExceptionHandler.java 全局异常（业务/404/405/400/系统）
   │  ├─ config/WebMvcConfig.java           跨域
   │  └─ controller/
   │     ├─ HealthController.java           /health
   │     ├─ admin/AdminPingController.java  /admin/**
   │     └─ user/UserPingController.java    /user/**
   └─ resources/application.yml
```

## 异常处理策略

| 异常 | 返回 | 日志 |
|---|---|---|
| `BusinessException` | HTTP 200 + 业务码（如 4001） | WARN 一行 |
| `NoResourceFoundException` | HTTP 404 | 不打印 |
| `HttpRequestMethodNotSupportedException` | HTTP 405 | 不打印 |
| 参数缺失 / 类型不匹配 | HTTP 400 | WARN 一行 |
| 其它 `Exception` | HTTP 500 | ERROR + 完整堆栈 |

`/favicon.ico` 这类浏览器自动请求不会被当成系统故障刷 ERROR 日志——日志里出现 ERROR 就是真问题。

## 已完成 / 待实现

**已完成**

- 统一返回 `Result<T>`
- 全局异常分类处理
- CORS（放行 localhost 任意端口）
- `/admin/**` 与 `/user/**` 双前缀路由

**待实现**（按顺序）

1. MySQL + MyBatis：数据源、实体、mapper
2. Redis：登录态存储
3. JWT 拦截器：按前缀分别校验，当前登录人放 ThreadLocal
4. 业务模块：员工 → 分类 → 菜品 → 套餐 → 订单 → 统计
5. 前端：Vue3 + Vite + ElementPlus（管理端）、H5（用户端）

## 说明

这是课程项目的实践骨架，用于把《企业级框架应用开发》与《前端开发技术》两门课的技术栈串成一个可运行的工程。
