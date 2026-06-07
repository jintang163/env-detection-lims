package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel("配制记录保存DTO")
public class QcSamplePrepareSaveDTO {

    @ApiModelProperty("配制记录ID（更新时传）")
    private Long id;

    @ApiModelProperty("配制编号")
    @NotBlank(message = "配制编号不能为空")
    private String prepareNo;

    @ApiModelProperty("样品名称")
    @NotBlank(message = "样品名称不能为空")
    private String sampleName;

    @ApiModelProperty("样品类型")
    private String sampleType;

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
}
