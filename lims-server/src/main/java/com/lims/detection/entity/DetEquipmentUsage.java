package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("det_equipment_usage")
@ApiModel("设备使用记录表")
public class DetEquipmentUsage implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("使用记录ID")
    private Long id;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

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

    @ApiModelProperty("计划开始使用时间")
    private LocalDateTime planStartTime;

    @ApiModelProperty("计划结束使用时间")
    private LocalDateTime planEndTime;

    @ApiModelProperty("实际开始使用时间")
    private LocalDateTime actualStartTime;

    @ApiModelProperty("实际结束使用时间")
    private LocalDateTime actualEndTime;

    @ApiModelProperty("使用状态 0待使用 1使用中 2已完成")
    private Integer usageStatus;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
