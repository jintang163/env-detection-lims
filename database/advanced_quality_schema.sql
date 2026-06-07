-- =============================================
-- 高级质量管理模块数据库脚本
-- 包含：能力验证、标准曲线、稳定性考察、CAPA
-- =============================================

-- =============================================
-- 1. 能力验证模块
-- =============================================

-- 能力验证计划表
DROP TABLE IF EXISTS `pt_plan`;
CREATE TABLE `pt_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pt_no` varchar(50) NOT NULL COMMENT '能力验证编号',
  `pt_name` varchar(200) NOT NULL COMMENT '能力验证名称',
  `organizer` varchar(200) DEFAULT NULL COMMENT '组织者（CNAS等）',
  `pt_type` varchar(20) NOT NULL COMMENT '类型：PT能力验证 ILC实验室间比对',
  `project_name` varchar(100) NOT NULL COMMENT '检测项目',
  `item_code` varchar(50) DEFAULT NULL COMMENT '项目编码',
  `matrix` varchar(100) DEFAULT NULL COMMENT '基质类型',
  `register_deadline` datetime DEFAULT NULL COMMENT '报名截止时间',
  `sample_dispatch_date` datetime DEFAULT NULL COMMENT '样品发放时间',
  `result_deadline` datetime DEFAULT NULL COMMENT '结果上报截止时间',
  `feedback_date` datetime DEFAULT NULL COMMENT '结果反馈时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0草稿 1报名中 2待检测 3已上报 4已评价 5已完成 6已取消',
  `registration_fee` decimal(10,2) DEFAULT '0.00' COMMENT '报名费',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `description` text COMMENT '描述',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pt_no` (`pt_no`),
  KEY `idx_status` (`status`),
  KEY `idx_pt_type` (`pt_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='能力验证计划表';

