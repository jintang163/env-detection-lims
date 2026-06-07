package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("配制记录查询DTO")
public class QcSamplePrepareQuery extends PageQuery {

    @ApiModelProperty("关键词（配制编号/样品名称）")
    private String keyword;
}
