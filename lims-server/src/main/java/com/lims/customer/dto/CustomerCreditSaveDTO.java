package com.lims.customer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("客户信用调整参数")
public class CustomerCreditSaveDTO {

    @ApiModelProperty("主键ID")
    private Long id;

    @NotNull(message = "客户ID不能为空")
    @ApiModelProperty(value = "客户ID", required = true)
    private Long customerId;

    @NotNull(message = "变更类型不能为空")
    @ApiModelProperty(value = "变更类型 1增加 2扣减", required = true)
    private Integer changeType;

    @NotNull(message = "变更分数不能为空")
    @ApiModelProperty(value = "变更分数", required = true)
    private Integer changeScore;

    @NotBlank(message = "变更原因不能为空")
    @ApiModelProperty(value = "变更原因", required = true)
    private String changeReason;

    @ApiModelProperty("变更说明")
    private String changeDescription;

    @ApiModelProperty("关联业务类型 1订单 2合同 3投诉 4其他")
    private Integer businessType;

    @ApiModelProperty("关联业务ID")
    private Long businessId;

    @ApiModelProperty("关联业务编号")
    private String businessNo;

    @ApiModelProperty("涉及金额")
    private BigDecimal involvedAmount;

    @ApiModelProperty("备注")
    private String remark;
}
