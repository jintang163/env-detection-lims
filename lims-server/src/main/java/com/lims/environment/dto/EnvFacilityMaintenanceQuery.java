package com.lims.environment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("维护记录查询DTO")
public class EnvFacilityMaintenanceQuery extends PageQuery {

    @ApiModelProperty("设施ID")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("维护类型")
    private Integer maintenanceType;

    @ApiModelProperty("维护开始日期")
    private LocalDate maintenanceDateStart;

    @ApiModelProperty("维护结束日期")
    private LocalDate maintenanceDateEnd;

    @ApiModelProperty("维护人ID")
    private Long maintainerId;

    @ApiModelProperty("维护结果")
    private Integer result;
}
