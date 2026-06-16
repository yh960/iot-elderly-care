@echo off
chcp 65001 >nul
title 后端服务启动器

set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/elderly_care?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set SPRING_DATASOURCE_USERNAME=admin
set SPRING_DATASOURCE_PASSWORD=admin123
set SPRING_JPA_HIBERNATE_DDL_AUTO=update
set AI_API_KEY=0c31e6fa6c1d4cb8838e378d2e4ae2cb.AH39rZIgE0l6ks3m
set AI_API_URL=https://open.bigmodel.cn/api/paas/v4/chat/completions
set AI_MODEL=glm-4-flash

echo ================================================
echo 正在启动后端服务...
echo ================================================
echo.
echo 数据库连接: %SPRING_DATASOURCE_URL%
echo.

java -Xms256m -Xmx512m -jar elderly-care-backend/target/elderly-care-backend-0.0.1-SNAPSHOT.jar

pause
