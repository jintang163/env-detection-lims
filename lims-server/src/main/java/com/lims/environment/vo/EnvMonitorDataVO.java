package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("监控数据VO")
public class EnvMonitorDataVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("监控点")
    private String monitorPoint;

    @ApiModelProperty("监控点名称")
    private String monitorPointName;

    @ApiModelProperty("监控类型：1温度 2湿度 3压差 4噪音")
    private Integer monitorType;

    @ApiModelProperty("监控类型名称")
    private String monitorTypeName;

    @ApiModelProperty("监控值")
    private BigDecimal monitorValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("采集时间")
    private LocalDateTime collectTime;

    @ApiModelProperty("采集方式：1自动采集 2人工录入")
    private Integer collectMethod;

    @ApiModelProperty("采集方式名称")
    private String collectMethodName;

    @ApiModelProperty("是否预警：0否 1是")
    private Integer isWarning;

    @ApiModelProperty("预警级别：1一般 2重要 3紧急")
    private Integer warnLevel;

    @ApiModelProperty("预警级别名称")
    private String warnLevelName;

    @ApiModelProperty("预警消息")
    private String warnMessage;

    @ApiModelProperty("采集人ID")
    private Long collectorId;

    @ApiModelProperty("采集人名称")
    private String collectorName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
