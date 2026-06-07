package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("稳定性方案查询DTO")
public class StabilitySchemeQuery {

    @ApiModelProperty("关键词（方案名称/样品名称）")
    private String keyword;

    @ApiModelProperty("方案类型")
    private String schemeType;

    @ApiModelProperty("状态")
    private Integer status;
}
