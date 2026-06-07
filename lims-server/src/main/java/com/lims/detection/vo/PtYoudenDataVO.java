package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("Youden图数据VO")
public class PtYoudenDataVO {

    @ApiModelProperty("样品代码")
    private String sampleCode;

    @ApiModelProperty("X轴值")
    private BigDecimal xValue;

    @ApiModelProperty("Y轴值")
    private BigDecimal yValue;

    @ApiModelProperty("实验室代码")
    private String laboratoryCode;

    @ApiModelProperty("评价结果")
    private String evaluation;
}
