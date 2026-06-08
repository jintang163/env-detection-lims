package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("维护计划保存DTO")
public class EnvFacilityMaintenancePlanSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设施ID")
    @NotNull(message = "设施ID不能为空")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("维护类型")
    @NotNull(message = "维护类型不能为空")
    private Integer maintenanceType;

    @ApiModelProperty("周期天数")
    @NotNull(message = "周期天数不能为空")
    private Integer cycleDays;

    @ApiModelProperty("上次维护日期")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty("下次维护日期")
    @NotNull(message = "下次维护日期不能为空")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("提前提醒天数")
    private Integer remindDays;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("管理员名称")
    private String managerName;

    @ApiModelProperty("内容模板")
    private String contentTemplate;

    @ApiModelProperty("备注")
    private String remark;
}
