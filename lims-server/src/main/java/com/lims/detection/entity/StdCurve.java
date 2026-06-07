package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("std_curve")
@ApiModel("标准曲线表")
public class StdCurve extends BaseEntity {

    @ApiModelProperty("曲线编号")
    private String curveNo;

    @ApiModelProperty("曲线名称")
    private String curveName;

    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("检测项目编码")
    private String itemCode;

    @ApiModelProperty("检测项目名称")
    private String itemName;

    @ApiModelProperty("方法ID")
    private Long methodId;

    @ApiModelProperty("方法名称")
    private String methodName;

    @ApiModelProperty("仪器ID")
    private Long instrumentId;

    @ApiModelProperty("仪器名称")
    private String instrumentName;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("拟合类型")
    private String fitType;

    @ApiModelProperty("权重类型")
    private String weightType;

    @ApiModelProperty("校准日期")
    private LocalDateTime calibrationDate;

    @ApiModelProperty("有效天数")
    private Integer validDays;

    @ApiModelProperty("有效期至")
    private LocalDateTime expireDate;

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

    @ApiModelProperty("曲线方程")
    private String curveEquation;

    @ApiModelProperty("残差标准差")
    private BigDecimal residualStd;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;

    @ApiModelProperty("配制人")
    private String preparedBy;

    @ApiModelProperty("审核人")
    private String verifiedBy;

    @ApiModelProperty("备注")
    private String remark;
}
