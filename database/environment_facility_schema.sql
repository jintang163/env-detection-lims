-- ========================================
-- 环境与设施管理模块数据库设计
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- ========================================

USE lims;

-- ========================================
-- 实验室房间表
-- ========================================
CREATE TABLE IF NOT EXISTS env_room (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    room_no VARCHAR(50) NOT NULL COMMENT '房间编号',
    room_name VARCHAR(100) NOT NULL COMMENT '房间名称',
    building VARCHAR(50) COMMENT '所属楼宇',
    floor VARCHAR(20) COMMENT '楼层',
    area DECIMAL(10,2) COMMENT '面积(㎡)',
    room_type VARCHAR(50) COMMENT '房间类型：1理化实验室 2微生物实验室 3仪器室 4试剂室 5样品室 6气瓶室 7办公室',
    temperature_min DECIMAL(5,2) COMMENT '温度下限(℃)',
    temperature_max DECIMAL(5,2) COMMENT '温度上限(℃)',
    humidity_min DECIMAL(5,2) COMMENT '湿度下限(%)',
    humidity_max DECIMAL(5,2) COMMENT '湿度上限(%)',
    pressure_min DECIMAL(10,2) COMMENT '压差下限(Pa)',
    pressure_max DECIMAL(10,2) COMMENT '压差上限(Pa)',
    noise_max DECIMAL(5,2) COMMENT '噪声上限(dB)',
    dept_id BIGINT COMMENT '所属部门ID',
    dept_name VARCHAR(100) COMMENT '所属部门名称',
    manager_id BIGINT COMMENT '负责人ID',
    manager_name VARCHAR(50) COMMENT '负责人姓名',
    status TINYINT DEFAULT 1 COMMENT '状态：0停用 1正常 2维护中',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_no (room_no),
    KEY idx_room_name (room_name),
    KEY idx_room_type (room_type),
    KEY idx_status (status),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室房间表';

-- ========================================
-- 设施表（通风橱、超净台等）
-- ========================================
CREATE TABLE IF NOT EXISTS env_facility (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    facility_no VARCHAR(50) NOT NULL COMMENT '设施编号',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    facility_type TINYINT NOT NULL COMMENT '设施类型：1通风橱 2超净台 3生物安全柜 4空调系统 5纯水系统 6供气系统 7排风系统 8其他',
    model VARCHAR(100) COMMENT '型号',
    specification VARCHAR(200) COMMENT '规格',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    room_id BIGINT NOT NULL COMMENT '所属房间ID',
    room_no VARCHAR(50) COMMENT '所属房间编号',
    room_name VARCHAR(100) COMMENT '所属房间名称',
    install_date DATE COMMENT '安装日期',
    warranty_date DATE COMMENT '质保到期日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0停用 1正常 2维护中 3故障',
    last_maintenance_date DATE COMMENT '上次维护日期',
    next_maintenance_date DATE COMMENT '下次维护日期',
    maintenance_cycle INT COMMENT '维护周期(天)',
    dept_id BIGINT COMMENT '所属部门ID',
    dept_name VARCHAR(100) COMMENT '所属部门名称',
    manager_id BIGINT COMMENT '负责人ID',
    manager_name VARCHAR(50) COMMENT '负责人姓名',
    technical_params TEXT COMMENT '技术参数',
    remark VARCHAR(1000) COMMENT '备注',
    attachments VARCHAR(500) COMMENT '附件',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_facility_no (facility_no),
    KEY idx_facility_name (facility_name),
    KEY idx_facility_type (facility_type),
    KEY idx_room_id (room_id),
    KEY idx_status (status),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施表';

-- ========================================
-- 环境监控阈值配置表
-- ========================================
CREATE TABLE IF NOT EXISTS env_monitor_threshold (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    monitor_type TINYINT NOT NULL COMMENT '监控类型：1温度 2湿度 3压差 4噪声',
    monitor_point VARCHAR(50) NOT NULL COMMENT '监控点（房间编号或设施编号）',
    monitor_point_name VARCHAR(100) COMMENT '监控点名称',
    min_value DECIMAL(10,2) COMMENT '最小值',
    max_value DECIMAL(10,2) COMMENT '最大值',
    unit VARCHAR(20) COMMENT '单位',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用：0禁用 1启用',
    warn_level TINYINT DEFAULT 1 COMMENT '预警等级：1一般 2重要 3紧急',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_monitor_type (monitor_type),
    KEY idx_monitor_point (monitor_point)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境监控阈值配置表';

-- ========================================
-- 环境监控数据表
-- ========================================
CREATE TABLE IF NOT EXISTS env_monitor_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    monitor_point VARCHAR(50) NOT NULL COMMENT '监控点（房间编号或设施编号）',
    monitor_point_name VARCHAR(100) COMMENT '监控点名称',
    monitor_type TINYINT NOT NULL COMMENT '监控类型：1温度 2湿度 3压差 4噪声',
    monitor_value DECIMAL(10,2) NOT NULL COMMENT '监控数值',
    unit VARCHAR(20) COMMENT '单位',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    collect_method TINYINT DEFAULT 1 COMMENT '采集方式：1人工录入 2在线采集',
    is_warning TINYINT DEFAULT 0 COMMENT '是否预警：0正常 1预警',
    warn_level TINYINT DEFAULT 1 COMMENT '预警等级：1一般 2重要 3紧急',
    warn_message VARCHAR(500) COMMENT '预警信息',
    collector_id BIGINT COMMENT '采集人ID',
    collector_name VARCHAR(50) COMMENT '采集人姓名',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_monitor_point (monitor_point),
    KEY idx_monitor_type (monitor_type),
    KEY idx_collect_time (collect_time),
    KEY idx_is_warning (is_warning)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境监控数据表';

-- ========================================
-- 环境预警记录表
-- ========================================
CREATE TABLE IF NOT EXISTS env_warning_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    monitor_data_id BIGINT NOT NULL COMMENT '监控数据ID',
    monitor_point VARCHAR(50) NOT NULL COMMENT '监控点',
    monitor_point_name VARCHAR(100) COMMENT '监控点名称',
    monitor_type TINYINT NOT NULL COMMENT '监控类型：1温度 2湿度 3压差 4噪声',
    monitor_value DECIMAL(10,2) NOT NULL COMMENT '监控数值',
    threshold_min DECIMAL(10,2) COMMENT '阈值下限',
    threshold_max DECIMAL(10,2) COMMENT '阈值上限',
    warn_level TINYINT DEFAULT 1 COMMENT '预警等级：1一般 2重要 3紧急',
    warn_message VARCHAR(500) COMMENT '预警信息',
    warn_time DATETIME NOT NULL COMMENT '预警时间',
    status TINYINT DEFAULT 0 COMMENT '处理状态：0待处理 1处理中 2已处理 3已忽略',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名',
    handle_time DATETIME COMMENT '处理时间',
    handle_result VARCHAR(1000) COMMENT '处理结果',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_monitor_point (monitor_point),
    KEY idx_monitor_type (monitor_type),
    KEY idx_warn_time (warn_time),
    KEY idx_status (status),
    KEY idx_handler_id (handler_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='环境预警记录表';

-- ========================================
-- 设施维护记录表
-- ========================================
CREATE TABLE IF NOT EXISTS env_facility_maintenance (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    facility_id BIGINT NOT NULL COMMENT '设施ID',
    facility_no VARCHAR(50) NOT NULL COMMENT '设施编号',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    maintenance_type TINYINT NOT NULL COMMENT '维护类型：1日常保养 2定期维护 3预防性维护 4故障维修',
    maintenance_date DATE NOT NULL COMMENT '维护日期',
    maintainer_id BIGINT COMMENT '维护人ID',
    maintainer_name VARCHAR(50) COMMENT '维护人姓名',
    maintenance_unit VARCHAR(100) COMMENT '维护单位',
    content TEXT NOT NULL COMMENT '维护内容',
    result TINYINT NOT NULL COMMENT '维护结果：1良好 2一般 3需维修',
    fault_description TEXT COMMENT '故障描述（故障维修时填写）',
    replaced_parts TEXT COMMENT '更换部件',
    next_maintenance_date DATE COMMENT '下次维护日期',
    cost DECIMAL(12,2) COMMENT '维护费用',
    attachments VARCHAR(500) COMMENT '附件',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_facility_id (facility_id),
    KEY idx_maintenance_date (maintenance_date),
    KEY idx_maintainer_id (maintainer_id),
    KEY idx_maintenance_type (maintenance_type),
    KEY idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施维护记录表';

-- ========================================
-- 设施维护计划表
-- ========================================
CREATE TABLE IF NOT EXISTS env_facility_maintenance_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    facility_id BIGINT NOT NULL COMMENT '设施ID',
    facility_no VARCHAR(50) NOT NULL COMMENT '设施编号',
    facility_name VARCHAR(100) NOT NULL COMMENT '设施名称',
    maintenance_type TINYINT NOT NULL COMMENT '维护类型：1日常保养 2定期维护 3预防性维护',
    cycle_days INT NOT NULL COMMENT '维护周期（天）',
    last_maintenance_date DATE COMMENT '上次维护日期',
    next_maintenance_date DATE NOT NULL COMMENT '下次维护日期',
    remind_days INT DEFAULT 30 COMMENT '提醒天数',
    status TINYINT DEFAULT 0 COMMENT '计划状态：0待执行 1已完成 2已过期',
    manager_id BIGINT COMMENT '负责人ID',
    manager_name VARCHAR(50) COMMENT '负责人姓名',
    content_template TEXT COMMENT '维护内容模板',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_facility_id (facility_id),
    KEY idx_next_maintenance_date (next_maintenance_date),
    KEY idx_status (status),
    KEY idx_manager_id (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设施维护计划表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入示例房间数据
INSERT INTO env_room (room_no, room_name, building, floor, area, room_type, temperature_min, temperature_max, humidity_min, humidity_max, pressure_min, pressure_max, noise_max, dept_id, dept_name, manager_id, manager_name, status) VALUES
('R-A101', '理化实验室1', 'A栋', '1层', 60.00, 1, 18.00, 26.00, 40.00, 60.00, NULL, NULL, 60.00, 1, '检测中心', 1, '张三', 1),
('R-A102', '理化实验室2', 'A栋', '1层', 60.00, 1, 18.00, 26.00, 40.00, 60.00, NULL, NULL, 60.00, 1, '检测中心', 2, '李四', 1),
('R-A201', '仪器室1', 'A栋', '2层', 80.00, 3, 20.00, 25.00, 45.00, 55.00, NULL, NULL, 55.00, 1, '检测中心', 1, '张三', 1),
('R-A202', '仪器室2', 'A栋', '2层', 80.00, 3, 20.00, 25.00, 45.00, 55.00, NULL, NULL, 55.00, 1, '检测中心', 2, '李四', 1),
('R-A301', '微生物实验室', 'A栋', '3层', 50.00, 2, 18.00, 26.00, 45.00, 65.00, 10.00, 15.00, 60.00, 1, '检测中心', 3, '王五', 1),
('R-B101', '试剂室', 'B栋', '1层', 40.00, 4, 15.00, 25.00, 30.00, 60.00, NULL, NULL, NULL, 1, '检测中心', 1, '张三', 1),
('R-B102', '样品室', 'B栋', '1层', 35.00, 5, 4.00, 8.00, 30.00, 60.00, NULL, NULL, NULL, 1, '检测中心', 2, '李四', 1),
('R-B201', '气瓶室', 'B栋', '2层', 20.00, 6, 15.00, 30.00, NULL, NULL, NULL, NULL, NULL, 1, '检测中心', 3, '王五', 1);

-- 插入示例设施数据
INSERT INTO env_facility (facility_no, facility_name, facility_type, model, specification, manufacturer, room_id, room_no, room_name, install_date, warranty_date, status, last_maintenance_date, next_maintenance_date, maintenance_cycle, dept_id, dept_name, manager_id, manager_name, technical_params) VALUES
('F-2024-001', '通风橱1', 1, 'LABCONCO 3000', '1200*800*2350mm', 'LABCONCO', 1, 'R-A101', '理化实验室1', '2024-01-10', '2026-01-09', 1, '2025-05-10', '2025-08-10', 90, 1, '检测中心', 1, '张三', '风速范围：0.3-0.5m/s，噪音≤60dB'),
('F-2024-002', '通风橱2', 1, 'LABCONCO 3000', '1200*800*2350mm', 'LABCONCO', 1, 'R-A101', '理化实验室1', '2024-01-10', '2026-01-09', 1, '2025-05-10', '2025-08-10', 90, 1, '检测中心', 1, '张三', '风速范围：0.3-0.5m/s，噪音≤60dB'),
('F-2024-003', '通风橱3', 1, 'Waldner SCALA', '1500*800*2350mm', 'Waldner', 2, 'R-A102', '理化实验室2', '2024-02-15', '2026-02-14', 1, '2025-05-15', '2025-08-15', 90, 1, '检测中心', 2, '李四', '风速范围：0.3-0.5m/s，噪音≤60dB'),
('F-2024-004', '超净台1', 2, 'AIRTECH SW-CJ-1FD', '1200*700*1800mm', 'AIRTECH', 5, 'R-A301', '微生物实验室', '2024-03-01', '2026-02-28', 1, '2025-05-01', '2025-08-01', 90, 1, '检测中心', 3, '王五', '洁净等级：100级，风速：0.35-0.55m/s'),
('F-2024-005', '超净台2', 2, 'AIRTECH SW-CJ-2FD', '1500*700*1800mm', 'AIRTECH', 5, 'R-A301', '微生物实验室', '2024-03-01', '2026-02-28', 2, '2025-05-01', NULL, NULL, 1, '检测中心', 3, '王五', '洁净等级：100级，风速：0.35-0.55m/s'),
('F-2024-006', '生物安全柜', 3, 'Thermo 1300 Series A2', '1345*790*1340mm', 'Thermo Fisher', 5, 'R-A301', '微生物实验室', '2024-03-15', '2026-03-14', 1, '2025-03-15', '2025-09-15', 180, 1, '检测中心', 3, '王五', '洁净等级：100级，排气量：700m³/h'),
('F-2024-007', '中央空调系统', 4, '格力 GMV-H250WL/A', '多联机', '格力', 0, 'R-A000', '整栋A栋', '2024-01-01', '2026-12-31', 1, '2025-04-01', '2025-10-01', 180, 1, '检测中心', 1, '张三', '制冷量：25kW，制热量：28kW'),
('F-2024-008', '纯水系统', 5, 'Millipore Direct-Q 3', '产水量：3L/h', 'Millipore', 2, 'R-A102', '理化实验室2', '2024-02-20', '2026-02-19', 1, '2025-05-20', '2025-08-20', 90, 1, '检测中心', 2, '李四', '电阻率：18.2MΩ·cm，TOC＜5ppb'),
('F-2024-009', '氮气发生器', 6, 'Peak Genius 1024', '产气量：24L/min', 'Peak', 3, 'R-A201', '仪器室1', '2024-03-10', '2026-03-09', 1, '2025-05-10', '2025-08-10', 90, 1, '检测中心', 1, '张三', '纯度：99.999%，压力：0-7bar可调');

-- 插入环境监控阈值配置
INSERT INTO env_monitor_threshold (monitor_type, monitor_point, monitor_point_name, min_value, max_value, unit, is_enabled, warn_level) VALUES
(1, 'R-A101', '理化实验室1', 18.00, 26.00, '℃', 1, 2),
(2, 'R-A101', '理化实验室1', 40.00, 60.00, '%', 1, 2),
(4, 'R-A101', '理化实验室1', 0, 60.00, 'dB', 1, 1),
(1, 'R-A102', '理化实验室2', 18.00, 26.00, '℃', 1, 2),
(2, 'R-A102', '理化实验室2', 40.00, 60.00, '%', 1, 2),
(1, 'R-A201', '仪器室1', 20.00, 25.00, '℃', 1, 2),
(2, 'R-A201', '仪器室1', 45.00, 55.00, '%', 1, 2),
(4, 'R-A201', '仪器室1', 0, 55.00, 'dB', 1, 1),
(1, 'R-A202', '仪器室2', 20.00, 25.00, '℃', 1, 2),
(2, 'R-A202', '仪器室2', 45.00, 55.00, '%', 1, 2),
(1, 'R-A301', '微生物实验室', 18.00, 26.00, '℃', 1, 2),
(2, 'R-A301', '微生物实验室', 45.00, 65.00, '%', 1, 2),
(3, 'R-A301', '微生物实验室', 10.00, 15.00, 'Pa', 1, 3),
(1, 'R-B101', '试剂室', 15.00, 25.00, '℃', 1, 1),
(2, 'R-B101', '试剂室', 30.00, 60.00, '%', 1, 1),
(1, 'R-B102', '样品室', 4.00, 8.00, '℃', 1, 3),
(2, 'R-B102', '样品室', 30.00, 60.00, '%', 1, 1);

-- 插入示例环境监控数据
INSERT INTO env_monitor_data (monitor_point, monitor_point_name, monitor_type, monitor_value, unit, collect_time, collect_method, is_warning, warn_level, warn_message, collector_id, collector_name) VALUES
('R-A101', '理化实验室1', 1, 22.5, '℃', '2025-06-08 08:00:00', 1, 0, 1, NULL, 1, '张三'),
('R-A101', '理化实验室1', 2, 52.0, '%', '2025-06-08 08:00:00', 1, 0, 1, NULL, 1, '张三'),
('R-A101', '理化实验室1', 4, 55.0, 'dB', '2025-06-08 08:00:00', 1, 0, 1, NULL, 1, '张三'),
('R-A101', '理化实验室1', 1, 23.0, '℃', '2025-06-08 12:00:00', 1, 0, 1, NULL, 2, '李四'),
('R-A101', '理化实验室1', 2, 55.0, '%', '2025-06-08 12:00:00', 1, 0, 1, NULL, 2, '李四'),
('R-A101', '理化实验室1', 1, 27.5, '℃', '2025-06-08 16:00:00', 1, 1, 2, '温度超出上限26.0℃', 2, '李四'),
('R-A101', '理化实验室1', 2, 65.0, '%', '2025-06-08 16:00:00', 1, 1, 2, '湿度超出上限60.0%', 2, '李四'),
('R-A102', '理化实验室2', 1, 21.5, '℃', '2025-06-08 08:30:00', 1, 0, 1, NULL, 2, '李四'),
('R-A102', '理化实验室2', 2, 48.0, '%', '2025-06-08 08:30:00', 1, 0, 1, NULL, 2, '李四'),
('R-A201', '仪器室1', 1, 23.0, '℃', '2025-06-08 09:00:00', 1, 0, 1, NULL, 1, '张三'),
('R-A201', '仪器室1', 2, 50.0, '%', '2025-06-08 09:00:00', 1, 0, 1, NULL, 1, '张三'),
('R-A201', '仪器室1', 4, 52.0, 'dB', '2025-06-08 09:00:00', 1, 0, 1, NULL, 1, '张三'),
('R-A202', '仪器室2', 1, 22.0, '℃', '2025-06-08 09:30:00', 1, 0, 1, NULL, 2, '李四'),
('R-A202', '仪器室2', 2, 49.0, '%', '2025-06-08 09:30:00', 1, 0, 1, NULL, 2, '李四'),
('R-A301', '微生物实验室', 1, 22.0, '℃', '2025-06-08 10:00:00', 1, 0, 1, NULL, 3, '王五'),
('R-A301', '微生物实验室', 2, 55.0, '%', '2025-06-08 10:00:00', 1, 0, 1, NULL, 3, '王五'),
('R-A301', '微生物实验室', 3, 12.5, 'Pa', '2025-06-08 10:00:00', 1, 0, 1, NULL, 3, '王五'),
('R-A301', '微生物实验室', 3, 8.0, 'Pa', '2025-06-08 14:00:00', 1, 1, 3, '压差异常，低于下限10.0Pa', 3, '王五'),
('R-B101', '试剂室', 1, 20.0, '℃', '2025-06-08 10:30:00', 1, 0, 1, NULL, 1, '张三'),
('R-B101', '试剂室', 2, 45.0, '%', '2025-06-08 10:30:00', 1, 0, 1, NULL, 1, '张三'),
('R-B102', '样品室', 1, 6.0, '℃', '2025-06-08 11:00:00', 1, 0, 1, NULL, 2, '李四'),
('R-B102', '样品室', 2, 40.0, '%', '2025-06-08 11:00:00', 1, 0, 1, NULL, 2, '李四');

-- 插入示例预警记录
INSERT INTO env_warning_record (monitor_data_id, monitor_point, monitor_point_name, monitor_type, monitor_value, threshold_min, threshold_max, warn_level, warn_message, warn_time, status, handler_id, handler_name, handle_time, handle_result) VALUES
(6, 'R-A101', '理化实验室1', 1, 27.5, 18.00, 26.00, 2, '温度超出上限26.0℃', '2025-06-08 16:00:00', 2, 1, '张三', '2025-06-08 16:30:00', '已调整空调温度，恢复正常'),
(7, 'R-A101', '理化实验室1', 2, 65.0, 40.00, 60.00, 2, '湿度超出上限60.0%', '2025-06-08 16:00:00', 1, 1, '张三', NULL, NULL),
(18, 'R-A301', '微生物实验室', 3, 8.0, 10.00, 15.00, 3, '压差异常，低于下限10.0Pa', '2025-06-08 14:00:00', 0, NULL, NULL, NULL, NULL);

-- 插入示例设施维护记录
INSERT INTO env_facility_maintenance (facility_id, facility_no, facility_name, maintenance_type, maintenance_date, maintainer_id, maintainer_name, maintenance_unit, content, result, next_maintenance_date, cost) VALUES
(1, 'F-2024-001', '通风橱1', 1, '2025-05-10', 1, '张三', NULL, '清洁玻璃视窗，检查排风系统，更换高效过滤器', 1, '2025-08-10', 300.00),
(2, 'F-2024-002', '通风橱2', 1, '2025-05-10', 1, '张三', NULL, '清洁玻璃视窗，检查排风系统，更换高效过滤器', 1, '2025-08-10', 300.00),
(3, 'F-2024-003', '通风橱3', 1, '2025-05-15', 2, '李四', NULL, '清洁玻璃视窗，检查排风系统', 1, '2025-08-15', 200.00),
(4, 'F-2024-004', '超净台1', 1, '2025-05-01', 3, '王五', NULL, '清洁工作台面，更换紫外灯，检查风速', 1, '2025-08-01', 350.00),
(5, 'F-2024-005', '超净台2', 4, '2025-06-01', 3, '王五', 'AIRTECH售后', '风速不稳，更换风机控制模块', 1, NULL, 2500.00),
(6, 'F-2024-006', '生物安全柜', 2, '2025-03-15', 3, '王五', 'Thermo售后', '全面检测，更换HEPA过滤器，泄漏测试', 1, '2025-09-15', 5000.00),
(7, 'F-2024-007', '中央空调系统', 2, '2025-04-01', 1, '张三', '格力售后', '清洗滤网，检查压缩机运行状态，添加制冷剂', 1, '2025-10-01', 1500.00),
(8, 'F-2024-008', '纯水系统', 1, '2025-05-20', 2, '李四', NULL, '更换预处理滤芯，检查电阻率', 1, '2025-08-20', 800.00),
(9, 'F-2024-009', '氮气发生器', 1, '2025-05-10', 1, '张三', NULL, '清洁过滤器，检查压力输出', 1, '2025-08-10', 150.00);

-- 插入示例设施维护计划
INSERT INTO env_facility_maintenance_plan (facility_id, facility_no, facility_name, maintenance_type, cycle_days, last_maintenance_date, next_maintenance_date, remind_days, status, manager_id, manager_name, content_template) VALUES
(1, 'F-2024-001', '通风橱1', 1, 90, '2025-05-10', '2025-08-10', 30, 0, 1, '张三', '1. 清洁玻璃视窗和内表面\n2. 检查排风系统运行状态\n3. 测试面风速\n4. 更换高效过滤器（按需）'),
(2, 'F-2024-002', '通风橱2', 1, 90, '2025-05-10', '2025-08-10', 30, 0, 1, '张三', '1. 清洁玻璃视窗和内表面\n2. 检查排风系统运行状态\n3. 测试面风速\n4. 更换高效过滤器（按需）'),
(3, 'F-2024-003', '通风橱3', 1, 90, '2025-05-15', '2025-08-15', 30, 0, 2, '李四', '1. 清洁玻璃视窗和内表面\n2. 检查排风系统运行状态\n3. 测试面风速'),
(4, 'F-2024-004', '超净台1', 1, 90, '2025-05-01', '2025-08-01', 30, 0, 3, '王五', '1. 清洁工作台面\n2. 更换紫外灯（累计使用800小时）\n3. 检查风速\n4. 沉降菌测试'),
(6, 'F-2024-006', '生物安全柜', 2, 180, '2025-03-15', '2025-09-15', 60, 0, 3, '王五', '1. 外观和功能检查\n2. 更换HEPA过滤器\n3. 气流速度测试\n4. 泄漏测试\n5. 紫外灯强度检测'),
(7, 'F-2024-007', '中央空调系统', 2, 180, '2025-04-01', '2025-10-01', 60, 0, 1, '张三', '1. 清洗滤网\n2. 检查压缩机运行状态\n3. 添加制冷剂（按需）\n4. 检查温控精度'),
(8, 'F-2024-008', '纯水系统', 1, 90, '2025-05-20', '2025-08-20', 30, 0, 2, '李四', '1. 更换预处理滤芯\n2. 更换反渗透膜（按需）\n3. 检查电阻率和TOC\n4. 消毒处理'),
(9, 'F-2024-009', '氮气发生器', 1, 90, '2025-05-10', '2025-08-10', 30, 0, 1, '张三', '1. 清洁前置过滤器\n2. 检查分子筛\n3. 检查压力输出稳定性\n4. 检查纯度');
