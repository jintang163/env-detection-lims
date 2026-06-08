package com.lims.environment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("env_facility_maintenance_plan")
@ApiModel("设施维护计划")
public class EnvFacilityMaintenancePlan extends BaseEntity {

    @ApiModelProperty("设施ID")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("维护类型")
    private Integer maintenanceType;

    @ApiModelProperty("周期天数")
    private Integer cycleDays;

    @ApiModelProperty("上次维护日期")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("提醒天数")
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
