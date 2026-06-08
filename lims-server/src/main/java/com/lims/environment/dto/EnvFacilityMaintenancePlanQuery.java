package com.lims.environment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("维护计划查询DTO")
public class EnvFacilityMaintenancePlanQuery extends PageQuery {

    @ApiModelProperty("设施ID")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("维护类型")
    private Integer maintenanceType;

    @ApiModelProperty("下次维护开始日期")
    private LocalDate nextMaintenanceDateStart;

    @ApiModelProperty("下次维护结束日期")
    private LocalDate nextMaintenanceDateEnd;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("管理员ID")
    private Long managerId;
}
