# 创建测试账户

由于BCrypt密码需要通过后端加密，请按以下步骤创建测试账户：

## 方法1：使用Postman或curl创建账户

### 1. 启动后端服务

```bash
cd D:\Bupt\2025Autumn\SET\Project\mshd-backend
mvn spring-boot:run
```

### 2. 发送注册请求

使用Postman或curl发送以下请求：

```bash
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "realName": "系统管理员",
    "email": "admin@mshd.com",
    "phone": "13800138000",
    "role": "admin",
    "department": "系统管理部"
  }'
```

### 3. 创建普通用户（可选）

```bash
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "operator",
    "password": "admin123",
    "realName": "操作员",
    "email": "operator@mshd.com",
    "phone": "13800138001",
    "role": "operator",
    "department": "数据处理部"
  }'
```

## 方法2：使用MySQL直接插入（需要密码哈希）

如果你想直接在数据库中插入，需要先获取BCrypt加密后的密码：

1. 访问在线BCrypt工具：https://bcrypt-generator.com/
2. 输入密码：`admin123`
3. 使用Rounds: 10
4. 复制生成的哈希值
5. 在数据库中执行：

```sql
INSERT INTO sys_user (username, password, real_name, email, phone, role, department, status, create_time, update_time)
VALUES (
    'admin',
    '你生成的BCrypt哈希值',
    '系统管理员',
    'admin@mshd.com',
    '13800138000',
    'admin',
    '系统管理部',
    1,
    NOW(),
    NOW()
);
```

## 测试登录

创建账户后，访问 http://localhost:3000 会自动跳转到登录页。

使用以下凭据登录：
- 用户名：`admin`
- 密码：`admin123`

## 用户角色说明

- **admin**: 系统管理员（最高权限）
- **analyst**: 数据分析师
- **commander**: 应急指挥人员
- **operator**: 普通操作员

## 注意事项

⚠️ 这些是测试账户，生产环境中请：
1. 使用更强的密码
2. 定期更换密码
3. 根据实际需求设置权限
4. 删除或禁用不需要的测试账户
