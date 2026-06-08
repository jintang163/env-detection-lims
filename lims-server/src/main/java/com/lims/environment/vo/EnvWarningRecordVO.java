package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("预警记录VO")
public class EnvWarningRecordVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("监控数据ID")
    private Long monitorDataId;

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

    @ApiModelProperty("阈值最小值")
    private BigDecimal thresholdMin;

    @ApiModelProperty("阈值最大值")
    private BigDecimal thresholdMax;

    @ApiModelProperty("预警级别：1一般 2重要 3紧急")
    private Integer warnLevel;

    @ApiModelProperty("预警级别名称")
    private String warnLevelName;

    @ApiModelProperty("预警消息")
    private String warnMessage;

    @ApiModelProperty("预警时间")
    private LocalDateTime warnTime;

    @ApiModelProperty("状态：0待处理 1处理中 2已处理 3已忽略")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("处理人ID")
    private Long handlerId;

    @ApiModelProperty("处理人名称")
    private String handlerName;

    @ApiModelProperty("处理时间")
    private LocalDateTime handleTime;

    @ApiModelProperty("处理结果")
    private String handleResult;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
