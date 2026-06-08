package com.lims.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("eq_repair_request")
@ApiModel("设备维修申请")
public class EqRepairRequest extends BaseEntity {

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("故障描述")
    private String faultDescription;

    @ApiModelProperty("故障发生时间")
    private LocalDateTime faultTime;

    @ApiModelProperty("申请人ID")
    private Long applicantId;

    @ApiModelProperty("申请人名称")
    private String applicantName;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty("紧急程度：1一般 2紧急 3特急")
    private Integer urgency;

    @ApiModelProperty("申请状态：0待受理 1维修中 2已完成 3已驳回")
    private Integer status;

    @ApiModelProperty("受理人ID")
    private Long handlerId;

    @ApiModelProperty("受理人名称")
    private String handlerName;

    @ApiModelProperty("受理时间")
    private LocalDateTime handleTime;

    @ApiModelProperty("维修单位")
    private String repairUnit;

    @ApiModelProperty("维修人员")
    private String repairPerson;

    @ApiModelProperty("维修开始时间")
    private LocalDateTime repairStartTime;

    @ApiModelProperty("维修结束时间")
    private LocalDateTime repairEndTime;

    @ApiModelProperty("维修内容")
    private String repairContent;

    @ApiModelProperty("更换部件")
    private String replacedParts;

    @ApiModelProperty("维修费用")
    private java.math.BigDecimal repairCost;

    @ApiModelProperty("维修结果：1修复 2部分修复 3无法修复")
    private Integer repairResult;

    @ApiModelProperty("确认人ID")
    private Long confirmerId;

    @ApiModelProperty("确认人名称")
    private String confirmerName;

    @ApiModelProperty("确认时间")
    private LocalDateTime confirmTime;

    @ApiModelProperty("确认意见")
    private String confirmOpinion;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