-- 能力验证样品表
DROP TABLE IF EXISTS `pt_sample`;
CREATE TABLE `pt_sample` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pt_plan_id` bigint NOT NULL COMMENT '能力验证计划ID',
  `sample_no` varchar(50) NOT NULL COMMENT '样品编号',
  `sample_name` varchar(200) DEFAULT NULL COMMENT '样品名称',
  `sample_code` varchar(50) DEFAULT NULL COMMENT '盲样编码',
  `matrix` varchar(100) DEFAULT NULL COMMENT '基质',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `assigned_value` decimal(18,6) DEFAULT NULL COMMENT '指定值',
  `assigned_value_uncertainty` decimal(18,6) DEFAULT NULL COMMENT '指定值不确定度',
  `target_sd` decimal(18,6) DEFAULT NULL COMMENT '目标标准差',
  `receive_date` datetime DEFAULT NULL COMMENT '接收日期',
  `storage_condition` varchar(200) DEFAULT NULL COMMENT '储存条件',
  `expire_date` datetime DEFAULT NULL COMMENT '有效期',
  `sample_status` tinyint DEFAULT '1' COMMENT '样品状态：0未接收 1已接收 2检测中 3已用完',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_pt_plan_id` (`pt_plan_id`),
  KEY `idx_sample_code` (`sample_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='能力验证样品表';

-- 能力验证结果表
DROP TABLE IF EXISTS `pt_result`;
CREATE TABLE `pt_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pt_plan_id` bigint NOT NULL COMMENT '能力验证计划ID',
  `pt_sample_id` bigint NOT NULL COMMENT '能力验证样品ID',
  `result_no` varchar(50) NOT NULL COMMENT '结果编号',
  `detected_value` decimal(18,6) NOT NULL COMMENT '实验室检测值',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `measure_date` datetime DEFAULT NULL COMMENT '检测日期',
  `operator` varchar(50) DEFAULT NULL COMMENT '检测人',
  `instrument` varchar(200) DEFAULT NULL COMMENT '使用仪器',
  `method_name` varchar(200) DEFAULT NULL COMMENT '检测方法',
  `uncertainty` decimal(18,6) DEFAULT NULL COMMENT '不确定度',
  `z_score` decimal(10,4) DEFAULT NULL COMMENT 'Z比分数',
  `z_score_type` varchar(20) DEFAULT NULL COMMENT 'Z比分数类型：Z实验室间 Z实验室内',
  `evaluation` varchar(20) DEFAULT NULL COMMENT '评价：satisfactory满意 questionable可疑 unsatisfactory不满意',
  `report_status` tinyint DEFAULT '0' COMMENT '上报状态：0未上报 1已上报 2已修改重报',
  `report_date` datetime DEFAULT NULL COMMENT '上报日期',
  `feedback_result` text COMMENT '官方反馈结果',
  `feedback_file` varchar(500) DEFAULT NULL COMMENT '反馈报告附件',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_pt_plan_id` (`pt_plan_id`),
  KEY `idx_pt_sample_id` (`pt_sample_id`),
  KEY `idx_evaluation` (`evaluation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='能力验证结果表';

-- =============================================
-- 2. 标准曲线管理模块
-- =============================================

-- 标准曲线表
DROP TABLE IF EXISTS `std_curve`;
CREATE TABLE `std_curve` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `curve_no` varchar(50) NOT NULL COMMENT '曲线编号',
  `curve_name` varchar(200) NOT NULL COMMENT '曲线名称',
  `item_id` bigint DEFAULT NULL COMMENT '检测项目ID',
  `item_code` varchar(50) DEFAULT NULL COMMENT '项目编码',
  `item_name` varchar(100) NOT NULL COMMENT '检测项目',
  `method_id` bigint DEFAULT NULL COMMENT '方法ID',
  `method_name` varchar(200) DEFAULT NULL COMMENT '检测方法',
  `instrument_id` bigint DEFAULT NULL COMMENT '仪器ID',
  `instrument_name` varchar(200) DEFAULT NULL COMMENT '仪器名称',
  `unit` varchar(20) DEFAULT NULL COMMENT '浓度单位',
  `fit_type` varchar(20) NOT NULL DEFAULT 'linear' COMMENT '拟合类型：linear线性 quadratic二次 linear_origin线性过原点 weighted加权',
  `weight_type` varchar(20) DEFAULT NULL COMMENT '权重类型：1/x 1/x²',
  `calibration_date` datetime NOT NULL COMMENT '校准日期',
  `valid_days` int DEFAULT '30' COMMENT '有效天数',
  `expire_date` datetime NOT NULL COMMENT '有效期至',
  `r_squared` decimal(10,8) DEFAULT NULL COMMENT '相关系数R²',
  `slope` decimal(18,8) DEFAULT NULL COMMENT '斜率',
  `intercept` decimal(18,8) DEFAULT NULL COMMENT '截距',
  `correlation_coefficient` decimal(10,8) DEFAULT NULL COMMENT '相关系数r',
  `lod` decimal(18,8) DEFAULT NULL COMMENT '检出限',
  `loq` decimal(18,8) DEFAULT NULL COMMENT '定量限',
  `curve_equation` varchar(200) DEFAULT NULL COMMENT '曲线方程',
  `residual_std` decimal(18,8) DEFAULT NULL COMMENT '残差标准差',
  `status` tinyint DEFAULT '1' COMMENT '状态：0过期 1有效 2作废',
  `prepared_by` varchar(50) DEFAULT NULL COMMENT '配制人',
  `verified_by` varchar(50) DEFAULT NULL COMMENT '校核人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_curve_no` (`curve_no`),
  KEY `idx_item_code` (`item_code`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标准曲线表';

-- 标准曲线点表
DROP TABLE IF EXISTS `std_curve_point`;
CREATE TABLE `std_curve_point` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `curve_id` bigint NOT NULL COMMENT '曲线ID',
  `point_no` int NOT NULL COMMENT '点序号',
  `concentration` decimal(18,8) NOT NULL COMMENT '浓度值',
  `response_1` decimal(18,8) DEFAULT NULL COMMENT '响应值1（吸光度/峰面积等）',
  `response_2` decimal(18,8) DEFAULT NULL COMMENT '响应值2',
  `response_3` decimal(18,8) DEFAULT NULL COMMENT '响应值3',
  `response_avg` decimal(18,8) DEFAULT NULL COMMENT '平均响应值',
  `response_rsd` decimal(10,4) DEFAULT NULL COMMENT '响应值RSD(%)',
  `calculated_value` decimal(18,8) DEFAULT NULL COMMENT '回算浓度',
  `residual` decimal(18,8) DEFAULT NULL COMMENT '残差',
  `relative_error` decimal(10,4) DEFAULT NULL COMMENT '相对误差(%)',
  `is_valid` tinyint DEFAULT '1' COMMENT '是否有效：0无效 1有效',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_curve_id` (`curve_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标准曲线点表';

-- =============================================
-- 3. 稳定性考察模块
-- =============================================

-- 稳定性考察方案表
DROP TABLE IF EXISTS `stability_scheme`;
CREATE TABLE `stability_scheme` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scheme_no` varchar(50) NOT NULL COMMENT '方案编号',
  `scheme_name` varchar(200) NOT NULL COMMENT '方案名称',
  `sample_name` varchar(200) NOT NULL COMMENT '考察样品',
  `sample_type` varchar(50) DEFAULT NULL COMMENT '样品类型',
  `item_name` varchar(100) NOT NULL COMMENT '检测项目',
  `item_code` varchar(50) DEFAULT NULL COMMENT '项目编码',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `initial_value` decimal(18,6) DEFAULT NULL COMMENT '初始值',
  `acceptance_criterion` decimal(10,4) DEFAULT NULL COMMENT '可接受偏差(%)',
  `scheme_type` varchar(20) NOT NULL DEFAULT 'long_term' COMMENT '考察类型：long_term长期 accelerated加速',
  `storage_condition` varchar(200) DEFAULT NULL COMMENT '储存条件（温度/湿度）',
  `temperature` decimal(5,2) DEFAULT NULL COMMENT '考察温度(℃)',
  `humidity` decimal(5,2) DEFAULT NULL COMMENT '考察湿度(RH%)',
  `duration_days` int DEFAULT NULL COMMENT '总考察天数',
  `estimated_shelf_life` int DEFAULT NULL COMMENT '预估保质期(天)',
  `start_date` datetime DEFAULT NULL COMMENT '开始日期',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `status` tinyint DEFAULT '0' COMMENT '状态：0未开始 1进行中 2已完成 3已取消',
  `description` text COMMENT '考察说明',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheme_no` (`scheme_no`),
  KEY `idx_scheme_type` (`scheme_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='稳定性考察方案表';

-- 稳定性考察点表
DROP TABLE IF EXISTS `stability_test_point`;
CREATE TABLE `stability_test_point` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scheme_id` bigint NOT NULL COMMENT '方案ID',
  `point_no` int NOT NULL COMMENT '时间点序号',
  `test_day` int NOT NULL COMMENT '考察天数',
  `plan_date` datetime DEFAULT NULL COMMENT '计划检测日期',
  `actual_date` datetime DEFAULT NULL COMMENT '实际检测日期',
  `task_id` bigint DEFAULT NULL COMMENT '关联检测任务ID',
  `task_no` varchar(50) DEFAULT NULL COMMENT '检测任务编号',
  `test_value_1` decimal(18,6) DEFAULT NULL COMMENT '检测值1',
  `test_value_2` decimal(18,6) DEFAULT NULL COMMENT '检测值2',
  `test_value_avg` decimal(18,6) DEFAULT NULL COMMENT '检测平均值',
  `rsd` decimal(10,4) DEFAULT NULL COMMENT '相对标准偏差(%)',
  `deviation_rate` decimal(10,4) DEFAULT NULL COMMENT '与初始值偏差(%)',
  `is_acceptable` tinyint DEFAULT NULL COMMENT '是否符合要求：0不符合 1符合',
  `operator` varchar(50) DEFAULT NULL COMMENT '检测人',
  `status` tinyint DEFAULT '0' COMMENT '状态：0待检测 1检测中 2已完成 3已取消',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_scheme_id` (`scheme_id`),
  KEY `idx_test_day` (`test_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='稳定性考察时间点表';

-- =============================================
-- 4. CAPA纠正预防措施模块
-- =============================================

-- CAPA记录表
DROP TABLE IF EXISTS `capa_record`;
CREATE TABLE `capa_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `capa_no` varchar(50) NOT NULL COMMENT 'CAPA编号',
  `capa_type` varchar(20) NOT NULL COMMENT '类型：CA纠正措施 PA预防措施',
  `source_type` varchar(50) NOT NULL COMMENT '来源类型：OOS、客户投诉、内审、外审、能力验证、偏差、其他',
  `source_id` bigint DEFAULT NULL COMMENT '来源记录ID',
  `source_no` varchar(50) DEFAULT NULL COMMENT '来源编号',
  `title` varchar(200) NOT NULL COMMENT 'CAPA标题',
  `description` text NOT NULL COMMENT '问题描述',
  `root_cause` text COMMENT '根本原因分析',
  `root_cause_method` varchar(100) DEFAULT NULL COMMENT '原因分析方法：5Why、鱼骨图、FMEA等',
  `severity_level` tinyint DEFAULT '2' COMMENT '严重等级：1低 2中 3高',
  `priority` tinyint DEFAULT '2' COMMENT '优先级：1低 2中 3高',
  `status` tinyint DEFAULT '0' COMMENT '状态：0草稿 1待审批 2执行中 3待验证 4已验证 5已关闭 6已驳回',
  `risk_assessment` text COMMENT '风险评估',
  `proposed_measures` text COMMENT '拟采取措施',
  `implementor` bigint DEFAULT NULL COMMENT '执行人ID',
  `implementor_name` varchar(50) DEFAULT NULL COMMENT '执行人姓名',
  `verifier` bigint DEFAULT NULL COMMENT '验证人ID',
  `verifier_name` varchar(50) DEFAULT NULL COMMENT '验证人姓名',
  `approver` bigint DEFAULT NULL COMMENT '审批人ID',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `plan_complete_date` datetime DEFAULT NULL COMMENT '计划完成日期',
  `actual_complete_date` datetime DEFAULT NULL COMMENT '实际完成日期',
  `verification_date` datetime DEFAULT NULL COMMENT '验证日期',
  `verification_result` text COMMENT '验证结果',
  `verification_evidence` varchar(500) DEFAULT NULL COMMENT '验证证据附件',
  `close_date` datetime DEFAULT NULL COMMENT '关闭日期',
  `effectiveness_review` text COMMENT '有效性复查',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_capa_no` (`capa_no`),
  KEY `idx_capa_type` (`capa_type`),
  KEY `idx_status` (`status`),
  KEY `idx_severity_level` (`severity_level`),
  KEY `idx_source` (`source_type`, `source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CAPA纠正预防措施表';

-- CAPA处理日志表
DROP TABLE IF EXISTS `capa_process_log`;
CREATE TABLE `capa_process_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `capa_id` bigint NOT NULL COMMENT 'CAPA记录ID',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型：创建、提交、审批、驳回、执行、验证、关闭',
  `operation_status` varchar(20) DEFAULT NULL COMMENT '操作后状态',
  `operation_content` text COMMENT '操作内容',
  `attachments` varchar(500) DEFAULT NULL COMMENT '附件',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_capa_id` (`capa_id`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CAPA处理日志表';

-- =============================================
-- 初始化数据
-- =============================================

-- 标准曲线初始化数据
INSERT INTO `std_curve` (`curve_no`, `curve_name`, `item_name`, `item_code`, `unit`, `fit_type`, `calibration_date`, `valid_days`, `expire_date`, `r_squared`, `slope`, `intercept`, `lod`, `status`, `prepared_by`) VALUES
('CURVE-2024-001', '铜标准曲线(0-5mg/L)', '铜', 'Cu', 'mg/L', 'linear', NOW(), 30, DATE_ADD(NOW(), INTERVAL 30 DAY), 0.9998, 0.1256, 0.0012, 0.005, 1, '张三'),
('CURVE-2024-002', '铅标准曲线(0-50μg/L)', '铅', 'Pb', 'μg/L', 'linear', NOW(), 30, DATE_ADD(NOW(), INTERVAL 30 DAY), 0.9995, 0.0089, 0.012, 0.1, 1, '李四');

-- 标准曲线点初始化数据
INSERT INTO `std_curve_point` (`curve_id`, `point_no`, `concentration`, `response_1`, `response_2`, `response_3`, `response_avg`, `calculated_value`, `is_valid`) VALUES
(1, 1, 0.0, 0.001, 0.0012, 0.0008, 0.0010, 0.002, 1),
(1, 2, 0.5, 0.064, 0.063, 0.065, 0.0640, 0.498, 1),
(1, 3, 1.0, 0.127, 0.125, 0.126, 0.1260, 0.995, 1),
(1, 4, 2.0, 0.253, 0.251, 0.254, 0.2527, 2.002, 1),
(1, 5, 3.0, 0.379, 0.377, 0.380, 0.3787, 3.008, 1),
(1, 6, 5.0, 0.628, 0.629, 0.627, 0.6280, 4.990, 1),
(2, 1, 0.0, 0.0015, 0.0018, 0.0012, 0.0015, 0.05, 1),
(2, 2, 5.0, 0.046, 0.045, 0.047, 0.0460, 4.83, 1),
(2, 3, 10.0, 0.091, 0.089, 0.092, 0.0907, 9.85, 1),
(2, 4, 20.0, 0.182, 0.180, 0.184, 0.1820, 20.11, 1),
(2, 5, 50.0, 0.457, 0.455, 0.459, 0.4570, 50.06, 1);

-- 能力验证计划初始化数据
INSERT INTO `pt_plan` (`pt_no`, `pt_name`, `organizer`, `pt_type`, `project_name`, `item_code`, `matrix`, `status`) VALUES
('PT-2024-001', '2024年水中铜能力验证', 'CNAS', 'PT', '铜', 'Cu', '水质', 1),
('PT-2024-002', '2024年土壤中铅能力验证', 'CNAS', 'PT', '铅', 'Pb', '土壤', 1),
('ILC-2024-001', '第一季度实验室间比对', '本实验室', 'ILC', '铜', 'Cu', '水质', 2);

-- 能力验证样品初始化数据
INSERT INTO `pt_sample` (`pt_plan_id`, `sample_no`, `sample_name`, `sample_code`, `unit`, `assigned_value`, `target_sd`, `receive_date`, `sample_status`) VALUES
(1, 'PT-S-2024-001A', '水中铜考核样A', 'PT-WATER-CU-A', 'mg/L', 1.25, 0.05, NOW(), 1),
(1, 'PT-S-2024-001B', '水中铜考核样B', 'PT-WATER-CU-B', 'mg/L', 2.38, 0.08, NOW(), 1),
(3, 'ILC-S-2024-001', '比对样品', 'ILC-WATER-CU-001', 'mg/L', NULL, NULL, NOW(), 2);

-- 稳定性考察方案初始化数据
INSERT INTO `stability_scheme` (`scheme_no`, `scheme_name`, `sample_name`, `item_name`, `unit`, `initial_value`, `acceptance_criterion`, `scheme_type`, `storage_condition`, `temperature`, `duration_days`, `start_date`, `status`) VALUES
('STAB-2024-001', '铜标准液长期稳定性考察', '铜标准溶液1mg/L', '铜', 'mg/L', 1.00, 5.0, 'long_term', '2-8℃冷藏', 4.0, 180, NOW(), 1),
('STAB-2024-002', '土壤样品加速稳定性考察', '土壤重金属质控样', '铅', 'mg/kg', 25.0, 10.0, 'accelerated', '40℃/75%RH', 40.0, 90, NOW(), 0);

-- 稳定性考察点初始化数据
INSERT INTO `stability_test_point` (`scheme_id`, `point_no`, `test_day`, `plan_date`, `status`) VALUES
(1, 1, 0, NOW(), 2),
(1, 2, 7, DATE_ADD(NOW(), INTERVAL 7 DAY), 0),
(1, 3, 14, DATE_ADD(NOW(), INTERVAL 14 DAY), 0),
(1, 4, 30, DATE_ADD(NOW(), INTERVAL 30 DAY), 0),
(1, 5, 60, DATE_ADD(NOW(), INTERVAL 60 DAY), 0),
(1, 6, 90, DATE_ADD(NOW(), INTERVAL 90 DAY), 0),
(1, 7, 180, DATE_ADD(NOW(), INTERVAL 180 DAY), 0);

-- CAPA初始化数据
INSERT INTO `capa_record` (`capa_no`, `capa_type`, `source_type`, `title`, `description`, `root_cause`, `severity_level`, `priority`, `status`, `implementor_name`, `verifier_name`, `plan_complete_date`) VALUES
('CAPA-2024-001', 'CA', 'OOS', '纠正措施：2024-02-20铜检测OOS超标', '2024-02-20批次B20240220001中铜检测结果超标，超出标准限值1.2倍', '经5Why分析，根本原因为原子吸收分光光度计雾化器堵塞，导致检测结果偏高', 2, 3, 2, '张三', '李四', DATE_ADD(NOW(), INTERVAL 7 DAY)),
('CAPA-2024-002', 'PA', '内审', '预防措施：实验室环境温湿度监控频率不足', '内审发现实验室环境温湿度监控频率为每日1次，不足以保证检测环境条件持续符合要求', '风险评估显示温湿度波动可能影响仪器稳定性和检测结果准确性', 1, 2, 0, '王五', '赵六', DATE_ADD(NOW(), INTERVAL 14 DAY));

-- CAPA日志初始化数据
INSERT INTO `capa_process_log` (`capa_id`, `operation_type`, `operation_status`, `operation_content`, `operator_name`) VALUES
(1, '创建', '0', '创建CAPA，描述OOS超标问题', '李四'),
(1, '提交审批', '1', '提交CAPA审批，附根本原因分析报告', '李四'),
(1, '审批通过', '2', '审批通过，同意采取的纠正措施', '主任'),
(2, '创建', '0', '创建CAPA预防措施', '王五');
