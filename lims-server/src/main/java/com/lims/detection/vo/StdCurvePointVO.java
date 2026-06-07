package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("标准曲线点VO")
public class StdCurvePointVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("曲线ID")
    private Long curveId;

    @ApiModelProperty("点号")
    private Integer pointNo;

    @ApiModelProperty("浓度")
    private BigDecimal concentration;

    @ApiModelProperty("响应值1")
    private BigDecimal response1;

    @ApiModelProperty("响应值2")
    private BigDecimal response2;

    @ApiModelProperty("响应值3")
    private BigDecimal response3;

    @ApiModelProperty("响应平均值")
    private BigDecimal responseAvg;

    @ApiModelProperty("响应值RSD")
    private BigDecimal responseRsd;

    @ApiModelProperty("计算值")
    private BigDecimal calculatedValue;

    @ApiModelProperty("残差")
    private BigDecimal residual;

    @ApiModelProperty("相对误差")
    private BigDecimal relativeError;

    @ApiModelProperty("是否有效 0否 1是")
    private Integer isValid;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
