package com.lims.equipment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("校准计划查询DTO")
public class CalibrationPlanQuery extends PageQuery {

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("校准类型")
    private Integer calibrationType;

    @ApiModelProperty("计划状态")
    private Integer status;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("下次校准开始日期")
    private LocalDate nextDateStart;

    @ApiModelProperty("下次校准结束日期")
    private LocalDate nextDateEnd;

    @ApiModelProperty("是否即将到期")
    private Boolean upcoming;

    @ApiModelProperty("提醒天数")
    private Integer remindDays;
}
