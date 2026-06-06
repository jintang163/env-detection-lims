package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("检测任务列表VO")
public class DetectionTaskVO {

    @ApiModelProperty("任务ID")
    private Long id;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法名称")
    private String methodName;

    @ApiModelProperty("优先级 1高 2中 3低")
    private Integer priority;

    @ApiModelProperty("优先级名称")
    private String priorityName;

    @ApiModelProperty("是否加急 0否 1是")
    private Integer isUrgent;

    @ApiModelProperty("分配方式 1手动分配 2智能分配 3抢单")
    private Integer assignType;

    @ApiModelProperty("分配方式名称")
    private String assignTypeName;

    @ApiModelProperty("分配对象类型 1个人 2科室")
    private Integer assigneeType;

    @ApiModelProperty("分配科室ID")
    private Long deptId;

    @ApiModelProperty("分配科室名称")
    private String deptName;

    @ApiModelProperty("分配人员ID")
    private Long assigneeId;

    @ApiModelProperty("分配人员姓名")
    private String assigneeName;

    @ApiModelProperty("分配时间")
    private LocalDateTime assignTime;

    @ApiModelProperty("接收时间")
    private LocalDateTime acceptTime;

    @ApiModelProperty("计划开始日期")
    private LocalDate planStartDate;

    @ApiModelProperty("计划完成日期")
    private LocalDate planEndDate;

    @ApiModelProperty("实际开始时间")
    private LocalDateTime actualStartTime;

    @ApiModelProperty("实际完成时间")
    private LocalDateTime actualEndTime;

    @ApiModelProperty("完成进度(%)")
    private BigDecimal progress;

    @ApiModelProperty("状态 0待分配 1待接收 2进行中 3数据录入 4审核中 5已完成 6已暂停 7已终止")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("是否抢单池任务 0否 1是")
    private Integer isGrabOrder;

    @ApiModelProperty("抢单截止时间")
    private LocalDateTime grabOrderExpireTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
