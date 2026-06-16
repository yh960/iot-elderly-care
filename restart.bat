@echo off
setlocal enabledelayedexpansion

REM ================================================
REM    老年人跌倒检测系统 - 重启服务脚本
REM ================================================

title Restart Elderly Care System

echo ================================================
echo    老年人跌倒检测系统 - 重启服务
echo ================================================
echo.

REM Stop existing services
call "%~dp0stop.bat" >nul 2>&1

echo.
echo Restarting services...
echo.

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

docker-compose up -d

echo.
echo ================================================
echo    Services restarted
echo ================================================
echo.
echo Opening browser...
timeout /t 3 /nobreak >nul
start http://localhost
echo.
echo Press any key to exit...
pause >nul