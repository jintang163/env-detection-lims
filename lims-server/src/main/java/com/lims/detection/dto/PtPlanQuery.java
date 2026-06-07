package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("能力验证计划查询")
public class PtPlanQuery {

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("能力验证类型")
    private String ptType;

    @ApiModelProperty("状态")
    private Integer status;
}
