package com.lims.entrust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("调账DTO")
public class AdjustDTO {

    @NotNull(message = "委托单ID不能为空")
    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @NotNull(message = "调账金额不能为空")
    @ApiModelProperty("调账金额")
    private BigDecimal adjustAmount;

    @NotBlank(message = "调账原因不能为空")
    @ApiModelProperty("调账原因")
    private String adjustReason;
}
