package com.lims.equipment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("设备使用记录查询DTO")
public class EquipmentUsageQuery extends PageQuery {

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("使用人ID")
    private Long userId;

    @ApiModelProperty("使用人姓名")
    private String userName;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("运行状况")
    private Integer runningStatus;

    @ApiModelProperty("使用状态")
    private Integer usageStatus;

    @ApiModelProperty("开始使用开始时间")
    private LocalDateTime startTimeStart;

    @ApiModelProperty("开始使用结束时间")
    private LocalDateTime startTimeEnd;
}
