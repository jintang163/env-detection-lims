package com.lims.environment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("env_monitor_threshold")
@ApiModel("环境监控阈值配置")
public class EnvMonitorThreshold extends BaseEntity {

    @ApiModelProperty("监控类型")
    private Integer monitorType;

    @ApiModelProperty("监控点")
    private String monitorPoint;

    @ApiModelProperty("监控点名称")
    private String monitorPointName;

    @ApiModelProperty("最小值")
    private BigDecimal minValue;

    @ApiModelProperty("最大值")
    private BigDecimal maxValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("是否启用")
    private Integer isEnabled;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("备注")
    private String remark;
}
