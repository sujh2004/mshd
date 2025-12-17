# MSHD 2.0 项目工作状态

最后更新时间：2025-12-17 23:45

---

## 📋 快速启动指南

### 1. 环境要求
- ✅ MySQL 8.0（root密码：Sujinghui2004@）
- ✅ MongoDB 5.0（无密码）
- ⚠️ Redis 6.0（未安装，已在配置中禁用）
- ✅ JDK 17+
- ✅ Node.js 18+
- ✅ Maven 3.6+

### 2. 启动服务（按顺序）

```bash
# 第一步：确保数据库服务已启动
# MySQL - 使用服务管理器或命令行：net start MySQL
# MongoDB - 应该已在后台运行

# 第二步：启动后端（在新终端）
cd D:\Bupt\2025Autumn\SET\Project\mshd-backend
mvn spring-boot:run
# 等待启动完成，看到 "Started MshdApplication" 提示

# 第三步：启动前端（在新终端）
cd D:\Bupt\2025Autumn\SET\Project\mshd-frontend
npm run dev
# 访问 http://localhost:3000
```

### 3. 访问地址
- **前端界面**：http://localhost:3000
- **后端API**：http://localhost:8080/api
- **默认账号**：admin / admin123（未实现登录功能）

---

## ✅ 已完成功能

### 后端 (Spring Boot)
- [x] 项目基础架构搭建
- [x] MySQL数据库连接和配置
- [x] MongoDB数据库连接
- [x] Spring Security配置（允许匿名访问）
- [x] CORS跨域配置
- [x] DisasterDataController（灾情数据控制器）
  - 创建、查询、删除灾情数据
  - 按来源、载体、类别查询
  - 按时间范围、地理范围查询
  - 统计分析（按类别、来源）
  - 编码解析
- [x] 数据库表结构（disaster_data, data_source, sys_user）
- [x] 测试数据（3条示例灾情数据）

### 前端 (Vue 3)
- [x] 项目基础架构
- [x] 路由配置
- [x] Layout布局（侧边栏+头部）
- [x] 灾情数据列表页面（基本功能）
- [x] API请求封装（axios）
- [x] Element Plus组件库集成

---

## 🚧 待开发功能

### 优先级：高 ⭐⭐⭐
1. **数据概览页面完善**
   - 统计卡片（总数据量、各类型占比）
   - 实时数据刷新
   - 趋势图表

2. **灾情数据管理完善**
   - 新增灾情数据表单
   - 编辑灾情数据功能
   - 数据详情查看
   - 高级筛选和搜索

3. **用户登录认证**
   - 登录页面
   - JWT Token生成和验证
   - 权限拦截器
   - 用户信息管理

### 优先级：中 ⭐⭐
4. **数据源管理**
   - 数据源列表展示
   - 新增/编辑/删除数据源
   - 数据源测试连接
   - 后端DataSourceController

5. **数据编码功能**
   - 编码生成器界面
   - 实时编码预览
   - 编码规则配置
   - 编码解析展示

6. **统计分析模块**
   - 多维度统计报表
   - 数据导出功能
   - 自定义统计维度

### 优先级：低 ⭐
7. **数据大屏**
   - 全屏数据展示
   - 实时数据更新
   - 地图可视化
   - 动态图表

8. **数据可视化**
   - ECharts集成
   - 多种图表类型（柱状图、饼图、折线图、地图）
   - 自定义图表配置

---

## 🐛 已知问题

1. **Redis未安装**
   - 状态：已在application.yml中禁用Redis自动配置
   - 影响：无法使用缓存功能
   - 解决方案：安装Redis或继续使用数据库查询

2. **部分页面功能未实现**
   - 数据大屏、数据源、数据编码、可视化、统计分析页面显示"功能开发中"
   - 需要逐步完善

3. **没有登录验证**
   - 当前所有接口允许匿名访问
   - 生产环境需要添加认证机制

---

## 📁 重要文件位置

### 配置文件
- 后端配置：`mshd-backend/src/main/resources/application.yml`
- 前端配置：`mshd-frontend/vite.config.js`
- Security配置：`mshd-backend/src/main/java/com/mshd/config/SecurityConfig.java`

### 数据库
- 初始化脚本：`database/init.sql`
- 数据库名：`mshd`
- 已创建表：disaster_data, data_source, sys_user

### API文档
- 控制器：`mshd-backend/src/main/java/com/mshd/controller/DisasterDataController.java`
- 前端API封装：`mshd-frontend/src/api/disaster.js`

---

## 🔧 技术栈详情

### 后端
- Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- Spring Data MongoDB
- Spring Security
- MySQL 8.0
- MongoDB 5.0
- JWT (待实现)
- Lombok

### 前端
- Vue 3.4
- Vite 5.0
- Vue Router 4
- Pinia (状态管理)
- Element Plus 2.5
- Axios
- ECharts 5.4 (待集成)
- SCSS

---

## 📊 数据编码规范

### 36位一体化编码结构
```
震情码(26位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
```

### 示例
```
632626200206 20210522020400 101 0 302001
│            │              │   │ │
│            │              │   │ └─ 灾情码(6位)
│            │              │   └─── 载体码(1位): 0=文字 1=图像
│            │              └─────── 来源码(3位): 101=后方指挥部
│            └────────────────────── 时间码(14位)
└─────────────────────────────────── 地理信息码(12位)
```

---

## 🎯 下一步工作建议

### 立即开始
1. 完善数据概览页面（Dashboard.vue）
2. 实现灾情数据的新增功能
3. 实现灾情数据的编辑功能

### 短期目标（1-2周）
1. 完成数据源管理模块
2. 实现用户登录认证
3. 完善数据编码功能

### 长期目标（1个月）
1. 完成所有核心功能模块
2. 添加数据大屏展示
3. 完善测试和文档
4. 部署到生产环境

---

## 💡 开发提示

### 常用命令
```bash
# 后端
mvn clean install           # 清理并构建
mvn spring-boot:run        # 启动应用
mvn test                   # 运行测试

# 前端
npm install                # 安装依赖
npm run dev                # 开发模式
npm run build              # 生产构建

# 数据库
mysql -u root -pSujinghui2004@ mshd  # 连接MySQL
show tables;                          # 查看表
```

### 常见问题解决
1. **端口被占用**：使用 `netstat -ano | findstr :8080` 查找进程并关闭
2. **前端无法访问后端**：检查后端是否启动，确认 http://localhost:8080/api/disaster/list 可访问
3. **数据库连接失败**：确认MySQL服务已启动，密码正确

---

## 📞 项目信息
- 课程：软件工程实践
- 小组：68组
- 版本：v1.0.0
- 创建日期：2025-12-17

---

**祝开发顺利！** 🚀
