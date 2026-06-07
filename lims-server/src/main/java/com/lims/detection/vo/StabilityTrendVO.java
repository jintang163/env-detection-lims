package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("稳定性趋势图VO")
public class StabilityTrendVO {

    @ApiModelProperty("考察天数")
    private Integer testDay;

    @ApiModelProperty("检测日期")
    private LocalDateTime testDate;

    @ApiModelProperty("检测值")
    private BigDecimal testValue;

    @ApiModelProperty("偏差率")
    private BigDecimal deviationRate;

    @ApiModelProperty("是否可接受")
    private Boolean isAcceptable;
}
