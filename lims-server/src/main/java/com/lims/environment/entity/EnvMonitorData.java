package com.lims.environment.entity;

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
@TableName("env_monitor_data")
@ApiModel("环境监控数据")
public class EnvMonitorData extends BaseEntity {

    @ApiModelProperty("监控点")
    private String monitorPoint;

    @ApiModelProperty("监控点名称")
    private String monitorPointName;

    @ApiModelProperty("监控类型")
    private Integer monitorType;

    @ApiModelProperty("监控值")
    private BigDecimal monitorValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("采集时间")
    private LocalDateTime collectTime;

    @ApiModelProperty("采集方式")
    private Integer collectMethod;

    @ApiModelProperty("是否预警")
    private Integer isWarning;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("预警信息")
    private String warnMessage;

    @ApiModelProperty("采集人ID")
    private Long collectorId;

    @ApiModelProperty("采集人名称")
    private String collectorName;

    @ApiModelProperty("备注")
    private String remark;
}
