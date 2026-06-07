package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("CAPA查询DTO")
public class CapaQuery {

    @ApiModelProperty("关键词（标题/来源编号）")
    private String keyword;

    @ApiModelProperty("CAPA类型")
    private String capaType;

    @ApiModelProperty("来源类型")
    private String sourceType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("严重程度")
    private String severityLevel;

    @ApiModelProperty("优先级")
    private String priority;
}
