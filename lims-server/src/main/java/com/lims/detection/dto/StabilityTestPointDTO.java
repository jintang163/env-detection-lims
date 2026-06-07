package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("稳定性检测点DTO")
public class StabilityTestPointDTO {

    @ApiModelProperty("检测点ID")
    private Long id;

    @ApiModelProperty("检测点序号")
    private Integer pointNo;

    @ApiModelProperty("检测天数")
    private Integer testDay;

    @ApiModelProperty("计划检测日期")
    private LocalDateTime planDate;
}
