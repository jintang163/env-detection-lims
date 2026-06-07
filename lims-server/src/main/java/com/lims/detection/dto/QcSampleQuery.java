package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("质控样品查询DTO")
public class QcSampleQuery extends PageQuery {

    @ApiModelProperty("关键词（样品编号/样品名称）")
    private String keyword;

    @ApiModelProperty("样品类型 1标准物质 2质控样 3加标样")
    private String sampleType;

    @ApiModelProperty("过期状态 0未过期 1即将过期 2已过期")
    private Integer expireStatus;
}
