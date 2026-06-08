package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("设施统计VO")
public class EnvFacilityStatsVO {

    @ApiModelProperty("设施总数")
    private Integer totalFacilities;

    @ApiModelProperty("正常设施数")
    private Integer normalFacilities;

    @ApiModelProperty("维护中设施数")
    private Integer maintenanceFacilities;

    @ApiModelProperty("故障设施数")
    private Integer faultFacilities;

    @ApiModelProperty("停用设施数")
    private Integer disabledFacilities;

    @ApiModelProperty("到期待维护数")
    private Integer dueMaintenance;

    @ApiModelProperty("逾期未维护数")
    private Integer overdueMaintenance;

    @ApiModelProperty("房间总数")
    private Integer totalRooms;

    @ApiModelProperty("正常房间数")
    private Integer normalRooms;

    @ApiModelProperty("维护中房间数")
    private Integer maintenanceRooms;
}
