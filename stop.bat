@echo off
chcp 65001 >nul
title 老年人跌倒检测系统 - 停止脚本

echo ================================================
echo      老年人跌倒检测系统 - 停止脚本
echo ================================================
echo.

cd /d "%~dp0"

echo 🛑 正在停止服务...
docker-compose down

echo.
echo ✅ 服务已停止！
echo.
echo 按任意键退出...
pause >nul
