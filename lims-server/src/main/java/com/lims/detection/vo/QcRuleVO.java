package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("质控规则VO")
public class QcRuleVO {

    @ApiModelProperty("规则ID")
    private Long id;

    @ApiModelProperty("规则编号")
    private String ruleCode;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("规则类型 1西屋规则 2自定义规则")
    private Integer ruleType;

    @ApiModelProperty("规则类型名称")
    private String ruleTypeName;

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

    @ApiModelProperty("是否启用名称")
    private String enabledName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
