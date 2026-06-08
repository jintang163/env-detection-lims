package com.lims.equipment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("校准计划列表VO")
public class CalibrationPlanVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("校准类型：1校准 2检定")
    private Integer calibrationType;

    @ApiModelProperty("校准类型名称")
    private String calibrationTypeName;

    @ApiModelProperty("校准周期（月）")
    private Integer cycleMonths;

    @ApiModelProperty("上次校准日期")
    private LocalDate lastCalibrationDate;

    @ApiModelProperty("下次校准日期")
    private LocalDate nextCalibrationDate;

    @ApiModelProperty("提醒天数")
    private Integer remindDays;

    @ApiModelProperty("计划状态：0待执行 1已完成 2已过期")
    private Integer status;

    @ApiModelProperty("计划状态名称")
    private String statusName;

    @ApiModelProperty("是否即将到期")
    private Boolean upcoming;

    @ApiModelProperty("距离到期天数")
    private Long daysUntilDue;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    private String managerName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
