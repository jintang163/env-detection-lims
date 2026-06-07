package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("分析结果VO")
public class QcAnalyzeResultVO {

    @ApiModelProperty("总数据点数")
    private Integer total;

    @ApiModelProperty("在控数")
    private Integer inControl;

    @ApiModelProperty("警告数")
    private Integer warning;

    @ApiModelProperty("失控数")
    private Integer outControl;

    @ApiModelProperty("均值")
    private BigDecimal mean;

    @ApiModelProperty("标准偏差")
    private BigDecimal sd;

    @ApiModelProperty("均值(兼容字段)")
    private BigDecimal meanValue;

    @ApiModelProperty("标准偏差(兼容字段)")
    private BigDecimal sdValue;

    @ApiModelProperty("总体状态 in_control-在控 warning-警告 out_control-失控")
    private String overallStatus;

    @ApiModelProperty("总体状态名称")
    private String overallStatusName;

    @ApiModelProperty("失控/警告数据列表")
    private List<QcDataVO> violationData;
}
