package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("规则统计VO")
public class QcRuleStatsVO {

    @ApiModelProperty("规则总数")
    private Integer total;

    @ApiModelProperty("启用数")
    private Integer enabled;

    @ApiModelProperty("警告规则数")
    private Integer warning;

    @ApiModelProperty("失控规则数")
    private Integer reject;
}
