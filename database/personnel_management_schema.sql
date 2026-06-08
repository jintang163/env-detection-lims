-- ========================================
-- 8. 人员管理表
-- ========================================

-- 人员档案表
CREATE TABLE per_personnel (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '人员ID',
    user_id BIGINT COMMENT '关联系统用户ID',
    employee_no VARCHAR(30) NOT NULL COMMENT '员工编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT COMMENT '性别 1男 2女',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    dept_id BIGINT COMMENT '所属部门ID',
    dept_name VARCHAR(50) COMMENT '所属部门名称',
    position VARCHAR(50) COMMENT '职务',
    post VARCHAR(50) COMMENT '岗位',
    title VARCHAR(50) COMMENT '职称',
    education VARCHAR(30) COMMENT '学历',
    major VARCHAR(100) COMMENT '专业',
    graduation_school VARCHAR(100) COMMENT '毕业院校',
    graduation_date DATE COMMENT '毕业日期',
    work_date DATE COMMENT '参加工作日期',
    entry_date DATE COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态 0离职 1在职 2休假',
    avatar_url VARCHAR(255) COMMENT '头像地址',
    address VARCHAR(255) COMMENT '家庭住址',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_employee_no (employee_no),
    KEY idx_user_id (user_id),
    KEY idx_dept_id (dept_id),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员档案表';

-- 人员学历经历表
CREATE TABLE per_personnel_education (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    education VARCHAR(30) COMMENT '学历',
    degree VARCHAR(30) COMMENT '学位',
    major VARCHAR(100) COMMENT '专业',
    graduation_school VARCHAR(100) COMMENT '毕业院校',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    is_full_time TINYINT DEFAULT 1 COMMENT '是否全日制 0否 1是',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员学历经历表';

-- 人员职称表
CREATE TABLE per_personnel_title (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    title_name VARCHAR(50) NOT NULL COMMENT '职称名称',
    title_level VARCHAR(30) COMMENT '职称等级 初级/中级/高级/正高级',
    acquire_date DATE COMMENT '取得日期',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    granting_authority VARCHAR(100) COMMENT '授予单位',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员职称表';

-- 人员授权项目表
CREATE TABLE per_personnel_authorization (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    personnel_name VARCHAR(50) COMMENT '人员姓名',
    authorization_type VARCHAR(30) COMMENT '授权类型 1检测参数 2检测方法',
    item_id BIGINT COMMENT '检测项目ID',
    item_code VARCHAR(30) COMMENT '项目编码',
    item_name VARCHAR(100) COMMENT '项目名称',
    standard_id BIGINT COMMENT '检测标准ID',
    standard_no VARCHAR(50) COMMENT '标准编号',
    standard_name VARCHAR(200) COMMENT '标准名称',
    method_name VARCHAR(200) COMMENT '检测方法名称',
    authorize_date DATE COMMENT '授权日期',
    expiry_date DATE COMMENT '有效期至',
    status TINYINT DEFAULT 1 COMMENT '状态 0无效 1有效',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id),
    KEY idx_item_id (item_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员授权项目表';

-- 人员上岗证表
CREATE TABLE per_personnel_certificate (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    certificate_type VARCHAR(50) NOT NULL COMMENT '证书类型',
    certificate_name VARCHAR(100) NOT NULL COMMENT '证书名称',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    issue_date DATE COMMENT '发证日期',
    expiry_date DATE COMMENT '有效期至',
    issuing_authority VARCHAR(100) COMMENT '发证机关',
    certificate_url VARCHAR(255) COMMENT '证书扫描件',
    is_remind TINYINT DEFAULT 1 COMMENT '是否到期提醒 0否 1是',
    remind_days INT DEFAULT 30 COMMENT '提前提醒天数',
    status TINYINT DEFAULT 1 COMMENT '状态 0已过期 1有效',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id),
    KEY idx_expiry_date (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员上岗证表';

-- 培训计划表
CREATE TABLE per_training_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    plan_no VARCHAR(30) NOT NULL COMMENT '计划编号',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    training_type VARCHAR(50) COMMENT '培训类型 1内部培训 2外部培训 3资质培训',
    training_content TEXT COMMENT '培训内容',
    trainer VARCHAR(100) COMMENT '讲师',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    training_location VARCHAR(200) COMMENT '培训地点',
    training_hours DECIMAL(5,1) COMMENT '培训时长(小时)',
    target_personnel TEXT COMMENT '培训对象人员ID列表(JSON)',
    target_dept_ids TEXT COMMENT '培训对象部门ID列表(JSON)',
    budget DECIMAL(10,2) COMMENT '培训预算',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1已发布 2进行中 3已完成 4已取消',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_plan_no (plan_no),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训计划表';

-- 培训人员表
CREATE TABLE per_training_participant (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    training_plan_id BIGINT NOT NULL COMMENT '培训计划ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    personnel_name VARCHAR(50) COMMENT '人员姓名',
    dept_name VARCHAR(50) COMMENT '部门名称',
    sign_in_time DATETIME COMMENT '签到时间',
    sign_in_status TINYINT DEFAULT 0 COMMENT '签到状态 0未签到 1已签到 2迟到 3早退 4缺勤',
    evaluation_score DECIMAL(5,2) COMMENT '考核分数',
    evaluation_result VARCHAR(20) COMMENT '考核结果 优秀/良好/合格/不合格',
    certificate_flag TINYINT DEFAULT 0 COMMENT '是否发证 0否 1是',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    certificate_expiry_date DATE COMMENT '证书有效期至',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_training_plan_id (training_plan_id),
    KEY idx_personnel_id (personnel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训人员表';

-- 培训效果评估表
CREATE TABLE per_training_evaluation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    training_plan_id BIGINT NOT NULL COMMENT '培训计划ID',
    personnel_id BIGINT NOT NULL COMMENT '评估人员ID',
    personnel_name VARCHAR(50) COMMENT '评估人员姓名',
    content_score TINYINT COMMENT '培训内容满意度(1-5)',
    trainer_score TINYINT COMMENT '讲师满意度(1-5)',
    organization_score TINYINT COMMENT '组织安排满意度(1-5)',
    usefulness_score TINYINT COMMENT '实用性满意度(1-5)',
    overall_score DECIMAL(5,2) COMMENT '综合评分',
    suggestion VARCHAR(1000) COMMENT '意见和建议',
    evaluate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评估时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_training_plan_id (training_plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训效果评估表';

-- 培训记录表
CREATE TABLE per_training_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    personnel_name VARCHAR(50) COMMENT '人员姓名',
    training_type VARCHAR(50) COMMENT '培训类型',
    training_name VARCHAR(200) NOT NULL COMMENT '培训名称',
    training_content TEXT COMMENT '培训内容',
    training_date DATE COMMENT '培训日期',
    training_hours DECIMAL(5,1) COMMENT '培训时长(小时)',
    training_organization VARCHAR(100) COMMENT '培训机构',
    certificate_flag TINYINT DEFAULT 0 COMMENT '是否获得证书 0否 1是',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    certificate_expiry_date DATE COMMENT '证书有效期至',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训记录表';

-- 人员考核记录表
CREATE TABLE per_assessment_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    personnel_name VARCHAR(50) COMMENT '人员姓名',
    assessment_type VARCHAR(50) COMMENT '考核类型 1年度考核 2季度考核 3月度考核 4试用期考核',
    assessment_period VARCHAR(50) COMMENT '考核周期',
    assessment_date DATE COMMENT '考核日期',
    assessor_id BIGINT COMMENT '考核人ID',
    assessor_name VARCHAR(50) COMMENT '考核人姓名',
    work_performance_score DECIMAL(5,2) COMMENT '工作业绩评分',
    work_attitude_score DECIMAL(5,2) COMMENT '工作态度评分',
    work_ability_score DECIMAL(5,2) COMMENT '工作能力评分',
    overall_score DECIMAL(5,2) COMMENT '综合评分',
    assessment_result VARCHAR(20) COMMENT '考核结果 优秀/良好/合格/基本合格/不合格',
    assessment_opinion VARCHAR(1000) COMMENT '考核意见',
    improvement_suggestion VARCHAR(1000) COMMENT '改进建议',
    personnel_opinion VARCHAR(1000) COMMENT '本人意见',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员考核记录表';

-- 证书到期预警表
CREATE TABLE per_certificate_warning (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
    personnel_id BIGINT NOT NULL COMMENT '人员ID',
    personnel_name VARCHAR(50) COMMENT '人员姓名',
    certificate_id BIGINT NOT NULL COMMENT '证书ID',
    certificate_type VARCHAR(50) COMMENT '证书类型',
    certificate_name VARCHAR(100) COMMENT '证书名称',
    certificate_no VARCHAR(50) COMMENT '证书编号',
    expiry_date DATE COMMENT '有效期至',
    warning_type VARCHAR(30) COMMENT '预警类型 即将到期/已过期',
    warning_days INT COMMENT '预警天数(剩余天数)',
    warning_status TINYINT DEFAULT 0 COMMENT '预警状态 0未处理 1已处理',
    process_result VARCHAR(500) COMMENT '处理结果',
    process_time DATETIME COMMENT '处理时间',
    process_user_id BIGINT COMMENT '处理人ID',
    process_user_name VARCHAR(50) COMMENT '处理人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_personnel_id (personnel_id),
    KEY idx_certificate_id (certificate_id),
    KEY idx_expiry_date (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书到期预警表';

-- ========================================
-- 人员管理初始化数据
-- ========================================

-- 初始化人员档案数据
INSERT INTO per_personnel (employee_no, name, gender, phone, email, dept_id, dept_name, position, post, title, education, major, graduation_school, entry_date, status) VALUES
('EMP001', '张三', 1, '13800138001', 'zhangsan@lims.com', 3, '检测部', '检测员', '检测岗位', '初级工程师', '本科', '环境工程', '清华大学', '2020-01-15', 1),
('EMP002', '李四', 2, '13800138002', 'lisi@lims.com', 3, '检测部', '检测员', '检测岗位', '中级工程师', '硕士', '分析化学', '北京大学', '2018-06-20', 1),
('EMP003', '王五', 1, '13800138003', 'wangwu@lims.com', 4, '质量部', '质量主管', '质量管理', '高级工程师', '本科', '环境科学', '复旦大学', '2015-03-10', 1),
('EMP004', '赵六', 2, '13800138004', 'zhaoliu@lims.com', 2, '市场部', '客户经理', '业务岗位', '初级经济师', '本科', '市场营销', '上海交通大学', '2021-07-01', 1);

-- 初始化人员授权项目
INSERT INTO per_personnel_authorization (personnel_id, personnel_name, authorization_type, item_id, item_code, item_name, standard_id, standard_no, standard_name, authorize_date, expiry_date, status) VALUES
(1, '张三', '1', 1, 'W001', 'pH值', 1, 'GB 3838-2002', '地表水环境质量标准', '2024-01-01', '2026-12-31', 1),
(1, '张三', '1', 2, 'W002', '化学需氧量(COD)', 2, 'GB 8978-1996', '污水综合排放标准', '2024-01-01', '2026-12-31', 1),
(1, '张三', '1', 7, 'A001', 'PM2.5', 3, 'GB 3095-2012', '环境空气质量标准', '2024-06-01', '2025-05-31', 1),
(2, '李四', '1', 1, 'W001', 'pH值', 1, 'GB 3838-2002', '地表水环境质量标准', '2023-01-01', '2025-12-31', 1),
(2, '李四', '1', 3, 'W003', '五日生化需氧量(BOD5)', 2, 'GB 8978-1996', '污水综合排放标准', '2023-01-01', '2025-12-31', 1),
(2, '李四', '1', 11, 'S001', '重金属-镉', 5, 'GB 15618-2018', '土壤环境质量标准', '2024-03-01', '2026-02-28', 1);

-- 初始化人员上岗证
INSERT INTO per_personnel_certificate (personnel_id, certificate_type, certificate_name, certificate_no, issue_date, expiry_date, issuing_authority, is_remind, remind_days, status) VALUES
(1, '上岗证', '环境监测人员上岗证', 'HJ20240001', '2024-01-10', '2025-07-15', '生态环境部', 1, 30, 1),
(1, '资质证书', '实验室检测资质证书', 'CZ20230015', '2023-05-20', '2025-05-19', '中国合格评定国家认可委员会', 1, 60, 1),
(2, '上岗证', '环境监测人员上岗证', 'HJ20230056', '2023-08-15', '2024-08-14', '生态环境部', 1, 90, 1),
(2, '资质证书', '实验室检测资质证书', 'CZ20220089', '2022-11-01', '2025-10-31', '中国合格评定国家认可委员会', 1, 60, 1),
(3, '资格证', '质量工程师资格证', 'ZL20200045', '2020-10-20', '2025-10-19', '人力资源和社会保障部', 1, 30, 1),
(4, '资格证', '经济师资格证', 'JJ20210078', '2021-03-15', '2026-03-14', '人力资源和社会保障部', 1, 30, 1);

-- 初始化培训计划
INSERT INTO per_training_plan (plan_no, plan_name, training_type, training_content, trainer, start_date, end_date, training_location, training_hours, status) VALUES
('TP2025001', '2025年度第一期检测技能培训', '1', '水质检测标准方法、仪器操作规范、质量控制要求', '王教授', '2025-03-10', '2025-03-14', '公司培训中心', 20.0, 3),
('TP2025002', '环境监测新标准解读培训', '2', 'GB 3838-2022、GB 3095-2022新标准解读', '李研究员', '2025-04-15', '2025-04-17', '市环保局会议室', 12.0, 1),
('TP2025003', '内审员资格培训', '3', '实验室内部审核流程、不符合项整改、管理评审', '张老师', '2025-05-20', '2025-05-23', '公司培训中心', 16.0, 0);

-- 初始化培训人员
INSERT INTO per_training_participant (training_plan_id, personnel_id, personnel_name, dept_name, sign_in_status, evaluation_score, evaluation_result, certificate_flag, certificate_no, certificate_expiry_date) VALUES
(1, 1, '张三', '检测部', 1, 92.5, '优秀', 1, 'PX202503001', '2027-03-13'),
(1, 2, '李四', '检测部', 1, 88.0, '良好', 1, 'PX202503002', '2027-03-13'),
(1, 3, '王五', '质量部', 1, 95.0, '优秀', 1, 'PX202503003', '2027-03-13'),
(2, 1, '张三', '检测部', 0, NULL, NULL, 0, NULL, NULL),
(2, 2, '李四', '检测部', 0, NULL, NULL, 0, NULL, NULL);

-- 初始化培训记录
INSERT INTO per_training_record (personnel_id, personnel_name, training_type, training_name, training_content, training_date, training_hours, training_organization, certificate_flag, certificate_no, certificate_expiry_date) VALUES
(1, '张三', '内部培训', '仪器设备操作培训', '气相色谱仪、液相色谱仪操作', '2024-08-10', 8.0, '公司技术部', 1, 'PX202408001', '2026-08-09'),
(1, '张三', '外部培训', '环境监测技术研讨会', '最新监测技术交流', '2024-10-15', 4.0, '中国环境监测总站', 0, NULL, NULL),
(2, '李四', '内部培训', '质量控制培训', '实验室质量控制方法', '2024-06-20', 6.0, '公司质量部', 1, 'PX202406001', '2026-06-19');

-- 初始化考核记录
INSERT INTO per_assessment_record (personnel_id, personnel_name, assessment_type, assessment_period, assessment_date, assessor_id, assessor_name, work_performance_score, work_attitude_score, work_ability_score, overall_score, assessment_result, assessment_opinion) VALUES
(1, '张三', '1', '2024年度', '2025-01-15', 3, '王五', 88.0, 90.0, 85.0, 87.5, '良好', '工作认真负责，检测技能扎实，需进一步提升理论知识。'),
(2, '李四', '1', '2024年度', '2025-01-15', 3, '王五', 92.0, 88.0, 90.0, 90.0, '优秀', '专业能力强，能够独立完成复杂样品检测，建议培养为技术骨干。'),
(3, '王五', '1', '2024年度', '2025-01-15', 1, '系统管理员', 95.0, 92.0, 93.0, 93.5, '优秀', '质量管理工作到位，有效保障检测数据准确可靠。');
