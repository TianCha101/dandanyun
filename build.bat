@echo off
chcp 65001 >nul
cd /d %~dp0

rem ---- 自动推导 JAVA_HOME（未设置时从 PATH 里的 java 反推） ----
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
  echo 没找到 java，请先安装 JDK 21+ 并加入 PATH，或手动设置 JAVA_HOME。
  pause
  exit /b 1
)
echo JAVA_HOME = %JAVA_HOME%

rem ---- 找 mvn：PATH 里没有就用 IDEA 自带的 ----
set "MVN="
where mvn >nul 2>nul
if errorlevel 1 (
  for %%r in (C D E F) do (
    if exist "%%r:\Program Files\JetBrains" (
      for /d %%d in ("%%r:\Program Files\JetBrains\*") do (
        if exist "%%d\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" set "MVN=%%d\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
      )
    )
  )
) else (
  set "MVN=mvn"
)
if not defined MVN (
  echo 没找到 mvn。用 IDEA 打开项目直接运行，或安装 Maven 后重试。
  pause
  exit /b 1
)

echo === 编译、测试、打包 ===
call "%MVN%" -B "-Dmaven.repo.local=%USERPROFILE%\.m2\repository" clean package
if errorlevel 1 (
  echo.
  echo 构建失败，看上面的报错。
  pause
  exit /b 1
)
echo.
echo 构建成功：target\dandanyun-server-0.0.1-SNAPSHOT.jar
pause
