package com.lims.quotation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("报价单审批参数")
public class QuotationApprovalDTO {

    @ApiModelProperty("报价单ID")
    @NotNull(message = "报价单ID不能为空")
    private Long id;

    @ApiModelProperty("审批结果：1通过 2驳回")
    @NotNull(message = "审批结果不能为空")
    private Integer approvalResult;

    @ApiModelProperty("审批意见")
    private String approvalRemark;
}
