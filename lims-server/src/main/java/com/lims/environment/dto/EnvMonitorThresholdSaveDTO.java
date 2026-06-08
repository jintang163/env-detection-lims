package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("阈值配置保存DTO")
public class EnvMonitorThresholdSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("监控类型")
    @NotNull(message = "监控类型不能为空")
    private Integer monitorType;

    @ApiModelProperty("监控点")
    @NotBlank(message = "监控点不能为空")
    private String monitorPoint;

    @ApiModelProperty("监控点名称")
    private String monitorPointName;

    @ApiModelProperty("最小值")
    @NotNull(message = "最小值不能为空")
    private BigDecimal minValue;

    @ApiModelProperty("最大值")
    @NotNull(message = "最大值不能为空")
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
