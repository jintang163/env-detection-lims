package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel("设备使用记录保存DTO")
public class EquipmentUsageSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设备ID")
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    @NotBlank(message = "设备编号不能为空")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @ApiModelProperty("使用人ID")
    @NotNull(message = "使用人ID不能为空")
    private Long userId;

    @ApiModelProperty("使用人姓名")
    @NotBlank(message = "使用人姓名不能为空")
    private String userName;

    @ApiModelProperty("开始使用时间")
    @NotNull(message = "开始使用时间不能为空")
    private LocalDateTime startTime;

    @ApiModelProperty("结束使用时间")
    private LocalDateTime endTime;

    @ApiModelProperty("使用时长(分钟)")
    private Integer usageMinutes;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("数据记录ID")
    private Long dataRecordId;

    @ApiModelProperty("运行状况：1正常 2异常 3故障")
    @NotNull(message = "运行状况不能为空")
    private Integer runningStatus;

    @ApiModelProperty("异常描述")
    private String anomalyDescription;

    @ApiModelProperty("使用状态：0使用中 1已完成")
    private Integer usageStatus;

    @ApiModelProperty("备注")
    private String remark;
}
