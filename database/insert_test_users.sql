-- 添加测试用户
-- 密码使用BCrypt加密，明文密码为: admin123

-- 删除已存在的admin用户（如果有）
DELETE FROM sys_user WHERE username = 'admin';

-- 插入管理员账户
-- 用户名: admin
-- 密码: admin123
-- BCrypt加密后的密码 (使用默认强度10)
INSERT INTO sys_user (username, password, real_name, email, phone, role, department, status, create_time, update_time, remark)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',  -- 密码: admin123
    '系统管理员',
    'admin@mshd.com',
    '13800138000',
    'admin',
    '系统管理部',
    1,
    NOW(),
    NOW(),
    '系统默认管理员账户'
);

-- 插入普通用户账户（可选）
INSERT INTO sys_user (username, password, real_name, email, phone, role, department, status, create_time, update_time, remark)
VALUES (
    'operator',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',  -- 密码: admin123
    '操作员',
    'operator@mshd.com',
    '13800138001',
    'operator',
    '数据处理部',
    1,
    NOW(),
    NOW(),
    '普通操作员账户'
);

-- 查询验证
SELECT id, username, real_name, role, department, status, create_time
FROM sys_user;
