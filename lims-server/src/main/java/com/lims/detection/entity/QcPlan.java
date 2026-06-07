package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qc_plan")
@ApiModel("质控计划表")
public class QcPlan extends BaseEntity {

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("检测方法名称")
    private String methodName;

    @ApiModelProperty("周期类型 1每日 2每周 3每月 4每批次")
    private String cycleType;

    @ApiModelProperty("状态 0草稿 1启用 2停用")
    private Integer status;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("质控样品ID列表(JSON格式)")
    private String qcSamples;

    @ApiModelProperty("是否开启提醒")
    private Boolean reminderEnabled;

    @ApiModelProperty("提醒类型 1邮件 2短信 3站内信")
    private String reminderType;

    @ApiModelProperty("提前提醒天数")
    private Integer reminderAdvance;

    @ApiModelProperty("提醒用户ID列表(JSON格式)")
    private String reminderUsers;

    @ApiModelProperty("计划描述")
    private String description;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;
}
