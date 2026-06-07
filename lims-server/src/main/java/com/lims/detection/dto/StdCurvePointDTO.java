package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("标准曲线点DTO")
public class StdCurvePointDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("点序号")
    private Integer pointNo;

    @ApiModelProperty("浓度")
    private BigDecimal concentration;

    @ApiModelProperty("响应值1")
    private BigDecimal response1;

    @ApiModelProperty("响应值2")
    private BigDecimal response2;

    @ApiModelProperty("响应值3")
    private BigDecimal response3;

    @ApiModelProperty("是否有效")
    private Boolean isValid;
}
