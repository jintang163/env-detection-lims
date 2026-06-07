package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("稳定性检测结果录入DTO")
public class StabilityTestResultDTO {

    @ApiModelProperty("结果ID")
    private Long id;

    @ApiModelProperty("检测值1")
    private BigDecimal testValue1;

    @ApiModelProperty("检测值2")
    private BigDecimal testValue2;

    @ApiModelProperty("检测平均值")
    private BigDecimal testValueAvg;

    @ApiModelProperty("相对标准偏差RSD")
    private BigDecimal rsd;

    @ApiModelProperty("操作人员")
    private String operator;

    @ApiModelProperty("备注")
    private String remark;
}
