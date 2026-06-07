package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("质控图数据查询DTO")
public class QcChartDataQuery {

    @ApiModelProperty("检测项目")
    private String project;

    @ApiModelProperty("质控样ID")
    private Long sampleId;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;
}
