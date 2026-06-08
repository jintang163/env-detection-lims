package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("环境监控统计VO")
public class EnvMonitorStatsVO {

    @ApiModelProperty("监控点总数")
    private Integer totalPoints;

    @ApiModelProperty("正常监控点数")
    private Integer normalPoints;

    @ApiModelProperty("预警监控点数")
    private Integer warningPoints;

    @ApiModelProperty("今日记录数")
    private Integer todayRecords;

    @ApiModelProperty("待处理预警数")
    private Integer pendingWarnings;

    @ApiModelProperty("温度预警数")
    private Integer temperatureWarning;

    @ApiModelProperty("湿度预警数")
    private Integer humidityWarning;

    @ApiModelProperty("压差预警数")
    private Integer pressureWarning;

    @ApiModelProperty("噪音预警数")
    private Integer noiseWarning;
}
