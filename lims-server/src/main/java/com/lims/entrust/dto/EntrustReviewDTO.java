package com.lims.entrust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("委托单评审DTO")
public class EntrustReviewDTO {

    @NotNull(message = "委托单ID不能为空")
    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("评审节点")
    private String reviewNode;

    @NotNull(message = "评审结果不能为空")
    @ApiModelProperty("评审结果 1通过 2驳回")
    private Integer reviewResult;

    @ApiModelProperty("评审意见")
    private String reviewOpinion;

    @ApiModelProperty("备注")
    private String remark;
}
