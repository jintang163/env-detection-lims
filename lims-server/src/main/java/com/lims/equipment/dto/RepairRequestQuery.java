package com.lims.equipment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("维修申请查询DTO")
public class RepairRequestQuery extends PageQuery {

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("申请人ID")
    private Long applicantId;

    @ApiModelProperty("紧急程度")
    private Integer urgency;

    @ApiModelProperty("申请状态")
    private Integer status;

    @ApiModelProperty("受理人ID")
    private Long handlerId;

    @ApiModelProperty("维修结果")
    private Integer repairResult;

    @ApiModelProperty("申请开始时间")
    private LocalDateTime applyTimeStart;

    @ApiModelProperty("申请结束时间")
    private LocalDateTime applyTimeEnd;
}
