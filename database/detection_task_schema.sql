-- ========================================
-- 检测任务管理模块数据库设计
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- ========================================

USE lims;

-- ========================================
-- 8. 检测任务管理表
-- ========================================

-- 人员资质表
CREATE TABLE det_user_qualification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    qualification_type VARCHAR(50) NOT NULL COMMENT '资质类型',
    qualification_name VARCHAR(100) NOT NULL COMMENT '资质名称',
    qualification_no VARCHAR(50) COMMENT '资质证书编号',
    issue_date DATE COMMENT '发证日期',
    expiry_date DATE COMMENT '有效期至',
    issuer VARCHAR(100) COMMENT '发证机构',
    test_item_ids TEXT COMMENT '可检测项目ID列表(JSON格式)',
    test_item_names TEXT COMMENT '可检测项目名称列表(JSON格式)',
    status TINYINT DEFAULT 1 COMMENT '状态 0无效 1有效',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员资质表';

-- 标准方法库表（含版本控制）
CREATE TABLE det_standard_method (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '标准方法ID',
    method_code VARCHAR(50) NOT NULL COMMENT '方法编号',
    method_name VARCHAR(200) NOT NULL COMMENT '方法名称',
    standard_type VARCHAR(30) NOT NULL COMMENT '标准类型 1国家标准 2行业标准 3企业标准',
    standard_no VARCHAR(50) COMMENT '标准编号（如GB/T 1234-2020）',
    version VARCHAR(20) DEFAULT '1.0' COMMENT '版本号',
    publish_date DATE COMMENT '发布日期',
    implement_date DATE COMMENT '实施日期',
    applicable_scope VARCHAR(500) COMMENT '适用范围',
    test_item_ids TEXT COMMENT '检测项目ID列表(JSON格式)',
    test_item_names TEXT COMMENT '检测项目名称列表(JSON格式)',
    equipment_ids TEXT COMMENT '所需设备ID列表(JSON格式)',
    equipment_names TEXT COMMENT '所需设备名称列表(JSON格式)',
    detection_limit DECIMAL(12,4) COMMENT '检出限',
    quantitation_limit DECIMAL(12,4) COMMENT '定量限',
    precision_requirement DECIMAL(10,2) COMMENT '精密度要求(%)',
    accuracy_requirement DECIMAL(10,2) COMMENT '准确度要求(%)',
    detection_cycle INT COMMENT '检测周期(天)',
    operation_steps TEXT COMMENT '操作步骤',
    calculation_formula TEXT COMMENT '计算公式',
    quality_control TEXT COMMENT '质量控制要求',
    file_url VARCHAR(255) COMMENT '标准文件附件',
    is_current TINYINT DEFAULT 1 COMMENT '是否当前版本 0否 1是',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1启用',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_method_code_version (method_code, version),
    KEY idx_standard_type (standard_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准方法库表';

-- 标准方法版本历史表
CREATE TABLE det_standard_method_version (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '版本记录ID',
    method_id BIGINT NOT NULL COMMENT '标准方法ID',
    method_code VARCHAR(50) NOT NULL COMMENT '方法编号',
    version VARCHAR(20) NOT NULL COMMENT '版本号',
    change_type VARCHAR(30) COMMENT '变更类型',
    change_content TEXT COMMENT '变更内容',
    change_reason VARCHAR(500) COMMENT '变更原因',
    before_content TEXT COMMENT '变更前内容(JSON)',
    after_content TEXT COMMENT '变更后内容(JSON)',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_method_id (method_id),
    KEY idx_method_code (method_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标准方法版本历史表';

-- 检测任务表
CREATE TABLE det_detection_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    task_no VARCHAR(30) NOT NULL COMMENT '任务编号',
    task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
    entrust_id BIGINT COMMENT '关联委托单ID',
    entrust_no VARCHAR(30) COMMENT '委托单编号',
    sample_id BIGINT COMMENT '关联样品ID',
    sample_no VARCHAR(50) COMMENT '样品编号',
    sample_name VARCHAR(100) COMMENT '样品名称',
    customer_id BIGINT COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    test_item_ids TEXT COMMENT '检测项目ID列表(JSON格式)',
    test_item_names TEXT COMMENT '检测项目名称列表(JSON格式)',
    method_id BIGINT COMMENT '标准方法ID',
    method_code VARCHAR(50) COMMENT '标准方法编号',
    method_name VARCHAR(200) COMMENT '标准方法名称',
    equipment_ids TEXT COMMENT '所需设备ID列表(JSON格式)',
    equipment_names TEXT COMMENT '所需设备名称列表(JSON格式)',
    priority TINYINT DEFAULT 2 COMMENT '优先级 1高 2中 3低',
    is_urgent TINYINT DEFAULT 0 COMMENT '是否加急 0否 1是',
    assign_type TINYINT DEFAULT 1 COMMENT '分配方式 1手动分配 2智能分配 3抢单',
    assignee_type TINYINT DEFAULT 1 COMMENT '分配对象类型 1个人 2科室',
    dept_id BIGINT COMMENT '分配科室ID',
    dept_name VARCHAR(50) COMMENT '分配科室名称',
    assignee_id BIGINT COMMENT '分配人员ID',
    assignee_name VARCHAR(50) COMMENT '分配人员姓名',
    assign_time DATETIME COMMENT '分配时间',
    accept_time DATETIME COMMENT '接收时间',
    plan_start_date DATE COMMENT '计划开始日期',
    plan_end_date DATE COMMENT '计划完成日期',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际完成时间',
    expected_days INT COMMENT '预计检测天数',
    progress DECIMAL(5,2) DEFAULT 0 COMMENT '完成进度(%)',
    status TINYINT DEFAULT 0 COMMENT '状态 0待分配 1待接收 2进行中 3数据录入 4审核中 5已完成 6已暂停 7已终止',
    before_pause_status TINYINT COMMENT '暂停前状态',
    pause_reason VARCHAR(500) COMMENT '暂停原因',
    terminate_reason VARCHAR(500) COMMENT '终止原因',
    data_entry_user_id BIGINT COMMENT '数据录入人ID',
    data_entry_user_name VARCHAR(50) COMMENT '数据录入人姓名',
    reviewer_id BIGINT COMMENT '审核人ID',
    reviewer_name VARCHAR(50) COMMENT '审核人姓名',
    review_opinion VARCHAR(1000) COMMENT '审核意见',
    review_result TINYINT COMMENT '审核结果 1通过 2驳回',
    review_time DATETIME COMMENT '审核时间',
    is_grab_order TINYINT DEFAULT 0 COMMENT '是否抢单池任务 0否 1是',
    grab_order_expire_time DATETIME COMMENT '抢单截止时间',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_entrust_id (entrust_id),
    KEY idx_sample_id (sample_id),
    KEY idx_customer_id (customer_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_dept_id (dept_id),
    KEY idx_status (status),
    KEY idx_priority (priority),
    KEY idx_is_grab_order (is_grab_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检测任务表';

-- 检测任务分配记录表
CREATE TABLE det_task_assignment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分配记录ID',
    task_id BIGINT NOT NULL COMMENT '检测任务ID',
    task_no VARCHAR(30) COMMENT '任务编号',
    assignment_type TINYINT NOT NULL COMMENT '分配类型 1手动分配 2智能分配 3抢单 4改派',
    assignee_type TINYINT DEFAULT 1 COMMENT '分配对象类型 1个人 2科室',
    old_dept_id BIGINT COMMENT '原科室ID',
    old_dept_name VARCHAR(50) COMMENT '原科室名称',
    old_assignee_id BIGINT COMMENT '原分配人员ID',
    old_assignee_name VARCHAR(50) COMMENT '原分配人员姓名',
    new_dept_id BIGINT COMMENT '新科室ID',
    new_dept_name VARCHAR(50) COMMENT '新科室名称',
    new_assignee_id BIGINT COMMENT '新分配人员ID',
    new_assignee_name VARCHAR(50) COMMENT '新分配人员姓名',
    assign_reason VARCHAR(500) COMMENT '分配原因',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    operate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_new_assignee_id (new_assignee_id),
    KEY idx_new_dept_id (new_dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检测任务分配记录表';

-- 检测任务状态流转日志表
CREATE TABLE det_task_status_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    task_id BIGINT NOT NULL COMMENT '检测任务ID',
    task_no VARCHAR(30) COMMENT '任务编号',
    before_status TINYINT COMMENT '变更前状态',
    after_status TINYINT NOT NULL COMMENT '变更后状态',
    operate_type VARCHAR(30) NOT NULL COMMENT '操作类型 创建 分配 接收 开始 数据录入 提交审核 审核通过 审核驳回 暂停 重启 终止 完成',
    operate_content VARCHAR(1000) COMMENT '操作内容',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    operate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_after_status (after_status),
    KEY idx_operate_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检测任务状态流转日志表';

-- 设备使用记录表
CREATE TABLE det_equipment_usage (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '使用记录ID',
    task_id BIGINT NOT NULL COMMENT '检测任务ID',
    task_no VARCHAR(30) COMMENT '任务编号',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(30) COMMENT '设备编号',
    equipment_name VARCHAR(100) COMMENT '设备名称',
    user_id BIGINT COMMENT '使用人ID',
    user_name VARCHAR(50) COMMENT '使用人姓名',
    plan_start_time DATETIME COMMENT '计划开始使用时间',
    plan_end_time DATETIME COMMENT '计划结束使用时间',
    actual_start_time DATETIME COMMENT '实际开始使用时间',
    actual_end_time DATETIME COMMENT '实际结束使用时间',
    usage_status TINYINT DEFAULT 0 COMMENT '使用状态 0待使用 1使用中 2已完成',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_task_id (task_id),
    KEY idx_equipment_id (equipment_id),
    KEY idx_user_id (user_id),
    KEY idx_usage_status (usage_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备使用记录表';

-- 智能排程方案表
CREATE TABLE det_schedule_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '排程方案ID',
    plan_no VARCHAR(30) NOT NULL COMMENT '方案编号',
    plan_name VARCHAR(200) COMMENT '方案名称',
    task_ids TEXT COMMENT '任务ID列表(JSON格式)',
    task_count INT DEFAULT 0 COMMENT '任务数量',
    schedule_date DATE COMMENT '排程日期',
    total_score DECIMAL(10,2) COMMENT '方案综合评分',
    resource_utilization DECIMAL(5,2) COMMENT '资源利用率(%)',
    estimated_completion_time DATETIME COMMENT '预计完成时间',
    schedule_result TEXT COMMENT '排程结果详情(JSON格式)',
    is_used TINYINT DEFAULT 0 COMMENT '是否已采用 0否 1是',
    operator_id BIGINT COMMENT '生成人ID',
    operator_name VARCHAR(50) COMMENT '生成人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_plan_no (plan_no),
    KEY idx_schedule_date (schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能排程方案表';

-- ========================================
-- 初始化数据
-- ========================================

-- 初始化人员资质
INSERT INTO det_user_qualification (user_id, user_name, qualification_type, qualification_name, qualification_no, status, test_item_ids, test_item_names) VALUES
(1, '系统管理员', '检测资质', '水质检测资格证', 'QTZ2024001', 1, '[1,2,3,4,5,6]', '["pH值","化学需氧量(COD)","五日生化需氧量(BOD5)","氨氮","总磷","总氮"]'),
(1, '系统管理员', '检测资质', '空气检测资格证', 'QTZ2024002', 1, '[7,8,9,10]', '["PM2.5","PM10","二氧化硫","氮氧化物"]');

-- 初始化标准方法库
INSERT INTO det_standard_method (method_code, method_name, standard_type, standard_no, version, applicable_scope, detection_limit, quantitation_limit, detection_cycle, status, test_item_ids, test_item_names) VALUES
('SM-W001', '水质 pH值的测定 玻璃电极法', '1', 'GB/T 6920-1986', '1.0', '适用于饮用水、地面水及工业废水pH值的测定', 0.01, 0.1, 2, 1, '[1]', '["pH值"]'),
('SM-W002', '水质 化学需氧量的测定 重铬酸盐法', '1', 'GB/T 11914-1989', '1.0', '适用于各种类型的含COD值大于30mg/L的水样', 5.0, 10.0, 5, 1, '[2]', '["化学需氧量(COD)"]'),
('SM-W003', '水质 五日生化需氧量(BOD5)的测定 稀释与接种法', '1', 'HJ 505-2009', '1.0', '适用于地表水、工业废水和生活污水中BOD5的测定', 2.0, 4.0, 7, 1, '[3]', '["五日生化需氧量(BOD5)"]'),
('SM-A001', '环境空气 PM2.5和PM10的测定 重量法', '1', 'HJ 618-2011', '1.0', '适用于环境空气中PM2.5和PM10的测定', 0.001, 0.01, 3, 1, '[7,8]', '["PM2.5","PM10"]'),
('SM-S001', '土壤和沉积物 重金属的测定 电感耦合等离子体发射光谱法', '2', 'HJ 781-2016', '1.0', '适用于土壤和沉积物中多种重金属元素的测定', 0.01, 0.1, 7, 1, '[11,12,13]', '["重金属-镉","重金属-铅","重金属-铬"]');
