#!/bin/bash
set -e

clear
echo "================================================"
echo "      老年人跌倒检测系统 - 启动脚本"
echo "================================================"
echo ""
echo "正在检查 Docker 是否已安装..."

if ! command -v docker &> /dev/null; then
    echo "❌ 错误: Docker 未安装"
    echo "   请先安装 Docker:"
    echo "   curl -fsSL https://get.docker.com | sh"
    exit 1
fi

echo "✅ Docker 已安装"

echo ""
echo "正在检查 Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
    echo "❌ 错误: Docker Compose 未安装"
    echo "   请安装 docker-compose-plugin"
    exit 1
fi

echo "✅ Docker Compose 已安装"

echo ""
echo "================================================"
echo "正在启动服务..."
echo "================================================"
echo ""

cd "$(dirname "$0")"

echo "📦 正在构建并启动容器..."
docker-compose up -d

echo ""
echo "================================================"
echo "服务启动完成！"
echo "================================================"
echo ""
echo "📋 服务状态:"
echo "   - 前端应用: http://localhost"
echo "   - 后端API:  http://localhost:8080"
echo "   - 数据库:   localhost:3306"
echo ""

sleep 5

if command -v xdg-open &> /dev/null; then
    xdg-open http://localhost
elif command -v open &> /dev/null; then
    open http://localhost
else
    echo "请手动打开浏览器访问: http://localhost"
fi

echo ""
echo "✅ 系统已启动！"
echo ""
echo "================================================"
echo "登录信息:"
echo "   用户名: testuser"
echo "   密码: password"
echo "================================================"
