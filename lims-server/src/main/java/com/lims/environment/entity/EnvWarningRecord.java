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
@TableName("env_warning_record")
@ApiModel("环境预警记录")
public class EnvWarningRecord extends BaseEntity {

    @ApiModelProperty("监控数据ID")
    private Long monitorDataId;

    @ApiModelProperty("监控点")
    private String monitorPoint;

    @ApiModelProperty("监控点名称")
    private String monitorPointName;

    @ApiModelProperty("监控类型")
    private Integer monitorType;

    @ApiModelProperty("监控值")
    private BigDecimal monitorValue;

    @ApiModelProperty("阈值下限")
    private BigDecimal thresholdMin;

    @ApiModelProperty("阈值上限")
    private BigDecimal thresholdMax;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("预警信息")
    private String warnMessage;

    @ApiModelProperty("预警时间")
    private LocalDateTime warnTime;

    @ApiModelProperty("状态")
    private Integer status;

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
}
