package com.lims.contract.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("con_contract_approval")
@ApiModel("合同审批记录表")
public class ConContractApproval extends BaseEntity {

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("变更ID（变更审批时关联）")
    private Long changeId;

    @ApiModelProperty("审批类型：1合同审批 2变更审批")
    private Integer approvalType;

    @ApiModelProperty("审批节点")
    private String approvalNode;

    @ApiModelProperty("审批人ID")
    private Long approverId;

    @ApiModelProperty("审批人名称")
    private String approverName;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

    @ApiModelProperty("审批结果：1通过 2驳回")
    private Integer approvalResult;

    @ApiModelProperty("审批时间")
    private LocalDate approvalDate;

    @ApiModelProperty("审批顺序")
    private Integer approvalOrder;

    @ApiModelProperty("备注")
    private String remark;
}
