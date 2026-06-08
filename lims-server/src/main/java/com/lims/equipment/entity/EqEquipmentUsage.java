package com.lims.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("eq_equipment_usage")
@ApiModel("设备使用记录")
public class EqEquipmentUsage extends BaseEntity {

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

    @ApiModelProperty("开始使用时间")
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
    private Integer runningStatus;

    @ApiModelProperty("异常描述")
    private String anomalyDescription;

    @ApiModelProperty("使用状态：0使用中 1已完成")
    private Integer usageStatus;

    @ApiModelProperty("备注")
    private String remark;
}
