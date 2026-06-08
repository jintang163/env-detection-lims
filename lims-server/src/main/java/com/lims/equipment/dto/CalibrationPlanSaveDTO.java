package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("校准计划保存DTO")
public class CalibrationPlanSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设备ID")
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    @NotNull(message = "设备编号不能为空")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    @NotNull(message = "设备名称不能为空")
    private String equipmentName;

    @ApiModelProperty("校准类型：1校准 2检定")
    @NotNull(message = "校准类型不能为空")
    private Integer calibrationType;

    @ApiModelProperty("校准周期（月）")
    @NotNull(message = "校准周期不能为空")
    private Integer cycleMonths;

    @ApiModelProperty("上次校准日期")
    private LocalDate lastCalibrationDate;

    @ApiModelProperty("下次校准日期")
    @NotNull(message = "下次校准日期不能为空")
    private LocalDate nextCalibrationDate;

    @ApiModelProperty("提醒天数")
    private Integer remindDays;

    @ApiModelProperty("计划状态")
    private Integer status;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    private String managerName;

    @ApiModelProperty("备注")
    private String remark;
}
