@echo off
chcp 65001 >nul
title 老年人跌倒检测系统 - Docker启动器

echo ================================================
echo      老年人跌倒检测系统 - Docker启动脚本
echo ================================================
echo.

cd /d "%~dp0"

echo ================================================
echo 正在启动数据库容器...
echo ================================================
echo.

docker-compose up -d

echo.
echo 等待数据库启动...
timeout /t 15 /nobreak >nul

echo.
echo ================================================
echo 正在启动后端服务...
echo ================================================
echo.

set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/elderly_care?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set SPRING_DATASOURCE_USERNAME=root
set SPRING_DATASOURCE_PASSWORD=155112
set SPRING_JPA_HIBERNATE_DDL_AUTO=update
set AI_API_KEY=0c31e6fa6c1d4cb8838e378d2e4ae2cb.AH39rZIgE0l6ks3m

echo 🚀 启动后端服务...
start "Backend Server" java -Xms256m -Xmx512m -jar elderly-care-backend/target/elderly-care-backend-0.0.1-SNAPSHOT.jar

echo.
echo ================================================
echo 正在启动前端服务...
echo ================================================
echo.

echo 🚀 启动前端服务...
start "Frontend Server" python -m http.server 80 --directory elderlycarefrontend

echo.
echo ================================================
echo 服务启动完成！
echo ================================================
echo.
echo 📋 服务状态:
echo   - 前端应用: http://localhost
echo   - 后端API:  http://localhost:8080
echo   - 数据库:   localhost:3307
echo.
echo 等待服务启动...
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
