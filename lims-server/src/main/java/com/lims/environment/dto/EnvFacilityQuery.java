package com.lims.environment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("设施查询DTO")
public class EnvFacilityQuery extends PageQuery {

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("设施类型")
    private Integer facilityType;

    @ApiModelProperty("房间ID")
    private Long roomId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("管理员ID")
    private Long managerId;
}
