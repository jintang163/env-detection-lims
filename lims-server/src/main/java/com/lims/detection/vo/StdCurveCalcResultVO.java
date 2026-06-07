package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("曲线计算结果VO")
public class StdCurveCalcResultVO {

    @ApiModelProperty("曲线方程")
    private String curveEquation;

    @ApiModelProperty("决定系数R²")
    private BigDecimal rSquared;

    @ApiModelProperty("斜率")
    private BigDecimal slope;

    @ApiModelProperty("截距")
    private BigDecimal intercept;

    @ApiModelProperty("相关系数")
    private BigDecimal correlationCoefficient;

    @ApiModelProperty("检出限LOD")
    private BigDecimal lod;

    @ApiModelProperty("定量限LOQ")
    private BigDecimal loq;

    @ApiModelProperty("残差标准差")
    private BigDecimal residualStd;

    @ApiModelProperty("标准曲线点列表")
    private List<StdCurvePointVO> points;
}
