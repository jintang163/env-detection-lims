package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("OOS审核DTO")
public class OosReviewDTO {

    @ApiModelProperty("OOS记录ID")
    @NotNull(message = "OOS记录ID不能为空")
    private Long oosId;

    @ApiModelProperty("审核意见")
    private String reviewOpinion;

    @ApiModelProperty("审核结果 1通过 2驳回")
    @NotNull(message = "审核结果不能为空")
    private Integer reviewResult;

    @ApiModelProperty("备注")
    private String remark;
}
