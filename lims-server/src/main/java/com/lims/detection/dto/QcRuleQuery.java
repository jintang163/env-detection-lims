package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("质控规则查询DTO")
public class QcRuleQuery extends PageQuery {

    @ApiModelProperty("关键词（规则编号/规则名称）")
    private String keyword;

    @ApiModelProperty("规则类型 1西屋规则 2自定义规则")
    private Integer ruleType;
}
