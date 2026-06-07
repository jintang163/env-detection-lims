package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("样品统计VO")
public class QcSampleStatsVO {

    @ApiModelProperty("样品总数")
    private Integer total;

    @ApiModelProperty("有效数")
    private Integer valid;

    @ApiModelProperty("即将过期数")
    private Integer warning;

    @ApiModelProperty("已过期数")
    private Integer expired;
}
