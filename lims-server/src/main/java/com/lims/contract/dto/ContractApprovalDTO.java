package com.lims.contract.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("合同审批DTO")
public class ContractApprovalDTO {

    @ApiModelProperty("合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @ApiModelProperty("变更ID（变更审批时传）")
    private Long changeId;

    @ApiModelProperty("审批类型：1合同审批 2变更审批")
    @NotNull(message = "审批类型不能为空")
    private Integer approvalType;

    @ApiModelProperty("审批节点")
    private String approvalNode;

    @ApiModelProperty("审批结果：1通过 2驳回")
    @NotNull(message = "审批结果不能为空")
    private Integer approvalResult;

    @ApiModelProperty("审批意见")
    @NotBlank(message = "审批意见不能为空")
    private String approvalOpinion;

    @ApiModelProperty("审批人ID")
    private Long approverId;

    @ApiModelProperty("审批人名称")
    private String approverName;
}
