@echo off
chcp 65001 >nul
cd /d %~dp0

if not defined JAVA_HOME (
  for /f "delims=" %%j in ('where java 2^>nul') do (
    set "JBIN=%%~dpj"
    goto :gotjava
  )
)
:gotjava
if not defined JAVA_HOME (
  if defined JBIN for %%k in ("%JBIN%..") do set "JAVA_HOME=%%~fk"
)
if not defined JAVA_HOME (
  echo 没找到 java，请先安装 JDK 21+ 并加入 PATH。
  pause
  exit /b 1
)

if not exist "target\dandanyun-server-0.0.1-SNAPSHOT.jar" (
  echo 还没构建过，先跑 build.bat
  pause
  exit /b 1
)

echo === 启动 点单云 ===
echo   健康检查  http://localhost:8080/health
echo   就绪检查  http://localhost:8080/health/ready
echo   管理端登录 POST http://localhost:8080/auth/admin/login
echo   用户端登录 POST http://localhost:8080/auth/user/login
echo   数据库密码请通过 DB_PASSWORD 环境变量提供。
echo 关闭窗口即停止服务。
echo.
java -jar target\dandanyun-server-0.0.1-SNAPSHOT.jar
pause
