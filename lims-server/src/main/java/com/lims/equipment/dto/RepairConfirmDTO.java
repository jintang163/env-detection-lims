package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("维修确认DTO")
public class RepairConfirmDTO {

    @ApiModelProperty("维修申请ID")
    @NotNull(message = "维修申请ID不能为空")
    private Long id;

    @ApiModelProperty("确认人ID")
    private Long confirmerId;

    @ApiModelProperty("确认人名称")
    private String confirmerName;

    @ApiModelProperty("确认意见")
    @NotBlank(message = "确认意见不能为空")
    private String confirmOpinion;

    @ApiModelProperty("是否通过：true通过 false驳回")
    @NotNull(message = "是否通过不能为空")
    private Boolean passed;
}
