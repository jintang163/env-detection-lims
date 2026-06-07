package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("质控规则保存DTO")
public class QcRuleSaveDTO {

    @ApiModelProperty("规则ID（更新时传）")
    private Long id;

    @ApiModelProperty("规则编号")
    @NotBlank(message = "规则编号不能为空")
    private String ruleCode;

    @ApiModelProperty("规则名称")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @ApiModelProperty("规则类型 1正态分布规则 2非参数规则 3自定义规则")
    @NotNull(message = "规则类型不能为空")
    private Integer ruleType;

    @ApiModelProperty("规则描述")
    private String description;

    @ApiModelProperty("标准差倍数")
    private BigDecimal sdMultiple;

    @ApiModelProperty("连续点数")
    private Integer consecutivePoints;

    @ApiModelProperty("是否批内")
    private Boolean withinRun;

    @ApiModelProperty("是否批间")
    private Boolean acrossRun;

    @ApiModelProperty("计算公式")
    private String formula;

    @ApiModelProperty("是否启用 0否 1是")
    private Integer enabled;

    @ApiModelProperty("备注")
    private String remark;
}
