package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("质控样配置内嵌DTO")
public class QcSampleConfigDTO {

    @ApiModelProperty("质控样类型 1标准物质 2质控样 3加标样")
    private String type;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("浓度值")
    private BigDecimal concentration;

    @ApiModelProperty("浓度单位")
    private String unit;

    @ApiModelProperty("检测频率")
    private String frequency;
}
