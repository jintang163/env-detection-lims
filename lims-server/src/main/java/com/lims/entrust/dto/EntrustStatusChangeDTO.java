package com.lims.entrust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("状态变更DTO")
public class EntrustStatusChangeDTO {

    @NotNull(message = "委托单ID不能为空")
    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @NotNull(message = "目标状态不能为空")
    @ApiModelProperty("目标状态")
    private Integer targetStatus;

    @ApiModelProperty("操作内容")
    private String operateContent;

    @ApiModelProperty("操作类型")
    private String operateType;
}
