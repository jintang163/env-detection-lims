package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("计划统计VO")
public class QcPlanStatsVO {

    @ApiModelProperty("计划总数")
    private Integer total;

    @ApiModelProperty("进行中数")
    private Integer active;

    @ApiModelProperty("今日需执行数")
    private Integer today;

    @ApiModelProperty("遗漏数")
    private Integer missed;
}
