package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("违反规则VO")
public class QcViolatedRuleVO {

    @ApiModelProperty("规则编号")
    private String ruleCode;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("类型 1警告 2失控")
    private Integer type;

    @ApiModelProperty("类型名称")
    private String typeName;

    @ApiModelProperty("规则描述")
    private String description;

    @ApiModelProperty("计算公式")
    private String formula;
}
