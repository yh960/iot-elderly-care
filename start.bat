@echo off
chcp 65001 >nul
title 老年人跌倒检测系统启动器

echo ================================================
echo      老年人跌倒检测系统 - Docker启动脚本
echo ================================================
echo.
echo 正在检查 Docker 是否已安装...

docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: Docker 未安装或未启动
    echo    请先安装 Docker Desktop 并启动服务
    echo    下载地址: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

echo ✅ Docker 已安装
echo.
echo ================================================
echo 正在启动所有服务...
echo ================================================
echo.

cd /d "%~dp0"

echo 📦 正在构建并启动容器...
docker-compose up -d --build

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
echo 正在等待服务启动...
echo.

timeout /t 15 /nobreak >nul

echo ✅ 系统已启动！
echo.
echo ================================================
echo 登录信息:
echo   用户名: testuser
echo   密码: password
echo ================================================
echo.
echo 正在打开登录页面...
start http://localhost

echo.
echo 按任意键退出...
pause >nul
