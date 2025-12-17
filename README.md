# MSHD 2.0 - 多源异构灾情数据的一体化编码管理系统

## 项目简介

MSHD（Multi-Source Heterogeneous Disaster Data Management System）是一个针对地震灾情数据的一体化编码管理服务系统。系统能够接收来自多个数据源的异构灾情数据（包括文本、图片、音频、视频等），进行统一的一体化编码，实现灾情数据的全周期管理和可视化展示。

## 功能特性

### 核心功能
- 🔗 **多源数据接入** - 支持业务报送、泛在感知、舆情感知等多种数据源
- 🔐 **一体化编码** - 36位标准编码：震情码(26位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
- 📊 **数据可视化** - 基于ECharts的数据分析和地图展示
- 🗄️ **全周期管理** - 根据时效性自动管理数据生命周期
- 🔍 **智能检索** - 支持多维度查询和统计分析
- 📱 **响应式界面** - 基于Element Plus的现代化UI设计

### 技术亮点
- ✅ 前后端分离架构
- ✅ RESTful API设计
- ✅ MySQL + MongoDB混合存储
- ✅ Redis缓存优化
- ✅ Spring Boot微服务架构
- ✅ Vue3 + Vite构建

## 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0（关系型数据）+ MongoDB（多媒体数据）
- **缓存**: Redis
- **构建工具**: Maven
- **JDK**: 17+

### 前端技术
- **框架**: Vue 3.4
- **UI组件**: Element Plus 2.5
- **图表库**: ECharts 5.4
- **构建工具**: Vite 5.0
- **包管理器**: npm

## 项目结构

```
MSHD/
├── mshd-backend/           # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mshd/
│   │   │   │   ├── controller/     # 控制器层
│   │   │   │   ├── service/        # 服务层
│   │   │   │   ├── repository/     # 数据访问层
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── config/         # 配置类
│   │   │   │   ├── util/           # 工具类
│   │   │   │   ├── exception/      # 异常处理
│   │   │   │   └── common/         # 公共类
│   │   │   └── resources/
│   │   │       └── application.yml # 配置文件
│   │   └── test/                   # 测试代码
│   └── pom.xml                     # Maven配置
│
├── mshd-frontend/          # 前端项目
│   ├── src/
│   │   ├── api/                    # API接口
│   │   ├── assets/                 # 静态资源
│   │   ├── components/             # 公共组件
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # 状态管理
│   │   ├── utils/                  # 工具函数
│   │   ├── views/                  # 页面组件
│   │   ├── App.vue                 # 根组件
│   │   └── main.js                 # 入口文件
│   ├── index.html                  # HTML模板
│   ├── package.json                # npm配置
│   └── vite.config.js              # Vite配置
│
├── database/               # 数据库脚本
│   └── init.sql                    # 初始化SQL
│
├── docs/                   # 文档目录
│   ├── API文档.md
│   └── 部署指南.md
│
└── README.md              # 项目说明
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- MongoDB 5.0+（可选）
- Redis 6.0+（可选）
- Maven 3.6+

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

### 2. 后端启动

```bash
# 进入后端目录
cd mshd-backend

# 修改配置文件 src/main/resources/application.yml
# 配置数据库连接信息

# 安装依赖并启动
mvn clean install
mvn spring-boot:run

# 或使用IDE（IDEA/Eclipse）直接运行 MshdApplication.java
```

后端服务将在 `http://localhost:8080/api` 启动

### 3. 前端启动

```bash
# 进入前端目录
cd mshd-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

前端服务将在 `http://localhost:3000` 启动

### 4. 访问系统

打开浏览器访问：`http://localhost:3000`

默认账号：
- 用户名: admin
- 密码: admin123

## 一体化编码规范

### 编码结构（36位）

```
震情码(26位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
```

### 编码示例

```
632626200206 20210522020400 101 0 302001
│            │              │   │ │
│            │              │   │ └─ 灾情码(6位): 灾害大类(1) + 子类(2) + 指标(3)
│            │              │   └─── 载体码(1位): 0=文字 1=图像 2=音频 3=视频
│            │              └─────── 来源码(3位): 业务报送(1XX) 泛在感知(2XX)
│            └────────────────────── 时间码(14位): YYYYMMDDHHmmss
└─────────────────────────────────── 地理信息码(12位): 省市区县乡镇村
```

### 详细说明

**震情码（26位）**
- 地理信息码（12位）：省(2) + 市(2) + 区县(2) + 乡镇(3) + 村/社区(3)
- 时间码（14位）：年月日时分秒 (YYYYMMDDHHmmss)

**来源码（3位）**
- 1XX: 业务报送数据（如 100=前方指挥部, 101=后方指挥部）
- 2XX: 泛在感知数据（如 200=互联网感知, 201=通信网感知）
- 3XX: 其他数据

**载体码（1位）**
- 0: 文字
- 1: 图像
- 2: 音频
- 3: 视频
- 4: 其他

**灾情码（6位）**
- 灾害大类（1位）: 1=震情 2=人员伤亡 3=房屋破坏 4=生命线工程 5=次生灾害
- 灾害子类（2位）: 具体类别
- 灾情指标（3位）: 具体指标（如 001=受灾人数, 002=受灾范围）

## API文档

### 灾情数据接口

#### 1. 创建灾情数据
```
POST /api/disaster/create
Content-Type: application/json

{
  "geographicCode": "632626200206",
  "location": "青海省海东市民和回族土族自治县",
  "longitude": 102.8394,
  "latitude": 36.3295,
  "earthquakeTime": "2021-05-22T02:04:00",
  "sourceCode": "101",
  "carrierCode": "0",
  "disasterCode": "302001",
  "content": "砖木房屋一般损坏面积500平方米"
}
```

#### 2. 查询灾情数据列表
```
GET /api/disaster/list
```

#### 3. 根据编码ID查询
```
GET /api/disaster/code/{encodedId}
```

#### 4. 统计分析
```
GET /api/disaster/stats/category    # 按类别统计
GET /api/disaster/stats/source      # 按来源统计
```

#### 5. 解析编码
```
GET /api/disaster/parse/{encodedId}
```

更多API请参考：[docs/API文档.md](docs/API文档.md)

## 数据库设计

### 核心表结构

#### disaster_data（灾情数据表）
- id: 主键ID
- encoded_id: 一体化编码ID（36位）
- earthquake_code: 震情码（26位）
- geographic_code: 地理信息码（12位）
- location: 地理位置描述
- longitude/latitude: 经纬度
- time_code: 时间码（14位）
- earthquake_time: 地震发生时间
- source_code: 来源码（3位）
- carrier_code: 载体码（1位）
- disaster_code: 灾情码（6位）
- content: 灾情数据内容
- file_path: 多媒体文件路径
- status: 数据状态
- create_time/update_time: 创建/更新时间

#### data_source（数据源配置表）
- id: 主键ID
- source_name: 数据源名称
- source_code: 数据源编码（3位）
- source_type: 数据源类型
- source_url: 数据源URL
- protocol: 接口协议
- enabled: 是否启用

#### sys_user（系统用户表）
- id: 主键ID
- username: 用户名
- password: 密码（加密）
- role: 角色
- status: 状态

## 部署指南

### Docker部署（推荐）

即将提供Docker Compose一键部署方案

### 生产环境部署

详见：[docs/部署指南.md](docs/部署指南.md)

## 开发指南

### 后端开发规范
- 遵循RESTful API设计原则
- 统一使用Result类封装返回结果
- 使用Lombok简化代码
- 添加必要的日志记录
- 编写单元测试

### 前端开发规范
- 使用Vue3 Composition API
- 组件化开发
- 统一使用axios进行API调用
- 遵循Element Plus组件规范
- 使用SCSS编写样式

## 常见问题

### 1. 数据库连接失败
检查 `application.yml` 中的数据库配置是否正确。

### 2. 前端无法访问后端API
检查前端 `vite.config.js` 中的代理配置。

### 3. 编码解析错误
确保编码ID符合36位标准格式。

## 项目团队

- 课程组：软件工程实践课程
- 开发团队：68组

## 许可证

本项目仅供学习使用。

## 更新日志

### v1.0.0 (2025-12-17)
- ✅ 完成项目基础架构搭建
- ✅ 实现一体化编码功能
- ✅ 完成前后端基础功能
- ✅ 实现数据可视化模块
- ✅ 完成核心API接口

## 联系方式

如有问题，请提交Issue或联系项目维护者。

---

**MSHD 2.0** - 让灾情数据管理更智能、更高效！
