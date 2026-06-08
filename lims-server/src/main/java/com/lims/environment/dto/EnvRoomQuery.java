package com.lims.environment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("房间查询DTO")
public class EnvRoomQuery extends PageQuery {

    @ApiModelProperty("房间编号")
    private String roomNo;

    @ApiModelProperty("房间名称")
    private String roomName;

    @ApiModelProperty("所属楼栋")
    private String building;

    @ApiModelProperty("楼层")
    private String floor;

    @ApiModelProperty("房间类型")
    private String roomType;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("状态")
    private Integer status;
}
