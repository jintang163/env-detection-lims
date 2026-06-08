package com.lims.environment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("监控数据查询DTO")
public class EnvMonitorDataQuery extends PageQuery {

    @ApiModelProperty("监控点")
    private String monitorPoint;

    @ApiModelProperty("监控类型")
    private Integer monitorType;

    @ApiModelProperty("采集开始时间")
    private LocalDateTime collectTimeStart;

    @ApiModelProperty("采集结束时间")
    private LocalDateTime collectTimeEnd;

    @ApiModelProperty("是否预警")
    private Integer isWarning;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("采集人ID")
    private Long collectorId;
}
