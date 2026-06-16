-- 创建用户表
CREATE TABLE user_info (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  user_type VARCHAR(20) NOT NULL, -- 'elderly'/'family'
  openid VARCHAR(100) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建设备表
CREATE TABLE edge_device (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  device_id VARCHAR(50) NOT NULL UNIQUE,
  location VARCHAR(100),
  user_id BIGINT,
  status VARCHAR(20) DEFAULT 'offline',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入初始数据
INSERT INTO user_info (name, user_type, openid) VALUES 
('测试老人', 'elderly', 'test_openid_elderly'),
('测试家属', 'family', 'test_openid_family');

INSERT INTO edge_device (device_id, location, user_id, status) VALUES 
('radar_001', '客厅', 1, 'online');
