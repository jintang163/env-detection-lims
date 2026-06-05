-- ========================================
-- 环境检测LIMS系统数据库设计
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- ========================================

CREATE DATABASE IF NOT EXISTS lims DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lims;

-- ========================================
-- 1. 系统管理表
-- ========================================

-- 用户表
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记 0未删除 1已删除',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_sort INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 部门表
CREATE TABLE sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    ancestors VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    dept_name VARCHAR(30) NOT NULL COMMENT '部门名称',
    leader VARCHAR(20) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(50) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '部门状态 0停用 1正常',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ========================================
-- 2. 客户管理表
-- ========================================

-- 客户表
CREATE TABLE cus_customer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    customer_no VARCHAR(30) NOT NULL COMMENT '客户编号',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    customer_type TINYINT DEFAULT 1 COMMENT '客户类型 1企业 2个人 3政府机构',
    level TINYINT DEFAULT 3 COMMENT '客户等级 1A级 2B级 3C级 4D级',
    credit_score INT DEFAULT 100 COMMENT '信用评分',
    credit_level VARCHAR(10) DEFAULT '良好' COMMENT '信用等级',
    industry VARCHAR(50) COMMENT '所属行业',
    region VARCHAR(100) COMMENT '所在地区',
    address VARCHAR(255) COMMENT '详细地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '电子邮箱',
    legal_person VARCHAR(50) COMMENT '法人代表',
    business_license VARCHAR(50) COMMENT '营业执照号',
    is_public_sea TINYINT DEFAULT 0 COMMENT '是否公海客户 0否 1是',
    public_sea_reason VARCHAR(500) COMMENT '公海原因',
    follow_user_id BIGINT COMMENT '跟进人ID',
    follow_time DATETIME COMMENT '最后跟进时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_no (customer_no),
    KEY idx_customer_name (customer_name),
    KEY idx_follow_user (follow_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 客户资质文件表
CREATE TABLE cus_customer_qualification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    qual_type VARCHAR(50) NOT NULL COMMENT '资质类型',
    qual_name VARCHAR(100) NOT NULL COMMENT '资质名称',
    qual_no VARCHAR(50) COMMENT '资质编号',
    issue_date DATE COMMENT '发证日期',
    expiry_date DATE COMMENT '有效期至',
    file_url VARCHAR(255) COMMENT '附件地址',
    file_name VARCHAR(255) COMMENT '文件名称',
    status TINYINT DEFAULT 1 COMMENT '状态 0无效 1有效',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户资质文件表';

-- 客户跟进记录表
CREATE TABLE cus_customer_follow (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    follow_type VARCHAR(30) COMMENT '跟进类型',
    follow_content VARCHAR(2000) NOT NULL COMMENT '跟进内容',
    follow_time DATETIME NOT NULL COMMENT '跟进时间',
    follow_user_id BIGINT NOT NULL COMMENT '跟进人ID',
    next_follow_time DATETIME COMMENT '下次跟进时间',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id),
    KEY idx_follow_user (follow_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户跟进记录表';

-- 客户信用记录表
CREATE TABLE cus_customer_credit (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    change_type VARCHAR(30) NOT NULL COMMENT '变更类型',
    score_before INT COMMENT '变更前分数',
    score_after INT COMMENT '变更后分数',
    change_score INT NOT NULL COMMENT '变更分数',
    reason VARCHAR(500) NOT NULL COMMENT '变更原因',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信用记录表';

-- ========================================
-- 3. 检测项目与标准表
-- ========================================

-- 检测项目表
CREATE TABLE dict_test_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    item_code VARCHAR(30) NOT NULL COMMENT '项目编码',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_category VARCHAR(50) COMMENT '项目分类',
    unit VARCHAR(20) COMMENT '计量单位',
    standard_price DECIMAL(10,2) NOT NULL COMMENT '标准价格',
    cost_price DECIMAL(10,2) COMMENT '成本价格',
    detection_limit DECIMAL(12,4) COMMENT '检出限',
    quantitation_limit DECIMAL(12,4) COMMENT '定量限',
    detection_cycle INT COMMENT '检测周期(天)',
    description VARCHAR(500) COMMENT '检测说明',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1启用',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_item_code (item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检测项目表';

-- 检测标准表
CREATE TABLE dict_test_standard (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '标准ID',
    standard_no VARCHAR(50) NOT NULL COMMENT '标准编号',
    standard_name VARCHAR(200) NOT NULL COMMENT '标准名称',
    standard_type VARCHAR(30) COMMENT '标准类型',
    publish_date DATE COMMENT '发布日期',
    implement_date DATE COMMENT '实施日期',
    valid TINYINT DEFAULT 1 COMMENT '是否有效 0无效 1有效',
    description VARCHAR(1000) COMMENT '标准说明',
    file_url VARCHAR(255) COMMENT '标准文件地址',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_standard_no (standard_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检测标准表';

-- 项目标准关联表
CREATE TABLE dict_item_standard (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    item_id BIGINT NOT NULL COMMENT '检测项目ID',
    standard_id BIGINT NOT NULL COMMENT '检测标准ID',
    standard_content VARCHAR(500) COMMENT '标准内容',
    limit_value VARCHAR(100) COMMENT '限值要求',
    PRIMARY KEY (id),
    KEY idx_item_id (item_id),
    KEY idx_standard_id (standard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目标准关联表';

-- ========================================
-- 4. 合同管理表
-- ========================================

-- 合同表
CREATE TABLE con_contract (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '合同ID',
    contract_no VARCHAR(30) NOT NULL COMMENT '合同编号',
    contract_name VARCHAR(200) NOT NULL COMMENT '合同名称',
    contract_type TINYINT DEFAULT 1 COMMENT '合同类型 1检测合同 2服务合同 3框架合同',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称（冗余）',
    sign_date DATE COMMENT '签订日期',
    effective_date DATE COMMENT '生效日期',
    expiry_date DATE COMMENT '到期日期',
    contract_amount DECIMAL(15,2) COMMENT '合同金额',
    prepaid_amount DECIMAL(15,2) DEFAULT 0 COMMENT '预付金额',
    settlement_method VARCHAR(30) COMMENT '结算方式',
    contract_content TEXT COMMENT '合同内容',
    attachment_url VARCHAR(255) COMMENT '合同附件',
    status TINYINT DEFAULT 0 COMMENT '合同状态 0草稿 1审批中 2已生效 3变更中 4已终止 5已完成',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态 0待审批 1审批中 2已通过 3已驳回',
    approval_node VARCHAR(50) COMMENT '当前审批节点',
    performance_status TINYINT DEFAULT 0 COMMENT '履约状态 0未开始 1履行中 2已完成 3违约',
    performance_rate DECIMAL(5,2) DEFAULT 0 COMMENT '履约进度%',
    is_urgent TINYINT DEFAULT 0 COMMENT '是否加急 0否 1是',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contract_no (contract_no),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- 合同变更记录表
CREATE TABLE con_contract_change (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '变更ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    change_no VARCHAR(30) NOT NULL COMMENT '变更编号',
    change_type VARCHAR(30) COMMENT '变更类型',
    change_content TEXT COMMENT '变更内容',
    change_reason VARCHAR(500) COMMENT '变更原因',
    before_change TEXT COMMENT '变更前内容',
    after_change TEXT COMMENT '变更后内容',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1审批中 2已通过 3已驳回',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态',
    operator_id BIGINT COMMENT '操作人ID',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_change_no (change_no),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同变更记录表';

-- 合同审批记录表
CREATE TABLE con_contract_approval (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    change_id BIGINT COMMENT '变更ID',
    approval_node VARCHAR(50) NOT NULL COMMENT '审批节点',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approval_opinion VARCHAR(1000) COMMENT '审批意见',
    approval_result TINYINT COMMENT '审批结果 1通过 2驳回',
    approval_time DATETIME COMMENT '审批时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同审批记录表';

-- 合同履约跟踪表
CREATE TABLE con_contract_performance (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '跟踪ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    track_type VARCHAR(30) COMMENT '跟踪类型',
    track_content VARCHAR(1000) NOT NULL COMMENT '跟踪内容',
    track_time DATETIME NOT NULL COMMENT '跟踪时间',
    tracker_id BIGINT COMMENT '跟踪人ID',
    attachment_url VARCHAR(255) COMMENT '附件',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同履约跟踪表';

-- ========================================
-- 5. 委托单管理表
-- ========================================

-- 委托单表
CREATE TABLE ent_entrust (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '委托单ID',
    entrust_no VARCHAR(30) NOT NULL COMMENT '委托单编号',
    entrust_type TINYINT DEFAULT 1 COMMENT '委托类型 1常规委托 2委托采样 3送样检测',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    contract_id BIGINT COMMENT '关联合同ID',
    contract_no VARCHAR(30) COMMENT '合同编号',
    sample_name VARCHAR(200) COMMENT '样品名称',
    sample_type VARCHAR(50) COMMENT '样品类型',
    sample_quantity INT COMMENT '样品数量',
    sampling_address VARCHAR(255) COMMENT '采样地址',
    sampling_longitude DECIMAL(10,7) COMMENT '采样经度',
    sampling_latitude DECIMAL(10,7) COMMENT '采样纬度',
    sampling_time DATETIME COMMENT '采样时间',
    sample_send_time DATETIME COMMENT '送样时间',
    sample_receive_time DATETIME COMMENT '收样时间',
    expected_report_time DATETIME COMMENT '期望报告时间',
    actual_report_time DATETIME COMMENT '实际报告时间',
    detection_basis VARCHAR(500) COMMENT '检测依据',
    evaluation_basis VARCHAR(500) COMMENT '评价依据',
    total_amount DECIMAL(15,2) COMMENT '总金额',
    discount_rate DECIMAL(5,2) DEFAULT 100 COMMENT '折扣率%',
    discount_amount DECIMAL(15,2) DEFAULT 0 COMMENT '折扣金额',
    actual_amount DECIMAL(15,2) COMMENT '实际金额',
    is_urgent TINYINT DEFAULT 0 COMMENT '是否加急 0否 1是',
    urgent_fee DECIMAL(15,2) DEFAULT 0 COMMENT '加急费',
    is_subcontract TINYINT DEFAULT 0 COMMENT '是否分包 0否 1是',
    subcontract_amount DECIMAL(15,2) DEFAULT 0 COMMENT '分包金额',
    is_adjust TINYINT DEFAULT 0 COMMENT '是否调账 0否 1是',
    adjust_amount DECIMAL(15,2) DEFAULT 0 COMMENT '调账金额',
    adjust_reason VARCHAR(500) COMMENT '调账原因',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1待受理 2已受理 3采样中 4检测中 5报告编制中 6报告审核中 7已完成 8已取消',
    approval_status TINYINT DEFAULT 0 COMMENT '合同评审状态 0待评审 1评审中 2评审通过 3评审驳回',
    review_opinion VARCHAR(1000) COMMENT '评审意见',
    report_no VARCHAR(30) COMMENT '报告编号',
    report_url VARCHAR(255) COMMENT '报告地址',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_entrust_no (entrust_no),
    KEY idx_customer_id (customer_id),
    KEY idx_contract_id (contract_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托单表';

-- 委托单检测项目表
CREATE TABLE ent_entrust_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    entrust_id BIGINT NOT NULL COMMENT '委托单ID',
    item_id BIGINT NOT NULL COMMENT '检测项目ID',
    item_code VARCHAR(30) COMMENT '项目编码',
    item_name VARCHAR(100) COMMENT '项目名称',
    standard_id BIGINT COMMENT '检测标准ID',
    standard_no VARCHAR(50) COMMENT '标准编号',
    standard_name VARCHAR(200) COMMENT '标准名称',
    unit VARCHAR(20) COMMENT '计量单位',
    limit_value VARCHAR(100) COMMENT '限值要求',
    unit_price DECIMAL(10,2) COMMENT '单价',
    quantity INT DEFAULT 1 COMMENT '检测点数/数量',
    subtotal DECIMAL(15,2) COMMENT '小计金额',
    is_subcontract TINYINT DEFAULT 0 COMMENT '是否分包',
    subcontractor VARCHAR(100) COMMENT '分包单位',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_entrust_id (entrust_id),
    KEY idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托单检测项目表';

-- 委托单状态流转记录表
CREATE TABLE ent_entrust_status_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    entrust_id BIGINT NOT NULL COMMENT '委托单ID',
    before_status TINYINT COMMENT '变更前状态',
    after_status TINYINT NOT NULL COMMENT '变更后状态',
    operate_type VARCHAR(30) NOT NULL COMMENT '操作类型',
    operate_content VARCHAR(1000) COMMENT '操作内容',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    operate_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_entrust_id (entrust_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托单状态流转记录表';

-- 委托单评审记录表
CREATE TABLE ent_entrust_review (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '评审ID',
    entrust_id BIGINT NOT NULL COMMENT '委托单ID',
    review_node VARCHAR(50) NOT NULL COMMENT '评审节点',
    reviewer_id BIGINT COMMENT '评审人ID',
    reviewer_name VARCHAR(50) COMMENT '评审人姓名',
    review_opinion VARCHAR(1000) COMMENT '评审意见',
    review_result TINYINT COMMENT '评审结果 1通过 2驳回',
    review_time DATETIME COMMENT '评审时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_entrust_id (entrust_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托单评审记录表';

-- 分包信息表
CREATE TABLE ent_subcontract (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分包ID',
    subcontract_no VARCHAR(30) NOT NULL COMMENT '分包编号',
    entrust_id BIGINT NOT NULL COMMENT '委托单ID',
    subcontractor_id BIGINT COMMENT '分包商ID',
    subcontractor_name VARCHAR(100) COMMENT '分包商名称',
    subcontract_items TEXT COMMENT '分包项目',
    subcontract_amount DECIMAL(15,2) COMMENT '分包金额',
    send_date DATE COMMENT '送样日期',
    expect_return_date DATE COMMENT '预计返回日期',
    actual_return_date DATE COMMENT '实际返回日期',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1已送出 2检测中 3已返回 4已完成',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_subcontract_no (subcontract_no),
    KEY idx_entrust_id (entrust_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分包信息表';

-- ========================================
-- 6. 报价单管理表
-- ========================================

-- 报价单表
CREATE TABLE quo_quotation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '报价单ID',
    quotation_no VARCHAR(30) NOT NULL COMMENT '报价单编号',
    quotation_name VARCHAR(200) COMMENT '报价单名称',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    contract_id BIGINT COMMENT '关联合同ID',
    entrust_id BIGINT COMMENT '关联委托单ID',
    valid_date DATE COMMENT '报价有效期',
    quotation_date DATE COMMENT '报价日期',
    total_amount DECIMAL(15,2) COMMENT '报价总额',
    discount_rate DECIMAL(5,2) DEFAULT 100 COMMENT '折扣率%',
    actual_amount DECIMAL(15,2) COMMENT '折后金额',
    quotation_content TEXT COMMENT '报价说明',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1审批中 2已通过 3已驳回 4已确认 5已作废',
    approval_status TINYINT DEFAULT 0 COMMENT '审批状态 0待审批 1审批中 2已通过 3已驳回',
    confirm_user_id BIGINT COMMENT '客户确认人ID',
    confirm_time DATETIME COMMENT '客户确认时间',
    is_converted TINYINT DEFAULT 0 COMMENT '是否已转委托 0否 1是',
    convert_entrust_id BIGINT COMMENT '转委托单ID',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_quotation_no (quotation_no),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单表';

-- 报价单明细表
CREATE TABLE quo_quotation_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    quotation_id BIGINT NOT NULL COMMENT '报价单ID',
    item_id BIGINT COMMENT '检测项目ID',
    item_code VARCHAR(30) COMMENT '项目编码',
    item_name VARCHAR(100) COMMENT '项目名称',
    item_category VARCHAR(50) COMMENT '项目分类',
    standard_id BIGINT COMMENT '检测标准ID',
    standard_no VARCHAR(50) COMMENT '标准编号',
    standard_name VARCHAR(200) COMMENT '标准名称',
    unit VARCHAR(20) COMMENT '计量单位',
    unit_price DECIMAL(10,2) COMMENT '单价',
    quantity INT DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(15,2) COMMENT '小计',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_quotation_id (quotation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单明细表';

-- 报价单审批记录表
CREATE TABLE quo_quotation_approval (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批ID',
    quotation_id BIGINT NOT NULL COMMENT '报价单ID',
    approval_node VARCHAR(50) NOT NULL COMMENT '审批节点',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approval_opinion VARCHAR(1000) COMMENT '审批意见',
    approval_result TINYINT COMMENT '审批结果 1通过 2驳回',
    approval_time DATETIME COMMENT '审批时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_quotation_id (quotation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单审批记录表';

-- ========================================
-- 7. 初始化数据
-- ========================================

-- 初始化管理员用户: admin / admin123
INSERT INTO sys_user (username, password, real_name, phone, email, status) 
VALUES ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800138000', 'admin@lims.com', 1);

-- 初始化角色
INSERT INTO sys_role (role_name, role_code, role_sort, status, remark) VALUES
('超级管理员', 'admin', 1, 1, '拥有所有权限'),
('客户经理', 'sales', 2, 1, '负责客户管理和合同签订'),
('采样员', 'sampler', 3, 1, '负责样品采集'),
('检测员', 'tester', 4, 1, '负责样品检测'),
('报告编制员', 'reporter', 5, 1, '负责报告编制'),
('审核员', 'auditor', 6, 1, '负责审核工作'),
('财务人员', 'finance', 7, 1, '负责财务管理');

-- 初始化用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始化部门
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, leader, phone, status) VALUES
(1, 0, '0', '总公司', '张总', '010-12345678', 1),
(2, 1, '0,1', '市场部', '李经理', '010-12345679', 1),
(3, 1, '0,1', '检测部', '王经理', '010-12345680', 1),
(4, 1, '0,1', '质量部', '赵经理', '010-12345681', 1),
(5, 1, '0,1', '财务部', '钱经理', '010-12345682', 1);

-- 初始化检测项目
INSERT INTO dict_test_item (item_code, item_name, item_category, unit, standard_price, cost_price, detection_limit, detection_cycle, description) VALUES
('W001', 'pH值', '水质检测', '无量纲', 50.00, 20.00, 0.01, 3, '水质pH值测定'),
('W002', '化学需氧量(COD)', '水质检测', 'mg/L', 120.00, 50.00, 5.0, 5, '水质化学需氧量测定'),
('W003', '五日生化需氧量(BOD5)', '水质检测', 'mg/L', 180.00, 80.00, 2.0, 7, '水质五日生化需氧量测定'),
('W004', '氨氮', '水质检测', 'mg/L', 80.00, 30.00, 0.025, 3, '水质氨氮测定'),
('W005', '总磷', '水质检测', 'mg/L', 100.00, 40.00, 0.01, 3, '水质总磷测定'),
('W006', '总氮', '水质检测', 'mg/L', 100.00, 40.00, 0.05, 3, '水质总氮测定'),
('A001', 'PM2.5', '空气检测', 'μg/m³', 150.00, 60.00, 0.001, 3, '环境空气PM2.5测定'),
('A002', 'PM10', '空气检测', 'μg/m³', 120.00, 50.00, 0.001, 3, '环境空气PM10测定'),
('A003', '二氧化硫', '空气检测', 'mg/m³', 100.00, 40.00, 0.007, 3, '环境空气二氧化硫测定'),
('A004', '氮氧化物', '空气检测', 'mg/m³', 120.00, 50.00, 0.005, 3, '环境空气氮氧化物测定'),
('S001', '重金属-镉', '土壤检测', 'mg/kg', 200.00, 80.00, 0.01, 7, '土壤中镉含量测定'),
('S002', '重金属-铅', '土壤检测', 'mg/kg', 180.00, 70.00, 0.1, 7, '土壤中铅含量测定'),
('S003', '重金属-铬', '土壤检测', 'mg/kg', 180.00, 70.00, 0.5, 7, '土壤中铬含量测定'),
('N001', '噪声', '噪声检测', 'dB(A)', 80.00, 30.00, NULL, 2, '环境噪声测定');

-- 初始化检测标准
INSERT INTO dict_test_standard (standard_no, standard_name, standard_type, valid, description) VALUES
('GB 3838-2002', '地表水环境质量标准', '国家标准', 1, '地表水环境质量标准'),
('GB 8978-1996', '污水综合排放标准', '国家标准', 1, '污水综合排放标准'),
('GB 3095-2012', '环境空气质量标准', '国家标准', 1, '环境空气质量标准'),
('GB 16297-1996', '大气污染物综合排放标准', '国家标准', 1, '大气污染物综合排放标准'),
('GB 15618-2018', '土壤环境质量 农用地土壤污染风险管控标准', '国家标准', 1, '土壤环境质量标准'),
('GB 36600-2018', '土壤环境质量 建设用地土壤污染风险管控标准', '国家标准', 1, '建设用地土壤标准'),
('GB 3096-2008', '声环境质量标准', '国家标准', 1, '声环境质量标准'),
('GB 12348-2008', '工业企业厂界环境噪声排放标准', '国家标准', 1, '工业企业噪声排放标准'),
('HJ 91.1-2019', '污水监测技术规范', '行业标准', 1, '污水监测技术规范'),
('HJ/T 164-2020', '地下水环境监测技术规范', '行业标准', 1, '地下水环境监测技术规范');

-- 初始化项目标准关联
INSERT INTO dict_item_standard (item_id, standard_id, standard_content, limit_value) VALUES
(1, 1, 'pH值', '6-9'),
(2, 2, '化学需氧量', '≤100mg/L'),
(3, 2, '五日生化需氧量', '≤30mg/L'),
(4, 2, '氨氮', '≤15mg/L'),
(7, 3, 'PM2.5', '≤75μg/m³'),
(8, 3, 'PM10', '≤150μg/m³'),
(11, 5, '镉', '≤0.3mg/kg'),
(12, 5, '铅', '≤100mg/kg'),
(14, 7, '等效连续A声级', '≤60dB(A)');

-- ========================================
-- 7. 现场采样管理表
-- ========================================

-- 采样计划表
CREATE TABLE smp_sampling_plan (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    plan_no VARCHAR(30) NOT NULL COMMENT '计划编号',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    entrust_id BIGINT COMMENT '关联委托单ID',
    entrust_no VARCHAR(30) COMMENT '委托单编号',
    customer_id BIGINT COMMENT '客户ID',
    customer_name VARCHAR(100) COMMENT '客户名称',
    plan_date DATE COMMENT '计划采样日期',
    sampling_type VARCHAR(50) COMMENT '采样类型 1水质 2空气 3土壤 4噪声',
    sampler_ids VARCHAR(500) COMMENT '采样员ID列表(逗号分隔)',
    sampler_names VARCHAR(500) COMMENT '采样员姓名列表(逗号分隔)',
    equipment_ids VARCHAR(500) COMMENT '设备ID列表(逗号分隔)',
    equipment_names VARCHAR(500) COMMENT '设备名称列表(逗号分隔)',
    container_ids VARCHAR(500) COMMENT '容器ID列表(逗号分隔)',
    container_names VARCHAR(500) COMMENT '容器名称列表(逗号分隔)',
    point_count INT DEFAULT 0 COMMENT '点位数量',
    sample_count INT DEFAULT 0 COMMENT '预计样品数量',
    status TINYINT DEFAULT 0 COMMENT '状态 0草稿 1待分配 2已分配 3采样中 4已完成 5已取消',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_plan_no (plan_no),
    KEY idx_entrust_id (entrust_id),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采样计划表';

-- 采样点位表
CREATE TABLE smp_sampling_point (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '点位ID',
    plan_id BIGINT NOT NULL COMMENT '采样计划ID',
    point_code VARCHAR(50) COMMENT '点位编号',
    point_name VARCHAR(100) NOT NULL COMMENT '点位名称',
    point_address VARCHAR(255) COMMENT '点位地址',
    longitude DECIMAL(11,7) COMMENT '经度',
    latitude DECIMAL(11,7) COMMENT '纬度',
    sampling_depth VARCHAR(50) COMMENT '采样深度',
    sampling_method VARCHAR(100) COMMENT '采样方法',
    sample_type VARCHAR(50) COMMENT '样品类型',
    expected_count INT DEFAULT 1 COMMENT '预计采样数量',
    test_items TEXT COMMENT '检测项目(JSON格式)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采样点位表';

-- 采样任务表
CREATE TABLE smp_sampling_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    task_no VARCHAR(30) NOT NULL COMMENT '任务编号',
    plan_id BIGINT NOT NULL COMMENT '采样计划ID',
    plan_no VARCHAR(30) COMMENT '计划编号',
    plan_name VARCHAR(200) COMMENT '计划名称',
    point_id BIGINT NOT NULL COMMENT '点位ID',
    point_code VARCHAR(50) COMMENT '点位编号',
    point_name VARCHAR(100) COMMENT '点位名称',
    point_address VARCHAR(255) COMMENT '点位地址',
    longitude DECIMAL(11,7) COMMENT '经度',
    latitude DECIMAL(11,7) COMMENT '纬度',
    sampler_id BIGINT COMMENT '采样员ID',
    sampler_name VARCHAR(50) COMMENT '采样员姓名',
    assign_time DATETIME COMMENT '分配时间',
    plan_sampling_date DATE COMMENT '计划采样日期',
    actual_sampling_date DATETIME COMMENT '实际采样日期',
    actual_longitude DECIMAL(11,7) COMMENT '实际采样经度',
    actual_latitude DECIMAL(11,7) COMMENT '实际采样纬度',
    temperature DECIMAL(6,2) COMMENT '现场温度(℃)',
    humidity DECIMAL(6,2) COMMENT '现场湿度(%)',
    ph DECIMAL(6,2) COMMENT '现场pH值',
    flow_rate DECIMAL(10,2) COMMENT '流量',
    weather VARCHAR(50) COMMENT '天气',
    site_photos VARCHAR(1000) COMMENT '现场照片(JSON格式)',
    qc_sample_flag TINYINT DEFAULT 0 COMMENT '质控样标记 0否 1是',
    qc_sample_type VARCHAR(50) COMMENT '质控样类型 1平行样 2空白样 3加标样',
    storage_condition VARCHAR(100) COMMENT '保存条件',
    sample_count INT DEFAULT 0 COMMENT '实际采样数量',
    status TINYINT DEFAULT 0 COMMENT '状态 0待下载 1已下载 2采样中 3已完成 4已上传 5已取消',
    sync_status TINYINT DEFAULT 0 COMMENT '同步状态 0未同步 1已同步',
    offline_flag TINYINT DEFAULT 0 COMMENT '离线标记 0否 1是',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_plan_id (plan_id),
    KEY idx_sampler_id (sampler_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采样任务表';

-- 样品记录表
CREATE TABLE smp_sample_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '样品ID',
    sample_no VARCHAR(50) NOT NULL COMMENT '样品编号',
    qr_code VARCHAR(200) COMMENT '二维码内容',
    task_id BIGINT NOT NULL COMMENT '采样任务ID',
    plan_id BIGINT NOT NULL COMMENT '采样计划ID',
    point_id BIGINT NOT NULL COMMENT '点位ID',
    point_code VARCHAR(50) COMMENT '点位编号',
    point_name VARCHAR(100) COMMENT '点位名称',
    sample_type VARCHAR(50) COMMENT '样品类型',
    sample_name VARCHAR(100) COMMENT '样品名称',
    sample_status VARCHAR(50) DEFAULT '待交接' COMMENT '样品状态 待交接 已交接 检测中 已完成',
    sampling_time DATETIME COMMENT '采样时间',
    temperature DECIMAL(6,2) COMMENT '采样温度(℃)',
    ph DECIMAL(6,2) COMMENT 'pH值',
    volume DECIMAL(10,2) COMMENT '样品体积(L)',
    container_type VARCHAR(50) COMMENT '容器类型',
    preservative VARCHAR(100) COMMENT '防腐剂',
    storage_condition VARCHAR(100) COMMENT '保存条件',
    expire_time DATETIME COMMENT '有效期至',
    qc_flag TINYINT DEFAULT 0 COMMENT '质控样标记 0否 1是',
    qc_type VARCHAR(50) COMMENT '质控样类型',
    test_items TEXT COMMENT '检测项目(JSON格式)',
    photos VARCHAR(1000) COMMENT '样品照片(JSON格式)',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sample_no (sample_no),
    KEY idx_task_id (task_id),
    KEY idx_plan_id (plan_id),
    KEY idx_qr_code (qr_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品记录表';

-- 样品交接表
CREATE TABLE smp_sample_transfer (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '交接ID',
    transfer_no VARCHAR(30) NOT NULL COMMENT '交接单编号',
    plan_id BIGINT COMMENT '采样计划ID',
    plan_no VARCHAR(30) COMMENT '计划编号',
    transfer_type TINYINT DEFAULT 1 COMMENT '交接类型 1采样交样 2样品送样',
    sampler_id BIGINT COMMENT '交接人ID(采样员)',
    sampler_name VARCHAR(50) COMMENT '交接人姓名',
    receiver_id BIGINT COMMENT '接收人ID(样品管理员)',
    receiver_name VARCHAR(50) COMMENT '接收人姓名',
    transfer_time DATETIME COMMENT '交接时间',
    sample_count INT DEFAULT 0 COMMENT '样品数量',
    sample_ids TEXT COMMENT '样品ID列表(JSON格式)',
    transfer_status TINYINT DEFAULT 0 COMMENT '交接状态 0待确认 1已确认 2已驳回',
    confirm_time DATETIME COMMENT '确认时间',
    reject_reason VARCHAR(500) COMMENT '驳回原因',
    exception_desc VARCHAR(1000) COMMENT '异常情况说明',
    attachment_url VARCHAR(255) COMMENT '交接单附件',
    remark VARCHAR(1000) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transfer_no (transfer_no),
    KEY idx_plan_id (plan_id),
    KEY idx_sampler_id (sampler_id),
    KEY idx_receiver_id (receiver_id),
    KEY idx_transfer_status (transfer_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='样品交接表';

-- 设备表
CREATE TABLE smp_equipment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
    equipment_no VARCHAR(30) NOT NULL COMMENT '设备编号',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    equipment_type VARCHAR(50) COMMENT '设备类型 1采样设备 2监测设备 3样品容器',
    specification VARCHAR(100) COMMENT '规格型号',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    purchase_date DATE COMMENT '购置日期',
    last_calibration_date DATE COMMENT '上次校准日期',
    next_calibration_date DATE COMMENT '下次校准日期',
    calibration_cycle INT COMMENT '校准周期(天)',
    status TINYINT DEFAULT 1 COMMENT '状态 0停用 1正常 2维修中 3已报废',
    borrow_status TINYINT DEFAULT 0 COMMENT '领用状态 0未领用 1已领用',
    current_borrower_id BIGINT COMMENT '当前借用人ID',
    current_borrower_name VARCHAR(50) COMMENT '当前借用人姓名',
    storage_location VARCHAR(100) COMMENT '存放位置',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_equipment_no (equipment_no),
    KEY idx_equipment_type (equipment_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 设备领用归还表
CREATE TABLE smp_equipment_borrow (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    borrow_no VARCHAR(30) NOT NULL COMMENT '领用单号',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    equipment_no VARCHAR(30) COMMENT '设备编号',
    equipment_name VARCHAR(100) COMMENT '设备名称',
    specification VARCHAR(100) COMMENT '规格型号',
    plan_id BIGINT COMMENT '关联采样计划ID',
    plan_no VARCHAR(30) COMMENT '计划编号',
    borrower_id BIGINT NOT NULL COMMENT '借用人ID',
    borrower_name VARCHAR(50) COMMENT '借用人姓名',
    borrow_date DATE COMMENT '领用日期',
    expected_return_date DATE COMMENT '预计归还日期',
    actual_return_date DATE COMMENT '实际归还日期',
    borrow_reason VARCHAR(500) COMMENT '领用原因',
    borrow_check VARCHAR(1000) COMMENT '领用时检查情况',
    return_check VARCHAR(1000) COMMENT '归还时检查情况',
    return_status TINYINT DEFAULT 0 COMMENT '归还状态 0未归还 1已归还 2逾期 3损坏 4丢失',
    damage_desc VARCHAR(500) COMMENT '损坏/丢失说明',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_by BIGINT COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_borrow_no (borrow_no),
    KEY idx_equipment_id (equipment_id),
    KEY idx_borrower_id (borrower_id),
    KEY idx_return_status (return_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备领用归还表';

-- 初始化设备数据
INSERT INTO smp_equipment (equipment_no, equipment_name, equipment_type, specification, manufacturer, status, borrow_status, storage_location, remark) VALUES
('EQ202401001', '便携式pH计', '2', 'PHS-3C', '上海雷磁', 1, 0, '设备室A区-01', '便携式，用于现场pH测定'),
('EQ202401002', '溶解氧测定仪', '2', 'JPB-607A', '上海雷磁', 1, 0, '设备室A区-02', '便携式，用于现场溶解氧测定'),
('EQ202401003', '电导率仪', '2', 'DDB-303A', '上海雷磁', 1, 0, '设备室A区-03', '便携式，用于现场电导率测定'),
('EQ202401004', '智能中流量采样器', '1', 'TH-150H', '武汉天虹', 1, 0, '设备室B区-01', '环境空气颗粒物采样'),
('EQ202401005', '大气综合采样器', '1', 'TH-110B', '武汉天虹', 1, 0, '设备室B区-02', '环境空气有害气体采样'),
('EQ202401006', '水质采样器', '1', 'ETC-100', '南京德林', 1, 0, '设备室C区-01', '污水、地表水采样'),
('EQ202401007', '土壤采样器', '1', 'ETC-300', '南京德林', 1, 0, '设备室C区-02', '土壤采样用'),
('EQ202401008', '声级计', '1', 'AWA5661', '杭州爱华', 1, 0, '设备室D区-01', '噪声测量'),
('EQ202401009', '500ml聚乙烯瓶', '3', '500ml', '国产', 1, 0, '容器区-A', '水质样品容器'),
('EQ202401010', '1L棕色玻璃瓶', '3', '1L', '国产', 1, 0, '容器区-B', '避光水样容器'),
('EQ202401011', '250ml聚乙烯瓶', '3', '250ml', '国产', 1, 0, '容器区-A', '水质样品容器'),
('EQ202401012', '土壤采样袋', '3', '500g', '国产', 1, 0, '容器区-C', '土壤样品容器');
