-- 清理旧用户
DELETE FROM sys_user WHERE username = 'admin';

-- 插入管理员账户
-- 用户名: admin
-- 密码: admin123
-- BCrypt加密后的密码
INSERT INTO sys_user (username, password, real_name, email, role, department, status)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'Admin',
    'admin@mshd.com',
    'admin',
    'IT Department',
    1
);

-- 验证
SELECT id, username, real_name, role, LEFT(password, 20) as password_prefix FROM sys_user;
