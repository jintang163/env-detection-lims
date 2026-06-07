package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("配制记录VO")
public class QcSamplePrepareVO {

    @ApiModelProperty("配制记录ID")
    private Long id;

    @ApiModelProperty("配制编号")
    private String prepareNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型 1标准物质 2质控样 3加标样")
    private String sampleType;

    @ApiModelProperty("样品类型名称")
    private String sampleTypeName;

    @ApiModelProperty("批号")
    private String batchNo;

    @ApiModelProperty("配制体积")
    private BigDecimal prepareVolume;

    @ApiModelProperty("体积单位")
    private String volumeUnit;

    @ApiModelProperty("浓度值")
    private BigDecimal concentration;

    @ApiModelProperty("浓度单位")
    private String unit;

    @ApiModelProperty("配制方法")
    private String prepareMethod;

    @ApiModelProperty("使用试剂")
    private String reagents;

    @ApiModelProperty("使用仪器")
    private String instruments;

    @ApiModelProperty("配制人")
    private String prepareBy;

    @ApiModelProperty("校核人")
    private String verifyBy;

    @ApiModelProperty("环境条件")
    private String environment;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
