-- ========================================
-- 仪器设备管理模块数据库设计
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- ========================================

USE lims;

-- ========================================
-- 设备台账表
-- ========================================
CREATE TABLE IF NOT EXISTS eq_equipment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    equipment_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    model VARCHAR(100) COMMENT '设备型号',
    specification VARCHAR(200) COMMENT '设备规格',
    equipment_type VARCHAR(50) COMMENT '设备类型',
    dept_id BIGINT COMMENT '所属部门ID',
    dept_name VARCHAR(100) COMMENT '所属部门名称',
    location VARCHAR(200) COMMENT '存放地点',
    supplier_id BIGINT COMMENT '供应商ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    purchase_date DATE COMMENT '购置日期',
    purchase_amount DECIMAL(12,2) COMMENT '购置金额',
    factory_no VARCHAR(100) COMMENT '出厂编号',
    status TINYINT DEFAULT 0 COMMENT '设备状态：0闲置 1在用 2维修中 3停用 4报废',
    manager_id BIGINT COMMENT '管理员ID',
    manager_name VARCHAR(50) COMMENT '管理员名称',
    technical_params TEXT COMMENT '技术参数',
    remark VARCHAR(1000) COMMENT '备注',
    attachments VARCHAR(500) COMMENT '附件',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_equipment_no (equipment_no),
    KEY idx_equipment_name (equipment_name),
    KEY idx_status (status),
    KEY idx_dept_id (dept_id),
    KEY idx_manager_id (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备台账表';

-- ========================================
-- 设备校准计划表
-- ========================================
CREATE TABLE IF NOT EXISTS eq_calibration_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    calibration_type TINYINT NOT NULL COMMENT '校准类型：1校准 2检定',
    cycle_months INT NOT NULL COMMENT '校准周期（月）',
    last_calibration_date DATE COMMENT '上次校准日期',
    next_calibration_date DATE NOT NULL COMMENT '下次校准日期',
    remind_days INT DEFAULT 30 COMMENT '提醒天数',
    status TINYINT DEFAULT 0 COMMENT '计划状态：0待执行 1已完成 2已过期',
    manager_id BIGINT COMMENT '负责人ID',
    manager_name VARCHAR(50) COMMENT '负责人名称',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_equipment_id (equipment_id),
    KEY idx_next_calibration_date (next_calibration_date),
    KEY idx_status (status),
    KEY idx_manager_id (manager_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备校准计划表';

-- ========================================
-- 设备校准记录表
-- ========================================
CREATE TABLE IF NOT EXISTS eq_calibration_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    plan_id BIGINT COMMENT '校准计划ID',
    calibration_type TINYINT NOT NULL COMMENT '校准类型：1校准 2检定',
    calibration_date DATE NOT NULL COMMENT '校准日期',
    calibration_unit VARCHAR(100) NOT NULL COMMENT '校准单位',
    certificate_no VARCHAR(100) NOT NULL COMMENT '证书编号',
    result TINYINT NOT NULL COMMENT '校准结果：1合格 2不合格 3部分合格',
    valid_until DATE NOT NULL COMMENT '有效期至',
    calibrator_id BIGINT COMMENT '校准人ID',
    calibrator_name VARCHAR(50) COMMENT '校准人名称',
    cost DECIMAL(12,2) COMMENT '校准费用',
    attachments VARCHAR(500) COMMENT '附件',
    conclusion TEXT COMMENT '校准结论',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_equipment_id (equipment_id),
    KEY idx_plan_id (plan_id),
    KEY idx_calibration_date (calibration_date),
    KEY idx_certificate_no (certificate_no),
    KEY idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备校准记录表';

-- ========================================
-- 设备使用记录表
-- ========================================
CREATE TABLE IF NOT EXISTS eq_equipment_usage (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    user_id BIGINT NOT NULL COMMENT '使用人ID',
    user_name VARCHAR(50) NOT NULL COMMENT '使用人姓名',
    start_time DATETIME NOT NULL COMMENT '开始使用时间',
    end_time DATETIME COMMENT '结束使用时间',
    usage_minutes INT COMMENT '使用时长(分钟)',
    sample_no VARCHAR(50) COMMENT '样品编号',
    task_no VARCHAR(50) COMMENT '任务编号',
    task_id BIGINT COMMENT '检测任务ID',
    data_record_id BIGINT COMMENT '数据记录ID',
    running_status TINYINT DEFAULT 1 COMMENT '运行状况：1正常 2异常 3故障',
    anomaly_description TEXT COMMENT '异常描述',
    usage_status TINYINT DEFAULT 0 COMMENT '使用状态：0使用中 1已完成',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_equipment_id (equipment_id),
    KEY idx_user_id (user_id),
    KEY idx_task_id (task_id),
    KEY idx_start_time (start_time),
    KEY idx_usage_status (usage_status),
    KEY idx_running_status (running_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备使用记录表';

-- ========================================
-- 设备维护记录表
-- ========================================
CREATE TABLE IF NOT EXISTS eq_maintenance_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    maintenance_type TINYINT NOT NULL COMMENT '维护类型：1日常保养 2定期维护 3预防性维护',
    maintenance_date DATE NOT NULL COMMENT '维护日期',
    maintainer_id BIGINT COMMENT '维护人ID',
    maintainer_name VARCHAR(50) COMMENT '维护人名称',
    content TEXT NOT NULL COMMENT '维护内容',
    result TINYINT NOT NULL COMMENT '维护结果：1良好 2一般 3需维修',
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
    KEY idx_equipment_id (equipment_id),
    KEY idx_maintenance_date (maintenance_date),
    KEY idx_maintainer_id (maintainer_id),
    KEY idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护记录表';

-- ========================================
-- 设备维修申请表
-- ========================================
CREATE TABLE IF NOT EXISTS eq_repair_request (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    fault_description TEXT NOT NULL COMMENT '故障描述',
    fault_time DATETIME COMMENT '故障发生时间',
    applicant_id BIGINT COMMENT '申请人ID',
    applicant_name VARCHAR(50) COMMENT '申请人名称',
    apply_time DATETIME NOT NULL COMMENT '申请时间',
    urgency TINYINT NOT NULL COMMENT '紧急程度：1一般 2紧急 3特急',
    status TINYINT DEFAULT 0 COMMENT '申请状态：0待受理 1维修中 2已完成 3已驳回',
    handler_id BIGINT COMMENT '受理人ID',
    handler_name VARCHAR(50) COMMENT '受理人名称',
    handle_time DATETIME COMMENT '受理时间',
    repair_unit VARCHAR(100) COMMENT '维修单位',
    repair_person VARCHAR(50) COMMENT '维修人员',
    repair_start_time DATETIME COMMENT '维修开始时间',
    repair_end_time DATETIME COMMENT '维修结束时间',
    repair_content TEXT COMMENT '维修内容',
    replaced_parts TEXT COMMENT '更换部件',
    repair_cost DECIMAL(12,2) COMMENT '维修费用',
    repair_result TINYINT COMMENT '维修结果：1修复 2部分修复 3无法修复',
    confirmer_id BIGINT COMMENT '确认人ID',
    confirmer_name VARCHAR(50) COMMENT '确认人名称',
    confirm_time DATETIME COMMENT '确认时间',
    confirm_opinion TEXT COMMENT '确认意见',
    attachments VARCHAR(500) COMMENT '附件',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_equipment_id (equipment_id),
    KEY idx_applicant_id (applicant_id),
    KEY idx_apply_time (apply_time),
    KEY idx_urgency (urgency),
    KEY idx_status (status),
    KEY idx_handler_id (handler_id),
    KEY idx_repair_result (repair_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维修申请表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入示例设备数据
INSERT INTO eq_equipment (equipment_no, equipment_name, model, specification, equipment_type, dept_id, dept_name, location, supplier_id, supplier_name, purchase_date, purchase_amount, factory_no, status, manager_id, manager_name, technical_params, remark) VALUES
('EQ-2024-001', '气相色谱仪', 'Agilent 7890B', '毛细管柱，FID检测器', '分析仪器', 1, '检测中心', '实验室A-101', 1, '安捷伦科技', '2024-01-15', 285000.00, 'G7890B-202401001', 1, 1, '张三', '温度范围：室温-450℃，压力范围：0-100psi', '主要用于有机化合物分析'),
('EQ-2024-002', '高效液相色谱仪', 'Waters Alliance e2695', '四元泵，紫外检测器', '分析仪器', 1, '检测中心', '实验室A-102', 2, '沃特世科技', '2024-02-20', 320000.00, 'HPLC-202402002', 1, 2, '李四', '流速范围：0.001-10mL/min，波长范围：190-700nm', '主要用于无机离子和有机化合物分析'),
('EQ-2024-003', '原子吸收分光光度计', 'PE AA900', '火焰+石墨炉', '分析仪器', 1, '检测中心', '实验室A-103', 3, '珀金埃尔默', '2024-03-10', 450000.00, 'AA900-202403003', 0, 3, '王五', '波长范围：189-900nm', '重金属元素分析'),
('EQ-2024-004', 'pH计', 'Mettler Toledo FiveEasy', '测量范围：0-14pH', '检测设备', 1, '检测中心', '实验室B-201', 4, '梅特勒托利多', '2024-04-05', 8500.00, 'PH-202404004', 1, 1, '张三', '精度：±0.01pH', '日常pH检测'),
('EQ-2024-005', '电子天平', 'Sartorius BSA224S', '量程：220g，精度：0.1mg', '检测设备', 1, '检测中心', '实验室B-202', 5, '赛多利斯', '2024-05-12', 15000.00, 'BAL-202405005', 2, 2, '李四', '重复性：±0.1mg', '样品称量，维修中');

-- 插入示例校准计划
INSERT INTO eq_calibration_plan (equipment_id, equipment_no, equipment_name, calibration_type, cycle_months, last_calibration_date, next_calibration_date, remind_days, status, manager_id, manager_name) VALUES
(1, 'EQ-2024-001', '气相色谱仪', 1, 12, '2024-01-20', '2025-01-20', 30, 0, 1, '张三'),
(2, 'EQ-2024-002', '高效液相色谱仪', 1, 12, '2024-02-25', '2025-02-25', 30, 0, 2, '李四'),
(3, 'EQ-2024-003', '原子吸收分光光度计', 2, 12, '2024-03-15', '2025-03-15', 60, 0, 3, '王五'),
(4, 'EQ-2024-004', 'pH计', 1, 6, '2024-10-05', '2025-04-05', 15, 0, 1, '张三'),
(5, 'EQ-2024-005', '电子天平', 2, 6, '2024-11-12', '2025-05-12', 15, 0, 2, '李四');

-- 插入示例校准记录
INSERT INTO eq_calibration_record (equipment_id, equipment_no, equipment_name, plan_id, calibration_type, calibration_date, calibration_unit, certificate_no, result, valid_until, calibrator_id, calibrator_name, cost, conclusion) VALUES
(1, 'EQ-2024-001', '气相色谱仪', 1, 1, '2024-01-20', '中国计量科学研究院', 'CAL-2024-0001', 1, '2025-01-19', 1, '张三', 3500.00, '各指标均符合要求，校准合格'),
(2, 'EQ-2024-002', '高效液相色谱仪', 2, 1, '2024-02-25', '中国计量科学研究院', 'CAL-2024-0002', 1, '2025-02-24', 2, '李四', 4200.00, '各指标均符合要求，校准合格'),
(3, 'EQ-2024-003', '原子吸收分光光度计', 3, 2, '2024-03-15', '国家计量测试中心', 'VER-2024-0003', 1, '2025-03-14', 3, '王五', 5800.00, '检定合格，符合JJG 064-2019规程要求'),
(4, 'EQ-2024-004', 'pH计', 4, 1, '2024-10-05', '省计量检定所', 'CAL-2024-0004', 1, '2025-04-04', 1, '张三', 800.00, '校准合格，示值误差在允许范围内'),
(5, 'EQ-2024-005', '电子天平', 5, 2, '2024-11-12', '省计量检定所', 'VER-2024-0005', 1, '2025-05-11', 2, '李四', 1200.00, '检定合格，符合JJG 1036-2008规程要求');

-- 插入示例使用记录
INSERT INTO eq_equipment_usage (equipment_id, equipment_no, equipment_name, user_id, user_name, start_time, end_time, usage_minutes, sample_no, task_no, task_id, data_record_id, running_status, anomaly_description, usage_status, remark) VALUES
(1, 'EQ-2024-001', '气相色谱仪', 1, '张三', '2025-06-01 09:00:00', '2025-06-01 11:30:00', 150, 'S20250601001', 'T20250601001', 1, 1, 1, NULL, 1, '检测水质VOC'),
(1, 'EQ-2024-001', '气相色谱仪', 2, '李四', '2025-06-02 14:00:00', '2025-06-02 16:45:00', 165, 'S20250602002', 'T20250602002', 2, 2, 1, NULL, 1, '检测土壤半挥发性有机物'),
(2, 'EQ-2024-002', '高效液相色谱仪', 3, '王五', '2025-06-01 10:00:00', '2025-06-01 12:00:00', 120, 'S20250601003', 'T20250601003', 3, 3, 1, NULL, 1, '检测水中酚类化合物'),
(4, 'EQ-2024-004', 'pH计', 1, '张三', '2025-06-03 08:30:00', '2025-06-03 09:00:00', 30, 'S20250603001', 'T20250603001', 4, 4, 1, NULL, 1, '日常水质pH检测'),
(1, 'EQ-2024-001', '气相色谱仪', 1, '张三', '2025-06-08 09:30:00', NULL, NULL, 'S20250608001', 'T20250608001', 5, NULL, 1, NULL, 0, '正在进行中');

-- 插入示例维护记录
INSERT INTO eq_maintenance_record (equipment_id, equipment_no, equipment_name, maintenance_type, maintenance_date, maintainer_id, maintainer_name, content, result, next_maintenance_date, cost) VALUES
(1, 'EQ-2024-001', '气相色谱仪', 1, '2025-05-15', 1, '张三', '清洗进样口，更换衬管，检查气密性', 1, '2025-06-15', 500.00),
(2, 'EQ-2024-002', '高效液相色谱仪', 1, '2025-05-20', 2, '李四', '更换柱塞密封圈，清洗色谱柱', 1, '2025-06-20', 800.00),
(3, 'EQ-2024-003', '原子吸收分光光度计', 2, '2025-04-10', 3, '王五', '全面检查光路系统，清理燃烧头', 1, '2025-07-10', 1200.00),
(1, 'EQ-2024-001', '气相色谱仪', 3, '2025-03-10', 1, '张三', '预防性维护：更换检测器，校准气体流量', 1, '2025-09-10', 2500.00),
(5, 'EQ-2024-005', '电子天平', 1, '2025-05-25', 2, '李四', '清洁称量室，校准水平', 3, NULL, 200.00);

-- 插入示例维修申请
INSERT INTO eq_repair_request (equipment_id, equipment_no, equipment_name, fault_description, fault_time, applicant_id, applicant_name, apply_time, urgency, status, handler_id, handler_name, handle_time, repair_unit, repair_person, repair_start_time, repair_end_time, repair_content, replaced_parts, repair_cost, repair_result, confirmer_id, confirmer_name, confirm_time, confirm_opinion) VALUES
(5, 'EQ-2024-005', '电子天平', '称量不稳定，数值漂移严重', '2025-06-05 10:30:00', 2, '李四', '2025-06-05 11:00:00', 2, 1, 3, '王五', '2025-06-05 14:00:00', '赛多利斯售后服务', '赵师傅', '2025-06-06 09:00:00', NULL, '正在排查故障原因，可能是传感器问题', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, 'EQ-2024-001', '气相色谱仪', '色谱柱温箱升温异常', '2025-05-28 15:20:00', 1, '张三', '2025-05-28 15:30:00', 3, 2, 3, '王五', '2025-05-28 16:00:00', '安捷伦售后服务', '钱师傅', '2025-05-29 09:00:00', '2025-05-30 17:00:00', '更换加热模块，重新校准温度控制系统', '加热模块1个，温度传感器1个', 8500.00, 1, 1, '张三', '2025-05-31 10:00:00', '维修完成，设备运行正常，同意验收');
