package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("质控记录查询DTO")
public class QcRecordQuery extends PageQuery {

    @ApiModelProperty("关键词（记录编号/样品名称）")
    private String keyword;

    @ApiModelProperty("状态 normal-正常 outlier-异常")
    private String status;

    @ApiModelProperty("质控计划ID")
    private Long planId;
}
