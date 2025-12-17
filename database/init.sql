-- MSHD多源异构灾情数据管理服务系统 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mshd DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mshd;

-- 灾情数据表
CREATE TABLE IF NOT EXISTS disaster_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    encoded_id VARCHAR(36) NOT NULL UNIQUE COMMENT '一体化编码ID',
    earthquake_code VARCHAR(26) COMMENT '震情码',
    geographic_code VARCHAR(12) COMMENT '地理信息码',
    location VARCHAR(200) COMMENT '地理位置描述',
    longitude DOUBLE COMMENT '经度',
    latitude DOUBLE COMMENT '纬度',
    time_code VARCHAR(14) COMMENT '时间码',
    earthquake_time DATETIME COMMENT '地震发生时间',
    source_code VARCHAR(3) COMMENT '来源码',
    source_type VARCHAR(50) COMMENT '数据来源类型',
    carrier_code VARCHAR(1) COMMENT '载体码',
    carrier_type VARCHAR(20) COMMENT '载体类型',
    disaster_code VARCHAR(6) COMMENT '灾情码',
    disaster_category VARCHAR(1) COMMENT '灾害大类代码',
    disaster_subcategory VARCHAR(2) COMMENT '灾害子类代码',
    indicator_code VARCHAR(3) COMMENT '灾情指标代码',
    content TEXT COMMENT '灾情数据内容',
    file_path VARCHAR(500) COMMENT '多媒体文件路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    ttl INT COMMENT '数据有效期(小时)',
    status INT DEFAULT 0 COMMENT '数据状态: 0待处理 1已编码 2已存储 3已过期 4已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_encoded_id (encoded_id),
    INDEX idx_earthquake_code (earthquake_code),
    INDEX idx_source_code (source_code),
    INDEX idx_carrier_type (carrier_type),
    INDEX idx_disaster_category (disaster_category),
    INDEX idx_earthquake_time (earthquake_time),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='灾情数据表';

-- 数据源配置表
CREATE TABLE IF NOT EXISTS data_source (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    source_name VARCHAR(100) NOT NULL COMMENT '数据源名称',
    source_code VARCHAR(3) NOT NULL UNIQUE COMMENT '数据源编码',
    source_type VARCHAR(50) NOT NULL COMMENT '数据源类型',
    source_url VARCHAR(500) COMMENT '数据源URL',
    protocol VARCHAR(20) COMMENT '接口协议',
    auth_type VARCHAR(20) COMMENT '认证方式',
    credentials VARCHAR(500) COMMENT '认证凭证',
    data_format VARCHAR(20) COMMENT '数据格式',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    poll_interval INT COMMENT '轮询间隔(秒)',
    last_sync_time DATETIME COMMENT '最后同步时间',
    sync_status INT COMMENT '同步状态: 0未同步 1同步中 2同步成功 3同步失败',
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_source_code (source_code),
    INDEX idx_source_type (source_type),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源配置表';

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL COMMENT '角色',
    department VARCHAR(100) COMMENT '部门',
    status INT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 插入默认管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, role, department, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHs', '系统管理员', 'admin', '技术部', 1);

-- 插入示例数据源
INSERT INTO data_source (source_name, source_code, source_type, source_url, protocol, data_format, enabled)
VALUES
('前方地震应急指挥部', '100', 'business', 'http://localhost:8080/api/data/source1', 'HTTP', 'JSON', TRUE),
('后方地震应急指挥部', '101', 'business', 'http://localhost:8080/api/data/source2', 'HTTP', 'JSON', TRUE),
('互联网感知', '200', 'ubiquitous', 'http://localhost:8080/api/data/source3', 'HTTP', 'JSON', TRUE),
('通信网感知', '201', 'ubiquitous', 'http://localhost:8080/api/data/source4', 'HTTP', 'JSON', TRUE);

-- 插入示例灾情数据
INSERT INTO disaster_data (
    encoded_id, earthquake_code, geographic_code, location, longitude, latitude,
    time_code, earthquake_time, source_code, source_type, carrier_code, carrier_type,
    disaster_code, disaster_category, disaster_subcategory, indicator_code,
    content, ttl, status
) VALUES (
    '63262620020620210522020400101030200',
    '63262620020620210522020400',
    '632626200206',
    '青海省海东市民和回族土族自治县',
    102.8394,
    36.3295,
    '20210522020400',
    '2021-05-22 02:04:00',
    '101',
    'business',
    '0',
    'text',
    '302001',
    '3',
    '02',
    '001',
    '砖木房屋一般损坏面积500平方米',
    168,
    2
);

SELECT 'Database initialization completed successfully!' AS message;
