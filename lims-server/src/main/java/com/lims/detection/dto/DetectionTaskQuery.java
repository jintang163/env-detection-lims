package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("检测任务查询DTO")
public class DetectionTaskQuery extends PageQuery {

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("优先级 1高 2中 3低")
    private Integer priority;

    @ApiModelProperty("是否加急 0否 1是")
    private Integer isUrgent;

    @ApiModelProperty("分配方式 1手动分配 2智能分配 3抢单")
    private Integer assignType;

    @ApiModelProperty("分配对象类型 1个人 2科室")
    private Integer assigneeType;

    @ApiModelProperty("分配科室ID")
    private Long deptId;

    @ApiModelProperty("分配人员ID")
    private Long assigneeId;

    @ApiModelProperty("状态 0待分配 1待接收 2进行中 3数据录入 4审核中 5已完成 6已暂停 7已终止")
    private Integer status;

    @ApiModelProperty("是否抢单池任务 0否 1是")
    private Integer isGrabOrder;

    @ApiModelProperty("计划开始日期开始")
    private LocalDate planStartDateStart;

    @ApiModelProperty("计划开始日期结束")
    private LocalDate planStartDateEnd;

    @ApiModelProperty("计划完成日期开始")
    private LocalDate planEndDateStart;

    @ApiModelProperty("计划完成日期结束")
    private LocalDate planEndDateEnd;
}
