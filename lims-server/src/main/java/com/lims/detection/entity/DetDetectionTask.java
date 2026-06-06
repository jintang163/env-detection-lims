package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("det_detection_task")
@ApiModel("检测任务表")
public class DetDetectionTask extends BaseEntity {

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("关联委托单ID")
    private Long entrustId;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("关联样品ID")
    private Long sampleId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("检测项目ID列表(JSON格式)")
    private String testItemIds;

    @ApiModelProperty("检测项目名称列表(JSON格式)")
    private String testItemNames;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法编号")
    private String methodCode;

    @ApiModelProperty("标准方法名称")
    private String methodName;

    @ApiModelProperty("所需设备ID列表(JSON格式)")
    private String equipmentIds;

    @ApiModelProperty("所需设备名称列表(JSON格式)")
    private String equipmentNames;

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

    @ApiModelProperty("预计检测天数")
    private Integer expectedDays;

    @ApiModelProperty("完成进度(%)")
    private BigDecimal progress;

    @ApiModelProperty("状态 0待分配 1待接收 2进行中 3数据录入 4审核中 5已完成 6已暂停 7已终止")
    private Integer status;

    @ApiModelProperty("暂停前状态")
    private Integer beforePauseStatus;

    @ApiModelProperty("暂停原因")
    private String pauseReason;

    @ApiModelProperty("终止原因")
    private String terminateReason;

    @ApiModelProperty("数据录入人ID")
    private Long dataEntryUserId;

    @ApiModelProperty("数据录入人姓名")
    private String dataEntryUserName;

    @ApiModelProperty("审核人ID")
    private Long reviewerId;

    @ApiModelProperty("审核人姓名")
    private String reviewerName;

    @ApiModelProperty("审核意见")
    private String reviewOpinion;

    @ApiModelProperty("审核结果 1通过 2驳回")
    private Integer reviewResult;

    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty("是否抢单池任务 0否 1是")
    private Integer isGrabOrder;

    @ApiModelProperty("抢单截止时间")
    private LocalDateTime grabOrderExpireTime;

    @ApiModelProperty("备注")
    private String remark;
}
