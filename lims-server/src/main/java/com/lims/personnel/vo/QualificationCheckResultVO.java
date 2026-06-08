package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("资质校验结果VO")
public class QualificationCheckResultVO {

    @ApiModelProperty("是否合格")
    private Boolean qualified;

    @ApiModelProperty("不合格项列表")
    private List<String> unqualifiedItems;

    @ApiModelProperty("合格项列表")
    private List<String> qualifiedItems;
}
