package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("OOS关闭DTO")
public class OosCloseDTO {

    @ApiModelProperty("OOS记录ID")
    @NotNull(message = "OOS记录ID不能为空")
    private Long oosId;

    @ApiModelProperty("关闭意见")
    private String closeOpinion;

    @ApiModelProperty("备注")
    private String remark;
}
