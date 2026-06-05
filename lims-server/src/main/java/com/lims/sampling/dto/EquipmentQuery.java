package com.lims.sampling.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("设备查询DTO")
public class EquipmentQuery extends PageQuery {

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("设备类型")
    private String equipmentType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("领用状态")
    private Integer borrowStatus;

    @ApiModelProperty("存放位置")
    private String storageLocation;
}
