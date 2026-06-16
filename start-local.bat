@echo off
chcp 65001 >nul
title 老年人跌倒检测系统启动器

echo ================================================
echo      老年人跌倒检测系统 - 本地启动脚本
echo ================================================
echo.
echo 正在检查 Docker 是否已安装...

docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: Docker 未安装或未启动
    echo    请先安装 Docker Desktop 并启动服务
    pause
    exit /b 1
)

echo ✅ Docker 已安装
echo.
echo ================================================
echo 正在启动数据库和前端服务...
echo ================================================
echo.

cd /d "%~dp0"

echo 📦 正在启动容器...
docker-compose -f docker-compose-simple.yml up -d

echo.
echo ================================================
echo 正在启动后端服务...
echo ================================================
echo.

set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/elderly_care?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set SPRING_DATASOURCE_USERNAME=admin
set SPRING_DATASOURCE_PASSWORD=admin123
set SPRING_JPA_HIBERNATE_DDL_AUTO=update
set AI_API_KEY=0c31e6fa6c1d4cb8838e378d2e4ae2cb.AH39rZIgE0l6ks3m
set AI_API_URL=https://open.bigmodel.cn/api/paas/v4/chat/completions
set AI_MODEL=glm-4-flash

echo 等待数据库启动...
timeout /t 15 /nobreak >nul

echo 🚀 启动后端服务...
start "Backend Server" java -Xms256m -Xmx512m -jar elderly-care-backend/target/elderly-care-backend-0.0.1-SNAPSHOT.jar

echo.
echo ================================================
echo 服务启动完成！
echo ================================================
echo.
echo 📋 服务状态:
echo   - 前端应用: http://localhost
echo   - 后端API:  http://localhost:8080
echo   - 数据库:   localhost:3306
echo.
echo 正在打开登录页面...
echo.

timeout /t 10 /nobreak >nul

start http://localhost

echo ✅ 系统已启动！
echo.
echo ================================================
echo 登录信息:
echo   用户名: testuser
echo   密码: password
echo ================================================
echo.
echo 按任意键退出...
pause >nul
