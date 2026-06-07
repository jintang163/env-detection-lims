package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("质控图数据VO")
public class QcChartDataVO {

    @ApiModelProperty("X轴数据（时间标签）")
    private List<String> xData;

    @ApiModelProperty("Y轴数据（检测值）")
    private List<BigDecimal> yData;

    @ApiModelProperty("均值")
    private BigDecimal meanValue;

    @ApiModelProperty("标准偏差")
    private BigDecimal sdValue;

    @ApiModelProperty("Z分数")
    private List<BigDecimal> zScores;

    @ApiModelProperty("CUSUM正累积和")
    private List<BigDecimal> cusumPos;

    @ApiModelProperty("CUSUM负累积和")
    private List<BigDecimal> cusumNeg;

    @ApiModelProperty("失控/警告数据点")
    private List<QcDataVO> violationData;
}
