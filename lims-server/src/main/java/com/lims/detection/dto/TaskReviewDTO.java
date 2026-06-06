package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("任务审核DTO")
public class TaskReviewDTO {

    @ApiModelProperty("任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @ApiModelProperty("审核结果 1通过 2驳回")
    @NotNull(message = "审核结果不能为空")
    private Integer reviewResult;

    @ApiModelProperty("审核意见")
    private String reviewOpinion;
}
