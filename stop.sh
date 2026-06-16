#!/bin/bash
set -e

clear
echo "================================================"
echo "      老年人跌倒检测系统 - 停止脚本"
echo "================================================"
echo ""

cd "$(dirname "$0")"

echo "🛑 正在停止服务..."
docker-compose down

echo ""
echo "✅ 服务已停止！"
echo ""
