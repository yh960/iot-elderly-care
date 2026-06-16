# 智慧养老 IoT 系统

基于 Spring Boot + 人体雷达传感器的老人跌倒检测系统，集成智谱 GLM-4 大模型进行 AI 风险分析。

## 技术栈

| 层 | 技术 |
|---|------|
| 后端 | Spring Boot 2.7 + Spring Data JPA + MySQL 8 |
| 安全 | Spring Security + JWT (JJWT 0.11.5) |
| 前端 | 原生 HTML / CSS / JavaScript |
| AI | 智谱 GLM-4-Flash API |
| 工具 | Lombok、Validation |

## 项目结构

```
├── elderly-care-backend/          # 后端 (Spring Boot)
│   ├── src/main/java/.../
│   │   ├── controller/            # 接口层
│   │   ├── service/               # 业务层
│   │   ├── repository/            # 数据访问层
│   │   ├── entity/                # 实体类
│   │   ├── dto/                   # 数据传输对象
│   │   ├── config/                # 配置类 (JWT、Security、跨域)
│   │   ├── exception/             # 全局异常处理
│   │   └── util/                  # 工具类 (JWT 生成/解析)
│   └── src/main/resources/
│       ├── application.yml        # 配置文件
│       └── static/                # 内嵌前端 (备用)
├── elderlycarefrontend/           # 前端 (独立部署)
│   ├── index.html
│   ├── app.js
│   └── style.css
└── elderly_care.sql               # 数据库初始化脚本
```

## 核心功能

### 设备管理
- 边缘设备（雷达传感器）的注册、绑定、状态管理
- 设备与用户（老人）的关联

### 雷达数据采集
- 接收传感器上报的速度、轨迹坐标数据
- 数据存储与历史查询

### 跌倒检测
- 基于速度和垂直位移阈值的规则判断
- 速度 > 3.0 m/s → 高风险（跌倒）
- 速度 1.5-3.0 m/s → 中风险（需关注）
- 速度 ≤ 1.5 m/s → 低风险（正常）

### AI 分析
- 调用智谱 GLM-4 大模型分析雷达数据
- 返回风险等级、结论、判断理由、处理建议
- API 调用失败时自动降级为本地模拟分析

### 告警管理
- 跌倒事件触发告警
- 告警去重（防止重复告警）
- 告警状态流转：PENDING → RESOLVED

### 用户认证
- JWT Token 认证
- BCrypt 密码加密
- 登录 / 注册

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/users` | GET | 获取用户列表 |
| `/api/devices` | GET/POST | 设备管理 |
| `/api/radar/upload` | POST | 上传雷达数据 |
| `/api/fall-detection/detect` | POST | 跌倒检测 |
| `/api/ai/test` | POST | AI 分析测试 |
| `/api/alerts` | GET | 告警列表 |
| `/api/hello` | GET | 健康检查 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 1. 创建数据库

```sql
CREATE DATABASE elderly_care DEFAULT CHARACTER SET utf8mb4;
```

然后导入初始化脚本：

```bash
mysql -u root -p elderly_care < elderly_care.sql
```

### 2. 修改配置

编辑 `elderly-care-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/elderly_care?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

ai:
  api-key: your_zhipu_api_key    # 智谱 AI API 密钥
```

### 3. 启动后端

```bash
cd elderly-care-backend
mvn spring-boot:run
```

后端启动在 http://localhost:8080

### 4. 打开前端

直接用浏览器打开 `elderlycarefrontend/index.html` 即可。

## 前端页面

| 页面 | 功能 |
|------|------|
| 登录页 | 用户登录 / 注册 |
| 仪表盘 | 系统概览、设备状态 |
| 设备管理 | 添加 / 查看雷达设备 |
| 用户管理 | 用户列表 |
| 告警中心 | 告警记录、处理告警 |
| AI 测试 | 输入雷达数据，查看 AI 分析结果 |

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `edge_device` | 边缘设备表 |
| `radar_data` | 雷达数据表 |
| `fall_event` | 跌倒事件表 |
| `alert_log` | 告警记录表 |

## 许可证

MIT License
