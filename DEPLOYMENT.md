# 老年人跌倒检测系统 - Docker 化部署方案

## 目录
1. [系统概述](#系统概述)
2. [架构设计](#架构设计)
3. [环境要求](#环境要求)
4. [在线构建步骤](#在线构建步骤)
5. [离线部署方案](#离线部署方案)
6. [一键启动](#一键启动)
7. [跨平台兼容性](#跨平台兼容性)
8. [常见问题](#常见问题)

---

## 系统概述

本系统实现 **"一次构建，到处运行"** 的目标，通过 Docker 容器化技术将所有服务打包为镜像，确保在任何安装了 Docker 引擎的环境中无需修改即可运行。

### 服务组件

| 组件 | 技术栈 | 端口 | 说明 |
|------|--------|------|------|
| 前端应用 | HTML5 + JavaScript + NGINX | 80 | 用户交互界面 |
| 后端服务 | Spring Boot 2.7.x + Java 17 | 8080 | API服务 |
| 数据库 | MySQL 8.0 | 3306 | 数据存储 |

---

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                     Docker Compose                          │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐   │
│  │   Frontend   │    │   Backend    │    │    MySQL     │   │
│  │   (NGINX)    │───>│ (Spring Boot)│───>│    (8.0)     │   │
│  │   :80        │    │   :8080      │    │    :3306     │   │
│  └──────────────┘    └──────────────┘    └──────────────┘   │
│         │                  │                  │              │
└─────────┼──────────────────┼──────────────────┼──────────────┘
          ▼                  ▼                  ▼
       localhost           localhost           localhost
         :80               :8080               :3306
```

---

## 环境要求

### 必须安装
- **Docker Engine** (版本 20.10.0+)
- **Docker Compose** (版本 2.0.0+)

### 系统支持
- ✅ Windows 10/11 (64位)
- ✅ Linux (Ubuntu 18.04+, CentOS 7+)
- ✅ macOS (10.15+)

---

## 在线构建步骤

### 步骤1：确保网络可访问 Docker Hub

```bash
# 测试 Docker Hub 连接
docker pull hello-world
```

### 步骤2：构建并启动服务

```bash
# 进入项目目录
cd iot-project

# 构建并启动所有服务
docker-compose up -d --build
```

### 步骤3：验证服务状态

```bash
# 查看容器状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

---

## 离线部署方案

### 场景：目标环境无法访问互联网

#### 步骤1：在有网络的环境中构建镜像

```bash
# 进入项目目录
cd iot-project

# 构建所有服务
docker-compose build

# 查看构建的镜像
docker images | grep elderly-care
```

#### 步骤2：导出镜像为 tar 文件

```bash
# 创建镜像导出目录
mkdir -p docker-images

# 导出后端镜像
docker save elderly-care-backend:latest > docker-images/backend.tar

# 导出前端镜像
docker save elderly-care-frontend:latest > docker-images/frontend.tar

# 导出 MySQL 镜像
docker save mysql:8.0 > docker-images/mysql.tar
```

#### 步骤3：传输镜像文件到目标环境

使用以下方式之一传输：
- USB 移动硬盘
- 内网文件共享
- SSH 传输

#### 步骤4：在目标环境导入镜像

```bash
# 进入项目目录
cd iot-project

# 创建网络
docker network create elderly-care-network

# 导入镜像
docker load < docker-images/mysql.tar
docker load < docker-images/backend.tar
docker load < docker-images/frontend.tar

# 启动服务（无需重新构建）
docker-compose up -d
```

---

## 一键启动

### Windows 系统
```cmd
start.bat
```

### Linux/macOS 系统
```bash
chmod +x start.sh
./start.sh
```

### 启动脚本功能
1. 检查 Docker 是否已安装
2. 自动构建/启动所有容器
3. 等待服务启动完成
4. 自动打开浏览器访问登录页面

---

## 跨平台兼容性

### 镜像可移植性设计

| 特性 | 实现方式 | 说明 |
|------|----------|------|
| 基础镜像 | 使用 Alpine Linux | 轻量级，跨平台兼容 |
| JDK 版本 | Eclipse Temurin 17 | 官方认证，跨平台支持 |
| 时区配置 | 统一设置 Asia/Shanghai | 避免时区问题 |
| 依赖打包 | Maven Shade 插件 | 包含所有依赖的独立 JAR |

### 支持的架构

| 架构 | 支持状态 | 说明 |
|------|----------|------|
| x86_64 | ✅ 完全支持 | 主流服务器架构 |
| ARM64 | ✅ 支持 | 需要重新构建 |
| ARM32 | ⚠️ 有限支持 | 需调整基础镜像 |

---

## 配置说明

### .env 文件参数

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=155112
MYSQL_DATABASE=elderly_care
MYSQL_USER=root
MYSQL_PASSWORD=155112

# 端口配置
FRONTEND_PORT=80
BACKEND_PORT=8080
MYSQL_PORT=3306

# JPA 配置
JPA_DDL_AUTO=update

# AI 配置
AI_API_KEY=your_api_key_here
AI_API_URL=https://open.bigmodel.cn/api/paas/v4/chat/completions
AI_MODEL=glm-4-flash

# 时区配置
TZ=Asia/Shanghai
```

### Dockerfile 设计说明

**后端 Dockerfile** (`elderly-care-backend/Dockerfile`)：
```dockerfile
FROM openjdk:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

COPY target/elderly-care-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**前端 Dockerfile** (`elderlycarefrontend/Dockerfile`)：
```dockerfile
FROM nginx:alpine

COPY index.html /usr/share/nginx/html/
COPY style.css /usr/share/nginx/html/
COPY app.js /usr/share/nginx/html/

RUN cat > /etc/nginx/conf.d/default.conf << 'EOF'
server {
    listen 80;
    location / { root /usr/share/nginx/html; }
    location /api { proxy_pass http://elderly-care-backend:8080; }
}
EOF

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

## 访问地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost |
| 后端 API | http://localhost:8080 |
| 健康检查 | http://localhost:8080/api/hello |
| 数据库 | localhost:3306 |

### 登录信息

```
用户名: testuser
密码: password
```

---

## 常见问题

### Q1: 镜像构建失败

**现象**：`docker-compose up --build` 失败

**解决方案**：
```bash
# 检查网络连接
docker pull openjdk:17-jre-alpine

# 如果网络受限，使用离线镜像导入方案
docker load < docker-images/backend.tar
```

### Q2: 端口被占用

**现象**：端口 80、8080 或 3306 被占用

**解决方案**：修改 `.env` 文件
```bash
FRONTEND_PORT=8081
BACKEND_PORT=8082
MYSQL_PORT=3307
```

### Q3: 数据库连接失败

**现象**：后端服务无法连接数据库

**解决方案**：
```bash
# 检查数据库容器状态
docker-compose logs db

# 确保数据库服务健康
docker-compose exec db mysql -u root -p155112
```

### Q4: 跨平台部署问题

**现象**：在 ARM 架构上运行失败

**解决方案**：
```bash
# 在 ARM 环境重新构建
docker-compose build --platform linux/arm64

# 或者使用多架构镜像
docker buildx build --platform linux/amd64,linux/arm64 -t elderly-care-backend:latest .
```

---

## 技术支持

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 查看指定服务日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs db

# 实时查看日志
docker-compose logs -f backend
```

### 进入容器

```bash
# 进入后端容器
docker exec -it elderly-care-backend sh

# 进入前端容器
docker exec -it elderly-care-frontend sh

# 进入数据库容器
docker exec -it elderly-care-db mysql -u root -p155112
```

### 停止服务

```bash
# 停止并删除容器
docker-compose down

# 停止但保留容器
docker-compose stop
```

---

**文档版本**: v2.0  
**更新日期**: 2026年4月  
**设计目标**: 一次构建，到处运行
