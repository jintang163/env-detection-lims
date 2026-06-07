-- ========================================
-- 环境检测LIMS系统 - 质量与质控管理模块
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- ========================================

USE lims;

-- ========================================
-- 1. 质控规则表 (Westgard多规则配置)
-- ========================================
CREATE TABLE qc_rule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    rule_code VARCHAR(30) NOT NULL COMMENT '规则代码 如1_3s',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type TINYINT DEFAULT 1 COMMENT '规则类型 1警告 2失控',
    description VARCHAR(500) COMMENT '规则描述',
    sd_multiple DECIMAL(5,2) COMMENT 'SD倍数',
    consecutive_points INT COMMENT '连续点数',
    within_run TINYINT DEFAULT 0 COMMENT '是否批内判断 0否 1是',
    across_run TINYINT DEFAULT 0 COMMENT '是否跨批判断 0否 1是',
    formula VARCHAR(500) COMMENT '计算公式',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用 0禁用 1启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0未删除 1已删除',
    remark VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_code (rule_code),
    KEY idx_rule_type (rule_type),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质控规则表';

-- ========================================
-- 2. 质控样品表
-- ========================================
CREATE TABLE qc_sample (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    sample_no VARCHAR(50) NOT NULL COMMENT '样品编号',
    sample_name VARCHAR(200) NOT NULL COMMENT '样品名称',
    sample_type VARCHAR(30) NOT NULL COMMENT '样品类型 STANDARD标准样 SPIKE加标样 PARALLEL平行样 BLANK空白样',
    batch_no VARCHAR(50) COMMENT '批号',
    concentration DECIMAL(15,6) COMMENT '浓度值',
    unit VARCHAR(20) COMMENT '计量单位',
    uncertainty DECIMAL(15,6) COMMENT '不确定度',
    stock_quantity DECIMAL(10,2) COMMENT '库存数量',
    prepare_date DATE COMMENT '配制日期',
    expire_date DATE COMMENT '有效期至',
    storage_condition VARCHAR(200) COMMENT '保存条件',
    traceability VARCHAR(500) COMMENT '溯源信息',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常 2已过期',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0未删除 1已删除',
    remark VARCHAR(1000) COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sample_no (sample_no),
    KEY idx_sample_type (sample_type),
    KEY idx_batch_no (batch_no),
    KEY idx_status (status),
    KEY idx_expire_date (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质控样品表';

-- ========================================
-- 3. 质控样品配制记录表
-- ========================================
CREATE TABLE qc_sample_prepare (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    prepare_no VARCHAR(50) NOT NULL COMMENT '配制单号',
    sample_name VARCHAR(200) NOT NULL COMMENT '样品名称',
    sample_type VARCHAR(30) NOT NULL COMMENT '样品类型 STANDARD标准样 SPIKE加标样 PARALLEL平行样 BLANK空白样',
    batch_no VARCHAR(50) COMMENT '批号',
    prepare_volume DECIMAL(10,2) COMMENT '配制体积',
    volume_unit VARCHAR(20) COMMENT '体积单位',
    concentration DECIMAL(15,6) COMMENT '浓度值',
    unit VARCHAR(20) COMMENT '计量单位',
    prepare_method VARCHAR(1000) COMMENT '配制方法',
    reagents TEXT COMMENT '使用试剂(JSON格式)',
    instruments TEXT COMMENT '使用仪器(JSON格式)',
    prepare_by BIGINT COMMENT '配制人ID',
    prepare_name VARCHAR(50) COMMENT '配制人姓名',
    verify_by BIGINT COMMENT '校核人ID',
    verify_name VARCHAR(50) COMMENT '校核人姓名',
    environment VARCHAR(500) COMMENT '环境条件',
    prepare_date DATE COMMENT '配制日期',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1待校核 2已完成 3已驳回',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0未删除 1已删除',
    remark VARCHAR(1000) COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prepare_no (prepare_no),
    KEY idx_sample_type (sample_type),
    KEY idx_batch_no (batch_no),
    KEY idx_prepare_by (prepare_by),
    KEY idx_status (status),
    KEY idx_prepare_date (prepare_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质控样品配制记录表';

-- ========================================
-- 4. 质控计划表
-- ========================================
CREATE TABLE qc_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plan_no VARCHAR(50) NOT NULL COMMENT '计划编号',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    project_name VARCHAR(200) COMMENT '检测项目',
    method_name VARCHAR(200) COMMENT '检测方法',
    cycle_type VARCHAR(30) NOT NULL COMMENT '周期类型 BATCH每批次 DAILY每日 WEEKLY每周 MONTHLY每月',
    status TINYINT DEFAULT 0 COMMENT '状态 0未开始 1执行中 2已暂停 3已结束',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    qc_samples TEXT COMMENT '质控样配置(JSON格式)',
    qc_rules TEXT COMMENT '质控规则配置(JSON格式)',
    mean_value DECIMAL(15,6) COMMENT '均值',
    sd_value DECIMAL(15,6) COMMENT '标准差',
    warning_limit DECIMAL(15,6) COMMENT '警告限(2SD)',
    control_limit DECIMAL(15,6) COMMENT '控制限(3SD)',
    reminder_enabled TINYINT DEFAULT 0 COMMENT '是否开启提醒 0关闭 1开启',
    reminder_type TEXT COMMENT '提醒方式(JSON格式)',
    reminder_advance INT DEFAULT 0 COMMENT '提前提醒分钟数',
    reminder_users TEXT COMMENT '提醒用户列表(JSON格式)',
    description VARCHAR(1000) COMMENT '计划描述',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0未删除 1已删除',
    remark VARCHAR(1000) COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_plan_no (plan_no),
    KEY idx_cycle_type (cycle_type),
    KEY idx_status (status),
    KEY idx_start_date (start_date),
    KEY idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质控计划表';

-- ========================================
-- 5. 质控执行记录表
-- ========================================
CREATE TABLE qc_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plan_id BIGINT NOT NULL COMMENT '质控计划ID',
    plan_no VARCHAR(50) COMMENT '计划编号',
    record_no VARCHAR(50) NOT NULL COMMENT '记录编号',
    execute_date DATE COMMENT '执行日期',
    batch_no VARCHAR(50) COMMENT '批次号',
    qc_sample_id BIGINT COMMENT '质控样品ID',
    qc_sample_type VARCHAR(30) COMMENT '质控样品类型',
    qc_sample_name VARCHAR(200) COMMENT '质控样品名称',
    measured_value DECIMAL(15,6) COMMENT '测量值',
    unit VARCHAR(20) COMMENT '计量单位',
    operator_id BIGINT COMMENT '操作人员ID',
    operator_name VARCHAR(50) COMMENT '操作人员姓名',
    status TINYINT DEFAULT 0 COMMENT '状态 0待执行 1执行中 2已完成 3已逾期',
    execute_time DATETIME COMMENT '执行时间',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0未删除 1已删除',
    remark VARCHAR(1000) COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_no (record_no),
    KEY idx_plan_id (plan_id),
    KEY idx_batch_no (batch_no),
    KEY idx_qc_sample_id (qc_sample_id),
    KEY idx_execute_date (execute_date),
    KEY idx_operator_id (operator_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质控执行记录表';

-- ========================================
-- 6. 质控数据表 (用于质控图分析)
-- ========================================
CREATE TABLE qc_data (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    plan_id BIGINT NOT NULL COMMENT '质控计划ID',
    sample_id BIGINT COMMENT '质控样品ID',
    record_id BIGINT COMMENT '质控记录ID',
    seq_no INT COMMENT '序号',
    batch_no VARCHAR(50) COMMENT '批次号',
    measure_date DATE COMMENT '测量日期',
    measured_value DECIMAL(15,6) NOT NULL COMMENT '测量值',
    unit VARCHAR(20) COMMENT '计量单位',
    mean_value DECIMAL(15,6) COMMENT '均值',
    sd_value DECIMAL(15,6) COMMENT '标准差',
    z_score DECIMAL(15,6) COMMENT 'Z分数',
    cusum_pos DECIMAL(15,6) COMMENT '累积和(正)',
    cusum_neg DECIMAL(15,6) COMMENT '累积和(负)',
    status VARCHAR(30) DEFAULT 'in_control' COMMENT '状态 in_control在控 warning警告 out_control失控',
    violated_rules TEXT COMMENT '违反规则(JSON格式)',
    project VARCHAR(200) COMMENT '检测项目',
    method_name VARCHAR(200) COMMENT '检测方法',
    operator_id BIGINT COMMENT '操作人员ID',
    operator_name VARCHAR(50) COMMENT '操作人员姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_plan_id (plan_id),
    KEY idx_sample_id (sample_id),
    KEY idx_record_id (record_id),
    KEY idx_batch_no (batch_no),
    KEY idx_measure_date (measure_date),
    KEY idx_status (status),
    KEY idx_seq_no (seq_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质控数据表';

-- ========================================
-- 初始化数据 - Westgard质控规则
-- ========================================
INSERT INTO qc_rule (rule_code, rule_name, rule_type, description, sd_multiple, consecutive_points, within_run, across_run, enabled, sort_order, remark) VALUES
('1_2s', '1_2s规则', 1, '一个质控结果超过均值±2SD，仅为警告规则，启动其他规则检查', 2.00, 1, 1, 0, 1, 1, '警告规则，提示可能存在随机误差'),
('1_3s', '1_3s规则', 2, '一个质控结果超过均值±3SD，判断为失控', 3.00, 1, 1, 1, 1, 2, '失控规则，提示存在较大随机误差'),
('2_2s', '2_2s规则', 2, '两个连续质控结果同时超过均值+2SD或均值-2SD，判断为失控', 2.00, 2, 1, 1, 1, 3, '失控规则，提示存在系统误差'),
('R_4s', 'R_4s规则', 2, '同一批内两个质控结果的差值超过4SD，判断为失控', 4.00, 2, 1, 0, 1, 4, '失控规则，提示存在随机误差'),
('4_1s', '4_1s规则', 2, '四个连续质控结果同时超过均值+1SD或均值-1SD，判断为失控', 1.00, 4, 1, 1, 1, 5, '失控规则，提示存在系统误差'),
('10_x', '10_x规则', 2, '十个连续质控结果都落在均值的同一侧，判断为失控', NULL, 10, 0, 1, 1, 6, '失控规则，提示存在系统误差');

-- ========================================
-- 初始化示例数据 - 质控样品
-- ========================================
INSERT INTO qc_sample (sample_no, sample_name, sample_type, batch_no, concentration, unit, uncertainty, stock_quantity, prepare_date, expire_date, storage_condition, traceability, status, remark) VALUES
('QCS202401001', 'COD标准质控样', 'STANDARD', 'B202401001', 50.0000, 'mg/L', 2.5000, 100.00, '2024-01-15', '2025-01-14', '2-8℃冷藏', '国家标准物质研究中心', 1, 'COD标准质控样，浓度50mg/L'),
('QCS202401002', '氨氮标准质控样', 'STANDARD', 'B202401002', 2.0000, 'mg/L', 0.1000, 100.00, '2024-01-15', '2025-01-14', '2-8℃冷藏', '国家标准物质研究中心', 1, '氨氮标准质控样，浓度2mg/L'),
('QCS202401003', '总磷标准质控样', 'STANDARD', 'B202401003', 0.5000, 'mg/L', 0.0250, 100.00, '2024-01-15', '2025-01-14', '2-8℃冷藏', '国家标准物质研究中心', 1, '总磷标准质控样，浓度0.5mg/L'),
('QCB202401001', '实验室空白样', 'BLANK', 'B202401001', NULL, 'mg/L', NULL, 500.00, '2024-01-15', '2024-02-14', '常温保存', '自制', 1, '纯水空白样'),
('QCP202401001', 'COD平行样', 'PARALLEL', 'B202401001', NULL, 'mg/L', NULL, 50.00, '2024-01-15', '2024-01-22', '2-8℃冷藏', '自制', 1, '平行双样');

-- ========================================
-- 初始化示例数据 - 质控计划
-- ========================================
INSERT INTO qc_plan (plan_no, plan_name, project_name, method_name, cycle_type, status, start_date, end_date, qc_samples, qc_rules, mean_value, sd_value, warning_limit, control_limit, reminder_enabled, description, remark) VALUES
('QCP202401001', 'COD日常质控计划', '化学需氧量(COD)', '重铬酸盐法 GB 11914-89', 'BATCH', 1, '2024-01-01', '2024-12-31', 
'[{"sampleId":1,"sampleName":"COD标准质控样","sampleType":"STANDARD","frequency":1}]',
'["1_2s","1_3s","2_2s","R_4s","4_1s","10_x"]',
50.0000, 2.5000, 5.0000, 7.5000, 1, '每批次检测COD时插入质控样，使用Westgard多规则进行质量控制', 'COD日常内部质量控制'),
('QCP202401002', '氨氮日常质控计划', '氨氮', '纳氏试剂分光光度法 HJ 535-2009', 'DAILY', 1, '2024-01-01', '2024-12-31',
'[{"sampleId":2,"sampleName":"氨氮标准质控样","sampleType":"STANDARD","frequency":1}]',
'["1_2s","1_3s","2_2s","R_4s","4_1s","10_x"]',
2.0000, 0.1000, 0.2000, 0.3000, 1, '每日氨氮检测前先测质控样，确保检测系统稳定', '氨氮日常内部质量控制');
