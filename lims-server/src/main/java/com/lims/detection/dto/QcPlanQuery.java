package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("质控计划查询DTO")
public class QcPlanQuery extends PageQuery {

    @ApiModelProperty("关键词（计划编号/计划名称）")
    private String keyword;

    @ApiModelProperty("周期类型 1每日 2每周 3每月 4每批次")
    private String cycleType;

    @ApiModelProperty("状态 0草稿 1启用 2停用")
    private Integer status;
}
