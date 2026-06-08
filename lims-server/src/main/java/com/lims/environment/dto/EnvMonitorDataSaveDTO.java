package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("监控数据保存DTO")
public class EnvMonitorDataSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("监控点")
    @NotBlank(message = "监控点不能为空")
    private String monitorPoint;

    @ApiModelProperty("监控点名称")
    private String monitorPointName;

    @ApiModelProperty("监控类型")
    @NotNull(message = "监控类型不能为空")
    private Integer monitorType;

    @ApiModelProperty("监控值")
    @NotNull(message = "监控值不能为空")
    private BigDecimal monitorValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("采集时间")
    @NotNull(message = "采集时间不能为空")
    private LocalDateTime collectTime;

    @ApiModelProperty("采集方式")
    private Integer collectMethod;

    @ApiModelProperty("是否预警")
    private Integer isWarning;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("预警消息")
    private String warnMessage;

    @ApiModelProperty("采集人ID")
    private Long collectorId;

    @ApiModelProperty("采集人名称")
    private String collectorName;

    @ApiModelProperty("备注")
    private String remark;
}
