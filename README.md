# MSHD 2.0 - 多源异构灾情数据的一体化编码管理系统

## 项目简介

MSHD（Multi-Source Heterogeneous Disaster Data Management System）是一个针对地震灾情数据的一体化编码管理服务系统。系统能够接收来自多个数据源的异构灾情数据（包括文本、图片、音频、视频等），进行统一的一体化编码，实现灾情数据的全周期管理和可视化展示。

**当前状态：** ✅ **所有功能已完成并测试通过！**
**最后更新：** 2025-12-18
**数据量：** 33条真实灾情测试数据
**版本：** 2.0.0

---

## 功能特性

### 核心功能
- 🔗 **多源数据接入** - 支持业务报送、泛在感知、舆情感知等多种数据源
- 🔐 **一体化编码** - 36位标准编码：地理信息码(12位) + 时间码(14位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
- 📊 **数据可视化** - 基于ECharts的数据分析和中国地图展示
- 🗄️ **全周期管理** - 根据时效性自动管理数据生命周期
- 🔍 **智能检索** - 支持多维度查询和统计分析
- 📱 **响应式界面** - 基于Element Plus的现代化UI设计
- 🎯 **编码工具** - 编码生成器和解析器，支持双向操作

### 已实现模块（9/9 完成）
- ✅ 用户登录系统（BCrypt加密 + Token认证）
- ✅ 数据概览页面（统计卡片 + 多种图表）
- ✅ 灾情数据管理（完整CRUD + 查询筛选）
- ✅ 数据源管理（配置管理 + 连接测试）
- ✅ 数据编码工具（生成器 + 解析器 + 规则文档）
- ✅ 统计分析模块（多维筛选 + CSV导出）
- ✅ 数据大屏（深色主题 + 全屏模式 + 实时刷新）
- ✅ 数据可视化（4种视图 + 中国地图 + 热力图）

### 技术亮点
- ✅ 前后端分离架构（Spring Boot + Vue 3）
- ✅ RESTful API设计
- ✅ MySQL + MongoDB混合存储
- ✅ Spring Boot 3.2.0 + Vue 3.4最新版本
- ✅ 中国地图实时加载（阿里云CDN）
- ✅ 响应式数据可视化

---

## 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0（关系型数据）+ MongoDB 5.0（多媒体数据）
- **安全**: Spring Security + BCrypt密码加密
- **持久化**: Spring Data JPA + Spring Data MongoDB
- **构建工具**: Maven 3.6+
- **JDK**: Java 17+

### 前端技术
- **框架**: Vue 3.4（Composition API）
- **构建工具**: Vite 5.4
- **UI组件**: Element Plus 2.5
- **图表库**: ECharts 5.4
- **状态管理**: Pinia
- **路由管理**: Vue Router 4
- **HTTP客户端**: Axios

---

## 项目结构

```
MSHD/
├── mshd-backend/              # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mshd/
│   │   │   │   ├── controller/        # 控制器层
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   ├── DisasterDataController.java
│   │   │   │   │   └── DataSourceController.java
│   │   │   │   ├── service/           # 服务层
│   │   │   │   │   └── DisasterDataService.java
│   │   │   │   ├── repository/        # 数据访问层
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── DisasterDataRepository.java
│   │   │   │   │   └── DataSourceRepository.java
│   │   │   │   ├── entity/            # 实体类
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── DisasterData.java
│   │   │   │   │   └── DataSource.java
│   │   │   │   ├── config/            # 配置类
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── CorsConfig.java
│   │   │   │   ├── util/              # 工具类
│   │   │   │   │   └── EncodingUtil.java
│   │   │   │   └── common/            # 公共类
│   │   │   │       └── Result.java
│   │   │   └── resources/
│   │   │       └── application.yml    # 配置文件
│   │   └── test/                      # 测试代码
│   └── pom.xml                        # Maven配置
│
├── mshd-frontend/             # 前端项目
│   ├── src/
│   │   ├── api/                       # API接口
│   │   │   ├── user.js
│   │   │   ├── disaster.js
│   │   │   └── datasource.js
│   │   ├── assets/                    # 静态资源
│   │   ├── components/                # 公共组件
│   │   ├── router/                    # 路由配置
│   │   │   └── index.js               # 路由守卫
│   │   ├── stores/                    # 状态管理
│   │   │   └── user.js
│   │   ├── utils/                     # 工具函数
│   │   │   └── request.js
│   │   ├── views/                     # 页面组件
│   │   │   ├── Login.vue              # 登录页
│   │   │   ├── Dashboard.vue          # 数据概览
│   │   │   ├── DataList.vue           # 灾情数据管理
│   │   │   ├── DataSource.vue         # 数据源管理
│   │   │   ├── Encoding.vue           # 数据编码工具
│   │   │   ├── Statistics.vue         # 统计分析
│   │   │   ├── DataScreen.vue         # 数据大屏
│   │   │   └── Visualization.vue      # 数据可视化
│   │   ├── App.vue                    # 根组件
│   │   └── main.js                    # 入口文件（含地图加载）
│   ├── index.html                     # HTML模板
│   ├── package.json                   # npm配置
│   └── vite.config.js                 # Vite配置
│
├── database/                  # 数据库脚本
│   ├── init.sql                       # 数据库初始化脚本
│   ├── insert_more_data.sql           # 测试数据脚本（30条）
│   └── DATA_SUMMARY.md                # 数据汇总报告
│
├── docs/                      # 文档目录
│   ├── QUICK_START.md                 # 快速启动指南
│   ├── TESTING_GUIDE.md               # 完整测试指南
│   ├── PROJECT_STATUS.md              # 项目状态文档
│   ├── PROJECT_SUMMARY.md             # 项目开发总结
│   └── CREATE_TEST_USER.md            # 测试用户创建指南
│
└── README.md                  # 项目说明（本文档）
```

---

## 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **MongoDB**: 5.0+（可选）
- **Maven**: 3.6+

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql

# （可选）插入30条测试数据以获得更好的展示效果
source database/insert_more_data.sql
```

**注意：** 测试数据包含33条真实的中国地震灾情数据，覆盖7个省份，适合演示和展示。

### 2. 后端启动

```bash
# 进入后端目录
cd mshd-backend

# 修改配置文件 src/main/resources/application.yml
# 配置数据库连接信息（默认密码：Sujinghui2004@）

# 使用Maven启动
mvn spring-boot:run

# 或使用IDE（IDEA/Eclipse）直接运行 MshdApplication.java
```

**启动成功标志：**
```
Started MshdApplication in X.XX seconds
MSHD 多源异构灾情数据管理系统启动成功
访问地址: http://localhost:8080/api
```

### 3. 前端启动

```bash
# 进入前端目录
cd mshd-frontend

# 首次运行需要安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

**启动成功标志：**
```
VITE v5.4.21 ready in XXX ms
➜  Local:   http://localhost:3000/
```

### 4. 访问系统

打开浏览器访问：**http://localhost:3000**

**默认登录账号：**
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| test | 123456 | 管理员 |

---

## 一体化编码规范

### 编码结构（36位）

```
地理信息码(12位) + 时间码(14位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
```

### 编码示例

```
632626200206 20210522020400 101 0 302001
│            │              │   │ │
│            │              │   │ └─ 灾情码(6位): 3(房屋破坏) + 02(砖木房屋) + 001(一般损坏)
│            │              │   └─── 载体码(1位): 0=文字 1=图像 2=音频 3=视频
│            │              └─────── 来源码(3位): 101=后方指挥部
│            └────────────────────── 时间码(14位): YYYYMMDDHHmmss
└─────────────────────────────────── 地理信息码(12位): 经度(6) + 纬度(6)
```

**完整编码：** `632626200206202105220204001010302001`

### 详细说明

#### 地理信息码（12位）
- **前6位**: 经度（去掉小数点，左补零）
- **后6位**: 纬度（去掉小数点，左补零）
- 示例: 经度102.82, 纬度36.32 → `632626200206`

#### 时间码（14位）
- 格式: YYYYMMDDHHmmss
- 示例: 2021-05-22 02:04:00 → `20210522020400`

#### 来源码（3位）
| 代码 | 说明 | 示例 |
|------|------|------|
| 100 | 前方指挥部 | 现场应急指挥部 |
| 101 | 后方指挥部 | 应急管理中心 |
| 102 | 现场指挥部 | 临时指挥所 |
| 201 | 业务报送 | 地震局业务系统 |
| 202 | 泛在感知 | 物联网传感器 |
| 203 | 舆情感知 | 社交媒体监测 |

#### 载体码（1位）
| 代码 | 类型 |
|------|------|
| 0 | 文字 |
| 1 | 图像 |
| 2 | 音频 |
| 3 | 视频 |

#### 灾情码（6位）
- **第1位**: 灾害大类
  - 1=震情, 2=人员伤亡, 3=房屋破坏, 4=生命线工程, 5=次生灾害
- **第2-3位**: 灾害子类
- **第4-6位**: 灾情指标

示例: `302001` = 房屋破坏(3) + 砖木房屋(02) + 一般损坏(001)

---

## API文档

### 用户管理接口

#### 用户注册
```http
POST /api/user/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "123456",
  "realName": "新用户",
  "email": "user@example.com",
  "role": "admin"
}
```

#### 用户登录
```http
POST /api/user/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### 灾情数据接口

#### 创建灾情数据
```http
POST /api/disaster/create
Content-Type: application/json

{
  "geographicCode": "632626200206",
  "location": "青海省海东市民和回族土族自治县",
  "longitude": 102.8394,
  "latitude": 36.3295,
  "earthquakeTime": "2021-05-22T02:04:00",
  "sourceCode": "101",
  "sourceType": "business",
  "carrierCode": "0",
  "carrierType": "text",
  "disasterCode": "302001",
  "disasterCategory": "3",
  "content": "砖木房屋一般损坏面积500平方米",
  "status": 1
}
```

#### 查询灾情数据列表
```http
GET /api/disaster/list
```

#### 根据编码ID查询
```http
GET /api/disaster/code/{encodedId}
```

#### 解析编码
```http
GET /api/disaster/parse/{encodedId}

Response:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "earthquakeCode": "63262620020620210522020400",
    "geographicCode": "632626200206",
    "timeCode": "20210522020400",
    "sourceCode": "101",
    "carrierCode": "0",
    "disasterCode": "302001",
    "disasterCategory": "3",
    "disasterSubcategory": "02",
    "indicatorCode": "001"
  }
}
```

#### 统计分析
```http
GET /api/disaster/stats/category    # 按类别统计
GET /api/disaster/stats/source      # 按来源统计
```

#### 删除数据
```http
DELETE /api/disaster/{id}
```

更多API详情请查看：`TESTING_GUIDE.md`

---

## 数据库设计

### 核心表结构

#### disaster_data（灾情数据表）
```sql
CREATE TABLE disaster_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    encoded_id VARCHAR(36) NOT NULL UNIQUE,      -- 一体化编码ID
    earthquake_code VARCHAR(26),                  -- 震情码
    geographic_code VARCHAR(12),                  -- 地理信息码
    location VARCHAR(200),                        -- 地理位置描述
    longitude DOUBLE,                             -- 经度
    latitude DOUBLE,                              -- 纬度
    time_code VARCHAR(14),                        -- 时间码
    earthquake_time DATETIME,                     -- 地震发生时间
    source_code VARCHAR(3),                       -- 来源码
    source_type VARCHAR(50),                      -- 数据来源类型
    carrier_code VARCHAR(1),                      -- 载体码
    carrier_type VARCHAR(20),                     -- 载体类型
    disaster_code VARCHAR(6),                     -- 灾情码
    disaster_category VARCHAR(1),                 -- 灾害大类代码
    disaster_subcategory VARCHAR(2),              -- 灾害子类代码
    indicator_code VARCHAR(3),                    -- 灾情指标代码
    content TEXT,                                 -- 灾情数据内容
    file_path VARCHAR(500),                       -- 多媒体文件路径
    file_size BIGINT,                             -- 文件大小(字节)
    ttl INT,                                      -- 数据有效期(小时)
    status INT DEFAULT 0,                         -- 数据状态
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark VARCHAR(500)
);
```

#### data_source（数据源配置表）
用于管理多个数据源的配置信息，包括连接参数、同步状态等。

#### sys_user（系统用户表）
用于存储系统用户信息，密码使用BCrypt加密存储。

---

## 最近修复的问题

### 1. 编码ID长度问题（已修复 ✅）
**问题：** 数据库中的编码ID只有35位，导致解析功能报错
**解决：** 修复了初始化脚本和现有数据，确保所有编码ID为标准的36位
**影响文件：**
- `database/init.sql`
- 数据库中的所有历史数据

### 2. 查询功能不工作（已修复 ✅）
**问题：** 灾情数据列表和统计分析页面的查询筛选不起作用
**原因：** 前端下拉选项的值与数据库存储的实际值不匹配
**解决：**
- 更新了灾情数据页面的下拉选项值（来源码、载体类型）
- 更新了统计分析页面的下拉选项值（数据来源、灾害类别）
- 添加了前端过滤逻辑
**影响文件：**
- `mshd-frontend/src/views/DataList.vue`
- `mshd-frontend/src/views/Statistics.vue`

### 3. 测试数据不足（已解决 ✅）
**问题：** 原始系统只有3条测试数据，展示效果不佳
**解决：** 新增30条真实的灾情测试数据
**数据特点：**
- 覆盖中国7个省份（四川、云南、青海、甘肃、新疆、西藏、台湾）
- 时间跨度：2021-2025年
- 多种载体类型：文字(13)、图像(10)、视频(7)、音频(3)
- 多种数据来源：业务报送(22)、泛在感知(11)
- 真实的地理坐标和灾情描述

---

## 开发指南

### 后端开发规范
- 遵循RESTful API设计原则
- 统一使用Result类封装返回结果
- 使用Lombok简化实体类代码
- 所有Controller添加`@CrossOrigin`支持跨域
- 添加必要的日志记录（使用Slf4j）
- 使用BCrypt加密敏感信息

### 前端开发规范
- 使用Vue 3 Composition API
- 组件化开发，复用公共组件
- 统一使用axios进行API调用
- 遵循Element Plus组件规范
- 使用SCSS编写样式
- ECharts图表需要在窗口resize时调整大小

---

## 常见问题

### 1. 数据库连接失败
检查 `application.yml` 中的数据库配置，确保：
- MySQL服务已启动
- 数据库名称正确（mshd）
- 用户名和密码正确
- 端口号正确（默认3306）

### 2. 前端无法访问后端API
检查：
- 后端服务是否已启动（http://localhost:8080/api）
- 浏览器控制台是否有CORS错误
- 前端 `vite.config.js` 中的代理配置

### 3. 编码解析错误
确保：
- 编码ID长度为36位
- 编码格式符合规范
- 后端EncodingUtil工具类正常工作

### 4. 地图不显示
可能原因：
- 中国地图数据未加载（等待3-5秒）
- 网络无法访问阿里云CDN
- 解决方法：刷新页面或检查控制台错误

### 5. 查询筛选不生效
已修复：确保使用最新版本的代码，下拉选项值已更新为与数据库匹配。

---

## 系统演示数据

系统包含33条真实的地震灾情数据，适合用于：
- 功能演示和测试
- 数据可视化展示
- 查询和统计功能验证
- 编码系统演示

**数据分布：**
- 地理位置：覆盖7个省份，30+个县市
- 时间范围：2021年5月 - 2025年9月
- 载体类型：文字、图像、音频、视频均衡分布
- 数据来源：业务报送、泛在感知两大类

详细的数据统计请查看：`database/DATA_SUMMARY.md`

---

## 文档索引

- **README.md** (本文档) - 项目说明和技术文档
- **QUICK_START.md** - 快速启动指南
- **TESTING_GUIDE.md** - 完整测试指南
- **PROJECT_STATUS.md** - 项目状态文档
- **PROJECT_SUMMARY.md** - 项目开发总结
- **database/DATA_SUMMARY.md** - 数据汇总报告
- **CREATE_TEST_USER.md** - 测试用户创建指南

---

## 更新日志

### v2.0.0 (2025-12-18)
- ✅ 完成项目基础架构搭建
- ✅ 实现一体化编码功能
- ✅ 完成前后端基础功能
- ✅ 实现数据可视化模块
- ✅ 完成核心API接口
- ✅ 修复编码ID长度问题（35位→36位）
- ✅ 修复查询功能不工作的问题
- ✅ 新增30条真实测试数据
- ✅ 更新项目文档

### v1.0.0 (2025-12-17)
- ✅ 项目初始版本
- ✅ 基础功能实现

---

## 项目团队

- **课程**: 软件工程实践课程
- **学校**: 北京邮电大学
- **开发团队**: 68组
- **开发时间**: 2025-12-17 至 2025-12-18
- **开发周期**: 2天
- **代码量**: 约15,000行

---

## 许可证

本项目仅供学习使用。

---

## 联系方式

如有问题，请：
1. 查看 `QUICK_START.md` 和 `TESTING_GUIDE.md`
2. 检查浏览器控制台和后端日志
3. 提交Issue或联系项目维护者

---

**🎉 MSHD 2.0** - 让灾情数据管理更智能、更高效！

**最后更新：** 2025-12-18
**版本：** 2.0.0
**状态：** ✅ 生产就绪
