#!/bin/bash

# ================================================
#    老年人跌倒检测系统 - 重启服务脚本
# ================================================

set -e

# 颜色定义
GREEN='\033[0;32m'
NC='\033[0m'

echo "================================================"
echo "   老年人跌倒检测系统 - 重启服务"
echo "================================================"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 停止服务
echo "正在停止旧服务..."
./stop.sh

echo ""
echo "正在重新启动服务..."
echo ""

# 重新启动服务
docker-compose up -d

echo ""
echo -e "${GREEN}[OK] 服务已重启${NC}"
echo ""

# 打开浏览器
if command -v xdg-open &> /dev/null; then
    xdg-open http://localhost 2>/dev/null || echo "请手动访问: http://localhost"
elif command -v gnome-open &> /dev/null; then
    gnome-open http://localhost 2>/dev/null || echo "请手动访问: http://localhost"
else
    echo "请在浏览器中访问: http://localhost"
fi

echo ""