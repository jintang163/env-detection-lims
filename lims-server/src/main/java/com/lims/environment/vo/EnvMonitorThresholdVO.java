package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("阈值配置VO")
public class EnvMonitorThresholdVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("监控类型：1温度 2湿度 3压差 4噪音")
    private Integer monitorType;

    @ApiModelProperty("监控类型名称")
    private String monitorTypeName;

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

    @ApiModelProperty("是否启用：0否 1是")
    private Integer isEnabled;

    @ApiModelProperty("预警级别：1一般 2重要 3紧急")
    private Integer warnLevel;

    @ApiModelProperty("预警级别名称")
    private String warnLevelName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
