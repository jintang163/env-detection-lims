package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("标准曲线查询")
public class StdCurveQuery {

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("拟合类型")
    private String fitType;
}
